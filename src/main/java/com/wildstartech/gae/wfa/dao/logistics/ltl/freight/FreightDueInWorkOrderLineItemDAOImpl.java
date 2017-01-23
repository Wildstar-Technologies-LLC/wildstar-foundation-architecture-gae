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
package com.wildstartech.gae.wfa.dao.logistics.ltl.freight;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.gae.wfa.dao.location.PersistentPostalCodeDistanceImpl;
import com.wildstartech.wfa.logistics.ltl.LineItemComparator;
import com.wildstartech.wfa.logistics.ltl.freight.FreightDueInWorkOrderLineItem;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.logistics.ltl.freight.FreightDueInWorkOrderLineItemDAO;
import com.wildstartech.wfa.dao.logistics.ltl.freight.PersistentFreightDueInWorkOrderLineItem;
import com.wildstartech.wfa.dao.user.UserContext;

public class FreightDueInWorkOrderLineItemDAOImpl 
extends WildDAOImpl<FreightDueInWorkOrderLineItem, 
        PersistentFreightDueInWorkOrderLineItem> 
implements FreightDueInWorkOrderLineItemDAO {
   private static final String _CLASS=
         FreightDueInWorkOrderLineItemDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   @Override
   public PersistentFreightDueInWorkOrderLineItem findInstance(
         FreightDueInWorkOrderLineItem item, UserContext ctx)
               throws DAOException {
      logger.entering(_CLASS, "findInstance(FreightDueInWorkOrderLineItem)",
            new Object[] {item, ctx});
      PersistentFreightDueInWorkOrderLineItem foundItem=null;
      logger.exiting(_CLASS, "");
      return foundItem;
   }

   @Override
   public PersistentFreightDueInWorkOrderLineItem create() {
      logger.entering(_CLASS, "create()");
      PersistentFreightDueInWorkOrderLineItemImpl item=null;
      item = new PersistentFreightDueInWorkOrderLineItemImpl();
      logger.entering(_CLASS, "create()",item);
      return item;
   }
   
   /**
    * Create a new object using an object reference.
    */
   @Override
   public PersistentFreightDueInWorkOrderLineItem create(
         FreightDueInWorkOrderLineItem item, UserContext ctx) {
      logger.entering(_CLASS, 
            "create(FreightDueInWorkOrderLineItem,UserContext)", 
            new Object[] {item,ctx});
      PersistentFreightDueInWorkOrderLineItemImpl persistentItem=null;
      logger.exiting(_CLASS, 
            "create(FreightDueInWorkOrderLineItem,UserContext)",
            persistentItem);
      return persistentItem;
   }

   /**
    * Returns a list of items belonging to the specified work order.
    * @param id The unique identifier of the work order that should be used
    * when looking for associated line items.
    * @param ctx The UserContext that should be used when performing the search
    * for the requested line items.
    * @return
    */
   @Override
   public List<PersistentFreightDueInWorkOrderLineItem> findByWorkOrderId(
         String id, UserContext ctx) {
      logger.entering(_CLASS, "findByWorkOrderId(String,UserContext)",
            new Object[] {id,ctx});
      List<PersistentFreightDueInWorkOrderLineItem> results=null;
      Query query=null;
      QueryWrapper wrapper=null;
      Filter filter=null;
      String kind=null;
      
      if ((id != null) && (id.length() > 0) && (ctx != null)) {
         kind=PersistentFreightDueInWorkOrderLineItemImpl._KIND;
         query=new Query(kind);
         filter=new Query.FilterPredicate("workOrderIdentifier",
               FilterOperator.EQUAL,
               id);
         query.setFilter(filter);
         query.addSort("lineItemNumber",SortDirection.ASCENDING);
         wrapper=new QueryWrapper(query);
         results=findByQuery(wrapper,ctx);
         // Let's sort the results by line item.
         Collections.sort(results,
             new LineItemComparator<PersistentFreightDueInWorkOrderLineItem>());
      } else {
         // Error handling...
         if (ctx == null) {
            logger.severe("The UserContext parameter was null.");
         } // END if (ctx == null)
         if (id == null) {
            logger.severe("A null value was passed as the id parameter.");
         } else {
            logger.severe("An empty string was passed as the id paraemter.");
         } // END if (id == null)         
      }
      logger.exiting(_CLASS, "findByWorkOrderId(String,UserContext)",results);
      return results;
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
            PersistentFreightDueInWorkOrderLineItemImpl._KIND);
      return PersistentFreightDueInWorkOrderLineItemImpl._KIND;
   }
   
}