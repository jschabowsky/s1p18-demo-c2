/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */

package org.springframework.cloud.stream.app.lcbo.data.mapper.processor;

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

import static org.hamcrest.CoreMatchers.startsWith;
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
public abstract class LcboDataMapperProcessorIntegrationTests {

	@Autowired
	protected Processor channels;

	@Autowired
	protected MessageCollector collector;
	
	private static final String RESULT_SUBSTRING = "{\"name\":null,\"div_code\":null,\"dept_code\":null,\"class_code\":\"AWA\",\"size\":0,\"csc\":0,\"price\":0.0,\"lcboPrice\":0.0,\"status\":null,\"tags\":null,\"_timestamp\":\"";

	/**
	 * Validates that the module loads with default properties.
	 */
	public static class UsingNothingIntegrationTests extends LcboDataMapperProcessorIntegrationTests {
		
		public static void doGenericProcessorTest(Processor channels, MessageCollector collector) {
			Product p = new Product();
			p.setClass_code("AWA");
			
			channels.input().send(new GenericMessage<Product>(p));
			assertThat(collector.forChannel(channels.output()), receivesPayloadThat(startsWith(RESULT_SUBSTRING)));
		}

		@Test
		public void test() {
			doGenericProcessorTest(channels, collector);
		}
	}

	@SpringBootTest("lcbo.data.mapper.publishTopicPrefix=product/")
	public static class UsingPropsIntegrationTests extends LcboDataMapperProcessorIntegrationTests {
		@Test
		public void test() {
			UsingNothingIntegrationTests.doGenericProcessorTest(channels, collector);
		}
	}

	@SpringBootApplication
	public static class LcboDataMapperProcessorApplication {
		public static void main(String[] args) {
			SpringApplication.run(LcboDataMapperProcessorApplication.class, args);
		}
	}

}
