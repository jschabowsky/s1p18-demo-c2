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
	public static final String DEFAULT_CATEGORY_DELIMITER = ":";
	
	private boolean categoryInfoPublished;
	private String categoryDelimiter = DEFAULT_CATEGORY_DELIMITER;
	
	public boolean isCategoryInfoPublished() {
		return categoryInfoPublished;
	}
	public void setCategoryInfoPublished(boolean categoryInfoPublished) {
		this.categoryInfoPublished = categoryInfoPublished;
	}
	public String getCategoryDelimiter() {
		return categoryDelimiter;
	}
	public void setCategoryDelimiter(String categoryDelimiter) {
		this.categoryDelimiter = categoryDelimiter;
	}
}
