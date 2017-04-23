package com.rns.web.erp.service.dao.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "project_log")
public class ERPProjectLog {
	
	
	private Integer id;
	private String log;
	private String action;
	private Date createdDate;
	private ERPLoginDetails createdBy;
	private ERPProjectRecords record;
	private ERPProjects project;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "log")
	public String getLog() {
		return log;
	}
	
	public void setLog(String log) {
		this.log = log;
	}
	
	@Column(name = "created_date")
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "record_id")
	public ERPProjectRecords getRecord() {
		return record;
	}
	
	public void setRecord(ERPProjectRecords record) {
		this.record = record;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id")
	public ERPLoginDetails getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(ERPLoginDetails createdBy) {
		this.createdBy = createdBy;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "project_id")
	public ERPProjects getProject() {
		return project;
	}
	public void setProject(ERPProjects project) {
		this.project = project;
	}
	
	@Column(name = "action")
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

}
