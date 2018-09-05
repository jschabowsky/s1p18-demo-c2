/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.lcbo.data.mapper.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import reactor.core.publisher.Flux;

import com.solace.demo.utahdabc.datamodel.Product;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SCS processor - pricelist parser - splits an HTML pricelist into individual products and outputs as JSON or a user-defined content type.
 *
 * @author Solace Corp
 */
@Configuration
@EnableBinding(Processor.class)
@EnableConfigurationProperties(LcboDataMapperProcessorProperties.class)
public class LcboDataMapperProcessorConfiguration {
	@Autowired
	private LcboDataMapperProcessorProperties properties;

	@Bean
	public LcboDataMapperProcessor lcboPricelistLoader() {
		return new LcboDataMapperProcessor();
	}

	@Autowired
    private BinderAwareChannelResolver resolver;

	@MessageEndpoint
	public static class LcboDataMapperProcessor {

		private static final Log LOG = LogFactory.getLog(LcboDataMapperProcessor.class);

		@Autowired
		private LcboDataMapperProcessorProperties properties;

	    @Autowired
	    private Processor processor;

		@ServiceActivator(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)	    
		public void process(LcboProduct product) {
			try {

			}
			catch (Exception e) {
				LOG.warn("Error in HTTP request", e);
			}
		}
	}
    
}
 