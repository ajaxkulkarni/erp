package com.rns.web.erp.service.bo.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.rns.web.erp.service.bo.api.ERPSalaryInfo;
import com.rns.web.erp.service.bo.api.ERPUserBo;
import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPFinancial;
import com.rns.web.erp.service.bo.domain.ERPLeave;
import com.rns.web.erp.service.bo.domain.ERPLeaveCategory;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.dao.domain.ERPCompanyDetails;
import com.rns.web.erp.service.dao.domain.ERPCompanyLeavePolicy;
import com.rns.web.erp.service.dao.domain.ERPEmployeeDetails;
import com.rns.web.erp.service.dao.domain.ERPEmployeeFinancials;
import com.rns.web.erp.service.dao.domain.ERPEmployeeLeave;
import com.rns.web.erp.service.dao.domain.ERPLeaveType;
import com.rns.web.erp.service.dao.domain.ERPLoginDetails;
import com.rns.web.erp.service.dao.domain.ERPSalaryStructure;
import com.rns.web.erp.service.dao.impl.ERPUserDAO;
import com.rns.web.erp.service.util.CommonUtils;
import com.rns.web.erp.service.util.ERPBusinessConverter;
import com.rns.web.erp.service.util.ERPConstants;
import com.rns.web.erp.service.util.ERPDataConverter;
import com.rns.web.erp.service.util.ERPMailUtil;
import com.rns.web.erp.service.util.ERPReportUtil;
import com.rns.web.erp.service.util.LoggingUtil;

public class ERPUserBoImpl implements ERPUserBo, ERPConstants {
	
	private SessionFactory sessionFactory;
	private ThreadPoolTaskExecutor executor;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public ThreadPoolTaskExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(ThreadPoolTaskExecutor executor) {
		this.executor = executor;
	}


	public String loginUser(ERPUser user) {
		if(user == null || StringUtils.isEmpty(user.getEmail())) {
			return ERROR_INVALID_USER_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			ERPLoginDetails loginDetails = dao.getLoginDetails(user.getEmail(), session);
			if(loginDetails == null || !StringUtils.equals(loginDetails.getPassword(), user.getPassword())) {
				result = ERROR_INVALID_LOGIN_DETAILS;
			} else if (StringUtils.equals(USER_STATUS_CREATED, loginDetails.getStatus())) {
				result = ERROR_INACTIVE_PROFILE;
			} 
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}


	public String subscribeUser(ERPUser user) {
		if(user == null || StringUtils.isEmpty(user.getEmail())) {
			return ERROR_INVALID_USER_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPLoginDetails existingLogin = new ERPUserDAO().getLoginDetails(user.getEmail(), session);
			if(existingLogin == null) {
				ERPLoginDetails login = ERPBusinessConverter.getLoginDetails(user);
				login.setStatus(USER_STATUS_CREATED);
				session.persist(login);
				tx.commit();
				ERPMailUtil mailUtil = new ERPMailUtil(MAIL_TYPE_SUBSCRIPTION);
				mailUtil.setUser(user);
				executor.execute(mailUtil);
				ERPMailUtil adminMail = new ERPMailUtil(MAIL_TYPE_SUBSCRIPTION_ADMIN);
				adminMail.setUser(user);
				executor.execute(adminMail);
			} else {
				result = ERROR_EMAIL_EXISTS;
			}
			
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}


	public ERPUser populateUser(ERPUser user) {
		if(user == null || StringUtils.isEmpty(user.getEmail())) {
			return null;
		}
		ERPUser erpUser = null;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			ERPLoginDetails loginDetails = dao.getLoginDetails(user.getEmail(), session);
			if(loginDetails != null) {
				erpUser = ERPDataConverter.getERPUser(loginDetails);
			} 
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return erpUser;
	}


	public String addEmployee(ERPUser user) {
		if(user == null || StringUtils.isEmpty(user.getEmail())) {
			return ERROR_INVALID_USER_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPEmployeeDetails employee = null;
			if(user.getId() == null) {
				employee = ERPBusinessConverter.getEmployeeDetails(user);
				session.persist(employee);
			} else {
				employee = new ERPUserDAO().getEmployeeById(user.getId(), session);
				if(employee != null) {
					if(user.getFinancial() != null) {
						employee.getFinancials();
					}
					ERPBusinessConverter.setEmployeeDetails(user, employee);
				}
				
			}
			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	public String addCompany(ERPCompany company) {
		if(company == null || StringUtils.isEmpty(company.getName()) || company.getCreatedBy() == null) {
			return ERROR_INVALID_COMPANY_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPLoginDetails login = new ERPUserDAO().getLoginDetails(company.getCreatedBy().getEmail(), session);
			ERPCompanyDetails cmp = ERPBusinessConverter.getCompanyDetails(company);
			cmp.setCreatedBy(login);
			session.persist(cmp);
			login.setCompany(cmp);
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	public String applyLeave(ERPLeave leave) {
		if(leave == null || leave.getUser()  == null) {
			return ERROR_INVALID_LEAVE_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPUserDAO erpUserDAO = new ERPUserDAO();
			List<ERPEmployeeLeave> leaves = erpUserDAO.getEmployeeLeaves(session, leave.getUser().getId(), leave.getFrom(), leave.getTo());
			if(CollectionUtils.isEmpty(leaves)) {
				ERPEmployeeLeave lv = ERPBusinessConverter.getLeaveDetails(leave);
				lv.setStatus(LEAVE_STATUS_APPLIED);
				lv.setAppliedDate(new Date());
				lv.setAppliedBy(erpUserDAO.getLoginDetails(leave.getAppliedBy().getEmail(), session));
				session.persist(lv);
				tx.commit();
			} else {
				result = ERROR_LEAVES_OVERLAP;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	public String updateLeave(ERPLeave leave) {
		if(leave == null || leave.getId()  == null) {
			return ERROR_INVALID_LEAVE_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			ERPEmployeeLeave lv = dao.getLeaveDetails(leave.getId(), session);
			if(lv != null) {
				Transaction tx = session.beginTransaction();
				//TODO 
				lv.setStatus(LEAVE_STATUS_CANCELLED);
				lv.setUpdatedDate(new Date());
				session.persist(lv);
				tx.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	public List<ERPUser> getAllEmployees(ERPCompany company) {
		if(company == null || company.getId() == null) {
			return null;
		}
		List<ERPUser> employees = new ArrayList<ERPUser>();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			List<ERPEmployeeDetails> emps = dao.getCompanyEmployees(company.getId(), session);
			extractDates(company);
			if(CollectionUtils.isNotEmpty(emps)) {
				List<ERPCompanyLeavePolicy> leaveTypes = dao.getCompanyLeaveTypes(session, company.getId());
				for(ERPEmployeeDetails emp: emps) {
					ERPUser user = ERPDataConverter.getEmployee(emp);
					if(user != null) {
						setEmployeeLeaveCount(session, leaveTypes, user, company);
						employees.add(user);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return employees;
	}

	private void extractDates(ERPCompany company) {
		if(company.getFilter() == null) {
			return;
		}
		if(company.getFilter().getYear() == null || company.getFilter().getMonth() == null) {
			company.getFilter().setFromDate(CommonUtils.getFirstDate(Calendar.getInstance().get(Calendar.YEAR), 0));
			company.getFilter().setToDate(CommonUtils.getLastDate(Calendar.getInstance().get(Calendar.YEAR), 11));
		} else {
			company.getFilter().setFromDate(CommonUtils.getFirstDate(company.getFilter().getYear(), company.getFilter().getMonth()));
			company.getFilter().setToDate(CommonUtils.getLastDate(company.getFilter().getYear(), company.getFilter().getMonth()));
		}
	}
	
	
	public List<ERPLeaveCategory> getAllLeaveTypes(ERPCompany company, String requestType) {
		if(company == null || company.getId() == null) {
			return null;
		}
		List<ERPLeaveCategory> leaveTypes = new ArrayList<ERPLeaveCategory>();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			List<ERPLeaveType> types = dao.getAllLeaveTypes(session);
			for(ERPLeaveType type: types) {
				ERPLeaveCategory category = new ERPLeaveCategory();
				if(!StringUtils.equals("ALL", requestType)) {
					ERPCompanyLeavePolicy policy = dao.getCompanyLeavePolicy(type.getId(), company.getId(), session);
					if(policy == null) {
						continue;
					}
					category.setCount(policy.getMaxAllowed());
				}
				category.setId(type.getId());
				category.setName(type.getName());
				leaveTypes.add(category);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return leaveTypes;
	}
	
	public List<ERPUser> getAllEmployeeLeaveData(ERPCompany company) {
		if(company == null || company.getId() == null) {
			return null;
		}
		List<ERPUser> employees = new ArrayList<ERPUser>();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			List<ERPEmployeeDetails> emps = dao.getCompanyEmployees(company.getId(), session);
			if(CollectionUtils.isNotEmpty(emps)) {
				extractDates(company);
				List<ERPCompanyLeavePolicy> leaveTypes = dao.getCompanyLeaveTypes(session, company.getId());
				for(ERPEmployeeDetails emp: emps) {
					ERPUser user = ERPDataConverter.getEmployee(emp);
					if(user != null) {
						setEmployeeLeaveCount(session, leaveTypes, user, company);
						setEmployeeFinancial(session, user, company);
						employees.add(user);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return employees;
	}

	private void setEmployeeFinancial(Session session, ERPUser user, ERPCompany company) {
		ERPFinancial financial = user.getFinancial();
		if(financial == null || financial.getSalary() == null) {
			return;
		}
		List<ERPSalaryStructure> salaryStructures = new ERPUserDAO().getCompanySalaryStructure(company.getId(), session);
		if(CollectionUtils.isEmpty(salaryStructures)) {
			return;
		}
		for(ERPSalaryStructure structure: salaryStructures) {
			if(StringUtils.equalsIgnoreCase("Basic",structure.getRule())) {
				ERPSalaryInfo basic = ERPDataConverter.getSalaryInfo(structure);
				if(basic.getAmount() == null) {
					basic.setAmount(financial.getSalary().multiply(basic.getPercentage().divide(new BigDecimal(100))));
				}
				financial.setBasic(basic);
			}
		}
		if(financial.getBasic() == null || financial.getBasic().getAmount() == null) {
			return;
		}
		for(ERPSalaryStructure structure: salaryStructures) {
			if(StringUtils.equalsIgnoreCase("Basic",structure.getRule())) {
				continue;
			}
			ERPSalaryInfo salaryInfo = ERPDataConverter.getSalaryInfo(structure);
			if(salaryInfo.getPercentage() != null && salaryInfo.getPercentage().compareTo(BigDecimal.ZERO) > 0) {
				salaryInfo.setAmount(financial.getBasic().getAmount().multiply(salaryInfo.getPercentage().divide(new BigDecimal(100))));
			}
			if(StringUtils.equals("add", salaryInfo.getType())) {
				financial.getBenefits().add(salaryInfo);
				financial.setTotalBenefits(salaryInfo.getAmount().add(financial.getTotalBenefits()));
			} else {
				financial.getDeductions().add(salaryInfo);
				financial.setTotalDeductions(salaryInfo.getAmount().add(financial.getTotalDeductions()));
			}
		}
		if(user.getWithoutPayLeaves() != null && user.getWithoutPayLeaves() > 0) {
			ERPSalaryInfo salaryInfo = new ERPSalaryInfo();
			salaryInfo.setRule("Withoutpay Leaves");
			salaryInfo.setAmount(user.getFinancial().getSalary().divide(new BigDecimal(user.getWithoutPayLeaves())));
			financial.getDeductions().add(salaryInfo);
			financial.setTotalDeductions(salaryInfo.getAmount().add(financial.getTotalDeductions()));
		}
		ERPSalaryInfo salaryInfo = new ERPSalaryInfo();
		salaryInfo.setAmount(financial.getSalary().subtract(financial.getTotalBenefits().add(financial.getBasic().getAmount())));
		salaryInfo.setRule("Other allowances");
		financial.getBenefits().add(salaryInfo);
		financial.setTotalBenefits(financial.getTotalBenefits().add(salaryInfo.getAmount()));
		financial.setAmountPayable(financial.getSalary().subtract(financial.getTotalDeductions()));
	}

	private void setEmployeeLeaveCount(Session session, List<ERPCompanyLeavePolicy> leaveTypes, ERPUser user, ERPCompany company) {
		if(CollectionUtils.isEmpty(leaveTypes)) {
			return;
		}
		Date date1 = null ,date2 = null;
		if(company.getFilter() != null) {
			date1 = company.getFilter().getFromDate();
			date2 = company.getFilter().getToDate();
		}
		List<ERPLeaveCategory> leaveCount = new ArrayList<ERPLeaveCategory>();
		Integer total = 0;
		for(ERPCompanyLeavePolicy type: leaveTypes) {
			ERPUserDAO erpUserDAO = new ERPUserDAO();
			Integer typeCount = erpUserDAO.getEmployeeLeaveCount(session, user.getId(), type.getType().getId(), date1, date2);
			ERPLeaveCategory leaveCategory = ERPDataConverter.getLeaveCategory(type.getType());
			leaveCategory.setCount(typeCount);
			Integer typeLeaves = typeCount;
			if(CommonUtils.getCalendarValue(date1, Calendar.MONTH) == CommonUtils.getCalendarValue(date2, Calendar.MONTH)) {
				typeLeaves = erpUserDAO.getEmployeeLeaveCount(session, user.getId(), type.getType().getId(), CommonUtils.getFirstDate(CommonUtils.getCalendarValue(date1, Calendar.YEAR), 0), CommonUtils.getLastDate(CommonUtils.getCalendarValue(date1,Calendar.YEAR), 11));
			}
			leaveCategory.setAvailable(type.getMaxAllowed() - typeLeaves);
			if(leaveCategory.getAvailable() < 0) {
				if(Math.abs(leaveCategory.getAvailable()) > typeCount) {
					user.setWithoutPayLeaves(user.getWithoutPayLeaves() + typeCount);
				} else {
					user.setWithoutPayLeaves(user.getWithoutPayLeaves() + Math.abs(leaveCategory.getAvailable()));
				}
			}
			total = total + typeCount;
			leaveCount.add(leaveCategory);
		}
		user.setTotalLeaves(total);
		user.setLeaveCount(leaveCount);
	}

	public ERPUser getEmployeeLeaveData(ERPUser user) {
		if(user == null || user.getId() == null) {
			return null;
		}
		List<ERPLeave> employeeLeaves = new ArrayList<ERPLeave>();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			List<ERPEmployeeLeave> leaves = dao.getEmployeeLeaves(session, user.getId());
			if(CollectionUtils.isNotEmpty(leaves)) {
				for(ERPEmployeeLeave lv: leaves) {
					ERPLeave leave = ERPDataConverter.getLeave(lv);
					employeeLeaves.add(leave);
				}
			}
			user.setLeaves(employeeLeaves);
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return user;
	}

	public String addLeavePolicy(ERPCompany company) {
		if(company == null || company.getId() == null || CollectionUtils.isEmpty(company.getLeaveTypes())) {
			return ERROR_INVALID_COMPANY_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			for(ERPLeaveCategory policy: company.getLeaveTypes()) {
				ERPCompanyLeavePolicy leavePolicy = new ERPUserDAO().getCompanyLeavePolicy(policy.getId(), company.getId(), session);
				if(leavePolicy == null) {
					leavePolicy = ERPBusinessConverter.getLeavePolicy(policy, company);
				} else {
					leavePolicy.setMaxAllowed(policy.getCount());
				}
				if(leavePolicy != null) {
					session.persist(leavePolicy);
				}
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}
	
	public String changePassword(ERPUser erpUser) {
		if(erpUser == null || StringUtils.isEmpty(erpUser.getEmail())) {
			return ERROR_INVALID_LOGIN_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			ERPLoginDetails user = dao.getLoginDetails(erpUser.getEmail(), session);
			if(user != null) {
				if(StringUtils.equals(erpUser.getPassword(), user.getPassword())) {
					Transaction tx = session.beginTransaction();
					user.setPassword(erpUser.getNewPassword());
					user.setStatus(USER_STATUS_ACTIVE);
					tx.commit();
					ERPMailUtil mail = new ERPMailUtil(MAIL_TYPE_PASSWORD_CHANGED);
					erpUser.setName(user.getName());
					mail.setUser(erpUser);
					executor.execute(mail);
				} else {
					result = ERROR_INCORRECT_OLD_PASSWORD;
				}
			} else {
				result = ERROR_INVALID_LOGIN_DETAILS;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}
	
	public String addSalaryStructure(ERPCompany company) {
		if(company == null || company.getId() == null || CollectionUtils.isEmpty(company.getSalaryInfo())) {
			return ERROR_INVALID_COMPANY_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPUserDAO erpUserDAO = new ERPUserDAO();
			erpUserDAO.removeAllSalaryStructure(company.getId(), session);
			company.getBasic().setRule("Basic");
			company.getBasic().setCompany(company);
			session.persist(ERPBusinessConverter.getSalaryStructure(company.getBasic()));
			for(ERPSalaryInfo salaryInfo: company.getSalaryInfo()) {
				salaryInfo.setCompany(company);
				ERPSalaryStructure structure = ERPBusinessConverter.getSalaryStructure(salaryInfo);
				session.persist(structure);
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	
	public List<ERPSalaryInfo> getSalaryInfo(ERPCompany company) {
		if(company == null || company.getId() == null) {
			return null;
		}
		List<ERPSalaryInfo> salaryInfos = new ArrayList<ERPSalaryInfo>();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			List<ERPSalaryStructure> structures = dao.getCompanySalaryStructure(company.getId(),session);
			for(ERPSalaryStructure structure: structures) {
				ERPSalaryInfo salaryInfo = ERPDataConverter.getSalaryInfo(structure);
				if(salaryInfo == null) {
					continue;
				}
				if("Basic".equalsIgnoreCase(salaryInfo.getRule())) {
					company.setBasic(salaryInfo);
					continue;
				}
				salaryInfos.add(salaryInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return salaryInfos;
	}
	
	public String updateSalary(ERPUser user) {
		if(user == null || StringUtils.isEmpty(user.getEmail())) {
			return ERROR_INVALID_USER_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPEmployeeDetails employee = null;
			if(user.getId() != null && user.getFinancial() != null) {
				employee = new ERPUserDAO().getEmployeeById(user.getId(), session);
				ERPEmployeeFinancials financials = employee.getFinancials();
				if(employee != null && financials != null) {
					financials.setSalary(user.getFinancial().getSalary());
				}
			}
			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}
	
	public List<ERPUser> getAllEmployeeSalarySlips(ERPCompany company) {
		if(company == null || company.getId() == null) {
			return null;
		}
		List<ERPUser> employees = new ArrayList<ERPUser>();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			List<ERPEmployeeDetails> emps = dao.getCompanyEmployees(company.getId(), session);
			if(CollectionUtils.isNotEmpty(emps)) {
				extractDates(company);
				List<ERPCompanyLeavePolicy> leaveTypes = dao.getCompanyLeaveTypes(session, company.getId());
				for(ERPEmployeeDetails emp: emps) {
					ERPUser user = ERPDataConverter.getEmployee(emp);
					if(user != null) {
						setEmployeeLeaveCount(session, leaveTypes, user, company);
						setEmployeeFinancial(session, user, company);
						employees.add(user);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return employees;
	}
	
	public InputStream downloadSalarySlip(ERPUser employee) {
		if(employee == null || employee.getId() == null || employee.getCompany() == null) {
			return null;
		}
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			ERPEmployeeDetails emp = dao.getEmployeeById(employee.getId(), session);
			List<ERPCompanyLeavePolicy> leaveTypes = dao.getCompanyLeaveTypes(session, employee.getCompany().getId());
			ERPUser user = ERPDataConverter.getEmployee(emp);
			ERPCompanyDetails company = emp.getCompany();
			if (user != null && company.getId().intValue() == employee.getCompany().getId().intValue()) {
				extractDates(employee.getCompany());
				setEmployeeLeaveCount(session, leaveTypes, user, employee.getCompany());
				setEmployeeFinancial(session, user, employee.getCompany());
				user.setCompany(employee.getCompany());
			}
			return ERPReportUtil.getSalarySlip(user);
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return null;
	}
	
}
