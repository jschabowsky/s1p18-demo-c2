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
	public static final int DEFAULT_MIN_TOKEN_MATCH_PERCENTAGE = 50;
	
	private int minTokenMatchPercentage = DEFAULT_MIN_TOKEN_MATCH_PERCENTAGE;

	public int getMinTokenMatchPercentage() {
		return minTokenMatchPercentage;
	}

	public void setMinTokenMatchPercentage(int minTokenMatchPercentage) {
		this.minTokenMatchPercentage = minTokenMatchPercentage;
	}
}
