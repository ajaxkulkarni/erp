package com.rns.web.erp.service.util;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class LoggingUtil {
	
	private static Logger reportLogger = Logger.getLogger(LoggingUtil.class);
	
	private static Logger errorLogger = Logger.getLogger(LoggingUtil.class);
	
	//private static Logger emailLogger = Logger.getLogger("email");
	
	public static void logMessage(String message) {
		reportLogger.info(message);
	}
	
	public static void logError(String message) {
		errorLogger.error(message);
	}
	
	public static void logObject(String message,Object object) {
		try {
			logMessage(message);
			logMessage(new ObjectMapper().writer().writeValueAsString(object));
		} catch (JsonGenerationException e1) {
		} catch (JsonMappingException e1) {
		} catch (IOException e1) {
		}
	}

}
