package com.rns.web.erp.service.bo.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ERPUser implements Comparable<ERPUser> {
	
	private Integer id;
	private String name;
	private String designation;
	private String email;
	private String password;
	private String newPassword;
	private String phone;
	private ERPCompany company;
	private String status;
	private String type;
	private String department;
	private String location;
	private String qualification;
	private Date joiningDate;
	private ERPFinancial financial;
	private String employeeStatus;
	private List<ERPLeaveCategory> leaveCount;
	private Integer totalLeaves;
	private List<ERPLeaveCategory> categories;
	private List<ERPLeave> leaves;
	private Integer withoutPayLeaves;
	private List<ERPUserExperience> experiences;
	private BigDecimal experience;
	private List<ERPUserExperience> qualifications;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public ERPCompany getCompany() {
		return company;
	}
	public void setCompany(ERPCompany company) {
		this.company = company;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<ERPLeaveCategory> getLeaveCount() {
		return leaveCount;
	}
	public void setLeaveCount(List<ERPLeaveCategory> leaveCount) {
		this.leaveCount = leaveCount;
	}
	public List<ERPLeaveCategory> getCategories() {
		return categories;
	}
	public void setCategories(List<ERPLeaveCategory> categories) {
		this.categories = categories;
	}
	public List<ERPLeave> getLeaves() {
		return leaves;
	}
	public void setLeaves(List<ERPLeave> leaves) {
		this.leaves = leaves;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public Date getJoiningDate() {
		return joiningDate;
	}
	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
	}
	public ERPFinancial getFinancial() {
		return financial;
	}
	public void setFinancial(ERPFinancial financial) {
		this.financial = financial;
	}
	public String getEmployeeStatus() {
		return employeeStatus;
	}
	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public Integer getTotalLeaves() {
		return totalLeaves;
	}
	public void setTotalLeaves(Integer totalLeaves) {
		this.totalLeaves = totalLeaves;
	}
	public Integer getWithoutPayLeaves() {
		if(withoutPayLeaves == null){
			return 0;
		}
		return withoutPayLeaves;
	}
	public void setWithoutPayLeaves(Integer withoutPayLeaves) {
		this.withoutPayLeaves = withoutPayLeaves;
	}
	
	@Override
	public boolean equals(Object obj) {
		ERPUser user = (ERPUser) obj;
		if(user == null) {
			return false;
		}
		if(user.getId() == null || this.getId() == null){
			return false;
		}
		return user.getId().intValue() == this.getId().intValue();
	}
	public int compareTo(ERPUser o) {
		if(o == null) {
			return 1;
		}
		if(o.getId() == null || this.getId() == null){
			return 1;
		}
		if(o.getId().intValue() == this.getId().intValue()) {
			return 0;
		} else if (o.getId().intValue() < this.getId().intValue()) {
			return 1;
		}
		return -1;
	}
	public List<ERPUserExperience> getExperiences() {
		return experiences;
	}
	public void setExperiences(List<ERPUserExperience> experiences) {
		this.experiences = experiences;
	}
	public BigDecimal getExperience() {
		return experience;
	}
	public void setExperience(BigDecimal experience) {
		this.experience = experience;
	}
	public List<ERPUserExperience> getQualifications() {
		return qualifications;
	}
	public void setQualifications(List<ERPUserExperience> qualifications) {
		this.qualifications = qualifications;
	}
	

}
