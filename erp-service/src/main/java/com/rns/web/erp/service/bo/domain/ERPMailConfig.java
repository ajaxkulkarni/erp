package com.rns.web.erp.service.bo.domain;

public class ERPMailConfig {

	private boolean allMails;
	private boolean assignedToMails;
	private boolean createdByMails;
	
	public boolean isAllMails() {
		return allMails;
	}
	public void setAllMails(boolean allMails) {
		this.allMails = allMails;
	}
	public boolean isAssignedToMails() {
		return assignedToMails;
	}
	public void setAssignedToMails(boolean assignedToMails) {
		this.assignedToMails = assignedToMails;
	}
	public boolean isCreatedByMails() {
		return createdByMails;
	}
	public void setCreatedByMails(boolean createdByMails) {
		this.createdByMails = createdByMails;
	}
	
}
