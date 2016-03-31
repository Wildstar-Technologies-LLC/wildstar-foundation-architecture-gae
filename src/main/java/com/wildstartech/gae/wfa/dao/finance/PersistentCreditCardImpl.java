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

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.finance.PersistentCreditCard;
import com.wildstartech.wfa.finance.CreditCard;

public class PersistentCreditCardImpl 
extends PersistentPaymentCardImpl<CreditCard> 
implements PersistentCreditCard {
   /** Used in object serialization. */
   private static final long serialVersionUID = 4912806757561483035L;
   private static final String _CLASS=PersistentCreditCardImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   protected static final String _KIND=
         "com.wildstartech.wfa.finance.CreditCard";
   
   private String cardHolderName="";
   private String verification="";
   
   /**
    * Default, no-argument constructor.
    */
   public PersistentCreditCardImpl() {
      logger.entering(_CLASS,"PersistentCreditCardImpl()");
      logger.exiting(_CLASS,"PersistentCredtCardImpl()");
   }
   
   /**
    * Initializes the {@code PersistentCreditCardImpl} instance using the 
    * specified {@code CreditCard} as a reference.
    * @param creditCard The {@code CreditCard} whose properties should be used 
    * to initialize the current object.
    */
   public PersistentCreditCardImpl(CreditCard creditCard) {
      logger.entering(_CLASS,"PersistentCreditCardImpl(CreditCard)");
      if (creditCard != null) {
         populateFromObject(creditCard);
      } else {
         logger.warning("The creditCard parameter is null.");
      } // END if (creditCard != null)
      logger.exiting(_CLASS,"PersistentCredtCardImpl(CreditCard)");
   }
   
   //********** Utility Methods  
   
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      PersistentCreditCardImpl other = (PersistentCreditCardImpl) obj;
      if (cardHolderName == null) {
         if (other.cardHolderName != null)
            return false;
      } else if (!cardHolderName.equals(other.cardHolderName))
         return false;
      if (verification == null) {
         if (other.verification != null)
            return false;
      } else if (!verification.equals(other.verification))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result
            + ((cardHolderName == null) ? 0 : cardHolderName.hashCode());
      result = prime * result
            + ((verification == null) ? 0 : verification.hashCode());
      return result;
   }

   /**
    * Used by the {@code DatastoreService} to identify the type of entity
    * when storing {@code PersistentPaymentType} instances in persistent
    * storage.
    */
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentCreditCardImpl._KIND);
      return PersistentCreditCardImpl._KIND;
   }

   @Override 
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(Entity)",entity);
      if (entity != null) {
         super.populateEntity(entity);
         // cardHolderName
         entity.setProperty("cardHolderName", getCardHolderName());
         // verification
         entity.setProperty("verification", getVerification());
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateEntity(Entity)");
   }
   
   @Override 
   protected void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity,UserContext)", 
            new Object[] {entity, ctx});
      
      if (entity != null) {
         super.populateFromEntity(entity, ctx);
         // cardHolderName
         setCardHolderName(getPropertyAsString(entity,"cardHolderName"));
         // verification
         setVerification(getPropertyAsString(entity,"verification"));         
      } // END if (entity != null)
      
      logger.exiting(_CLASS, "populateFromEntity(Entity,UserContext)");
   }
   
   public void populateFromObject(CreditCard creditCard) {
      logger.entering(_CLASS, "populateFromObject(CreditCard)",creditCard);
      if (creditCard != null) {
         // BEGIN FIXME
         // For some reason or another invocation of super.populateFromObject
         // was causing errors with the maven build.
         //super.populateFromObject(creditCard);
         // accountNumber
         setAccountNumber(creditCard.getAccountNumber());
         // brandName
         setBrandName(creditCard.getBrandName());
         // expirationMonth
         setExpirationMonth(creditCard.getExpirationMonth());
         // expirationYear
         setExpirationYear(creditCard.getExpirationYear());
         // issuingBankName
         setIssuingBankName(creditCard.getIssuingBankName());
         // description
         setDescription(creditCard.getDescription());
         // cardholder Name
         setCardHolderName(creditCard.getCardHolderName());
         // verification
         setVerification(creditCard.getVerification());
      } else {
         logger.warning("The specified parameter is null.");
      } // END if (creditCard != null)
      logger.exiting(_CLASS, "populateFromObject(CreditCard)");
   }
   
   /**
    * Return a string representation of the properties that compose this object.
    * @return A {@code String} representation of the properties associated 
    * with this {@code Object} expressed using the following pattern:
    * {@code property1=value, property2=value, ...}
    */
   @Override
   public String toPropertyString() {
      logger.entering(_CLASS, "toPropertyString()");
      String result="";
      StringBuilder sb=null;
      
      
      result=super.toPropertyString();
      if (result.length() > 0) {
         sb=new StringBuilder(result.length()*2);
      } else {
         sb=new StringBuilder(80);
      } // END if (result.length() > 0)
      sb.append(result);
      if (sb.length() > 0) {
         sb.append(", ");
      } // END if (sb.length() > 0)
      sb.append("cardHolderName=").append(getCardHolderName());
      sb.append(", verification=").append(getVerification());
      result=sb.toString();
      
      logger.exiting(_CLASS, "toPropertyString()",result);
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
   //********** Accessor Methods
   @Override
   public String getCardHolderName() {
      logger.entering(_CLASS, "getCardHolderName()");
      logger.exiting(_CLASS, "getCardHolderName()",this.cardHolderName);
      return this.cardHolderName;
   }

   @Override
   public void setCardHolderName(String cardHolderName) {
      logger.entering(_CLASS, "setCardHolderName(String)",cardHolderName);
      this.cardHolderName=defaultValue(cardHolderName);
      logger.exiting(_CLASS, "setCardHolderName(String)");      
   }

   @Override
   public String getVerification() {
      logger.entering(_CLASS, "getVerification()");
      logger.exiting(_CLASS, "getVerification()",this.verification);
      return this.verification;
   }

   @Override
   public void setVerification(String verification) {
      logger.entering(_CLASS, "setVerification(String)",verification);
      this.verification=defaultValue(verification);
      logger.exiting(_CLASS, "setVerification(String)");       
   }
}