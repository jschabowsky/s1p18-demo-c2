/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.lcbo.matcher.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the inventory lookup processor.
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("utah.lcbo.matcher")
public class UtahLcboMatcherProcessorProperties {	
	public static final String DEFAULT_INVENTORY_QUERY_URL = "https://webapps2.abc.utah.gov/Production/OnlineInventoryQuery/IQ/InventoryQuery.aspx";
	public static final String DEFAULT_PUBLISH_TOPIC_PREFIX = "inventory/";
	
	private String inventoryQueryUrl = DEFAULT_INVENTORY_QUERY_URL;
	private String publishTopicPrefix = DEFAULT_PUBLISH_TOPIC_PREFIX;

	public String getInventoryQueryUrl() {
		return inventoryQueryUrl;
	}

	public void setInventoryQueryUrl(String inventoryQueryUrl) {
		this.inventoryQueryUrl = inventoryQueryUrl;
	}

	public String getPublishTopicPrefix() {
		return publishTopicPrefix;
	}

	public void setPublishTopicPrefix(String publishTopicPrefix) {
		this.publishTopicPrefix = publishTopicPrefix;
	}

}
