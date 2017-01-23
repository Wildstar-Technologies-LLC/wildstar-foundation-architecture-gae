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
package com.wildstartech.gae.wfa.dao.logistics.ltl.freight;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.logistics.ltl.PersistentEditableCommodityLineItemImpl;
import com.wildstartech.wfa.dao.logistics.ltl.freight.PersistentFreightDueInWorkOrderLineItem;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.freight.FreightDueInWorkOrderLineItem;

public class PersistentFreightDueInWorkOrderLineItemImpl
extends PersistentEditableCommodityLineItemImpl<FreightDueInWorkOrderLineItem>
implements PersistentFreightDueInWorkOrderLineItem {
   /** Used in object serialization. */
   private static final long serialVersionUID = 881186131889676484L;
   private static final String _CLASS=
         PersistentFreightDueInWorkOrderLineItemImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);

   static final String _KIND=
         "com.wildstartech.wfa.logistics.ltl.FreightDueInWorkOrderLineItem";
   
   private String workOrderIdentifier="";
   
   //********** acccessor Methods
   public String getWorkOrderIdentifier() {
      logger.entering(_CLASS, "getWorkOrderIdentifier()");
      logger.exiting(_CLASS, "getWorkOrderIdentifier()",
            this.workOrderIdentifier);
      return this.workOrderIdentifier;
   }
   
   public void setWorkOrderIdentifier(String identifier) {
      logger.entering(_CLASS, "setWorkOrderIdentifier(String)", identifier);
      this.workOrderIdentifier=defaultValue(identifier);
      logger.exiting(_CLASS, "setWorkOrderIdentifier(String)");
   }
   //********** utility
   /**
    * Returns type information used by Google's Datastore.
    */
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",
            PersistentFreightDueInWorkOrderLineItemImpl._KIND);
      return PersistentFreightDueInWorkOrderLineItemImpl._KIND;
   }
   
   @Override
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS,"populateEntity(Entity)",entity);
      if (entity != null) {
         super.populateEntity(entity);
         entity.setProperty("workOrderIdentifier", getWorkOrderIdentifier());
      } else {
         logger.severe("The specified Entity object was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS,"populateEntity(Entity)");
   }
   
   @Override
   protected void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity)",entity);
      if (entity != null) {
         super.populateFromEntity(entity, ctx);
         setWorkOrderIdentifier(getPropertyAsString(entity, "workOrderIdentifier"));
      } else {
         logger.severe("The specified Entity object was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateFromEntity(Entity)");
   }
   
   @Override
   public void populateFromObject(FreightDueInWorkOrderLineItem item) {
      logger.entering(_CLASS, 
            "populateFromObject(FreightDueInWorkOrderLineItem)",
            item);
      if (item != null) {
         super.populateFromObject(item);
         setWorkOrderIdentifier(item.getWorkOrderIdentifier());
      } else {
         logger.severe("The specified FreightDueInWorkOrderLineItem is null.");
      } // END if (wo != null)
      logger.exiting(_CLASS, 
            "populateFromObject(FreightDueInWorkOrderLineItem)");
   }   
   
   public String toPropertyString() {
      logger.entering(_CLASS, "toPropertyString()");
      String returnValue=null;
      StringBuilder sb=null;
      
      returnValue=super.toPropertyString();
      sb=new StringBuilder(returnValue.length() + 1024);
      sb.append(returnValue);
      if (sb.length() > 0) sb.append(", ");
      sb.append("workOrderIdentifier=\"").append(
            getWorkOrderIdentifier()).append("\", ");
      
      logger.entering(_CLASS, "toPropertyString()",returnValue);
      return returnValue;
   }
   
   public String toString() {
      logger.entering(_CLASS, "toPropertyString()");
      String returnValue=null;
      StringBuilder sb=null;
      
      sb=new StringBuilder(1024);
      sb.append(_CLASS).append(" [").append(toPropertyString()).append("]");
      returnValue=sb.toString();
      
      logger.entering(_CLASS, "toPropertyString()",returnValue);
      return returnValue;
   }
   
   public void updateFromObject(FreightDueInWorkOrderLineItem item) {
      logger.entering(_CLASS, "updateFromObject(FreightDueInWorkOrderLineItem)",
            item);
      if (item != null) {
         populateFromObject(item);
      } else {
         logger.warning("The item parameter was null.");
      } // END if (item != null)
      logger.exiting(_CLASS, "updateFromObject(FreightDueInWorkOrderLineItem)");
   }
}