package com.rns.web.erp.service.bo.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.rns.web.erp.service.bo.api.ERPSalaryInfo;
import com.rns.web.erp.service.bo.api.ERPUserBo;
import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPFilter;
import com.rns.web.erp.service.bo.domain.ERPFinancial;
import com.rns.web.erp.service.bo.domain.ERPLeave;
import com.rns.web.erp.service.bo.domain.ERPLeaveCategory;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.dao.domain.ERPCompanyDetails;
import com.rns.web.erp.service.dao.domain.ERPCompanyLeavePolicy;
import com.rns.web.erp.service.dao.domain.ERPEmployeeDetails;
import com.rns.web.erp.service.dao.domain.ERPEmployeeFinancials;
import com.rns.web.erp.service.dao.domain.ERPEmployeeLeave;
import com.rns.web.erp.service.dao.domain.ERPEmployeeLeaveBalance;
import com.rns.web.erp.service.dao.domain.ERPEmployeeSalarySlips;
import com.rns.web.erp.service.dao.domain.ERPEmployeeSalaryStructure;
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
import com.rns.web.erp.service.util.ERPUtils;
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
			ERPUserDAO erpUserDAO = new ERPUserDAO();
			ERPLoginDetails login = erpUserDAO.getLoginDetails(company.getCreatedBy().getEmail(), session);
			ERPCompanyDetails cmp = null;
			if(company.getId() != null)  {
				cmp = erpUserDAO.getCompany(company.getId(),session);
				if(cmp != null) {
					ERPBusinessConverter.setCompanyDetails(company, cmp);
				}
			} else {
				cmp = ERPBusinessConverter.getCompanyDetails(company);
				cmp.setCreatedBy(login);
				session.persist(cmp);
				login.setCompany(cmp);
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
				ERPLoginDetails appliedBy = erpUserDAO.getLoginDetails(leave.getAppliedBy().getEmail(), session);
				lv.setAppliedBy(appliedBy);
				if(LEAVE_TYPE_LOP.equals(leave.getType().getName())) {
					lv.setWithoutPay(lv.getNoOfDays());
				} else if(leave.getType().getId() != null) {
					//Integer yearlyLeaves = erpUserDAO.getEmployeeLeaveCount(session, leave.getUser().getId(), leave.getType().getId(), CommonUtils.getFirstDate(CommonUtils.getCalendarValue(leave.getFrom(), Calendar.YEAR), 0), CommonUtils.getLastDate(CommonUtils.getCalendarValue(leave.getFrom(),Calendar.YEAR), 11), "noOfDays");
					//ERPCompanyLeavePolicy leavePolicy = erpUserDAO.getCompanyLeavePolicy(leave.getType().getId(), appliedBy.getCompany().getId(), session);
					ERPEmployeeLeaveBalance balance = erpUserDAO.getEmployeeLeaveBalance(leave.getUser().getId(), leave.getType().getId(), session);
					
					if(balance == null || balance.getBalance() == null) {
						lv.setWithoutPay(lv.getNoOfDays());
					} else {
						BigDecimal availableBalance = balance.getBalance().subtract(new BigDecimal(lv.getNoOfDays()));
						if(BigDecimal.ZERO.compareTo(availableBalance) > 0) {
							lv.setWithoutPay(Math.abs(availableBalance.intValue()));
							balance.setBalance(BigDecimal.ZERO);
						} else {
							balance.setBalance(availableBalance);
						}
					}
				}
				if(lv.getWithoutPay() == null || lv.getWithoutPay() == 0 || LEAVE_TYPE_LOP.equals(leave.getType().getName())) {
					session.persist(lv);
					tx.commit();
				} else  {
					if(StringUtils.equals(YES, leave.getApproval())) {
						session.persist(lv);
						tx.commit();
					} else {
						result= ERROR_LEAVES_EXCEEDED;
					}
				}
				
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
				ERPEmployeeLeaveBalance balance = dao.getEmployeeLeaveBalance(leave.getUser().getId(), leave.getType().getId(), session);
				if(balance != null && balance.getBalance() != null) {
					balance.setBalance(balance.getBalance().add(new BigDecimal(lv.getNoOfDays())));
				}
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
			int year = Calendar.getInstance().get(Calendar.YEAR);
			if(company.getFilter().getYear() != null) {
				year = company.getFilter().getYear().intValue();
			}
			company.getFilter().setFromDate(CommonUtils.getFirstDate(year, 0));
			company.getFilter().setToDate(CommonUtils.getLastDate(year, 11));
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
				ERPCompanyLeavePolicy policy = dao.getCompanyLeavePolicy(type.getId(), company.getId(), session);
				category.setFrequency("Yearly");
				if(policy != null) {
					category.setCount(policy.getMaxAllowed());
					if(policy.getFrequency() != null) {
						category.setFrequency(policy.getFrequency());
					}
				}
				category.setId(type.getId());
				category.setName(type.getName());
				if(StringUtils.equals("ALL", requestType)) {
					leaveTypes.add(category);
				} else if (category.getCount() != null && category.getCount() > 0) {
					leaveTypes.add(category);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		if(CollectionUtils.isNotEmpty(leaveTypes) && !StringUtils.equals("ALL", requestType)) {
			ERPLeaveCategory withoutPay = new ERPLeaveCategory();
			withoutPay.setName(LEAVE_TYPE_LOP);
			leaveTypes.add(withoutPay);
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
						setEmployeeFinancialDetails(session, user, emp, company);
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

	
	private void setEmployeeFinancialDetails(Session session, ERPUser user, ERPEmployeeDetails emp, ERPCompany company) {
		ERPFinancial financial = user.getFinancial();
		if(company == null || company.getFilter() == null || financial == null) {
			return;
		}
		Integer year = CommonUtils.getCalendarValue(new Date(), Calendar.YEAR);
		Integer month = CommonUtils.getCalendarValue(new Date(), Calendar.MONTH);
		if(company.getFilter().getYear()!= year && company.getFilter().getMonth() != month) {
			ERPEmployeeSalarySlips employeeSalarySlips = new ERPUserDAO().getEmployeeSalarySlip(emp.getId(), year, month, session);
			if(employeeSalarySlips != null) {
				financial.setBenefits(CommonUtils.getSalaryInfos(employeeSalarySlips.getBenefits()));
				financial.setDeductions(CommonUtils.getSalaryInfos(employeeSalarySlips.getDeductions()));
				financial.setAmountPayable(employeeSalarySlips.getAmountPaid());
				financial.setBasic(CommonUtils.getSalaryInfo(employeeSalarySlips.getBenefits(), "Basic"));
				financial.setTotalBenefits(CommonUtils.calculateTotal(financial.getBenefits()));
				financial.setTotalDeductions(CommonUtils.calculateTotal(financial.getDeductions()));
			}
		} else {
			ERPUtils.setEmployeeFinancial(session, user, emp);
		}
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
		Integer withoutPayCount = 0;
		for(ERPCompanyLeavePolicy type: leaveTypes) {
			ERPUserDAO erpUserDAO = new ERPUserDAO();
			Integer typeCount = erpUserDAO.getEmployeeLeaveCount(session, user.getId(), type.getType().getId(), date1, date2, "noOfDays");
			ERPLeaveCategory leaveCategory = ERPDataConverter.getLeaveCategory(type.getType());
			leaveCategory.setCount(typeCount);
			
			Integer withoutPayLeaves = erpUserDAO.getEmployeeLeaveCount(session, user.getId(), type.getType().getId(), date1, date2, "withoutPay");
			withoutPayCount = withoutPayCount + withoutPayLeaves;
			leaveCategory.setWithoutPay(withoutPayLeaves);
			total = total + typeCount;
			leaveCount.add(leaveCategory);
		}
		user.setWithoutPayLeaves(new ERPUserDAO().getWithoutPayCount(session, user.getId(), date1, date2));
		ERPLeaveCategory withoutPay = new ERPLeaveCategory();
		withoutPay.setName(LEAVE_TYPE_LOP);
		withoutPay.setCount(user.getWithoutPayLeaves());
		leaveCount.add(withoutPay);
		total = total + (user.getWithoutPayLeaves() - withoutPayCount);
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
			extractDates(user.getCompany());
			List<ERPEmployeeLeave> leaves = dao.getEmployeeLeaveDetails(session, user.getId(), user.getCompany().getFilter().getFromDate(), user.getCompany().getFilter().getToDate());
			if(CollectionUtils.isNotEmpty(leaves)) {
				for(ERPEmployeeLeave lv: leaves) {
					ERPLeave leave = ERPDataConverter.getLeave(lv);
					employeeLeaves.add(leave);
				}
			}
			//Leave balance data
			List<ERPCompanyLeavePolicy> leavePolicies = dao.getCompanyLeaveTypes(session, user.getCompany().getId());
			if(CollectionUtils.isNotEmpty(leavePolicies)) {
				List<ERPLeaveCategory> leaveBalance = new ArrayList<ERPLeaveCategory>();
				for(ERPCompanyLeavePolicy leavePolicy: leavePolicies) {
					ERPEmployeeLeaveBalance balance = dao.getEmployeeLeaveBalance(user.getId(), leavePolicy.getType().getId(), session);
					ERPLeaveCategory category = ERPDataConverter.getLeaveCategory(leavePolicy.getType());
					if(balance != null && balance.getBalance() != null) {
						category.setCount(balance.getBalance().intValue());
					} else {
						category.setCount(0);
					}
					leaveBalance.add(category);
				}
				user.setLeaveBalance(leaveBalance);
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
					leavePolicy.setFrequency(policy.getFrequency());
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
		if(company == null || company.getId() == null) {
			return ERROR_INVALID_COMPANY_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPUserDAO erpUserDAO = new ERPUserDAO();
			//erpUserDAO.removeAllSalaryStructure(company.getId(), session);
			if(company.getBasic().getId() != null) {
				ERPSalaryStructure basic = erpUserDAO.getSalaryStructure(company.getBasic().getId(), session);
				basic.setPercentage(company.getBasic().getPercentage());
			} else {
				company.getBasic().setRule("Basic");
				company.getBasic().setCompany(company);
				company.getBasic().setAmountType(AMOUNT_TYPE_PERCENTAGE);
				session.persist(ERPBusinessConverter.getSalaryStructure(company.getBasic()));
			}
			ERPCompanyDetails companyDetails = erpUserDAO.getCompany(company.getId(), session);
			
			//Delete all the structures not present currently
			if(CollectionUtils.isNotEmpty(companyDetails.getSalaryInfo())) {
				for(ERPSalaryStructure structure: companyDetails.getSalaryInfo()) {
					if(StringUtils.equalsIgnoreCase("Basic", structure.getRule())) {
						continue;
					}
					if(CollectionUtils.isEmpty(company.getSalaryInfo())) {
						session.delete(structure);
						continue;
					}
					boolean found = false;
					for(ERPSalaryInfo salaryInfo:company.getSalaryInfo()) {
						if(salaryInfo.getId() != null && salaryInfo.getId().intValue() == structure.getId().intValue()) {
							found = true;
							break;
						}
					}
					if(!found) {
						session.delete(structure);
					}
				}
			}
			
			// update all the current salary structures
			if(CollectionUtils.isNotEmpty(company.getSalaryInfo())) {
				for(ERPSalaryInfo salaryInfo: company.getSalaryInfo()) {
					ERPSalaryStructure structure = null;
					if(salaryInfo.getId() != null) {
						structure = erpUserDAO.getSalaryStructure(salaryInfo.getId(), session);
					} else {
						structure = new ERPSalaryStructure();
					}
					if(structure == null) {
						structure = new ERPSalaryStructure();
					}
					salaryInfo.setCompany(company);
					ERPBusinessConverter.setSalaryStructure(salaryInfo, structure);
					session.persist(structure);
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
				if(company.getCurrentEmployee() != null && company.getCurrentEmployee().getId() != null) {
					ERPEmployeeSalaryStructure employeeSalaryStructure = dao.getEmployeeSalaryStructure(salaryInfo.getId(),company.getCurrentEmployee().getId(),session);
					if(employeeSalaryStructure != null) {
						salaryInfo.setAmount(employeeSalaryStructure.getAmount());
					}
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
				ERPUserDAO erpUserDAO = new ERPUserDAO();
				employee = erpUserDAO.getEmployeeById(user.getId(), session);
				ERPEmployeeFinancials financials = employee.getFinancials();
				if(employee != null && financials != null) {
					financials.setSalary(user.getFinancial().getSalary());
					erpUserDAO.removeAllEmployeeSalaryStructure(employee.getId(), session);
					if(user.getCompany() != null) {
						ERPSalaryInfo basic = user.getCompany().getBasic();
						ERPEmployeeSalaryStructure employeeSalaryStructure = ERPBusinessConverter.getEmployeeSalaryStructure(basic);
						employeeSalaryStructure.setSalaryStructure(erpUserDAO.getSalaryStructure(basic.getId(), session));
						employeeSalaryStructure.setEmployee(employee);
						session.persist(employeeSalaryStructure);
						addEmployeeStructures(user.getCompany().getSalaryInfo(), session, employee);
						//addEmployeeStructures(user.getCompany().getFinancial().getDeductions(), session, employee);
					}
					tx.commit();
				} else {
					result = ERROR_INCOMPLETE_BANK_DETAILS;
				}
			} else {
				result = ERROR_INCOMPLETE_BANK_DETAILS;
			}
			
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
			if(StringUtils.isBlank(StringUtils.trimToEmpty(result))) {
				result = ERROR_IN_PROCESSING;
			}
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	private void addEmployeeStructures(List<ERPSalaryInfo> list, Session session, ERPEmployeeDetails employee) {
		if(CollectionUtils.isNotEmpty(list)) {
			for(ERPSalaryInfo salaryInfo:list) {
				if(salaryInfo.getAmount() != null && BigDecimal.ZERO.compareTo(salaryInfo.getAmount()) < 0) {
					ERPEmployeeSalaryStructure employeeSalaryStructure = ERPBusinessConverter.getEmployeeSalaryStructure(salaryInfo);
					employeeSalaryStructure.setEmployee(employee);
					employeeSalaryStructure.setSalaryStructure(new ERPUserDAO().getSalaryStructure(salaryInfo.getId(), session));
					session.persist(employeeSalaryStructure);
				}
			}
		}
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
			ERPCompanyDetails companyDetails = dao.getCompany(company.getId(), session);
			ERPDataConverter.setCompany(companyDetails, company);
			List<ERPEmployeeDetails> emps = dao.getCompanyEmployees(company.getId(), session);
			if(CollectionUtils.isNotEmpty(emps)) {
				extractDates(company);
				List<ERPCompanyLeavePolicy> leaveTypes = dao.getCompanyLeaveTypes(session, company.getId());
				for(ERPEmployeeDetails emp: emps) {
					ERPUser user = ERPDataConverter.getEmployee(emp);
					if(user != null && isNotFiltered(company, user)) {
						setEmployeeLeaveCount(session, leaveTypes, user, company);
						setEmployeeFinancialDetails(session, user, emp, company);
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

	private boolean isNotFiltered(ERPCompany company, ERPUser user) {
		if(CollectionUtils.isEmpty(company.getEmployees())) {
			return true;
		}
		return company.getEmployees().contains(user);
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
				setEmployeeFinancialDetails(session, user, emp, employee.getCompany());
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

	public String updateEmployeeSalarySlips(ERPCompany company) {
		if(company == null || company.getId() == null || company.getFilter() == null) {
			return ERROR_INVALID_COMPANY_DETAILS;
		}
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPUserDAO dao = new ERPUserDAO();
			List<ERPEmployeeDetails> employees = dao.getCompanyEmployees(company.getId(), session);
			ERPUtils.saveEmployeeSalarySlips(company.getFilter(), session, dao, employees);
		} catch (Exception e) {
			e.printStackTrace();
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return RESPONSE_OK;
	}

	

	public String forgotPassword(ERPUser user) {
		if(user == null || StringUtils.isEmpty(user.getEmail())) {
			return ERROR_INVALID_USER_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPLoginDetails existingLogin = new ERPUserDAO().getLoginDetails(user.getEmail(), session);
			if(existingLogin != null) {
				if(StringUtils.equals(USER_STATUS_ACTIVE, existingLogin.getStatus()) || StringUtils.equals(USER_STATUS_PASSWORD_SENT, existingLogin.getStatus())) {
					ERPUser currentUser = ERPDataConverter.getERPUser(existingLogin);
					String password = CommonUtils.generatePassword(currentUser);
					existingLogin.setPassword(password);
					existingLogin.setStatus(USER_STATUS_PASSWORD_SENT);
					tx.commit();
					ERPMailUtil forgotMail = new ERPMailUtil(MAIL_TYPE_FORGOT_PASSWORD);
					currentUser.setPassword(password);
					forgotMail.setUser(currentUser);
					executor.execute(forgotMail);
				} else {
					result = ERROR_INACTIVE_PROFILE;
				}
			} else {
				result = ERROR_INVALID_LOGIN_DETAILS;
			}
			
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	public String updateEmployeeLeaveBalance(ERPUser user) {
		if(user == null || user.getId() == null || CollectionUtils.isEmpty(user.getLeaveCount())) {
			return ERROR_INVALID_USER_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPUserDAO erpUserDAO = new ERPUserDAO();
			ERPEmployeeDetails existingLogin = erpUserDAO.getEmployeeById(user.getId(), session);
			if(existingLogin != null) {
				for(ERPLeaveCategory type:user.getLeaveBalance()) {
					if(type.getId() == null || type.getCount() == null) {
						continue;
					}
					ERPEmployeeLeaveBalance balance = erpUserDAO.getEmployeeLeaveBalance(user.getId(), type.getId(),session);
					if(balance == null) {
						balance = new ERPEmployeeLeaveBalance();
						ERPLeaveType leaveType = new ERPLeaveType();
						leaveType.setId(type.getId());
						balance.setType(leaveType);
						balance.setEmployee(existingLogin);
					}
					balance.setUpdatedDate(new Date());
					balance.setBalance(new BigDecimal(type.getCount()));
					session.persist(balance);
				}
				tx.commit();
			} else {
				result = ERROR_INVALID_LOGIN_DETAILS;
			}
			
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}
	
	
	
}
