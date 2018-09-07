package org.springframework.cloud.stream.app.lcbo.pricelist.loader.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LcboProduct {
	private int id;
	private String name;
	private String tags;
	
	private int price_in_cents;		// CAD value
	
	private String primary_category;
	private String secondary_category;
	private String tertiary_category;
	
	private int volume_in_milliliters;
	private int alcohol_content;
	private int inventory_count;
	
	private String producer_name;
	
	private String image_url;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public int getPrice_in_cents() {
		return price_in_cents;
	}

	public void setPrice_in_cents(int price_in_cents) {
		this.price_in_cents = price_in_cents;
	}

	public String getPrimary_category() {
		return primary_category;
	}

	public void setPrimary_category(String primary_category) {
		this.primary_category = primary_category;
	}

	public String getSecondary_category() {
		return secondary_category;
	}

	public void setSecondary_category(String secondary_category) {
		this.secondary_category = secondary_category;
	}

	public String getTertiary_category() {
		return tertiary_category;
	}

	public void setTertiary_category(String tertiary_category) {
		this.tertiary_category = tertiary_category;
	}

	public int getVolume_in_milliliters() {
		return volume_in_milliliters;
	}

	public void setVolume_in_milliliters(int volume_in_milliliters) {
		this.volume_in_milliliters = volume_in_milliliters;
	}

	public int getAlcohol_content() {
		return alcohol_content;
	}

	public void setAlcohol_content(int alcohol_content) {
		this.alcohol_content = alcohol_content;
	}

	public int getInventory_count() {
		return inventory_count;
	}

	public void setInventory_count(int inventory_count) {
		this.inventory_count = inventory_count;
	}

	public String getProducer_name() {
		return producer_name;
	}

	public void setProducer_name(String producer_name) {
		this.producer_name = producer_name;
	}
	
	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	@Override
	public String toString() {
		return name;
	}
}
