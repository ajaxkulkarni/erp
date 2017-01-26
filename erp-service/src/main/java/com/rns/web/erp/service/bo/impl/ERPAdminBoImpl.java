package com.rns.web.erp.service.bo.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.rns.web.erp.service.bo.api.ERPAdminBo;
import com.rns.web.erp.service.bo.domain.ERPLeaveCategory;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.dao.domain.ERPLeaveType;
import com.rns.web.erp.service.dao.domain.ERPLoginDetails;
import com.rns.web.erp.service.dao.impl.ERPAdminDAO;
import com.rns.web.erp.service.dao.impl.ERPUserDAO;
import com.rns.web.erp.service.util.CommonUtils;
import com.rns.web.erp.service.util.ERPConstants;
import com.rns.web.erp.service.util.ERPDataConverter;
import com.rns.web.erp.service.util.ERPMailUtil;
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

}
