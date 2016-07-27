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
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentWorkOrderLineItem;
import com.wildstartech.wfa.dao.logistics.ltl.WorkOrderLineItemDAO;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.LineItemComparator;
import com.wildstartech.wfa.logistics.ltl.WorkOrderLineItem;

public class WorkOrderLineItemDAOImpl
extends WildDAOImpl<WorkOrderLineItem, PersistentWorkOrderLineItem> 
implements WorkOrderLineItemDAO {
   private static final String _CLASS=WorkOrderLineItemDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   @Override
   public PersistentWorkOrderLineItem findInstance(WorkOrderLineItem wli,
         UserContext ctx) throws DAOException {
      logger.entering(_CLASS, "findInstance(WorkOrderLineItem,UserContext)",
            new Object[] {wli,ctx});
      PersistentWorkOrderLineItem pWli=null;
      logger.exiting(_CLASS, "findInstance(WorkOrderLineItem,UserContext", pWli);
      return pWli;
   }

   @Override
   public List<WorkOrderLineItem> findByWorkOrderIdentifier(String workOrderId,
         UserContext ctx) {
      logger.entering(_CLASS, 
            "findByWorkOrderIdentifier(String,UserContext)",
            new Object[] {workOrderId,ctx});
      List<WorkOrderLineItem> lineItems=null;
      List<PersistentWorkOrderLineItem> results=null;
      Query query=null;
      QueryWrapper wrapper=null;
      Filter filter=null;
      String kind=null;
      
      if (
            (workOrderId != null) && 
            (workOrderId.length() > 0) && 
            (ctx != null)
         ) {
         kind=PersistentWorkOrderLineItemImpl._KIND;
         query=new Query(kind);
         filter=new Query.FilterPredicate("workOrderIdentifier",
               FilterOperator.EQUAL,
               workOrderId);
         query.setFilter(filter);
         query.addSort("lineItemNumber",SortDirection.ASCENDING);
         wrapper=new QueryWrapper(query);
         results=findByQuery(wrapper,ctx);
         // Let's sort the results by line item.
         Collections.sort(results,
             new LineItemComparator<PersistentWorkOrderLineItem>());
         lineItems=new ArrayList<WorkOrderLineItem>();
         for (WorkOrderLineItem item: results) {
            lineItems.add(item);
         } // END for (WorkOrderLineItem item: results)
      } else {
         // Error handling...
         if (ctx == null) {
            logger.severe("The UserContext parameter was null.");
         } // END if (ctx == null)
         if (workOrderId == null) {
            logger.severe("A null value was passed as the id parameter.");
         } else {
            logger.severe("An empty string was passed as the id paraemter.");
         } // END if (id == null)         
      }      
      
      if (lineItems == null) {
         lineItems=new ArrayList<WorkOrderLineItem>();
      } // END if (lineItems == null)
      logger.exiting(_CLASS,
            "findByWorkOrderIdentifier(String,UserContext)",
            lineItems);
      return lineItems;
   }

   @Override
   public PersistentWorkOrderLineItem create() {
      logger.entering(_CLASS, "create()");
      PersistentWorkOrderLineItem pWli=null;
      pWli=new PersistentWorkOrderLineItemImpl();
      logger.exiting(_CLASS, "create()");
      return pWli;
   }

   @Override
   public PersistentWorkOrderLineItem create(WorkOrderLineItem wli,
         UserContext ctx) {
      logger.entering(_CLASS, "create(WorkOrderLineItem,UserContext)",
            new Object[] {wli,ctx});
      PersistentWorkOrderLineItemImpl pWli=null;
      pWli=new PersistentWorkOrderLineItemImpl();
      pWli.populateFromObject(wli);
      logger.exiting(_CLASS, "create(WorkOrderLineItem,UserContext)",pWli);
      return pWli;
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
      logger.exiting(_CLASS, "getKind()",PersistentWorkOrderLineItemImpl._KIND);
      return PersistentWorkOrderLineItemImpl._KIND;
   }
}