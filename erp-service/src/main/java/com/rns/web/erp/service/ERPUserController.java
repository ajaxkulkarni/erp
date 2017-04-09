package com.rns.web.erp.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.rns.web.erp.service.bo.api.ERPUserBo;
import com.rns.web.erp.service.bo.domain.ERPCompany;
import com.rns.web.erp.service.bo.domain.ERPFilter;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.domain.ERPServiceRequest;
import com.rns.web.erp.service.domain.ERPServiceResponse;
import com.rns.web.erp.service.util.CommonUtils;
import com.rns.web.erp.service.util.ERPConstants;
import com.rns.web.erp.service.util.ERPExcelUtil;
import com.rns.web.erp.service.util.ERPReportUtil;
import com.rns.web.erp.service.util.LoggingUtil;

@Component
@Path("/service")
public class ERPUserController {

	private static final String APPLICATION_PDF = "application/pdf";
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
	public ERPServiceResponse subscribe(ERPServiceRequest request) {
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
	@Path("/forgotPassword")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse forgotPassword(ERPServiceRequest request) {
		LoggingUtil.logObject("Forgot password Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, (userBo.forgotPassword(request.getUser())));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Forgot password Response :", response);
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
			response.setCompanyId(company.getId());
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
			String responseText = userBo.applyLeave(request.getLeave());
			CommonUtils.setResponse(response, responseText);
			if(ERPConstants.ERROR_LEAVES_EXCEEDED.equals(responseText)) {
				response.setStatus(-101);
			}
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
	
	@POST
	@Path("/addSalaryStructure")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse addSalaryStructure(ERPServiceRequest request) {
		LoggingUtil.logObject("add salary structure Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			 CommonUtils.setResponse(response, userBo.addSalaryStructure(request.getUser().getCompany()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("add salary structure Response :", response);
		return response;
	}
	
	@POST
	@Path("/getSalaryStructure")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse getSalaryStructure(ERPServiceRequest request) {
		LoggingUtil.logObject("Get salary structure Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			ERPCompany company = request.getUser().getCompany();
			company.setSalaryInfo(userBo.getSalaryInfo(company));
			response.setCompany(company);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Get salary structure Response :", response);
		return response;
	}
	
	@POST
	@Path("/addSalary")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse addSalary(ERPServiceRequest request) {
		LoggingUtil.logObject("Add Salary Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, userBo.updateSalary(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Add salary Response :", response);
		return response;
	}
	
	@POST
	@Path("/getAllEmployeeSalaryInfo")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse getAllSalaryInfo(ERPServiceRequest request) {
		LoggingUtil.logObject("Get all salary info Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			ERPCompany company = request.getUser().getCompany();
			company.setEmployees(userBo.getAllEmployeeSalarySlips(company));
			response.setCompany(company);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Get all salary info Response :", response);
		return response;
	}
	
	@GET
	@Path("/download/{companyId}/{employeeId}/{year}/{month}")
	//@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(APPLICATION_PDF)
	public Response downloadSalarySlip(@PathParam("companyId") Integer companyId, @PathParam("employeeId") Integer employeeId,@PathParam("year") Integer year,@PathParam("month") Integer month) {
		LoggingUtil.logMessage("Download Salary slip request for :" + employeeId);
		try {
			ERPUser employee = new ERPUser();
			employee.setId(employeeId);
			ERPCompany company = new ERPCompany();
			ERPFilter filter = new ERPFilter();
			filter.setYear(year);
			filter.setMonth(month);
			company.setFilter(filter);
			company.setId(companyId);
			employee.setCompany(company);
			InputStream is = userBo.downloadSalarySlip(employee);
			ResponseBuilder response = Response.ok(is);
			String fileName = company.getName() + "_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "_Salary_Statement.pdf";
			response.header("Content-Disposition","filename=" + fileName);  
			return response.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//LoggingUtil.logObject("Download resume Response :", response);
		return Response.serverError().build();

	}
	
	@GET
	@Path("/downloadSalaryMaster/{companyId}/{employeeId}/{year}/{month}")
	//@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/vnd.ms-excel")
	public Response downloadSalaryMaster(@PathParam("companyId") Integer companyId, @PathParam("employeeId") String employeeId,@PathParam("year") Integer year,@PathParam("month") Integer month) {
		LoggingUtil.logMessage("Download Master request for :" + companyId);
		try {
			/*ERPUser employee = new ERPUser();
			employee.setId(employeeId);*/
			ERPCompany company = new ERPCompany();
			ERPFilter filter = new ERPFilter();
			filter.setYear(year);
			filter.setMonth(month);
			company.setFilter(filter);
			company.setId(companyId);
			//employee.setCompany(company);
			company.setEmployees(extractEmployees(employeeId));
			List<ERPUser> employees = userBo.getAllEmployeeSalarySlips(company);
			HSSFWorkbook wb = ERPExcelUtil.generateEmployeeSalarySheet(employees);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			wb.write(os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			ResponseBuilder response = Response.ok(is);
			String fileName = company.getName() + "_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "_Financial_Master.xls";
			response.header("Content-Disposition","attachment; filename=" + fileName);  
			return response.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//LoggingUtil.logObject("Download resume Response :", response);
		return Response.serverError().build();

	}
	
	@GET
	@Path("/downloadBankStatement/{companyId}/{employeeId}/{year}/{month}")
	//@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(APPLICATION_PDF)
	public Response downloadBankStatement(@PathParam("companyId") Integer companyId, @PathParam("employeeId") String employeeId,@PathParam("year") Integer year,@PathParam("month") Integer month) {
		LoggingUtil.logMessage("Download bank statement request for :" + companyId);
		try {
			/*ERPUser employee = new ERPUser();
			employee.setId(employeeId);*/
			ERPCompany company = new ERPCompany();
			ERPFilter filter = new ERPFilter();
			filter.setYear(year);
			filter.setMonth(month);
			company.setFilter(filter);
			company.setId(companyId);
			List<ERPUser> filteredEmployees = extractEmployees(employeeId);
			company.setEmployees(filteredEmployees);
			//employee.setCompany(company);
			List<ERPUser> employees = userBo.getAllEmployeeSalarySlips(company);
			company.setEmployees(employees);
			InputStream is = ERPReportUtil.getBankStatement(company);
			ResponseBuilder response = Response.ok(is);
			String fileName = company.getName() + "_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "_Bank_Master.pdf";
			response.header("Content-Disposition","filename=" + fileName);  
			return response.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//LoggingUtil.logObject("Download resume Response :", response);
		return Response.serverError().build();

	}

	private List<ERPUser> extractEmployees(String employeeId) {
		if(StringUtils.isEmpty(StringUtils.trimToEmpty(employeeId))) {
			return null;
		}
		String[] employeeIds = StringUtils.split(employeeId,",");
		if(employeeIds == null || employeeIds.length == 0) {
			return null;
		}
		List<ERPUser> filteredEmployees = new ArrayList<ERPUser>();
		for(String id: employeeIds) {
			ERPUser user = new ERPUser();
			if(!StringUtils.isNumeric(id)) {
				continue;
			}
			user.setId(new Integer(id));
			filteredEmployees.add(user);
		}
		return filteredEmployees;
	}
	
	@GET
	@Path("/downloadLeavesMaster/{companyId}/{employeeId}/{year}/{month}")
	//@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/vnd.ms-excel")
	public Response downloadLeavesMaster(@PathParam("companyId") Integer companyId, @PathParam("employeeId") String employeeId,@PathParam("year") Integer year,@PathParam("month") Integer month) {
		LoggingUtil.logMessage("Download Leaves Master request for :" + companyId + " Employee:" + employeeId);
		try {
			ERPCompany company = new ERPCompany();
			ERPFilter filter = new ERPFilter();
			filter.setYear(year);
			filter.setMonth(month);
			if(month < 0 || month > 12) {
				filter.setMonth(null);
			}
			company.setFilter(filter);
			company.setId(companyId);
			//employee.setCompany(company);
			//company.setEmployees(extractEmployees(employeeId));
			List<ERPUser> employees = null;
			HSSFWorkbook wb = null;
			String fileName = "";
			String monthString = month > 12 ? "": month + "_";
			if(companyId != null && companyId != 0) {
				employees = userBo.getAllEmployeeLeaveData(company);
				wb = ERPExcelUtil.generateEmployeeLeavesSheet(employees);
				String companyName = "";
				if(CollectionUtils.isNotEmpty(employees) && employees.get(0).getCompany() != null) {
					companyName = employees.get(0).getCompany().getName() + "_";
				}
				fileName = companyName + monthString + year + "_Leaves_Master.xls";
			} else {
				ERPUser user = new ERPUser();
				user.setCompany(company);
				user = userBo.getEmployeeLeaveData(user);
				wb = ERPExcelUtil.generateEmployeeLeavesSheet(user);
				fileName = company.getName() + "_" + user.getName() + "_" + monthString + year + "_Leaves_Details.xls";
			}
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			wb.write(os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			ResponseBuilder response = Response.ok(is);
			response.header("Content-Disposition","attachment; filename=" + fileName);  
			return response.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//LoggingUtil.logObject("Download resume Response :", response);
		return Response.serverError().build();

	}
	
	@GET
	@Path("/downloadEmployeeProfile/{employeeId}/{name}")
	//@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(APPLICATION_PDF)
	public Response downloadEmployeeProfile(@PathParam("employeeId") Integer employeeId,@PathParam("name") String name) {
		LoggingUtil.logMessage("Download employee profile request for :" + employeeId);
		try {
			ERPUser employee = new ERPUser();
			employee.setId(employeeId);
			InputStream is = userBo.getEmployeeDocument(employee);
			ResponseBuilder response = Response.ok(is);
			String fileName = name + "_" +  "_Profile.pdf";
			response.header("Content-Disposition","filename=" + fileName);  
			return response.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//LoggingUtil.logObject("Download resume Response :", response);
		return Response.serverError().build();

	}
	
	@POST
	@Path("/updateLeaveBalance")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ERPServiceResponse updateLeaveBalance(ERPServiceRequest request) {
		LoggingUtil.logObject("Leave balance Request :", request);
		ERPServiceResponse response = CommonUtils.initResponse();
		try {
			CommonUtils.setResponse(response, userBo.updateEmployeeLeaveBalance(request.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERPConstants.ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Leave balance Response :", response);
		return response;
	}
	
	
}
