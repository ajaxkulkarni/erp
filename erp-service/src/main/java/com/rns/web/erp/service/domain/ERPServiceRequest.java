package com.rns.web.erp.service.domain;

import com.rns.web.erp.service.bo.domain.ERPLeave;
import com.rns.web.erp.service.bo.domain.ERPUser;

public class ERPServiceRequest {
	
	private ERPUser user;
	private ERPLeave leave;
	private String requestType;

	public ERPUser getUser() {
		return user;
	}

	public void setUser(ERPUser user) {
		this.user = user;
	}

	public ERPLeave getLeave() {
		return leave;
	}

	public void setLeave(ERPLeave leave) {
		this.leave = leave;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	
}
