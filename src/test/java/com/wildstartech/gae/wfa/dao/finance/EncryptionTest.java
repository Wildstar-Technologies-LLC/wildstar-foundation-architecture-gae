package com.wildstartech.gae.wfa.dao.finance;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.testng.annotations.Test;

import com.google.api.client.util.Base64;

public class EncryptionTest {
   @Test
   public void testEncryption() throws Exception {
      byte[] passwordBytes=null;
      MessageDigest md=null;
      SecretKey key=null;
      String encryptedText=null;
      String referenceText="This is the unencrypted text";
      String passwordText="j1IientjO4YXEB1FOrnMLPaTpXLrxfDVGnhHZH9Lm4umzjnP3fRbX6VmaDS06esofE2JgxRac4VoiHaUc2yQBXKNwtALPXFhGaWtjHAYg3iv38RrOYzuNCKyity4SN94QpoCdgxnqQOGmPcCzBbCzuMnEiGw5qnN7lcQ68zJXtRUpghiwjAGeDIobs2nUukmoZOQlr5enoKj5rLEtYrX6nckOa4bpUP2KsdlHBsytiPTgDejOGI6FRm9Ht8T016a";
      String salt="JmgHPJfPr0ZXwUZ2ihHPlzraCHNuqgwh1NANdZKAuTlo8FW8V9jipwPVr4qbKdEuIX1duOv6yLpEBNFdRogjOVvNgev1kt3flM6PE08iCawQICQGtjZhMQH8KmxdHfshrZTcPWDRnKCadOAw68P0QMMbMx3Kzb3wDTkUJquRitoxYdeh4qkEGclDcy9ks1mDAEdwIM8jQRmHYP9ThDoOynDPosjD5LfeEuX3jmPpN4UBtyFWK6EaSwN3fIkeTvoK";
      String saltedPassword="";
      StringBuilder sb=null;
      
      sb=new StringBuilder((salt.length()+passwordText.length())*2);
      sb.append(passwordText).append(salt);
      saltedPassword=sb.toString();
      md=MessageDigest.getInstance("MD5");
      passwordBytes=md.digest(saltedPassword.getBytes());      
      key=new SecretKeySpec(passwordBytes,"AES");
      System.out.print("Plain text string: ");
      System.out.println(referenceText);
      System.out.print("Encrypted string: ");
      encryptedText=encrypt(referenceText,key);
      System.out.println(encryptedText);
      System.out.print("Decrypted text: ");
      System.out.println(decrypt(encryptedText,key));
   }
   public static String encrypt(String text, SecretKey key) 
   throws Exception {
      byte[] plainBytes=null;
      byte[] encryptedBytes=null;
      Cipher cipher=null;
      String encryptedText=null;
      
      plainBytes=text.getBytes();
      cipher=Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, key);
      encryptedBytes=cipher.doFinal(plainBytes);
      encryptedText=Base64.encodeBase64String(encryptedBytes);
      
      return encryptedText;
   }
   
   public static String decrypt(String encryptedText, SecretKey key)
   throws Exception {
      byte[] encryptedBytes=null;
      byte[] plainBytes=null;
      Cipher cipher=null;
      String plainText=null;
      
      
      encryptedBytes=Base64.decodeBase64(encryptedText.getBytes());
      cipher=Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, key);
      plainBytes=cipher.doFinal(encryptedBytes);
      plainText=new String(plainBytes);
      
      return plainText;
   }
}
