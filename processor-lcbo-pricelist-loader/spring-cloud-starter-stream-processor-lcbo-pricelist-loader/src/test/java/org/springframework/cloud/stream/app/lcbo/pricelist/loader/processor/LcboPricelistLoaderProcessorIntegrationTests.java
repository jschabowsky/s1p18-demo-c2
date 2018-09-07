/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */

package org.springframework.cloud.stream.app.lcbo.pricelist.loader.processor;

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
public abstract class LcboPricelistLoaderProcessorIntegrationTests {

	@Autowired
	protected Processor channels;

	@Autowired
	protected MessageCollector collector;
	
	private static final String RESULT_SUBSTRING = "{\"id\":52902,\"name\":\"Kissui Vodka\",\"tags\":\"kissui vodka spirits japan region not specified takara sake usa inc bottle\",\"price_in_cents\":2495,\"primary_category\":\"Spirits\",\"secondary_category\":\"Vodka\",\"tertiary_category\":\"Unflavoured Vodka\",\"volume_in_milliliters\":750,\"alcohol_content\":4000,\"inventory_count\":0,\"producer_name\":\"Takara Sake U.S.A. Inc.\",\"image_url\":\"https://dx5vpyka4lqst.cloudfront.net/products/52902/images/full.jpeg\"}";
	
	@SpringBootTest("lcbo.pricelist.loader.processorUrl=http://lcboapi.com/products?where=is_dead,is_discontinued&q=sake")
	public static class UsingPropsIntegrationTests extends LcboPricelistLoaderProcessorIntegrationTests {
		@Test
		public void test() {
			channels.input().send(new GenericMessage<String>("Test Trigger"));
			assertThat(collector.forChannel(channels.output()), receivesPayloadThat(startsWith(RESULT_SUBSTRING)));
		}
	}

	@SpringBootApplication
	public static class LcboPricelistLoaderProcessorApplication {
		public static void main(String[] args) {
			SpringApplication.run(LcboPricelistLoaderProcessorApplication.class, args);
		}
	}

}
