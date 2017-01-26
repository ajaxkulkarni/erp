package com.rns.web.erp.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.rns.web.erp.service.bo.api.ERPUserBo;
import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.domain.ERPServiceRequest;
import com.rns.web.erp.service.domain.ERPServiceResponse;
import com.rns.web.erp.service.util.CommonUtils;
import com.rns.web.erp.service.util.ERPConstants;
import com.rns.web.erp.service.util.LoggingUtil;

@Component
@Path("/service")
public class ERPUserController {

	@Autowired(required = true)
	@Qualifier(value = "userBo")
	ERPUserBo userBo;
	
	public void setUserBo(ERPUserBo userBo) {
		this.userBo = userBo;
	}
	
	public ERPUserBo getUserBo() {
		return userBo;
	}
	
	@POST
	@Path("/subscribeUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse searchSkill(ERPServiceRequest request) {
		LoggingUtil.logObject("Subscribe Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, (userBo.subscribeUser(request.getUser())));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Subscribe Response :", response);
		return response;
	}
	
	@POST
	@Path("/loginUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse loginUser(ERPServiceRequest request) {
		LoggingUtil.logObject("Login Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, (userBo.loginUser(request.getUser())));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Login Response :", response);
		return response;
	}
	
	@POST
	@Path("/getUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse getUser(ERPServiceRequest request) {
		LoggingUtil.logObject("Get user Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			response.setUser(userBo.populateUser(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Get user Response :", response);
		return response;
	}
	
	@POST
	@Path("/addCompany")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse addCompany(ERPServiceRequest request) {
		LoggingUtil.logObject("Get user Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			ERPCompany company = request.getUser().getCompany();
			company.setCreatedBy(request.getUser());
			CommonUtils.setResponse(response, userBo.addCompany(company));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Get user Response :", response);
		return response;
	}
	
	@POST
	@Path("/addEmployee")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse addEmployee(ERPServiceRequest request) {
		LoggingUtil.logObject("Add employee Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, userBo.addEmployee(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Add employee Response :", response);
		return response;
	}
	
	@POST
	@Path("/getAllEmployees")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse allEmployees(ERPServiceRequest request) {
		LoggingUtil.logObject("All employee Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			List<ERPUser> allEmployees = userBo.getAllEmployees(request.getUser().getCompany());
			response.setUser(request.getUser());
			response.getUser().getCompany().setEmployees(allEmployees);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("All employee Response :", response);
		return response;
	}
	
	@POST
	@Path("/getAllLeaveTypes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse allLeaveTypes(ERPServiceRequest request) {
		LoggingUtil.logObject("All company leave types Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			ERPCompany company = request.getUser().getCompany();
			company.setLeaveTypes(userBo.getAllLeaveTypes(company, request.getRequestType()));
			response.setCompany(company);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("All company leave types Response :", response);
		return response;
	}
	
	@POST
	@Path("/applyLeave")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse applyLeave(ERPServiceRequest request) {
		LoggingUtil.logObject("Apply leave Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, userBo.applyLeave(request.getLeave()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Apply leave Response :", response);
		return response;
	}
	
	@POST
	@Path("/getAllLeaves")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse getAllLeaves(ERPServiceRequest request) {
		LoggingUtil.logObject("All employee leaves Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			ERPCompany company = request.getUser().getCompany();
			List<ERPUser> employees = userBo.getAllEmployeeLeaveData(company);
			company.setEmployees(employees);
			company.setLeaveTypes(userBo.getAllLeaveTypes(company, request.getRequestType()));
			response.setCompany(company);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("All employee leaves Response :", response);
		return response;
	}
	
	@POST
	@Path("/getEmployeeLeaves")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse getEmployeeLeaves(ERPServiceRequest request) {
		LoggingUtil.logObject("employee leaves Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			ERPUser user = userBo.getEmployeeLeaveData(request.getUser());
			response.setUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("employee leaves Response :", response);
		return response;
	}
	
	@POST
	@Path("/updateLeave")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse updateLeave(ERPServiceRequest request) {
		LoggingUtil.logObject("update leave Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			 CommonUtils.setResponse(response, userBo.updateLeave(request.getLeave()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("update leaves Response :", response);
		return response;
	}
	
	@POST
	@Path("/addLeavePolicy")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse addLeavePolicy(ERPServiceRequest request) {
		LoggingUtil.logObject("add leave policy Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			 CommonUtils.setResponse(response, userBo.addLeavePolicy(request.getUser().getCompany()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("add leave policy Response :", response);
		return response;
	}
	
	@POST
	@Path("/changePassword")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse changePassword(ERPServiceRequest request) {
		LoggingUtil.logObject("Change password Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, userBo.changePassword(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Change password Response :", response);
		return response;
	}
	
	
}
