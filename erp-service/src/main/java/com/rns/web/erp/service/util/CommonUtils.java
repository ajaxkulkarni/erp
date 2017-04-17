package com.rns.web.erp.service.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.rns.web.erp.service.bo.domain.ERPSalaryInfo;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.bo.domain.ERPUserExperience;
import com.rns.web.erp.service.domain.ERPServiceResponse;

public class CommonUtils {
	
	public static void closeSession(Session session) {
		if(session == null || !session.isOpen())  {
			return;
		}
		session.close();
		//System.out.println("Session closed!");
	}

	public static String convertDate(Date date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").format(date);
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String readFile(String contentPath) throws FileNotFoundException {
		File file = getFile(contentPath);
		Scanner scanner = new Scanner(file);
		StringBuilder result = new StringBuilder();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			result.append(line).append("\n");
		}

		scanner.close();
		return result.toString();
	}

	public static File getFile(String contentPath) {
		ClassLoader classLoader = new CommonUtils().getClass().getClassLoader();
		URL resource = classLoader.getResource(contentPath);
		File file = new File(resource.getFile());
		return file;
	}
	
	
	public static String getStringValue(String value) {
		return StringUtils.isNotEmpty(value) ? value : "";
	}


	public static String getFileName(String filePath) {
		String[] tokens = StringUtils.split(filePath, ".");
		if(tokens == null || tokens.length == 0) {
			return null;
		}
		return tokens[tokens.length - 1];
	}

	public static ERPServiceResponse initResponse() {
		ERPServiceResponse response = new ERPServiceResponse();
		okResponse(response);
		return response;
	}

	private static void okResponse(ERPServiceResponse response) {
		response.setStatus(200);
		response.setResponseText(ERPConstants.RESPONSE_OK);
	}
	
	public static ERPServiceResponse setResponse(ERPServiceResponse response, String status) {
		if(StringUtils.equals(ERPConstants.RESPONSE_OK, status)) {
			okResponse(response);
		} else {
			response.setStatus(-111);
			response.setResponseText(status);
		}
		return response;
	}

	public static String generatePassword(ERPUser user) {
		return user.getEmail().substring(0, 1)  + new Random().nextInt(10000);
	}
	
	
	public static boolean isAmountPresent(BigDecimal amount) {
		if(amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
			return true;
		}
		return false;
	}

	public static Date getFirstDate(Integer year, Integer month) {
		if(year == null || month == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 1);
		return cal.getTime();
	}
	
	public static Date getLastDate(Integer year, Integer month) {
		if(year == null || month == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	public static Integer getCalendarValue(Date date1, int value) {
		if(date1 == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		return cal.get(value);
	}

	public static String getStringValue(Integer value) {
		if(value == null) {
			return "";
		}
		return value.toString();
	}

	public static String getStringValue(BigDecimal value) {
		if(value == null) {
			return "";
		}
		return value.toString();
	}
	
	public static String getUserExperience(List<ERPUserExperience> exp) {
		if(CollectionUtils.isEmpty(exp)) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for(ERPUserExperience experience:exp) {
			builder.append(StringUtils.remove(StringUtils.remove(experience.getCompanyName(), ","), ":")).append(":")
					.append(experience.getFromYear()).append(":")
					.append(experience.getToYear()).append(":")
					.append(experience.getDesignation()).append(":")
					.append(experience.getSpecialization()).append(",");
		}
		return StringUtils.removeEnd(builder.toString(), ",");
	}
	
	public static List<ERPUserExperience> getUserExperiences(String expString) {
		if(StringUtils.isBlank(expString)) {
			return null;
		}
		List<ERPUserExperience> experiences = new ArrayList<ERPUserExperience>();
		String[] experienceArray = StringUtils.split(expString, ",");
		for(String exp: experienceArray) {
			String[] values = StringUtils.split(exp, ":");
			if(values != null && values.length > 2) {
				ERPUserExperience experience = new ERPUserExperience();
				experience.setCompanyName(values[0]);
				experience.setFromYear(values[1]);
				experience.setToYear(values[2]);
				experience.setDesignation(values[3]);
				experience.setSpecialization(values[4]);
				experiences.add(experience);
				
			}
		}
		return experiences;
	}

	public static String getSalaryInfoString(List<ERPSalaryInfo> salaryInfos) {
		if(CollectionUtils.isEmpty(salaryInfos)) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		for(ERPSalaryInfo salaryInfo: salaryInfos) {
			buffer.append(salaryInfo.getRule()).append(":").append(salaryInfo.getAmount()).append(",");
		}
		return StringUtils.removeEnd(buffer.toString(), ",");
	}
	
	public static List<ERPSalaryInfo> getSalaryInfos(String salaryInfos) {
		if(StringUtils.isBlank(salaryInfos)) {
			return null;
		}
		List<ERPSalaryInfo> salaryInfoList = new ArrayList<ERPSalaryInfo>();
		String[] salaryInfoArray = StringUtils.split(salaryInfos, ",");
		for(String sal: salaryInfoArray) {
			String[] values = StringUtils.split(sal, ":");
			if(values != null && values.length > 1) {
				if(StringUtils.equalsIgnoreCase(values[0], "Basic")) {
					continue;
				}
				ERPSalaryInfo salaryInfo = new ERPSalaryInfo();
				salaryInfo.setRule(values[0]);
				salaryInfo.setAmount(new BigDecimal(values[1]));
				salaryInfoList.add(salaryInfo);
			}
		}
		return salaryInfoList;
	}

	public static ERPSalaryInfo getSalaryInfo(String salaryInfos, String string) {
		if(StringUtils.isBlank(salaryInfos)) {
			return null;
		}
		String[] salaryInfoArray = StringUtils.split(salaryInfos, ",");
		for(String sal: salaryInfoArray) {
			String[] values = StringUtils.split(sal, ":");
			if(values != null && values.length > 1) {
				if(StringUtils.equalsIgnoreCase(values[0], "Basic")) {
					ERPSalaryInfo salaryInfo = new ERPSalaryInfo();
					salaryInfo.setRule(values[0]);
					salaryInfo.setAmount(new BigDecimal(values[1]));
					return salaryInfo;
				}
			}
		}
		return null;
	}

	public static BigDecimal calculateTotal(List<ERPSalaryInfo> salaryInfos) {
		if(CollectionUtils.isEmpty(salaryInfos)) {
			return BigDecimal.ZERO;
		}
		BigDecimal total = BigDecimal.ZERO;
		for(ERPSalaryInfo salaryInfo:salaryInfos) {
			if(salaryInfo.getAmount() != null) {
				total= total.add(salaryInfo.getAmount());
			}
		}
		return total;
	}

	public static String getDate(Date date) {
		if(date == null) {
			return null;
		}
		try {
			return new SimpleDateFormat(ERPConstants.DATE_FORMAT).format(date);
		} catch (Exception e) {
			
		}
		return null;
	}
	
}
