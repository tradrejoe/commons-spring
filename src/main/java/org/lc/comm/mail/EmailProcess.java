package org.lc.comm.mail;

import java.io.*;

import javax.mail.*;
import javax.mail.internet.*;

//import javax.activation.*;
import java.util.Properties;
  
import java.io.InputStream;
import java.util.*;
import java.io.File;
import java.io.IOException;

import javax.naming.*;
import javax.naming.directory.*;
import javax.servlet.http.HttpSession;

public class EmailProcess
{

	private static String default_hostname = "mail.lacare.org";
	private String hostName = default_hostname;

	public void setHost(String host) {
		if (host!=null && !host.trim().equals(""))
			hostName = host;
	}
	
	public EmailProcess(String... hostName){
		if (hostName!=null && hostName.length>0 && !hostName[0].trim().equals(""))
			this.hostName = hostName[0];
    }

  public void sendMail(String fromEmail,String userName, String[] toAddresses, String subject, String msg) throws Exception {
	  if (hostName!=null && !hostName.trim().equals("")) {
		Email mail = new Email();
        mail.setSmtpHost(hostName);
		mail.setFromAddress(fromEmail);
		if (userName!=null && !userName.trim().equals("")) mail.setFromName(userName);		
        mail.setToAddresses(toAddresses);      
        mail.setSubject(subject); 
		mail.setBody(msg);		        
		mail.send();
		//NotificationDAO.getInstance().send(mail);
	  }
	}

  public void sendMailTest(String fromEmail,String userID) throws Exception {
		Email mail = new Email();
        mail.setSmtpHost(hostName);
        mail.setSubject("test subject"); 
		//mail.setSubject(createSubject(form.getPcfStatus()));            
		mail.setBody("test message");	        
        mail.setToAddresses(new String[]{"mzheng@lacare.org"});      
		mail.setFromAddress(fromEmail);
		mail.setFromName(userID);		
		mail.send();
	}  
  
public static void main(String[] args) {
	try {
		new EmailProcess().sendMail("test@lacare.org", "tester", new String[]{"mzheng@lacare.org"}, "test subject", "test msg line 1\ntest msg line2");
	} catch(Exception e) {}
}
}
 


