package com.rns.web.erp.service.bo.api;

import java.math.BigDecimal;

import com.rns.web.erp.service.bo.domain.ERPCompany;

public class ERPSalaryInfo {

	private Integer id;
	private String rule;
	private String type;
	private BigDecimal amount;
	private BigDecimal percentage;
	private ERPCompany company;
	private String description;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getPercentage() {
		return percentage;
	}
	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}
	public ERPCompany getCompany() {
		return company;
	}
	public void setCompany(ERPCompany company) {
		this.company = company;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
