package com.rns.web.erp.service.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.rns.web.erp.service.dao.domain.ERPLoginDetails;

public class ERPAdminDAO {
	
	public List<ERPLoginDetails> getAllUsers(String status, Session session) {
		String sql = "from ERPLoginDetails";
		if(StringUtils.isNotBlank(status)) {
			sql = sql + " where status=:status";
		}
		Query query = session.createQuery(sql);
		if(StringUtils.contains(sql, "status")) {
			query.setString("status", status);
		}
		return query.list();
	}

}
