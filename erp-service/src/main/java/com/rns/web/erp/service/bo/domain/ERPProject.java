package com.rns.web.erp.service.bo.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ERPProject {
	
	private Integer id;
	private String title;
	private String description;
	private ERPUser createdBy;
	private Date createdDate;
	private List<ERPUser> users;
	private String status;
	private ERPField titleField;
	private List<ERPField> fields;
	private List<ERPRecord> records;
	private ERPAccessRights accessRights;
	private ERPMailConfig mailConfig;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ERPUser getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(ERPUser createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public List<ERPUser> getUsers() {
		if(users == null) {
			users = new ArrayList<ERPUser>();
		}
		return users;
	}
	public void setUsers(List<ERPUser> users) {
		this.users = users;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ERPField getTitleField() {
		return titleField;
	}
	public void setTitleField(ERPField titleField) {
		this.titleField = titleField;
	}
	public List<ERPField> getFields() {
		if(fields == null) {
			fields = new ArrayList<ERPField>();
		}
		return fields;
	}
	public void setFields(List<ERPField> fields) {
		this.fields = fields;
	}
	public List<ERPRecord> getRecords() {
		if(records == null) {
			records = new ArrayList<ERPRecord>();
		}
		return records;
	}
	public void setRecords(List<ERPRecord> records) {
		this.records = records;
	}
	
	public ERPAccessRights getAccessRights() {
		return accessRights;
	}
	public void setAccessRights(ERPAccessRights accessRights) {
		this.accessRights = accessRights;
	}
	public ERPMailConfig getMailConfig() {
		return mailConfig;
	}
	public void setMailConfig(ERPMailConfig mailConfig) {
		this.mailConfig = mailConfig;
	}
	
}
