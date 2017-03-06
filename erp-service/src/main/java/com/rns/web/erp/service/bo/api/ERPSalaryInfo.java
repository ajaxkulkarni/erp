package com.rns.web.erp.service.bo.api;

import java.math.BigDecimal;

import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPUser;

public class ERPSalaryInfo {

	private Integer id;
	private String rule;
	private String type;
	private BigDecimal amount;
	private BigDecimal percentage;
	private ERPCompany company;
	private String description;
	private String amountType;
	private boolean customRule;
	private boolean error;
	private ERPUser user;
	
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
	public BigDecimal getPayable(ERPSalaryInfo basic) {
		if(this.amount != null) {
			return this.amount;
		}
		if(basic == null || basic.getAmount() == null) {
			return BigDecimal.ZERO;
		}
		if(this.percentage != null) {
			this.percentage.divide(new BigDecimal(100)).multiply(basic.getAmount());
		}
		return BigDecimal.ZERO;
	}
	public String getAmountType() {
		return amountType;
	}
	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}
	public boolean isCustomRule() {
		return customRule;
	}
	public void setCustomRule(boolean customRule) {
		this.customRule = customRule;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public ERPUser getUser() {
		return user;
	}
	public void setUser(ERPUser user) {
		this.user = user;
	}
	
}
