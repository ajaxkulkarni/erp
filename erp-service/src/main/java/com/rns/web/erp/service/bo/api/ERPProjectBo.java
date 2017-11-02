package com.rns.web.erp.service.bo.api;

import java.io.InputStream;
import java.util.List;

import com.rns.web.erp.service.bo.domain.ERPComment;
import com.rns.web.erp.service.bo.domain.ERPFile;
import com.rns.web.erp.service.bo.domain.ERPProject;
import com.rns.web.erp.service.bo.domain.ERPRecord;
import com.rns.web.erp.service.bo.domain.ERPUser;

public interface ERPProjectBo {

	String updateProject(ERPUser user);
	String updateProjectStructure(ERPUser user);
	List<ERPProject> getAllUserProjects(ERPUser user);
	ERPProject getProject(ERPUser user, String requestType, String timeRange);
	List<ERPUser> getAllCompanyLogins(ERPUser user);
	String updateRecord(ERPUser record);
	ERPRecord getRecord(ERPUser user);
	ERPFile updateFile(ERPUser user);
	InputStream getFile(ERPFile file);
	ERPComment updateComment(ERPUser user);
}
