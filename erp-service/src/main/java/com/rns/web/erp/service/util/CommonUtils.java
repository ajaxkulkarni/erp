package com.rns.web.erp.service.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.rns.web.erp.service.bo.domain.ERPUser;
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
	
	
}
