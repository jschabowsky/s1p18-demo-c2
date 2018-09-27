/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.fx.rate.lookup.processor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.cloud.stream.annotation.EnableBinding;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.web.client.RestTemplate;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.solace.demo.utahdabc.datamodel.Product;


/**
 * See README.adoc
 *
 * @author Solace Corp
 */

@EnableBinding(Processor.class)
@EnableConfigurationProperties(FxRateLookupProcessorProperties.class)
public class FxRateLookupProcessorConfiguration {
	private static final Log LOG = LogFactory.getLog(FxRateLookupProcessorConfiguration.class);	
	
	@Autowired
	private FxRateLookupProcessorProperties properties;

	@Autowired
	private RestTemplate restTemplate;
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
		
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
		LOG.info("Processing product: " + p.getName());
		p.setPrice(p.getLcboPrice() / getFxRate(properties.getBaseLookupCurrency(), 
					properties.getTargetLookupCurrency(),
					properties.getCacheTtlSec()));
		
		// Cache the LCBO product for later price comparison, grouping by volume, using name + volume as key to match Utah
		String key = Integer.valueOf(p.getSize()).toString();
		String hashKey = p.getName() + " " + p.getSize();
		redisOps.opsForHash().put(key, hashKey, p.getPrice());
		
		return p;
    }
	
	private double getFxRate(String baseCurrency, String targetCurrency, long cacheTtlSec) {
		Double fxRate = (Double)redisOps.opsForHash().get(baseCurrency, targetCurrency);
		if (fxRate != null) return fxRate;
		
		FxLookupResponse response = restTemplate.getForObject(properties.getFxLookupUrl(), FxLookupResponse.class);

		if (!response.getBase().equals(baseCurrency)) {
			throw new IllegalArgumentException("Unexpected base currency: " + response.getBase());
		}
		
		fxRate = response.getRates().get(targetCurrency); 
		redisOps.opsForHash().put(baseCurrency, targetCurrency, fxRate);
		redisOps.expire(baseCurrency, cacheTtlSec, TimeUnit.SECONDS);
		
		LOG.info(baseCurrency + "/" + targetCurrency + ": " + fxRate);
 		
		return fxRate;
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class FxLookupResponse {
		private String base;
		private Map<String, Double> rates;
		
		public String getBase() {
			return base;
		}
		public Map<String, Double> getRates() {
			return rates;
		}
	}
}
