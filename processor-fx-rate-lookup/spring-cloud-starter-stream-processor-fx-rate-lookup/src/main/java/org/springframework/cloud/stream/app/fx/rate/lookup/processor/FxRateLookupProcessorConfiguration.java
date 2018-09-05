/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.fx.rate.lookup.processor;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import org.springframework.cloud.stream.messaging.Processor;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisOperations;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import reactor.core.publisher.Flux;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import com.solace.demo.utahdabc.datamodel.ProductInventoryData;
import com.solace.demo.utahdabc.datamodel.StoreInventory;

/**
 * SCS processor - geocoder.  Looks up an address (could be cached) associated with a product in a store, and sets lat/long
 *
 * @author Solace Corp
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(FxRateLookupProcessorProperties.class)
public class FxRateLookupProcessorConfiguration {
	@Autowired
	private FxRateLookupProcessorProperties properties;

    private static final Logger log = LoggerFactory.getLogger(FxRateLookupProcessorConfiguration.class);
  
    // Google Maps Geocoder API Context
    private GeoApiContext geoContext;
    
	@Autowired
	private RedisOperations<String, String> redisOps;
    
	@StreamListener
	@Output(Processor.OUTPUT)
    public Flux<ProductInventoryData> resolveAddressToLatLng(@Input(Processor.INPUT) Flux<ProductInventoryData> input) {
		return input.map(pid -> resolveLatLong(pid));
    }
	
	private ProductInventoryData resolveLatLong(ProductInventoryData pid) {
		StoreInventory storeInventory = pid.getStoreInventory();
		String storeId = storeInventory.getStoreID();
		
		if (storeInventory == null || storeId == null) {
			log.error("Invalid store for product CSC " + pid.getProduct().getCsc());
			return null;
		}

		List<Point> positions = redisOps.opsForGeo().position(properties.getLookupState(), storeId);
		if (positions.isEmpty() || positions.get(0) == null) {
			String partialAddress = storeInventory.getStoreAddress();
			String city = storeInventory.getStoreCity();

			StringJoiner sj = new StringJoiner(",");
			String address = sj.add(partialAddress).add(city).add(properties.getLookupState()).toString();
			
			GeocodingResult[] results = null;
			try {
				if (geoContext == null) {
					geoContext = new GeoApiContext.Builder().apiKey(properties.getGoogleMapsApiKey()).build();
				}
				
				results = GeocodingApi.geocode(geoContext, address).await();
			} catch (ApiException e) {
				log.error(e.toString());
				e.printStackTrace();
			} catch (InterruptedException e) {
				log.error(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				log.error(e.toString());
			}

			if (results != null) {
				storeInventory.setStoreGeoLat(results[0].geometry.location.lat);
				storeInventory.setStoreGeoLng(results[0].geometry.location.lng);
				
				log.info(address + " Lat/Lng: " + storeInventory.getStoreGeoLat() + " / " + storeInventory.getStoreGeoLng());			
			}
			
			redisOps.opsForGeo().add(properties.getLookupState(), 
					new RedisGeoCommands.GeoLocation<String>(storeId, 
							new Point(storeInventory.getStoreGeoLng(), storeInventory.getStoreGeoLat())));

		} else {
			Point pt = positions.get(0);
			storeInventory.setStoreGeoLng(pt.getX());
			storeInventory.setStoreGeoLat(pt.getY());
		}
		
		return pid;
	}

}
