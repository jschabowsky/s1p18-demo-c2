/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.lcbo.pricelist.loader.processor;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.app.lcbo.pricelist.loader.domain.LcboProduct;
import org.springframework.cloud.stream.app.lcbo.pricelist.loader.domain.LcboProductsResponse;
import org.springframework.cloud.stream.app.lcbo.pricelist.loader.domain.Pager;

import org.springframework.cloud.stream.messaging.Processor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * See README.adoc
 *
 * @author Solace Corp
 */
@Configuration
@EnableBinding(Processor.class)
@EnableConfigurationProperties(LcboPricelistLoaderProcessorProperties.class)
public class LcboPricelistLoaderProcessorConfiguration {
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public LcboPricelistLoaderProcessor lcboPricelistLoader() {
		return new LcboPricelistLoaderProcessor();
	}

	@MessageEndpoint
	public static class LcboPricelistLoaderProcessor {

		private static final Log LOG = LogFactory.getLog(LcboPricelistLoaderProcessor.class);

		@Autowired
		private LcboPricelistLoaderProcessorProperties properties;

		@Autowired
		private RestTemplate restTemplate;

	    @Autowired
	    private Processor processor;
	    
	    @ServiceActivator(inputChannel = Processor.INPUT)
		public void process(Message<?> message) {
			try {
				String url = properties.getProcessorUrl();
				
				UriComponents uriComponents = UriComponentsBuilder.fromUriString(url).build();
				String path = uriComponents.getPath();
				int pathIndex = url.indexOf(path);
				String baseUri = url.substring(0, pathIndex);
				String uriSuffix = url.substring(pathIndex);

				publishProducts(baseUri, uriSuffix, processor.output());

			}
			catch (Exception e) {
				LOG.warn("Error in HTTP request", e);
			}
		}
		
		private void publishProducts(String baseUri, String path, MessageChannel output) {
			LcboProductsResponse response = restTemplate.getForObject(baseUri + path, LcboProductsResponse.class);
			HttpStatus status = HttpStatus.valueOf(Integer.parseInt(response.getStatus()));
			if (status.isError()) {
				if (status.is4xxClientError()) {
					throw new HttpClientErrorException(status);
				} else {
					throw new HttpServerErrorException(status);
				}
			}

			List<LcboProduct> products = response.getResult();
			for(LcboProduct p : products) {
				output.send(message(p));
				LOG.info(p);
			}
			
			Pager pager = response.getPager();
			if (!pager.isIs_final_page()) {
				String nextPath = pager.getNext_page_path();
				publishProducts(baseUri, nextPath, output);
			}
		}
	    
	    private static final <T> Message<T> message(T val) {
	        return MessageBuilder.withPayload(val).build();
	    }
	}
}
