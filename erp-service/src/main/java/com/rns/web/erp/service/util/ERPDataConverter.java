package com.rns.web.erp.service.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

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

public class ERPDataConverter {

	public static ERPUser getERPUser(ERPLoginDetails loginDetails) {
		if(loginDetails == null) {
			return null;
		}
		ERPUser erpUser = new ERPUser();
		erpUser.setId(loginDetails.getId());
		erpUser.setName(loginDetails.getName());
		erpUser.setEmail(loginDetails.getEmail());
		erpUser.setPhone(loginDetails.getPhone());
		erpUser.setStatus(loginDetails.getStatus());
		erpUser.setCompany(getCompany(loginDetails.getCompany()));
		return erpUser;
	}

	public static ERPCompany getCompany(ERPCompanyDetails companyDetails) {
		if(companyDetails == null) {
			return null;
		}
		ERPCompany company = new ERPCompany();
		company.setId(companyDetails.getId());
		company.setName(companyDetails.getName());
		company.setLeavePolicy(getLeavePolicy(companyDetails.getPolicy()));
		return company;
	}

	public static List<ERPLeavePolicy> getLeavePolicy(Set<ERPCompanyLeavePolicy> policy) {
		if(policy == null || CollectionUtils.isEmpty(policy)) {
			return null;
		}
		List<ERPLeavePolicy> policies = new ArrayList<ERPLeavePolicy>();
		for(ERPCompanyLeavePolicy p: policy) {
			ERPLeavePolicy leavePolicy = new ERPLeavePolicy();
			leavePolicy.setId(p.getId());
			leavePolicy.setMaxAllowed(p.getMaxAllowed());
			leavePolicy.setCategory(getLeaveCategory(p.getType()));
			policies.add(leavePolicy);
		}
		return policies;
	}

	public static ERPUser getEmployee(ERPEmployeeDetails emp) {
		if(emp == null) {
			return null;
		}
		ERPUser employee = new ERPUser();
		employee.setId(emp.getId());
		employee.setName(emp.getName());
		employee.setEmail(emp.getEmail());
		employee.setDesignation(emp.getDesignation());
		employee.setType(emp.getType());
		employee.setLocation(emp.getLocation());
		employee.setQualification(emp.getQualification());
		employee.setDepartment(emp.getDepartment());
		employee.setJoiningDate(emp.getJoiningDate());
		employee.setEmployeeStatus(emp.getStatus());
		employee.setFinancial(getFinancial(emp.getFinancials()));
		return employee;
	}
	
	private static ERPFinancial getFinancial(ERPEmployeeFinancials financials) {
		if(financials == null) {
			return null;
		}
		ERPFinancial financial = new ERPFinancial();
		financial.setId(financials.getId());
		financial.setAccountNumber(financials.getAccountNo());
		financial.setBankName(financials.getBankName());
		financial.setPan(financials.getPanNo());
		financial.setPfNumber(financials.getPfNo());
		return financial;
	}

	public static ERPLeaveCategory getLeaveCategory(ERPEmployeeLeave leave) {
		if(leave == null) {
			return null;
		}
		ERPLeaveCategory type = getLeaveCategory(leave.getType());
		return type;
	}

	public static ERPLeaveCategory getLeaveCategory(ERPLeaveType type) {
		if(type == null) {
			return null;
		}
		ERPLeaveCategory category = new ERPLeaveCategory();
		category.setId(type.getId());
		category.setName(type.getName());
		return category;
	}
	
	public static ERPLeave getLeave(ERPEmployeeLeave leave) {
		ERPLeave lv = new ERPLeave();
		lv.setId(leave.getId());
		lv.setFrom(leave.getFromDate());
		lv.setTo(leave.getToDate());
		lv.setType(ERPDataConverter.getLeaveCategory(leave));
		lv.setAppliedBy(ERPDataConverter.getERPUser(leave.getAppliedBy()));
		lv.setUser(ERPDataConverter.getEmployee(leave.getEmployee()));
		lv.setNoOfDays(leave.getNoOfDays());
		SimpleDateFormat sdf = new SimpleDateFormat(ERPConstants.DATE_FORMAT);
		lv.setFromString(sdf.format(leave.getFromDate()));
		lv.setToString(sdf.format(leave.getToDate()));
		return lv;
	}
	
}
