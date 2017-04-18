package com.rns.web.erp.service.util;

public interface ERPConstants {
	
	String ROOT_PATH = "F:/Resoneuronance/HREasy/Documents/Data/";
	
	
	String USER_STATUS_CREATED = "C";
	String USER_STATUS_PASSWORD_SENT = "P";
	String USER_STATUS_ACTIVE = "A";
	String USER_STATUS_DELETED = "D";
	
	String FIELD_TYPE_TEXT = "TEXT";
	String FIELD_TYPE_TITLE = "TITLE";
	
	String USER_TYPE_OWNER = "Owner";
	String USER_TYPE_EMPLOYEE = "Employee";
	String AMOUNT_TYPE_PERCENTAGE = "percentage";
	String AMOUNT_TYPE_AMOUNT = "amount";
	String LEAVE_STATUS_APPLIED = "Applied";
	String LEAVE_STATUS_CANCELLED = "Cancelled";
	String LEAVE_TYPE_LOP = "Without Pay";
	String YES = "Y";
	
	String RESPONSE_OK = "OK";
	String DATE_FORMAT = "yyyy-MM-dd";
	
	String MAIL_TYPE_SUBSCRIPTION = "subscriptionMail";
	String MAIL_TYPE_SUBSCRIPTION_ADMIN = "subscriptionMailAdmin";
	String MAIL_TYPE_PASSWORD_SENT = "passwordSentMail";
	String MAIL_TYPE_PASSWORD_CHANGED = "passwordChanged";
	String MAIL_TYPE_FORGOT_PASSWORD = "forgotPassword";
	
	
	String ERROR_INVALID_USER_DETAILS = "Invalid User Details!";
	String ERROR_INVALID_LOGIN_DETAILS = "Invalid Login Details!";
	String ERROR_INACTIVE_PROFILE = "Your profile is not active. Please contact the support team to activate your profile.";
	String ERROR_EMAIL_EXISTS = "Email address already exists!";
	String ERROR_INVALID_LEAVE_DETAILS = "Invalid leave details";
	String ERROR_INVALID_COMPANY_DETAILS = "Invalid company details";
	String ERROR_IN_PROCESSING = "Error in processing..";
	String ERROR_INVALID_LEAVE_POLICY = "Invalid leave policy!";
	String ERROR_LEAVES_OVERLAP = "Applied leaves overlapping with existing leaves!";
	String ERROR_INCORRECT_OLD_PASSWORD = "Incorrect old password!";
	String ERROR_LEAVES_EXCEEDED = "The leaves entered exceed the maximum allowed limit. Any applied leave will be considered as without pay. Do you want to continue?";
	String ERROR_INCOMPLETE_BANK_DETAILS = "Please update the employee bank details first!";
	String ERROR_EMPLOYEE_ID_EXISTS = "Employee ID already Exists!";
	String ERROR_PROJECT_NOT_FOUND = "Project not found. Please check if the project is archived.";
	String ERROR_RECORD_NOT_FOUND = "Record not found!";
	
	
	
	String[] ADMIN_MAILS = {"ajinkyashiva@gmail.com"};
	
}
