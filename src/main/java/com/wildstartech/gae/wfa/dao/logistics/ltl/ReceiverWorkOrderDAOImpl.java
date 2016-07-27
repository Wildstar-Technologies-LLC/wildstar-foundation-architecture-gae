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
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Transaction;
import com.wildstartech.gae.wfa.dao.journal.JournalDAOImpl;
import com.wildstartech.gae.wfa.dao.journal.PersistentJournalEntryImpl;
import com.wildstartech.gae.wfa.dao.ticketing.BasicTicketDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentReceiverWorkOrder;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentReceiverWorkOrderLineItem;
import com.wildstartech.wfa.dao.logistics.ltl.ReceiverWorkOrderDAO;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrder;
import com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrderLineItem;

public class ReceiverWorkOrderDAOImpl
extends BasicTicketDAOImpl<ReceiverWorkOrder, PersistentReceiverWorkOrder>
implements ReceiverWorkOrderDAO {
   private static String _CLASS=ReceiverWorkOrderDAOImpl.class.getName();
   private static Logger logger=Logger.getLogger(_CLASS);
   
   /**
    * Default, no-argument constructor.
    */
   public ReceiverWorkOrderDAOImpl() {
	   logger.entering(_CLASS,"ReceiverWorkOrderDAOImpl()");
	   logger.exiting(_CLASS, "ReceiverWorkOrderDAOImpl()");
   }
   
   /**
    * Retrieve the persistent version of the object that is passed as a 
    * parameter.
    */
   @Override
   public PersistentReceiverWorkOrder findInstance(ReceiverWorkOrder workOrder,
         UserContext ctx) throws DAOException {
      logger.entering(_CLASS, "findInstance(ReceiverWorkOrder,UserContext)",
            new Object[] {workOrder,ctx});
      PersistentReceiverWorkOrder pWO=null;
      
      logger.entering(_CLASS, "findInstance(ReceiverWorkOrder,UserContext)",
            pWO);
      return pWO;
   }
   
   /**
    * Creates a new, default instance of the {@code PersitentReceiverWorkOrder}.
    */
   @Override
   public PersistentReceiverWorkOrder create() {
      logger.entering(_CLASS, "create()");
      PersistentReceiverWorkOrder workOrder=null;
      workOrder=new PersistentReceiverWorkOrderImpl();
      logger.exiting(_CLASS, "create()",workOrder);
      return workOrder;
   }
   
   /**
    * Creates a new instance of the {@code PersistentReceiverWorkOrder} using 
    * the specified object as a reference.
    */
   @Override
   public PersistentReceiverWorkOrder create(ReceiverWorkOrder workOrder,
         UserContext ctx) {
      logger.entering(_CLASS, "create(ReceiverWorkOrder,UserContext)",
            new Object[] {workOrder,ctx});
      PersistentReceiverWorkOrderImpl pWO=null;
      
      pWO=new PersistentReceiverWorkOrderImpl();
      pWO.populateFromObject(workOrder);
      
      logger.exiting(_CLASS, "create(ReceiverWorkOrder,UserContext)",pWO);
      return pWO;
   }
   
   
   /**
    * Returns the type of object for use in persisting to the data store.
    */
   @Override
   protected String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentReceiverWorkOrderImpl._KIND);
      return PersistentReceiverWorkOrderImpl._KIND;
   }

   /**
    * Returns a list of objects that are waiting some type of action.
    */
   @Override
   public List<PersistentReceiverWorkOrder> findActionable(UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findActionable(UserContext)");
      List<PersistentReceiverWorkOrder> workOrders=null;
      
      if (workOrders == null) {
         workOrders=new ArrayList<PersistentReceiverWorkOrder>();
      } // END if (workOrders == null)       
      logger.exiting(_CLASS, "findActionable(UserContext)",workOrders);
      return workOrders;
   }

   @Override
   public List<PersistentReceiverWorkOrder> findAllOpen(UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findAllOpen(UserContext)");
      List<PersistentReceiverWorkOrder> workOrders=null;
      
      if (workOrders == null) {
         workOrders=new ArrayList<PersistentReceiverWorkOrder>();
      } // END if (workOrders == null)       
      logger.exiting(_CLASS, "findAllOpen(UserContext)",workOrders);
      return workOrders;
   }
   
   public PersistentReceiverWorkOrder theSave(
		   ReceiverWorkOrder receiver, 
		   UserContext ctx) {
	   PersistentReceiverWorkOrder pReceiver = null;
	   
	   pReceiver=save(receiver,ctx,null);
	   
	   return pReceiver;
   }
   /**
    * Save method responsible for persisting the Quote entity.
    * 
    * <p>
    * Overriding the WildDAOImpl superclass method because we want to intercept
    * that method as there are children that need to be saved.
    * </p>
    */
   @Override
   public PersistentReceiverWorkOrder save(
		   ReceiverWorkOrder receiver, 
		   UserContext ctx,
	       Transaction txn) {
	  logger.entering(_CLASS, "save(Quote,UserContext,Transaction)", 
			  new Object[] {receiver,ctx,txn});
	  JournalDAOImpl journalDAO=null;
      List<ReceiverWorkOrderLineItem> lineItems = null;
      List<ReceiverWorkOrderLineItem> lineItemsToDelete = null;
      PersistentJournalEntryImpl pJournalEntry=null;
      PersistentReceiverWorkOrderImpl pReceiver = null;
      PersistentReceiverWorkOrderLineItemImpl pRli = null;
      ReceiverWorkOrderLineItem rli=null;
      ReceiverWorkOrderLineItemDAOImpl rliDAO = null; 
      ReceiverWorkOrderRequestIdGenerator requestIdGenerator=null;
      String identifier = "";
      String workOrderIdentifier = "";
      String requestId = "";      

      if ((receiver != null) && (ctx != null)) {
         // Get the receiver requestId
         requestId = receiver.getRequestId();
         // Get a reference to the current, saved version of the quote.
         if (!isEmpty(requestId)) {
            // Yes there is a request Id, let's use it to get the current
            // version of the object.
            pReceiver=(PersistentReceiverWorkOrderImpl) 
            		findByRequestId(requestId,ctx);
         } else {
            // No, there is no requestId, so let's generate one....
        	requestIdGenerator=new ReceiverWorkOrderRequestIdGenerator();
            requestId=requestIdGenerator.getNextId();            
         } // END if (!isEmpty(requestId))
         // Let's check to see if the persistent quote was found...
         if (pReceiver == null) {
            // Nope, so let's create a new persistent object.
            pReceiver=(PersistentReceiverWorkOrderImpl) create();
         } // END if (pReceiver == null)
         // So let's update the persistent quote from the specified quote.
         pReceiver.updateFromObject(receiver);
         pReceiver.setRequestId(requestId);
         // ******************** Process Rules
         
         /* Get the list of line items that will need to be saved */
         lineItems = pReceiver.getLineItems();
         /* Get the list of line items that will need to be removed. */
         lineItemsToDelete = pReceiver.getLineItemsToDelete(); 
         /* Get the journal entry. */
         pJournalEntry=(PersistentJournalEntryImpl) 
        		 pReceiver.getNewJournalEntry();
         
         /* Saving the quote will return ONLY the parent object.  The children
          * will not be present in the object that is returned. */
         pReceiver = (PersistentReceiverWorkOrderImpl) 
        		 super.save(pReceiver, ctx, null);
         
         // ******************** Journal Entry ********************
         if (!pJournalEntry.isEmpty()) {
            // The journal contains data, so let's save it.
            journalDAO=new JournalDAOImpl();
            pJournalEntry.setRelatedKind(getKind());
            pJournalEntry.setRelatedIdentifier(pReceiver.getIdentifier());
            pJournalEntry=(PersistentJournalEntryImpl)
                  journalDAO.save(pJournalEntry, ctx);
            pReceiver.setNewJournalEntry(null);
         } // END if (!pJournalEntry.isEmpty())
         // Reset the new journal information.         
         // ******************** Line Items ********************
         /* workOrderIdentifier will be saved with the ReceiverWorkOrderLineItem
          * instances */
         workOrderIdentifier = pReceiver.getIdentifier();
         rliDAO = new ReceiverWorkOrderLineItemDAOImpl();
         // ***** Iterate through the ReceiverWorkOrderLineItems and save them.
         for (int i = 0; i < lineItems.size(); i++) {
            rli = lineItems.get(i);
            if (!(rli instanceof PersistentReceiverWorkOrderLineItemImpl)) {
               // The Receiver Work Order LineItem IS NOT a persistent entity.
               pRli = (PersistentReceiverWorkOrderLineItemImpl) 
            		   rliDAO.findInstance(rli,ctx);
            } else {
               identifier = ((PersistentReceiverWorkOrderLineItemImpl) 
            		   rli).getIdentifier();
               if (!isEmpty(identifier)) {
                  pRli = (PersistentReceiverWorkOrderLineItemImpl) rliDAO
                        .findByIdentifier(identifier, ctx);
               } else {
                  logger.info(
                   "The entity is a persistent one, but it hasn't been saved.");
               } // END if (identifier != null)
            } // END if (!(qli instanceof PersistentReceiverWorkOrderLineItem...
            if (pRli == null) {
               // An existing line item WAS NOT found.
               pRli = (PersistentReceiverWorkOrderLineItemImpl) rliDAO.create();
            } // END if (pQli == null)
            // Popluate with line item data
            // Populate the object with information from the passsed object.
            pRli.populateFromObject(rli);
            // Associate the ReceiverWorkOrderLineItem with the work order.
            pRli.setWorkOrderIdentifier(workOrderIdentifier);
            // Save the ReceiverWorkOrderLineItem
            pRli = (PersistentReceiverWorkOrderLineItemImpl) 
            		((ReceiverWorkOrderLineItemDAOImpl) 
            				rliDAO).save(pRli, ctx, null);
            // Add the saved line item to the persistent quote
            pReceiver.addLineItem(pRli);
            rli = null;
            pRli = null;
         } // END for (ReceiverWorkOrderLineItem rli: lineItems)
         
         // Remove ReceiverWorkOrderLineItems No longer associated.
         for (ReceiverWorkOrderLineItem item : lineItemsToDelete) {
            // Get the identifier.
            identifier = ((PersistentReceiverWorkOrderLineItem)
            		item).getIdentifier();
            // Remove the object.
            rliDAO.deleteByIdentifier(identifier, ctx);
         } // END for (PersistentReceiverWorkOrderLineItem item:
      } else {
         // Either the Quote object was null or the UserContext object was null.
         if (receiver == null)
            logger.warning("The ReceiverWorkOrder object was null.");
         if (ctx == null)
            logger.warning("The UserContext object was null.");
      } // END if ((quote != null) && (ctx !=null))

      logger.exiting(_CLASS, "save(Quote,UserContext,Transaction)", pReceiver);
      return pReceiver;	   
   }
}