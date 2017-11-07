package com.rns.web.erp.service.bo.api;

import java.util.List;

import com.rns.web.erp.service.bo.domain.ERPLeaveCategory;
import com.rns.web.erp.service.bo.domain.ERPUser;

public interface ERPAdminBo {

	String activateUser(ERPUser user);
	String addLeaveType(ERPLeaveCategory type);
	List<ERPUser> getUsersByStatus(String status);
	void saveSalarySlips();
	
	void followUpRecords();
	
}
