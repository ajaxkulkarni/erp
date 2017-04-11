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
import com.rns.web.erp.service.dao.domain.ERPSalaryStructure;

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
		setCompany(companyDetails, company);
		return company;
	}

	public static void setCompany(ERPCompanyDetails companyDetails, ERPCompany company) {
		company.setName(companyDetails.getName());
		company.setEmail(companyDetails.getEmail());
		company.setPhone(companyDetails.getPhone());
		company.setAddress(companyDetails.getAddress());
		company.setTan(companyDetails.getTanNumber());
		company.setLeavePolicy(getLeavePolicy(companyDetails.getPolicy()));
		ERPFinancial financial = new ERPFinancial();
		financial.setAccountNumber(companyDetails.getAccountNumber());
		financial.setBankName(companyDetails.getBankName());
		financial.setBranchName(companyDetails.getBranchName());
		financial.setIfscCode(companyDetails.getIfscCode());
		financial.setPan(companyDetails.getCompanyPan());
		company.setFinancial(financial);
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
			leavePolicy.setFrequency(p.getFrequency());
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
		employee.setRegId(emp.getRegId());
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
		employee.setExperiences(CommonUtils.getUserExperiences(emp.getExperiences()));
		employee.setQualifications(CommonUtils.getUserExperiences(emp.getQualifications()));
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
		financial.setSalary(financials.getSalary());
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
		lv.setWithoutPay(leave.getWithoutPay());
		ERPLeaveCategory leaveCategory = ERPDataConverter.getLeaveCategory(leave);
		if(leaveCategory == null && lv.getWithoutPay() > 0) {
			leaveCategory = new ERPLeaveCategory();
			leaveCategory.setName(ERPConstants.LEAVE_TYPE_LOP);
		}
		lv.setType(leaveCategory);
		lv.setAppliedBy(ERPDataConverter.getERPUser(leave.getAppliedBy()));
		lv.setUser(ERPDataConverter.getEmployee(leave.getEmployee()));
		lv.setNoOfDays(leave.getNoOfDays());
		SimpleDateFormat sdf = new SimpleDateFormat(ERPConstants.DATE_FORMAT);
		lv.setFromString(sdf.format(leave.getFromDate()));
		lv.setToString(sdf.format(leave.getToDate()));
		lv.setReason(leave.getComments());
		return lv;
	}

	public static ERPSalaryInfo getSalaryInfo(ERPSalaryStructure structure) {
		if(structure == null) {
			return null;
		}
		ERPSalaryInfo salaryInfo = new ERPSalaryInfo();
		salaryInfo.setId(structure.getId());
		salaryInfo.setAmount(structure.getAmount());
		salaryInfo.setPercentage(structure.getPercentage());
		salaryInfo.setRule(structure.getRule());
		salaryInfo.setType(structure.getType());
		salaryInfo.setDescription(structure.getDescription());
		salaryInfo.setAmountType(ERPConstants.AMOUNT_TYPE_AMOUNT);
		if(salaryInfo.getPercentage() != null) {
			salaryInfo.setAmountType(ERPConstants.AMOUNT_TYPE_PERCENTAGE);
		} else {
			salaryInfo.setPercentage(salaryInfo.getAmount());
		}
		return salaryInfo;
	}

	public static ERPSalaryInfo getSalaryInfo(ERPEmployeeSalaryStructure structure) {
		if(structure != null && structure.getSalaryStructure() != null && structure.getEmployee() != null) {
			ERPSalaryInfo salary = getSalaryInfo(structure.getSalaryStructure());
			salary.setAmount(structure.getAmount());
			return salary;
		}
		return null;
	}
	
}
