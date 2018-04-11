package com.rns.web.erp.service.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Session;

import com.rns.web.erp.service.bo.domain.ERPNotification;
import com.rns.web.erp.service.dao.domain.ERPUserFcmTokens;
import com.rns.web.erp.service.dao.impl.ERPUserDAO;


public class ERPNotificationUtil {

	//private static final String APP_KEY = "key=AAAACMqZO2Y:APA91bFvKzS7o72_2hf-zA5jqc9hKfCUQcT-qvo_p3lsrKNGYrN6QmLKWdbqe48dwoSD53kn7Q2d-jnlDvjtZyM57vmgkU1vI_ZERkkkEVpZzDm3VzgunyGzPDa1pB1LbD0AGGBFdgNA";
	private static final String APP_KEY = "key=AAAACPufexU:APA91bFY_4O65vERiu1Ccm4K6TBLP6gUR1MEzm5NQzH5mvFigd4u__xM3Ka4AcxUqqg3YQy8Wm4qWQi5L22HZNyzcwNt8nuzUcAeXy_CCJBduV6dRz1QEWiQ_XEIEu2XWXt79FErTSl6";
	private static final String POST_URL = "https://fcm.googleapis.com/fcm/send";
	private static final String POST_PARAMS = "{ \"notification\": { \"title\": \"{message}\", \"body\": \"{description}\"}, \"to\" : \"{regId}\"}";
	
	private ERPMailUtil mailUtil;

	public static void sendNotification(ERPNotification notification) throws MalformedURLException, IOException, ProtocolException {
		URL obj = new URL(POST_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", APP_KEY);


		// For POST only - START
		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		String postString = new String(POST_PARAMS);
		postString = StringUtils.replace(postString, "{regId}", notification.getTo());
		postString = StringUtils.replace(postString, "{message}", notification.getTitle());
		postString = StringUtils.replace(postString, "{description}", notification.getMessage());
		System.out.println(postString);
		os.write(postString.getBytes());
		os.flush();
		os.close();
		// For POST only - END

		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} else {
			System.out.println("POST request not worked");
		}
	}
	
	/*public static void sendUserNotification(Integer userId, Session session, String message, String description) throws MalformedURLException, ProtocolException, IOException {
		String trimmedMessage = StringUtils.trimToEmpty(message);
		if(StringUtils.isBlank(trimmedMessage)) {
			return;
		}
		List<NGODbGCMRegistration> userDevices = new NgoUserDAO().getUserDevices(session, userId);
		if(CollectionUtils.isEmpty(userDevices)) {
			return;
		}
		NGONotification notification = new NGONotification();
		notification.setMessage(message);
		notification.setDescription(description);
		for(NGODbGCMRegistration device: userDevices) {
			notification.setDeviceId(device.getDeviceId());
			sendNotification(notification);
		}
		NGODbNotifications notifications = NGOBusinessConverters.getNotifications(userId, notification, "USER");
		session.persist(notifications);
		
	}


	public static void sendRegistrationNotification(Integer regId, Session session, String message, String description) throws MalformedURLException, ProtocolException, IOException {
		String trimmedMessage = StringUtils.trimToEmpty(message);
		if(StringUtils.isBlank(trimmedMessage)) {
			return;
		}
		List<NGODbGCMRegistration> userDevices = new NgoUserDAO().getUserDevicesByRegistration(session, regId);
		if(CollectionUtils.isEmpty(userDevices)) {
			return;
		}
		for(NGODbGCMRegistration device: userDevices) {
			NGONotification notification = new NGONotification();
			notification.setDeviceId(device.getDeviceId());
			notification.setMessage(message);
			notification.setDescription(description);
			sendNotification(notification);
		}
		
	}*/
	
	public static void sendNotifications(ERPNotification notification, List<String> emails, Session session) {
		try {
			List<ERPUserFcmTokens> tokens = new ERPUserDAO().getFcmTokens(emails, session);
			if(CollectionUtils.isNotEmpty(tokens)) {
				for(ERPUserFcmTokens token: tokens) {
					notification.setTo(token.getFcmToken());
					sendNotification(notification);
					LoggingUtil.logMessage("Sent notification to - " + token.getFcmToken());
				}
			}
		} catch (Exception e) {
			LoggingUtil.logError(ExceptionUtils.getStackTrace(e));
		}
	}

	public ERPMailUtil getMailUtil() {
		return mailUtil;
	}

	public void setMailUtil(ERPMailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public static void main(String[] args) throws MalformedURLException, ProtocolException, IOException {
		ERPNotification notification = new ERPNotification();
		notification.setMessage("Testing from the code..");
		notification.setTitle("Ajinkya is Testing");
		notification.setTo("c4UYpN7gWF4:APA91bEf6ijm4tX8OuH3SrqpM9eSHx722Cwxnx8dVB9LVyK3evGFiHhP3VjxosEWQ5o29jJPVisisSGHZ_Wb2Si5BxnfccnJf-FPPZUzkXhlpFtpWI_nttyTx3ujJGwhh0i_fAF07D0S");
		sendNotification(notification);
	}
	
}
