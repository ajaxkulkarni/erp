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
@Table(name = "project_users")
public class ERPProjectUsers {
	
	private Integer id;
	private String status;
	private Date createdDate;
	private String getNotification;
	private Date lastUpdated;
	private ERPProjects project;
	private ERPLoginDetails user;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column(name = "created_date")
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@Column(name = "get_notification")
	public String getGetNotification() {
		return getNotification;
	}
	public void setGetNotification(String getNotification) {
		this.getNotification = getNotification;
	}
	
	@Column(name = "last_updated")
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
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
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id")
	public ERPLoginDetails getUser() {
		return user;
	}
	public void setUser(ERPLoginDetails user) {
		this.user = user;
	}
	
}
