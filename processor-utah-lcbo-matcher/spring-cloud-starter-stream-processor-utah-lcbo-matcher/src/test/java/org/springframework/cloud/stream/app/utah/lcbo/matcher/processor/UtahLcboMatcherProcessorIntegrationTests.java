/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */

package org.springframework.cloud.stream.app.utah.lcbo.matcher.processor;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.Output;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.solace.demo.utahdabc.datamodel.Product;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;

/**
 * Integration Tests for the lookup Processor.
 *
 * @author Solace Corp.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@SpringBootTest
public abstract class UtahLcboMatcherProcessorIntegrationTests {

	@Autowired
	protected Processor channels;

	@Autowired
	protected MessageCollector collector;
		
	private static final String TEST_RESULT = "{\"storeID\":\"0039\",\"storeName\":\"Store 39 - St George Metro\",\"productQty\":2,\"storeAddress\":\"161 North 900 East\",\"storeGeoLat\":0.0,\"storeGeoLng\":0.0,\"storeCity\":\"Saint George\",\"storePhone\":\"435-674-9550\"}";

	/**
	 * Validates that the module loads with default properties.
	 */
	public static class UsingNothingIntegrationTests extends UtahLcboMatcherProcessorIntegrationTests {
		
		public static void doGenericProcessorTest(Processor channels, MessageCollector collector) {
			Product p = new Product();
			p.setClass_code("AWS");
			p.setCsc(4006);
			channels.input().send(new GenericMessage<Product>(p));
			assertThat(collector.forChannel(channels.output()), receivesPayloadThat(is(TEST_RESULT)));
		}

		
		@Test
	    @Output(Processor.OUTPUT)
		public void test() {
			doGenericProcessorTest(channels, collector);
		}
	}

	@SpringBootTest("utah.lcbo.matcher.publishTopicPrefix=inventory/")
	public static class UsingPropsIntegrationTests extends UtahLcboMatcherProcessorIntegrationTests {
		@Test
		public void test() {
			UsingNothingIntegrationTests.doGenericProcessorTest(channels, collector);
		}
	}

	@SpringBootApplication
	public static class UtahLcboMatcherProcessorApplication {
		public static void main(String[] args) {
			SpringApplication.run(UtahLcboMatcherProcessorApplication.class, args);
		}
	}

}
