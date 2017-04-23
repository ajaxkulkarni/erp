package com.rns.web.erp.service.bo.domain;

import java.util.Date;

public class ERPComment {
	
	private Integer id;
	private String comment;
	private String status;
	private ERPUser commentedBy;
	private Date date;
	private String dateString;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ERPUser getCommentedBy() {
		return commentedBy;
	}
	public void setCommentedBy(ERPUser commentedBy) {
		this.commentedBy = commentedBy;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
	
}
