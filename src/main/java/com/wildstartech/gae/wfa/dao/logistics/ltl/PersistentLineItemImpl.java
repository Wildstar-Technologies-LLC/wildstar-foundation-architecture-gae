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

import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentLineItem;
import com.wildstartech.wfa.logistics.ltl.LineItem;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;

public class PersistentLineItemImpl<T extends LineItem>
extends WildObjectImpl<T>
implements PersistentLineItem {
   /** Used in object serialization. */
   private static final long serialVersionUID = -6703815457855051472L;
   private static final String _CLASS=PersistentLineItemImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   private static final String _KIND=
         "com.wildstartech.wfa.logistics.ltl.LineItem";
   
   /*
    * The position of the quote line item on the associated quote. 
    */
   private int lineItemNumber=1;
   
   /**
    * Default, no-argumnet constructor.
    */
   public PersistentLineItemImpl() {
      super();
      logger.entering(_CLASS,"PersistentLineItemImpl()");
      logger.exiting(_CLASS,"PersistentLineItemImpl()");
   }
   //********** Utility Methods
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + lineItemNumber;
      return result;
   }
   
   @Override
   public boolean equals(Object object) {
      if (this == object)
         return true;
      if (!super.equals(object))
         return false;
      if (getClass() != object.getClass())
         return false;
      PersistentLineItemImpl other = (PersistentLineItemImpl) object;
      if (lineItemNumber != other.lineItemNumber)
         return false;
      return true;
   }
   
   //***** populateEntity
   /**
    * Populate the specified entity with information from the current object.
    * 
    * <p>This method will populate the <code>Entit</code> object with 
    * the lineItemNumber field value</p>
    * 
    * @param entity The instance of the <code>Entity</code> class from the 
    * <code>com.google.appengine.api.datastore.Entity</code> package.
    */
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS,"populateEntity(Entity)",entity);
      if (entity != null) {
         super.populateEntity(entity);
         entity.setProperty("lineItemNumber", getLineItemNumber());
      } else {
         logger.severe("The Entity parameter was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS,"populateEntity(Entity)");
   }
   
   protected void populateFromEntity(Entity entity, UserContext ctx) {
     logger.entering(_CLASS,"populateFromEntity(Entity,UserContext)",
          new Object[] {entity,ctx});
     if (entity != null) {
        super.populateFromEntity(entity, ctx);
        setLineItemNumber(getPropertyAsInteger(entity,"lineItemNumber"));
     } else {
        logger.fine("The entity object is null.");
     } // END if (entity != null)
     logger.exiting(_CLASS,"populateFromEntity(Entity,UserContext)");
   }
   
   @Override
   public void populateFromObject(T lineItem) {
      logger.entering(_CLASS, "populateFromObject(LineItem)",lineItem);
      if (lineItem != null) {
         super.populateFromObject(lineItem);
         setLineItemNumber(lineItem.getLineItemNumber());
      } else {
         logger.fine("The lineItem parameter is null.");
      } // END if (lineItem != null)
      logger.exiting(_CLASS, "populateFromObject(LineItem)");
   }
   
   @Override
   public String toPropertyString() {
      logger.entering(_CLASS, "toPropertyString()");
      StringBuilder sb=null;
      String returnValue=null;
      
      returnValue=super.toPropertyString();
      sb=new StringBuilder(returnValue.length() + 1024);
      sb.append(returnValue);
      if (sb.length() > 0) {
         sb.append(", ");
      } // END if (sb.length() > 0)
      sb.append("lineItemNumber=\"").append(getLineItemNumber()).append("\"");
      returnValue=sb.toString();
      logger.exiting(_CLASS, "toPropertyString()",returnValue);
      return returnValue;
   }
   
   @Override
   public String toString() {
      logger.entering(_CLASS,"toString()");
      String result=null;
      StringBuilder sb=null;
      
      sb=new StringBuilder(1024);
      sb.append(_CLASS).append(" [");
      sb.append(toPropertyString());
      sb.append("]");
      result=sb.toString();
      
      logger.entering(_CLASS,"toString()",result);
      return result;
    }  
   
   //********** Accessor Methods
   
   //*****
   @Override 
   public final int getLineItemNumber() {
     logger.entering(_CLASS, "getLineItemNumber()");
     logger.exiting(_CLASS, "getLineItemNumber()",this.lineItemNumber);
     return this.lineItemNumber;    
   }
   @Override
   public final void setLineItemNumber(int lineItemNumber) {
     logger.entering(_CLASS,"setLineItemNumber(int)",lineItemNumber);
     this.lineItemNumber=lineItemNumber;
     logger.exiting(_CLASS,"setLineItemNumber(int)");
   }  
   //***** kind
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentLineItemImpl._KIND);
      return PersistentLineItemImpl._KIND;
   }   
}