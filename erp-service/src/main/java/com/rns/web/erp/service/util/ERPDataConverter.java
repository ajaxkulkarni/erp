package com.rns.web.erp.service.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.rns.web.erp.service.bo.domain.ERPAccessRights;
import com.rns.web.erp.service.bo.domain.ERPComment;
import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPField;
import com.rns.web.erp.service.bo.domain.ERPFile;
import com.rns.web.erp.service.bo.domain.ERPFinancial;
import com.rns.web.erp.service.bo.domain.ERPLeave;
import com.rns.web.erp.service.bo.domain.ERPLeaveCategory;
import com.rns.web.erp.service.bo.domain.ERPLeavePolicy;
import com.rns.web.erp.service.bo.domain.ERPLog;
import com.rns.web.erp.service.bo.domain.ERPProject;
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
import com.rns.web.erp.service.dao.domain.ERPProjectComments;
import com.rns.web.erp.service.dao.domain.ERPProjectFields;
import com.rns.web.erp.service.dao.domain.ERPProjectFiles;
import com.rns.web.erp.service.dao.domain.ERPProjectLog;
import com.rns.web.erp.service.dao.domain.ERPProjectRecordValues;
import com.rns.web.erp.service.dao.domain.ERPProjectRecords;
import com.rns.web.erp.service.dao.domain.ERPProjectUsers;
import com.rns.web.erp.service.dao.domain.ERPProjects;
import com.rns.web.erp.service.dao.domain.ERPSalaryStructure;
import com.rns.web.erp.service.dao.impl.ERPProjectDAO;

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
		erpUser.setLoginType(loginDetails.getType());
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
		setEmployee(emp, employee);
		return employee;
	}

	public static void setEmployee(ERPEmployeeDetails emp, ERPUser employee) {
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
	
	public static ERPProject getProject(ERPProjectUsers userProject) {
		ERPProjects projects = userProject.getProject();
		ERPProject project = getProjectBasic(projects);
		ERPAccessRights rights = getAccessRights(userProject);
		project.setAccessRights(rights);
		return project;
	}

	public static ERPAccessRights getAccessRights(ERPProjectUsers userProject) {
		ERPAccessRights rights = new ERPAccessRights();
		if(StringUtils.contains(userProject.getAccessRights(), ERPConstants.ACCESS_RIGHT_RECORD)) {
			rights.setRecordAccess(true);
		}
		if(StringUtils.contains(userProject.getAccessRights(), ERPConstants.ACCESS_RIGHT_FILE)) {
			rights.setFileAccess(true);
		}
		if(StringUtils.contains(userProject.getAccessRights(), ERPConstants.ACCESS_RIGHT_COMMENT)) {
			rights.setCommentAccess(true);
		}
		if(StringUtils.contains(userProject.getAccessRights(), ERPConstants.ACCESS_RIGHT_PROJECT)) {
			rights.setProjectAccess(true);
		}
		return rights;
	}

	public static ERPProject getProjectBasic(ERPProjects projects) {
		ERPProject project = new ERPProject();
		project.setId(projects.getId());
		project.setTitle(projects.getTitle());
		project.setDescription(projects.getDescription());
		project.setCreatedBy(ERPDataConverter.getERPUser(projects.getCreatedBy()));
		project.setCreatedDate(projects.getCreatedDate());
		return project;
	}
	
	public static ERPRecord getRecord(Session session, List<ERPProjectFields> projectFields, ERPProjectRecords rec) {
		ERPRecord record = new ERPRecord();
		record.setId(rec.getId());
		record.setCreatedDate(rec.getCreatedDate());
		record.setCreatedUser(ERPDataConverter.getERPUser(rec.getCreatedBy()));
		record.setRecordDate(rec.getRecordDate());
		record.setRecordDateString(CommonUtils.getDate(rec.getRecordDate()));
		record.setAssignedUser(ERPDataConverter.getERPUser(rec.getAssignedTo()));
		for(ERPProjectFields fields: projectFields) {
			ERPProjectRecordValues values = new ERPProjectDAO().getRecordValueByField(fields.getId(),rec.getId(), session);
			ERPField field = new ERPField();
			field.setName(fields.getName());
			field.setType(fields.getType());
			field.setId(fields.getId());
			if(values != null) {
				field.setRecordId(values.getId());
				field.setValue(values.getValue());
			}
			if(StringUtils.isNotBlank(fields.getValues()) && StringUtils.equals(ERPConstants.FIELD_TYPE_MULTIPLE, fields.getType())) {
				field.setPossibleValues(Arrays.asList(StringUtils.split(fields.getValues(), ",")));
			}
			if(StringUtils.equals(ERPConstants.FIELD_TYPE_TITLE, fields.getType())) {
				record.setTitleField(field);
			} else {
				record.getValues().add(field);
			}
		}
		return record;
	}
	
	
	public static ERPFile getFile(ERPProjectFiles projectFile) {
		ERPFile file = new ERPFile();
		file.setId(projectFile.getId());
		file.setFileName(projectFile.getFileName());
		file.setCreatedDate(projectFile.getCreatedDate());
		file.setCreatedBy(ERPDataConverter.getERPUser(projectFile.getCreatedBy()));
		return file;
	}

	public static ERPComment getComment(ERPProjectComments cm) {
		if(cm == null) {
			return null;
		}
		ERPComment comment = new ERPComment();
		comment.setId(cm.getId());
		comment.setComment(cm.getComment());
		comment.setCommentedBy(getERPUser(cm.getCreatedBy()));
		comment.setDate(cm.getCreatedDate());
		comment.setDateString(CommonUtils.getDate(comment.getDate()));
		return comment;
	}

	public static ERPLog getLog(ERPProjectLog log) {
		if(log == null) {
			return null;
		}
		ERPLog erpLog = new ERPLog();
		erpLog.setId(log.getId());
		erpLog.setLog(log.getLog());
		erpLog.setLogDate(CommonUtils.getDate(log.getCreatedDate()));
		erpLog.setUser(getERPUser(log.getCreatedBy()));
		return erpLog;
	}
	
}
