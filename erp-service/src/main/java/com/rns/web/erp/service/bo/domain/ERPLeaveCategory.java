package com.rns.web.erp.service.bo.domain;

public class ERPLeaveCategory {
	
	private Integer id;
	private String name;
	private Integer count;
	private Integer available;
	private Integer withoutPay;
	private String frequency;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Integer getAvailable() {
		return available;
	}
	public void setAvailable(Integer available) {
		this.available = available;
	}
	public Integer getWithoutPay() {
		return withoutPay;
	}
	public void setWithoutPay(Integer withoutPay) {
		this.withoutPay = withoutPay;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

}
