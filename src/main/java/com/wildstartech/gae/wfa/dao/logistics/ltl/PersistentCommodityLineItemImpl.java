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
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.CommodityLineItem;

public class PersistentCommodityLineItemImpl<T extends CommodityLineItem>
extends PersistentLineItemImpl<T> implements CommodityLineItem {
   /** Used in object serialization. */
   private static final long serialVersionUID = 3668676313847479433L;
   private static final String _CLASS=
         PersistentCommodityLineItemImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   public static final String _KIND=
         "com.wildstartech.wfa.logistics.ltl.CommodityLineItem";

   /*
    * The height of the item (in inches).
    */
   private int height=1;
   /* 
    * The length of the item (in inches).
    */
   private int length=1;
   
   /*
    * The weight of a single instance of the object represented by the line item
    * in pounds.
    */
   private int weight=1;
   /*
    * The width of the item (in inches).
    */
   private int width=1;
   /*
    * A count of the number of the same item.
    */
   private int quantity=1;  
   /* */
   private String packagingType = "";
   /* */
   private String productId = "";
   
   /**
    * Default, no-argument constructor.
    */
   public PersistentCommodityLineItemImpl() {
      super();
      logger.entering(_CLASS, "PersistentCommodityLineItemImpl()");
      logger.exiting(_CLASS, "PersistentCommodityLineItemImpl()");
   }
   
   //********** Utility methods
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      PersistentCommodityLineItemImpl other = (PersistentCommodityLineItemImpl) obj;
      if (height != other.height)
         return false;
      if (length != other.length)
         return false;
      if (packagingType == null) {
         if (other.packagingType != null)
            return false;
      } else if (!packagingType.equals(other.packagingType))
         return false;
      if (productId == null) {
         if (other.productId != null)
            return false;
      } else if (!productId.equals(other.productId))
         return false;
      if (quantity != other.quantity)
         return false;
      if (weight != other.weight)
         return false;
      if (width != other.width)
         return false;
      return true;
   }
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + height;
      result = prime * result + length;
      result = prime * result
            + ((packagingType == null) ? 0 : packagingType.hashCode());
      result = prime * result
            + ((productId == null) ? 0 : productId.hashCode());
      result = prime * result + quantity;
      result = prime * result + weight;
      result = prime * result + width;
      return result;
   }
   
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
       entity.setProperty("height",getHeight());
       entity.setProperty("length",getLength());
       entity.setProperty("packagingType", getPackagingType());
       entity.setProperty("productId", getProductId());
       entity.setProperty("quantity",getQuantity());
       entity.setProperty("weight",getWeight());
       entity.setProperty("width",getWidth());      
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
       // height
       setHeight(getPropertyAsInteger(entity,"height"));
       // length
       setLength(getPropertyAsInteger(entity,"length"));
       // packagingType
       setPackagingType(getPropertyAsString(entity,"packagingType"));
       // productId
       setProductId(getPropertyAsString(entity,"productId"));
       // quantity
       setQuantity(getPropertyAsInteger(entity,"quantity"));
       // weight
       setWeight(getPropertyAsInteger(entity,"weight"));
       // width
       setWidth(getPropertyAsInteger(entity,"width"));
     } else {
       logger.warning("The entity passed was null.");
     } // END if (entity != null)
     logger.exiting(_CLASS,"populateFromEntity(Entity,UserContext)");
   }
   
   @Override
   public void populateFromObject(T commodityLineItem) {
     logger.entering(_CLASS,"populateFromObject(T)",commodityLineItem);
     if (commodityLineItem != null) {
       super.populateFromObject(commodityLineItem);
       // height
       setHeight(commodityLineItem.getHeight());
       // length
       setLength(commodityLineItem.getLength());
       // packagingType
       setPackagingType(commodityLineItem.getPackagingType());
       // productId
       setProductId(commodityLineItem.getProductId());
       // quantity
       setQuantity(commodityLineItem.getQuantity());
       // weight
       setWeight(commodityLineItem.getWeight());
       // width
       setWidth(commodityLineItem.getWidth());
     } else {
       logger.warning("The CommodityLineItem passed was null.");
     } // END if(qli != null)
     logger.exiting(_CLASS,"populateFromObject(T)");
   }

   @Override
   public String toPropertyString() {
      logger.entering(_CLASS,"toPropertyString()");
      String returnValue=null;
      StringBuilder sb=null;
      
      returnValue=super.toPropertyString();
      sb=new StringBuilder(returnValue.length() + 1024);
      sb.append(returnValue);
      if (sb.length() > 0) sb.append(", ");
      sb.append("length=").append(getHeight()).append(", ");
      sb.append("width=").append(getWidth()).append(", ");
      sb.append("height=").append(getHeight()).append(", ");
      sb.append("productId=").append(getProductId()).append(",");
      sb.append("weight=").append(getWeight()).append(", ");
      sb.append("quantity=").append(getQuantity());
      returnValue=sb.toString();
      
      logger.exiting(_CLASS, "toPropertyString()",returnValue);
      return returnValue;
   }
   @Override
   public String toString() {
     logger.entering(_CLASS,"toString()");
     String result=null;
     StringBuilder sb=null;
     
     sb=new StringBuilder(256);
     sb.append(_CLASS).append(" [");
     sb.append(toPropertyString());
     sb.append("]");
     result=sb.toString();
     
     logger.entering(_CLASS,"toString()",result);
     return result;
   }

   //***** accessor methods
   
   //***** cube
   /**
    * Returns the number of cubic feet occupied by the object in question.
    * 
    * <p>When calculating the cube value, standard rounding will apply such that 
    * fractional cubic feet less than 0.5 will be rounded <em>down</em> to the 
    * nearest whole number.  Fractional cubic feel 0.5 and up will be rounded 
    * <em>up</em> to the nearest whole number.</p>
    * 
    * @return An integer value indicating the number of volume of space occupied 
    * by the item represented by this line item.
    */
   @Override
   public final int getCube() {
     logger.entering(_CLASS,"getCube()");
     int cubeValue=0;
     double calculatedValue=0d;    
     
     calculatedValue=(this.length*this.width*this.height)/1728d;
     cubeValue=new Long(Math.round(calculatedValue)).intValue();       
     logger.exiting(_CLASS,"getCube()",cubeValue);
     return cubeValue;
   }
   /**
    * The cube of the line item multiplied by the quantity.
    * @return
    */
   @Override
   public final int getTotalCube() {
     logger.entering(_CLASS,"getTotalCube()");
     int total=0;
     total=(this.length*this.width*this.height*this.quantity) / 1728;
     logger.entering(_CLASS,"getTotalCube()",total);
     return total;
   }   
   
   //***** height
   @Override
   public final int getHeight() {
     logger.entering(_CLASS,"getHeight()");
     logger.exiting(_CLASS,"getHeight()",this.height);
     return this.height;
   }
   @Override
   public final void setHeight(int height) {
     logger.entering(_CLASS,"setHeight(int)",height);
     if (height < 1) {
       this.height=1;
     } else {
       this.height=height;
     } // END if (height < 1)    
     logger.exiting(_CLASS,"setHeight(int)");
   }

   //***** length
   @Override
   public final int getLength() {
     logger.entering(_CLASS,"getLength()");
     logger.exiting(_CLASS,"getLength()",this.length);    
     return this.length;
   }
   @Override
   public final void setLength(int length) {
     logger.entering(_CLASS,"setLength(int)",length);
     if (length < 1) {
       this.length=1;
     } else {
       this.length=length;
     } // END if (length < 1)
     logger.exiting(_CLASS,"setLength(int)");    
   }
   //***** packagingType 
   @Override
   public final String getPackagingType() {
      logger.entering(_CLASS, "getPackagingType()");
      logger.exiting(_CLASS, "getPackagingType()",this.packagingType);
      return this.packagingType;
   }
   @Override 
   public final void setPackagingType(String packagingType) {
      logger.entering(_CLASS, "setPackagingType(String)",packagingType);
      this.packagingType=defaultValue(packagingType);
      logger.exiting(_CLASS, "setPackagingType(String)");
   }
   
   //***** product Id
   @Override
   public final String getProductId() {
      logger.entering(_CLASS, "getProductId()");
      logger.exiting(_CLASS, "getProductId()",this.productId);
      return this.productId;
   }
   @Override
   public final void setProductId(String productId) {
      logger.entering(_CLASS, "setProductId(String productId)",productId);
      this.productId=defaultValue(productId);
      logger.exiting(_CLASS, "setProductId(String)");
   }
   
   //***** quantity
   @Override
   public final int getQuantity() {
     logger.entering(_CLASS, "getQuantity()");
     logger.exiting(_CLASS, "getQuantity()",this.quantity);
     return this.quantity;
   }
   @Override
   public final void setQuantity(int quantity) {
     logger.entering(_CLASS, "setQuantity(int)",quantity);
     if (quantity < 1) {
       this.quantity=1;
     } else {
       this.quantity=quantity;
     } // END if (quantity < 1)
     logger.entering(_CLASS, "setQuantity(int)",quantity);    
   }
   
   //***** weight
   @Override
   public final int getWeight() {
     logger.entering(_CLASS,"getWeight()");
     logger.exiting(_CLASS,"getWeight()",this.weight);
     return this.weight;
   }

   @Override
   public final void setWeight(int weight) {
     logger.entering(_CLASS,"setWeight(weight)",weight);
     if (weight < 1) {
       this.weight=1;
     } else {
       this.weight=weight;
     } // END if (weight < 1)
     logger.exiting(_CLASS,"setWeight(weight)");    
   }
   
   @Override
   public final int getTotalWeight() {
      logger.entering(_CLASS, "getTotalWeight()");
      int quantity=0;
      int totalWeight=0;
      int weight=0;
      
      quantity=getQuantity();
      weight=getWeight();
      totalWeight=quantity * weight;
      
      logger.exiting(_CLASS, "getTotalWeight()",totalWeight);
      return totalWeight;
   }
   //***** width
   @Override
   public final int getWidth() {
     logger.entering(_CLASS,"getWidth()");
     logger.exiting(_CLASS,"getWidth()",this.width);
     return this.width;
   }
   @Override
   public final void setWidth(int width) {
     logger.entering(_CLASS,"setWidth(int)",width);
     if (this.width < 1) {
       this.width=1;
     } else {
       this.width=width;
     } // END if (this.width < 1)
     logger.exiting(_CLASS,"setWidth(int)");
   }   
}