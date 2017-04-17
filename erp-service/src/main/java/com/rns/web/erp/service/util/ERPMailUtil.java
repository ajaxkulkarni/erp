package com.rns.web.erp.service.util;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.rns.web.erp.service.bo.domain.ERPUser;

public class ERPMailUtil implements Runnable, ERPConstants {

	private static final String READ_RECEIPT_MAIL = "talnoterns@gmail.com";
	private static final String MAIL_ID = "contact@hreasy.in";
	private static final String MAIL_AUTH = "true";
	private static final String MAIL_HOST = "mail.hreasy.in";
	//private static final String MAIL_HOST = "smtp.zoho.com";
	private static final String MAIL_PORT = "25";
	private static final String MAIL_PASSWORD = "contact2017";

	private String type;
	private ERPUser user;
	private String messageText;
	private String mailSubject;
	
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
		// props.put("mail.smtp.starttls.enable", "true");
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
			if (StringUtils.isNotBlank(messageText)) {
				result = StringUtils.replace(result, "{message}", messageText);
			}
			message.setContent(result, "text/html");
			if(StringUtils.contains(type, "Admin")) {
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getEmails(Arrays.asList(ADMIN_MAILS))));
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


	private static Map<String, String> MAIL_TEMPLATES = Collections.unmodifiableMap(new HashMap<String, String>() {
		{
			put(MAIL_TYPE_SUBSCRIPTION, "subscription_mail.html");
			put(MAIL_TYPE_SUBSCRIPTION_ADMIN, "subscription_mail_admin.html");
			put(MAIL_TYPE_PASSWORD_SENT, "password_sent_mail.html");
			put(MAIL_TYPE_PASSWORD_CHANGED, "password_changed_mail.html");
			put(MAIL_TYPE_FORGOT_PASSWORD, "forgot_password.html");
		}
	});

	private static Map<String, String> MAIL_SUBJECTS = Collections.unmodifiableMap(new HashMap<String, String>() {
		{
			put(MAIL_TYPE_SUBSCRIPTION, "Thank you for subscribing!");
			put(MAIL_TYPE_SUBSCRIPTION_ADMIN, "New Subscription!");
			put(MAIL_TYPE_PASSWORD_SENT, "Your account is now active!");
			put(MAIL_TYPE_PASSWORD_CHANGED, "Your password is changed!");
			put(MAIL_TYPE_FORGOT_PASSWORD, "Temporary password for HREasy login");
		}
	});

}

