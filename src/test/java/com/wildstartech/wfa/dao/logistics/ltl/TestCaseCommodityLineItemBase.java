/*
 * Copyright (c) 2013 - 2015 Wildstar Technologies, LLC.
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
package com.wildstartech.wfa.dao.logistics.ltl;

import com.wildstartech.wfa.logistics.ltl.CommodityLineItem;

public class TestCaseCommodityLineItemBase 
extends TestCaseLineItemBase
implements CommodityLineItem {
   /*
    * The number of cubic feet of space occupied by commodity.
    */
   private int cube=0;
   /*
    * The height of the item (in inches).
    */
   private int height=0;
   /* 
    * The length of the item (in inches).
    */
   private int length=0;
   
   /*
    * The weight of a single instance of the object represented by the line item
    * in pounds.
    */
   private int weight=0;
   /*
    * The width of the item (in inches).
    */
   private int width=0;
   /*
    * A count of the number of the same item.
    */
   private int quantity=0;
   /*
    * A free-form text description of the item.
    */
   private String description="";
   
   /**
    * Packaging Type
    */
   private String packagingType="";
   /**
    * The product identifier.
    */
   private String productId="";
   
   
   /**
    * Default, no-argument constructor.
    */
   public TestCaseCommodityLineItemBase() {
      
   }
   
   //********** BEGIN: Accessor methods
   //***** cube
   public final int getCube() {
      return this.cube;
   }
   public final void setCube(int cube) {
      this.cube=cube;
   }
   //***** description
   public final String getDescription() {
      return this.description;
   }
   public final void setDescription(String description) {
      this.description = description;
   }
   
   //***** height
   public final int getHeight() {
      return this.height;
   }
   public final void setHeight(int height) {
      this.height = height;
   }
   
   //***** length
   public final int getLength() {
      return this.length;
   }
   public final void setLength(int length) {
      this.length = length;
   }
   
   //***** packagingType
   public final String getPackagingType() {
      return this.packagingType;
   }
   public final void setPackagingType(String packagingType) {
      this.packagingType=packagingType;
   }
   //***** productId
   public final String getProductId() {
      return this.productId;      
   }
   public final void setProductId(String productId) {
      this.productId=productId;
   }
   
   //***** quantity
   public final int getQuantity() {
      return this.quantity;
   }
   public final void setQuantity(int quantity) {
      this.quantity = quantity;
   }   
   //***** totalCube
   public final int getTotalCube() {
      return this.cube * this.quantity;
   }
   
   //***** weight
   public final int getWeight() {
      return this.weight;
   }
   public final void setWeight(int weight) {
      this.weight = weight;
   }
   public int getTotalWeight() {
      return this.weight * this.quantity;
   }
   //***** width
   public final int getWidth() {
      return this.width;
   }
   public final void setWidth(int width) {
      this.width = width;
   }   
   //********** END: Accessor Methods 
}