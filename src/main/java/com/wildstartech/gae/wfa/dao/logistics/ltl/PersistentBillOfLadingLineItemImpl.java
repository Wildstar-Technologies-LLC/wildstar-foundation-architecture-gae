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
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentBillOfLadingLineItem;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem;

/**
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 * @version 0.1, 2015.01.08
 */
public class PersistentBillOfLadingLineItemImpl 
extends WildObjectImpl<BillOfLadingLineItem>
implements PersistentBillOfLadingLineItem {
  /** Used in object serialization. */
  private static final long serialVersionUID = -254258372332992236L;
  private static String _CLASS=
      PersistentBillOfLadingLineItemImpl.class.getName();
  private static Logger logger=Logger.getLogger(_CLASS);
  
  protected static final String _KIND=
      "com.wildstartech.wfa.logistics.ltl.BillOfLadingLinteItem";
  
  private boolean isHazardous=false;
  private int numberOfShippingUnits=0;
  private int numberOfPieces=0;
  private double length=0.0;
  private double height=0.0;
  private double width=0.0;
  private String description=null;
  private String itemClass=null;
  private String nmfcItemNumber=null;
  private String unitOfMeasure=null;
  private String kindOfPackage=null;
  
  /**
   * Default, no-argumnet constructor.
   */
  public PersistentBillOfLadingLineItemImpl() {
    logger.entering(_CLASS,"BillOfLadingLineItemImpl()");
    logger.exiting(_CLASS,"BillOfLadingLineItemImpl()");
  }
  
  //********** kind
  public String getKind() {
    logger.entering(_CLASS,"getKind()");
    logger.exiting(_CLASS,"getKind()",_KIND);
    return _KIND;
  }
  
  //**********
  protected void populateEntity(Entity entity) {
    logger.entering(_CLASS,"populateEntity(Entity)");
    if (entity != null) {
        super.populateEntity(entity);
        //***** description
        entity.setProperty("description",this.description);
        //***** height
        entity.setProperty("height",this.height);
        //***** isHazardous
        entity.setProperty("isHazardous",this.isHazardous);
        //***** itemClass
        entity.setProperty("itemClass",this.itemClass);
        //***** kindOfPackage
        entity.setProperty("kindOfPackage",this.kindOfPackage);
        //***** length
        entity.setProperty("length",this.length);
        //***** nmfcItemNumber
        entity.setProperty("nmfcItemNumber",this.nmfcItemNumber);
        //***** numberOfPieces
        entity.setProperty("numberOfPieces",this.numberOfPieces);
        //***** numberOfShippingUnits
        entity.setProperty("numberOfShippingUnits",this.numberOfShippingUnits);
        //***** unitOfMeasure
        entity.setProperty("unitOfMeasure",this.unitOfMeasure);
    }
    logger.exiting(_CLASS, "populateEntity(Entity)");
  }
  //**********
  protected void populateFromEntity(Entity entity,UserContext ctx) {
    logger.entering(_CLASS,"populateFromEntity(Entity,UserContext)");
    Object obj=null;        
    
    if (entity != null) {
        super.populateFromEntity(entity,ctx);
        //***** description
        obj=entity.getProperty("description");
        if (obj instanceof String) {
            this.description=(String) obj;
        } // END if (obj instanceof String)        
        //***** height
        obj=entity.getProperty("height");
        if (obj instanceof Double) {
            this.height=(Double) obj;
        } // END if (obj instanceof String)
        //***** isHazardous
        obj=entity.getProperty("isHazardous");
        if (obj instanceof Boolean) {
            this.isHazardous=(Boolean) obj;
        } // END if (obj instanceof String)
        //***** itemClass
        obj=entity.getProperty("itemClass");
        if (obj instanceof String) {
            this.itemClass=(String) obj;
        } // END if (obj instanceof String)
        //***** kindOfPackage
        obj=entity.getProperty("kindOfPackage");
        if (obj instanceof String) {
            this.kindOfPackage=(String) obj;
        } // END if (obj instanceof String)
        //***** length
        obj=entity.getProperty("length");
        if (obj instanceof Double) {
            this.length=(Double) obj;
        } // END if (obj instanceof String)
        //***** nmfcItemNumber
        obj=entity.getProperty("nmfcItemNumber");
        if (obj instanceof String) {
            this.nmfcItemNumber=(String) obj;
        } // END if (obj instanceof String)
        //***** numberOfPieces
        obj=entity.getProperty("numberOfPieces");
        if (obj instanceof Integer) {
            this.numberOfPieces=(Integer) obj;
        } // END if (obj instanceof String)
        //***** numberOfShippingUnits
        obj=entity.getProperty("numberOfShippingUnits");
        if (obj instanceof Integer) {
            this.numberOfShippingUnits=(Integer) obj;
        } // END if (obj instanceof String)
        //***** unitOfMeasure
        obj=entity.getProperty("unitOfMeasure");
        if (obj instanceof String) {
            this.unitOfMeasure=(String) obj;
        } // END if (obj instanceof String)        
    } // END if (entity != null) 
    logger.exiting(_CLASS, "populateEntity(Entity)");
  }
  
  public void populateFromObject(BillOfLadingLineItem lineItem) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }
  //********** isHazardous
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#isHazardous()
   */
  @Override
  public boolean isHazardous() {
    logger.entering(_CLASS,"isHazardous()");
    logger.exiting(_CLASS,"isHazardous()",this.isHazardous);
    return this.isHazardous;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#setHazardous(boolean)
   */
  @Override
  public void setHazardous(boolean isHazardous) {
    logger.entering(_CLASS,"setHazardous(boolean)",isHazardous);
    this.isHazardous = isHazardous;
    logger.exiting(_CLASS,"setHazardous(boolean)");
  }
  //********** numberOfShippingUnits
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#getNumberOfShippingUnits()
   */
  @Override
  public int getNumberOfShippingUnits() {
    logger.entering(_CLASS,"getNumberOfShippingUnits()");
    logger.exiting(_CLASS,"getNumberOfShippingUnits()",
        this.numberOfShippingUnits);
    return numberOfShippingUnits;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#setNumberOfShippingUnits(int)
   */
  @Override
  public void setNumberOfShippingUnits(int numberOfShippingUnits) {
    logger.entering(_CLASS,"setNumberOfShippingUnits(int)",
        numberOfShippingUnits);
    this.numberOfShippingUnits = numberOfShippingUnits;
    logger.exiting(_CLASS,"setNumberOfShippingUnits(int)");
  }
  //********** numberOfPieces 
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#getNumberOfPieces()
   */
  @Override
  public int getNumberOfPieces() {
    logger.entering(_CLASS,"getNumberOfPieces()");
    logger.exiting(_CLASS,"getNumberOfPieces()",this.numberOfPieces);
    return this.numberOfPieces;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#setNumberOfPieces(int)
   */
  @Override
  public void setNumberOfPieces(int numberOfPieces) {
    logger.entering(_CLASS,"setNumberOfPieces(int)",numberOfPieces);
    this.numberOfPieces = numberOfPieces;
    logger.exiting(_CLASS,"setNumberOfPieces(int)");
  }
  //********** length 
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#getLength()
   */
  @Override
  public double getLength() {
    logger.entering(_CLASS,"getLength()");
    logger.exiting(_CLASS,"getLength()",this.length);
    return this.length;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#setLength(double)
   */
  @Override
  public void setLength(double length) {
    logger.entering(_CLASS,"setLength(double)",length);
    this.length = length;
    logger.exiting(_CLASS,"setLength(double)");
  }
  //********** height
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#getHeight()
   */
  @Override
  public double getHeight() {
    logger.entering(_CLASS,"getHeight()");
    logger.exiting(_CLASS,"getHeight()",this.height);
    return this.height;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#setHeight(double)
   */
  @Override
  public void setHeight(double height) {
    logger.entering(_CLASS,"setHeight(double)",height);
    this.height = height;
    logger.exiting(_CLASS,"setHeight(double)");
  }
  //********** width
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#getWidth()
   */
  @Override
  public double getWidth() {
    logger.entering(_CLASS,"getWidth()");
    logger.exiting(_CLASS,"getWidth()",this.width);
    return this.width;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#setWidth(double)
   */
  @Override
  public void setWidth(double width) {
    logger.entering(_CLASS,"setWidth(double)",width);
    this.width = width;
    logger.exiting(_CLASS,"setWidth(double)");
  }
  //********** description
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#getDescription()
   */
  @Override
  public String getDescription() {
    logger.entering(_CLASS,"getDescription()");
    logger.exiting(_CLASS,"getDescription()",this.description);
    return this.description;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#setDescription(java.lang.String)
   */
  @Override
  public void setDescription(String description) {
    logger.entering(_CLASS,"setDescription(String)",description);
    this.description = description;
    logger.exiting(_CLASS,"setDescription(String)");
  }
  //********** itemClass
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#getItemClass()
   */
  @Override
  public String getItemClass() {
    logger.entering(_CLASS,"getItemClass()");
    logger.exiting(_CLASS,"getItemClass()",this.itemClass);
    return this.itemClass;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#setItemClass(java.lang.String)
   */
  @Override
  public void setItemClass(String itemClass) {
    logger.entering(_CLASS,"setItemClass(String)",itemClass);
    this.itemClass = itemClass;
    logger.exiting(_CLASS,"setItemClass(String)");
  }
  //********** nmfcItemNumber
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#getNmfcItemNumber()
   */
  @Override
  public String getNmfcItemNumber() {
    logger.entering(_CLASS,"getNmfcItemNumber()");
    logger.exiting(_CLASS,"getNmfcItemNumber()",this.nmfcItemNumber);
    return nmfcItemNumber;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#setNmfcItemNumber(java.lang.String)
   */
  @Override
  public void setNmfcItemNumber(String nmfcItemNumber) {
    logger.entering(_CLASS,"setNmfcItemNumber(String)",nmfcItemNumber);
    this.nmfcItemNumber = nmfcItemNumber;
    logger.exiting(_CLASS,"setNmfcItemNumber(String)");
  }
  //********** unitOfMeasure
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#getUnitOfMeasure()
   */
  @Override
  public String getUnitOfMeasure() {
    logger.entering(_CLASS,"getUnitOfMeasure()");
    logger.exiting(_CLASS,"getUnitOfMeasure()",this.unitOfMeasure);
    return this.unitOfMeasure;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#setUnitOfMeasure(java.lang.String)
   */
  @Override
  public void setUnitOfMeasure(String unitOfMeasure) {
    logger.entering(_CLASS,"setUnitOfMeasure(String)",unitOfMeasure);
    this.unitOfMeasure = unitOfMeasure;
    logger.exiting(_CLASS,"setUnitOfMeasure(String)");
  }
  //********** kindOfPackage
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#getKindOfPackage()
   */
  @Override
  public String getKindOfPackage() {
    logger.entering(_CLASS,"getKindOfPackage()");
    logger.exiting(_CLASS,"getKindOfPackage()",this.kindOfPackage);
    return this.kindOfPackage;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLadingLineItem#setKindOfPackage(java.lang.String)
   */
  @Override
  public void setKindOfPackage(String kindOfPackage) {
    logger.entering(_CLASS,"setKindOfPackage(String)",kindOfPackage);
    this.kindOfPackage = kindOfPackage;
    logger.exiting(_CLASS,"setKindOfPackage(String)");
  }
}