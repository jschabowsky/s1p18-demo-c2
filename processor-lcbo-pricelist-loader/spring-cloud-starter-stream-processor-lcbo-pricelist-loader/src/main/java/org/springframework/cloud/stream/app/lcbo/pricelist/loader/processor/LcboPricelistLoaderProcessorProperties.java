/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.lcbo.pricelist.loader.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for LcboPricelistLoaderProcessorConfiguration
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("lcbo.pricelist.loader")
public class LcboPricelistLoaderProcessorProperties {
	public static final String DEFAULT_PROCESSOR_URL = "http://lcboapi.com/products?where_not=is_dead,is_discontinued&q=whisky";
	
	/**
	 * LCBO REST API URL with query string parameters (by default, searching for whiskies)
	 */
	private String processorUrl = DEFAULT_PROCESSOR_URL;

	public String getProcessorUrl() {
		return processorUrl;
	}

	public void setProcessorUrl(String processorUrl) {
		this.processorUrl = processorUrl;
	}
}
