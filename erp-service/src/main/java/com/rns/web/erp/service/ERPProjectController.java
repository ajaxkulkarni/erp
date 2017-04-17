package com.rns.web.erp.service;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.rns.web.erp.service.bo.api.ERPProjectBo;
import com.rns.web.erp.service.bo.api.ERPUserBo;
import com.rns.web.erp.service.bo.domain.ERPProject;
import com.rns.web.erp.service.bo.domain.ERPRecord;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.domain.ERPServiceRequest;
import com.rns.web.erp.service.domain.ERPServiceResponse;
import com.rns.web.erp.service.util.CommonUtils;
import com.rns.web.erp.service.util.ERPConstants;
import com.rns.web.erp.service.util.LoggingUtil;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Component
@Path("/projectService")
public class ERPProjectController {

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
	@Qualifier(value = "projectBo")
	ERPProjectBo projectBo;
	
	public ERPProjectBo getProjectBo() {
		return projectBo;
	}
	
	public void setProjectBo(ERPProjectBo projectBo) {
		this.projectBo = projectBo;
	}
	
	@POST
	@Path("/getAllCompanyLogins")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse getAllCompanyLogins(ERPServiceRequest request) {
		LoggingUtil.logObject("Get all logins Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			response.setUsers(projectBo.getAllCompanyLogins(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Get all logins response", response);
		return response;
	}
	
	@POST
	@Path("/getAllUserProjects")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse getAllUsersProjects(ERPServiceRequest request) {
		LoggingUtil.logObject("Get user projects Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			response.setProjects(projectBo.getAllUserProjects(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Get user projects response", response);
		return response;
	}
	
	
	@POST
	@Path("/getProject")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse getProject(ERPServiceRequest request) {
		LoggingUtil.logObject("Get project Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			ERPProject project = projectBo.getProject(request.getUser(), "REC");
			request.getUser().setCurrentProject(project);
			response.setUser(request.getUser());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Get project response", response);
		return response;
	}
	
	@POST
	@Path("/createProject")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse activateUser(ERPServiceRequest request) {
		LoggingUtil.logObject("Create project Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, projectBo.updateProject(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Create project Response :", response);
		return response;
	}
	
	@POST
	@Path("/updateProjectStructure")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse updateProjectStructure(ERPServiceRequest request) {
		LoggingUtil.logObject("Update project structure Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, projectBo.updateProjectStructure(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Update project structure Response :", response);
		return response;
	}
	
	@POST
	@Path("/updateRecord")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse updateRecord(ERPServiceRequest request) {
		LoggingUtil.logObject("Update record Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, projectBo.updateRecord(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Update record Response :", response);
		return response;
	}
	
	@POST
	@Path("/getRecord")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse getRecord(ERPServiceRequest request) {
		LoggingUtil.logObject("Get record Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			ERPRecord record = projectBo.getRecord(request.getUser());
			request.getUser().setCurrentRecord(record);
			response.setUser(request.getUser());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Get record Response :", response);
		return response;
	}
	
	@POST
	@Path("/register")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public ERPServiceResponse uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail,
		@FormDataParam("user") String user) {
		LoggingUtil.logObject("Register request:", user);
		ObjectMapper mapper = new ObjectMapper();
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			ERPUser chatUser = mapper.readValue(user, ERPUser.class);
			
			if(fileDetail != null) {
				//chatUser.setProfilePic(fileDetail.getFileName());
			}
			//CommonUtils.setResponse(response, userBo.register(chatUser));
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		return response;

	}
}