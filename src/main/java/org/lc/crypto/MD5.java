package org.lc.crypto;


import java.io.*;
import java.security.*;
import java.util.logging.*;

public class MD5 extends GenericCryptoUtil {
  
	private static final Logger logger = Logger.getLogger(MD5.class.getName());

	  public static synchronized String digest(String value) throws Exception
	  {
	    GenericCryptoUtil.setCurrentDigest(GenericCryptoUtil.DIGEST_TYPE.MD5);	    
	    return GenericCryptoUtil.digest(buffer.toByteArray());
	  }

}

