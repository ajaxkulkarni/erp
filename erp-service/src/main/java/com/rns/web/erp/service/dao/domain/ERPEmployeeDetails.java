package com.rns.web.erp.service.dao.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "employee_details")
public class ERPEmployeeDetails {

	
	private Integer id;
	private String regId;
	private String designation;
	private String name;
	private String email;
	private ERPCompanyDetails company;
	private Date createdDate;
	private String department;
	private String type;
	private Date joiningDate;
	private String location;
	private String qualification;
	private ERPEmployeeFinancials financials;
	private String status;
	private String experiences;
	private String qualifications;
	private Integer totalExperience;
	private List<ERPEmployeeSalaryStructure> structures;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "designation")
	public String getDesignation() {
		return designation;
	}
	
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	@Column(name = "created_date")
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@Column(name = "email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "department")
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	
	@Column(name = "type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "joining_date")
	public Date getJoiningDate() {
		return joiningDate;
	}
	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
	}
	
	@Column(name = "location")
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	@Column(name = "qualification")
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "employee")
	public ERPEmployeeFinancials getFinancials() {
		return financials;
	}
	public void setFinancials(ERPEmployeeFinancials financials) {
		this.financials = financials;
	}
	
	@Column(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column(name = "experiences")
	public String getExperiences() {
		return experiences;
	}
	public void setExperiences(String experiences) {
		this.experiences = experiences;
	}
	
	@Column(name = "total_experience")
	public Integer getTotalExperience() {
		return totalExperience;
	}
	public void setTotalExperience(Integer totalExperience) {
		this.totalExperience = totalExperience;
	}
	
	@Column(name = "qualifications")
	public String getQualifications() {
		return qualifications;
	}
	public void setQualifications(String qualifications) {
		this.qualifications = qualifications;
	}
	
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	public List<ERPEmployeeSalaryStructure> getStructures() {
		return structures;
	}
	public void setStructures(List<ERPEmployeeSalaryStructure> structures) {
		this.structures = structures;
	}
	
	@Column(name = "reg_id")
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	
}

