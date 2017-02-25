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
@Table(name = "employee_leaves")
public class ERPEmployeeLeave {

	
	private Integer id;
	private Date fromDate;
	private Date toDate;
	private ERPLeaveType type;
	private String status;
	private String comments;
	private Date updatedDate;
	private Date appliedDate;
	private Integer noOfDays;
	private ERPEmployeeDetails employee;
	private ERPLoginDetails appliedBy;
	private Integer withoutPay;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "from_date")	
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	@Column(name = "to_date")
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
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
	
	@Column(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column(name = "comments")
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	@Column(name = "updated_date")
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "employee_id")
	public ERPEmployeeDetails getEmployee() {
		return employee;
	}
	
	public void setEmployee(ERPEmployeeDetails employee) {
		this.employee = employee;
	}
	
	@Column(name = "applied_date")
	public Date getAppliedDate() {
		return appliedDate;
	}
	
	public void setAppliedDate(Date appliedDate) {
		this.appliedDate = appliedDate;
	}
	
	@Column(name = "no_of_days")
	public Integer getNoOfDays() {
		return noOfDays;
	}
	public void setNoOfDays(Integer noOfDays) {
		this.noOfDays = noOfDays;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "applied_by")
	public ERPLoginDetails getAppliedBy() {
		return appliedBy;
	}
	public void setAppliedBy(ERPLoginDetails appliedBy) {
		this.appliedBy = appliedBy;
	}
	
	public void setWithoutPay(Integer withoutPay) {
		this.withoutPay = withoutPay;
	}
	
	@Column(name = "without_pay")
	public Integer getWithoutPay() {
		return withoutPay;
	}
	
}

