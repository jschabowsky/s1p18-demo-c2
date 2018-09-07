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
		private static final String TEST_RESULT = "{\"name\":\"JACK DANIELS TENNESSEE HONEY 1750ml\",\"div_code\":null,\"dept_code\":null,\"class_code\":null,\"size\":1750,\"csc\":0,\"price\":0.0,\"lcboPrice\":57";
		
		public static void doGenericProcessorTest(Processor channels, MessageCollector collector, String expectedResult) {
			Product p = new Product();
			p.setName("JACK DANIELS TENNESSEE HONEY 1750ml");
			p.setSize(1750);
			
			channels.input().send(new GenericMessage<Product>(p));
			assertThat(collector.forChannel(channels.output()), receivesPayloadThat(containsString(expectedResult)));
		}

		
		@Test
	    @Output(Processor.OUTPUT)
		public void test() {
			doGenericProcessorTest(channels, collector, TEST_RESULT);
		}
	}

	@SpringBootTest("utah.lcbo.matcher.minTokenMatchPercentage=90")
	public static class UsingPropsIntegrationTests extends UtahLcboMatcherProcessorIntegrationTests {
		private static final String TEST_RESULT = "{\"name\":\"JACK DANIELS TENNESSEE HONEY 1750ml\",\"div_code\":null,\"dept_code\":null,\"class_code\":null,\"size\":1750,\"csc\":0,\"price\":0.0,\"lcboPrice\":0.0,\"status\":null,\"tags\":null,\"creationTimestamp";
		
		@Test
		public void test() {
			UsingNothingIntegrationTests.doGenericProcessorTest(channels, collector, TEST_RESULT);
		}
	}

	@SpringBootApplication
	public static class UtahLcboMatcherProcessorApplication {
		public static void main(String[] args) {
			SpringApplication.run(UtahLcboMatcherProcessorApplication.class, args);
		}
	}

}
