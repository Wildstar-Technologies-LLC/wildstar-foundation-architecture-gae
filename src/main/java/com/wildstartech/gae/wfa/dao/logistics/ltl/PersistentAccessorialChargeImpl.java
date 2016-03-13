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
package com.wildstartech.gae.wfa.dao.logistics.ltl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentAccessorialCharge;
import com.wildstartech.wfa.finance.ChargeDescriptionTooLongException;
import com.wildstartech.wfa.logistics.ltl.AccessorialCharge;

public class PersistentAccessorialChargeImpl 
extends WildObjectImpl<AccessorialCharge> 
implements PersistentAccessorialCharge {
  /** Used in object serialization. */
  private static final long serialVersionUID = 5169782031161049363L;
  private static final String _CLASS=
      PersistentAccessorialChargeImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  protected static final String _KIND=
      "com.wildstartech.wfa.logistics.ltl.AccessorialCharge";
  
  private int quantity=0;
  private BigDecimal amount=null;
  private String description="";
  private String quoteIdentifier="";
  
  /**
   * Default, no-argument constructor.
   */
  public PersistentAccessorialChargeImpl() {
    logger.entering(_CLASS,"PersistentAccessorialChargeImpl()");
    logger.exiting(_CLASS,"PersistentAccessorialChargeImpl()");
  }
  
  /**
   * Calculate the hashcode for the PersistentAccessorialCharge
   */
  public int hashCode() {
    logger.entering(_CLASS, "hashCode()");
    int result=0;
    Date tmpDate=null;
    StringBuilder sb=null;
    MathContext mc=null;
    
    mc=new MathContext(3,RoundingMode.HALF_EVEN);
    sb=new StringBuilder(256);
    sb.append(PersistentAccessorialChargeImpl.class.getName());
    sb.append(getIdentifier());
    sb.append(getQuoteIdentifier());
    sb.append(getDescription());
    sb.append(getAmount().round(mc));
    sb.append(getQuantity());
    sb.append(getTotalAmount().round(mc));
    tmpDate=getDateCreated();
    if (tmpDate != null) {
      sb.append(String.valueOf(tmpDate.getTime()));
    } // END if (tmpDate != null)
    sb.append(getCreatedBy());
    tmpDate=getDateModified();
    if (tmpDate != null) {
      sb.append(String.valueOf(tmpDate.getTime()));
    } // END if (tmpDate != null)
    sb.append(getModifiedBy());
    result=sb.hashCode();
    
    logger.exiting(_CLASS, "hashCode()",result);
    return result;
  }
  /**
   * Generate a string representation of the object.
   */
  public String toString() {
    logger.entering(_CLASS,"toString()");
    Date tmpDate=null;
    DateFormat dFmt=null;
    MathContext mc=null;
    NumberFormat fmt=null;
    String result=null;
    StringBuilder sb=null;
    
    dFmt=DateFormat.getDateInstance();
    fmt=NumberFormat.getCurrencyInstance();
    mc=new MathContext(3,RoundingMode.HALF_EVEN);
    sb=new StringBuilder(256);
    sb.append("PersistentAccessorialChargeImpl['");
    sb.append(getDescription());
    sb.append("','");
    sb.append(getQuantity());
    sb.append("','");
    sb.append(fmt.format(getAmount().round(mc)));
    sb.append("','");
    sb.append(fmt.format(getTotalAmount().round(mc)));
    sb.append("','");
    tmpDate=getDateCreated();
    if (tmpDate != null) {
      sb.append(dFmt.format(tmpDate));
    } // END if (tmpDate != null)
    sb.append("','");
    sb.append(getCreatedBy());
    sb.append("','");
    tmpDate=getDateModified();
    if (tmpDate != null) {
      sb.append(dFmt.format(tmpDate));
    } // END if (tmpDate != null)
    sb.append("','");
    sb.append(getModifiedBy());
    sb.append("']");
    result=sb.toString();
    logger.exiting(_CLASS,"toString()",result);
    return result;
  }
  //***** utility methods
  /**
   * Populate the specified <code>Entity</code> from current object.
   * 
   * <p>This method is responsible for populating the contents of the 
   * properties of the <code>Entity</code> object with information taken from
   * the current <code>PersistentAccessorialChargeImpl</code> object.
   * 
   * @param entity - the object to be populated with data using information 
   * taken from the current oject.
   */
  protected void populateEntity(Entity entity) {
    logger.entering(_CLASS,"popualteEntity(Entity)",entity);
    BigDecimal amount=null;
    if (entity != null) {
      super.populateEntity(entity);
      amount=getAmount();
      if (amount != null) {
        entity.setProperty("amount", getAmount().toPlainString());
      } // END if (amount != null)
      entity.setProperty("description", getDescription());
      entity.setProperty("quantity", getQuantity());
      entity.setProperty("quoteIdentifier",getQuoteIdentifier());
    } else {
      logger.severe("The specified entity is null.");
    }
    
    logger.exiting(_CLASS,"popualteEntity(Entity)");
  }
  /**
   * Populates the current object using data from the <code>Entity</code>.
   * 
   * <p>This method populates the current object with information read 
   * from the <code>Entity</code> object passed to the method.</p>
   * 
   * @param entity The entity object to be used to populate the fields from the
   * current object.
   */
  @Override
  protected void populateFromEntity(Entity entity, UserContext ctx) {
    logger.entering(_CLASS,"populateFromEntity(Entity)",entity);
    int tmpInt=0;
    String tmpStr="";
    
    if(entity !=null) {
      // Populate base WildObject fields.
      super.populateFromEntity(entity,ctx);
      //***** amount
      tmpStr=getPropertyAsString(entity,"amount");
      if (tmpStr != null) {
        this.amount=new BigDecimal(tmpStr);
      } else {
        this.amount=new BigDecimal(0);
      } // END if (tmpStr != null)
      this.amount=this.amount.setScale(2,RoundingMode.HALF_UP);
      //***** description
      tmpStr=(String) entity.getProperty("description");
      try {
        setDescription(tmpStr);
      } catch (ChargeDescriptionTooLongException ex) {
        logger.log(Level.SEVERE,
            "Error reading accessorial charge description.",
            ex);
      } // END try/catch
      //***** quantity
      tmpInt=getPropertyAsInteger(entity,"quantity");
      setQuantity(tmpInt);
      //***** quoteIdentiifer
      setQuoteIdentifier(getPropertyAsString(entity,"quoteIdentifier"));
    } else {
      logger.severe("The specified entity is null.");
    }
    logger.exiting(_CLASS,"populateFromEntity(Entity)");
  }
  /**
   * Populate the current object using the referenced template.
   */
  @Override
  public void populateFromObject(AccessorialCharge charge) {
    logger.entering(_CLASS,"populateFromObject(AccessorialCharge)",charge);
    StringBuilder sb=null;
    if (charge != null) {
      //***** amount
      setAmount(charge.getAmount());
      //***** description
      try {
        setDescription(charge.getDescription());
      } catch (ChargeDescriptionTooLongException ex) {
        sb=new StringBuilder(80);
        sb.append("The specified description, \"");
        sb.append(charge.getDescription());
        sb.append("\", is too long.");
        logger.log(Level.SEVERE,sb.toString(),ex);
      } // END try/catch();
      //***** quantity
      setQuantity(charge.getQuantity());
      //***** quoteIdentifier
      if (charge instanceof PersistentAccessorialChargeImpl) {
        setQuoteIdentifier(
            ((PersistentAccessorialChargeImpl) charge).getQuoteIdentifier()
        );
      } // END if (charge instanceof PersistentAccessorialChargeImpl)
    } else {
      logger.severe("The charge passed to this method was null.");
    } // END if (charge != null)
    logger.exiting(_CLASS,"populateFromObject(AccessorialCharge)");
  }
  //***** accessor methods
  
  //***** amount
  @Override
  public BigDecimal getAmount() {
    logger.entering(_CLASS,"getAmount()");
    logger.exiting(_CLASS,"getAmount()",this.amount);
    return this.amount;
  }
  @Override
  public void setAmount(double amount) {
    logger.entering(_CLASS,"setAmount(BigDecimal)",amount);
    this.amount=new BigDecimal(amount);
    this.amount=this.amount.setScale(2,RoundingMode.HALF_UP);
    logger.entering(_CLASS,"setAmount(BigDecimal)");
  }
  
  @Override
  public void setAmount(int amount) {
    logger.entering(_CLASS,"setAmount(BigDecimal)",amount);
    this.amount=new BigDecimal(amount);
    this.amount=this.amount.setScale(2,RoundingMode.HALF_UP);
    logger.entering(_CLASS,"setAmount(BigDecimal)");
  }
  
  @Override
  public void setAmount(BigDecimal amount) {
    logger.entering(_CLASS,"setAmount(BigDecimal)",amount);
    if (amount != null) {
      this.amount=amount.setScale(2,RoundingMode.HALF_UP);
    } // END if (amount != null)
    logger.entering(_CLASS,"setAmount(BigDecimal)");    
  }
  
  //***** description
  @Override
  public String getDescription() {
    logger.entering(_CLASS,"getDescription()");
    logger.exiting(_CLASS,"getDescription()",this.description);
    return this.description;
  }
  @Override
  public void setDescription(String description)
      throws ChargeDescriptionTooLongException {
    logger.entering(_CLASS,"setDescription(String)",description);
    this.description=defaultValue(description,"");    
    logger.exiting(_CLASS,"setDescription(String)");
  }
  
  //***** kind
  @Override
  public String getKind() {
    logger.entering(_CLASS,"getKind()");
    logger.exiting(_CLASS,"getKind()",PersistentAccessorialChargeImpl._KIND);
    return PersistentAccessorialChargeImpl._KIND;
  }
  
  //***** quantity
  @Override
  public int getQuantity() {
    logger.entering(_CLASS,"getQuantity()");
    logger.exiting(_CLASS,"getQuantity()",this.quantity);
    return this.quantity;
  }
  
  @Override
  public void setQuantity(int quantity) {
    logger.entering(_CLASS,"setQuantity(int)",quantity);
    if (quantity < 0) {
      this.quantity=0;
    } else {
      this.quantity=quantity;
    } // END if (quantity < 0)
    logger.exiting(_CLASS,"setQuantity(int)");
  }
  //***** quoteIdentifier
  public String getQuoteIdentifier() {
    logger.entering(_CLASS,"getQuoteIdentifier()");
    logger.exiting(_CLASS,"getQuoteIdentifier()",this.quoteIdentifier);
    return this.quoteIdentifier;    
  }
  public void setQuoteIdentifier(String quoteIdentifier) {
    logger.entering(_CLASS,"setQuoteIdentifier(String)",quoteIdentifier);
    this.quoteIdentifier=defaultValue(quoteIdentifier);
    logger.exiting(_CLASS,"setQuoteIdentifier(String)");
  }
  //***** totalAmount
  @Override
  public BigDecimal getTotalAmount() {
    logger.entering(_CLASS,"getTotalAmount()");
    BigDecimal total=null;
    
    total=this.amount.multiply(new BigDecimal(this.quantity));
    
    logger.exiting(_CLASS,"getTotalAmount()",total);
    return total;
  }  
}
