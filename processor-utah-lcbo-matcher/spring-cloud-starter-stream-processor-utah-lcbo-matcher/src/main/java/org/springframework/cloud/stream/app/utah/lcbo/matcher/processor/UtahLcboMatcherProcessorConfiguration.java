/*
 * 
 * Copyright (c) 2018 Solace Corp.
 * 
 */
package org.springframework.cloud.stream.app.utah.lcbo.matcher.processor;

import java.util.StringJoiner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import reactor.core.publisher.Flux;


import com.solace.demo.utahdabc.datamodel.Product;
import com.solace.demo.utahdabc.datamodel.ProductInventoryData;
import com.solace.demo.utahdabc.datamodel.StoreInventory;

/**
 * SCS processor - lookup.  Converts an address to lat/long, or the reverse.
 *
 * @author Solace Corp
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(UtahLcboMatcherProcessorProperties.class)
public class UtahLcboMatcherProcessorConfiguration {
	@Autowired
	private UtahLcboMatcherProcessorProperties properties;
	
	@Autowired
    private BinderAwareChannelResolver resolver;
	
    private static final Logger log = LoggerFactory.getLogger(UtahLcboMatcherProcessorConfiguration.class);	

	@StreamListener
	@Output(Processor.OUTPUT)
    public Flux<StoreInventory> process(@Input(Processor.INPUT) Flux<Product> input) {
		return input.map(p -> lookupInventory(p));
    }

	// Returns: Top store for inventory (max inventory for product)
	private StoreInventory lookupInventory(Product p) {
		RestTemplate restTemplate = new RestTemplate();
		String init = restTemplate.getForObject(properties.getInventoryQueryUrl(), String.class);

		Document doc = Jsoup.parse(init);
		String VIEWSTATE = doc.select("input[id$=__VIEWSTATE]").get(0).attributes().get("value");
		String EVENTVALIDATION = doc.select("input[id$=__EVENTVALIDATION]").get(0).attributes().get("value");

		HttpHeaders headers = new HttpHeaders();
		headers.add("User-Agent", "Mozilla");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("__VIEWSTATE", VIEWSTATE);
		map.add("__EVENTVALIDATION", EVENTVALIDATION);
		map.add("__ASYNCPOST", "true");
		map.add("ctl00$ContentPlaceHolderBody$tbCscCode", Integer.toString(p.getCsc()));

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(
				properties.getInventoryQueryUrl(), request, String.class);

		Document responseDoc = Jsoup.parse(response.toString());
		ProductInventoryData prodInv = new ProductInventoryData();
		StoreInventory topInventoryStore = new StoreInventory();
		
		if (responseDoc.select("#ContentPlaceHolderBody_lblWhsInv").size() > 0) {
			prodInv.setWarehouseInventoryQty(
					Integer.parseInt(responseDoc.select("#ContentPlaceHolderBody_lblWhsInv").get(0).text()));
			prodInv.setWarehouseOnOrderQty(
					Integer.parseInt(responseDoc.select("#ContentPlaceHolderBody_lblWhsOnOrder").get(0).text()));
			prodInv.setProductStatus(responseDoc.select("#ContentPlaceHolderBody_lblStatus").get(0).text());
			prodInv.setProduct(p);

			Elements table = responseDoc.select("#ContentPlaceHolderBody_gvInventoryDetails");
			Elements tableRows = table.select("tr[class='gridViewRow']");

			for (Element row : tableRows) {
				StoreInventory storeInv = new StoreInventory();
				Elements data = row.select("td");
				storeInv.setStoreID(data.get(0).text());
				storeInv.setStoreName(data.get(1).text());
				storeInv.setProductQty(Integer.parseInt(data.get(2).select("span").get(0).text()));
				storeInv.setStoreAddress(data.get(3).text());
				storeInv.setStoreCity(data.get(4).text());
				storeInv.setStorePhone(data.get(5).text());
				prodInv.setStoreInventory(storeInv);

				StringJoiner sj = new StringJoiner(" ");
				p.setTags(sj.add(p.getName())
						.add(p.getClass_code())
						.add(Integer.toString(p.getSize()))
						.add(prodInv.getProductStatus())
						.add(storeInv.getStoreName()).toString());
				
				// Track the store with the highest inventory for this product
				if (storeInv.getProductQty() > topInventoryStore.getProductQty())
					topInventoryStore = storeInv;
				
				String classCode = p.getClass_code();
				if (classCode != null && !classCode.isEmpty()) {
					char[] codeArray = classCode.toCharArray();
					String target = properties.getPublishTopicPrefix();

					for (int i = 0; i < codeArray.length; i++) {
						target += Character.toString(codeArray[i]);
						if (i < codeArray.length - 1) {
							target += "/";
						}
					}
					
					log.info(target + ": " + p.getName() + "[" + prodInv.getProductStatus() + "] Qty: " + prodInv.getStoreInventory().getProductQty());
					
					resolver.resolveDestination(target).send(MessageBuilder.withPayload(prodInv).build());
					
				} else {
					log.error("No class code for product: " + p.getName() + " CSC: " + p.getCsc());
				}
			}
		}
		
		return topInventoryStore;
	}
 
}
