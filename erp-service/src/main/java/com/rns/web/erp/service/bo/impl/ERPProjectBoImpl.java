package com.rns.web.erp.service.bo.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.rns.web.erp.service.bo.domain.ERPComment;
import com.rns.web.erp.service.bo.domain.ERPField;
import com.rns.web.erp.service.bo.domain.ERPFile;
import com.rns.web.erp.service.bo.domain.ERPLog;
import com.rns.web.erp.service.bo.domain.ERPMailConfig;
import com.rns.web.erp.service.bo.domain.ERPProject;
import com.rns.web.erp.service.bo.domain.ERPRecord;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.dao.domain.ERPEmployeeDetails;
import com.rns.web.erp.service.dao.domain.ERPLoginDetails;
import com.rns.web.erp.service.dao.domain.ERPProjectComments;
import com.rns.web.erp.service.dao.domain.ERPProjectFields;
import com.rns.web.erp.service.dao.domain.ERPProjectFiles;
import com.rns.web.erp.service.dao.domain.ERPProjectLog;
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
import com.rns.web.erp.service.util.ProjectLogUtil;

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
					if(StringUtils.equals(USER_STATUS_DELETED, currentProject.getStatus())) {
						//Delete request
						projects.setStatus(currentProject.getStatus());
					} else {
						projects.setTitle(currentProject.getTitle());
						projects.setDescription(currentProject.getDescription());
						projects.setStatus(USER_STATUS_ACTIVE);
						List<ERPProjectUsers> projectUsers = new ERPProjectDAO().getProjectUsers(currentProject.getId(),
								session);
						if (CollectionUtils.isEmpty(projectUsers)) {
							// Add all users
							addAllUsers(currentProject, session, projects);
							ProjectLogUtil.addProjectUsersLog(currentProject.getUsers(), projects, session, loginDetails);
						} else {
							// Delete users
							ProjectLogUtil.projectUsersDeletedLog(deleteUnwantedUsers(currentProject, projectUsers, session), loginDetails, session);
							// Add new users
							ProjectLogUtil.projectUsersAddedLog(addNewUsers(currentProject, session, projects, projectUsers), loginDetails, session);
						}
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
				
				ProjectLogUtil.projectCreateLog(session, loginDetails, projects);
				
				addAllUsers(currentProject, session, projects);
				
				ProjectLogUtil.addProjectUsersLog(currentProject.getUsers(), projects, session, loginDetails);
				
				ERPProjectUsers projectUsers = new ERPProjectUsers();
				projectUsers.setUser(loginDetails);
				projectUsers.setProject(projects);
				projectUsers.setStatus(USER_STATUS_ACTIVE);
				projectUsers.setCreatedDate(new Date());
				user.setRights(ERPDataConverter.createAllAccess());
				ERPBusinessConverter.setAccessRights(user, projectUsers);
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

	
	private List<ERPProjectUsers> deleteUnwantedUsers(ERPProject currentProject, List<ERPProjectUsers> projectUsers, Session session) {
		List<ERPProjectUsers> deleted = new ArrayList<ERPProjectUsers>(); 
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
				deleted.add(projectUser);
			}
		}
		return deleted;
	}

	private List<ERPProjectUsers> addNewUsers(ERPProject currentProject, Session session, ERPProjects projects, List<ERPProjectUsers> projectUsers) {
		if(CollectionUtils.isEmpty(currentProject.getUsers())) {
			return null;
		}
		List<ERPProjectUsers> added = new ArrayList<ERPProjectUsers>();
		for (ERPUser erpUser : currentProject.getUsers()) {
			boolean found = false;
			for (ERPProjectUsers projectUser : projectUsers) {
				if (StringUtils.equals(erpUser.getEmail(), projectUser.getUser().getEmail())) {
					found = true;
					ERPBusinessConverter.setAccessRights(erpUser, projectUser);
					break;
				}
			}
			if (!found) {
				ERPProjectUsers users = ERPBusinessConverter.getProjectUser(session, projects, erpUser);
				if(users != null) {
					session.persist(users);
					added.add(users);
				}
			}
		}
		return added;
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
					if(project != null) {
						userProjects.add(project);
					}
				}
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return userProjects;
	}

	public ERPProject getProject(ERPUser user, String requestType, String timeRange) {
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
					if(erpUser != null) {
						/*if(projects.getCreatedBy() != null && projects.getCreatedBy().getId().intValue() == projectUser.getUser().getId().intValue()) {
							//Creator has all rights ..
							ERPAccessRights rights = ERPDataConverter.createAllAccess();
							erpUser.setRights(rights);
							project.setAccessRights(rights);
						} else {*/
							erpUser.setRights(ERPDataConverter.getAccessRights(projectUser));
							if(user.getId() != null && erpUser.getId().intValue() == user.getId().intValue()) {
								project.setAccessRights(erpUser.getRights());
							}
						//}
					}
					ERPEmployeeDetails erpEmp = new ERPUserDAO().getEmployeeByEmail(erpUser.getEmail(), session);
					if(erpEmp != null) {
						ERPDataConverter.setEmployee(erpEmp, erpUser);
					}
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
					if(StringUtils.isNotBlank(fields.getValues()) && StringUtils.equals(FIELD_TYPE_MULTIPLE, fields.getType())) {
						field.setPossibleValues(Arrays.asList(StringUtils.split(fields.getValues(), ",")));
						field.setValues(fields.getValues());
					}
					if(StringUtils.equals(field.getType(), FIELD_TYPE_TITLE)) {
						project.setTitleField(field);
						continue;
					}
					project.getFields().add(field);
				}
			}
			
			//Project records fetch if request Type 'REC'
			if(StringUtils.equals("REC", requestType)) {
				List<ERPProjectRecords> records = null;
				if(StringUtils.equals(timeRange, "ALL")) {		
					records = erpProjectDAO.getProjectRecords(projects.getId(), session);
				} else if (StringUtils.equals("WEEK", timeRange)) {
					records = erpProjectDAO.getProjectRecords(projects.getId(),CommonUtils.getWeekFirstDate(), CommonUtils.getWeekLastDate(), session);
				} else {
					records = erpProjectDAO.getProjectRecords(projects.getId(),CommonUtils.getMonthFirstDate(), CommonUtils.getMonthLastDate(), session);
				}
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
			ERPLoginDetails loginDetails = new ERPUserDAO().getLoginDetails(user.getEmail(), session);
			if (currentProject.getId() != null) {
				ERPProjectDAO erpProjectDAO = new ERPProjectDAO();
				ERPProjects projects = erpProjectDAO.getProjectById(currentProject.getId(), session);
				if (projects == null) {
					result = ERROR_PROJECT_NOT_FOUND;
				} else {
					List<ERPProjectFields> projectFields = erpProjectDAO.getProjectFields(projects.getId(), session);
					// Delete unwanted
					ProjectLogUtil.projectDeletedFieldsLog(deleteUnwanted(currentProject, projectFields), loginDetails, session);
					//Add new and edit current
					String changeLog = updateNewRecords(currentProject, session, projects, projectFields);
					ProjectLogUtil.projectFieldChangeLog(changeLog,loginDetails,session, projects);
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

	private String updateNewRecords(ERPProject currentProject, Session session, ERPProjects projects, List<ERPProjectFields> projectFields) {
		ERPField titleField = currentProject.getTitleField();
		StringBuilder addedFields = new StringBuilder();
		StringBuilder changedFields = new StringBuilder();
		if(titleField != null) {
			if(titleField.getId() == null) {
				ERPProjectFields title = new ERPProjectFields();
				title.setName(titleField.getName());
				title.setType(FIELD_TYPE_TITLE);
				title.setCreatedDate(new Date());
				title.setStatus(USER_STATUS_ACTIVE);
				title.setProject(projects);
				session.persist(title);
				addedFields.append(title.getName()).append(",");
			} else {
				ERPProjectFields oldTitleField = new ERPProjectDAO().getField(titleField.getId(), session);
				if(oldTitleField != null && !StringUtils.equals(titleField.getName(), oldTitleField.getName())) {
					changedFields.append(oldTitleField.getName()).append(" to ").append(titleField.getName()).append(",");
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
							if(StringUtils.isNotBlank(pf.getName()) && !StringUtils.equals(field.getName(), pf.getName())) {
								changedFields.append(field.getName()).append(" to ").append(pf.getName()).append(",");
								field.setName(pf.getName());
							}
							if(StringUtils.isNotBlank(pf.getType()) && !StringUtils.equals(field.getType(), pf.getType())) {
								field.setType(pf.getType());
							}
							if(StringUtils.isNotBlank(pf.getValues()) && !StringUtils.equals(field.getValues(), pf.getValues())) {
								field.setValues(StringUtils.removeEnd(StringUtils.trimToEmpty(pf.getValues()), ","));
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
					addedFields.append(fields.getName()).append(",");
				}
			}
		}
		if(StringUtils.isBlank(addedFields)) {
			addedFields.append(" ");
		}
		if(StringUtils.isBlank(changedFields)) {
			changedFields.append(" ");
		}
		return addedFields.append("||").append(changedFields).toString();
	}

	private List<ERPProjectFields> deleteUnwanted(ERPProject currentProject, List<ERPProjectFields> projectFields) {
		if(CollectionUtils.isNotEmpty(projectFields)) {
			List<ERPProjectFields> fields = new ArrayList<ERPProjectFields>();
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
					fields.add(pf);
				}
			}
			return fields;
		}
		return null;
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
			List<ERPProjectRecordValues> changedValues = new ArrayList<ERPProjectRecordValues>();
			List<ERPProjectRecordValues> addedValues = new ArrayList<ERPProjectRecordValues>();
			List<ERPProjectLog> logs = new ArrayList<ERPProjectLog>();
			ERPProjectRecords records = null;
			boolean isRecordAssigned = false;
			String type = NOTIFICATION_RECORD_UPDATE;
			if (currentRecord.getId() != null) {
				
				records = erpProjectDAO.getRecordById(currentRecord.getId(), session);
				if (records == null) {
					result = ERROR_RECORD_NOT_FOUND;
				} else {
					ERPProjectLog recordChangeLog = ProjectLogUtil.createRecordChangeLog(records, currentRecord, loginDetails);
					records.setStatus(currentRecord.getStatus());
					records.setRecordDate(currentRecord.getRecordDate());
					records.setColor(currentRecord.getColor());
					if(records.getAssignedTo() == null || (currentRecord.getAssignedUser() != null && records.getAssignedTo().getId().intValue() != currentRecord.getAssignedUser().getId().intValue())) {
						isRecordAssigned = true;
					}
					records.setAssignedTo(ERPBusinessConverter.getLoginDetails(currentRecord.getAssignedUser()));
					
					if(StringUtils.equals(currentRecord.getStatus(), USER_STATUS_DELETED)) {
						type = NOTIFICATION_RECORD_DELETED;
					}
					if(recordChangeLog != null) {
						session.persist(recordChangeLog);
						logs.add(recordChangeLog);
					}
					
					//Set title field
					if(titleField != null) {
						ERPProjectRecordValues titleValue = erpProjectDAO.getRecordValueById(titleField.getRecordId(), session);
						if(titleValue != null && !StringUtils.equals(titleValue.getValue(), titleField.getValue())) {
							titleValue.setValue(titleField.getValue());
							changedValues.add(titleValue);
						}
					}
					if (CollectionUtils.isNotEmpty(currentRecord.getValues())) {
						for (ERPField value : currentRecord.getValues()) {
							if(value.getRecordId() != null) {
								ERPProjectRecordValues values = erpProjectDAO.getRecordValueById(value.getRecordId(), session);
								if(values != null && StringUtils.isNotBlank(value.getValue()) && !StringUtils.equals(values.getValue(), value.getValue())) {
									values.setUpdatedBy(loginDetails);
									values.setUpdatedDate(new Date());
									values.setValue(value.getValue());
									changedValues.add(values);
								}
							} else {
								ERPProjectRecordValues values = ERPBusinessConverter.getRecordValues(loginDetails, records, value);
								session.persist(values);
								addedValues.add(values);
							}
						}
					}
					
				}
			} else {
				type = NOTIFICATION_RECORD_ADDED;
				records = ERPBusinessConverter.getRecords(user, currentRecord, loginDetails);
				ERPProjects project = new ERPProjectDAO().getProjectById(user.getCurrentProject().getId(), session);
				records.setProject(project);
				records.setAssignedTo(ERPBusinessConverter.getLoginDetails(currentRecord.getAssignedUser()));
				if(records.getAssignedTo() != null) {
					isRecordAssigned = true;
				}
				session.persist(records);
				ERPProjectLog recordAddLog = ProjectLogUtil.createRecordChangeLog(records, currentRecord, loginDetails);
				session.persist(recordAddLog);
				logs.add(recordAddLog);
				
				if(titleField != null) {
					ERPProjectRecordValues titleValue = ERPBusinessConverter.getRecordValues(loginDetails, records, titleField);
					session.persist(titleValue);
					addedValues.add(titleValue);
				}
				if(CollectionUtils.isNotEmpty(currentRecord.getValues())) {
					for(ERPField value: currentRecord.getValues()) {
						ERPProjectRecordValues values = ERPBusinessConverter.getRecordValues(loginDetails, records, value);
						session.persist(values);
						addedValues.add(values);
					}
				}
				
			}
			ERPProjectLog addedValuesLog = ProjectLogUtil.projectAddedValuesLog(addedValues, session, loginDetails, currentRecord);
			ERPProjectLog changedValuesLog = ProjectLogUtil.projectChangedValuesLog(changedValues, session, loginDetails, currentRecord);
			
			if(addedValuesLog != null) {
				logs.add(addedValuesLog);
			}
			if(changedValuesLog != null) {
				logs.add(changedValuesLog);
			}
			//Send mails
			ProjectLogUtil.sendLog(logs, session, executor, records, loginDetails, currentRecord, type, isRecordAssigned);
			
			tx.commit();
			
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = ERROR_IN_PROCESSING;
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;

	}

	public ERPFile updateFile(ERPUser user) {
		if (user == null || StringUtils.isEmpty(user.getEmail()) || user.getCurrentRecord() == null || user.getCurrentRecord().getFile() == null) {
			ERPFile file = new ERPFile();
			file.setStatus(ERROR_INVALID_USER_DETAILS);
			return file;
		}
		ERPRecord currentRecord = user.getCurrentRecord();
		Session session = null;
		ERPFile file = currentRecord.getFile();
		file.setStatus(RESPONSE_OK);
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPProjectDAO erpProjectDAO = new ERPProjectDAO();
			ERPLoginDetails loginDetails = new ERPUserDAO().getLoginDetails(user.getEmail(), session);
			if (currentRecord.getId() != null) {
				
				ERPProjectRecords records = erpProjectDAO.getRecordById(currentRecord.getId(), session);
				if (records == null) {
					file.setStatus(ERROR_RECORD_NOT_FOUND);
				} else {
					if(file.getId() != null) {
						ERPProjectFiles files = erpProjectDAO.getRecordFile(file.getId(), session);
						files.setStatus(USER_STATUS_DELETED);
						ERPProjectLog fileDeleteLog = ProjectLogUtil.fileDeleteLog(session, loginDetails, files);
						ProjectLogUtil.sendLog(fileDeleteLog, session, executor, records, loginDetails, currentRecord, NOTIFICATION_FILE_DELETED);
					} else if(file.getFileData() != null) {
						ERPProjectFiles files = ERPBusinessConverter.getERPProjectFiles(loginDetails, records, file);
						session.persist(files);
						ERPProjectLog fileCreateLog = ProjectLogUtil.fileCreateLog(session, loginDetails, files);
						ProjectLogUtil.sendLog(fileCreateLog, session, executor, records, loginDetails, currentRecord, NOTIFICATION_FILE_ADDED);
						file = ERPDataConverter.getFile(files);
					}
				}
			} 
			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			file.setStatus(ERROR_IN_PROCESSING);
		} finally {
			CommonUtils.closeSession(session);
		}
		return file;

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
			//Files
			List<ERPProjectFiles> files = erpProjectDAO.getRecordFiles(rec.getId(), session);
			if(CollectionUtils.isNotEmpty(files)) {
				for(ERPProjectFiles projectFile: files) {
					ERPFile file = ERPDataConverter.getFile(projectFile);
					if(file == null) {
						continue;
					}
					record.getFiles().add(file);
				}
			}
			//Comments
			List<ERPProjectComments> comments = erpProjectDAO.getRecordComments(rec.getId(), session);
			if(CollectionUtils.isNotEmpty(comments)) {
				for(ERPProjectComments cm: comments) {
					ERPComment comment = ERPDataConverter.getComment(cm);
					if(comment == null) {
						continue;
					}
					record.getComments().add(comment);
				}
			}
			//Logs
			List<ERPProjectLog> logs = erpProjectDAO.getRecordLogs(rec.getId(), session);
			if(CollectionUtils.isNotEmpty(logs)) {
				for(ERPProjectLog log: logs) {
					ERPLog erpLog = ERPDataConverter.getLog(log);
					if(erpLog == null) {
						continue;
					}
					record.getLogs().add(erpLog);
				}
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return record;
	}

	
	public InputStream getFile(ERPFile file) {
		if(file.getId() == null) {
			return null;
		}
		Session session = null;
		InputStream is = null;
		try {
			session = this.sessionFactory.openSession();
			ERPProjectDAO erpProjectDAO = new ERPProjectDAO();
			ERPProjectFiles files = erpProjectDAO.getRecordFile(file.getId(), session);
			if(files != null) {
				file.setFileName(files.getFileName() + "." + files.getFileType());
				is = new FileInputStream(files.getFilePath());
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return is;
	}
	
	public ERPComment updateComment(ERPUser user) {
		if (user == null || StringUtils.isEmpty(user.getEmail()) || user.getCurrentRecord() == null
				|| user.getCurrentRecord().getComment() == null) {
			ERPComment comment = new ERPComment();
			comment.setStatus(ERROR_INVALID_USER_DETAILS);
			return comment;
		}
		ERPRecord currentRecord = user.getCurrentRecord();
		Session session = null;
		ERPComment comment = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPProjectDAO erpProjectDAO = new ERPProjectDAO();
			ERPLoginDetails loginDetails = new ERPUserDAO().getLoginDetails(user.getEmail(), session);
			if (currentRecord.getId() != null) {

				ERPProjectRecords records = erpProjectDAO.getRecordById(currentRecord.getId(), session);
				if (records == null) {
					ERPComment response = new ERPComment();
					response.setStatus(ERROR_INVALID_USER_DETAILS);
				} else {
					comment = currentRecord.getComment();
					ERPProjectComments comments = null;
					if (comment.getId() != null) {
						comments = erpProjectDAO.getRecordComment(comment.getId(), session);
						comments.setStatus(USER_STATUS_DELETED);
						ERPProjectLog deleteLog = ProjectLogUtil.commentDeleteLog(session, loginDetails, comments);
						ProjectLogUtil.sendLog(deleteLog, session, executor, records, loginDetails, currentRecord, NOTIFICATION_COMMENT_DELETED);
					} else {
						comments = ERPBusinessConverter.getERPProjectComments(loginDetails, records, comment);
						session.persist(comments);
						ERPProjectLog addedLog = ProjectLogUtil.commentCreateLog(session, loginDetails, comments);
						ProjectLogUtil.sendLog(addedLog, session, executor, records, loginDetails, currentRecord, NOTIFICATION_COMMENT_ADDED);
						comment = ERPDataConverter.getComment(comments);
					}
					comment.setStatus(RESPONSE_OK);
				}
			}
			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			comment.setStatus(ERROR_IN_PROCESSING);
		} finally {
			CommonUtils.closeSession(session);
		}
		return comment;

	}

	public String updateMailSettings(ERPUser user) {
		if (user == null || user.getCurrentProject() == null || user.getCurrentProject().getMailConfig() == null) {
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
					ERPProjectUsers users = new ERPProjectDAO().getProjectUser(projects.getId(), loginDetails.getId(), session);
					if(users != null) {
						StringBuilder builder = new StringBuilder();
						if(currentProject.getMailConfig().isAssignedToMails()) {
							builder.append(PROJECT_MAIL_ASSIGNED_RECORDS);
						}
						if(currentProject.getMailConfig().isCreatedByMails()) {
							builder.append(PROJECT_MAIL_CREATED_RECORDS);
						}
						if(currentProject.getMailConfig().isAllMails()) {
							builder.append(PROJECT_MAIL_ALL_RECORDS);
						}
						users.setEmailSettings(builder.toString());
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

	public ERPUser getMailSettings(ERPUser user) {
		if (user == null || user.getCurrentProject() == null) {
			return null;
		}
		ERPProject currentProject = user.getCurrentProject();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			ERPLoginDetails loginDetails = new ERPUserDAO().getLoginDetails(user.getEmail(), session);
			if (currentProject.getId() != null) {
				ERPProjects projects = new ERPProjectDAO().getProjectById(currentProject.getId(), session);
				if (projects != null) {
					ERPMailConfig config = new ERPMailConfig();
					ERPProjectUsers users = new ERPProjectDAO().getProjectUser(projects.getId(), loginDetails.getId(), session);
					if(users != null) {
						if(StringUtils.contains(users.getEmailSettings(), PROJECT_MAIL_ALL_RECORDS)) {
							config.setAllMails(true);
						}
						if(StringUtils.contains(users.getEmailSettings(), PROJECT_MAIL_ASSIGNED_RECORDS)) {
							config.setAssignedToMails(true);
						}
						if(StringUtils.contains(users.getEmailSettings(), PROJECT_MAIL_CREATED_RECORDS)) {
							config.setCreatedByMails(true);
						}
					}
					currentProject.setMailConfig(config);
				} 
			} 
			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return user;
	}


}
