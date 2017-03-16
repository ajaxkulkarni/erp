package com.rns.web.erp.service.dao.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
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
@Table(name = "employee_leave_balance")
public class ERPEmployeeLeaveBalance {
	
	private Integer id;
	private ERPEmployeeDetails employee;
	private ERPLeaveType type;
	private String comments;
	private Date updatedDate;
	private BigDecimal balance;
	private Date lastScheduled;
	
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
	@JoinColumn(name = "employee_id")
	public ERPEmployeeDetails getEmployee() {
		return employee;
	}
	
	public void setEmployee(ERPEmployeeDetails employee) {
		this.employee = employee;
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
	
	@Column(name = "updated_date")
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	@Column(name = "balance")
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	@Column(name = "last_scheduled")
	public Date getLastScheduled() {
		return lastScheduled;
	}
	public void setLastScheduled(Date lastScheduled) {
		this.lastScheduled = lastScheduled;
	}
	
	
	
}
