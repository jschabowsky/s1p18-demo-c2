/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.lcbo.data.mapper.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the pricelist parser processor.
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("lcbo.data.mapper")
public class LcboDataMapperProcessorProperties {
	public static final String DEFAULT_PUBLISH_TOPIC_PREFIX = "product/";

	private String publishTopicPrefix = DEFAULT_PUBLISH_TOPIC_PREFIX;

	public String getPublishTopicPrefix() {
		return publishTopicPrefix;
	}

	public void setPublishTopicPrefix(String publishTopicPrefix) {
		this.publishTopicPrefix = publishTopicPrefix;
	}

}
