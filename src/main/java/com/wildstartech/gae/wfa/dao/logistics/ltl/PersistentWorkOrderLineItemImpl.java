/*
 * Copyright (c) 2013 - 2016 Wildstar Technologies, LLC.
 *
 * This file is part of Wildstar Foundation Architecture.
 *
 * Wildstar Foundation Architecture is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Wildstar Foundation Architecture is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Wildstar Foundation Architecture.  If not, see 
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
package com.wildstartech.gae.wfa.dao.logistics.ltl;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentWorkOrderLineItem;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.workorder.WorkOrderLineItem;

public class PersistentWorkOrderLineItemImpl 
extends PersistentEditableCommodityLineItemImpl<WorkOrderLineItem> 
implements PersistentWorkOrderLineItem {
   /** Used in object serialization. */
   private static final long serialVersionUID = 971987588880318395L;
   private static final String _CLASS = 
         PersistentWorkOrderLineItemImpl.class.getName();
   private static final Logger logger = Logger.getLogger(_CLASS);
   protected static final String _KIND = 
         "com.wildstartech.wfa.logistics.ltl.WorkOrderLineItem";

   /* The price of the item. */
   private float price = 0;
   
   /*
    * Uniquely identifies the work order with which this line item object is
    * associated.
    */
   private String workOrderIdentifier = null;

   /**
    * Default, no-argument constructor.
    */
   public PersistentWorkOrderLineItemImpl() {
      logger.entering(_CLASS, "PersistentWorkOrderLineItemImpl()");
      logger.exiting(_CLASS, "PersistentWorkOrderLineItemImpl()");
   }

   // ***** utility methods
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      PersistentWorkOrderLineItemImpl other = (PersistentWorkOrderLineItemImpl) obj;
      if (Float.floatToIntBits(price) != Float.floatToIntBits(other.price))
         return false;
      if (workOrderIdentifier == null) {
         if (other.workOrderIdentifier != null)
            return false;
      } else if (!workOrderIdentifier.equals(other.workOrderIdentifier))
         return false;
      return true;
   }
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Float.floatToIntBits(price);
      result = prime * result + ((workOrderIdentifier == null) ? 0
            : workOrderIdentifier.hashCode());
      return result;
   }
 
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",
            PersistentWorkOrderLineItemImpl._KIND);
      return PersistentWorkOrderLineItemImpl._KIND;
   }

   @Override
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(Entity)", entity);
      if (entity != null) {
         super.populateEntity(entity);
         entity.setProperty("price", getPrice());
         entity.setProperty("workOrderIdentifier", getWorkOrderIdentifier());         
      } else {
         logger.severe("The Entity object is null.");
      } // END if (entity != null)

      logger.exiting(_CLASS, "populateEntity(Entity)");
   }

   @Override
   protected void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity,UserContext)",
            new Object[] { entity, ctx });
      if (entity != null) {
         super.populateFromEntity(entity, ctx);
         // price
         setPrice(getPropertyAsFloat(entity, "price"));
         // workOrderIdentifier
         setWorkOrderIdentifier(
               getPropertyAsString(entity, "workOrderIdentifier"));
      } else {
         logger.warning("The entity passed was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateFromEntity(Entity,UserContext)");
   }

   @Override
   public void populateFromObject(WorkOrderLineItem woli) {
      logger.entering(_CLASS, "populateFromObject(WorkOrderLineItem)", woli);
      if (woli != null) {
         super.populateFromObject(woli);
         if (woli instanceof PersistentWorkOrderLineItem) {
            // workOrderIdentifier
            setWorkOrderIdentifier(((PersistentWorkOrderLineItem) woli)
                     .getWorkOrderIdentifier());
         } // END if (qli instanceof PersistentQuoteLineItem)
      } else {
         logger.warning("The WorkOrderLineItem passed was null.");
      } // END if(woli != null)
      logger.exiting(_CLASS, "populateFromObject(WorkOrderLineItem)");
   }

   /**
    * Calculate the hashcode for the PersistentAccessorialCharge
    */
   
   @Override
   public String toPropertyString() {
      logger.entering(_CLASS,"toPropertyString()");
      String returnValue=null;
      StringBuilder sb=null;
      
      returnValue=super.toPropertyString();
      sb=new StringBuilder(returnValue.length() + 80);
      sb.append(returnValue);
      if (sb.length() > 0) sb.append(", ");
      sb.append("workOrderIdentifier=\"").append(getWorkOrderIdentifier());
      returnValue=sb.toString();
      
      logger.exiting(_CLASS, "toPropertyString()",returnValue);
      return returnValue;
   }
   @Override
   public String toString() {
     logger.entering(_CLASS,"toString()");
     String result=null;
     String tmpStr=null;
     StringBuilder sb=null;
     
     tmpStr=toPropertyString();
     sb=new StringBuilder(tmpStr.length() + 160);
     sb.append(_CLASS).append(" [");
     sb.append(tmpStr);
     sb.append("]");
     result=sb.toString();
     
     logger.entering(_CLASS,"toString()",result);
     return result;
   }

   /**
    * Updates current object using information from the specified object.
    */
   public void updateFromObject(WorkOrderLineItem lineItem) {
      logger.entering(_CLASS, "updateFromObject(WorkOrderLineItem)", lineItem);
      if (lineItem != null) {
         populateFromObject(lineItem);
      } else {
         logger.finest("The specified WorkOrderLineItem object is null.");
      } // END
      logger.exiting(_CLASS, "updateFromObject(WorkOrderLineItem)");
   }

   // ***** accessor methods   

   // ***** price
   @Override
   public final float getPrice() {
      logger.entering(_CLASS, "getPrice()");
      logger.exiting(_CLASS, "getPrice()", this.price);
      return this.price;
   }

   @Override
   public final void setPrice(float price) {
      logger.entering(_CLASS, "setPrice(float)", price);
      this.price = price;
      logger.entering(_CLASS, "setPrice(float)");

   }

   // ***** totalPrice
   @Override
   public final float getTotalPrice() {
      logger.entering(_CLASS, "getTotalPrice()");
      int quantity=0;
      float price=0f;
      float totalPrice=0f;
      
      quantity=getQuantity();
      price=getPrice();
      totalPrice=quantity * price;
      logger.exiting(_CLASS, "getTotalPrice()",totalPrice);
      return totalPrice;
   }
   
   // ***** workOrderIdentifier
   @Override
   public final String getWorkOrderIdentifier() {
      logger.entering(_CLASS, "getWorkOrderIdentifier()");
      logger.exiting(_CLASS, "getWorkOrderIdentifier()",
            this.workOrderIdentifier);
      return this.workOrderIdentifier;
   }

   @Override
   public final void setWorkOrderIdentifier(String identifier) {
      logger.entering(_CLASS, "setWorkOrderIdentifier(String)", identifier);
      this.workOrderIdentifier = defaultValue(identifier);
      logger.exiting(_CLASS, "setWorkOrderIdentifier(String)");
   }   
}