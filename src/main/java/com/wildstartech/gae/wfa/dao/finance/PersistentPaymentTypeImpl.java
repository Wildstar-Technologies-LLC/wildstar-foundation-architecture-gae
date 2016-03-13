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
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.finance.PaymentType;

public class PersistentPaymentTypeImpl<T extends PaymentType>
extends WildObjectImpl<T> 
implements PaymentType {
   /** Used in object serialization. */
   private static final long serialVersionUID = -8763349858375997466L;
   private static final String _CLASS=PersistentPaymentTypeImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   protected static final String _KIND=
         "com.wildstartech.wfa.finance.PaymentType";
   
   private String description="";
   
   /**
    * Default, no-argument constructor
    */
   public PersistentPaymentTypeImpl() {
      logger.entering(_CLASS, "PersistentPaymentTypeImpl()");
      logger.exiting(_CLASS, "PersistentPaymentTypeImpl()");
   }
   
   //***** Utility Methods
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      @SuppressWarnings("rawtypes")
      PersistentPaymentTypeImpl other = (PersistentPaymentTypeImpl) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      return true;
   }
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result
            + ((description == null) ? 0 : description.hashCode());
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
      logger.exiting(_CLASS, "getKind()",PersistentPaymentTypeImpl._KIND);
      return PersistentPaymentTypeImpl._KIND;
   }
   
   @Override 
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(Entity)",entity);
      if (entity != null) {
         super.populateEntity(entity);
         // description
         entity.setProperty("description", getDescription());
      } else {
         logger.severe("The Entity parameter was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateEntity(Entity)");
   }
   
   @Override
   protected void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateEntity(Entity)",entity);
      if (entity != null) {
         super.populateFromEntity(entity,ctx);
         // description
         setDescription(getPropertyAsString(entity,"description"));
      } else {
         logger.severe("The Entity parameter was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateEntity(Entity)");
   }
   
   @Override
   public void populateFromObject(PaymentType paymentType) {
      logger.entering(_CLASS, "populateFromObject(PaymentType)",paymentType);
      if (paymentType != null) {
         // description
         setDescription(paymentType.getDescription());
      } else {
         logger.severe("The paymentType parameter is null");
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
      sb.append("description=").append(getDescription());
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
   @Override
   public String getDescription() {
      logger.entering(_CLASS, "getDescription()");
      logger.exiting(_CLASS, "getDescription()",this.description);
      return this.description;
   }

   @Override
   public void setDescription(String description) {
      logger.entering(_CLASS, "setDescription(String)",description);
      this.description=defaultValue(description);
      logger.exiting(_CLASS, "setDescription(String)");
   }
}