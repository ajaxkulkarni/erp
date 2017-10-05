package com.rns.web.erp.service.bo.domain;

public class ERPAccessRights {
	
	private boolean recordAccess;
	private boolean fileAccess;
	private boolean commentAccess;
	private boolean projectAccess;
	
	public boolean isRecordAccess() {
		return recordAccess;
	}
	public void setRecordAccess(boolean recordAccess) {
		this.recordAccess = recordAccess;
	}
	public boolean isFileAccess() {
		return fileAccess;
	}
	public void setFileAccess(boolean fileAccess) {
		this.fileAccess = fileAccess;
	}
	public boolean isCommentAccess() {
		return commentAccess;
	}
	public void setCommentAccess(boolean commentAccess) {
		this.commentAccess = commentAccess;
	}
	public boolean isProjectAccess() {
		return projectAccess;
	}
	public void setProjectAccess(boolean projectAccess) {
		this.projectAccess = projectAccess;
	}
	
}
