package com.rns.web.erp.service.domain;

import java.util.List;

import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPUser;

public class ERPServiceResponse {
	
	private Integer status;
	private Integer companyId;
	private String responseText;
	private List<ERPUser> users;
	private ERPUser user;
	private ERPCompany company;
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getResponseText() {
		return responseText;
	}
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	public List<ERPUser> getUsers() {
		return users;
	}
	public void setUsers(List<ERPUser> users) {
		this.users = users;
	}
	public ERPUser getUser() {
		return user;
	}
	public void setUser(ERPUser user) {
		this.user = user;
	}
	public ERPCompany getCompany() {
		return company;
	}
	public void setCompany(ERPCompany company) {
		this.company = company;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	
	

}
