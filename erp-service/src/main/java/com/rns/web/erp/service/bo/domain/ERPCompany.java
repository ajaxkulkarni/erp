package com.rns.web.erp.service.bo.domain;

import java.util.List;

import com.rns.web.erp.service.bo.api.ERPSalaryInfo;

public class ERPCompany {

	private Integer id;
	private String name;
	private String email;
	private String phone;
	private String address;
	private ERPUser createdBy;
	private List<ERPUser> employees;
	private List<ERPLeaveCategory> leaveTypes;
	private List<ERPLeavePolicy> leavePolicy;
	private List<ERPSalaryInfo> salaryInfo;
	private ERPSalaryInfo basic;
	private ERPFilter filter;
	
	
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public ERPUser getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(ERPUser createdBy) {
		this.createdBy = createdBy;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public List<ERPUser> getEmployees() {
		return employees;
	}
	public void setEmployees(List<ERPUser> employees) {
		this.employees = employees;
	}
	public List<ERPLeaveCategory> getLeaveTypes() {
		return leaveTypes;
	}
	public void setLeaveTypes(List<ERPLeaveCategory> leaveTypes) {
		this.leaveTypes = leaveTypes;
	}
	public List<ERPLeavePolicy> getLeavePolicy() {
		return leavePolicy;
	}
	public void setLeavePolicy(List<ERPLeavePolicy> leavePolicy) {
		this.leavePolicy = leavePolicy;
	}
	public List<ERPSalaryInfo> getSalaryInfo() {
		return salaryInfo;
	}
	public void setSalaryInfo(List<ERPSalaryInfo> salaryInfo) {
		this.salaryInfo = salaryInfo;
	}
	public ERPSalaryInfo getBasic() {
		return basic;
	}
	public void setBasic(ERPSalaryInfo basic) {
		this.basic = basic;
	}
	public ERPFilter getFilter() {
		return filter;
	}
	public void setFilter(ERPFilter filter) {
		this.filter = filter;
	}
	
}
