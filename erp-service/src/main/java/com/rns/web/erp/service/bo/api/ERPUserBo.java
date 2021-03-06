package com.rns.web.erp.service.bo.api;

import java.io.InputStream;
import java.util.List;

import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPLeave;
import com.rns.web.erp.service.bo.domain.ERPLeaveCategory;
import com.rns.web.erp.service.bo.domain.ERPSalaryInfo;
import com.rns.web.erp.service.bo.domain.ERPUser;

public interface ERPUserBo {

	String loginUser(ERPUser user);
	String subscribeUser(ERPUser user);
	ERPUser populateUser(ERPUser user);
	String addEmployee(ERPUser user);
	String addCompany(ERPCompany user);
	String applyLeave(ERPLeave user);
	String updateLeave(ERPLeave user);
	List<ERPUser> getAllEmployees(ERPUser erpUser);
	List<ERPLeaveCategory> getAllLeaveTypes(ERPCompany company, String requestType);
	List<ERPUser> getAllEmployeeLeaveData(ERPUser company);
	ERPUser getEmployeeLeaveData(ERPUser user);
	String addLeavePolicy(ERPCompany company);
	String changePassword(ERPUser erpUser);
	String addSalaryStructure(ERPCompany salaryInfo);
	List<ERPSalaryInfo> getSalaryInfo(ERPCompany company);
	String updateSalary(ERPUser employee);
	List<ERPUser> getAllEmployeeSalarySlips(ERPUser erpUser);
	InputStream downloadSalarySlip(ERPUser employee);
	String updateEmployeeSalarySlips(ERPCompany user);
	String forgotPassword(ERPUser user);
	String updateEmployeeLeaveBalance(ERPUser user);
	InputStream getEmployeeDocument(ERPUser user);
	String updateUserFcmToken(ERPUser user);
}
