/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.fx.rate.lookup.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the inventory geocoder processor.
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("fx.rate.lookup")
public class FxRateLookupProcessorProperties {
	public static final String DEFAULT_LOOKUP_STATE = "UT";
		
	private String googleMapsApiKey;
	
	private String lookupState = DEFAULT_LOOKUP_STATE;

	public String getGoogleMapsApiKey() {
		return googleMapsApiKey;
	}

	public void setGoogleMapsApiKey(String googleMapsApiKey) {
		this.googleMapsApiKey = googleMapsApiKey;
	}

	public String getLookupState() {
		return lookupState;
	}

	public void setLookupState(String lookupState) {
		this.lookupState = lookupState;
	}
	
	
}
