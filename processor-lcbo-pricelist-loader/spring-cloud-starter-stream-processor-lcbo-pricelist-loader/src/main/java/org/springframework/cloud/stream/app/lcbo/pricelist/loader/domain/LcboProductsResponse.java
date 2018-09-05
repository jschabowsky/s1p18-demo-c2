package org.springframework.cloud.stream.app.lcbo.pricelist.loader.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LcboProductsResponse {
	private String status;
	private Pager pager;
	
	private List<LcboProduct> result;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

	public List<LcboProduct> getResult() {
		return result;
	}

	public void setResult(List<LcboProduct> result) {
		this.result = result;
	}

}
