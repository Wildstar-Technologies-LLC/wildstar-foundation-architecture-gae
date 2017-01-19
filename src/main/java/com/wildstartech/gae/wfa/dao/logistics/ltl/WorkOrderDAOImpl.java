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

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.journal.JournalDAOImpl;
import com.wildstartech.gae.wfa.dao.journal.PersistentJournalEntryImpl;
import com.wildstartech.gae.wfa.dao.ticketing.BasicTicketDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentWorkOrder;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentWorkOrderLineItem;
import com.wildstartech.wfa.dao.logistics.ltl.WorkOrderDAO;
import com.wildstartech.wfa.dao.logistics.ltl.WorkOrderLineItemDAO;
import com.wildstartech.wfa.dao.logistics.ltl.WorkOrderLineItemDAOFactory;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.WorkOrder;
import com.wildstartech.wfa.logistics.ltl.WorkOrderLineItem;

public class WorkOrderDAOImpl
extends BasicTicketDAOImpl<WorkOrder, PersistentWorkOrder>
implements WorkOrderDAO {
   private static final String _CLASS=WorkOrderDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   /**
    * Default, no-argument constructor.
    */
   public WorkOrderDAOImpl() {
      super();
      logger.entering(_CLASS, "WorkOrderDAOImpl()");
      logger.exiting(_CLASS, "WorkOrderDAOImpl()");
   }
   
   @Override
   public PersistentWorkOrder create() {
      logger.entering(_CLASS, "create()");
      PersistentWorkOrder pWorkOrder=null;
      pWorkOrder=new PersistentWorkOrderImpl();
      logger.exiting(_CLASS, "create()",pWorkOrder);
      return pWorkOrder;
   }

   @Override
   public PersistentWorkOrder create(WorkOrder workOrder, UserContext ctx) {
      logger.entering(_CLASS, "create(WorkOrder,UserContext)",
            new Object[] {workOrder,ctx});
      PersistentWorkOrderImpl pWorkOrder=null;
      
      pWorkOrder=new PersistentWorkOrderImpl();
      pWorkOrder.populateFromObject(workOrder);
      
      logger.exiting(_CLASS, "create(WorkOrder,UserContext)",pWorkOrder);
      return pWorkOrder;
   }
   
   @Override
   public List<PersistentWorkOrder> findActionable(UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findActionable(UserContext)",ctx);
      Filter filter=null;
      Filter userFilter=null;
      List<Filter> filters=null;
      List<PersistentWorkOrder> workOrders=null;
      Query query=null;
      QueryWrapper qw=null;
      String currentUser=null;
      
      workOrders=new ArrayList<PersistentWorkOrder>();
      query=new Query(PersistentWorkOrderImpl._KIND);
      // Let's build the list of composite queries.
      filters=new ArrayList<Filter>();
      filters.add(
            new FilterPredicate("statusState",FilterOperator.EQUAL,"New"));
      filters.add(
            new FilterPredicate("statusState",FilterOperator.EQUAL,"Assigned"));
      filters.add(
            new FilterPredicate("statusState",FilterOperator.EQUAL,"Accepted"));
      filter=new Query.CompositeFilter(
            Query.CompositeFilterOperator.OR,
            filters);
      
      /* ***** BEGIN: User Filtering */
      currentUser = ctx.getUserName();
      if (
            (currentUser != null) && 
            (!currentUser.equalsIgnoreCase("transit.systems@justodelivery.com")) &&
            (currentUser.endsWith("justodelivery.com"))
         ) {
         // No-Op
         // This is a Justo Employee, so ALL records are welcome.
      } else {
         filters = new ArrayList<Filter>();
         filters.add(new FilterPredicate("createdBy",
               FilterOperator.EQUAL, currentUser));
         filters.add(new FilterPredicate("contactEmail",
               FilterOperator.EQUAL, currentUser));
         userFilter = new Query.CompositeFilter(
               Query.CompositeFilterOperator.OR, filters);
         filters=new ArrayList<Filter>();
         filters.add(filter);
         filters.add(userFilter);
         filter=new Query.CompositeFilter(
               Query.CompositeFilterOperator.AND,
               filters);
      } // END if ((currentUser != null) && ...
      /* ***** END: User Filtering */
      
      query.setFilter(filter);
      qw=new QueryWrapper(query);
      workOrders=findByQuery(qw,ctx);
      
      logger.entering(_CLASS, "findActionable(UserContext)",
            workOrders);
      return workOrders;
   }
   
   /**
    * 
    */
   @Override
   public List<PersistentWorkOrder> findAll(UserContext ctx) {
      logger.entering(_CLASS, "findAll(UserContext)",ctx);
      Query query = null;
      Filter userFilter=null;
      List<Filter> filters=null;
      List<PersistentWorkOrder> results=null;
      QueryWrapper qw = null;
      String currentUser = null;
      String kind = null;
      String msg = null;
      StringBuilder sb = null;

      if (
            (ctx != null) && 
            (ctx.isAuthenticated())
         ) {
         kind = getKind();
         query = new Query(kind);

         /* ***** BEGIN: User Filtering */
         currentUser = ctx.getUserName();
         if (
               (currentUser != null) && 
               (!currentUser.equalsIgnoreCase("transit.systems@justodelivery.com")) &&
               (currentUser.endsWith("justodelivery.com"))
            ) {
            // No-Op
            // This is a Justo Employee, so ALL records are welcome.
         } else {
            filters = new ArrayList<Filter>();
            filters.add(new FilterPredicate("createdBy",
                  FilterOperator.EQUAL, currentUser));
            filters.add(new FilterPredicate("contactEmail",
                  FilterOperator.EQUAL, currentUser));
            userFilter = new Query.CompositeFilter(
                  Query.CompositeFilterOperator.OR, filters);
         } // END if ((currentUser != null) && ...
         if (userFilter != null) {
            query.setFilter(userFilter);
         } // END if (userFilter != null)
         /* ***** END: User Filtering */

         qw = new QueryWrapper(query);
         results = findByQuery(qw, ctx);
      } else {
         // The specified context was either null or has not been authenticated.
         if (ctx == null) {
            msg = ("The UserContext parameter was null.");
         } else if (!ctx.isAuthenticated()) {
            sb = new StringBuilder(80);
            sb.append("The specified UserContext, ").append(ctx.getUserName());
            sb.append(", is not authenticated.");
            msg = sb.toString();
         } // END if (ctx == null)
         logger.fine(msg);

      } // END if ((ctx != null) && (ctx.isAuthenticated()))
      
      logger.exiting(_CLASS, "findAll(UserContext)",results);
      return results;
   }
   /**
    * Returns a list of <code>PersistentWorkOrder</code> objects that are have a
    * value of neither "Resolved" nor "Closed" in the <code>statusState</code> 
    * field.
    *   
    */
   @Override
   public List<PersistentWorkOrder> findAllOpen(UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findAllOpen(UserContext)",ctx);
      Filter filter=null;
      Filter userFilter=null;
      List<Filter> filters=null;
      List<PersistentWorkOrder> workOrders=null;
      Query query=null;
      QueryWrapper qw=null;
      String currentUser=null;      
      
      workOrders=new ArrayList<PersistentWorkOrder>();
      query=new Query(PersistentWorkOrderImpl._KIND);
      // Let's build the list of composite queries.
      filters=new ArrayList<Filter>();
      filters.add(
            new FilterPredicate("statusState",FilterOperator.EQUAL,"New"));
      filters.add(
            new FilterPredicate("statusState",FilterOperator.EQUAL,"Pending"));
      filters.add(
            new FilterPredicate("statusState",FilterOperator.EQUAL,"Scheduled"));
      filters.add(
            new FilterPredicate("statusState",FilterOperator.EQUAL,"Unscheduled"));
      
      filter=new Query.CompositeFilter(
            Query.CompositeFilterOperator.OR,
            filters);
      
      /* ***** BEGIN: User Filtering */
      currentUser = ctx.getUserName();
      if (
            (currentUser != null) && 
            (!currentUser.equalsIgnoreCase("transit.systems@justodelivery.com")) &&
            (currentUser.endsWith("justodelivery.com"))
         ) {
         // No-Op
         // This is a Justo Employee, so ALL records are welcome.
      } else {
         filters = new ArrayList<Filter>();
         filters.add(new FilterPredicate("createdBy",
               FilterOperator.EQUAL, currentUser));
         filters.add(new FilterPredicate("contactEmail",
               FilterOperator.EQUAL, currentUser));
         userFilter = new Query.CompositeFilter(
               Query.CompositeFilterOperator.OR, filters);
         filters=new ArrayList<Filter>();
         filters.add(filter);
         filters.add(userFilter);
         filter=new Query.CompositeFilter(
               Query.CompositeFilterOperator.AND,
               filters);
      } // END if ((currentUser != null) && ...
      /* ***** END: User Filtering */
      
      query.setFilter(filter);
      qw=new QueryWrapper(query);
      workOrders=findByQuery(qw,ctx);
      
      logger.exiting(_CLASS, "findAllOpen(UserContext)",workOrders);
      return workOrders;
   }
   
   /**
    * 
    */
   @Override
   public List<PersistentWorkOrder> findByCustomerOrderId(
         String customerOrderId, UserContext ctx) throws DAOException {
      // TODO Auto-generated method stub
      return null;
   }
   
   /**
    * Returns the work order represented by the specified work order ID.
    * 
    */
   @Override
   public PersistentWorkOrder findByWorkOrderId(String workOrderId, UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findByWorkOrderId(String,UserContext)",
            new Object[] {workOrderId,ctx});
      PersistentWorkOrder pWorkOrder=null;
      
      if ((workOrderId != null) && (ctx != null)) {
         pWorkOrder=findByRequestId(workOrderId,ctx);
      } else {
         // One or both of the parameters to this method were null.
         if ((workOrderId == null) && (ctx == null)) {
            logger.warning(
                  "The Work Order ID and User Context parameters are null.");
         } else if (workOrderId == null) {
            logger.warning("The workOrderId parameter is null.");
         } else {
            logger.warning("The User Context parameter is null.");
         } // END if (workOrderId == null)
      } // END if ((workOrderId != null) && (ctx != null))
      logger.exiting(_CLASS, "findByWorkOrderId(String,UserContext)",
            pWorkOrder);
      return pWorkOrder;
   }

   @Override
   public PersistentWorkOrder findInstance(WorkOrder workOrder, UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findInstance(WorkOrder,UserContext)",
            new Object[] {workOrder,ctx});
      PersistentWorkOrder pWorkOrder=null;
      
      logger.exiting(_CLASS, "findInstance(WorkOrder,UserContext)",pWorkOrder);
      return pWorkOrder;
   }   

   public List<PersistentWorkOrder> findByTypeAndStatus(
         String type,
         String statusState,
         String statusReason, 
         UserContext ctx) {
      logger.entering(_CLASS, 
            "findByTypeAndStatus(String,String,String,UserContext)",
            new Object[] {type,statusState,statusReason,ctx});
      boolean filterPresent=false;
      Filter filter=null;
      Filter userFilter=null;
      List<Filter> filters=null;
      List<PersistentWorkOrder> workOrders=null;
      Query query=null;
      QueryWrapper qw=null;
      String currentUser=null;
      String kind="";
      
      if ((ctx != null) && (ctx.isAuthenticated())) {
         // So the user context has been specified
         kind = getKind();
         query = new Query(kind);
         
         filters=new ArrayList<Filter>();
         if ((type != null) && (!type.isEmpty())) {
            filter=new FilterPredicate("type",FilterOperator.EQUAL,type);
            filters.add(filter);
            if (!filterPresent) filterPresent=true;
         } // END if ((type != null) && (!type.isEmpty()))
         if ((statusState != null) && (!statusState.isEmpty())) {
            filter=new FilterPredicate(
               "statusState",FilterOperator.EQUAL,statusState);
            filters.add(filter);
            if (!filterPresent) filterPresent=true;
         } // END if ((statusState != null) && (!statusState.isEmpty()))
         if ((statusReason !=null) && (!statusReason.isEmpty())) {
            filter=new FilterPredicate(
               "statusReason",FilterOperator.EQUAL,statusReason);
            filters.add(filter);
            if (!filterPresent) filterPresent=true;
         } // END if ((statusReason !=null) && (!statusReason.isEmpty()))
         
         if (filterPresent) {
            if (filters.size() > 1) {
               filter=new Query.CompositeFilter(
                     Query.CompositeFilterOperator.AND,
                     filters);
            } else {
               filter=filters.get(0);
            } // END if (filters.size() > 1) 
            
            /* ***** BEGIN: User Filtering */
            currentUser = ctx.getUserName();
            if (
                  (currentUser != null) && 
                  (!currentUser.equalsIgnoreCase("transit.systems@justodelivery.com")) &&
                  (currentUser.endsWith("justodelivery.com"))
               ) {
               // No-Op
               // This is a Justo Employee, so ALL records are welcome.
            } else {
               filters = new ArrayList<Filter>();
               filters.add(new FilterPredicate("createdBy",
                     FilterOperator.EQUAL, currentUser));
               filters.add(new FilterPredicate("contactEmail",
                     FilterOperator.EQUAL, currentUser));
               userFilter = new Query.CompositeFilter(
                     Query.CompositeFilterOperator.OR, filters);
               filters=new ArrayList<Filter>();
               filters.add(filter);
               filters.add(userFilter);
               filter=new Query.CompositeFilter(
                     Query.CompositeFilterOperator.AND,
                     filters);
            } // END if ((currentUser != null) && ...
            /* ***** END: User Filtering */
            
            query.setFilter(filter);
            qw=new QueryWrapper(query);
            workOrders=findByQuery(qw,ctx);            
         } else {            
            logger.severe("No filtering criteria specified.");
         } // END if (filterPresent)         
      } else {
         logger.severe("UserContext is null or NOT AUTHENTICATED");
      } // END ((ctx != null) && (ctx.isAuthenticated()))
      
      logger.exiting(_CLASS, 
            "findByTypeAndStatus(String,String,String,UserContext)",
            workOrders);
      return workOrders;
   }
   /**
    * Returns the <em>Kind</em> property of the entity which is used for the 
    * purpose of querying the Datastore.
    * 
    * @return A string value which is used by the Datastore for the purpose of
    * categorizing entities of this object's type to provide the ability to 
    * querying the Datastore and retrieve entities. 
    */
   protected String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentWorkOrderImpl._KIND);
      return PersistentWorkOrderImpl._KIND;
   }
   
   /**
    * Save method responsible for saving the work order.
    */
   public PersistentWorkOrder save(WorkOrder workOrder, UserContext ctx) {
      logger.entering(_CLASS, "save(WorkOrder,UserContext)",
            new Object[] {workOrder,ctx});
      PersistentWorkOrder pWorkOrder=null;
      
      pWorkOrder=save(workOrder,ctx,null);
      
      logger.exiting(_CLASS, "save(WorkOrder,UserContext)",pWorkOrder);
      return pWorkOrder;
   }
   
   /**
    * Save method responsible for persisting the Quote entity.  
    * 
    * <p>Overriding the WildDAOImpl superclass method because we want to 
    * intercept that method as there are children that need to be saved.</p>
    */
   @Override
   public PersistentWorkOrderImpl save(
         WorkOrder workOrder, 
         UserContext ctx, 
         Transaction txn) {
     logger.entering(_CLASS,"save(WorkOrder,UserContext,Transaction)",
         new Object[] {workOrder,ctx,txn});
     JournalDAOImpl journalDAO=null;
     List<WorkOrderLineItem> lineItems=null;
     List<WorkOrderLineItem> lineItemsToDelete=null;
     PersistentJournalEntryImpl pJournalEntry=null;
     PersistentWorkOrderImpl pWO=null;
     PersistentWorkOrderLineItemImpl pWli=null;
     String identifier="";
     String workOrderIdentifier="";
     String requestId="";
     WorkOrderLineItemDAO wliDAO=null;
     WorkOrderLineItemDAOFactory wliFactory=null;
     WorkOrderReconcileCreditCard reconcileCreditCard=null;
     WorkOrderRequestIdGenerator requestIdGenerator=null;
     
     if ((workOrder != null) && (ctx !=null)) {
       // Get the workOrder identifier
       requestId=workOrder.getRequestId();
       // Get a reference to the current/saved version of the work order.
       if (!isEmpty(requestId)) {
          // The requestId exist, so let's get the current version of the 
          // work order.
          pWO=(PersistentWorkOrderImpl) findByRequestId(requestId,ctx);
       } else {
          // There is no requestId, so let's generate one.
          requestIdGenerator=new WorkOrderRequestIdGenerator();
          requestId=requestIdGenerator.getNextId();          
       } // END if (!isEmpty(requestId))
       if (pWO == null) {
          pWO=(PersistentWorkOrderImpl) create();
       } // END  if (pWO == null) 
       pWO.updateFromObject(workOrder);
       pWO.setRequestId(requestId);
       
       // ******************** Process Rules
       reconcileCreditCard=new WorkOrderReconcileCreditCard(ctx);
       reconcileCreditCard.apply(pWO);
       
       /* Get the list of line items that will need to be saved. */
       lineItems=workOrder.getLineItems();
       /* Get the list of line items that will need to be removed. */
       lineItemsToDelete=
             ((PersistentWorkOrderImpl) workOrder).getLineItemsToDelete();
       /* Get the journal entry. */
       pJournalEntry=(PersistentJournalEntryImpl) pWO.getNewJournalEntry();
       
       pWO=(PersistentWorkOrderImpl) super.save(pWO,ctx, txn);
       
       // ******************** Journal Entry ********************
       if (!pJournalEntry.isEmpty()) {
          // The journal contains data, so let's save it.
          journalDAO=new JournalDAOImpl();
          pJournalEntry.setRelatedKind(getKind());
          pJournalEntry.setRelatedIdentifier(pWO.getIdentifier());
          pJournalEntry=(PersistentJournalEntryImpl)
                journalDAO.save(pJournalEntry, ctx);
          // Clear out the new journal entry.
          pWO.setNewJournalEntry(null);
       } // END if (!pJournalEntry.isEmpty())
       
       // ******************** Line Items ********************
       workOrderIdentifier=pWO.getIdentifier(); 
       wliFactory=new WorkOrderLineItemDAOFactory();
       wliDAO=wliFactory.getDAO();
       //***** Iterate through the WorkOrderLineItems and save them.
       for (int i=0; i < lineItems.size(); i++) {
         WorkOrderLineItem wli=lineItems.get(i);
         if (!(wli instanceof PersistentWorkOrderLineItemImpl)) {
           // The Quote Line Item IS NOT a persistent entity.
           pWli=(PersistentWorkOrderLineItemImpl) wliDAO.findInstance(wli, ctx);
         } else {
           identifier=((PersistentWorkOrderLineItemImpl) wli).getIdentifier();
           if (!isEmpty(identifier)) {
             pWli=(PersistentWorkOrderLineItemImpl) wliDAO.findByIdentifier(
               identifier, ctx);            
           } else {
             logger.warning(
                 "The entity is a persistent one, but it hasn't been saved.");
           } // END if (!isEmpty(identifier))
         } // END if (!(qli instanceof PersistentWorkOrderLineItemImpl))
         if (pWli == null) {
           // An existing line item WAS NOT found.
           pWli=(PersistentWorkOrderLineItemImpl) wliDAO.create();
         } // END if (pQli == null) 
         // Popluate with line item data
         // Populate the object with information from the passsed object.
         pWli.populateFromObject(wli);
         // Associate the QuoteLineItem with the quote.
         pWli.setWorkOrderIdentifier(workOrderIdentifier);
         // Save the QuoteLineItem
         pWli=(PersistentWorkOrderLineItemImpl) 
             ((WorkOrderLineItemDAOImpl) wliDAO).save(pWli,ctx,txn);
         // Add the saved line item to the persistent quote
         pWO.addLineItem(pWli);
         wli=null;
         pWli=null;        
       } // END for (WorkOrderLineItem qli: lineItems)
       // Remove WorkOrderLineItems No longer associated.
       for (WorkOrderLineItem item: lineItemsToDelete) {
         // Get the identifier.
          if (item instanceof PersistentWorkOrderLineItem) {
             identifier=((PersistentWorkOrderLineItem) item).getIdentifier();
             // Remove the object.
             wliDAO.deleteByIdentifier(identifier, ctx);
          } // END if (item instanceof PersistentWorkOrderLineItem)         
       } // END  for (WorkOrderLineItem item: lineItemsToDelete)
     } else {
       // Either the Quote object was null or the UserContext object was null.
       if (workOrder == null) logger.warning("The Work Order object was null.");
       if (ctx==null) logger.warning("The UserContext object was null.");
     } // END if ((workOrder != null) && (ctx !=null))
     
     logger.entering(_CLASS,"save(WorkOrder,UserContext,Transaction)",pWO);
     return pWO;
   }   
}