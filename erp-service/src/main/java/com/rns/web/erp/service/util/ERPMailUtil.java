package com.rns.web.erp.service.util;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.rns.web.erp.service.bo.domain.ERPLog;
import com.rns.web.erp.service.bo.domain.ERPRecord;
import com.rns.web.erp.service.bo.domain.ERPUser;
import com.rns.web.erp.service.dao.domain.ERPLoginDetails;

public class ERPMailUtil implements Runnable, ERPConstants {

	private static final String READ_RECEIPT_MAIL = "talnoterns@gmail.com";
	
	private static final String MAIL_HOST = "mail.hreasy.in";
	private static final String MAIL_ID = "contact@hreasy.in";
	private static final String MAIL_PASSWORD = "contact2017";
	
	private static final String MAIL_AUTH = "true";
	//private static final String MAIL_HOST = "smtp.zoho.com";
	private static final String MAIL_PORT = "587";
	

	private String type;
	private ERPUser user;
	private List<String> users;
	private String messageText;
	private String mailSubject;
	private ERPRecord record;
	private List<ERPRecord> records;
	
	public void setRecords(List<ERPRecord> records) {
		this.records = records;
	}
	
	public void setUser(ERPUser user) {
		this.user = user;
	}

	public ERPMailUtil(String mailType) {
		this.type = mailType;
	}

	public ERPMailUtil() {
		
	}

	public void sendMail() {

		Session session = prepareMailSession();

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(MAIL_ID, "HR Easy"));
			prepareMailContent(message);
			Transport.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private static Session prepareMailSession() {
		Properties props = new Properties();

		props.put("mail.smtp.auth", MAIL_AUTH);
		//props.put("mail.smtp.socketFactory.port", "465"); //PROD
		//props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //PROD
		//props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", MAIL_HOST);
		props.put("mail.smtp.port", MAIL_PORT);
		

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(MAIL_ID, MAIL_PASSWORD);
			}
		});
		return session;
	}

	private String prepareMailContent(Message message) {

		try {
			//boolean attachCv = false;
			String result = readMailContent(message);
			if (user != null) {
				result = StringUtils.replace(result, "{name}", CommonUtils.getStringValue(user.getName()));
				result = StringUtils.replace(result, "{password}", CommonUtils.getStringValue(user.getPassword()));
				result = StringUtils.replace(result, "{email}", CommonUtils.getStringValue(user.getEmail()));
				result = StringUtils.replace(result, "{phone}", CommonUtils.getStringValue(user.getPhone()));
				if(user.getCompany() != null) {
					result = StringUtils.replace(result, "{company}", CommonUtils.getStringValue(user.getCompany().getName()));
				}
			}
			if (CollectionUtils.isNotEmpty(records)) {
				StringBuilder builder = new StringBuilder();
				int count = 1;
				for (ERPRecord rec : records) {
					builder.append("<tr>")
					.append("<td>").append(count++).append("</td>")
					.append("<td>").append(rec.getTitleField().getValue()).append("</td>")
					.append("<td>").append(rec.getProjectName()).append("</td>")
					.append("<td>").append(rec.getAssignedUser() != null ? rec.getAssignedUser().getName() : "").append("</td>")
					.append("</tr>");
				}
				result = StringUtils.replace(result, "{body}", builder.toString());
			} else {
				result = StringUtils.replace(result, "{body}", "");
			}
			if(record != null && CollectionUtils.isNotEmpty(record.getLogs())) {
				result = StringUtils.replace(result, "{recordTitle}", record.getTitleField().getValue());
				result = StringUtils.replace(result, "{recordType}", record.getStatus());
				result = StringUtils.replace(result, "{projectId}", CommonUtils.getStringValue(record.getProjectId()));
				if(CollectionUtils.isNotEmpty(record.getLogs())) {
					String logMessage = "";
					for(ERPLog log: record.getLogs()) {
						logMessage = logMessage + "<p>" + log.getLog() + "</p>";
					}
					result = StringUtils.replace(result, "{recordLog}", logMessage);
				} else {
					result = StringUtils.replace(result, "{recordLog}", "");
				}
				if(StringUtils.isNotBlank(mailSubject)) {
					message.setSubject(mailSubject);
				}
			}
			if (StringUtils.isNotBlank(messageText)) {
				result = StringUtils.replace(result, "{message}", messageText);
			}
			//message.setContent(result, "text/html");
			message.setContent(result, "text/html; charset=utf-8");
			if(StringUtils.contains(type, "Admin")) {
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getEmails(Arrays.asList(ADMIN_MAILS))));
			} else if(CollectionUtils.isNotEmpty(users)) {
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("talnoterns@gmail.com"));
				message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(getEmails(users)));
			} else {
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
			}
			return result;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		return "";
	}


	private String getEmails(List<String> users) {
		if (CollectionUtils.isEmpty(users)) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (String user : users) {
			if (StringUtils.isEmpty(user)) {
				continue;
			}
			builder.append(user).append(",");
		}
		return StringUtils.removeEnd(builder.toString(), ",");
	}

	public void run() {
		sendMail();
	}

	/*private String prepareActivationMailContent() {
		StringBuilder builder = new StringBuilder();
		builder.append(ROOT_URL_ACTIVATION).append("#?").append(ACTIVATION_URL_VAR).append("=").append(candidate.getActivationCode()).append("&").append(ACTIVATION_USER_VAR).append("=")
				.append(candidate.getEmail());
		return builder.toString();
	}*/
	

	private String readMailContent(Message message) throws FileNotFoundException, MessagingException {
		String contentPath = "";
		contentPath = "email/" + MAIL_TEMPLATES.get(type);
		message.setSubject(MAIL_SUBJECTS.get(type));
		return CommonUtils.readFile(contentPath);
	}

	/*private void attachCv(Message message, Candidate candidate, String result) throws MessagingException, IOException {
		Multipart mp = new MimeMultipart();
		BodyPart fileBody = new MimeBodyPart();
		DataSource source = new FileDataSource(candidate.getResume());
		fileBody.setDataHandler(new DataHandler(source));
		fileBody.setFileName(candidate.getFilePath());
		BodyPart messsageBody = new MimeBodyPart();
		messsageBody.setText(result);
		messsageBody.setContent(result, "text/html");
		mp.addBodyPart(fileBody);
		mp.addBodyPart(messsageBody);
		message.setContent(mp);
	}*/


	public ERPRecord getRecord() {
		return record;
	}

	public void setRecord(ERPRecord record) {
		this.record = record;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	private static Map<String, String> MAIL_TEMPLATES = Collections.unmodifiableMap(new HashMap<String, String>() {
		{
			put(MAIL_TYPE_SUBSCRIPTION, "subscription_mail.html");
			put(MAIL_TYPE_SUBSCRIPTION_ADMIN, "subscription_mail_admin.html");
			put(MAIL_TYPE_PASSWORD_SENT, "password_sent_mail.html");
			put(MAIL_TYPE_PASSWORD_CHANGED, "password_changed_mail.html");
			put(MAIL_TYPE_FORGOT_PASSWORD, "forgot_password.html");
			put(MAIL_TYPE_RECORD_CHANGED, "record_update.html");
			put(MAIL_TYPE_FOLLOW_UP, "follow_up_mail.html");
		}
	});

	private static Map<String, String> MAIL_SUBJECTS = Collections.unmodifiableMap(new HashMap<String, String>() {
		{
			put(MAIL_TYPE_SUBSCRIPTION, "Thank you for subscribing!");
			put(MAIL_TYPE_SUBSCRIPTION_ADMIN, "New Subscription!");
			put(MAIL_TYPE_PASSWORD_SENT, "Your account is now active!");
			put(MAIL_TYPE_PASSWORD_CHANGED, "Your password is changed!");
			put(MAIL_TYPE_FORGOT_PASSWORD, "Temporary password for HREasy login");
			put(MAIL_TYPE_RECORD_CHANGED, "Project Name | Record name");
			put(MAIL_TYPE_FOLLOW_UP, "HR Easy | Follow up");
		}
	});

}

