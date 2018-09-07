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
import org.springframework.cloud.stream.app.lcbo.pricelist.loader.domain.LcboProduct;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
	
	/**
	 * Validates that the module loads with default properties.
	 */
	public static class UsingNothingIntegrationTests extends LcboDataMapperProcessorIntegrationTests {
		private static final String RESULT_SUBSTRING = "{\"name\":\"Jack Daniel's Tennessee Whiskey\",\"div_code\":null,\"dept_code\":null,\"class_code\":null,\"size\":0,\"csc\":215616,\"price\":0.0,\"lcboPrice\":46.95,\"status\":null,\"tags\":\"jack daniel's daniels tennessee whiskey spirits whiskywhiskey usa brown forman brown-forman brownforman louisville operations bottle\",\"creationTimestamp\":";
	
		public static void doGenericProcessorTest(Processor channels, MessageCollector collector, String expectedResult) {

			LcboProduct p = new LcboProduct();
			p.setName("Jack Daniel's Tennessee Whiskey");
			p.setId(215616);
			p.setPrice_in_cents(4695);
			p.setPrimary_category("Spirits");
			p.setSecondary_category("Whisky/Whiskey");
			p.setTags("jack daniel's daniels tennessee whiskey spirits whiskywhiskey usa brown forman brown-forman brownforman louisville operations bottle");
			p.setTertiary_category("Bourbon/American Whiskey");
			
			channels.input().send(new GenericMessage<LcboProduct>(p));
			assertThat(collector.forChannel(channels.output()), receivesPayloadThat(startsWith(expectedResult)));
		}

		@Test
		public void test() {
			doGenericProcessorTest(channels, collector, RESULT_SUBSTRING);
		}
	}

	@SpringBootTest("lcbo.data.mapper.categoryInfoPublished=true")
	public static class UsingPropsIntegrationTests extends LcboDataMapperProcessorIntegrationTests {
		private static final String RESULT_SUBSTRING = "{\"name\":\"Jack Daniel's Tennessee Whiskey\",\"div_code\":null,\"dept_code\":null,\"class_code\":\"Spirits:Whisky/Whiskey:Bourbon/American Whiskey\",\"size\":0,\"csc\":215616,\"price\":0.0,\"lcboPrice\":46.95,\"status\":null,\"tags\":\"jack daniel's daniels tennessee whiskey spirits whiskywhiskey usa brown forman brown-forman brownforman louisville operations bottle\",\"creationTimestamp\":";
													   
		@Test
		public void test() {
			UsingNothingIntegrationTests.doGenericProcessorTest(channels, collector, RESULT_SUBSTRING);
		}
	}

	@SpringBootApplication
	public static class LcboDataMapperProcessorApplication {
		public static void main(String[] args) {
			SpringApplication.run(LcboDataMapperProcessorApplication.class, args);
		}
	}

}
