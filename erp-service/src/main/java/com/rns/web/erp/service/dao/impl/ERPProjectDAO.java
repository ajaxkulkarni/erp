package com.rns.web.erp.service.dao.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.rns.web.erp.service.dao.domain.ERPLoginDetails;
import com.rns.web.erp.service.dao.domain.ERPProjectComments;
import com.rns.web.erp.service.dao.domain.ERPProjectFields;
import com.rns.web.erp.service.dao.domain.ERPProjectFiles;
import com.rns.web.erp.service.dao.domain.ERPProjectLog;
import com.rns.web.erp.service.dao.domain.ERPProjectRecordValues;
import com.rns.web.erp.service.dao.domain.ERPProjectRecords;
import com.rns.web.erp.service.dao.domain.ERPProjectUsers;
import com.rns.web.erp.service.dao.domain.ERPProjects;
import com.rns.web.erp.service.util.ERPConstants;

public class ERPProjectDAO {

	public ERPProjects getProjectById(Integer id, Session session) {
		Query query = session.createQuery("from ERPProjects where id=:id AND status!=:deleted");
		query.setInteger("id", id);
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		List<ERPProjects> projects = query.list();
		if (CollectionUtils.isEmpty(projects)) {
			return null;
		}
		return projects.get(0);
	}

	public List<ERPProjectUsers> getProjectUsers(Integer projectId, Session session) {
		Query query = session.createQuery("from ERPProjectUsers where project.id=:id AND status!=:deleted");
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		query.setInteger("id", projectId);
		return query.list();
	}

	public List<ERPProjectUsers> getUserProjects(Integer userId, Session session) {
		Query query = session.createQuery("from ERPProjectUsers where user.id=:id AND status!=:deleted");
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		query.setInteger("id", userId);
		return query.list();
	}

	public List<ERPLoginDetails> getAllUsers(Integer id, Session session) {
		Query query = session.createQuery("from ERPLoginDetails where company.id=:id AND status!=:deleted");
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		query.setInteger("id", id);
		return query.list();
	}
	
	public List<ERPLoginDetails> getRemainingUsers(Integer id, Integer projectId, Session session) {
		Query query = session.createQuery("from ERPLoginDetails where company.id=:id AND status!=:deleted AND id NOT IN "
				+ "(select user.id from ERPProjectUsers where project.id=:projectId)");
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		query.setInteger("id", id);
		query.setInteger("projectId", projectId);
		return query.list();
	}

	public List<ERPProjectFields> getProjectFields(Integer id, Session session) {
		Query query = session.createQuery("from ERPProjectFields where project.id=:id AND status!=:deleted order by id");
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		query.setInteger("id", id);
		return query.list();
	}

	public ERPProjectFields getField(Integer id, Session session) {
		Query query = session.createQuery("from ERPProjectFields where id=:id AND status!=:deleted");
		query.setInteger("id", id);
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		List<ERPProjectFields> projects = query.list();
		if (CollectionUtils.isEmpty(projects)) {
			return null;
		}
		return projects.get(0);
	}

	public ERPProjectRecords getRecordById(Integer id, Session session) {
		Query query = session.createQuery("from ERPProjectRecords where id=:id AND status!=:deleted");
		query.setInteger("id", id);
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		List<ERPProjectRecords> records = query.list();
		if (CollectionUtils.isEmpty(records)) {
			return null;
		}
		return records.get(0);
	}

	public ERPProjectRecordValues getRecordValueById(Integer id, Session session) {
		Query query = session.createQuery("from ERPProjectRecordValues where id=:id");
		query.setInteger("id", id);
		List<ERPProjectRecordValues> records = query.list();
		if (CollectionUtils.isEmpty(records)) {
			return null;
		}
		return records.get(0);
	}

	public List<ERPProjectRecords> getProjectRecords(Integer id, Session session) {
		Query query = session.createQuery("from ERPProjectRecords where project.id=:id AND status!=:deleted order by recordDate DESC,createdDate DESC");
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		query.setInteger("id", id);
		return query.list();
	}

	public ERPProjectRecordValues getRecordValueByField(Integer id, Integer recordId, Session session) {
		Query query = session.createQuery("from ERPProjectRecordValues where field.id=:id AND record.id=:recordId");
		query.setInteger("id", id);
		query.setInteger("recordId", recordId);
		List<ERPProjectRecordValues> records = query.list();
		if (CollectionUtils.isEmpty(records)) {
			return null;
		}
		return records.get(0);
	}

	public List<ERPProjectFiles> getRecordFiles(Integer id, Session session) {
		Query query = session.createQuery("from ERPProjectFiles where record.id=:id AND status!=:deleted order by id DESC");
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		query.setInteger("id", id);
		return query.list();
	}
	
	public Number getRecordFileCount(Integer id, Session session) {
		Query query = session.createQuery("select count(*) from ERPProjectFiles where record.id=:id AND status!=:deleted");
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		query.setInteger("id", id);
		List list = query.list();
		if(CollectionUtils.isNotEmpty(list)) {
			return (Number) list.get(0);
		}
		return null;
	}

	public ERPProjectFiles getRecordFile(Integer id, Session session) {
		Query query = session.createQuery("from ERPProjectFiles where id=:id AND status!=:deleted");
		query.setInteger("id", id);
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		List<ERPProjectFiles> records = query.list();
		if (CollectionUtils.isEmpty(records)) {
			return null;
		}
		return records.get(0);
	}

	public ERPProjectComments getRecordComment(Integer id, Session session) {
		Query query = session.createQuery("from ERPProjectComments where id=:id AND status!=:deleted");
		query.setInteger("id", id);
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		List<ERPProjectComments> records = query.list();
		if (CollectionUtils.isEmpty(records)) {
			return null;
		}
		return records.get(0);
	}

	public List<ERPProjectComments> getRecordComments(Integer id, Session session) {
		Query query = session.createQuery("from ERPProjectComments where record.id=:id AND status!=:deleted order by id DESC");
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		query.setInteger("id", id);
		return query.list();
	}
	
	public Number getRecordCommentCount(Integer id, Session session) {
		Query query = session.createQuery("select count(*) from ERPProjectComments where record.id=:id AND status!=:deleted");
		query.setString("deleted", ERPConstants.USER_STATUS_DELETED);
		query.setInteger("id", id);
		List list = query.list();
		if(CollectionUtils.isNotEmpty(list)) {
			return (Number) list.get(0);
		}
		return null;
	}

	public List<ERPProjectLog> getRecordLogs(Integer id, Session session) {
		Query query = session.createQuery("from ERPProjectLog where record.id=:id order by id DESC");
		query.setInteger("id", id);
		query.setMaxResults(ERPConstants.MAX_LOG_RECORDS);
		return query.list();
	}

}
