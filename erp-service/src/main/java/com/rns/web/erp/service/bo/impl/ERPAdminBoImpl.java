package com.rns.web.erp.service.bo.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.rns.web.erp.service.bo.api.ERPAdminBo;
import com.rns.web.erp.service.bo.domain.ERPFilter;
import com.rns.web.erp.service.bo.domain.ERPLeaveCategory;
import com.rns.web.erp.service.bo.domain.ERPRecord;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.dao.domain.ERPCompanyLeavePolicy;
import com.rns.web.erp.service.dao.domain.ERPEmployeeDetails;
import com.rns.web.erp.service.dao.domain.ERPEmployeeLeaveBalance;
import com.rns.web.erp.service.dao.domain.ERPLeaveType;
import com.rns.web.erp.service.dao.domain.ERPLoginDetails;
import com.rns.web.erp.service.dao.domain.ERPProjectFields;
import com.rns.web.erp.service.dao.domain.ERPProjectRecords;
import com.rns.web.erp.service.dao.impl.ERPAdminDAO;
import com.rns.web.erp.service.dao.impl.ERPProjectDAO;
import com.rns.web.erp.service.dao.impl.ERPUserDAO;
import com.rns.web.erp.service.util.CommonUtils;
import com.rns.web.erp.service.util.ERPConstants;
import com.rns.web.erp.service.util.ERPDataConverter;
import com.rns.web.erp.service.util.ERPMailUtil;
import com.rns.web.erp.service.util.ERPUtils;
import com.rns.web.erp.service.util.LoggingUtil;

public class ERPAdminBoImpl implements ERPAdminBo, ERPConstants {
	
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

	public String activateUser(ERPUser user) {
		if(user == null || StringUtils.isEmpty(user.getEmail())) {
			return ERROR_INVALID_USER_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPUserDAO dao = new ERPUserDAO();
			ERPLoginDetails loginDetails = dao.getLoginDetails(user.getEmail(), session);
			if(loginDetails == null) {
				result = ERROR_INVALID_LOGIN_DETAILS;
			} else {
				loginDetails.setStatus(USER_STATUS_PASSWORD_SENT);
				loginDetails.setPassword(CommonUtils.generatePassword(user));
			}
			tx.commit();
			ERPMailUtil mailUtil = new ERPMailUtil(MAIL_TYPE_PASSWORD_SENT);
			user.setName(loginDetails.getName());
			user.setPassword(loginDetails.getPassword());
			mailUtil.setUser(user);
			executor.execute(mailUtil);
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}
	
	public String addLeaveType(ERPLeaveCategory cat) {
		if(cat == null) {
			return ERROR_INVALID_LEAVE_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPLeaveType type = new ERPLeaveType();
			type.setName(cat.getName());
			session.persist(type);
			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	public List<ERPUser> getUsersByStatus(String status) {
		List<ERPUser> users = new ArrayList<ERPUser>();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPAdminDAO dao = new ERPAdminDAO();
			List<ERPLoginDetails> loginUsers = dao.getAllUsers(status, session);
			for(ERPLoginDetails login: loginUsers) {
				users.add(ERPDataConverter.getERPUser(login));
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return users;
	}
	
	@Scheduled(cron = "0 14 2 * * *")
	public void saveSalarySlips() {
		LoggingUtil.logMessage("########### START OF SAVING SALARY SLIPS PROCESS ##############");
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			List<ERPEmployeeDetails> employees =  dao.getAllActiveEmployees(session);
			ERPFilter filter = new ERPFilter();
			filter.setYear(CommonUtils.getCalendarValue(new Date(), Calendar.YEAR));
			filter.setMonth(CommonUtils.getCalendarValue(new Date(), Calendar.MONTH));
			ERPUtils.saveEmployeeSalarySlips(filter, session, dao, employees);
			LoggingUtil.logMessage("########### END OF SAVING SALARY SLIPS PROCESS ##############");
		} catch (Exception e) {
			LoggingUtil.logError("########### ERROR OCCURRED IN SAVING SALARY SLIPS ##############");
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
	}

	@Scheduled(cron = "0 03 22 1 * * ")
	public void updatedEmployeesBalances() {
		LoggingUtil.logMessage("########### START OF LEAVE BALANCE UPDATE PROCESS ##############");
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			Date date = new Date();
			List<ERPEmployeeLeaveBalance> balances =  dao.getAllEmployeeLeaveBalances(session);
			if(CollectionUtils.isNotEmpty(balances)) {
				Transaction tx = session.beginTransaction();
				for(ERPEmployeeLeaveBalance balance:balances) {
					ERPCompanyLeavePolicy policy = dao.getCompanyLeavePolicy(balance.getType().getId(), balance.getEmployee().getCompany().getId(), session);
					if(policy != null) {
						Integer month = CommonUtils.getCalendarValue(date, Calendar.MONTH);
						Integer savedMonth = CommonUtils.getCalendarValue(balance.getLastScheduled(), Calendar.MONTH);
						Integer savedYear = CommonUtils.getCalendarValue(balance.getLastScheduled(), Calendar.YEAR);
						if(savedMonth != null && month.intValue() == savedMonth.intValue() && savedYear != null && savedYear.intValue() == CommonUtils.getCalendarValue(date, Calendar.YEAR).intValue()) {
							continue;
						}
						if(policy.getFrequency() == null || StringUtils.equals("Yearly",policy.getFrequency())) {
							//First month check for yearly
							if(month == 0) {
								balance.setBalance(balance.getBalance().add(new BigDecimal(policy.getMaxAllowed())));
								balance.setLastScheduled(date);
							}
						} else {
							balance.setBalance(balance.getBalance().add(new BigDecimal(policy.getMaxAllowed())));
							balance.setLastScheduled(date);
						}
					}
				}
				tx.commit();
			}
			LoggingUtil.logMessage("########### END OF LEAVE BALANCE UPDATE PROCESS ##############");
		} catch (Exception e) {
			LoggingUtil.logError("########### ERROR OCCURRED IN LEAVE BALANCE UPDATE ##############");
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
	}

	@Scheduled(cron = "0 30 9 * * *")
	public void followUpRecords() {
		LoggingUtil.logMessage("########### START OF FOLLOW UP PROCESS ##############");
		Session session = null;
		Map<ERPUser, List<ERPRecord>> mailRecords = new HashMap<ERPUser, List<ERPRecord>>();
		try {
			session = this.sessionFactory.openSession();
			List<ERPProjectRecords> followUpRecords = new ERPProjectDAO().getFollowUpRecords(session, new Date());
			if(CollectionUtils.isNotEmpty(followUpRecords)) {
				for(ERPProjectRecords records: followUpRecords) {
					List<ERPProjectFields> projectFields = new ERPProjectDAO().getProjectFields(records.getProject().getId(), session);
					ERPRecord record = ERPDataConverter.getRecord(session, projectFields, records);
					record.setProjectName(records.getProject().getTitle());
					if(record != null) {
						if(record.getAssignedUser() != null) {
							addUserEmail(mailRecords, record, record.getAssignedUser());
						}
						if(record.getCreatedUser() != null) {
							addUserEmail(mailRecords, record, record.getCreatedUser());
						}
					}
				}
			}
			if(CollectionUtils.isNotEmpty(mailRecords.values())) {
				for(Entry<ERPUser, List<ERPRecord>> e: mailRecords.entrySet()) {
					if(CollectionUtils.isNotEmpty(e.getValue())) {
						ERPMailUtil mailUtil = new ERPMailUtil(MAIL_TYPE_FOLLOW_UP);
						mailUtil.setUser(e.getKey());
						mailUtil.setRecords(e.getValue());
						executor.execute(mailUtil);
					}
				}
			}
			LoggingUtil.logMessage("########### END OF FOLLOW UP PROCESS ##############");
		} catch (Exception e) {
			LoggingUtil.logError("########### ERROR OCCURRED IN FOLLOW UPs ##############");
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
	}

	private void addUserEmail(Map<ERPUser, List<ERPRecord>> mailRecords, ERPRecord record, ERPUser user) {
		if(user != null) {
			List<ERPRecord> userRecords = null;
			if(mailRecords.get(user) != null) {
				userRecords = mailRecords.get(user);
			} else {
				userRecords = new ArrayList<ERPRecord>();
			}
			userRecords.add(record);
			mailRecords.put(user, userRecords);
		}
	}

	
	
}
