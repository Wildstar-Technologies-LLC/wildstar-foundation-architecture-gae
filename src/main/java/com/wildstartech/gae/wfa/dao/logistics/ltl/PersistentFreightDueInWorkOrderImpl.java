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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.wildstartech.gae.wfa.dao.ticketing.PersistentBasicTicketImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.WildObject;
import com.wildstartech.wfa.dao.logistics.ltl.FreightDueInWorkOrderLineItemDAO;
import com.wildstartech.wfa.dao.logistics.ltl.FreightDueInWorkOrderLineItemDAOFactory;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentFreightDueInWorkOrder;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentFreightDueInWorkOrderLineItem;
import com.wildstartech.wfa.logistics.ltl.FreightDueInWorkOrder;
import com.wildstartech.wfa.logistics.ltl.FreightDueInWorkOrderLineItem;

public class PersistentFreightDueInWorkOrderImpl
extends PersistentBasicTicketImpl<FreightDueInWorkOrder>
implements PersistentFreightDueInWorkOrder {
   /** Used in object serialization. */
   private static final long serialVersionUID = -6015131875983650643L;
   private static final String _CLASS=
         PersistentFreightDueInWorkOrderImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   private static final String lineItemNumberOutOfBoundsTemplate=
         "The lineItemNumber parameter must be between 1 and {0}.";
   
   /* List of values available for the statusState field 
    * At some point in time, this will be read from configurable data. 
    */
   private static final List<String> statusStates=new ArrayList<String>();
   static {
      statusStates.add("New");      
      statusStates.add("Pending");
      statusStates.add("Resolved");
      statusStates.add("Closed");
   }
   /* List of values available for the statusReason field for each status state. 
    * At some point in time, this will be read from configurable data.
    */
   private static final Map<String,List<String>> statusReasons=
         new TreeMap<String,List<String>>();
   static {
      // Pending
      List<String> tmpList=new ArrayList<String>();
      tmpList.add("Incoming Freight");
      statusReasons.put("Pending", tmpList);
      tmpList.clear();
      // Resolved/Closed
      tmpList.add("Cancelled");
      tmpList.add("Complete");
      statusReasons.put("Resolved", tmpList);
      statusReasons.put("Closed", tmpList);
   }
   public static final String _KIND=
         "com.wildstartech.wfa.logistics.ltl.FreightDueInWorkOrder";
   
   // Instance fields
   private Date estimatedArrivalDate=null;
   private Date estimatedShipDate=null;
   private Date dateReceived=null;
   private Date dateShipped=null;
   private List<FreightDueInWorkOrderLineItem> lineItems=null;
   private List<FreightDueInWorkOrderLineItem> lineItemsToDelete=null;
   private String carrierName=null;
   private String carrierTrackingNumber=null;
   private String contactCompanyName=null;
   private String contactName=null;
   private String contactEmail=null;
   private String contactPreferredTelephoneNumber=null;
   private String depot=null;
   private String manufacturerName=null;
   private String purchaseOrder=null;
   private String quoteRequestId=null;
   private String referenceIdentifier=null;
   private String specialHandling=null;
   
   //********** Utility Methods
   /**
    * Indicates the type of entity as stored in the Datastore. 
    */
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",
            PersistentFreightDueInWorkOrderImpl._KIND);
      return PersistentFreightDueInWorkOrderImpl._KIND;
   }
   
   @Override
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS,"populateEntity(Entity)",entity);
      if (entity != null) {
         super.populateEntity(entity);
         entity.setProperty("carrierName", getCarrierName());
         entity.setProperty("carrierTrackingNumber", 
               getCarrierTrackingNumber());
         entity.setProperty("contactCompanyName",getContactCompanyName());
         entity.setProperty("contactEmail", getContactEmail());
         entity.setProperty("contactName",getContactName());
         entity.setProperty("contactPreferredTelephoneNumber",
               getContactPreferredTelephoneNumber());
         entity.setProperty("dateReceived", getDateReceived());
         entity.setProperty("dateShipped", getDateShipped());
         entity.setProperty("depot", getDepot());
         entity.setProperty("estimatedArrivalDate", getEstimatedArrivalDate());
         entity.setProperty("estimatedShipDate", getEstimatedShipDate());
         entity.setProperty("manufacturerName",getManufacturerName());
         entity.setProperty("purchaseOrder", getPurchaseOrder());
         entity.setProperty("quoteRequestId", getQuoteRequestId());
         entity.setProperty("receivedDate", getDateReceived());
         entity.setProperty("referenceIdentifier",getReferenceIdentifier());
         // Allows for storage of up to 1MB of text.
         entity.setProperty("specialHandling", new Text(getSpecialHandling()));
      } else {
         logger.severe("The specified Entity object was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS,"populateEntity(Entity)");
   }
   
   @Override
   protected void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity)",entity);
      FreightDueInWorkOrderLineItemDAO dao=null;
      List<PersistentFreightDueInWorkOrderLineItem> lineItems=null;
      String workOrderId=null;
      
      if (entity != null) {
         super.populateFromEntity(entity, ctx);
         setCarrierName(getPropertyAsString(entity,"carrierName"));
         setCarrierTrackingNumber(
               getPropertyAsString(entity,"carrierTrackingNumber"));
         setContactCompanyName(
               getPropertyAsString(entity,"contactCompanyName"));
         setContactEmail(getPropertyAsString(entity,"contactEmail"));
         setContactName(getPropertyAsString(entity,"contactName"));
         setContactPreferredTelephoneNumber(
               getPropertyAsString(entity,"contactPreferredTelephoneNumber"));
         setDateReceived(getPropertyAsDate(entity,"dateReceived"));
         setDateShipped(getPropertyAsDate(entity,"dateShipped"));
         setDepot(getPropertyAsString(entity,"depot"));
         setEstimatedArrivalDate(getPropertyAsDate(
               entity,"estimatedArrivalDate"));
         setEstimatedShipDate(
               getPropertyAsDate(entity,"estimatedShipDate"));
         setManufacturerName(getPropertyAsString(entity,"manufacturerName"));
         setPurchaseOrder(getPropertyAsString(entity,"purchaseOrder"));
         setQuoteRequestId(getPropertyAsString(entity,"quoteRequestId"));
         setReferenceIdentifier(
               getPropertyAsString(entity,"referenceIdentifier"));
         setSpecialHandling(
               getPropertyAsString(entity,"specialHandling"));
         
         //***** Let's get the line items...
         workOrderId=this.getIdentifier();
         dao=new FreightDueInWorkOrderLineItemDAOFactory().getDAO();
         lineItems=dao.findByWorkOrderId(workOrderId, ctx);
         this.lineItems=new ArrayList<FreightDueInWorkOrderLineItem>();
         for (PersistentFreightDueInWorkOrderLineItem item: lineItems) {
            this.lineItems.add(item);
         } // END for (PersistentFreightDueInWorkOrderLineItem item: lineItems)
      } else {
         logger.severe("The specified Entity object was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateFromEntity(Entity)");
   }
   
   @Override
   public void populateFromObject(FreightDueInWorkOrder wo) {
      logger.entering(_CLASS, "populateFromObject(FreightDueInWorkOrder)",wo);
      List<FreightDueInWorkOrderLineItem> itemsToRemove=null;
      Map<String, PersistentFreightDueInWorkOrderLineItemImpl> 
         targetLineItems=null;
      PersistentFreightDueInWorkOrderLineItemImpl pLineItem=null;
      String identifier=null;
      String sourceId=null;
      
      if (wo != null) {
         super.populateFromObject(wo);
         setCarrierName(wo.getCarrierName());
         setCarrierTrackingNumber(wo.getCarrierTrackingNumber());
         setContactCompanyName(wo.getContactCompanyName());
         setContactEmail(wo.getContactEmail());
         setContactName(wo.getContactName());
         setContactPreferredTelephoneNumber(
               wo.getContactPreferredTelephoneNumber());
         setDateReceived(wo.getDateReceived());
         setDateShipped(wo.getDateShipped());
         setDepot(wo.getDepot());
         setEstimatedArrivalDate(wo.getEstimatedArrivalDate());
         setEstimatedShipDate(wo.getEstimatedShipDate());
         setManufacturerName(wo.getManufacturerName());
         setPurchaseOrder(wo.getPurchaseOrder());
         setReferenceIdentifier(wo.getReferenceIdentifier());
         setSpecialHandling(wo.getSpecialHandling());
         
         //**************************************************
         // Let's synchronize the line items.
         //***** Build a list of identifiers for records in the source data set.
         targetLineItems=
              new TreeMap<String,PersistentFreightDueInWorkOrderLineItemImpl>();
         for (FreightDueInWorkOrderLineItem item: getLineItems()) {
            if (item instanceof PersistentFreightDueInWorkOrderLineItemImpl) {
               pLineItem=(PersistentFreightDueInWorkOrderLineItemImpl) item;
               identifier=pLineItem.getIdentifier();
               targetLineItems.put(identifier, pLineItem);
            } // END if (item instanceof PersistentFreightDueInWorkOrderLine...            
         } // END for (FreightDueInWorkOrderLineItem item: getLineItems())
         // Now let's iterate over the list of associated items.
         for (FreightDueInWorkOrderLineItem source: wo.getLineItems()) {
            sourceId="";
            if (source instanceof PersistentFreightDueInWorkOrderLineItem) {
               sourceId=((PersistentFreightDueInWorkOrderLineItem) 
                     source).getIdentifier();
            } else if (source instanceof WildObject) {
               sourceId=((WildObject) source).getIdentifier();
            } // END if (source instanceof PersistentFreightDueInWorkOrder...
            
            if ((sourceId == null) || (sourceId.length() == 0)) {
               pLineItem=new PersistentFreightDueInWorkOrderLineItemImpl();
               pLineItem.populateFromObject(source);
               pLineItem.setWorkOrderIdentifier(getIdentifier());
               addLineItem(pLineItem);
            } else {
               // Remove the line item with matching identifier
               pLineItem=targetLineItems.remove(sourceId);
               if (pLineItem == null) {
                  // The current quote does not have the specified line item,
                  // so we will add it.
                  pLineItem=new PersistentFreightDueInWorkOrderLineItemImpl();
                  pLineItem.populateFromObject(source);
                  pLineItem.setWorkOrderIdentifier(getIdentifier());
                  this.lineItems.add(pLineItem);
               } else {
                  pLineItem.updateFromObject(source);
               } // END if (pLineItem == null)               
            } // END if ((sourceId == null) || (sourceId.length() == 0))
         } // END for (FreightDueInWorkOrderLineItem item: wo.getLineItems())
         /* Let's iterate through the list of line items and find and remove 
          * those which are no longer associated. */
         if ((targetLineItems != null) && (targetLineItems.size() > 0)) {
            // Remove line items that are no longer associated.
            itemsToRemove=new ArrayList<FreightDueInWorkOrderLineItem>();
            for (String id: targetLineItems.keySet()) {
               itemsToRemove.add(targetLineItems.get(id));
            } // END for (String id: targetLineItems.keySet())
            for (FreightDueInWorkOrderLineItem item: itemsToRemove) {
               removeLineItem(item);
            } // END for (FreightDueInWorkOrderLineItem item: itemsToRemove)
            itemsToRemove=null;     // Free the reference
            targetLineItems=null;   // Free the reference
         } // END if ((targetLineItems != null) && (targetLineItems.size() > 0))          
      } else {
         logger.severe("The specified FreightDueInWOrkOrder is null.");
      } // END if (wo != null)
      logger.exiting(_CLASS, "populateFromObject(FreightDueInWorkOrder)");
   }
   
   /**
    * Update the contents of the current object from the specified object.
    * @param workOrder The work order to use as a reference when populating the
    * current work order.
    */
   public void updateFromObject(FreightDueInWorkOrder workOrder) {
      logger.entering(_CLASS, 
            "updateFromObject(FreightDueInWorkOrder)",
            workOrder);
      populateFromObject(workOrder);
      logger.exiting(_CLASS,"updateFromObject(FreightDueInWorkOrder)");
   }
   
   public String toPropertyString() {
      logger.entering(_CLASS, "toPropertyString()");
      List<FreightDueInWorkOrderLineItem> lineItems=null;
      String returnValue=null;
      StringBuilder sb=null;
      
      sb=new StringBuilder(1024);
      sb.append(super.toPropertyString());
      if (sb.length() > 0) sb.append(", ");
      sb.append("carrierName=\"").append(getCarrierName()).append("\", ");
      sb.append("carrierTrackingNumber=\"").append(
            getCarrierTrackingNumber()).append("\", ");
      sb.append("contactCompanyName=\"").append(
            getContactCompanyName()).append("\", ");
      sb.append("contactEmail=\"").append(getContactEmail()).append("\", ");
      sb.append("contactPreferredTelephoneNumber=\"").append(
            getContactPreferredTelephoneNumber()).append("\", ");
      sb.append("dateShipped=\"").append(
            getFormattedDate(getDateShipped())).append("\", ");
      sb.append("depot=\"").append(getDepot()).append("\", ");
      sb.append("estimatedShipDate=\"").append(
            getFormattedDate(getEstimatedShipDate())).append("\", ");
      sb.append("manufacturerName=\"").append(
            getManufacturerName()).append("\", ");
      sb.append("referenceIdentifier=\"").append(
            getReferenceIdentifier()).append("\", ");
      sb.append("specialHandling=\"").append(
            getSpecialHandling()).append("\", ");
      lineItems=getLineItems();
      for (FreightDueInWorkOrderLineItem lineItem: lineItems) {
         sb.append(lineItem.toString());
      } // END for (FreightDueInWorkOrderLineItem item: lineItems)
      
      logger.exiting(_CLASS, "toPropertyString()",returnValue);
      return returnValue;
   }
   public String toString() {
      logger.entering(_CLASS, "toString()");
      String returnValue=null;
      StringBuilder sb=null;
      
      sb=new StringBuilder();
      sb.append(_CLASS);
      sb.append(" [");
      sb.append(toPropertyString());
      sb.append("]");
      logger.exiting(_CLASS, "toString()",returnValue);
      return returnValue;
   }
   //********** accessor methods
   //***** carrierName
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getCarrierName()
    */
   @Override
   public String getCarrierName() {
      logger.entering(_CLASS, "getCarrierName()");
      logger.entering(_CLASS, "getCarrierName()", 
            this.carrierName);
      return this.carrierName;
      
   }
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setCarrierName(java.lang.String)
    */
   @Override
   public void setCarrierName(String carrierName) {
      logger.entering(_CLASS, "setCarrierName(String)", carrierName);
      this.carrierName = defaultValue(carrierName);
      logger.exiting(_CLASS, "setCarrierName(String)");
   }
   //***** carrierTrackingNumber
   @Override 
   public String getCarrierTrackingNumber() {
      logger.entering(_CLASS, "getCarrierTrackingNumber()");
      logger.entering(_CLASS, "getCarrierTrackingNumber()", 
            this.carrierTrackingNumber);
      return this.carrierTrackingNumber;
   }
   @Override
   public void setCarrierTrackingNumber(String trackingNumber) {
      logger.entering(_CLASS, "setCarrierTrackingNumber(String)", 
            trackingNumber);
      this.carrierTrackingNumber=defaultValue(trackingNumber);
      logger.exiting(_CLASS, "setCarrierTrackingNumber(String)");
   }
   //***** contactCompanyName
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getContactCompanyName()
    */
   @Override
   public String getContactCompanyName() {
      logger.entering(_CLASS, "getContactCompanyName()");
      logger.entering(_CLASS, "getContactCompanyName()", 
            this.contactCompanyName);
      return this.contactCompanyName;
   }

   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setContactCompanyName(java.lang.String)
    */
   @Override
   public void setContactCompanyName(String contactCompanyName) {
      logger.entering(_CLASS, "setContactCompanyName(String)", 
            contactCompanyName);
      this.contactCompanyName = contactCompanyName;
      logger.exiting(_CLASS, "setContactCompanyName(String)");
   }
   //***** contactEmail
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getContactEmail()
    */
   @Override
   public String getContactEmail() {
      logger.entering(_CLASS, "getContactEmail()");
      logger.entering(_CLASS, "getContactEmail()", this.contactEmail);
      return this.contactEmail;
   }
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setContactEmail(java.lang.String)
    */
   @Override
   public void setContactEmail(String contactEmail) {
      logger.entering(_CLASS, "setContactEmail(String)", contactEmail);
      this.contactEmail = contactEmail;
      logger.exiting(_CLASS, "setContactEmail(String)");
   }
   //***** contactName
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getContactName()
    */
   @Override
   public String getContactName() {
      logger.entering(_CLASS, "getContactName()");
      logger.entering(_CLASS, "getContactName()", this.contactName);
      return this.contactName;
   }

   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setContactName(java.lang.String)
    */
   @Override
   public void setContactName(String contactName) {
      logger.entering(_CLASS, "setContactName(String)", contactName);
      this.contactName = contactName;
      logger.exiting(_CLASS, "setContactName(String)");
   }
   
   //***** contactPreferredTelephoneNumber
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getContactPreferredTelephoneNumber()
    */
   @Override
   public String getContactPreferredTelephoneNumber() {
      logger.entering(_CLASS, "getContactPreferredTelephoneNumber()");
      logger.entering(_CLASS, "getContactPreferredTelephoneNumber()",
            this.contactPreferredTelephoneNumber);
      return contactPreferredTelephoneNumber;
   }
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setContactPreferredTelephoneNumber(java.lang.String)
    */
   @Override
   public void setContactPreferredTelephoneNumber(
         String contactPreferredTelephoneNumber) {
      logger.entering(_CLASS, "setContactPreferredTelephoneNumber(String)",
            contactPreferredTelephoneNumber);
      this.contactPreferredTelephoneNumber = contactPreferredTelephoneNumber;
      logger.exiting(_CLASS, "setContactPreferredTelephoneNumber(String)");
   }
   //***** dateReceived
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getDateReceived()
    */
   @Override
   public Date getDateReceived() {
      logger.entering(_CLASS, "getDateReceived()");
      logger.entering(_CLASS, "getDateReceived()", this.dateReceived);
      return this.dateReceived;
   }
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setDateReceived(java.util.Date)
    */
   @Override
   public void setDateReceived(Date dateReceived) {
      logger.entering(_CLASS, "setDateReceived(Date)", dateReceived);
      this.dateReceived = dateReceived;
      logger.exiting(_CLASS, "setDateReceived(Date)");
   }
   //***** dateShipped  
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getDateShipped()
    */
   @Override
   public Date getDateShipped() {
      logger.entering(_CLASS, "getDateShipped()");
      logger.entering(_CLASS, "getDateShipped()", this.dateShipped);
      return this.dateShipped;
   }
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setDateShipped(java.util.Date)
    */
   @Override
   public void setDateShipped(Date dateShipped) {
      logger.entering(_CLASS, "setDateShipped(Date)", dateShipped);
      this.dateShipped = dateShipped;
      logger.exiting(_CLASS, "setDateShipped(Date)");
   }
   //***** depot
   @Override
   public String getDepot() {
      logger.entering(_CLASS, "getDepot()");
      logger.exiting(_CLASS, "getDepot()",this.depot);
      return this.depot;
   }
   @Override
   public void setDepot(String depot) {
      logger.entering(_CLASS, "setDepot(String)",depot);
      this.depot=defaultValue(depot);
      logger.exiting(_CLASS, "setDepot(String)");
   }
   //***** estimatedArrivalDate
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getEstimatedArrivalDate()
    */
   @Override
   public Date getEstimatedArrivalDate() {
      logger.entering(_CLASS, "getEstimatedArrivalDate()");
      logger.exiting(_CLASS, "getEstimatedArrivalDate()",
            this.estimatedArrivalDate);
      return this.estimatedArrivalDate;
   }
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setEstimatedArrivalDate(java.util.Date)
    */
   @Override
   public void setEstimatedArrivalDate(Date estimatedArrivalDate) {
      logger.entering(_CLASS, "setEstimatedArrivalDate(Date)", 
            estimatedArrivalDate);
      this.estimatedArrivalDate = estimatedArrivalDate;
      logger.exiting(_CLASS, "setEstimatedArrivalDate(Date)");
      
   }
   //***** estimatedShipDate
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getEstimatedDateShipped()
    */
   @Override
   public Date getEstimatedShipDate() {
      logger.entering(_CLASS, "getEstimatedShipDate()");
      logger.entering(_CLASS, "getEstimatedShipDate()",
            this.estimatedShipDate);
      return this.estimatedShipDate;
   }
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setEstimatedDateShipped(java.util.Date)
    */
   @Override
   public void setEstimatedShipDate(Date estimatedShipDate) {
      logger.entering(_CLASS, "setEstimatedShipDate(Date)", estimatedShipDate);
      this.estimatedShipDate = estimatedShipDate;
      logger.exiting(_CLASS, "setEstimatedShipDate(Date)");
   }
   //***** lineItems
   /**
    * Appends the specified line item to the end of this list.
    * @param item
    * @return <code>true</code> if the item was successfully added to the list
    * or <code>false</code> it it was not added.
    */
   public boolean addLineItem(
         FreightDueInWorkOrderLineItem item) {
      logger.entering(_CLASS, 
            "addLineItem(FreightDueInWorkOrderLineItem)",item);
      boolean added=false;
      int size=0;
      int tmpLineItemNumber=0;
      
      PersistentFreightDueInWorkOrderLineItemImpl pItem=null;      
      
      if (item != null) {
         /* Let's check to see if the item passed is of the 
          * persistent variety */
         if (!(item instanceof PersistentFreightDueInWorkOrderLineItemImpl)) 
         {
            // It would seem the lineItem is not of the persistent variety.
            pItem=new PersistentFreightDueInWorkOrderLineItemImpl();
            pItem.populateFromObject(item);
            logger.finest(
                  "item is NOT persistent. Creating persistent version.");
         } else {
            // The item is, so a simple case shall do...
            pItem=(PersistentFreightDueInWorkOrderLineItemImpl) item;
            logger.finest("item is already persistent.");
         } // END if (!(item instanceof PersistentFreightDueInWorkOrder...
         if ( this.lineItems == null) {
            // The line item list doesn't exist, so create it.
            this.lineItems=new ArrayList<FreightDueInWorkOrderLineItem>();                      
         } // END if ( this.lineItems == null) 
         // Get the total number of lineItems
         size=this.lineItems.size();
         tmpLineItemNumber=size+1;
         pItem.setLineItemNumber(tmpLineItemNumber); 
         added=this.lineItems.add(pItem);
      } else {
         logger.warning("The item passed as a parameter was null.");
      } // END if (item != null)
      
      logger.exiting(_CLASS, "addLineItem(FreightDueInWorkOrderLineItem)",
            added);
      return added;
   }
   /**
    * Inserts the specified line item at the specified position in this list.
    * <p>Any item that presently exists at the specified position will be moved 
    * to the right.  For example, if you have a work order that already has 
    * four associated line items and you invoke the method in the following way:
    * </p>
    * <pre>
    * workOrder.addLineItem(newItem,4);
    * </pre>
    * <p>The line item that was in position four (4) will now be in position 
    * five (5) and the <code>newItem</code> will now be in position four.</p> 
    * <table>
    *   <tr>
    *     <td><strong>NOTE:</strong></td>
    *     <td>
    *       The first element in the list has a position number of one (1). 
    *       I.e., the list of line items is not zero-based.
    *     </td>
    *   </tr>
    * </table>
    * @param item
    * @param position
    */
   public void addLineItem(int lineItemNumber,
         FreightDueInWorkOrderLineItem item) {
      logger.entering(_CLASS, "addLineItem(int,FreightDueInWorkOrderLineItem)",
            new Object[]{lineItemNumber, item});
      int size=0;
      FreightDueInWorkOrderLineItem tmpItem=null;
      String msg="";
      
      if (item != null) {
         size=this.lineItems.size();
         if ((lineItemNumber < 1) || 
             (lineItemNumber > size + 1)) {
            msg=MessageFormat.format(lineItemNumberOutOfBoundsTemplate,size+2);
            throw new IndexOutOfBoundsException(msg);
         } else {
            if (lineItemNumber == size+1) {
               /* This is adding to the end of the list ... there is a method
                * for that already. */ 
                addLineItem(item);
            } else {
               // Replace the specified line item
               this.lineItems.set(lineItemNumber -1, item);
               // Let's renumber the items.
               for (int pos=lineItemNumber; pos < size; pos++) {
                  tmpItem=this.lineItems.get(pos);
                  tmpItem.setLineItemNumber(tmpItem.getLineItemNumber() + 1);
               } // END for (int pos=lineItemNumber - 1; pos < size; pos++)
            } // END if (lineItemNumber == size+1)
         } // END if ((lineItemNumber < 1) || (lineItemNumber > ...
      } else {
         logger.fine("The item parameter was null, so nothing was added.");
      } // END if (item != null)
      
      logger.exiting(_CLASS, "addLineItem(int,FreightDueInWorkOrderLineItem)");
   }
   
   /**
    * Create a line item.
    */
   public FreightDueInWorkOrderLineItem createLineItem() {
      logger.entering(_CLASS, "createLineItem()");
      PersistentFreightDueInWorkOrderLineItemImpl item=null;
      
      item=new PersistentFreightDueInWorkOrderLineItemImpl();
      
      logger.exiting(_CLASS, "createLineItem()",item);
      return item;
   }
   
   /**
    * Returns the line item at the specified position in the list.
    * <table>
    *   <tr>
    *     <td><strong>NOTE:</strong></td>
    *     <td>
    *       The first element in the list has a position number of one (1). 
    *       I.e., the list of line items is not zero-based.
    *     </td>
    *   </tr>
    * </table>
    */
   @Override
   public FreightDueInWorkOrderLineItem getLineItem(int lineItemNumber) {
      logger.entering(_CLASS, "getLineItem(int)",lineItemNumber);
      int size=0;
      FreightDueInWorkOrderLineItem item=null;
      String msg=null;
      
      size=this.lineItems.size();
      if ((lineItemNumber > 0) || (lineItemNumber <= size)) {
         /* Since the array stores everything in a zero-index and we are using
          * 1-index arrays, we'll use the requested lineItemNumber -1 */
         item=this.lineItems.get(lineItemNumber -1);
      } else {
         msg=MessageFormat.format(lineItemNumberOutOfBoundsTemplate, size);
         logger.warning(msg);
         throw new IndexOutOfBoundsException(msg);
      } // END if ((lineItemNumber > 0) || (lineItemNumber <= size))
      logger.exiting(_CLASS,"getLineItem(int)",item);
      return item;
   }
   
   /**
    * Returns an immutable list of line items associated with the work order.
    * @return
    */
   public List<FreightDueInWorkOrderLineItem> getLineItems() {
      logger.entering(_CLASS, "getLineItems()");      
      List<FreightDueInWorkOrderLineItem> returnableLineItems=null;
      if (this.lineItems == null) {
         this.lineItems=new ArrayList<FreightDueInWorkOrderLineItem>();
       
      } // END if (this.lineItems == null) 
      returnableLineItems=Collections.unmodifiableList(this.lineItems);
      logger.exiting(_CLASS, "getLineItems()",returnableLineItems);
      return returnableLineItems;
   }
   
   /**
    * Returns an immutable list of line items that should be removed.
    * @return A list of line items that should be removed from the persistent
    * data store.
    */
   public List<FreightDueInWorkOrderLineItem> getLineItemsToDelete() {
      logger.entering(_CLASS, "getLineItemsToDelete()");
      List<FreightDueInWorkOrderLineItem> lineItemsToRemove=null;
      if (this.lineItemsToDelete != null) {
         lineItemsToRemove=Collections.unmodifiableList(this.lineItemsToDelete);
      } else {
         lineItemsToRemove=new ArrayList<FreightDueInWorkOrderLineItem>();
      } // END if (this.lineItemsToRemove != null)
      logger.exiting(_CLASS, "getLineItemsToDelete()",lineItemsToRemove);
      return lineItemsToRemove;
   }
   
   /**
    * Remove the specified line item from the list of associated line items.
    */
   public boolean removeLineItem(
         FreightDueInWorkOrderLineItem item) {
      logger.entering(_CLASS, "removeLineItem(FreightDueInWorkOrderLineItem)",
            item);
      boolean removed=false;
      int counter=1;
      
      if (item != null) {
         removed=this.lineItems.remove(item);
         if (removed) {
            /* The line item was removed, so lets add it to the list to remove
             * when the FreightDueInWorkOrder is removed. */
            if (this.lineItemsToDelete == null) {
               this.lineItemsToDelete=
                     new ArrayList<FreightDueInWorkOrderLineItem>();
            } // END if (this.lineItemsToDelete == null)
            if (item instanceof PersistentFreightDueInWorkOrderLineItem) {
               this.lineItemsToDelete.add(item);
            } // END if (item instanceof ...
            // Re-number the line items
            for (FreightDueInWorkOrderLineItem anItem: this.lineItems) {
               anItem.setLineItemNumber(counter);
               counter++;
            } // END for (FreightDueInWorkOrderLineItem anItem: this.lineItems)               
         } // END if (removed)
      } else {
         logger.warning("The item parameter is null.");
      } // END 
      logger.exiting(_CLASS, "removeLineItem(FreightDueInWorkOrderLineItem)",
            removed);
      return removed;
   }
   
   /**
    * Replace the identified line item with the specified object.
    * @param lineItemNumber - the line item which should be replaced with the
    * specified <code>FreightDueInWorkOrderLineItem</code> object.
    * @param item - the <code>FreightDueInWorkOrderLineItem</code> object
    * that should replace the current object
    * @return The work order line item object that was previously at the 
    * specified position.
    */
   public FreightDueInWorkOrderLineItem setLineItem(int lineItemNumber, 
         FreightDueInWorkOrderLineItem item) {
      logger.entering(_CLASS, "setLineItem(int,FreightDueInWorkOrderLineItem)",
            new Object[] {lineItemNumber, item});
      int index=0;
      int size=this.lineItems.size();
      FreightDueInWorkOrderLineItem previousLineItem=null;
      String msg=null;
      
      if (
            (lineItemNumber < 1) ||
            (lineItemNumber > this.lineItems.size()+1)
         ) {
         msg=MessageFormat.format(lineItemNumberOutOfBoundsTemplate,size+2);
         throw new IndexOutOfBoundsException(msg);            
      } else if (item == null) {
         msg="FreightDueInWorkOrderLineItem parameter is null.";
         throw new NullPointerException(msg);
      } else {
         index=item.getLineItemNumber() - 1;
         if ((index == 0) && (this.lineItems.size() == 0)) {
            this.lineItems.add(item);
         } else {
            previousLineItem=this.lineItems.set(index,item);
         } // END if ((index == 0) && (this.lineItems.size() == 0))
      } // END if ((lineItemNumber < 1) || (lineItemNumber > this.lineItems...
      
      logger.exiting(_CLASS, "setLineItem(int,FreightDueInWorkOrderLineItem)", 
            previousLineItem);
      return previousLineItem;
   }
   //***** manufacturerName
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getManufacturerName()
    */
   @Override
   public String getManufacturerName() {
      logger.entering(_CLASS, "getManufacturerName()");
      logger.exiting(_CLASS, "getManufacturerName()", this.manufacturerName);
      return this.manufacturerName;
   }
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setManufacturerName(java.lang.String)
    */
   @Override
   public void setManufacturerName(String manufacturerName) {
      logger.entering(_CLASS, "setManufacturerName(String)",manufacturerName);
      this.manufacturerName=defaultValue(manufacturerName);
      logger.exiting(_CLASS, "setManufacturerName(String)");
   }
   //***** purchaseOrder
   @Override
   public String getPurchaseOrder() {
      logger.entering(_CLASS, "getPurchaseOrder()");
      logger.exiting(_CLASS, "getPurchaseOrder()",this.purchaseOrder);
      return this.purchaseOrder;
   }
   @Override
   public void setPurchaseOrder(String purchaseOrder) {
      logger.entering(_CLASS, "setPurchaseOrder(String)",purchaseOrder);
      this.purchaseOrder=defaultValue(purchaseOrder);
      logger.exiting(_CLASS, "setPurchaseOrder(String)");
   }
   //***** quoteRequestId
   @Override
   public String getQuoteRequestId() {
      logger.entering(_CLASS, "getQuoteRequestId()");
      logger.exiting(_CLASS, "getQuoteRequestId()",this.quoteRequestId);
      return this.quoteRequestId;
   }
   @Override
   public void setQuoteRequestId(String requestId) {
      logger.entering(_CLASS, "setQuoteRequestId(String)",requestId);
      this.quoteRequestId=defaultValue(requestId);
      logger.exiting(_CLASS, "setQuoteRequestId(String)");
   }
   //***** referenceIdentifier
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getReferenceIdentifier()
    */
   @Override
   public String getReferenceIdentifier() {
      logger.entering(_CLASS, "getReferenceIdentifier()");
      logger.exiting(_CLASS, "getReferenceIdentifier()", 
            this.referenceIdentifier);
      return this.referenceIdentifier;
   }
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setReferenceIdentifier(java.lang.String)
    */
   @Override
   public void setReferenceIdentifier(String referenceIdentifier) {
      logger.entering(_CLASS, "setReferenceIdentifier(String)",
            referenceIdentifier);
      this.referenceIdentifier=defaultValue(referenceIdentifier);
      logger.exiting(_CLASS, "setReferenceIdentifier(String)");
   }
   //***** specialHandling
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#getSpecialHandling()
    */
   @Override
   public String getSpecialHandling() {
      logger.entering(_CLASS, "getSpecialHandling()");
      logger.exiting(_CLASS, "getSpecialHandling()",this.specialHandling);
      return this.specialHandling;
   }
   /* (non-Javadoc)
    * @see com.wildstartech.servicedesk.logistics.ltl.jsf.FreightDueIn#setSpecialHandling(java.lang.String)
    */
   @Override
   public void setSpecialHandling(String specialHandling) { 
      logger.entering(_CLASS, "setSpecialHandling(String)",specialHandling);
      this.specialHandling=defaultValue(specialHandling);
      logger.exiting(_CLASS, "setSpecialHandling(String)");      
   }
   //***** statusReason
   public List<String> getAvailableStatusReasons() {
      logger.entering(_CLASS, "getAvailableStatusReasons()");
      List<String> statusReasons=null;
      String statusState=null;
      
      statusState=getStatusState();
      statusReasons=
            PersistentFreightDueInWorkOrderImpl.statusReasons.get(statusState);
      if (statusReasons == null) {
         statusReasons=new ArrayList<String>();
      } // END if (statusReasons == null)
      logger.exiting(_CLASS, "getAvailableStatusReasons()",statusReasons);
      return statusReasons;
   }
   //***** statusState
   public List<String> getAvailableStatusStates() {
      logger.entering(_CLASS, "getAvailableStatusStates()");
      logger.exiting(_CLASS, "getAvailableStatusStates()",
            PersistentFreightDueInWorkOrderImpl.statusStates);
      return PersistentFreightDueInWorkOrderImpl.statusStates;
   }
   //***** workOrderId
   /**
    * A unique identifier for the work order.
    * 
    * <p>As the <code>FreightDueInWorkOrder</code> is an extension of the 
    * <code>BasicTicket</code> interface, the <code>workOrderId</code> property
    * IS an alias for the <code>requestId</code> property.  Any concrete 
    * implementation of this interface <strong>MUST</strong> ensure that 
    * invocation of the <code>getWorkOrderId()</code> method returns the 
    * <strong>EXACT</strong> same value as the invocation of the 
    * <code>getRequestId</code> method of the <code>BasicTicket</code> super 
    * class.</p>
    * 
    * @return The unique identifier for this work order.
    */
   public String getWorkOrderId() {
      logger.entering(_CLASS, "getWorkOrderId()");
      String workOrderId="";
      
      workOrderId=super.getRequestId();
      
      logger.exiting(_CLASS, "getWorkOrderId()",workOrderId);
      return workOrderId;
   }
   /**
    * Sets the unique identifier for the work order.
    * <p>As the <code>FreightDueInWorkOrder</code> is an extension of the 
    * <code>BasicTicket</code> interface, the <code>workOrderId</code> property
    * IS an alias for the <code>requestId</code> property.  Any concrete 
    * implementation of this interface <strong>MUST</strong> ensure that 
    * invocation of the <code>setWorkOrderId(String)</code> method updates the
    * <strong>EXACT</strong> same field as the invocation of the 
    * <code>setRequestId(String)</code> method of the 
    * <code>BasicTicket</code> super class.</p>
    * 
    * @param workOrderId the value to be used as the unique identifier of the
    * work order.
    */
   public void setWorkOrderId(String workOrderId) {
      logger.entering(_CLASS, "setWorkOrderId(String)",workOrderId);
      super.setRequestId(workOrderId);
      logger.entering(_CLASS, "setWorkOrderId(String)");
   }
}