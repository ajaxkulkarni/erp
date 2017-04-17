package com.rns.web.erp.service.util;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPField;
import com.rns.web.erp.service.bo.domain.ERPFinancial;
import com.rns.web.erp.service.bo.domain.ERPLeave;
import com.rns.web.erp.service.bo.domain.ERPLeaveCategory;
import com.rns.web.erp.service.bo.domain.ERPLeavePolicy;
import com.rns.web.erp.service.bo.domain.ERPRecord;
import com.rns.web.erp.service.bo.domain.ERPSalaryInfo;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.dao.domain.ERPCompanyDetails;
import com.rns.web.erp.service.dao.domain.ERPCompanyLeavePolicy;
import com.rns.web.erp.service.dao.domain.ERPEmployeeDetails;
import com.rns.web.erp.service.dao.domain.ERPEmployeeFinancials;
import com.rns.web.erp.service.dao.domain.ERPEmployeeLeave;
import com.rns.web.erp.service.dao.domain.ERPEmployeeSalaryStructure;
import com.rns.web.erp.service.dao.domain.ERPLeaveType;
import com.rns.web.erp.service.dao.domain.ERPLoginDetails;
import com.rns.web.erp.service.dao.domain.ERPProjectFields;
import com.rns.web.erp.service.dao.domain.ERPProjectRecordValues;
import com.rns.web.erp.service.dao.domain.ERPProjectRecords;
import com.rns.web.erp.service.dao.domain.ERPProjectUsers;
import com.rns.web.erp.service.dao.domain.ERPProjects;
import com.rns.web.erp.service.dao.domain.ERPSalaryStructure;
import com.rns.web.erp.service.dao.impl.ERPUserDAO;

public class ERPBusinessConverter {

	public static ERPLoginDetails getLoginDetails(ERPUser user) {
		if(user == null) {
			return null;
		}
		ERPLoginDetails login = new ERPLoginDetails();
		if(user.getId() != null) {
			login.setId(user.getId());
		}
		setLoginDetails(user, login);
		return login;
	}

	public static void setLoginDetails(ERPUser user, ERPLoginDetails login) {
		login.setEmail(user.getEmail());
		login.setCreatedDate(new Date());
		login.setName(user.getName());
		login.setPassword(user.getPassword());
		login.setPhone(user.getPhone());
	}
	
	public static ERPEmployeeDetails getEmployeeDetails(ERPUser user) {
		ERPEmployeeDetails employee = new ERPEmployeeDetails();
		setEmployeeDetails(user, employee);
		employee.setCreatedDate(new Date());
		if(user.getCompany() != null) {
			ERPCompanyDetails company = new ERPCompanyDetails();
			company.setId(user.getCompany().getId());
			employee.setCompany(company);
		}
		return employee;
	}

	public static void setEmployeeDetails(ERPUser user, ERPEmployeeDetails employee) {
		employee.setName(user.getName());
		employee.setRegId(user.getRegId());
		employee.setDesignation(user.getDesignation());
		employee.setEmail(user.getEmail());
		employee.setType(user.getType());
		employee.setDepartment(user.getDepartment());
		employee.setDesignation(user.getDesignation());
		employee.setJoiningDate(user.getJoiningDate());
		employee.setLocation(user.getLocation());
		employee.setQualification(user.getQualification());
		employee.setStatus(user.getEmployeeStatus());
		ERPEmployeeFinancials financials = getFinancials(user.getFinancial(), employee);
		employee.setFinancials(financials);
		employee.setExperiences(CommonUtils.getUserExperience(user.getExperiences()));
		employee.setQualifications(CommonUtils.getUserExperience(user.getQualifications()));
	}
	
	private static ERPEmployeeFinancials getFinancials(ERPFinancial financial, ERPEmployeeDetails employee) {
		if(financial == null) {
			return null;
		}
		ERPEmployeeFinancials financials = employee.getFinancials();
		if(financials == null) {
			financials = new ERPEmployeeFinancials();
		} 
		financials.setAccountNo(financial.getAccountNumber());
		financials.setPanNo(financial.getPan());
		financials.setBankName(financial.getBankName());
		financials.setPfNo(financial.getPfNumber());
		if(financials.getEmployee() == null) {
			financials.setEmployee(employee);
		}
		return financials;
	}

	public static ERPCompanyDetails  getCompanyDetails(ERPCompany company) {
		if(company == null || StringUtils.isEmpty(company.getName())) {
			return null;
		}
		ERPCompanyDetails cmp = new ERPCompanyDetails();
		cmp.setCreatedDate(new Date());
		cmp.setName(company.getName());
		setCompanyDetails(company, cmp);
		return cmp;
	}

	public static void setCompanyDetails(ERPCompany company, ERPCompanyDetails cmp) {
		cmp.setAddress(company.getAddress());
		cmp.setEmail(company.getEmail());
		cmp.setPhone(company.getPhone());
		cmp.setTanNumber(company.getTan());
		if(company.getFinancial() != null) {
			cmp.setAccountNumber(company.getFinancial().getAccountNumber());
			cmp.setBankName(company.getFinancial().getBankName());
			cmp.setCompanyPan(company.getFinancial().getPan());
			cmp.setBranchName(company.getFinancial().getBranchName());
			cmp.setIfscCode(company.getFinancial().getIfscCode());
		}
	}

	public static ERPEmployeeLeave getLeaveDetails(ERPLeave leave) {
		if(leave == null) {
			return null;
		}
		ERPEmployeeLeave empLeave = new ERPEmployeeLeave();
		if(leave.getNoOfDays() != null) {
			empLeave.setNoOfDays(leave.getNoOfDays());
		}
		if(leave.getFrom() != null) {
			empLeave.setFromDate(leave.getFrom());
		}
		if(leave.getTo() != null) {
			empLeave.setToDate(leave.getTo());
		}
		/*if(StringUtils.isNotBlank(leave.getStatus())) {
			empLeave.setStatus(leave.getStatus());
		}*/
		if(leave.getType() != null && leave.getType().getId() != null) {
			ERPLeaveType type = new ERPLeaveType();
			type.setId(leave.getType().getId());
			empLeave.setType(type);
		}
		if(StringUtils.isNotBlank(leave.getReason())) {
			empLeave.setComments(leave.getReason());
		}
		if(leave.getUser() != null) {
			ERPEmployeeDetails emp = new ERPEmployeeDetails();
			emp.setId(leave.getUser().getId());
			empLeave.setEmployee(emp);
		}
		/*if(leave.getAppliedBy() != null) {
			empLeave.setAppliedBy(getLoginDetails(leave.getAppliedBy()));
		}*/
		return empLeave;
	}
	
	public static ERPCompanyLeavePolicy getLeavePolicy(ERPLeavePolicy policy) {
		if(policy.getCompany() == null || policy.getCategory() == null || policy.getMaxAllowed() == null) {
			return null;
		}
		ERPCompanyLeavePolicy companyPolicy = new ERPCompanyLeavePolicy();
		ERPCompanyDetails erpCompany = new ERPCompanyDetails();
		erpCompany.setId(policy.getCompany().getId());
		companyPolicy.setCompany(erpCompany);
		ERPLeaveType type = new ERPLeaveType();
		type.setId(policy.getCategory().getId());
		companyPolicy.setType(type);
		companyPolicy.setCreatedDate(new Date());
		companyPolicy.setMaxAllowed(policy.getMaxAllowed());
		companyPolicy.setFrequency(policy.getFrequency());
		return companyPolicy;
	}

	public static ERPCompanyLeavePolicy getLeavePolicy(ERPLeaveCategory policy, ERPCompany company) {
		if(company == null || policy.getId() == null || policy.getCount() == null) {
			return null;
		}
		ERPCompanyLeavePolicy companyPolicy = new ERPCompanyLeavePolicy();
		ERPCompanyDetails erpCompany = new ERPCompanyDetails();
		erpCompany.setId(company.getId());
		companyPolicy.setCompany(erpCompany);
		ERPLeaveType type = new ERPLeaveType();
		type.setId(policy.getId());
		companyPolicy.setType(type);
		companyPolicy.setCreatedDate(new Date());
		companyPolicy.setMaxAllowed(policy.getCount());
		companyPolicy.setFrequency(policy.getFrequency());
		return companyPolicy;
	}

	public static ERPSalaryStructure getSalaryStructure(ERPSalaryInfo salaryInfo) {
		if(salaryInfo == null) {
			return null;
		}
		ERPSalaryStructure structure = new ERPSalaryStructure();
		setSalaryStructure(salaryInfo, structure);
		return structure;
	}

	public static void setSalaryStructure(ERPSalaryInfo salaryInfo, ERPSalaryStructure structure) {
		if(StringUtils.equals(ERPConstants.AMOUNT_TYPE_PERCENTAGE, salaryInfo.getAmountType())) {
			structure.setPercentage(salaryInfo.getPercentage());
		} else {
			structure.setAmount(salaryInfo.getPercentage());
		}
		ERPCompanyDetails company = new ERPCompanyDetails();
		company.setId(salaryInfo.getCompany().getId());
		structure.setCompany(company);
		structure.setType(salaryInfo.getType());
		structure.setRule(salaryInfo.getRule());
		structure.setDescription(salaryInfo.getDescription());
	}
	
	public static ERPEmployeeSalaryStructure getEmployeeSalaryStructure(ERPSalaryInfo salaryInfo) {
		if(salaryInfo == null || salaryInfo.getAmount() == null || salaryInfo.getId() == null) {
			return null;
		}
		ERPEmployeeSalaryStructure salaryStructure = new ERPEmployeeSalaryStructure();
		if(salaryInfo.getAmount() != null && BigDecimal.ZERO.compareTo(salaryInfo.getAmount()) < 0) {
			salaryStructure.setAmount(salaryInfo.getAmount());
		} else {
			salaryInfo.setAmount(BigDecimal.ZERO);
		}
		/*ERPEmployeeDetails employee = new ERPEmployeeDetails();
		employee.setId(salaryInfo.getUser().getId());
		salaryStructure.setEmployee(employee);*/
		/*ERPSalaryStructure structure = new ERPSalaryStructure();
		structure.setId(salaryInfo.getId());
		salaryStructure.setSalaryStructure(structure);*/
		salaryStructure.setCreatedDate(new Date());
		return salaryStructure;
	}
	
	public static ERPProjectUsers getProjectUser(Session session, ERPProjects projects, ERPUser erpUser) {
		ERPProjectUsers projectUser = new ERPProjectUsers();
		ERPLoginDetails erpLogin = new ERPUserDAO().getLoginDetails(erpUser.getEmail(), session);
		if(erpLogin != null) {
			projectUser.setUser(erpLogin);
			projectUser.setProject(projects);
			projectUser.setStatus(ERPConstants.USER_STATUS_ACTIVE);
			projectUser.setCreatedDate(new Date());
		} else {
			return null;
		}
		return projectUser;
	}

	
	public static ERPProjectRecords getRecords(ERPUser user, ERPRecord currentRecord, ERPLoginDetails loginDetails) {
		ERPProjectRecords records = new ERPProjectRecords();
		records.setCreatedDate(new Date());
		records.setCreatedBy(loginDetails);
		ERPProjects project = new ERPProjects();
		project.setId(user.getCurrentProject().getId());
		records.setProject(project);
		records.setRecordDate(currentRecord.getRecordDate());
		records.setStatus(ERPConstants.USER_STATUS_ACTIVE);
		return records;
	}
	
	public static ERPProjectRecordValues getRecordValues(ERPLoginDetails loginDetails, ERPProjectRecords records, ERPField value) {
		ERPProjectRecordValues values = new ERPProjectRecordValues();
		values.setRecord(records);
		ERPProjectFields field = new ERPProjectFields();
		field.setId(value.getId());
		values.setField(field);
		values.setUpdatedBy(loginDetails);
		values.setUpdatedDate(new Date());
		values.setValue(value.getValue());
		return values;
	}

}
