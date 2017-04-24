package com.rns.web.erp.service.bo.domain;

public class ERPLog {
	
	private Integer id;
	private String logDate;
	private String log;
	private ERPUser user;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLogDate() {
		return logDate;
	}
	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public ERPUser getUser() {
		return user;
	}
	public void setUser(ERPUser user) {
		this.user = user;
	}
	
}
