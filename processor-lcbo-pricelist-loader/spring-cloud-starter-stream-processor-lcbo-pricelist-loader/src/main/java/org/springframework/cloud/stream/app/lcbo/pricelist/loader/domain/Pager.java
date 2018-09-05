package org.springframework.cloud.stream.app.lcbo.pricelist.loader.domain;

public class Pager {
	private int records_per_page;
	private int total_record_count;
	private int current_page_record_count;
	private boolean is_first_page;
	private boolean is_final_page;
	private int current_page;
	private String current_page_path;
	private int next_page;
	private String next_page_path;
	private String previous_page;
	private String previous_page_path;
	private int total_pages;
	private String total_pages_path;
	public int getRecords_per_page() {
		return records_per_page;
	}
	public void setRecords_per_page(int records_per_page) {
		this.records_per_page = records_per_page;
	}
	public int getTotal_record_count() {
		return total_record_count;
	}
	public void setTotal_record_count(int total_record_count) {
		this.total_record_count = total_record_count;
	}
	public int getCurrent_page_record_count() {
		return current_page_record_count;
	}
	public void setCurrent_page_record_count(int current_page_record_count) {
		this.current_page_record_count = current_page_record_count;
	}
	public boolean isIs_first_page() {
		return is_first_page;
	}
	public void setIs_first_page(boolean is_first_page) {
		this.is_first_page = is_first_page;
	}
	public boolean isIs_final_page() {
		return is_final_page;
	}
	public void setIs_final_page(boolean is_final_page) {
		this.is_final_page = is_final_page;
	}
	public String getCurrent_page_path() {
		return current_page_path;
	}
	public void setCurrent_page_path(String current_page_path) {
		this.current_page_path = current_page_path;
	}
	public int getCurrent_page() {
		return current_page;
	}
	public void setCurrent_page(int current_page) {
		this.current_page = current_page;
	}
	public int getNext_page() {
		return next_page;
	}
	public void setNext_page(int next_page) {
		this.next_page = next_page;
	}
	public String getNext_page_path() {
		return next_page_path;
	}
	public void setNext_page_path(String next_page_path) {
		this.next_page_path = next_page_path;
	}
	public String getPrevious_page() {
		return previous_page;
	}
	public void setPrevious_page(String previous_page) {
		this.previous_page = previous_page;
	}
	public String getPrevious_page_path() {
		return previous_page_path;
	}
	public void setPrevious_page_path(String previous_page_path) {
		this.previous_page_path = previous_page_path;
	}
	public int getTotal_pages() {
		return total_pages;
	}
	public void setTotal_pages(int total_pages) {
		this.total_pages = total_pages;
	}
	public String getTotal_pages_path() {
		return total_pages_path;
	}
	public void setTotal_pages_path(String total_pages_path) {
		this.total_pages_path = total_pages_path;
	}
}
