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
@Table(name = "project_record_values")
public class ERPProjectRecordValues {
	
	private Integer id;
	private String value;
	private Date updatedDate;
	private ERPProjectRecords record;
	private ERPProjectFields field;
	private ERPLoginDetails updatedBy;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	@JoinColumn(name = "record_id")
	public ERPProjectRecords getRecord() {
		return record;
	}
	
	public void setRecord(ERPProjectRecords record) {
		this.record = record;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "updated_by")
	public ERPLoginDetails getUpdatedBy() {
		return updatedBy;
	}
	
	public void setUpdatedBy(ERPLoginDetails updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	@Column(name = "value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "field_id")
	public ERPProjectFields getField() {
		return field;
	}
	public void setField(ERPProjectFields field) {
		this.field = field;
	}
}
