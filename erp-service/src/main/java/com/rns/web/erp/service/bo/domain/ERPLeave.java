package com.rns.web.erp.service.bo.domain;

import java.util.Date;

public class ERPLeave {
	
	private Integer id;
	private Date from;
	private Date to;
	private ERPLeaveCategory type;
	private String reason;
	private Date appliedDate;
	private Date updatedDate;
	private String status;
	private ERPUser user;
	private ERPUser appliedBy;
	private Integer noOfDays;
	private String fromString;
	private String toString;
	private Integer withoutPay;
	private String approval;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	public ERPLeaveCategory getType() {
		return type;
	}
	public void setType(ERPLeaveCategory type) {
		this.type = type;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Date getAppliedDate() {
		return appliedDate;
	}
	public void setAppliedDate(Date appliedDate) {
		this.appliedDate = appliedDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ERPUser getUser() {
		return user;
	}
	public void setUser(ERPUser user) {
		this.user = user;
	}
	public ERPUser getAppliedBy() {
		return appliedBy;
	}
	public void setAppliedBy(ERPUser appliedBy) {
		this.appliedBy = appliedBy;
	}
	public Integer getNoOfDays() {
		return noOfDays;
	}
	public void setNoOfDays(Integer noOfDays) {
		this.noOfDays = noOfDays;
	}
	public String getFromString() {
		return fromString;
	}
	public void setFromString(String fromString) {
		this.fromString = fromString;
	}
	public String getToString() {
		return toString;
	}
	public void setToString(String toString) {
		this.toString = toString;
	}
	public Integer getWithoutPay() {
		return withoutPay;
	}
	public void setWithoutPay(Integer withoutPay) {
		this.withoutPay = withoutPay;
	}
	public String getApproval() {
		return approval;
	}
	public void setApproval(String approval) {
		this.approval = approval;
	}

}
