package org.lc.comm.mail;
import java.util.Hashtable;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.lc.web.controller.BaseController;


public class Email implements java.io.Serializable, Runnable 
{
	
	private String fieldSmtpHost = "lacnte1"; //defautl
	private String fieldFromName = "";  //default;
	private String fieldFromAddress="";		
	private String fieldSubject = "";
	private String fieldBody = "";
	private String[] toAddresses;
    private String contentFormat="text/html";  // as defalut//text/plain
    private javax.mail.Message mail = null;
    Logger logger = Logger.getLogger(Email.class);
   
	public Email() 
	{
		super();
	}

  public String getContentFormat()
  {
    return contentFormat;
  }
	
	public String getBody() 
	{
		return fieldBody;
	}
	
	public String getFromAddress() 
	{
		return fieldFromAddress;
	}
	
	
	/**
	 * Gets the fromName property (String) value.
	 * @return The fromName property value.
	 * @see #setFromName
	 */
	
	public String getFromName() 
	{
		return fieldFromName;
	}
	
	
	/**
	 * Gets the subject property (String) value.
	 * @return The subject property value.
	 * @see #setSubject
	 */
	
	public String getSubject() 
	{
		return fieldSubject;
	}
	
	/**
	 * run method comment.
	 */
	public void run() 
	{
		try {
			Transport.send(mail);
		} 
		
		catch (javax.mail.MessagingException e) 
		{
			logger.error("Cannot send email" + (e.getMessage()!=null ? ", error: " + e.getMessage() : ""));
			logger.error(BaseController.getStackTrace(e));			
		}
	}
	
	/**
	 * Perform the send method.
	 */
	
	public void send() {
		
		try {
			InternetAddress from = new InternetAddress(getFromAddress(), getFromName());
			InternetAddress[] to = this.getBatchAddresses();
      
			Properties props = new Properties();
			props.put("mail.smtp.host", fieldSmtpHost);
	
			// Create a mail session
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(false);
	
			mail = new MimeMessage(session);
			mail.setFrom(from);
			mail.addRecipients(Message.RecipientType.TO, to);     
			mail.setSubject(getSubject());
			mail.setContent(getBody(), getContentFormat());
		} catch (Exception e) {
			logger.error("email connection has problem in email.java" + (e.getMessage()!=null ? ", error: " + e.getMessage() : ""));
			logger.error(BaseController.getStackTrace(e));
		}
	
		// Send the message asychronously
		(new Thread(this)).start();
	
		// To send it Synchronously
		// Transport.send(mail);
		
		return;
	}
	
	
	/**
	 * Sets the body property (String) value.
	 * @param body The new value for the property.
	 * @see #getBody
	 */
 
     public void setContentFormat(String format)
     {
        contentFormat=format;
        
     }
	public void setBody(String body) 
	{
		fieldBody = body;
	}
	
	
	/**
	 * Sets the fromAddress property (String) value.
	 * @param fromAddress The new value for the property.
	 * @see #getFromAddress
	 */
	
	public void setFromAddress(String fromAddress) 
	{
		fieldFromAddress = fromAddress;
	}
	
	
	/**
	 * Sets the fromName property (String) value.
	 * @param fromName The new value for the property.
	 * @see #getFromName
	 */
	
	public void setFromName(String fromName) 
	{
		fieldFromName = fromName;
	}
	
	
	/**
	 * Sets the smtpHost property (String) value.
	 * @param smtpHost The new value for the property.
	 */
	
	public void setSmtpHost(String smtpHost) 
	{
		fieldSmtpHost = smtpHost;
	}
	
	
	/**
	 * Sets the subject property (String) value.
	 * @param subject The new value for the property.
	 * @see #getSubject
	 */
	
	public void setSubject(String subject) 
	{
		fieldSubject = subject;
	}


	public void setToAddresses(String[] toAddresses){
		this.toAddresses = toAddresses;
	}
	

	private InternetAddress[] getBatchAddresses()throws Exception{
		InternetAddress[] toAddress = null;
		if(this.toAddresses != null){
			toAddress = new InternetAddress[this.toAddresses.length];
			for(int i = 0; i < this.toAddresses.length; i++){
				toAddress[i] = new InternetAddress(this.toAddresses[i]);
			}
		}
		return toAddress;
	}
	
	public String getStringBatchAddresses() {
		String toAddress = null;
		if(this.toAddresses != null && this.toAddresses.length>0){
			for(int i = 0; i < this.toAddresses.length-1; i++){
				toAddress = this.toAddresses[i]+",";
			}
			toAddress = (toAddress!=null?toAddress:"") + this.toAddresses[this.toAddresses.length-1];
		}
		return toAddress;
	}
	
  public static void main(String arg[])
  {
      Email email=new Email();      
      email.send();
     
  }
}
