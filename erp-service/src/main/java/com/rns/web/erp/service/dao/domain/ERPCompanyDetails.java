package com.rns.web.erp.service.dao.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "company_details")
public class ERPCompanyDetails {

	private Integer id;
	private String name;
	private String email;
	private String address;
	private String phone;
	private Date createdDate;
	private ERPLoginDetails createdBy;
	private Set<ERPCompanyLeavePolicy> policy = new HashSet<ERPCompanyLeavePolicy>(0);
	private Set<ERPSalaryStructure> salaryInfo = new HashSet<ERPSalaryStructure>(0);
	private String bankName;
	private String accountNumber;
	private String companyPan;
	private String branchName;
	private String ifscCode;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "address")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Column(name = "phone")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Column(name = "created_date")
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@OneToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "created_by")
	public ERPLoginDetails getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(ERPLoginDetails createdBy) {
		this.createdBy = createdBy;
	}
	
	@OneToMany(mappedBy = "company",fetch = FetchType.LAZY)
	public Set<ERPCompanyLeavePolicy> getPolicy() {
		return policy;
	}
	
	public void setPolicy(Set<ERPCompanyLeavePolicy> policy) {
		this.policy = policy;
	}
	
	@OneToMany(mappedBy = "company",fetch = FetchType.LAZY)
	public Set<ERPSalaryStructure> getSalaryInfo() {
		return salaryInfo;
	}
	public void setSalaryInfo(Set<ERPSalaryStructure> salaryInfo) {
		this.salaryInfo = salaryInfo;
	}
	
	@Column(name = "bank_name")
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	@Column(name = "account_no")
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	@Column(name = "pan")
	public String getCompanyPan() {
		return companyPan;
	}
	public void setCompanyPan(String companyPan) {
		this.companyPan = companyPan;
	}
	
	@Column(name = "branch_name")
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	@Column(name = "ifsc_code")
	public String getIfscCode() {
		return ifscCode;
	}
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	
}
