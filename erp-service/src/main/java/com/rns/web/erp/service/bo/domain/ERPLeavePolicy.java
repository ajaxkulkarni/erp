package com.rns.web.erp.service.bo.domain;

public class ERPLeavePolicy {
	
	private Integer id;
	private ERPCompany company;
	private ERPLeaveCategory category;
	private Integer maxAllowed;
	private String frequency;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public ERPLeaveCategory getCategory() {
		return category;
	}
	public void setCategory(ERPLeaveCategory category) {
		this.category = category;
	}
	public Integer getMaxAllowed() {
		return maxAllowed;
	}
	public void setMaxAllowed(Integer maxAllowed) {
		this.maxAllowed = maxAllowed;
	}
	public ERPCompany getCompany() {
		return company;
	}
	public void setCompany(ERPCompany company) {
		this.company = company;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	
}
