/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.lcbo.matcher.processor;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.handler.annotation.SendTo;

import com.solace.demo.utahdabc.datamodel.Product;

/**
 * SCS processor - lookup.  Converts an address to lat/long, or the reverse.
 *
 * @author Solace Corp
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(UtahLcboMatcherProcessorProperties.class)
public class UtahLcboMatcherProcessorConfiguration {
	private static final Log LOG = LogFactory.getLog(UtahLcboMatcherProcessorConfiguration.class);	

	@Autowired
	private UtahLcboMatcherProcessorProperties properties;
	
	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory();
	}
	
	@Bean
	public RedisOperations<String, Double> redisTemplate() {
		final RedisTemplate<String, Double> template =  new RedisTemplate<String, Double>();
		template.setConnectionFactory(redisConnectionFactory());
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
		Integer size = new Integer(p.getSize());
		Set<Object> lcboWhiskeyNames = redisOps.opsForHash().keys(size.toString());

		if (lcboWhiskeyNames == null) {
			LOG.info("No matching LCBO product found for: " + p.getName());
			return p;
		}
		
		StringTokenizer utahTokens = new StringTokenizer(p.getName().toUpperCase());
		Set<String> utahTokenSet = new HashSet<String>();
		while(utahTokens.hasMoreTokens()) {
			utahTokenSet.add(utahTokens.nextToken());
		}
		
		double maxHitRatio = 0;
		String bestMatchWhiskey = null;
		double minHitThreshold = (double)properties.getMinTokenMatchPercentage() / 100;
		
		for(Object lcboWhiskeyName : lcboWhiskeyNames) {
			StringTokenizer lcboTokens = new StringTokenizer((String)lcboWhiskeyName);

			Set<String> lcboTokenSet = new HashSet<String>();
			while(lcboTokens.hasMoreTokens()) {
				lcboTokenSet.add(lcboTokens.nextToken());
			}
			
			int initLcboTokenCount = lcboTokenSet.size();
			lcboTokenSet.retainAll(utahTokenSet);
			
			double hitRatio = (double)lcboTokenSet.size() / initLcboTokenCount;
			
			if (hitRatio >= minHitThreshold) {
				if (hitRatio > maxHitRatio) {
					bestMatchWhiskey = (String)lcboWhiskeyName;
					maxHitRatio = hitRatio;
				}
			}
		}
		
		if (bestMatchWhiskey != null) {
			Double lcboPrice = (Double)redisOps.opsForHash().get(size.toString(), bestMatchWhiskey);
			
			p.setLcboPrice(lcboPrice);
			p.setSPA(bestMatchWhiskey);
		}
		
		return p;
    }
}
