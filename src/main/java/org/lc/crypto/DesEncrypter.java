package org.lc.crypto;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.*;

import javax.crypto.spec.*;
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;


public class DesEncrypter {
        Cipher ecipher;
        Cipher dcipher;
    
        // 8-byte Salt
        byte[] salt = {
            (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
            (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
        };
    
        // Iteration count
        int iterationCount = 19;
    
    public DesEncrypter(String passPhrase) {
            try {
                // Create the key
                KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
                SecretKey key = SecretKeyFactory.getInstance(
                    "PBEWithMD5AndDES").generateSecret(keySpec);
                ecipher = Cipher.getInstance(key.getAlgorithm());
                dcipher = Cipher.getInstance(key.getAlgorithm());
    
                // Prepare the parameter to the ciphers
                AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
    
                // Create the ciphers
                ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
                dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
            } catch (java.security.InvalidAlgorithmParameterException e) {
            } catch (java.security.spec.InvalidKeySpecException e) {
            } catch (javax.crypto.NoSuchPaddingException e) {
            } catch (java.security.NoSuchAlgorithmException e) {
            } catch (java.security.InvalidKeyException e) {
            }
        }
    
        public String encrypt(String str) {
            try {
                // Encode the string into bytes using utf-8
                byte[] utf8 = str.getBytes("UTF8");
    
                // Encrypt
                byte[] enc = ecipher.doFinal(utf8);
    
                // Encode bytes to base64 to get a string
                return new String(org.apache.commons.codec.binary.Base64.encodeBase64(enc));
            } catch (javax.crypto.BadPaddingException e) {
            } catch (IllegalBlockSizeException e) {
            } catch (UnsupportedEncodingException e) {
            } catch (java.io.IOException e) {
            }
            return null;
        }
    
        public String decrypt(String str) {
            try {
                // Decode base64 to get bytes
                byte[] dec =  org.apache.commons.codec.binary.Base64.decodeBase64(str.getBytes());
    
                // Decrypt
                byte[] utf8 = dcipher.doFinal(dec);
    
                // Decode using utf-8
                return new String(utf8, "UTF8");
            } catch (javax.crypto.BadPaddingException e) {
            } catch (IllegalBlockSizeException e) {
            } catch (UnsupportedEncodingException e) {
            } catch (java.io.IOException e) {
            }
            return null;
       }

   public static void main(String argv[]){
    try {
       // Create encrypter/decrypter class
        DesEncrypter encrypter = new DesEncrypter("Pr0vb0rt8l");
    
        // Encrypt
        String encrypted = encrypter.encrypt("AP");
        System.out.println(encrypted);
        System.out.println(URLEncoder.encode(encrypted, "UTF-8"));
        // Decrypt
        String urldecoded = URLDecoder.decode("5UaUfC4MqLbvShbDJ66v1g%3D%3D", "UTF-8");
        String decrypted = encrypter.decrypt(urldecoded);
        System.out.println(decrypted);
        
        
       
    } catch (Exception e) {  }
  }


   
 }

