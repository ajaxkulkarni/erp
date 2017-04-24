package com.rns.web.erp.service.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;

import com.rns.web.erp.service.bo.domain.ERPRecord;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.dao.domain.ERPLoginDetails;
import com.rns.web.erp.service.dao.domain.ERPProjectFields;
import com.rns.web.erp.service.dao.domain.ERPProjectLog;
import com.rns.web.erp.service.dao.domain.ERPProjectRecordValues;
import com.rns.web.erp.service.dao.domain.ERPProjectRecords;
import com.rns.web.erp.service.dao.domain.ERPProjectUsers;
import com.rns.web.erp.service.dao.domain.ERPProjects;

public class ProjectLogUtil {

	private static final String ACTION_DELETE_ITEM = "DeleteItem";
	private static final String ACTION_NEW_ITEM = "NewItem";
	private static final String ACTION_CHANGE_ITEM = "ChangeItem";

	public static void projectCreateLog(Session session, ERPLoginDetails loginDetails, ERPProjects projects) {
		String msg = loginDetails.getName() + " created the project '" + projects.getTitle() + "'";
		ERPProjectLog log = createNewLog(loginDetails, msg, projects, ACTION_NEW_ITEM);
		session.persist(log);
	}

	public static void addProjectUsersLog(List<ERPUser> users, ERPProjects projects, Session session, ERPLoginDetails loginDetails) {
		if (CollectionUtils.isEmpty(users)) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(projects.getCreatedBy().getName()).append(" has added ");
		StringBuilder usersList = new StringBuilder();
		for (ERPUser user : users) {
			usersList.append(user.getName()).append(",");
		}
		builder.append(StringUtils.removeEnd(",", usersList.toString()));
		builder.append(" to the project '").append(projects.getTitle()).append("'");

		ERPProjectLog log = createNewLog(loginDetails, builder.toString(), projects, ACTION_NEW_ITEM);
		session.persist(log);
	}

	public static void projectUsersDeletedLog(List<ERPProjectUsers> deletedUsers, ERPLoginDetails loginDetails, Session session) {
		if (CollectionUtils.isEmpty(deletedUsers)) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(loginDetails.getName()).append(" has deleted ");
		ERPProjects project = prepareUsersList(deletedUsers, builder);
		if (project != null) {
			builder.append(" from the project '").append(project.getTitle()).append("'");
		}

		ERPProjectLog log = createNewLog(loginDetails, builder.toString(), project, ACTION_DELETE_ITEM);
		session.persist(log);
	}

	public static void projectUsersAddedLog(List<ERPProjectUsers> addedUsers, ERPLoginDetails loginDetails, Session session) {
		if (CollectionUtils.isEmpty(addedUsers)) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(loginDetails.getName()).append(" has added ");
		ERPProjects project = prepareUsersList(addedUsers, builder);
		if (project != null) {
			builder.append(" to the project '").append(project.getTitle()).append("'");
		}

		ERPProjectLog log = createNewLog(loginDetails, builder.toString(), project, ACTION_NEW_ITEM);
		session.persist(log);
	}

	private static ERPProjects prepareUsersList(List<ERPProjectUsers> deletedUsers, StringBuilder builder) {
		StringBuilder usersList = new StringBuilder();
		for (ERPProjectUsers user : deletedUsers) {
			if (user.getUser() == null) {
				continue;
			}
			usersList.append(user.getUser().getName()).append(",");
		}
		builder.append(StringUtils.removeEnd(usersList.toString(), ","));
		ERPProjects project = deletedUsers.get(0).getProject();
		return project;
	}

	public static void projectDeletedFieldsLog(List<ERPProjectFields> deletedFields, ERPLoginDetails loginDetails, Session session) {
		if (CollectionUtils.isEmpty(deletedFields) || loginDetails == null) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(loginDetails.getName()).append(" has deleted fields ");
		prepareFieldList(deletedFields, builder);
		ERPProjects project = deletedFields.get(0).getProject();
		builder.append(" from the project " + project.getTitle());

		ERPProjectLog log = createNewLog(loginDetails, builder.toString(), project, ACTION_DELETE_ITEM);
		session.persist(log);
	}

	public static void projectAddedFieldsLog(List<ERPProjectFields> addedFields, ERPLoginDetails loginDetails, Session session) {
		if (CollectionUtils.isEmpty(addedFields) || loginDetails == null) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(loginDetails.getName()).append(" has added fields ");
		prepareFieldList(addedFields, builder);
		ERPProjects project = addedFields.get(0).getProject();
		builder.append(" to the project " + project.getTitle());

		ERPProjectLog log = createNewLog(loginDetails, builder.toString(), project, ACTION_NEW_ITEM);
		session.persist(log);
	}

	private static ERPProjectLog createNewLog(ERPLoginDetails loginDetails, String msg, ERPProjects project, String type) {
		ERPProjectLog log = new ERPProjectLog();
		log.setAction(type);
		log.setCreatedBy(loginDetails);
		log.setProject(project);
		log.setCreatedDate(new Date());
		log.setLog(msg);
		return log;
	}

	private static void prepareFieldList(List<ERPProjectFields> deletedFields, StringBuilder builder) {
		StringBuilder fieldsList = new StringBuilder();
		for (ERPProjectFields fields : deletedFields) {
			fieldsList.append(fields.getName()).append(",");
		}
		builder.append(StringUtils.removeEnd(fieldsList.toString(), ","));
	}

	public static void projectFieldChangeLog(String changeLog, ERPLoginDetails loginDetails, Session session, ERPProjects projects) {
		String[] logs = StringUtils.split(changeLog, "||");
		if (logs == null || logs.length < 2) {
			return;
		}
		String addedFields = logs[0];
		String changedFields = logs[1];
		if (StringUtils.isNotBlank(changedFields)) {
			String msg = loginDetails.getName() + " has changed the fields " + StringUtils.removeEnd(changedFields, ",") + " of the project "
					+ projects.getTitle();
			ERPProjectLog log = createNewLog(loginDetails, msg, projects, ACTION_CHANGE_ITEM);
			session.persist(log);
		}

		if (StringUtils.isNotBlank(addedFields)) {
			String msg = loginDetails.getName() + " has added the fields " + StringUtils.removeEnd(addedFields, ",") + " to the project " + projects.getTitle();
			ERPProjectLog log = createNewLog(loginDetails, msg, projects, ACTION_NEW_ITEM);
			session.persist(log);
		}
	}

	public static ERPProjectLog createRecordChangeLog(ERPProjectRecords records, ERPRecord currentRecord, ERPLoginDetails loginDetails) {
		if (records == null || currentRecord == null) {
			return null;
		}
		String msg = null;
		if(currentRecord.getId() == null) {
			msg = loginDetails.getName() + " has created a new record '" + currentRecord.getTitleField().getValue() + "'";
			msg = msg + " for the date " + CommonUtils.getDate(currentRecord.getRecordDate());
		} else if (StringUtils.equals(currentRecord.getStatus(), ERPConstants.USER_STATUS_DELETED)) {
			msg = loginDetails.getName() + " deleted the record '" + currentRecord.getTitleField().getName() + "'";
		} else if (!DateUtils.isSameDay(currentRecord.getRecordDate(), records.getRecordDate())) {
			msg = loginDetails.getName() + " changed the record date from " + CommonUtils.getDate(records.getRecordDate()) + " to "
					+ CommonUtils.getDate(currentRecord.getRecordDate()) + " of the record '" + currentRecord.getTitleField().getName() + "'";
		}
		if (StringUtils.isBlank(msg)) {
			return null;
		}
		ERPProjectLog recordChange = createNewLog(loginDetails, msg, records.getProject(), ACTION_CHANGE_ITEM);
		return recordChange;
	}

	public static void projectAddedValuesLog(List<ERPProjectRecordValues> addedValues, Session session, ERPLoginDetails loginDetails, ERPRecord currentRecord) {
		if (CollectionUtils.isEmpty(addedValues) || currentRecord == null) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(loginDetails.getName()).append(" has added values - ");
		ERPProjectRecords records = addedValues.get(0).getRecord();
		ERPProjects projects = records.getProject();
		prepareValuesList(addedValues, builder);
		builder.append(" to the record ").append("'").append(currentRecord.getTitleField().getName()).append("'").append(" of the project ").append("'").append(projects.getTitle()).append("'");
		ERPProjectLog addedValuesLog = createNewLog(loginDetails, builder.toString(), projects, ACTION_NEW_ITEM);
		addedValuesLog.setRecord(records);
		session.persist(addedValuesLog);
	}

	private static void prepareValuesList(List<ERPProjectRecordValues> addedValues, StringBuilder builder) {
		StringBuilder valueList = new StringBuilder();
		for (ERPProjectRecordValues value : addedValues) {
			valueList.append("'").append(CommonUtils.getStringValue(value.getField().getName())).append("'").append(" as ").append("'").append(value.getValue()).append("'").append(",");
		}
		builder.append(StringUtils.removeEnd(valueList.toString(), ","));
	}

	public static void projectChangedValuesLog(List<ERPProjectRecordValues> changedValues, Session session, ERPLoginDetails loginDetails, ERPRecord currentRecord) {
		if (CollectionUtils.isEmpty(changedValues) || currentRecord == null) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(loginDetails.getName()).append(" has changed values - ");
		prepareValuesList(changedValues, builder);
		ERPProjectRecords records = changedValues.get(0).getRecord();
		ERPProjects project = records.getProject();
		builder.append(" of the record ").append("'").append(currentRecord.getTitleField().getName()).append("'").append(" of the project ").append("'").append(project.getTitle()).append("'");
		ERPProjectLog addedValuesLog = createNewLog(loginDetails, builder.toString(), project, ACTION_NEW_ITEM);
		addedValuesLog.setRecord(records);
		session.persist(addedValuesLog);
	}

}
