/*
 * Copyright (c) 2013 - 2016 Wildstar Technologies, LLC.
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

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentQuoteLineItem;
import com.wildstartech.wfa.logistics.ltl.QuoteLineItem;

public class PersistentQuoteLineItemImpl 
extends PersistentEditableCommodityLineItemImpl<QuoteLineItem> 
implements PersistentQuoteLineItem {
  /** Used in object serialization. */
  private static final long serialVersionUID = -7189394778017025871L;
  private static final String _CLASS=
      PersistentQuoteLineItemImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  protected static final String _KIND=
      "com.wildstartech.wfa.logistics.ltl.QuoteLineItem";
  
  /* 
   * Uniquely identifies the quote with which this line item object is 
   * associated.
   */
  private String quoteIdentifier=null;
  
  /** 
   * Default, no-argument constructor.
   */
  public PersistentQuoteLineItemImpl() {
    logger.entering(_CLASS,"PersistentQuoteLineItemImpl()");
    logger.exiting(_CLASS,"PersistentQuoteLineItemImpl()");
  }
  
  //***** utility methods
  @Override
  public boolean equals(Object obj) {
     if (this == obj)
        return true;
     if (!super.equals(obj))
        return false;
     if (getClass() != obj.getClass())
        return false;
     PersistentQuoteLineItemImpl other = (PersistentQuoteLineItemImpl) obj;
     if (quoteIdentifier == null) {
        if (other.quoteIdentifier != null)
           return false;
     } else if (!quoteIdentifier.equals(other.quoteIdentifier))
        return false;
     return true;
  }  
  
  @Override
  public int hashCode() {
     final int prime = 31;
     int result = super.hashCode();
     result = prime * result
           + ((quoteIdentifier == null) ? 0 : quoteIdentifier.hashCode());
     return result;
  }
  
  @Override
  public String getKind() {
    logger.entering(_CLASS,"getKind()");
    logger.exiting(_CLASS,"getKind()",PersistentQuoteLineItemImpl._KIND);
    return PersistentQuoteLineItemImpl._KIND;
  }
  @Override
  protected void populateEntity(Entity entity) {
    logger.entering(_CLASS,"populateEntity(Entity)",entity);
    if (entity != null) {
      super.populateEntity(entity);
      entity.setProperty("quoteIdentifier", getQuoteIdentifier());
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
      // quoteIdentifier
      setQuoteIdentifier(getPropertyAsString(entity,"quoteIdentifier"));      
    } else {
      logger.warning("The entity passed was null.");
    } // END if (entity != null)
    logger.exiting(_CLASS,"populateFromEntity(Entity,UserContext)");
  }
  
  @Override
  public void populateFromObject(QuoteLineItem qli) {
    logger.entering(_CLASS,"populateFromObject(QuoteLineItem)",qli);
    if (qli != null) {
      super.populateFromObject(qli);
      if (qli instanceof PersistentQuoteLineItem) {
         // quoteIdentifier
         setQuoteIdentifier(
               ((PersistentQuoteLineItem) qli).getQuoteIdentifier()
          );
      } // END if (qli instanceof PersistentQuoteLineItem)
    } else {
      logger.warning("The QuoteLineItem passed was null.");
    } // END if(qli != null)
    logger.exiting(_CLASS,"populateFromObject(QuoteLineItem)");
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
     sb.append("quoteIdentifier=\"").append(getQuoteIdentifier());
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

  
  /**
   * Updates current object using information from the specified object.
   */
  public void updateFromObject(QuoteLineItem lineItem) {
    logger.entering(_CLASS,"updateFromObject(QuoteLineItem)",lineItem);
    if (lineItem != null) {
      populateFromObject(lineItem);
    } else {
      logger.finest("The specified QuoteLineItem object is null.");
    } // END 
    logger.exiting(_CLASS,"updateFromObject(Quote)");
  }
  //***** accessor methods
  
  //***** quote identifier
  @Override
  public String getQuoteIdentifier() {
    logger.entering(_CLASS,"getQuoteIdentifier()");
    logger.exiting(_CLASS,"getQuoteIdentifier()",this.quoteIdentifier);
    return this.quoteIdentifier;
  }
  @Override
  public void setQuoteIdentifier(String identifier) {
    logger.entering(_CLASS,"setQuoteIdentifier(String)",identifier);
    this.quoteIdentifier=defaultValue(identifier);
    logger.exiting(_CLASS,"setQuoteIdentifier(String)");
  }
}