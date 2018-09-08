/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */

package org.springframework.cloud.stream.app.fx.rate.lookup.processor;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.solace.demo.utahdabc.datamodel.Product;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;

/**
 * Integration Tests for the geocoder Processor.
 *
 * @author Solace Corp.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@SpringBootTest
public abstract class FxRateLookupProcessorIntegrationTests {
	@Autowired
	protected Processor channels;

	@Autowired
	protected MessageCollector collector;
	
	@SpringBootTest("fx.rate.lookup.fxLookupUrl=https://openexchangerates.org/api/latest.json?app_id=<INSERT_APP_ID_HERE>")
	public static class UsingPropsIntegrationTests extends FxRateLookupProcessorIntegrationTests {
		// Adjust this as FX values fluctuate
		private static final String RESULT_SUBSTRING = "{\"name\":\"Clown Royale With Cheese\",\"div_code\":null,\"dept_code\":null,\"class_code\":null,\"size\":666,\"csc\":0,\"price\":7";
		
		@Test
		public void test() {
			Product p = new Product();
			p.setName("Clown Royale With Cheese");
			p.setLcboPrice(100);
			p.setSize(666);
			channels.input().send(new GenericMessage<Product>(p));
			assertThat(collector.forChannel(channels.output()), receivesPayloadThat(containsString(RESULT_SUBSTRING)));
		}
	}

	@SpringBootApplication
	public static class FxRateLookupProcessorApplication {
		public static void main(String[] args) {
			SpringApplication.run(FxRateLookupProcessorApplication.class, args);
		}
	}
}
