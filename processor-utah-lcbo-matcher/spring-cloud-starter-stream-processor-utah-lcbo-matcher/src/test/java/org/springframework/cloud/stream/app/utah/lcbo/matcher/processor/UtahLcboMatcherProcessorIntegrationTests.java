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
		
	/**
	 * Validates that the module loads with default properties.
	 */
	public static class UsingNothingIntegrationTests extends UtahLcboMatcherProcessorIntegrationTests {
		private static final String TEST_RESULT = "{\"name\":\"JACK DANIELS TENNESSEE WHISKEY 750ml\",\"div_code\":null,\"dept_code\":null,\"class_code\":null,\"size\":750,\"csc\":0,\"price\":33.9,\"lcboPrice\":28.061493782803282,\"status";
		
		public static void doGenericProcessorTest(Processor channels, MessageCollector collector, Product p, String expectedResult) {
			channels.input().send(new GenericMessage<Product>(p));
			assertThat(collector.forChannel(channels.output()), receivesPayloadThat(containsString(expectedResult)));
		}
		
		@Test
		public void testMatch() {
			Product p = new Product();
			p.setName("JACK DANIELS TENNESSEE WHISKEY 750ml");
			p.setSize(750);
			p.setPrice(33.9);

			doGenericProcessorTest(channels, collector, p, TEST_RESULT);
		}
		
		@Test(expected = RuntimeException.class)
		public void testPriceThresholdBreach() {
			Product p = new Product();
			p.setName("JAMESON GOLD IRISH WHISKEY 750ml");
			p.setSize(750);
			p.setPrice(84.9);

			channels.input().send(new GenericMessage<Product>(p));
		}
		
		@Test(expected = RuntimeException.class)
		public void testNoMatch() {
			Product p = new Product();
			p.setName("JACK ASTOR'S BEER");
			p.setSize(750);
			p.setPrice(21.99);

			channels.input().send(new GenericMessage<Product>(p));			
		}

	}

	@SpringBootTest("utah.lcbo.matcher.minTokenMatchPercentage=75")
	public static class UsingPropsIntegrationTests extends UtahLcboMatcherProcessorIntegrationTests {
		private static final String TEST_RESULT = "{\"name\":\"JACK DANIELS TENNESSEE WHISKEY 750ml\",\"div_code\":null,\"dept_code\":null,\"class_code\":null,\"size\":750,\"csc\":0,\"price\":33.9,\"lcboPrice\":28.061493782803282,\"status";
		
		@Test
		public void test() {
			Product p = new Product();
			p.setName("JACK DANIELS TENNESSEE WHISKEY 750ml");
			p.setSize(750);
			p.setPrice(33.9);
			
			UsingNothingIntegrationTests.doGenericProcessorTest(channels, collector, p, TEST_RESULT);
		}
	}

	@SpringBootApplication
	public static class UtahLcboMatcherProcessorApplication {
		public static void main(String[] args) {
			SpringApplication.run(UtahLcboMatcherProcessorApplication.class, args);
		}
	}

}
