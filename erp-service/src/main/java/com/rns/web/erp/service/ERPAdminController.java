package com.rns.web.erp.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.rns.web.erp.service.bo.api.ERPAdminBo;
import com.rns.web.erp.service.bo.api.ERPUserBo;
import com.rns.web.erp.service.domain.ERPServiceRequest;
import com.rns.web.erp.service.domain.ERPServiceResponse;
import com.rns.web.erp.service.util.CommonUtils;
import com.rns.web.erp.service.util.ERPConstants;
import com.rns.web.erp.service.util.LoggingUtil;

@Component
@Path("/adminService")
public class ERPAdminController {

	@Autowired(required = true)
	@Qualifier(value = "userBo")
	ERPUserBo userBo;
	
	public void setUserBo(ERPUserBo userBo) {
		this.userBo = userBo;
	}
	
	public ERPUserBo getUserBo() {
		return userBo;
	}
	
	@Autowired(required = true)
	@Qualifier(value = "adminBo")
	ERPAdminBo adminBo;
	
	public ERPAdminBo getAdminBo() {
		return adminBo;
	}
	public void setAdminBo(ERPAdminBo adminBo) {
		this.adminBo = adminBo;
	}
	
	@POST
	@Path("/adminGetAllUsers")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse searchSkill(ERPServiceRequest request) {
		LoggingUtil.logObject("Get all users Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			response.setUsers(adminBo.getUsersByStatus(null));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		if(CollectionUtils.isNotEmpty(response.getUsers())) {
			LoggingUtil.logMessage("Users retrieved .. " + response.getUsers().size());
		} else {
			LoggingUtil.logMessage("No users retrieved ..");
		}
		return response;
	}
	
	@POST
	@Path("/activateUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse activateUser(ERPServiceRequest request) {
		LoggingUtil.logObject("Activate Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, adminBo.activateUser(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Activate Response :", response);
		return response;
	}
	
}
