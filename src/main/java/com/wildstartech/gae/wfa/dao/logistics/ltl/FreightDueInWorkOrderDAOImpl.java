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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.ticketing.BasicTicketDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.logistics.ltl.FreightDueInWorkOrderDAO;
import com.wildstartech.wfa.dao.logistics.ltl.FreightDueInWorkOrderLineItemDAO;
import com.wildstartech.wfa.dao.logistics.ltl.FreightDueInWorkOrderLineItemDAOFactory;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentFreightDueInWorkOrder;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentFreightDueInWorkOrderLineItem;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentQuote;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.FreightDueInWorkOrder;
import com.wildstartech.wfa.logistics.ltl.FreightDueInWorkOrderLineItem;

public class FreightDueInWorkOrderDAOImpl 
extends BasicTicketDAOImpl<FreightDueInWorkOrder, 
                           PersistentFreightDueInWorkOrder> 
implements FreightDueInWorkOrderDAO {
   private static final String _CLASS=
         FreightDueInWorkOrderDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   public FreightDueInWorkOrderDAOImpl() {
      super();
      logger.entering(_CLASS, "FreightDueInWorkOrderDAOImpl()");
      logger.exiting(_CLASS, "FreightDueInWorkOrderDAOImpl()");
   }
   //***** Utility methods
   @Override
   public PersistentFreightDueInWorkOrder findInstance(
         FreightDueInWorkOrder object, UserContext ctx) throws DAOException {
      logger.entering(_CLASS, 
            "findInstance(FreightDueInWorkOrder, UserContext",
            new Object[] {object,ctx});
      PersistentFreightDueInWorkOrder foundWorkOrder=null;
      logger.entering(_CLASS, 
            "findInstance(FreightDueInWorkOrder, UserContext",
            foundWorkOrder);
      return foundWorkOrder;
   }

   @Override
   public PersistentFreightDueInWorkOrder create() {
      logger.entering(_CLASS, "create()");
      PersistentFreightDueInWorkOrder newWorkOrder=null;
      newWorkOrder=new PersistentFreightDueInWorkOrderImpl();
      logger.exiting(_CLASS, "create()",newWorkOrder);
      return newWorkOrder;
   }

   /**
    * Create a new work order using the specified object as a reference.
    */
   @Override
   public PersistentFreightDueInWorkOrder create(
         FreightDueInWorkOrder workOrder,
         UserContext ctx) {
      logger.entering(_CLASS, "create(FreightDueInWorkOrder,UserContext",
            new Object[] {workOrder,ctx});
      PersistentFreightDueInWorkOrderImpl newWorkOrder=null;
      
      newWorkOrder=new PersistentFreightDueInWorkOrderImpl();
      logger.exiting(_CLASS,"create(FreightDueInWorkOrder,UserContext)",
            newWorkOrder);
      return newWorkOrder;
   }
   
   /**
    * Returns the <em>Kind</em> property of the entity which is used for the 
    * purpose of querying the Datastore.
    * 
    * @return A string value which is used by the Datastore for the purpose of
    * categorizing entities of this object's type to provide the ability to 
    * querying the Datastore and retrieve entities. 
    */
   protected final String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",
            PersistentFreightDueInWorkOrderImpl._KIND);
      return PersistentFreightDueInWorkOrderImpl._KIND;
   }
   
   public PersistentFreightDueInWorkOrder save(
         FreightDueInWorkOrder workOrder, UserContext ctx) {
      logger.entering(_CLASS, "save(FreightDueInWorkOrder,UserContext)");
      FreightDueInWorkOrderLineItem lineItem=null;
      FreightDueInWorkOrderLineItemDAO lineItemDAO=null;
      FreightDueInWorkOrderLineItemDAOFactory lineItemDAOFactory=null;
      FreightDueInWorkOrderRequestIdGenerator requestIdGenerator=null;
      List<FreightDueInWorkOrderLineItem> lineItems=null;
      List<FreightDueInWorkOrderLineItem> lineItemsToDelete=null;
      PersistentFreightDueInWorkOrderImpl pWorkOrder=null;
      PersistentFreightDueInWorkOrderLineItemImpl pLineItem=null;
      String lineItemIdentifier=null;
      String requestId=null;
      String workOrderIdentifier=null;
      
      if ((workOrder != null) && (ctx != null)) {        
         // Get the work Order Request ID
         requestId=workOrder.getRequestId();
         if (!isEmpty(requestId)) {
            /* Yes there is a requestId, let's use it to get the current 
             * version of the object. */
            pWorkOrder=(PersistentFreightDueInWorkOrderImpl) 
                  findByRequestId(requestId,ctx);
         } else {
            // No, there is no requestId, so let's generate one.
            requestIdGenerator=new FreightDueInWorkOrderRequestIdGenerator();
            requestId=requestIdGenerator.getNextId();
         } // END if (!isEmpty(requestId))
         // Let's check to see if the persistent work order was found.
         if (pWorkOrder == null) {
            // Nope, so let's create a new persistent object.
            pWorkOrder=(PersistentFreightDueInWorkOrderImpl) create();
         } // END if (pWorkOrder == null)
         // Let's update the persistent work order from the specified work order
         pWorkOrder.updateFromObject(workOrder);
         pWorkOrder.setRequestId(requestId);
         //***** Process Rules
         
         // Get the list of line items that will need to be saved.
         lineItems=pWorkOrder.getLineItems();
         // Get the list of line items that will need to be removed.
         lineItemsToDelete=pWorkOrder.getLineItemsToDelete();
         /* Get the journal Entry */
         // TODO
         
         /* Saving the object will ONLY return the parent object.  The children 
          * will not be present in the object that is returned.
          */
         pWorkOrder=(PersistentFreightDueInWorkOrderImpl) 
               super.save(pWorkOrder, ctx, null);
         
         // ******************** Journal Entry ********************
         // ******************** Line Items ********************
         // The workOrderIdentifier will be saved with the line items. 
         workOrderIdentifier=pWorkOrder.getIdentifier();
         lineItemDAO=new FreightDueInWorkOrderLineItemDAOImpl();
         for (int i=0; i < lineItems.size(); i++) {
            lineItem=lineItems.get(i);
            if (!(lineItem instanceof PersistentFreightDueInWorkOrderLineItem)){
               // The work order line item is not a persistent entity.
               pLineItem=(PersistentFreightDueInWorkOrderLineItemImpl)
                     lineItemDAO.findInstance(lineItem, ctx);
            } else {
               logger.info(
                  "The entity is a persistent one, but it has not been saved.");
            } // END if (!(lineItem instanceof PersistentFreightDueInWork...
            if (pLineItem == null) {
               // An existing line item was not found.
               pLineItem=(PersistentFreightDueInWorkOrderLineItemImpl)
                     lineItemDAO.create();
            } // END if (pLineItem == null)
            // Populate the line item with data.
            pLineItem.populateFromObject(lineItem);
            // Associate the FreightDueInLineItem with the work order.
            pLineItem.setWorkOrderIdentifier(workOrderIdentifier);
            // Save the line item
            pLineItem=(PersistentFreightDueInWorkOrderLineItemImpl)
                  lineItemDAO.save(pLineItem, ctx);
            // Add the saved line item back to the work order
            pWorkOrder.addLineItem(pLineItem);
            lineItem=null;
            pLineItem=null;            
         } // END for (int i=0; i < lineItems.size(); i++)
         // Remove FreightDueInWorkOrderLineItems no longer associated.
         for (FreightDueInWorkOrderLineItem item: lineItemsToDelete) {
            if (item instanceof PersistentFreightDueInWorkOrderLineItem) {
               pLineItem=(PersistentFreightDueInWorkOrderLineItemImpl) item;
               lineItemIdentifier=pLineItem.getIdentifier();
               lineItemDAO.deleteByIdentifier(lineItemIdentifier, ctx);
            } // END if (item instanceof PersistentFreightDueInWorkOrderLine...            
         } // END for (FreightDueInWorkOrderLineItem item: lineItemsToDelete)
      } else {
          if (workOrder == null) {
             logger.severe("The specified workOrder was null.");
          } // END if (workOrder == null)
          if (ctx == null) {
             logger.severe("The specified UserContext was null.");
          } // END if (ctx == null)
      } // END if ((workOrder != null) && (ctx != null))
      
      logger.exiting(_CLASS, "save(FreightDueInWorkOrder,UserContext)",
            pWorkOrder);
      return pWorkOrder;
   }
   
   /**
    * Finds a list of all quotes in the system.
    * 
    * <p>
    * This method was deployed in an effort to provide the ability to filter out
    * quotes for specific users.
    * </p>
    */
   @Override
   public final List<PersistentFreightDueInWorkOrder> findAll(
         UserContext ctx) {
      logger.entering(_CLASS, "findAll(UserContext)", ctx);
      Query query = null;
      Filter filter = null;
      Filter userFilter=null;
      List<Filter> filters=null;
      List<PersistentFreightDueInWorkOrder> results = null;
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

      logger.exiting(_CLASS, "findAll(UserContext)", results);
      return results;
   }
   
   /**
    * 
    */
   @Override
   public List<PersistentFreightDueInWorkOrder> findActionable(UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findActionable(UserContext ctx)",ctx);
      List<PersistentFreightDueInWorkOrder> workOrders=null;
      
      workOrders=findAllOpen(ctx);
      
      logger.exiting(_CLASS, "findActionable(UserContext ctx)",workOrders);
      return workOrders;
   }
   /**
    * 
    */
   @Override
   public List<PersistentFreightDueInWorkOrder> findAllOpen(UserContext ctx)
         throws DAOException {
      // TODO Auto-generated method stub
      return null;
   }
}