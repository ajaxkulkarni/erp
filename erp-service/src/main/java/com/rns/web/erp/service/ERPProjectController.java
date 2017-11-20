package com.rns.web.erp.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.rns.web.erp.service.bo.api.ERPProjectBo;
import com.rns.web.erp.service.bo.api.ERPUserBo;
import com.rns.web.erp.service.bo.domain.ERPComment;
import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPFile;
import com.rns.web.erp.service.bo.domain.ERPFilter;
import com.rns.web.erp.service.bo.domain.ERPProject;
import com.rns.web.erp.service.bo.domain.ERPRecord;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.domain.ERPServiceRequest;
import com.rns.web.erp.service.domain.ERPServiceResponse;
import com.rns.web.erp.service.util.CommonUtils;
import com.rns.web.erp.service.util.ERPConstants;
import com.rns.web.erp.service.util.ERPExcelUtil;
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
			if(StringUtils.equals("Assigned", request.getRequestType())) {
				ERPProject project = new ERPProject();
				project.setId(0);
				project.setRecords(projectBo.getUserAssignedRecords(request.getUser(), request.getTimeRange()));
				request.getUser().setCurrentProject(project);
			} else if (StringUtils.equals("Archived", request.getRequestType())) {
				ERPProject project = new ERPProject();
				project.setId(0);
				project.setRecords(projectBo.getArchivedRecords(request.getUser(), request.getTimeRange()));
				request.getUser().setCurrentProject(project);
			} else {
				ERPProject project = projectBo.getProject(request.getUser(), "REC", request.getTimeRange());
				request.getUser().setCurrentProject(project);
			}
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
	@Path("/uploadFile")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public ERPServiceResponse uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail,
		@FormDataParam("user") String user) {
		LoggingUtil.logObject("File Upload request:", user);
		ObjectMapper mapper = new ObjectMapper();
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			String result = ERPConstants.ERROR_IN_PROCESSING;
			ERPUser erpUser = mapper.readValue(user, ERPUser.class);
			if(fileDetail != null && erpUser.getCurrentRecord() != null && erpUser.getCurrentRecord().getFile() != null ) {
				ERPFile file = erpUser.getCurrentRecord().getFile();
				file.setFileData(uploadedInputStream);
				file.setFileSize(new BigDecimal((fileDetail.getSize())));
				file.setFileType(fileDetail.getType());
				file.setFilePath(fileDetail.getFileName());
				erpUser.getCurrentRecord().setFile(file);
				ERPFile responseFile = projectBo.updateFile(erpUser);
				result = responseFile.getStatus();
				CommonUtils.setResponse(response, result);
				erpUser.getCurrentRecord().setFile(responseFile);
				response.setUser(erpUser);
			}
			
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	@POST
	@Path("/deleteFile")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse deleteFile(ERPServiceRequest request) {
		LoggingUtil.logObject("Update file record Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			ERPFile file = projectBo.updateFile(request.getUser());
			CommonUtils.setResponse(response, file.getStatus());
			request.getUser().getCurrentRecord().setFile(file);
			response.setUser(request.getUser());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Update file record Response :", response);
		return response;
	}
	
	@GET
	@Path("/getFile/{fileId}")
	@Produces(MediaType.MULTIPART_FORM_DATA)
	public Response getImage(@PathParam("fileId") Integer fileId) {
		LoggingUtil.logObject("File request:", fileId);
		try {
			ERPFile file = new ERPFile();
			file.setId(fileId);
			InputStream is = projectBo.getFile(file);
			ResponseBuilder response = Response.ok(is);
			response.header("Content-Disposition","filename=" + file.getFileName());  
			return response.build();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@POST
	@Path("/updateComment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse updateComment(ERPServiceRequest request) {
		LoggingUtil.logObject("Update comment Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			ERPComment responseComment = projectBo.updateComment(request.getUser());
			CommonUtils.setResponse(response, responseComment.getStatus());
			request.getUser().getCurrentRecord().setComment(responseComment);
			response.setUser(request.getUser());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Update comment Response :", response);
		return response;
	}
	
	@POST
	@Path("/updateMailSettings")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse updateMailSettings(ERPServiceRequest request) {
		LoggingUtil.logObject("Update mail settings Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			
			CommonUtils.setResponse(response, projectBo.updateMailSettings(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Update mail settings Response :", response);
		return response;
	}
	
	@POST
	@Path("/getMailSettings")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse getMailSettings(ERPServiceRequest request) {
		LoggingUtil.logObject("Get mail settings Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			response.setUser(projectBo.getMailSettings(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Get mail settings response", response);
		return response;
	}
	
	@POST
	@Path("/downloadProjectData")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/vnd.ms-excel")
	public Response downloadLeavesMaster(ERPProject project) {
		LoggingUtil.logMessage("Download project request for :" + project.getId());
		try {
			HSSFWorkbook wb = ERPExcelUtil.getProjectDetails(project);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			wb.write(os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			ResponseBuilder response = Response.ok(is);
			response.header("Content-Disposition","attachment; filename=" + project.getTitle() + ".xls");  
			return response.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//LoggingUtil.logObject("Download resume Response :", response);
		return Response.serverError().build();

	}
	
}
