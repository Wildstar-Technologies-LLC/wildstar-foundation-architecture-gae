/*
 * Copyright (c) 2016 Wildstar Technologies, LLC.
 *
 * This file is part of Wildstar Foundation Architecture for Google App Engine.
 *
 * Wildstar Foundation Architecture for Google App Engine is free software: you
 * can redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either version
 * 3 of the License, or (at your option) any later version.
 *
 * Wildstar Foundation Architecture for Google App Engine is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Wildstar Foundation Architecture for Google App Engine.  If not, see 
 * <http://www.gnu.org/licenses/>.
 * 
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the 
 * GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you 
 * permission to link this library with independent modules to produce an 
 * executable, regardless of the license terms of these independent modules, 
 * and to copy and distribute the resulting executable under terms of your 
 * choice, provided that you also meet, for each linked independent module, the
 * terms and conditions of the license of that module. An independent module is
 * a module which is not derived from or based on this library. If you modify 
 * this library, you may extend this exception to your version of the library, 
 * but you are not obliged to do so. If you do not wish to do so, delete this 
 * exception statement from your version.
 * 
 * If you need additional information or have any questions, please contact:
 *
 *      Wildstar Technologies, LLC.
 *      63 The Greenway Loop
 *      Panama City Beach, FL 32413
 *      USA
 *
 *      derek.berube@wildstartech.com
 *      www.wildstartech.com
 */
package com.wildstartech.gae.wfa.dao.finance;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.google.api.client.util.Base64;
import com.google.appengine.api.datastore.Entity;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.finance.PaymentCard;

public class PersistentPaymentCardImpl<T extends PaymentCard> 
extends PersistentPaymentTypeImpl<T>
implements PaymentCard {
   /** Used in object serialization. */
   private static final long serialVersionUID = -2469302507272606721L;
   private static final String _CLASS=PersistentPaymentCardImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   private static String ENCRYPTION_KEY_SALT=
      "j1IientjO4YXEB1FOrnMLPaTpXLrxfDVGnhHZH9Lm4umzjnP3fRbX6VmaDS06esofE2JgxR"+
      "ac4VoiHaUc2yQBXKNwtALPXFhGaWtjHAYg3iv38RrOYzuNCKyity4SN94QpoCdgxnqQOGmP"+
      "cCzBbCzuMnEiGw5qnN7lcQ68zJXtRUpghiwjAGeDIobs2nUukmoZOQlr5enoKj5rLEtYrX6"+
      "nckOa4bpUP2KsdlHBsytiPTgDejOGI6FRm9Ht8T016a";
   
   private static String DEFAULT_ENCRYPTION_ALGORITHM="AES";
   private static String ENCRYPTION_KEY_PROPERTY="encryption-key";
   
   private static String encryptedNull="<< ~~ !! NULL !! ~~ >>";  
   private static String encryptionAlgorithm="";
   private static SecretKey encryptionKey=null;
   static {
      classInitializer();
   }
   
   protected static final String _KIND=
         "com.wildstartech.wfa.finance.PaymentCard";
   
   private Date expirationDate=null;
   private String accountNumber="";
   private String brandName="";
   private String issuingBankName="";
   
   /**
    * Default, no-argument constructor.
    */
   public PersistentPaymentCardImpl() {
      logger.entering(_CLASS, "PersistentPaymentCardImpl()");
      logger.exiting(_CLASS, "PersistentPaymentCardImpl()");
   }
   //********** Static methods
   private static void classInitializer() {
      logger.entering(_CLASS, "classInitializer()");
      byte[] keyBytes=null;
      ClassLoader cl=null;
      InputStream in=null;
      MessageDigest md=null;
      Properties props=null;
      SecretKey key=null;
      String encryptionAlgorithm=null;
      String encryptionKey=null;
      String encryptionPassword=null;
      StringBuilder passwordBuilder=null;
      
      cl=PersistentCreditCardImpl.class.getClassLoader();
      if (cl != null) {
         in=cl.getResourceAsStream("PersistentPaymentCardImpl.properties");
         if (in != null) {
            props=new Properties();
            try {
               props.load(in);
               if (props != null) {
                  //***** Let's get the properties...
                  // encryptionKey
                  encryptionKey=props.getProperty(ENCRYPTION_KEY_PROPERTY);
                  // Let's check the algorithm....
                  if (encryptionAlgorithm == null) {
                     encryptionAlgorithm=
                        PersistentPaymentCardImpl.DEFAULT_ENCRYPTION_ALGORITHM;
                  } // END if (encryptionAlgorithm == null)
                  // Store the static encryptionAlgorithm...
                  PersistentPaymentCardImpl.encryptionAlgorithm=
                        encryptionAlgorithm;
                  
                  // Let's create the encryption key
                  if (encryptionKey != null) {
                     encryptionKey=
                           PersistentPaymentCardImpl.ENCRYPTION_KEY_SALT;
                  } // END if (encryptionKey != null);
                  passwordBuilder=new StringBuilder(
                       (ENCRYPTION_KEY_SALT.length()+encryptionKey.length())*2);
                  passwordBuilder.append(
                        PersistentPaymentCardImpl.ENCRYPTION_KEY_SALT);
                  passwordBuilder.append(encryptionKey);
                  encryptionPassword=passwordBuilder.toString();
                  try {
                     md=MessageDigest.getInstance("MD5");
                     keyBytes=md.digest(encryptionPassword.getBytes());             
                  } catch (NoSuchAlgorithmException ex) {
                     logger.log(
                           Level.SEVERE,
                           "NoSuchAlgorithm thrown getting digest.",
                           ex);
                  } // END try/catch
                  key=new SecretKeySpec(keyBytes,"AES");
                  // Let's save the encryptionKey...
                  PersistentPaymentCardImpl.encryptionKey=key;                  
               } // END if (props != null)
            } catch (IOException ex) {
               logger.log(
                     Level.SEVERE,
                     "IOException thrown reading properties file.",
                     ex);
            } // END try/catch
         } else {
            logger.severe("Unable to locate configuration file.");
         } // END if (in != null)
      } else {
         logger.severe("Unable to locate the ClassLoader.");
      } // END if (cl != null)
      
      logger.exiting(_CLASS, "classInitializer()");
   }
   //***** Utility Methods
   /**
    * Returns the decrypted version of the specified string.
    * @param encryptedString The {@code String} value that should be decrypted.
    * @return The decrypted version of the {@code String} passed as a parameter
    * to the method.
    */
   private String decrypt(String encryptedString) {
      logger.entering(_CLASS, "decrypt(String)",encryptedString);
      byte[] decryptedBytes=null;
      byte[] encryptedBytes=null;
      Cipher cipher=null;
      SecretKey key=null;
      String decryptedText="";
      String encryptionAlgorithm="";
      
      if (encryptedString != null) {
         encryptionAlgorithm=PersistentPaymentCardImpl.encryptionAlgorithm;
         key=PersistentPaymentCardImpl.encryptionKey;
         
         try {
            encryptedBytes=Base64.decodeBase64(encryptedString.getBytes());
            cipher=Cipher.getInstance(encryptionAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedBytes=cipher.doFinal(encryptedBytes);
            decryptedText=new String(decryptedBytes);
            if (
                  (decryptedText != null) && 
                  (decryptedText.equalsIgnoreCase(
                        PersistentPaymentCardImpl.encryptedNull))                 
               ) {
               decryptedText="";
            } // END 
         } catch (NoSuchAlgorithmException ex) {
            logger.log(
                  Level.SEVERE,
                  "Unable to locate the specified encryption algorithm.",
                  ex);
         } catch (NoSuchPaddingException ex) {
            logger.log(
                  Level.SEVERE,
                  "NoSuchPaddingException thrown getting cipher.",
                  ex);
         } catch (InvalidKeyException ex) {
            logger.log(
                  Level.SEVERE,
                  "InvalidKeyException thrown initializing cipher.",
                  ex);
         } catch (IllegalBlockSizeException ex) {
            logger.log(
                  Level.SEVERE,
                  "IllegalBlockSizeException thrown while decrypting text.",
                  ex);
         } catch (BadPaddingException ex) {
            logger.log(
                  Level.SEVERE,
                  "BadPaddingException thrown while decrypting text.",
                  ex);
         } // END try/catch
      } // END if (encryptedString != null)
      logger.exiting(_CLASS, "decrypt(String)",decryptedText);
      return decryptedText;
   }
   /**
    * Returns an encrypted version of the specified string.
    * @param textToEncrypt The {@code String} value that should be encrypted.
    * @return The encrypted version of the {@code String} passed as a parameter
    * to the method.
    */
   private String encrypt(String textToEncrypt) {
      logger.entering(_CLASS, "encrypt(String)",textToEncrypt);
      byte[] encryptedBytes=null;
      byte[] unEncryptedBytes=null;
      Cipher cipher=null;
      SecretKey key=null;
      String encryptedText="";
      String encryptionAlgorithm="";
      
      if ((textToEncrypt == null) || (textToEncrypt.length() == 0)) {
         textToEncrypt=encryptedNull;
      } // END if ((textToEncrypt == null) || (textToEncrypt.length() == 0))
      try {
         key=PersistentPaymentCardImpl.encryptionKey;
         encryptionAlgorithm=PersistentPaymentCardImpl.encryptionAlgorithm;
         unEncryptedBytes=textToEncrypt.getBytes();
         cipher=Cipher.getInstance(encryptionAlgorithm);
         cipher.init(Cipher.ENCRYPT_MODE, key);
         encryptedBytes=cipher.doFinal(unEncryptedBytes);
         encryptedText=Base64.encodeBase64String(encryptedBytes);
      } catch (NoSuchAlgorithmException ex) {
         logger.log(
               Level.SEVERE,
               "Unable to load encryption algorithm.",
               ex);
      } catch (NoSuchPaddingException ex) {
         logger.log(
               Level.SEVERE,
               "NoSuchPaddingException error thrown.",
               ex);
      } catch (InvalidKeyException ex) {
         logger.log(
               Level.SEVERE,
               "InvalidKeyException error thrown.",
               ex);
      } catch (IllegalBlockSizeException ex) {
         logger.log(
               Level.SEVERE,
               "IllegalBlockSizeException thrown encrypting data.",
               ex);
      } catch (BadPaddingException ex) {
         logger.log(
               Level.SEVERE,
               "BadPaddingException thrown encrypting data.",
               ex);            
      } // END try/catch      
      
      logger.exiting(_CLASS, "encrypt(String)",encryptedText);
      return encryptedText;
   }
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      @SuppressWarnings("rawtypes")
      PersistentPaymentCardImpl other = (PersistentPaymentCardImpl) obj;
      if (accountNumber == null) {
         if (other.accountNumber != null)
            return false;
      } else if (!accountNumber.equals(other.accountNumber))
         return false;
      if (brandName == null) {
         if (other.brandName != null)
            return false;
      } else if (!brandName.equals(other.brandName))
         return false;
      if (expirationDate == null) {
         if (other.expirationDate != null)
            return false;
      } else if (!expirationDate.equals(other.expirationDate))
         return false;
      if (issuingBankName == null) {
         if (other.issuingBankName != null)
            return false;
      } else if (!issuingBankName.equals(other.issuingBankName))
         return false;
      return true;
   }
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result
            + ((accountNumber == null) ? 0 : accountNumber.hashCode());
      result = prime * result
            + ((brandName == null) ? 0 : brandName.hashCode());
      result = prime * result
            + ((expirationDate == null) ? 0 : expirationDate.hashCode());
      result = prime * result
            + ((issuingBankName == null) ? 0 : issuingBankName.hashCode());
      return result;
   }
   /**
    * Determines whether or not the specified user is authorized to view the 
    * full credit card information. 
    * 
    * @param ctx The {@code UserContext} object that contains the 
    * authentication information of the current user.
    * @return
    */
   public final boolean isAuthorized(UserContext ctx) {
      logger.entering(_CLASS, "isAuthorized(UserContext)",ctx);
      boolean result=false;
      String userName=null;
      if (ctx != null) {
         userName=ctx.getUserName();
         /* CRM00098 - Allow All Justo Employees to View Credit Card Data
         switch(userName) {
         case "derek.berube@justodelivery.com":
         case "edina@justodelivery.com":
         case "edina.berube@justodelivery.com":
         case "kirk@justodelivery.com":
         case "kirk.berube@justodelivery.com":
         case "sally@justodelivery.com":
         case "sally.perez@justodelivery.com":
            result=true;
            break;
         } // END switch(userName)
         */
         if ((userName != null) && (userName.contains("@justodelivery.com"))) {
            result=true;
         } // END if ((userName != null) && (userName.contains...
      } // END if (ctx != null)
      logger.exiting(_CLASS, "isAuthorized(UserContext)",result);
      return result;
   }
   /**
    * Used by the {@code DatastoreService} to identify the type of entity
    * when storing {@code PersistentPaymentType} instances in persistent
    * storage.
    * 
    */
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentPaymentCardImpl._KIND);
      return PersistentPaymentCardImpl._KIND;
   }
   
   /**
    * Parses a date from a {@code String} in the MM / YY format.
    * @return If the {@code expirationDate} {@code String} is NOT in the 
    * "MM / YY" format, then a {@code null} is returned. 
    */
   public Date parseExpirationDate(String expirationDate) {
      logger.entering(_CLASS, "parseExpirationDate(String)");
      Date parsedDate=null;
      DateFormat fmt=null;
      
      if (expirationDate != null) {
         fmt=new SimpleDateFormat("MM / yy");
         try {
            parsedDate=fmt.parse(expirationDate);
         } catch (ParseException e) {
            logger.warning(
                 "ParseException thrown parse the card expiration date.");                  
         } // END try/catch
      } // END if (expirationDate != null)
      
      logger.exiting(_CLASS, "parseExpirationDate(String)",parsedDate);
      return parsedDate;
   }
   
   @Override 
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(Entity)",entity);
      Date expirationDate=null;
      String accountNumber=null;
      String encryptedAccountNumber=null;;
      
      if (entity != null) {
         super.populateEntity(entity);
         // accountNumber
         accountNumber=getAccountNumber();
         if (!accountNumber.startsWith("*")) {
            encryptedAccountNumber=encrypt(accountNumber);
            entity.setProperty("accountNumber", encryptedAccountNumber);
         } // END if (!accountNumber.startsWith("*"))
         // brandName
         entity.setProperty("brandName",getBrandName());
         // expirationDate
         expirationDate=getExpirationDate();
         if (expirationDate != null) {
            entity.setProperty("expirationDate", expirationDate);
         } // END if (expirationDate != null)
         // issuingBankName
         entity.setProperty("issuingBankName", getIssuingBankName());
      } else {
         logger.severe("The Entity parameter was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateEntity(Entity)");
   }
   public String getMaskedAccountNumber(String accountNumber) {
      logger.entering(_CLASS, "maskAccountNumber()");
      int length=0;
      int pos=0;
      String maskedNumber="";
      StringBuilder sb=null;
      
      if ((accountNumber != null) && (accountNumber.length() > 0)) {
         length=accountNumber.length();
         sb=new StringBuilder(length*2);
         if (length > 4) {
            pos=length-4;
         } else {
            pos=length/2;
         } // END if (length > 4)
         for (int i=0; i < length; i++) {
            if (i < pos) {
               sb.append('*');               
            } else {
               sb.append(accountNumber.charAt(i));
            } // END if (i < pos)
         } // END for (int i=0; i < length; i++)
         maskedNumber=sb.toString();
      } // END if ((accountNumber != null) && (accountNumber.length() > 0))
      
      logger.exiting(_CLASS, "maskAccountNumber()",maskedNumber);
      return maskedNumber;
   }
   @Override
   protected void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateEntity(Entity)",entity);
      String encryptedAccountNumber=null;
      String decryptedAccountNumber=null;
      
      if (entity != null) {
         super.populateFromEntity(entity,ctx);
         // accountNumber
         encryptedAccountNumber=getPropertyAsString(entity,"accountNumber");
         decryptedAccountNumber=decrypt(encryptedAccountNumber);
         if (isAuthorized(ctx)) {
            setAccountNumber(decryptedAccountNumber);
         } else {
            setAccountNumber(getMaskedAccountNumber(decryptedAccountNumber));
         } // END if (isAuthorized(ctx))
         // brandName
         setBrandName(getPropertyAsString(entity,"brandName"));
         // expirationDate
         setExpirationDate(getPropertyAsDate(entity,"expirationDate"));
         // issuingBankName
         setIssuingBankName(getPropertyAsString(entity,"issuingBankName"));         
      } else {
         logger.severe("The Entity parameter was null.");
      } // END if (entity != null)
      
      logger.exiting(_CLASS, "populateEntity(Entity)");
   }
   
   public void populateFromObject(PaymentCard paymentCard) {
      logger.entering(_CLASS, "populateFromObject(PaymentType)",paymentCard);
      if (paymentCard != null) {
         super.populateFromObject(paymentCard);
         // accountNumber
         setAccountNumber(paymentCard.getAccountNumber());
         // brandName
         setBrandName(paymentCard.getBrandName());
         // expirationDate
         setExpirationDate(paymentCard.getExpirationDate());
         // issuingBankName
         setIssuingBankName(paymentCard.getIssuingBankName());
      } else {
         logger.warning("The paymentType parameter is null");
      } // END if (paymentType != null)
      logger.exiting(_CLASS, "populateFromObject(PaymentType)");
   }
   
   /**
    * Return a string representation of the properties that compose this object.
    * @return A {@code String} representation of the properties associated 
    * with this {@code Object} expressed using the following pattern:
    * {@code property1=value, property2=value, ...}
    */
   @Override
   public String toPropertyString() {
      logger.entering(_CLASS, "PersistentPaymentTypeImpl()");
      int length=0;
      Date expirationDate=null;
      DateFormat fmt=null;
      String result="";
      String tmpStr=null;
      StringBuilder sb=null;
      
      
      result=super.toPropertyString();
      sb=new StringBuilder(result.length()*2);
      sb.append(result);
      // accountNumber
      tmpStr=getAccountNumber();
      length=tmpStr.length();
      sb.append(", accountNumber=");
      if (length > 4) {
         sb.append("**** **** **** ");
         sb.append(tmpStr.substring(tmpStr.length() - 4));
      } else if (length > 0) {
         sb.append("****");
      } // END if (length > 4)      
      // brandName
      sb.append(", brandName=").append(getBrandName());
      // expirationDate
      sb.append(", expirationDate=");
      expirationDate=getExpirationDate();
      if (expirationDate != null) {
         fmt=new SimpleDateFormat("MM / yy");
         sb.append(fmt.format(expirationDate));
      } // END if (expirationDate != null) 
      // issuingBank
      sb.append(", issuingBankName=").append(getIssuingBankName());
      result=sb.toString();
      
      logger.exiting(_CLASS, "PersistentPaymentTypeImpl()",result);
      return result;
   }
   
   /**
    * Returns a string representation of the object.
    * @return A {@code String} representation of the object matching the 
    * following pattern {@code ClassName [property1=value,property2=value,...]}
    */
   @Override
   public String toString() {
      logger.entering(_CLASS, "toString()");
      String result="";
      StringBuilder sb=null;
      
      result=toPropertyString();
      sb=new StringBuilder(result.length()+50);
      sb.append(_CLASS).append(" [");
      sb.append(result);
      sb.append("]");
      result=sb.toString();
      logger.exiting(_CLASS, "toString()",result);
      return result;
   }
   //***** Accessor Methods
   // accountNumber
   @Override
   public String getAccountNumber() {
      logger.entering(_CLASS, "getAccountNumber()");
      logger.exiting(_CLASS, "getAccountNumber()",this.accountNumber);
      return this.accountNumber;
   }
   @Override
   public void setAccountNumber(String accountNumber) {
      logger.entering(_CLASS, "setAccountNumber(String)");
      this.accountNumber=defaultValue(accountNumber);
      logger.exiting(_CLASS, "setAccountNumber(String)");
   }
   // brandName
   @Override
   public String getBrandName() {
      logger.entering(_CLASS, "getBrandName()");
      logger.exiting(_CLASS, "getBrandName()",this.brandName);
      return this.brandName;
   }
   @Override
   public void setBrandName(String brandName) {
      logger.entering(_CLASS, "setBrandName(String)");
      this.brandName=defaultValue(brandName);
      logger.exiting(_CLASS, "setBrandName(String)");
   }
   // expirationDate
   @Override
   public Date getExpirationDate() {
      logger.entering(_CLASS, "getExpirationDate()");
      logger.exiting(_CLASS, "getExpirationDate()",this.expirationDate);
      return this.expirationDate;
   }
   
   public String getFormattedExpirationDate() {
      logger.entering(_CLASS, "getFormattedExpirationDate()");
      DateFormat fmt=null;
      String formattedDate=null;
      
      if (this.expirationDate != null) {
         fmt=new SimpleDateFormat("MM / yy");
         formattedDate=fmt.format(this.expirationDate);
      } // END if (this.expriationDate != null)
      
      logger.exiting(_CLASS, "getFormattedExpirationDate()",formattedDate);
      return formattedDate;
   }
   @Override
   public void setExpirationDate(Date expirationDate) {
      logger.entering(_CLASS, "setExpirationDate(Date)");
      this.expirationDate=expirationDate;
      logger.exiting(_CLASS, "setExpirationDate(Date)");
   }
   // issuingBankName
   @Override
   public String getIssuingBankName() {
      logger.entering(_CLASS, "getIssuingBankName()");
      logger.exiting(_CLASS, "getIssuingBankName()",this.issuingBankName);
      return this.issuingBankName;
   }
   @Override
   public void setIssuingBankName(String issuingBankName) {
      logger.entering(_CLASS, "setIssuingBankName(String)",issuingBankName);
      this.issuingBankName=issuingBankName;
      logger.exiting(_CLASS, "setIssuingBankName(String)");      
   }   
}