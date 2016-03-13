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

import java.util.List;
import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.ticketing.BasicTicketDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.logistics.ltl.FreightDueInWorkOrderDAO;
import com.wildstartech.wfa.dao.logistics.ltl.FreightDueInWorkOrderLineItemDAO;
import com.wildstartech.wfa.dao.logistics.ltl.FreightDueInWorkOrderLineItemDAOFactory;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentFreightDueInWorkOrder;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentFreightDueInWorkOrderLineItem;
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
      PersistentFreightDueInWorkOrderLineItem pLineItem=null;
      String lineItemIdentifier=null;
      String requestId=null;
      String workOrderIdentifier=null;
      
      if ((workOrder != null) && (ctx != null)) {        
         lineItemDAOFactory=new FreightDueInWorkOrderLineItemDAOFactory();
         lineItemDAO=lineItemDAOFactory.getDAO();
         
         //***** lineItems
         lineItems=workOrder.getLineItems();
         
         // Let's save the work order.
         requestId=workOrder.getRequestId();
         if ((requestId == null) || (requestId.length() == 0)) {
            requestIdGenerator=new FreightDueInWorkOrderRequestIdGenerator();
            requestId=requestIdGenerator.getNextId();
            workOrder.setRequestId(requestId);
         } // END if ((requestId == null) || (requestId.length() == 0))
         pWorkOrder=(PersistentFreightDueInWorkOrderImpl) 
               super.save(workOrder, ctx);
         workOrderIdentifier=pWorkOrder.getIdentifier();
         
         //***** Save the work order line items.
         for (int i=0; i < lineItems.size(); i++) {
            lineItem=lineItems.get(i);
            if (!(lineItem instanceof PersistentFreightDueInWorkOrderLineItem)){
               // The work order line item is NOT persistent...
               pLineItem=lineItemDAO.findInstance(lineItem,ctx);               
            } else {
               lineItemIdentifier=((PersistentFreightDueInWorkOrderLineItem) 
                     lineItem).getIdentifier();
               if (lineItemIdentifier != null) {
                  pLineItem=
                        lineItemDAO.findByIdentifier(lineItemIdentifier, ctx);
               } else {
                  logger.warning(
                        "The entity is persistent, but it hasn't been saved.");
               } // END if (lineItemIdentifier != null)
            } // END if (!(lineItem instanceof PersistentFreightDueInWork...
            if (pLineItem == null) {
               // An existing line item was not found.
               pLineItem=lineItemDAO.create();
            } // END if (pLineItem == null)
            ((PersistentFreightDueInWorkOrderLineItemImpl) 
                  pLineItem).populateFromObject(lineItem);
            // Associate the workorder line item with the work order
            pLineItem.setWorkOrderIdentifier(workOrderIdentifier);
            // Save the line item
            pLineItem=lineItemDAO.save(pLineItem, ctx);
            // Put the line item back in the list.
            pWorkOrder.setLineItem(pLineItem.getLineItemNumber(),pLineItem);
            pLineItem=null;
            lineItem=null;            
         } // END for (int i=0; i < lineItems.size(); i++)
         // Remove FreightDueInWorkOrderLineItems no longer assocaited
         lineItemsToDelete=((PersistentFreightDueInWorkOrderImpl) 
               workOrder).getLineItemsToDelete();
         for (FreightDueInWorkOrderLineItem item: lineItemsToDelete) {
            lineItemDAO.delete(item, ctx);
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