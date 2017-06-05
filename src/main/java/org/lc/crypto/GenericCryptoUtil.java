package org.lc.crypto;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public abstract class GenericCryptoUtil {

	  public enum DIGEST_TYPE {
		  MD5(0, "MD5"),
		  SHA512(1, "SHA-512");
		  int type;
		  String name;
		  DIGEST_TYPE(int type, String name) {
			  this.type = type;
			  this.name = name;
		  }
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	  }
	  	  
	  static final char[] BINARY2HEX
	    = new char[]{'0', '1', '2', '3', '4', '5', '6', '7',
	                 '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	  protected static ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	  protected static OutputStreamWriter writer = new OutputStreamWriter(buffer);
	  protected static StringBuffer hexBuffer = new StringBuffer(32);
	  protected static Map<String, MessageDigest> digestMap = new HashMap<String, MessageDigest>();
	  protected static MessageDigest digest = null;
	  protected static Logger logger = Logger.getLogger(GenericCryptoUtil.class);
	  
	  static {
		  Security.addProvider(new BouncyCastleProvider());
		  try {
			  digestMap.put(DIGEST_TYPE.MD5.getName(), MessageDigest.getInstance("MD5"));
		  } catch(Exception e) {
			  logger.error("Cannot initialize MD5 service.");
		  }
		  try {
			  digestMap.put(DIGEST_TYPE.SHA512.getName(), MessageDigest.getInstance("SHA-512"));
		  } catch(Exception e) {
			  logger.error("Cannot initialize SHA-512 service.");
		  }		  
	  }

	  protected static synchronized void setCurrentDigest(DIGEST_TYPE type) {
		  digest = digestMap.get(type.getName());
	  }
	  
	  public static void byteArrayToHex(StringBuffer buffer, byte[] bytes) {
	    for (int i = 0; i < bytes.length; i++) {
	      int hi = (bytes[i] >> 4) & 0x0f;
	      int lo = bytes[i] & 0x0f;
	      buffer.append(BINARY2HEX[hi]);
	      buffer.append(BINARY2HEX[lo]);
	    }
	  }
	  
	  public static synchronized String digest(byte[] bytes)
	    throws Exception
	  {    
	    byte[] digest = GenericCryptoUtil.digestToBytes(bytes);

	    hexBuffer.setLength(0);
	    byteArrayToHex(hexBuffer, digest);
	    return hexBuffer.toString();
	  }

	  public static synchronized String digest(String value)
	    throws Exception
	  {
	    writer.flush();
	    buffer.reset();

	    writer.write(value);
	    writer.flush();
	    
	    return digest(buffer.toByteArray());
	  }
	  
	  public static synchronized String digest(StringBuffer buf)
	    throws Exception
	  {
	    return digest(buf.toString());
	  }	
	  
	  public static synchronized byte[] digestToBytes(byte[] bytes)
	    throws Exception {
	    digest.reset();
	    digest.update(bytes);

	    return digest.digest();
	  }
	  
	  
}
