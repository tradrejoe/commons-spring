package org.lc.comm.ftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;

@Service
public class FTPService {

	public FTPService() {
	}

	public static interface IFTPServiceConsumer {
		public String exec(FTPClient ftp) throws Exception;
	}
	
	public String connect(String server, String username, String password, String directory, IFTPServiceConsumer consumer) 
			throws RuntimeException {
	    FTPClient ftp = new FTPClient();
	    //FTPClientConfig config = new FTPClientConfig();
	    //config.setXXX(YYY); // change required options
	    //ftp.configure(config );
	    try {
	      int reply;
	      ftp.connect(server);
	      // After connection attempt, you should check the reply code to verify
	      // success.
	      reply = ftp.getReplyCode();
	      if(!FTPReply.isPositiveCompletion(reply)) {
	        ftp.disconnect();
	        throw new RuntimeException("Cannot connect to ftp server, reply code: " + reply);
	      }
	      ftp.login(username, password);
          ftp.changeWorkingDirectory(directory + (directory.endsWith(File.separator) ? "" : File.separator));//Constants.directory);  
          ftp.setFileType(FTP.BINARY_FILE_TYPE);	      
	      if (consumer!=null) {
	    	  String strcon = consumer.exec(ftp);
	    	  if (strcon!=null) return strcon;
	      }
	      //boolean completed = ftp.completePendingCommand();
	      return null;
	    } catch(IOException e) {
	    	e.printStackTrace();
	    	throw new RuntimeException("Cannot upload file, error: " + e.getMessage() + " Reply string: " + ftp.getReplyString());
	    } catch(Exception ex) {
	    	ex.printStackTrace();
	    	throw new RuntimeException("Cannot upload file, error: " + ex.getMessage() + " Reply string: " + ftp.getReplyString());
	    } finally {
	      if(ftp.isConnected()) {
	        try {
		          ftp.logout();
		        } catch(IOException ioe) {}
	        try {
	          ftp.disconnect();
	        } catch(IOException ioe) {}
	      }
	    }		
	}
}
