package com.rns.web.erp.service.bo.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.rns.web.erp.service.bo.api.ERPProjectBo;
import com.rns.web.erp.service.bo.domain.ERPField;
import com.rns.web.erp.service.bo.domain.ERPProject;
import com.rns.web.erp.service.bo.domain.ERPRecord;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.dao.domain.ERPEmployeeDetails;
import com.rns.web.erp.service.dao.domain.ERPLoginDetails;
import com.rns.web.erp.service.dao.domain.ERPProjectFields;
import com.rns.web.erp.service.dao.domain.ERPProjectRecordValues;
import com.rns.web.erp.service.dao.domain.ERPProjectRecords;
import com.rns.web.erp.service.dao.domain.ERPProjectUsers;
import com.rns.web.erp.service.dao.domain.ERPProjects;
import com.rns.web.erp.service.dao.impl.ERPProjectDAO;
import com.rns.web.erp.service.dao.impl.ERPUserDAO;
import com.rns.web.erp.service.util.CommonUtils;
import com.rns.web.erp.service.util.ERPBusinessConverter;
import com.rns.web.erp.service.util.ERPConstants;
import com.rns.web.erp.service.util.ERPDataConverter;
import com.rns.web.erp.service.util.LoggingUtil;

public class ERPProjectBoImpl implements ERPProjectBo, ERPConstants {

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

	public String updateProject(ERPUser user) {
		if (user == null || StringUtils.isEmpty(user.getEmail()) || user.getCurrentProject() == null) {
			return ERROR_INVALID_USER_DETAILS;
		}
		ERPProject currentProject = user.getCurrentProject();
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPLoginDetails loginDetails = new ERPUserDAO().getLoginDetails(user.getEmail(), session);
			if (currentProject.getId() != null) {
				ERPProjects projects = new ERPProjectDAO().getProjectById(currentProject.getId(), session);
				if (projects == null) {
					result = ERROR_PROJECT_NOT_FOUND;
				} else {
					projects.setTitle(currentProject.getTitle());
					projects.setDescription(currentProject.getDescription());
					if(StringUtils.isNotEmpty(currentProject.getStatus())) {
						projects.setStatus(currentProject.getStatus());
					}
					projects.setStatus(USER_STATUS_ACTIVE);

					List<ERPProjectUsers> projectUsers = new ERPProjectDAO().getProjectUsers(currentProject.getId(),
							session);
					if (CollectionUtils.isEmpty(projectUsers)) {
						// Add all users
						addAllUsers(currentProject, session, projects);
					} else {
						// Delete users
						deleteUnwantedUsers(currentProject, projectUsers, session);
						// Add new users
						addNewUsers(currentProject, session, projects, projectUsers);
					}
				}
			} else {
				ERPProjects projects = new ERPProjects();
				projects.setTitle(currentProject.getTitle());
				projects.setDescription(currentProject.getDescription());
				projects.setCreatedBy(loginDetails);
				projects.setCreatedDate(new Date());
				projects.setStatus(USER_STATUS_ACTIVE);
				session.persist(projects);
				
				addAllUsers(currentProject, session, projects);
				
				ERPProjectUsers projectUsers = new ERPProjectUsers();
				projectUsers.setUser(loginDetails);
				projectUsers.setProject(projects);
				projectUsers.setStatus(USER_STATUS_ACTIVE);
				projectUsers.setCreatedDate(new Date());
				
				session.persist(projectUsers);
			}

			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = ERROR_IN_PROCESSING;
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	private void deleteUnwantedUsers(ERPProject currentProject, List<ERPProjectUsers> projectUsers, Session session) {
		for (ERPProjectUsers projectUser : projectUsers) {
			boolean found = false;
			if(CollectionUtils.isNotEmpty(currentProject.getUsers())) {
				for (ERPUser erpUser : currentProject.getUsers()) {
					if (StringUtils.equals(erpUser.getEmail(), projectUser.getUser().getEmail())) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				//projectUser.setStatus(USER_STATUS_DELETED);
				session.delete(projectUser);
			}
		}
	}

	private void addNewUsers(ERPProject currentProject, Session session, ERPProjects projects, List<ERPProjectUsers> projectUsers) {
		if(CollectionUtils.isEmpty(currentProject.getUsers())) {
			return;
		}
		for (ERPUser erpUser : currentProject.getUsers()) {
			boolean found = false;
			for (ERPProjectUsers projectUser : projectUsers) {
				if (StringUtils.equals(erpUser.getEmail(), projectUser.getUser().getEmail())) {
					found = true;
					break;
				}
			}
			if (!found) {
				ERPProjectUsers users = ERPBusinessConverter.getProjectUser(session, projects, erpUser);
				if(users != null) {
					session.persist(users);
				}
			}
		}
	}

	private void addAllUsers(ERPProject currentProject, Session session, ERPProjects projects) {
		if (CollectionUtils.isNotEmpty(currentProject.getUsers())) {
			for (ERPUser erpUser : currentProject.getUsers()) {
				ERPProjectUsers projectUser = ERPBusinessConverter.getProjectUser(session, projects, erpUser);
				if (projectUser != null) {
					session.persist(projectUser);
				}
			}
		}
	}

	public List<ERPProject> getAllUserProjects(ERPUser user) {
		if (user == null || StringUtils.isEmpty(user.getEmail()) || user.getId() == null) {
			return null;
		}
		Session session = null;
		List<ERPProject> userProjects = new ArrayList<ERPProject>();
		
		try {
			session = this.sessionFactory.openSession();
			List<ERPProjectUsers> projectUsers = new ERPProjectDAO().getUserProjects(user.getId(), session);
			if(CollectionUtils.isNotEmpty(projectUsers)) {
				for(ERPProjectUsers userProject: projectUsers) {
					ERPProject project = ERPDataConverter.getProject(userProject);
					userProjects.add(project);
				}
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return userProjects;
	}

	public ERPProject getProject(ERPUser user, String requestType) {
		if (user == null || user.getCurrentProject() == null || user.getCurrentProject().getId() == null) {
			return null;
		}
		Session session = null;
		ERPProject project = null;
		try {
			session = this.sessionFactory.openSession();
			ERPProjectDAO erpProjectDAO = new ERPProjectDAO();
			ERPProjects projects = erpProjectDAO.getProjectById(user.getCurrentProject().getId(), session);
			project = ERPDataConverter.getProjectBasic(projects);
			//Project users
			List<ERPProjectUsers> projectUsers = erpProjectDAO.getProjectUsers(projects.getId(), session);
			if(CollectionUtils.isNotEmpty(projectUsers)) {
				for(ERPProjectUsers projectUser: projectUsers) {
					ERPUser erpUser = ERPDataConverter.getERPUser(projectUser.getUser());
					ERPEmployeeDetails erpEmp = new ERPUserDAO().getEmployeeByEmail(erpUser.getEmail(), session);
					ERPDataConverter.setEmployee(erpEmp, erpUser);
					if(erpUser != null) {
						project.getUsers().add(erpUser);
					}
				}
			}
			//Project structure
			List<ERPProjectFields> projectFields = erpProjectDAO.getProjectFields(projects.getId(), session);
			if(CollectionUtils.isNotEmpty(projectFields)) {
				for(ERPProjectFields fields: projectFields) {
					ERPField field = new ERPField();
					field.setId(fields.getId());
					field.setName(fields.getName());
					field.setType(fields.getType());
					if(StringUtils.equals(field.getType(), FIELD_TYPE_TITLE)) {
						project.setTitleField(field);
						continue;
					}
					project.getFields().add(field);
				}
			}
			
			//Project records fetch if request Type 'REC'
			if(StringUtils.equals("REC", requestType)) {
				List<ERPProjectRecords> records = erpProjectDAO.getProjectRecords(projects.getId(), session);
				if(CollectionUtils.isNotEmpty(records)) {
					for(ERPProjectRecords rec: records) {
						ERPRecord record = ERPDataConverter.getRecord(session, projectFields, rec);
						project.getRecords().add(record);
					}
				}
			}
			
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return project;
	}


	public List<ERPUser> getAllCompanyLogins(ERPUser user) {
		if(user == null || user.getCompany() == null || user.getCompany().getId() == null) {
			return null;
		}
		List<ERPUser> users = new ArrayList<ERPUser>();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			ERPProjectDAO erpProjectDAO = new ERPProjectDAO();
			List<ERPLoginDetails> loginUsers = null;
			if(user.getCurrentProject() == null || user.getCurrentProject().getId() == null) {
				loginUsers = erpProjectDAO.getAllUsers(user.getCompany().getId(), session);
			} else {
				loginUsers = erpProjectDAO.getRemainingUsers(user.getCompany().getId(), user.getCurrentProject().getId(), session);
			}
			for(ERPLoginDetails login: loginUsers) {
				if(user.getId() != null && user.getId().intValue() == login.getId().intValue()) {
					continue;
				}
				ERPUser erpUser = ERPDataConverter.getERPUser(login);
				ERPEmployeeDetails erpEmp = new ERPUserDAO().getEmployeeByEmail(login.getEmail(), session);
				ERPDataConverter.setEmployee(erpEmp, erpUser);
				users.add(erpUser);
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return users;
	}

	public String updateProjectStructure(ERPUser user) {
		if (user == null || StringUtils.isEmpty(user.getEmail()) || user.getCurrentProject() == null) {
			return ERROR_INVALID_USER_DETAILS;
		}
		ERPProject currentProject = user.getCurrentProject();
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			//ERPLoginDetails loginDetails = new ERPUserDAO().getLoginDetails(user.getEmail(), session);
			if (currentProject.getId() != null) {
				ERPProjectDAO erpProjectDAO = new ERPProjectDAO();
				ERPProjects projects = erpProjectDAO.getProjectById(currentProject.getId(), session);
				if (projects == null) {
					result = ERROR_PROJECT_NOT_FOUND;
				} else {
					List<ERPProjectFields> projectFields = erpProjectDAO.getProjectFields(projects.getId(), session);
					// Delete unwanted
					deleteUnwanted(currentProject, projectFields);
					//Add new and edit current
					updateNewRecords(currentProject, session, projects, projectFields);
				}
			} else {
				result = ERROR_PROJECT_NOT_FOUND;
			}

			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = ERROR_IN_PROCESSING;
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	private void updateNewRecords(ERPProject currentProject, Session session, ERPProjects projects,
			List<ERPProjectFields> projectFields) {
		ERPField titleField = currentProject.getTitleField();
		if(titleField != null) {
			if(titleField.getId() == null) {
				ERPProjectFields title = new ERPProjectFields();
				title.setName(titleField.getName());
				title.setType(FIELD_TYPE_TITLE);
				title.setCreatedDate(new Date());
				title.setStatus(USER_STATUS_ACTIVE);
				title.setProject(projects);
				session.persist(title);
			} else {
				ERPProjectFields oldTitleField = new ERPProjectDAO().getField(titleField.getId(), session);
				if(oldTitleField != null) {
					oldTitleField.setName(titleField.getName());
				}
			}
		}
		
		if(CollectionUtils.isNotEmpty(currentProject.getFields())) {
			for(ERPField pf: currentProject.getFields()) {
				boolean found = false;
				if(CollectionUtils.isNotEmpty(projectFields)) {
					for(ERPProjectFields field: projectFields) {
						if(field.getId() != null && pf.getId() != null && field.getId().intValue() == pf.getId().intValue()) {
							found = true;
							if(StringUtils.isNotBlank(pf.getName())) {
								field.setName(pf.getName());
							}
							if(StringUtils.isNotBlank(pf.getType())) {
								field.setType(pf.getType());
							}
							break;
						}
					}
				}
				if(!found) {
					ERPProjectFields fields = new ERPProjectFields();
					fields.setName(pf.getName());
					fields.setType(pf.getType());
					fields.setCreatedDate(new Date());
					fields.setStatus(USER_STATUS_ACTIVE);
					fields.setProject(projects);
					session.persist(fields);
				}
			}
		}
	}

	private void deleteUnwanted(ERPProject currentProject, List<ERPProjectFields> projectFields) {
		if(CollectionUtils.isNotEmpty(projectFields)) {
			for(ERPProjectFields pf: projectFields) {
				if(StringUtils.equals(FIELD_TYPE_TITLE, pf.getType())) {
					continue;
				}
				boolean found = false;
				if(CollectionUtils.isNotEmpty(currentProject.getFields())) {
					for(ERPField field: currentProject.getFields()) {
						if(field.getId() != null && pf.getId() != null && field.getId().intValue() == pf.getId().intValue()) {
							found = true;
							break;
						}
					}
				}
				if(!found) {
					pf.setStatus(USER_STATUS_DELETED);
				}
			}
		}
	}

	public String updateRecord(ERPUser user) {
		if (user == null || StringUtils.isEmpty(user.getEmail()) || user.getCurrentRecord() == null) {
			return ERROR_INVALID_USER_DETAILS;
		}
		ERPRecord currentRecord = user.getCurrentRecord();
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPProjectDAO erpProjectDAO = new ERPProjectDAO();
			ERPLoginDetails loginDetails = new ERPUserDAO().getLoginDetails(user.getEmail(), session);
			ERPField titleField = currentRecord.getTitleField();
			if (currentRecord.getId() != null) {
				
				ERPProjectRecords records = erpProjectDAO.getRecordById(currentRecord.getId(), session);
				if (records == null) {
					result = ERROR_RECORD_NOT_FOUND;
				} else {
					records.setStatus(currentRecord.getStatus());
					records.setRecordDate(currentRecord.getRecordDate());
					//Set title field
					if(titleField != null) {
						ERPProjectRecordValues titleValue = erpProjectDAO.getRecordValueById(titleField.getRecordId(), session);
						titleValue.setValue(titleField.getValue());
					}
					if (CollectionUtils.isNotEmpty(currentRecord.getValues())) {
						for (ERPField value : currentRecord.getValues()) {
							if(value.getRecordId() != null) {
								ERPProjectRecordValues values = erpProjectDAO.getRecordValueById(value.getRecordId(), session);
								if(values != null && StringUtils.isNotBlank(value.getValue())) {
									values.setUpdatedBy(loginDetails);
									values.setUpdatedDate(new Date());
									values.setValue(value.getValue());
								}
							}
						}
					}
				}
			} else {
				ERPProjectRecords records = ERPBusinessConverter.getRecords(user, currentRecord, loginDetails);
				session.persist(records);
				if(titleField != null) {
					ERPProjectRecordValues titleValue = ERPBusinessConverter.getRecordValues(loginDetails, records, titleField);
					session.persist(titleValue);
				}
				if(CollectionUtils.isNotEmpty(currentRecord.getValues())) {
					for(ERPField value: currentRecord.getValues()) {
						ERPProjectRecordValues values = ERPBusinessConverter.getRecordValues(loginDetails, records, value);
						session.persist(values);
					}
				}
				
			}

			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = ERROR_IN_PROCESSING;
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;

	}

	public ERPRecord getRecord(ERPUser user) {
		if (user == null || user.getCurrentProject() == null || user.getCurrentRecord().getId() == null) {
			return null;
		}
		Session session = null;
		ERPRecord record = null;
		try {
			session = this.sessionFactory.openSession();
			ERPProjectDAO erpProjectDAO = new ERPProjectDAO();
			List<ERPProjectFields> projectFields = erpProjectDAO.getProjectFields(user.getCurrentProject().getId(), session);
			ERPProjectRecords rec = erpProjectDAO.getRecordById(user.getCurrentRecord().getId(), session);
			record = ERPDataConverter.getRecord(session, projectFields, rec);
			
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return record;
	}

}
