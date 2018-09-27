/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.lcbo.data.mapper.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for LcboDataMapperProcessorConfiguration
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("lcbo.data.mapper")
public class LcboDataMapperProcessorProperties {
	public static final String DEFAULT_CATEGORY_DELIMITER = ":";
	
	/**
	 * Flag to indicate whether LCBO category info (e.g. Spirits/Whiskey) is published
	 */
	private boolean categoryInfoPublished;
	
	/**
	 * Delimiter to use between primary, secondary and tertiary categories
	 */
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
