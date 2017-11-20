package com.rns.web.erp.service.domain;

import com.rns.web.erp.service.bo.domain.ERPLeave;
import com.rns.web.erp.service.bo.domain.ERPUser;

public class ERPServiceRequest {
	
	private ERPUser user;
	private ERPLeave leave;
	private String requestType;
	private String timeRange;

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

	public String getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}

	
}
