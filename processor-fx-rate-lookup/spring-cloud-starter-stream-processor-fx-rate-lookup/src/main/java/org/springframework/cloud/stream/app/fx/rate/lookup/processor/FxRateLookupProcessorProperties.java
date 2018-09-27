/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.fx.rate.lookup.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for FxRateLookupProcessorConfiguration
 *
 * @author Solace Corp.
 */
@ConfigurationProperties("fx.rate.lookup")
public class FxRateLookupProcessorProperties {	
	public static final String DEFAULT_BASE_LOOKUP_CURRENCY = "USD";
	public static final String DEFAULT_TARGET_LOOKUP_CURRENCY = "CAD";
	public static final long DEFAULT_CACHE_TTL_SEC = 86400;
	
	/**
	 * Currency to which other amounts are converted to
	 */
	private String baseLookupCurrency = DEFAULT_BASE_LOOKUP_CURRENCY;
	
	/**
	 * Currency from which the conversion will occur
	 */
	private String targetLookupCurrency = DEFAULT_TARGET_LOOKUP_CURRENCY;

	/**
	 * Expiration time for cached currency values
	 */
	private long cacheTtlSec = DEFAULT_CACHE_TTL_SEC;
	
	/**
	 * FX Rates Conversion Service URL - must include valid API key
	 */
	private String fxLookupUrl;
	
	public String getFxLookupUrl() {
		return fxLookupUrl;
	}

	public void setFxLookupUrl(String fxLookupUrl) {
		this.fxLookupUrl = fxLookupUrl;
	}

	public String getBaseLookupCurrency() {
		return baseLookupCurrency;
	}

	public String getTargetLookupCurrency() {
		return targetLookupCurrency;
	}
	
	public void setTargetLookupCurrency(String targetLookupCurrency) {
		this.targetLookupCurrency = targetLookupCurrency;
	}

	public long getCacheTtlSec() {
		return cacheTtlSec;
	}

	public void setCacheTtlSec(long cacheTtlSec) {
		this.cacheTtlSec = cacheTtlSec;
	}

}
