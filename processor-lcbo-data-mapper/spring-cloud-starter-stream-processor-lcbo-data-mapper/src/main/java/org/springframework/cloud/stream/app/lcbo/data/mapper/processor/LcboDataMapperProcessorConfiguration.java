/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.lcbo.data.mapper.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.app.lcbo.pricelist.loader.domain.LcboProduct;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;
import com.solace.demo.utahdabc.datamodel.Product;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SCS processor - pricelist parser - splits an HTML pricelist into individual products and outputs as JSON or a user-defined content type.
 *
 * @author Solace Corp
 */

@EnableBinding(Processor.class)
@EnableConfigurationProperties(LcboDataMapperProcessorProperties.class)
public class LcboDataMapperProcessorConfiguration {
	private static final Log LOG = LogFactory.getLog(LcboDataMapperProcessorConfiguration.class);

	@Autowired
	private LcboDataMapperProcessorProperties properties;

	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
	public Product process(LcboProduct lcboProduct) {
		LOG.info(lcboProduct);
		
		Product p = new Product();
		p.setName(lcboProduct.getName());
		p.setSize(lcboProduct.getVolume_in_milliliters());
		p.setLcboPrice(lcboProduct.getPrice_in_cents());
		p.setTags(lcboProduct.getTags());
		p.setCsc(lcboProduct.getId());

		if (properties.isCategoryInfoPublished()) {
			String classCode = Stream.of(lcboProduct.getPrimary_category(),
					lcboProduct.getSecondary_category(),
					lcboProduct.getTertiary_category())
			.filter(s -> s != null && !s.isEmpty())
			.collect(Collectors.joining(properties.getCategoryDelimiter()));
			
			p.setClass_code(classCode);
		}

		return p;
	}
}
