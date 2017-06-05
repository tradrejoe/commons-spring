package org.lc.comm.mail;

import java.io.InputStream;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.apache.log4j.Logger;
import org.lc.comm.mail.sendgrid.SendGrid;
import org.lc.comm.mail.sendgrid.SendGrid.Email;
import org.lc.comm.mail.sendgrid.SendGridException;
import org.springframework.stereotype.Service;


@Service
public class EmailSender {

	private String smtpServer = "email-smtp.us-east-1.amazonaws.com";
	private String port = "25";
	private String user = "AKIAJ2HP7WGCBVE5L7TQ";
	private String password = "ApfnOpbRN2TpdDpsK01ItisvKOeTSRLxzt54BLhZsdhT";
	private String auth = "true";
	private Logger logger = Logger.getLogger(EmailSender.class);
	private String sender = "support@pyfia.com";
	private String senderType = SmtpSetting.SENDGRID.toString();
	private SendGrid sendGrid = null;
	private SendGrid.Response sendGridResponse = null;
	
	public SendGrid.Response getSendGridResponse() {
		return sendGridResponse;
	}

	public void setSendGridResponse(SendGrid.Response sendGridResponse) {
		this.sendGridResponse = sendGridResponse;
	}

	public EmailSender() {
		SmtpSetting setting = SmtpSetting.valueOf(this.getSenderType());
		if (this.getSenderType()!=null && setting==SmtpSetting.SENDGRID) {
			this.setSendGrid(new SendGrid(setting.getUser(), setting.getPassword()));
		}
	}
	
	public SendGrid getSendGrid() {
		return sendGrid;
	}

	public void setSendGrid(SendGrid sendGrid) {
		this.sendGrid = sendGrid;
	}

	public String getSenderType() {
		return senderType;
	}

	public void setSenderType(String emailSender) {
		senderType = emailSender;
	}

	private enum SmtpSetting {
		GSMTP("smtp.gmail.com", "25", "tradrejoe", "orgy1112"),
		AWSSMTP("email-smtp.us-east-1.amazonaws.com", "25", "AKIAJ2HP7WGCBVE5L7TQ", "ApfnOpbRN2TpdDpsK01ItisvKOeTSRLxzt54BLhZsdhT"),
		PYFIASMTP("pyfia.com", "25", "ec2-user", "$n00pL10n"),
		PYFIALOCALSMTP("localhost", "25", "ec2-user", "$n00pL10n"),
		SENDGRID(null, null, "tradrejoe", "$n00pL10n");
		
		String host;
		String port;
		String user;
		String password;
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public String getPort() {
			return port;
		}
		public void setPort(String port) {
			this.port = port;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		private SmtpSetting(String host, String port, String user, String password) {
			this.host = host;
			this.port = port;
			this.user = user;
			this.password = password;
		}
		public Properties toProperty() {
			Properties props = new Properties();
			props.setProperty("mail.smtp.host", this.host);
			props.setProperty("mail.smtp.port", this.port);
			props.setProperty("mail.smtp.user", this.user);
			props.setProperty("mail.smtp.password", this.password);
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.smtp.starttls.enable", "true");
			return props;
		}
	};
	
	private Properties _prepareProperties() {
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", smtpServer);
		props.setProperty("mail.smtp.port", port);
		props.setProperty("mail.smtp.user", user);
		props.setProperty("mail.smtp.password", password);
		props.setProperty("mail.smtp.auth", auth);
		props.setProperty("mail.smtp.starttls.enable", "true");
        /*props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");*/
        
		return props;
	}
	
	private MimeMessage _prepareMessage(Session mailSession,String charset,
		String from, String subject,
		String HtmlMessage,String[] recipient, Object... args) {
		
		MimeMessage message = null;
		try {
			message = new MimeMessage(mailSession);
			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);
			for (int i=0;i<recipient.length;i++)
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient[i]));
			MimeMultipart mult = new MimeMultipart();
			MimeBodyPart bp = new MimeBodyPart();
			bp.setContent(HtmlMessage, "text/html; charset=\""+charset+"\"");
			mult.addBodyPart(bp);
			if (args!=null && args.length>0) {
				for (int a=0; a<args.length; a++) {
					if (args[a]!=null && args[a] instanceof String) {
						try {
							bp = new MimeBodyPart();
							String filename = args[a]+"";
							DataSource ds = new FileDataSource(filename);
							bp.setDataHandler(new DataHandler(ds));
							bp.setFileName(filename);
							mult.addBodyPart(bp);
						} catch(Exception e) {}
					}
				}
			}
			message.setContent(mult);
		} catch (Exception ex) {
			logger.error(ex);
		}
		return message;
	}
	
	public void _sendSmtp(String from, String subject,String HtmlMessage,String[] to, Object... args) {
		Transport transport = null;
		try {
			Properties props = SmtpSetting.PYFIALOCALSMTP.toProperty();
			Session mailSession = Session.getDefaultInstance(props, new SMTPAuthenticator(sender, password, true));
			transport =  mailSession.getTransport("smtp");
			MimeMessage message = _prepareMessage(mailSession, "UTF-8", from, subject, 
				"from: " + from + "<br>" + HtmlMessage, to, args);
			transport.connect();
			Transport.send(message);
		} catch (Exception ex) {
			logger.error(ex);
			throw new RuntimeException(ex);
		} finally{
			try {
				transport.close();
			} catch (MessagingException ex) {
				logger.error(ex);
			}
		}		
	}
	
	public void _sendGrid(String from, String subject,String HtmlMessage,String[] to, Object... args) {
		try {
			if (to==null||to.length==0)
				throw new SendGridException(new RuntimeException("No recipients."));
			Email email = new SendGrid.Email();
		    for (String recipient : to) {
		    	email.addTo(recipient);
		    }
		    String fromaddress = "from@mailinator.com";
		    email.setFrom(from);	
		    email.setSubject(subject);
		    email.setHtml(HtmlMessage);
		    if (args!=null && args.length>0 && args[0] instanceof Map) {
		    	try {
		    		Map<String, InputStream> am = (Map<String, InputStream>)args[0];
		    		for (String fn : am.keySet()) {
		    			email.addAttachment(fn, am.get(fn));
		    		}
		    	} catch(Exception e) {}
		    }
		    setSendGridResponse(sendGrid.send(email));
		} catch(Exception e) {
			logger.error(e);
		}
	}
	public void sendEmail(String from, String subject,String HtmlMessage,String[] to, Object... args) {
		
		if (SmtpSetting.valueOf(this.getSenderType())==SmtpSetting.SENDGRID) {
			_sendGrid(from, subject,HtmlMessage, to, args);
		} else {
			_sendSmtp(from, subject,HtmlMessage, to, args);
		}
	}
	
	public static class SMTPAuthenticator extends Authenticator{

		private String username;
		private String password;
		private boolean needAuth;

		public SMTPAuthenticator(String username, String password,boolean needAuth) {
			this.username = username;
			this.password = password;
			this.needAuth = needAuth;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			if (needAuth)
				return new PasswordAuthentication(username, password);
			else 
				return null;
		}
	}
	
	public static void main(String[] args) {
		EmailSender emailSender = new EmailSender();
		emailSender.sendEmail("test@pyfia.com", 
				"test subject 000", "<b>hello</b>", new String[]{"tradrejoe@gmail.com", "mgzheng@yahoo.com"});
		System.out.println(emailSender.getSendGridResponse());
	}
}