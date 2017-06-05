package org.lc.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESEncrypter {
	
    static IvParameterSpec iv = null;
    static SecretKey skey = null;
    
    public static String encrypt(String value) {
        try {
            if (iv==null) {
            	iv = genIVParamSpec();
            	System.out.println(String.format("AESEncrypter::encrypt(), generated iv spec %1$s", new String(iv.getIV())));
            }
            if (skey==null) {
            	skey = genSecretKey();
            	try {
	            	System.out.println(String.format("AESEncrypter::encrypt(), generated secret key, encoded: %1$s format: %2$s", 
	            			new String(skey.getEncoded(), skey.getFormat())));
            	} catch(Exception e) {}
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static String decrypt(String encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skey, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }    
    public static String encryptStringKeys(String key1, String key2, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2.getBytes("UTF-8"));

            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes("UTF-8"),
                    "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decryptStringKeys(String key1, String key2, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2.getBytes("UTF-8"));

            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes("UTF-8"),
                    "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
	 public static String EncryptNoIV(String plainText, String key) throws
	    NoSuchAlgorithmException,
	    NoSuchPaddingException,
	    InvalidKeyException,
	    IllegalBlockSizeException,
	    BadPaddingException,
	    UnsupportedEncodingException
	{
	    SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
	     
	    // Instantiate the cipher
	    Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.ENCRYPT_MODE, keySpec);
	     
	    byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
	     
	    return new Base64().encodeAsString(encryptedTextBytes);
	}
	 
	public static String DecryptNoIV(String encryptedText, String key) throws
	    NoSuchAlgorithmException,
	    NoSuchPaddingException,
	    InvalidKeyException,
	    IllegalBlockSizeException,
	    BadPaddingException,
	    UnsupportedEncodingException 
	{
	     
	    SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
	 
	    // Instantiate the cipher
	    Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.DECRYPT_MODE, keySpec);
	     
	    byte[] encryptedTextBytes = new Base64().decodeBase64(encryptedText);
	    byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
	     
	    return new String(decryptedTextBytes);
	}    

    public static IvParameterSpec genIVParamSpec() {
    	SecureRandom rnd = new SecureRandom();
    	byte[] seed = rnd.generateSeed(16);
    	System.out.println(String.format("AESEncrypter::genIVParamSpec(), generating seed %1$s", new String(seed)));
    	IvParameterSpec iv = new IvParameterSpec(seed);
    	return iv;
    }
    
    public static SecretKey genSecretKey() throws NoSuchAlgorithmException {
    	KeyGenerator generator = KeyGenerator.getInstance("AES");
    	//need to download unlimited jce7 jar files for this key size: 
    	//http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
    	//http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
    	generator.init(256);
    	SecretKey k = generator.generateKey();
    	return k;
    }
    
    public static void main(String[] args) {

        //String key1 = "Bar12345Bar12345"; // 128 bit key
      //IV (Initialization Vector) key is usually block size. Allow using the same secret key, should be none repeating, almost the "real" secret key.
        /*String key2 = "ThisIsASecretKet"; 
        String enc =encryptStringKeys(key1, key2, "Hello World");
        String dec = decryptStringKeys(key1, key2, enc);
        System.out.println(String.format("encrypted, with IV: %1$s", enc));
        System.out.println(String.format("decrypted, with IV: %1$s", dec));
        
        String plainText = "Hello World";
        String key = "Bar12345Bar12345";  
        // Encryption
        String encryptedText = null;
        try {
            encryptedText = EncryptNoIV(plainText, key);
            System.out.println("Encrypted Text, no IV: " +encryptedText);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
         
        // Decryption
        try {
            String decryptedText = DecryptNoIV(encryptedText, key);
            System.out.println("Decrypted Text, no IV: " +decryptedText);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }   */   
    	String test = "Hello World!";
    	String enc = AESEncrypter.encrypt(test);
    	System.out.println(String.format("encrypted: %1$s", enc));
    	System.out.println(String.format("decrypted: %1$s", AESEncrypter.decrypt(enc)));
    }
}
