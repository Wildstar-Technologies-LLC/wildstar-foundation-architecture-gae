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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.journal.PersistentJournalEntryImpl;
import com.wildstartech.gae.wfa.dao.ticketing.PersistentBasicTicketImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.WildObject;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentReceiverWorkOrder;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentReceiverWorkOrderLineItem;
import com.wildstartech.wfa.journal.JournalEntry;
import com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrder;
import com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrderLineItem;

public class PersistentReceiverWorkOrderImpl 
extends PersistentBasicTicketImpl<ReceiverWorkOrder>
implements PersistentReceiverWorkOrder {
   /** Used in object serialization. */
   private static final long serialVersionUID = 1064420473087362429L;
   private static final String _CLASS=
         PersistentReceiverWorkOrderImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   protected static final String _KIND=
         "com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrder"; 
   
   private Date dateReceived=null;
   private JournalEntry newJournalEntry=null;
   private List<ReceiverWorkOrderLineItem> lineItems=null;
   private List<ReceiverWorkOrderLineItem> lineItemsToDelete=null;
   private String billOfLadingNumber="";
   private String depot="";
   private String inboundCarrier="";
   private String purchaseOrderNumber="";
   private String salesOrderNumber="";
      
   /** 
    * Default, no-argument constructor.
    */
   public PersistentReceiverWorkOrderImpl() {
      logger.entering(_CLASS, "PersistentReceiverWorkOrderImpl()");      
      init();
      logger.exiting(_CLASS,"PersistentReceiverWorkOrderImpl()");
   }
   // ***** Utility methods
      
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.entering(_CLASS, "getKind()",
            PersistentReceiverWorkOrderImpl._KIND);
      return PersistentReceiverWorkOrderImpl._KIND;
   }
   /**
    * Initialization routine.
    */
   private void init() {
	  logger.entering(_CLASS, "init()");
      this.lineItems=new ArrayList<ReceiverWorkOrderLineItem>();
      this.lineItemsToDelete=new ArrayList<ReceiverWorkOrderLineItem>();
      this.newJournalEntry=new PersistentJournalEntryImpl();
	  logger.exiting(_CLASS, "init()");
   }
   @Override
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(Entity)", entity);
      if (entity != null) {
         super.populateEntity(entity);
         entity.setProperty("billOfLadingNumber", getBillOfLadingNumber());
         entity.setProperty("depot",getDepot());
         entity.setProperty("inboundCarrier", getInboundCarrier());
         entity.setProperty("dateReceived", getDateReceived());
         entity.setProperty("purchaseOrderNumber", getPurchaseOrderNumber());
         entity.setProperty("salesOrderNumber", getSalesOrderNumber());         
      } else {
         logger.severe("The entity passed to the method was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateEntity(Entity)");
   }
   
   @Override
   protected void populateFromEntity(Entity entity,UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity)", entity);
      List<PersistentReceiverWorkOrderLineItem> items=null;
      ReceiverWorkOrderLineItemDAOImpl itemDAO=null;
      String identifier="";
      
      if (entity != null) {
         super.populateFromEntity(entity, ctx);
         setBillOfLadingNumber(
               getPropertyAsString(entity,"billOfLadingNumber"));
         setDepot(getPropertyAsString(entity,"depot"));
         setInboundCarrier(getPropertyAsString(entity,"inboundCarrier"));
         setDateReceived(getPropertyAsDate(entity,"dateReceived"));
         setPurchaseOrderNumber(
               getPropertyAsString(entity,"purchaseOrderNumber"));
         setSalesOrderNumber(getPropertyAsString(entity,"salesOrderNumber"));
         // Get the line items.
         identifier=getIdentifier();
         itemDAO=new ReceiverWorkOrderLineItemDAOImpl();
         items=itemDAO.findByWorkOrderId(identifier,ctx);
         for (PersistentReceiverWorkOrderLineItem item: items) {
        	 addLineItem(item);
         } // END for (PersistentReceiverWorkOrderLineItem item: items)
      } else {
         logger.severe("The entity passed to the method was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateFromEntity(Entity)");
   }
   
   public void populateFromObject(ReceiverWorkOrder workOrder) {
      logger.entering(_CLASS, 
            "populateFromObject(ReceiverWorkOrder)", 
            workOrder);
      List<ReceiverWorkOrderLineItem> itemsToRemove=null;
      List<ReceiverWorkOrderLineItem> lineItems=null;
      PersistentReceiverWorkOrderLineItemImpl pLineItem=null;
      String sourceId="";
      TreeMap<String,PersistentReceiverWorkOrderLineItemImpl> 
         targetLineItems=null;
      
      if (workOrder != null) {
         super.populateFromObject(workOrder);
         setBillOfLadingNumber(workOrder.getBillOfLadingNumber());
         setDepot(workOrder.getDepot());
         setInboundCarrier(workOrder.getInboundCarrier());
         setDateReceived(workOrder.getDateReceived());
         setPurchaseOrderNumber(workOrder.getPurchaseOrderNumber());
         setSalesOrderNumber(workOrder.getSalesOrderNumber());
         setNewJournalEntry(workOrder.getNewJournalEntry());
         
         //********************************************************************
         //* Line Item Synchronization
         /* Build a list of identifiers for records that are already in the
          * lineItem list of the object being updated */
         targetLineItems=
               new TreeMap<String,PersistentReceiverWorkOrderLineItemImpl>();
         for (ReceiverWorkOrderLineItem item: getLineItems()) {
            pLineItem = (PersistentReceiverWorkOrderLineItemImpl) item;
            targetLineItems.put(pLineItem.getIdentifier(), pLineItem);
         } // END for (ReceiverWorkOrderLineItem item: getLineItems())
         // Now let's iterate over the list of "new" associated line items
         lineItems=workOrder.getLineItems();
         for (ReceiverWorkOrderLineItem source:lineItems) {
            sourceId = "";
            if (source instanceof PersistentReceiverWorkOrderLineItemImpl) {
               pLineItem=(PersistentReceiverWorkOrderLineItemImpl) source;
               sourceId=pLineItem.getIdentifier();
            } else if (source instanceof WildObject) {
               sourceId=((WildObject) source).getIdentifier();
            } // END if (item instanceof ...    
            
            if (isEmpty(sourceId)) {
               /* The sourceId property is null, so create a new object and
                * populate it. */
               pLineItem=new PersistentReceiverWorkOrderLineItemImpl();
               pLineItem.populateFromObject(source);
               pLineItem.setWorkOrderIdentifier(getIdentifier());
               addLineItem(pLineItem);
            } else {
               // Remove the line item with the matching identifier
               pLineItem=targetLineItems.remove(sourceId);
               if (pLineItem == null) {
                  // The current work order does not have the specified line
                  //  item, so we will add it.
                  pLineItem=new PersistentReceiverWorkOrderLineItemImpl();
                  pLineItem.populateFromObject(source);
                  pLineItem.setWorkOrderIdentifier(getIdentifier());
                  this.lineItems.add(pLineItem);
               } else {
                  pLineItem.populateFromObject(source);
               } // END if (pLineItem == null)
            } // END if (isEmpty(sourceId))
         } // END for (ReceiverWorkOrderLineItem item: workOrder.getLineItems())
         /*
          * Let's iterate through the list of line items found and remove
          * Those which are no longer associated.
          */
         if ((targetLineItems != null) && (targetLineItems.size() > 0)) {
            // Remove the line items that are no longer a mtach
            itemsToRemove=new ArrayList<ReceiverWorkOrderLineItem>();
            for (String id: targetLineItems.keySet()) {
               itemsToRemove.add(targetLineItems.get(id));
            } // END for (String id: targetLineItems.keySet())
            for (ReceiverWorkOrderLineItem item: itemsToRemove) {
               removeLineItem(item);
            } // END for (ReceiverWorkOrderLineItem item: itemsToRemove)
            itemsToRemove = null;      // Free the reference
            targetLineItems = null;    // Free the reference
         }
      } else {
         logger.warning("The specified ReceiverWorkOrder object was null.");
      } // END if (workOrder != null)
      
      logger.exiting(_CLASS, "populateFromObject(ReceiverWorkOrder)");
   }   
   
   /**
    * Update the contents of the current object from the specified object.
    * @param workOrder The work order to use as a reference when populating the
    * current work order.
    */
   public void updateFromObject(ReceiverWorkOrder receiver) {
      logger.entering(_CLASS, 
            "updateFromObject(ReceiverWorkOrder)",
            receiver);
      populateFromObject(receiver);
      logger.exiting(_CLASS,"updateFromObject(ReceiverWorkOrder)");
   }
   
   //********** Accessor Methods
   
   //***** carrier
   @Override
   public String getInboundCarrier() {
      logger.entering(_CLASS, "getInboundCarrier()");
      logger.exiting(_CLASS, "getInboundCarrier()",this.inboundCarrier);
      return this.inboundCarrier;
   }

   @Override
   public void setInboundCarrier(String carrier) {
      logger.entering(_CLASS, "setInboundCarrier(String)",carrier);
      this.inboundCarrier=defaultValue(carrier);
      logger.entering(_CLASS, "setInboundCarrier(String)");
   }

   //***** billOfLadingNumber
   @Override
   public String getBillOfLadingNumber() {
      logger.entering(_CLASS, "getBillOfLadingNumber()");
      logger.exiting(_CLASS, "getBillOfLadingNumber()",this.billOfLadingNumber);
      return this.billOfLadingNumber;
   }

   @Override
   public void setBillOfLadingNumber(String billOfLading) {
      logger.entering(_CLASS, "setBillOfLadingNumber(String)",billOfLading);
      this.billOfLadingNumber=defaultValue(billOfLading);
      logger.entering(_CLASS, "setBillOfLadingNumber(String)");
   }

   //***** dateReceived
   @Override
   public Date getDateReceived() {
      logger.entering(_CLASS, "getDateReceived()");
      logger.exiting(_CLASS, "getDateReceived()",this.dateReceived);
      return this.dateReceived;
   }

   @Override
   public void setDateReceived(Date dateReceived) {
      logger.entering(_CLASS, "setDateReceived(Date)",dateReceived);
      this.dateReceived=dateReceived;
      logger.exiting(_CLASS, "setDateReceived(Date)");
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
      this.depot=depot;
      logger.exiting(_CLASS, "setDepot(String)");
   }
   //***** lineItems
   @Override
   public List<ReceiverWorkOrderLineItem> getLineItems() {
      logger.entering(_CLASS,"getLineItems()");
      
      List<ReceiverWorkOrderLineItem> lineItems=null;
      lineItems=Collections.unmodifiableList(this.lineItems);
      
      logger.exiting(_CLASS, "getLineItems()",lineItems);
      return lineItems;
   }
   
   /**
    * Returns a list of line items that should be removed when the object is 
    * saved.
    * @return
    */
   protected List<ReceiverWorkOrderLineItem> getLineItemsToDelete() {
	   logger.entering(_CLASS, "getLineItemsToDelete()");
	   logger.exiting(_CLASS, "getLineItemsToDelete()",this.lineItemsToDelete);
	   return this.lineItemsToDelete;
   }

   @Override
   public ReceiverWorkOrderLineItem addLineItem(
         ReceiverWorkOrderLineItem item) {
      logger.entering(_CLASS, "addLineItem(ReceiverWorkOrder)",item);
      boolean added=false;
      int size=0;
      PersistentReceiverWorkOrderLineItemImpl pLineItem=null;
      PersistentReceiverWorkOrderLineItemImpl tmpLineItem=null;
      
      if (item != null) {
         // The lineItem is NOT null, so let's see about adding it.
         // Is it a PersistentReceiverWorkOrderLineItem instance?
         if (!(item instanceof PersistentReceiverWorkOrderLineItemImpl)) {
            pLineItem=new PersistentReceiverWorkOrderLineItemImpl();
            pLineItem.populateFromObject(item);
         } else {
            pLineItem = (PersistentReceiverWorkOrderLineItemImpl) item;
         } // END if (!(item instanceof PersistentReceiverWorkOrderLineItem...
         /**
          * Loop through the list of associated line items to see if the new one
          * passed is already in the list
          */
         size=this.lineItems.size();
         for (int pos = 0; pos < size; pos++) {
            tmpLineItem=(PersistentReceiverWorkOrderLineItemImpl) 
                  this.lineItems.get(pos);
            if (
                  (tmpLineItem.equals(pLineItem)) ||
                  (tmpLineItem.isNonPersistentEquivalent(pLineItem))
               ) {
               /* Replace the item in the list with the specified persistent
                * object. */
               this.lineItems.remove(pos);
               this.lineItems.add(pos,pLineItem);
               added=true;
               break;
            } // END if ((tmpLineItem.equals(pLineItem)) ||
         } // END for (int pos = 0; pos < size; pos++)
         if (!added) {
            // The item is not already in the list, so add it.
            this.lineItems.add(pLineItem);
         } // END if (!added)
      } else {
         logger.warning("The ReceiverWorkOrderLineItem is null.");
      } // END if (item != null)
      logger.exiting(_CLASS, "addLineItem(ReceiverWorkOrder)",pLineItem);
      return pLineItem;
   }
   @Override
   public ReceiverWorkOrderLineItem removeLineItem(
         ReceiverWorkOrderLineItem item) {
      logger.entering(_CLASS,"remove(ReceiverWorkOrderLineItem)",item);
      boolean removed = false;
      int counter = 1;
      
      if (item != null) {
         removed=this.lineItems.remove(item);
         if (removed) {
            /* The line item was removed, so lets  add it to the list to remove
             * when the ReceiverWorkOrder is saved. */
            if (this.lineItemsToDelete == null) {
               this.lineItemsToDelete=
                     new ArrayList<ReceiverWorkOrderLineItem>();
            } // END if (this.lineItemsToDelete == null)
            if (item instanceof PersistentReceiverWorkOrderLineItem) {
               this.lineItemsToDelete.add(
                     (PersistentReceiverWorkOrderLineItem)item);
            } // END if (item instanceof PersistentReceiverWorkOrderLineItem)
            for (ReceiverWorkOrderLineItem lineItem: this.lineItems) {
               lineItem.setLineItemNumber(counter++);
            } // END for (ReceiverWorkOrderLineItem lineItem: this.lineItems)
         } // END if (removed)
      } else {
         logger.warning("The item parameter is null.");          
      } // END if (item != null)
      
      logger.exiting(_CLASS,"remove(ReceiverWorkOrderLineItem)",item);
      return item;
   }

   //***** purchaseOrderNumber
   @Override
   public String getPurchaseOrderNumber() {
      logger.entering(_CLASS, "getPurchaseOrderNumber()");
      logger.exiting(_CLASS, "getPurchaseOrderNumber()",
            this.purchaseOrderNumber);
      return this.purchaseOrderNumber;
   }

   @Override
   public void setPurchaseOrderNumber(String purchaseOrderNumber) {
      logger.entering(_CLASS, 
            "setPurchaseOrderNumber(String)",
            purchaseOrderNumber);
      this.purchaseOrderNumber=defaultValue(purchaseOrderNumber);
      logger.entering(_CLASS,"setPurchaseOrderNumber(String)");
   }
   
   //***** salesOrderNumber
   @Override
   public String getSalesOrderNumber() {
      logger.entering(_CLASS, "getSalesOrderNumber()");
      logger.exiting(_CLASS, "getSalesOrderNumber()",
            this.salesOrderNumber);
      return this.salesOrderNumber;
   }

   @Override
   public void setSalesOrderNumber(String salesOrderNumber) {
      logger.entering(_CLASS, "setSalesOrderNumber(String)",salesOrderNumber);
      this.salesOrderNumber=defaultValue(salesOrderNumber);
      logger.exiting(_CLASS, "setSalesOrderNumber(String)");
   }

   //***** newJournalEntry
   @Override
   public JournalEntry getNewJournalEntry() {
      logger.entering(_CLASS, "getNewJournalEntry()");
      if (this.newJournalEntry == null) {
         this.newJournalEntry=new PersistentJournalEntryImpl();
      } // END if (this.newJournalEntry == null)
      logger.exiting(_CLASS, "getNewJournalEntry()",this.newJournalEntry);
      return this.newJournalEntry;
   }

   @Override
   public void setNewJournalEntry(JournalEntry entry) {
      logger.entering(_CLASS, "setNewJournalEntry(JournalEntry)", entry);
      if (entry != null) {
         if (!(entry instanceof PersistentJournalEntryImpl)) {
            this.newJournalEntry=new PersistentJournalEntryImpl();
            ((PersistentJournalEntryImpl) this.newJournalEntry)
               .populateFromObject(entry);
         } else {
            this.newJournalEntry=entry;
         } // END if (!(entry instanceof PersistentJournalEntryImpl))
      }
      logger.entering(_CLASS, "setNewJournalEntry(JournalEntry)");
   }
}