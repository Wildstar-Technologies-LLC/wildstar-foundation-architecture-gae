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
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.logistics.ltl.EditableCommodityLineItem;

public class PersistentEditableCommodityLineItemImpl<T extends EditableCommodityLineItem> 
extends PersistentCommodityLineItemImpl<T> 
implements EditableCommodityLineItem {
   /** Used in object serialization. */
   private static final long serialVersionUID = 2005174249723994097L;
   private static final String _CLASS=
         PersistentEditableCommodityLineItemImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   /*
    * Indicates if the description can be edited.
    */
   private boolean customDescription=true;
   /*
    * Indicates if length, width and height can be edited.
    */
   private boolean customDimensions=true;
   /*
    * Indicates if the weight can be edited.
    */
   private boolean customWeight=true;
   
   //********** Utility methods
   
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.entering(_CLASS, "getKind()",
            PersistentCommodityLineItemImpl._KIND);
      return PersistentCommodityLineItemImpl._KIND;
   }

   @Override
   protected void populateEntity(Entity entity) {
     logger.entering(_CLASS,"populateEntity(Entity)",entity);
     if (entity != null) {
        super.populateEntity(entity);
        entity.setProperty("customDescription",isCustomDescription());
        entity.setProperty("customDimensions",isCustomDimensions());
        entity.setProperty("customWeight",isCustomWeight());     
     } else {
       logger.severe("The Entity object is null.");
     } // END if (entity != null)
     
     logger.exiting(_CLASS,"populateEntity(Entity)");
   }
   
   @Override 
   protected void populateFromEntity(Entity entity, UserContext ctx) {
     logger.entering(_CLASS,"populateFromEntity(Entity,UserContext)",
         new Object[] {entity, ctx});
     if (entity != null) {
       super.populateFromEntity(entity, ctx);
       // customDescription
       setCustomDescription(getPropertyAsBoolean(
           entity,"customDescription",true));
       // customDimensions
       setCustomDimensions(getPropertyAsBoolean(entity,"customDimensions",true));
       // customWeight
       setCustomWeight(getPropertyAsBoolean(entity,"customWeight",true));
     } else {
       logger.warning("The entity passed was null.");
     } // END if (entity != null)
     logger.exiting(_CLASS,"populateFromEntity(Entity,UserContext)");
   }
   
   @Override
   public void populateFromObject(T editableCommodityLineItem) {
     logger.entering(_CLASS,"populateFromObject(T)",editableCommodityLineItem);
     if (editableCommodityLineItem != null) {
        super.populateFromObject(editableCommodityLineItem);        
        // customDescription
        setCustomDescription(editableCommodityLineItem.isCustomDescription());
        // customDimensions
        setCustomDimensions(editableCommodityLineItem.isCustomDimensions());
        // customWeight
        setCustomWeight(editableCommodityLineItem.isCustomWeight());
     } else {
       logger.warning("The MockEditableCommodityLineItem passed was null.");
     } // END if(qli != null)
     logger.exiting(_CLASS,"populateFromObject(T)");
   }
   
   /**
    * Calculate the hashcode for the PersistentAccessorialCharge
    */
   public int hashCode() {
     logger.entering(_CLASS, "hashCode()");
     int hashCode=0;
     String tmpStr=null;
     
     tmpStr=toString();
     hashCode=tmpStr.hashCode();
     
     logger.exiting(_CLASS, "hashCode()",hashCode);
     return hashCode;
   }
   
   public String toPropertyString() {
      logger.entering(_CLASS, "toPropertyString()");
      String returnValue=null;
      StringBuilder sb=null;
      
      returnValue=super.toPropertyString();
      sb=new StringBuilder(returnValue.length() + 1024);
      sb.append(returnValue);
      if (sb.length() > 0) sb.append(", ");
      sb.append("customDescription=\"").append(
            isCustomDescription()).append(", ");
      sb.append("customDimenstions=\"").append(
            isCustomDimensions()).append(", ");
      sb.append("customWeight=\"").append(isCustomWeight()).append("\"");
      returnValue=sb.toString();
      
      logger.entering(_CLASS, "toPropertyString()",returnValue);
      return returnValue;
   }
   public String toString() {
     logger.entering(_CLASS,"toString()");
     String result=null;
     StringBuilder sb=null;
     
     sb=new StringBuilder(256);
     sb.append("PersistentEditableCommodityLineItemImpl [");
     sb.append(toPropertyString());
     sb.append("]'");
     result=sb.toString();     
     
     logger.entering(_CLASS,"toString()",result);
     return result;
   }
   //********** accessor methods
   //***** customDescription
   @Override
   public final boolean isCustomDescription() {
     logger.entering(_CLASS,"isCustomDescription()");
     logger.exiting(_CLASS,"isCustomDescription()",this.customDescription);
     return this.customDescription;
   }
   @Override
   public final void setCustomDescription(boolean customDescription) {
     logger.entering(_CLASS,"setCustomDescription(boolean)",customDescription);
     this.customDescription=customDescription;
     logger.exiting(_CLASS,"setCustomDescription(boolean)");
   }
   //***** customDimensions
   @Override
   public final boolean isCustomDimensions() {
     logger.entering(_CLASS,"isCustomDimensions()");
     logger.exiting(_CLASS,"isCustomDimensions()",this.customDimensions);
     return this.customDimensions;
   }
   @Override
   public final void setCustomDimensions(boolean customDimensions) {
     logger.entering(_CLASS,"setCustomDimensions(boolean)",customDimensions);
     this.customDimensions=customDimensions;
     logger.exiting(_CLASS,"setCustomDimensions(boolean)");
   }
   //***** customWeight
   @Override
   public final boolean isCustomWeight() {
     logger.entering(_CLASS,"isCustomWeight()");
     logger.exiting(_CLASS,"isCustomWeight()",this.customWeight);
     return this.customWeight;
   }
   @Override
   public final void setCustomWeight(boolean customWeight) {
     logger.entering(_CLASS,"setCustomWeight(boolean)",customWeight);
     this.customWeight=customWeight;
     logger.exiting(_CLASS,"setCustomWeight(boolean)");
   }
}
