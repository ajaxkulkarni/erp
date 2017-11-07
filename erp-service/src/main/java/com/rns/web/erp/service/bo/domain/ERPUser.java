package com.rns.web.erp.service.bo.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ERPUser implements Comparable<ERPUser> {
	
	private Integer id;
	private String regId;
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
	private List<ERPLeaveCategory> leaveBalance;
	private Integer totalLeaves;
	private List<ERPLeaveCategory> categories;
	private List<ERPLeave> leaves;
	private Integer withoutPayLeaves;
	private List<ERPUserExperience> experiences;
	private BigDecimal experience;
	private List<ERPUserExperience> qualifications;
	private String loginType;
	private ERPProject currentProject;
	private List<ERPProject> projects;
	private ERPRecord currentRecord;
	private ERPAccessRights rights;
	
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
	
	@Override
	public int hashCode() {
		if(id == null) {
			return super.hashCode();
		}
		return id;
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
	public List<ERPLeaveCategory> getLeaveBalance() {
		return leaveBalance;
	}
	public void setLeaveBalance(List<ERPLeaveCategory> leaveBalance) {
		this.leaveBalance = leaveBalance;
	}
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public ERPProject getCurrentProject() {
		return currentProject;
	}
	public void setCurrentProject(ERPProject currentProject) {
		this.currentProject = currentProject;
	}
	public List<ERPProject> getProjects() {
		if(projects == null) {
			projects = new ArrayList<ERPProject>();
		}
		return projects;
	}
	public void setProjects(List<ERPProject> projects) {
		this.projects = projects;
	}
	public ERPRecord getCurrentRecord() {
		return currentRecord;
	}
	public void setCurrentRecord(ERPRecord currentRecord) {
		this.currentRecord = currentRecord;
	}
	public ERPAccessRights getRights() {
		return rights;
	}
	public void setRights(ERPAccessRights rights) {
		this.rights = rights;
	}
	

}
