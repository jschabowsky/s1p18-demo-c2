/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.lcbo.matcher.processor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.handler.annotation.SendTo;

import com.solace.demo.utahdabc.datamodel.Product;

/**
 * See README.adoc
 *
 * @author Solace Corp
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(UtahLcboMatcherProcessorProperties.class)
public class UtahLcboMatcherProcessorConfiguration {
	private static final Log LOG = LogFactory.getLog(UtahLcboMatcherProcessorConfiguration.class);
	private static final String NON_ALPHA_MATCH = "[^a-zA-Z0-9 ]";
	private static final String WHITESPACE_MATCH = "\\s+";
	private static final String EMPTY_STRING = "";

	@Autowired
	private UtahLcboMatcherProcessorProperties properties;

	@Bean
	public RedisOperations<String, Double> redisTemplate(RedisConnectionFactory rcf) {
		final RedisTemplate<String, Double> template =  new RedisTemplate<String, Double>();
		template.setConnectionFactory(rcf);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericToStringSerializer<Double>(Double.class));
		template.setHashKeySerializer(new StringRedisSerializer());

		return template;
	}	
	
	@Autowired
	private RedisOperations<String, Double> redisOps;

	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
    public Product process(Product p) {
		// Lookup all cached LCBO products matching this Utah product's volume
		Integer size = new Integer(p.getSize());
		Set<Object> lcboNames = redisOps.opsForHash().keys(size.toString());

		if (lcboNames == null) {
			LOG.info("No matching LCBO product found for: " + p.getName());
			return p;
		}
		
		String utahName = p.getName();
		// Drop the last two chars in the name as it's usually the volume unit (ml)
		String[] utahWords = utahName.substring(0, utahName.length() - 2)
				.toUpperCase()
				.replaceAll(NON_ALPHA_MATCH, EMPTY_STRING)
				.split(WHITESPACE_MATCH);
		
		// Create a set of words making up the Utah name to comparison for similarity to the LCBO name
		Set<String> utahWordSet = new LinkedHashSet<String>();
		for(String utahWord : utahWords) {
			utahWordSet.add(utahWord);
		}
		
		double maxHitRatio = 0;
		String bestMatchWhiskey = null;
		double minHitThreshold = (double)properties.getMinTokenMatchPercentage() / 100;
		
		for(Object lcboNameObj : lcboNames) {
			String lcboName = (String)lcboNameObj;
			String[] lcboWords = lcboName.toUpperCase()
					.replaceAll(NON_ALPHA_MATCH, EMPTY_STRING)
					.split(WHITESPACE_MATCH);
			// Ensure at least the first and last words are identical (name and volume)
			if(!lcboWords[0].equals(utahWords[0]) || !lcboWords[lcboWords.length - 1].equals(utahWords[utahWords.length - 1]))
				continue;
			
			Set<String> lcboWordSet = new LinkedHashSet<String>();
			for(String lcboWord : lcboWords) {
				lcboWordSet.add(lcboWord);
			}
			
			int lcboWordCount = lcboWords.length;
			lcboWordSet.retainAll(utahWordSet);
			
			double hitRatio = (double)lcboWordSet.size() / lcboWordCount;			
			if (hitRatio >= minHitThreshold && hitRatio > maxHitRatio) {
				bestMatchWhiskey = lcboName;
				maxHitRatio = hitRatio;
			}
		}
		
		if (bestMatchWhiskey != null) {
			Double lcboPrice = (Double)redisOps.opsForHash().get(size.toString(), bestMatchWhiskey);
			
			if (Math.abs(p.getPrice() - lcboPrice) <= properties.getMaxAllowablePriceDelta()) { 
				p.setLcboPrice(lcboPrice);
				p.setSPA(bestMatchWhiskey);
				
				LOG.info("Matched Utah: " + p.getName() + "[$" + p.getPrice() + "] to LCBO: " 
						+ bestMatchWhiskey + "[$" + lcboPrice + "]");
			} else {
				String err = "Price delta beyond threshold $" + properties.getMaxAllowablePriceDelta() + 
						" for match: " + p.getName() + " / " + bestMatchWhiskey;
				LOG.error(err);
				throw new RuntimeException(err);
			}
		} else {
			String err = "No matching LCBO whiskey with name: " + p.getName();
			LOG.error(err);
			throw new RuntimeException(err);
		}
		
		return p;
    }
}
