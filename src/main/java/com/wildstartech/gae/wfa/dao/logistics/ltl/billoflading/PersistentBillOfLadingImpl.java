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
package com.wildstartech.gae.wfa.dao.logistics.ltl.billoflading;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.logistics.ltl.billoflading.PersistentBillOfLading;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.billoflading.BillOfLading;
import com.wildstartech.wfa.logistics.ltl.billoflading.BillOfLadingLineItem;

/**
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 * @version 0.1, 2015.01.08
 */
public class PersistentBillOfLadingImpl 
extends WildObjectImpl<BillOfLading>
implements PersistentBillOfLading {
  /** Used in object serialization. */
  private static final long serialVersionUID = 4590922937236469310L;
  private static final String _CLASS=PersistentBillOfLadingImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  
  protected static final String _KIND=
      "com.wildstartech.wfa.logistics.ltl.BillOfLading";
  
  private boolean isCollect=false;
  private Date date=null;
  private String blNumber=null;
  private String carrier=null;
  private String consigneeCompany=null;
  private String consigneeEmail=null;
  private String consigneeName=null;
  private String consigneePhone=null;
  private String destinationCity=null;
  private String destinationState=null;
  private String destinationZip=null;
  private String freightCountedBy=null;
  private String invoiceeName=null;
  private String invoiceeCompany=null;
  private String invoiceeAddress=null;
  private String invoiceeCity=null;
  private String invoiceeState=null;
  private String invoiceeZip=null;
  private String originCity=null;
  private String originState=null;
  private String originZip=null;
  private String purchaseOrderNumber=null;
  private String proNumber=null;
  private String shipperAddress=null;
  private String shipperCity=null;
  private String shipperCompany=null;
  private String shipperName=null;
  private String shipperNumber=null;
  private String shipperState=null;
  private String shipperZip=null;
  private String specialInstructions=null;
  private String trailerLoadedBy=null;
  private String trailerNumber=null;
  private List<BillOfLadingLineItem> lineItems=null;
   
  /**
   * Default, no argument constructor.
   */
  public PersistentBillOfLadingImpl() {
    logger.entering(_CLASS,"BillOfLadingImpl()");
    logger.exiting(_CLASS,"BillOfLadingImpl()");
  }
  
  //**********
  /**
   * Returns the Kind of object represented by this class.
   */
  @Override
  public String getKind() {
    logger.entering(_CLASS,"getKind()");
    logger.exiting(_CLASS,"getKind()",_KIND);
    return PersistentBillOfLadingImpl._KIND;
  }
  
  //**********
  protected void populateEntity(Entity entity) {
    logger.entering(_CLASS,"populateEntity(Entity)");
    if (entity != null) {
        super.populateEntity(entity);
        //***** blNumber
        entity.setProperty("blNumber",this.blNumber);
        //***** carrier
        entity.setProperty("carrier",this.carrier);
        //***** consigneeCompany
        entity.setProperty("consigneeCompany",this.consigneeCompany);
        //***** consigneeEmail
        entity.setProperty("consigneeEmail",this.consigneeEmail);
        //***** consigneeName
        entity.setProperty("consigneeName",this.consigneeName);
        //***** consigneePhone
        entity.setProperty("consigneePhone",this.consigneePhone);
        //***** date
        entity.setProperty("date",this.date);
        //***** destinationCity
        entity.setProperty("destinationCity",this.destinationCity);
        //***** destinationState
        entity.setProperty("destinationState",this.destinationState);
        //***** destinationZip
        entity.setProperty("destinationZip",this.destinationZip);
        //***** freightCountedBy
        entity.setProperty("freightCountedBy",this.freightCountedBy);
        //***** invoiceeAddress
        entity.setProperty("invoiceeAddress",this.invoiceeAddress);
        //***** invoiceeCity
        entity.setProperty("invoiceeCity",this.invoiceeCity);
        //***** invoiceeCompany
        entity.setProperty("invoiceeCompany",this.invoiceeCompany);
        //***** invoiceeName
        entity.setProperty("invoiceeName",this.invoiceeName);
        //***** invoiceeState
        entity.setProperty("invoiceeState",this.invoiceeState);
        //***** invoiceeZip
        entity.setProperty("invoiceeZip",this.invoiceeZip);
        //***** isCollect
        entity.setProperty("isCollect",this.isCollect);
        //***** originCity
        entity.setProperty("originCity",this.originCity);
        //***** originState
        entity.setProperty("originState",this.originState);
        //***** originZip
        entity.setProperty("originZip",this.originZip);
        //***** proNumber
        entity.setProperty("proNumber",this.proNumber);
        //***** purchaseOrderNumber
        entity.setProperty("purchaseOrderNumber",this.purchaseOrderNumber);
        //***** shipperAddress
        entity.setProperty("shipperAddress",this.shipperAddress);
        //***** shipperCity
        entity.setProperty("shipperCity",this.shipperCity);
        //***** shipperCompany
        entity.setProperty("shipperCompany",this.shipperCompany);
        //***** shipperName
        entity.setProperty("shipperName",this.shipperName);
        //***** shipperNumber
        entity.setProperty("shipperNumber",this.shipperNumber);
        //***** shipperState
        entity.setProperty("shipperState",this.shipperState);
        //***** shipperZip
        entity.setProperty("shipperZip",this.shipperZip);
        //***** specialInstructions
        entity.setProperty("specialInstructions",this.specialInstructions);
        //***** trailerLoadedBy
        entity.setProperty("trailerLoadedBy",this.trailerLoadedBy);
        //***** trailerNumber
        entity.setProperty("trailerNumber",this.trailerNumber);
    } // END if (entity != null)
    logger.exiting(_CLASS,"populateEntity(Entity)");
  }

  /**
   * Populates the current object from the entity
   */
  protected void populateFromEntity(Entity entity,UserContext ctx) {
      logger.entering(_CLASS,"populateFromEntity(Entity,UserContext)",
              new Object[] {entity,ctx});
      Object obj=null;        
      
      if (entity != null) {
          super.populateFromEntity(entity,ctx);
          //***** blNumber
          obj=entity.getProperty("blNumber");
          if (obj instanceof String) {
              this.blNumber=(String) obj;
          } // END if (obj instanceof String)
          //***** carrier
          obj=entity.getProperty("carrier");
          if (obj instanceof String) {
              this.carrier=(String) obj;
          } // END if (obj instanceof String)
          //***** consigneeCompany
          obj=entity.getProperty("consigneeCompany");
          if (obj instanceof String) {
              this.consigneeCompany=(String) obj;
          } // END if (obj instanceof String)
          //***** consigneeEmail
          obj=entity.getProperty("consigneeEmail");
          if (obj instanceof String) {
              this.consigneeEmail=(String) obj;
          } // END if (obj instanceof String)
          //***** consigneeName
          obj=entity.getProperty("consigneeName");
          if (obj instanceof String) {
              this.consigneeName=(String) obj;
          } // END if (obj instanceof String)
          //***** consigneePhone
          obj=entity.getProperty("consigneePhone");
          if (obj instanceof String) {
              this.consigneePhone=(String) obj;
          } // END if (obj instanceof String)
          //***** date
          obj=entity.getProperty("date");
          if (obj instanceof Date) {
              this.date=(Date) obj;
          } // END if (obj instanceof String)
          //***** destinationCity
          obj=entity.getProperty("destinationCity");
          if (obj instanceof String) {
              this.destinationCity=(String) obj;
          } // END if (obj instanceof String)
          //***** destinationState
          obj=entity.getProperty("destinationState");
          if (obj instanceof String) {
              this.destinationState=(String) obj;
          } // END if (obj instanceof String)
          //***** destinationZip
          obj=entity.getProperty("destinationZip");
          if (obj instanceof String) {
              this.destinationZip=(String) obj;
          } // END if (obj instanceof String)
          //***** freightCountedBy
          obj=entity.getProperty("freightCountedBy");
          if (obj instanceof String) {
              this.freightCountedBy=(String) obj;
          } // END if (obj instanceof String)
          //***** invoiceeAddress
          obj=entity.getProperty("invoiceeAddress");
          if (obj instanceof String) {
              this.invoiceeAddress=(String) obj;
          } // END if (obj instanceof String)
          //***** invoiceeCity
          obj=entity.getProperty("invoiceeCity");
          if (obj instanceof String) {
              this.invoiceeCity=(String) obj;
          } // END if (obj instanceof String)
          //***** invoiceeCompany
          obj=entity.getProperty("invoiceeCompany");
          if (obj instanceof String) {
              this.invoiceeCompany=(String) obj;
          } // END if (obj instanceof String)
          //***** invoiceeName
          obj=entity.getProperty("invoiceeName");
          if (obj instanceof String) {
              this.invoiceeName=(String) obj;
          } // END if (obj instanceof String)
          //***** invoiceeState
          obj=entity.getProperty("invoiceeState");
          if (obj instanceof String) {
              this.invoiceeState=(String) obj;
          } // END if (obj instanceof String)
          //***** invoiceeZip
          obj=entity.getProperty("invoiceeZip");
          if (obj instanceof String) {
              this.invoiceeZip=(String) obj;
          } // END if (obj instanceof String)
          //***** isCollect
          obj=entity.getProperty("isCollect");
          if (obj instanceof Boolean) {
              this.isCollect=(Boolean) obj;
          } // END if (obj instanceof Boolean)
          //***** originCity
          obj=entity.getProperty("originCity");
          if (obj instanceof String) {
              this.originCity=(String) obj;
          } // END if (obj instanceof String)
          //***** originState
          obj=entity.getProperty("originState");
          if (obj instanceof String) {
              this.originState=(String) obj;
          } // END if (obj instanceof String)
          //***** originZip
          obj=entity.getProperty("originZip");
          if (obj instanceof String) {
              this.originZip=(String) obj;
          } // END if (obj instanceof String)
          //***** proNumber
          obj=entity.getProperty("proNumber");
          if (obj instanceof String) {
              this.proNumber=(String) obj;
          } // END if (obj instanceof String)
          //***** purchaseOrderNumber
          obj=entity.getProperty("purchaseOrderNumber");
          if (obj instanceof String) {
              this.purchaseOrderNumber=(String) obj;
          } // END if (obj instanceof String)
          //***** shipperAddress
          obj=entity.getProperty("shipperAddress");
          if (obj instanceof String) {
              this.shipperAddress=(String) obj;
          } // END if (obj instanceof String)
          //***** shipperCity
          obj=entity.getProperty("shipperCity");
          if (obj instanceof String) {
              this.shipperCity=(String) obj;
          } // END if (obj instanceof String)
          //***** shipperCompany
          obj=entity.getProperty("shipperCompany");
          if (obj instanceof String) {
              this.shipperCompany=(String) obj;
          } // END if (obj instanceof String)
          //***** shipperName
          obj=entity.getProperty("shipperName");
          if (obj instanceof String) {
              this.shipperName=(String) obj;
          } // END if (obj instanceof String)
          //***** shipperNumber
          obj=entity.getProperty("shipperNumber");
          if (obj instanceof String) {
              this.shipperNumber=(String) obj;
          } // END if (obj instanceof String)
          //***** shipperState
          obj=entity.getProperty("shipperState");
          if (obj instanceof String) {
              this.shipperState=(String) obj;
          } // END if (obj instanceof String)
          //***** shipperZip
          obj=entity.getProperty("shipperZip");
          if (obj instanceof String) {
              this.shipperZip=(String) obj;
          } // END if (obj instanceof String)
          //***** specialInstructions
          obj=entity.getProperty("specialInstructions");
          if (obj instanceof String) {
              this.specialInstructions=(String) obj;
          } // END if (obj instanceof String)
          //***** trailerLoadedBy
          obj=entity.getProperty("trailerLoadedBy");
          if (obj instanceof String) {
              this.trailerLoadedBy=(String) obj;
          } // END if (obj instanceof String)
          //***** trailerNumber
          obj=entity.getProperty("trailerNumber");
          if (obj instanceof String) {
              this.trailerNumber=(String) obj;
          } // END if (obj instanceof String)
      } // END if (entity != null) 
      logger.exiting(_CLASS,"populateFromEntity(Entity)");
  }
  
  /**
   * Populate the current object from the template specified.
   */
  public void populateFromObject(BillOfLading billOfLading) {
    logger.entering(_CLASS, "populateFromObject(BillOfLading)",billOfLading);
    // TODO
    logger.exiting(_CLASS, "populateFromObject(BillOfLading)");
  }
  
  //********** isCollect
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#isCollect()
   */
  @Override
  public boolean isCollect() {
    logger.entering(_CLASS,"isCollect()");
    logger.exiting(_CLASS,"isCollect()",this.isCollect);
    return this.isCollect;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setCollect(boolean)
   */
  @Override
  public void setCollect(boolean isCollect) {
    logger.entering(_CLASS,"setCollect(boolean)",isCollect);
    this.isCollect = isCollect;
    logger.exiting(_CLASS,"setCollect(boolean)");    
  }
  //********** date
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getDate()
   */
  @Override
  public Date getDate() {
    logger.entering(_CLASS,"getDate()");
    logger.exiting(_CLASS,"getDate()",this.date);
    return this.date;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setDate(java.util.Date)
   */
  @Override
  public void setDate(Date date) {
    logger.entering(_CLASS,"setDate(Date)",date);
    this.date = date;
    logger.exiting(_CLASS,"setDate(Date)");
  }
  //********** blNumber
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getBlNumber()
   */
  @Override
  public String getBlNumber() {
    logger.entering(_CLASS,"getBlNumber()");
    logger.exiting(_CLASS,"getBlNumber()",this.blNumber);
    return this.blNumber;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setBlNumber(java.lang.String)
   */
  @Override
  public void setBlNumber(String blNumber) {
    logger.entering(_CLASS,"setBlNumber(String)",blNumber);
    this.blNumber = blNumber;
    logger.exiting(_CLASS,"setBlNumber(String)");
  }
  //********** carrier
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getCarrier()
   */
  @Override
  public String getCarrier() {
    logger.entering(_CLASS,"getCarrier()");
    logger.exiting(_CLASS,"getCarrier()",this.carrier);
    return carrier;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setCarrier(java.lang.String)
   */
  @Override
  public void setCarrier(String carrier) {
    logger.entering(_CLASS,"setCarrier(String)",carrier);
    this.carrier = carrier;
    logger.exiting(_CLASS,"setCarrier(String)");
  }
  //********** consigneeCompany
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getConsigneeCompany()
   */
  @Override
  public String getConsigneeCompany() {
    logger.entering(_CLASS,"getConsigneeCompany()");
    logger.exiting(_CLASS,"getConsigneeCompany()",this.consigneeCompany);
    return this.consigneeCompany;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setConsigneeCompany(java.lang.String)
   */
  @Override
  public void setConsigneeCompany(String consigneeCompany) {
    logger.entering(_CLASS,"setConsigneeCompany(String)",consigneeCompany);
    this.consigneeCompany = consigneeCompany;
    logger.exiting(_CLASS,"setConsigneeCompany(String)");
  }
  //********** consigneeEmail
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getConsigneeEmail()
   */
  @Override
  public String getConsigneeEmail() {
    logger.entering(_CLASS,"getConsigneeEmail()");
    logger.exiting(_CLASS,"getConsigneeEmail()",this.consigneeEmail);
    return this.consigneeEmail;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setConsigneeEmail(java.lang.String)
   */
  @Override
  public void setConsigneeEmail(String consigneeEmail) {
    logger.entering(_CLASS,"setConsigneeEmail(String)",consigneeEmail);
    this.consigneeEmail = consigneeEmail;
    logger.exiting(_CLASS,"setConsigneeEmail(String)");
  }
  //********** consigneeName
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getConsigneeName()
   */
  @Override
  public String getConsigneeName() {
    logger.entering(_CLASS,"getConsigneeName()");
    logger.exiting(_CLASS,"getConsigneeName()",this.consigneeName);
    return this.consigneeName;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setConsigneeName(java.lang.String)
   */
  @Override
  public void setConsigneeName(String consigneeName) {
    logger.entering(_CLASS,"setConsigneeName(String)",consigneeName);
    this.consigneeName = consigneeName;
    logger.exiting(_CLASS,"setConsigneeName(String)");
  }
  //********** consigneePhone
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getConsigneePhone()
   */
  @Override
  public String getConsigneePhone() {
    logger.entering(_CLASS,"getConsigneePhone()");
    logger.exiting(_CLASS,"getConsigneePhone()",this.consigneePhone);
    return this.consigneePhone;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setConsigneePhone(java.lang.String)
   */
  @Override
  public void setConsigneePhone(String consigneePhone) {
    logger.entering(_CLASS,"setConsigneePhone(String)",consigneePhone);
    this.consigneePhone = consigneePhone;
    logger.exiting(_CLASS,"setConsigneePhone(String)");
  }
  //********** destinationCity
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getDestinationCity()
   */
  @Override
  public String getDestinationCity() {
    logger.entering(_CLASS,"getDestinationCity()");
    logger.exiting(_CLASS,"getDestinationCity()",this.destinationCity);
    return this.destinationCity;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setDestinationCity(java.lang.String)
   */
  @Override
  public void setDestinationCity(String destinationCity) {
    logger.entering(_CLASS,"setDestinationCity(String)",destinationCity);
    this.destinationCity = destinationCity;
    logger.exiting(_CLASS,"setDestinationCity(String)");
  }
  //********** destinationState
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getDestinationState()
   */
  @Override
  public String getDestinationState() {
    logger.entering(_CLASS,"getDestinationState()");
    logger.exiting(_CLASS,"getDestinationState()",this.destinationState);
    return this.destinationState;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setDestinationState(java.lang.String)
   */
  @Override
  public void setDestinationState(String destinationState) {
    logger.entering(_CLASS,"setDestinationState(String)",destinationState);
    this.destinationState = destinationState;
    logger.exiting(_CLASS,"setDestinationState(String)");
  }
  //********** getDestinationZip
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getDestinationZip()
   */
  @Override
  public String getDestinationZip() {
    logger.entering(_CLASS,"getDestinationZip()");
    logger.exiting(_CLASS,"getDestinationZip()",this.destinationZip);
    return this.destinationZip;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setDestinationZip(java.lang.String)
   */
  @Override
  public void setDestinationZip(String destinationZip) {
    logger.entering(_CLASS,"setDestinationZip(String)",destinationZip);
    this.destinationZip = destinationZip;
    logger.exiting(_CLASS,"setDestinationZip(String)");
  }
  //********** freightCountedBy
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getFreightCountedBy()
   */
  @Override
  public String getFreightCountedBy() {
    logger.entering(_CLASS,"getFreightCountedBy()");
    logger.exiting(_CLASS,"getFreightCountedBy()",this.freightCountedBy);
    return this.freightCountedBy;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setFreightCountedBy(java.lang.String)
   */
  @Override
  public void setFreightCountedBy(String freightCountedBy) {
    logger.entering(_CLASS,"setFreightCountedBy(String)",freightCountedBy);
    this.freightCountedBy = freightCountedBy;
    logger.exiting(_CLASS,"setFreightCountedBy(String)");
  }
  //********** invoiceeName
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getInvoiceeName()
   */
  @Override
  public String getInvoiceeName() {
    logger.entering(_CLASS,"getInvoiceeName()");
    logger.exiting(_CLASS,"getInvoiceeName()",this.invoiceeName);
    return this.invoiceeName;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setInvoiceeName(java.lang.String)
   */
  @Override
  public void setInvoiceeName(String invoiceeName) {
    logger.entering(_CLASS,"setInvoiceeName(String)",invoiceeName);
    this.invoiceeName = invoiceeName;
    logger.exiting(_CLASS,"setInvoiceeName(String)");
  }
  //********** invoiceeCompany
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getInvoiceeCompany()
   */
  @Override
  public String getInvoiceeCompany() {
    logger.entering(_CLASS,"getInvoiceeCompany()");
    logger.exiting(_CLASS,"getInvoiceeCompany()",this.invoiceeCompany);
    return this.invoiceeCompany;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setInvoiceeCompany(java.lang.String)
   */
  @Override
  public void setInvoiceeCompany(String invoiceeCompany) {
    logger.entering(_CLASS,"setInvoiceeCompany(String)",invoiceeCompany);
    this.invoiceeCompany = invoiceeCompany;
    logger.exiting(_CLASS,"setInvoiceeCompany(String)");
  }
  //********** invoiceeAddress
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getInvoiceeAddress()
   */
  @Override
  public String getInvoiceeAddress() {
    logger.entering(_CLASS,"getInvoiceeAddress()");
    logger.exiting(_CLASS,"getInvoiceeAddress()",this.invoiceeAddress);
    return this.invoiceeAddress;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setInvoiceeAddress(java.lang.String)
   */
  @Override
  public void setInvoiceeAddress(String invoiceeAddress) {
    logger.entering(_CLASS,"setInvoiceeAddress(String)",invoiceeAddress);
    this.invoiceeAddress = invoiceeAddress;
    logger.exiting(_CLASS,"setInvoiceeAddress()");
    
  }
  //********** invoiceeCity
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getInvoiceeCity()
   */
  @Override
  public String getInvoiceeCity() {
    logger.entering(_CLASS,"getInvoiceeCity()");
    logger.exiting(_CLASS,"getInvoiceeCity()",this.invoiceeCity);
    return this.invoiceeCity;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setInvoiceeCity(java.lang.String)
   */
  @Override
  public void setInvoiceeCity(String invoiceeCity) {
    logger.entering(_CLASS,"setInvoiceeCity(String)",invoiceeCity);
    this.invoiceeCity = invoiceeCity;
    logger.exiting(_CLASS,"setInvoiceeCity(String)");
  }
  //********** invoiceeState
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getInvoiceeState()
   */
  @Override
  public String getInvoiceeState() {
    logger.entering(_CLASS,"getInvoiceeState()");
    logger.exiting(_CLASS,"getInvoiceeState()",this.invoiceeState);
    return this.invoiceeState;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setInvoiceeState(java.lang.String)
   */
  @Override
  public void setInvoiceeState(String invoiceeState) {
    logger.entering(_CLASS,"setInvoiceeState(String)",invoiceeState);
    this.invoiceeState = invoiceeState;
    logger.exiting(_CLASS,"setInvoiceeState(String)");
  }
  //********** invoiceeZip
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getInvoiceeZip()
   */
  @Override
  public String getInvoiceeZip() {
    logger.entering(_CLASS,"getInvoiceeZip()");
    logger.exiting(_CLASS,"getInvoiceeZip()",this.invoiceeZip);
    return this.invoiceeZip;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setInvoiceeZip(java.lang.String)
   */
  @Override
  public void setInvoiceeZip(String invoiceeZip) {
    logger.entering(_CLASS,"setInvoiceeZip(String)",invoiceeZip);
    this.invoiceeZip = invoiceeZip;
    logger.exiting(_CLASS,"setInvoiceeZip(String)");
  }
  //********** originCity
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getOriginCity()
   */
  @Override
  public String getOriginCity() {
    logger.entering(_CLASS,"getOriginCity()");
    logger.exiting(_CLASS,"getOriginCity()",this.originCity);
    return this.originCity;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setOriginCity(java.lang.String)
   */
  @Override
  public void setOriginCity(String originCity) {
    logger.entering(_CLASS,"setOriginCity(String)",originCity);
    this.originCity = originCity;
    logger.exiting(_CLASS,"setOriginCity(String)");
  }
  //********* originState
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getOriginState()
   */
  @Override
  public String getOriginState() {
    logger.entering(_CLASS,"getOriginState()");
    logger.exiting(_CLASS,"getOriginState()",this.originState);
    return this.originState;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setOriginState(java.lang.String)
   */
  @Override
  public void setOriginState(String originState) {
    logger.entering(_CLASS,"setOriginState(String)",originState);
    this.originState = originState;
    logger.exiting(_CLASS,"setOriginState(String)");
  }
  //********** originZip
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getOriginZip()
   */
  @Override
  public String getOriginZip() {
    logger.entering(_CLASS,"getOriginZip()");
    logger.exiting(_CLASS,"getOriginZip()",this.originZip);
    return this.originZip;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setOriginZip(java.lang.String)
   */
  @Override
  public void setOriginZip(String originZip) {
    logger.entering(_CLASS,"setOriginZip(String)",originZip);
    this.originZip = originZip;
    logger.exiting(_CLASS,"setOriginZip(String)");
  }
  //********** purchaseOrderNumber
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getPurchaseOrderNumber()
   */
  @Override
  public String getPurchaseOrderNumber() {
    logger.entering(_CLASS,"getPurchaseOrderNumber()");
    logger.exiting(_CLASS,"getPurchaseOrderNumber()",this.purchaseOrderNumber);
    return this.purchaseOrderNumber;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setPurchaseOrderNumber(java.lang.String)
   */
  @Override
  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    logger.entering(_CLASS,"setPurchaseOrderNumber(String)",
        purchaseOrderNumber);
    this.purchaseOrderNumber = purchaseOrderNumber;
    logger.exiting(_CLASS,"setPurchaseOrderNumber(String)");
  }
  //********** proNumber
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getProNumber()
   */
  @Override
  public String getProNumber() {
    logger.entering(_CLASS,"getProNumber()");
    logger.exiting(_CLASS,"getProNumber()",this.proNumber);
    return this.proNumber;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setProNumber(java.lang.String)
   */
  @Override
  public void setProNumber(String proNumber) {
    logger.entering(_CLASS,"setProNumber(String)",proNumber);
    this.proNumber = proNumber;
    logger.exiting(_CLASS,"setProNumber(String)");
  }
  //********** shipperAddress
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getShipperAddress()
   */
  @Override
  public String getShipperAddress() {
    logger.entering(_CLASS,"getShipperAddress()");
    logger.exiting(_CLASS,"getShipperAddress()",this.shipperAddress);
    return this.shipperAddress;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setShipperAddress(java.lang.String)
   */
  @Override
  public void setShipperAddress(String shipperAddress) {
    logger.entering(_CLASS,"setShipperAddress(String)",shipperAddress);
    this.shipperAddress = shipperAddress;
    logger.exiting(_CLASS,"setShipperAddress(String)");
  }
  //********** shipperCity
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getShipperCity()
   */
  @Override
  public String getShipperCity() {
    logger.entering(_CLASS,"getShipperCity()");
    logger.exiting(_CLASS,"getShipperCity()",this.shipperCity);
    return shipperCity;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setShipperCity(java.lang.String)
   */
  @Override
  public void setShipperCity(String shipperCity) {
    logger.entering(_CLASS,"setShipperCity(String)",shipperCity);
    this.shipperCity = shipperCity;
    logger.exiting(_CLASS,"setShipperCity(String)");
  }
  //********** shipperCompany
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getShipperCompany()
   */
  @Override
  public String getShipperCompany() {
    logger.entering(_CLASS,"getShipperCompany()");
    logger.exiting(_CLASS,"getShipperCompany()",this.shipperCompany);
    return this.shipperCompany;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setShipperCompany(java.lang.String)
   */
  @Override
  public void setShipperCompany(String shipperCompany) {
    logger.entering(_CLASS,"setShipperCompany(String)",shipperCompany);
    this.shipperCompany = shipperCompany;
    logger.exiting(_CLASS,"setShipperCompany(String)");
  }
  //********** shipperName
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getShipperName()
   */
  @Override
  public String getShipperName() {
    logger.entering(_CLASS,"getShipperName()");
    logger.exiting(_CLASS,"getShipperName()",this.shipperName);
    return this.shipperName;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setShipperName(java.lang.String)
   */
  @Override
  public void setShipperName(String shipperName) {
    logger.entering(_CLASS,"setShipperName(String)",shipperName);
    this.shipperName = shipperName;
    logger.exiting(_CLASS,"setShipperName(String)");
  }
  //********** shipperNumber
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getShipperNumber()
   */
  @Override
  public String getShipperNumber() {
    logger.entering(_CLASS,"getShipperNumber()");
    logger.exiting(_CLASS,"getShipperNumber()",this.shipperNumber);
    return this.shipperNumber;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setShipperNumber(java.lang.String)
   */
  @Override
  public void setShipperNumber(String shipperNumber) {
    logger.entering(_CLASS,"setShipperNumber(String)",shipperNumber);
    this.shipperNumber = shipperNumber;
    logger.exiting(_CLASS,"setShipperNumber(String)");
  }
  //********** shipperState
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getShipperState()
   */
  @Override
  public String getShipperState() {
    logger.entering(_CLASS,"getShipperState()");
    logger.exiting(_CLASS,"getShipperState()",this.shipperState);
    return this.shipperState;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setShipperState(java.lang.String)
   */
  @Override
  public void setShipperState(String shipperState) {
    logger.entering(_CLASS,"setShipperState(String)",shipperState);
    this.shipperState = shipperState;
    logger.exiting(_CLASS,"setShipperState(String)");
  }
  //********* shipperZip
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getShipperZip()
   */
  @Override
  public String getShipperZip() {
    logger.entering(_CLASS,"getShipperZip()");
    logger.exiting(_CLASS,"getShipperZip()",this.shipperZip);
    return this.shipperZip;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setShipperZip(java.lang.String)
   */
  @Override
  public void setShipperZip(String shipperZip) {
    logger.entering(_CLASS,"setShipperZip(String)",shipperZip);
    this.shipperZip = shipperZip;
    logger.exiting(_CLASS,"setShipperZip(String)");
  }
  //********** specialInsructions
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getSpecialInstructions()
   */
  @Override
  public String getSpecialInstructions() {
    logger.entering(_CLASS,"getSpecialInstructions()");
    logger.exiting(_CLASS,"getSpecialInstructions()",this.specialInstructions);
    return this.specialInstructions;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setSpecialInstructions(java.lang.String)
   */
  @Override
  public void setSpecialInstructions(String specialInstructions) {
    logger.entering(_CLASS,"setSpecialInstructions(String)",specialInstructions);
    this.specialInstructions = specialInstructions;
    logger.exiting(_CLASS,"setSpecialInstructions(String)");
  }
  //********** trailerLoadedBy
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getTrailerLoadedBy()
   */
  @Override
  public String getTrailerLoadedBy() {
    logger.entering(_CLASS,"getTrailerLoadedBy()");
    logger.exiting(_CLASS,"getTrailerLoadedBy()",this.trailerLoadedBy);
    return this.trailerLoadedBy;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setTrailerLoadedBy(java.lang.String)
   */
  @Override
  public void setTrailerLoadedBy(String trailerLoadedBy) {
    logger.entering(_CLASS,"setTrailerLoadedBy(String)",trailerLoadedBy);
    this.trailerLoadedBy = trailerLoadedBy;
    logger.exiting(_CLASS,"setTrailerLoadedBy(String)");
  }
  //********** trailerNumber
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getTrailerNumber()
   */
  @Override
  public String getTrailerNumber() {
    logger.entering(_CLASS,"getTrailerNumber()");
    logger.exiting(_CLASS,"getTrailerNumber()",this.trailerNumber);
    return this.trailerNumber;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setTrailerNumber(java.lang.String)
   */
  @Override
  public void setTrailerNumber(String trailerNumber) {
    logger.entering(_CLASS,"setTrailerNumber(String)",trailerNumber);
    this.trailerNumber = trailerNumber;
    logger.exiting(_CLASS,"setTrailerNumber(String)");
  }
  //********** getLineItems
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#getLineItems()
   */
  @Override
  public List<BillOfLadingLineItem> getLineItems() {
    logger.entering(_CLASS,"getLineItems()");
    logger.exiting(_CLASS,"getLineItems()",this.lineItems);
    return this.lineItems;
  }
  /* (non-Javadoc)
   * @see com.wildstartech.wfa.logistics.ltl.BillOfLading#setLineItems(java.util.List)
   */
  @Override
  public void setLineItems(List<BillOfLadingLineItem> lineItems) {
    logger.entering(_CLASS,"setLineItems(List<BillOfLadingLineItemImpl>)",
        lineItems);
    this.lineItems = lineItems;
    logger.exiting(_CLASS,"setLineItems(List<BillOfLadingLineItemImpl>)");
  }
}