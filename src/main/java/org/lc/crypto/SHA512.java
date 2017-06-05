package org.lc.crypto;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SHA512 extends GenericCryptoUtil {

	  private static final Logger logger = Logger.getLogger(SHA512.class.getName());

	  public static synchronized String digest(String value) throws Exception
	  {
	    GenericCryptoUtil.setCurrentDigest(GenericCryptoUtil.DIGEST_TYPE.SHA512);	    
	    return GenericCryptoUtil.digest(buffer.toByteArray());
	  }

	  public static void main(String[] args) {
		  try {
			  System.out.println(SHA512.digest("KmhUnWR1MYWDYW4xuqdF5nbm+CXIyOVt"));
		  } catch(Exception e) {
			  e.printStackTrace();
		  }
	  }
}
