package com.rns.web.erp.service.util;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPFinancial;
import com.rns.web.erp.service.bo.domain.ERPLeave;
import com.rns.web.erp.service.bo.domain.ERPLeaveCategory;
import com.rns.web.erp.service.bo.domain.ERPLeavePolicy;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.dao.domain.ERPCompanyDetails;
import com.rns.web.erp.service.dao.domain.ERPCompanyLeavePolicy;
import com.rns.web.erp.service.dao.domain.ERPEmployeeDetails;
import com.rns.web.erp.service.dao.domain.ERPEmployeeFinancials;
import com.rns.web.erp.service.dao.domain.ERPEmployeeLeave;
import com.rns.web.erp.service.dao.domain.ERPLeaveType;
import com.rns.web.erp.service.dao.domain.ERPLoginDetails;

public class ERPBusinessConverter {

	public static ERPLoginDetails getLoginDetails(ERPUser user) {
		if(user == null) {
			return null;
		}
		ERPLoginDetails login = new ERPLoginDetails();
		if(user.getId() != null) {
			login.setId(user.getId());
		}
		login.setEmail(user.getEmail());
		login.setCreatedDate(new Date());
		login.setName(user.getName());
		login.setPassword(user.getPassword());
		login.setPhone(user.getPhone());
		return login;
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
		ERPCompanyDetails cmp = new ERPCompanyDetails();
		cmp.setCreatedDate(new Date());
		cmp.setName(company.getName());
		cmp.setAddress(company.getAddress());
		cmp.setEmail(company.getEmail());
		cmp.setPhone(company.getPhone());
		return cmp;
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
		if(leave.getType() != null) {
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
		return companyPolicy;
	}
	
}
