package org.lc.crypto;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.apache.commons.codec.binary.Base64;

public class RSAEncrypter {

	  static final String algorithm = "RSA";
	  static final String ENCRYPT = "encrypt";
	  static final String DECRYPT = "decrypt";
	  static final char[] password = "$n00pL10n".toCharArray();
	  static final String alias = "tomcat";
	  public static final String KEY_STORE_LOCATION = "key.store.location";
	  
	  protected static KeyPair getKeyPair() throws Exception {
		  KeyStore ks = getKeyStore();
		  PrivateKey prkey = (PrivateKey)ks.getKey(alias, password);
		  PublicKey pukey = ks.getCertificate(alias).getPublicKey();
		  return new KeyPair(pukey, prkey);
	  }
	  	  
	  protected static KeyStore getKeyStore() throws Exception {
		  String ksloc = System.getProperty(KEY_STORE_LOCATION);
		  KeyStore ks = KeyStore.getInstance("JKS");
		  FileInputStream is = new FileInputStream(new File(ksloc));
		  ks.load(is, password);
		  return ks;
	  }
	  
	   		public static byte[] encrypt(byte[] input) {
	   			try {
	   				return _crypto(Cipher.ENCRYPT_MODE, input);
	   			} catch(Exception e) {
	   				e.printStackTrace();
	   				return null;
	   			}
	   		}
	   		
	   		public static byte[] decrypt(byte[] input) {
	   			try {
	   				return _crypto(Cipher.DECRYPT_MODE, input);
	   			} catch(Exception e) {
	   				e.printStackTrace();
	   				return null;
	   			}
	   		}	   		
		   
	   		public static byte[] copyOfRange(byte[] src, int s, int e) {
	   			if (s>src.length-1 || e<=s) return new byte[0];
	   			byte[] out = new byte[src.length];
	   			for (int i=s; i<e && i<src.length; i++) {
	   				out[i] = src[i];
	   			}
	   			return out;
	   		}
	   		
		   private static byte[] _crypto(int mode, byte[] input) throws Exception {
				      Cipher cf = Cipher.getInstance(algorithm);
				      KeyPair kp = getKeyPair();
				      cf.init(mode, mode==Cipher.ENCRYPT_MODE ? kp.getPublic() : kp.getPrivate());				      
				      System.out.println();
				      System.out.println("Cipher Object Info: ");
				      System.out.println("Block Size = "+cf.getBlockSize());
				      System.out.println("Algorithm = "+cf.getAlgorithm());
				      System.out.println("Provider = "+cf.getProvider());
				      System.out.println("toString = "+cf.toString());
				      return cf.doFinal(input);
				   }		   

		   public static void main(String[] a) {
			      
		      String input = "hello world, rsa test!";
		      try {
		    	 System.out.println(String.format("encrypting %1$s", input)); 
		         byte[] enc = encrypt(input.getBytes());
		         System.out.println(String.format("encrypted: %1$s", new String(enc, "UTF-8")));
		         byte[] dec = decrypt(enc);
		         System.out.println(String.format("decrypted: %1$s", new String(dec, "UTF-8")));
		      } catch (Exception e) {
		      	 e.printStackTrace();
		         return;
		      }
		   }		   
}
