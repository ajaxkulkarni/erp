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
@Table(name = "company_leave_policy")
public class ERPCompanyLeavePolicy {
	
	private Integer id;
	private ERPCompanyDetails company;
	private ERPLeaveType type;
	private String comments;
	private Date createdDate;
	private Integer maxAllowed;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "company_id")
	public ERPCompanyDetails getCompany() {
		return company;
	}
	public void setCompany(ERPCompanyDetails company) {
		this.company = company;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "leave_type")
	public ERPLeaveType getType() {
		return type;
	}
	public void setType(ERPLeaveType type) {
		this.type = type;
	}
	
	@Column(name = "comments")
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	@Column(name = "created_date")
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@Column(name = "max_allowed")
	public Integer getMaxAllowed() {
		return maxAllowed;
	}
	public void setMaxAllowed(Integer maxAllowed) {
		this.maxAllowed = maxAllowed;
	}

}
