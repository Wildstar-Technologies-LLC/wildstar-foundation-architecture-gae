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
package com.wildstartech.gae.wfa.dao.ticketing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.Property;
import com.wildstartech.wfa.dao.SortCriterion;
import com.wildstartech.wfa.dao.ticketing.BasicTicketDAO;
import com.wildstartech.wfa.dao.ticketing.PersistentBasicTicket;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.ticketing.BasicTicket;

public abstract class BasicTicketDAOImpl<T extends BasicTicket, W extends PersistentBasicTicket<T>>
      extends WildDAOImpl<T, W> implements BasicTicketDAO<T, W> {
   private static final String _CLASS = BasicTicketDAOImpl.class.getName();
   private static final Logger logger = Logger.getLogger(_CLASS);

   @Override
   public final W findByRequestId(String requestId, UserContext ctx) {
      logger.entering(_CLASS, "findByRequestId(String,UserContext)",
            new Object[] { requestId, ctx });
      Filter filter = null;
      Filter userFilter=null;
      List<Filter> filters=null;
      List<W> pObjects = null;
      Query query = null;
      QueryWrapper qw = null;
      String currentUser=null;
      String kind = null;
      W pObject = null;

      if (((requestId != null) && (requestId.length() != 0)) && (ctx != null)) {
         // The requestId is a validString
         pObject = create();
         kind = ((WildObjectImpl<?>) pObject).getKind();
         query = new Query(kind);
         filter = new Query.FilterPredicate("requestId", FilterOperator.EQUAL,
               requestId);
         
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
         qw = new QueryWrapper(query);
         pObjects = findByQuery(qw, ctx);
      } else {
         /*
          * Either the requestId is null or an empty string or the UserContext
          * is null.
          */
         if (requestId == null) {
            logger.severe("The requestId is null.");
         } else if (requestId.length() == 0) {
            logger.severe("The requestId is a zero-length string.");
         } // END if (requestId == null)
         if (ctx == null) {
            logger.severe("The UserContext is null.");
         } // END if (ctx == null)
      } // END if ((quoteId != null) && (ctx != null))
        // Let's check to see if the list of items were returned.
      if (pObjects == null) {
         // Either the quoteId or ctx were null, so return an empty list.
         pObjects = new ArrayList<W>();
      } // END if (pObjects == null)
      if (!pObjects.isEmpty()) {
         pObject = pObjects.get(0);
      } // END if (! pObjects.isEmpty())

      logger.exiting(_CLASS, "findByRequestId(String,UserContext)", pObject);
      return pObject;
   }
   
   public List<W> findByStatusState(
         String statusState,
         String statusReason, 
         Date startDate, 
         UserContext ctx) throws DAOException {
      logger.entering(_CLASS, "findByStatus(String,String,Date,UserContext)",
            new Object[] {statusState,statusReason,startDate,ctx});
      List<Filter> filters=null;
      List<W> results=null;
      List<SortCriterion> sortCriteria=null;
      Property property=null;
      Query query=null;
      QueryWrapper qw=null;
      Query.Filter filter=null;
      Query.Filter userFilter=null;
      SortCriterion sortCriterion=null;
      String currentUser=null;
      String kind=null;      
      
      if (
            (ctx != null) &&
            (ctx.isAuthenticated()) &&
            (statusState != null) &&
            (!statusState.isEmpty())
         ) {
         kind=getKind();
         query=new Query(kind);
         filters=new ArrayList<Filter>();
         // Process the status state parameter
         filter=new FilterPredicate(
               "statusState",
               FilterOperator.EQUAL,
               statusState);
         filters.add(filter);
         // Process the statusReason parameter.
         if ((statusReason != null) && (!statusReason.isEmpty())) {
            filter=new FilterPredicate(
                  "statusReason",
                  FilterOperator.EQUAL,
                  statusReason);
            filters.add(filter);                  
         } // END if ((statusReason != null) && (!statusReason.isEmpty()))
         // Process the startDate parameter
         if (startDate != null) {
            filter=new FilterPredicate(
                  "dateCreated",
                  FilterOperator.GREATER_THAN,
                  startDate);
            filters.add(filter);
            /* ****************************************************************
             * BEGIN: Modify Sort Criteria
             * This is necessary because if there is a startDate specified, the
             * FIRST field in the list of sort criteria MUST Be the createDate.
             * Otherwise, the following error will be thrown at runtime:
             * 
             * SEVERE: java.lang.IllegalArgumentException: The first sort 
             * property must be the same as the property to which the 
             * inequality filter is applied.  In your query the first sort
             * property is dateCreated but the inequality filter is on 
             * dateCreated
             *****************************************************************/
            sortCriteria=this.getSortCriteria();
            if (sortCriteria == null) {
               sortCriteria=new ArrayList<SortCriterion>();
            } // END if (sortCriteria == null)
            sortCriterion=new SortCriterion();
            property=new Property();
            property.setName("dateCreated");
            property.setType(Date.class);
            sortCriterion.setProperty(property);
            sortCriteria.add(0,sortCriterion);
            setSortCriteria(sortCriteria);
            /* ****************************************************************
             * END: Modify Sort Criteria
             * ***************************************************************/
         } // END if (startDate != null)    
         filter=new Query.CompositeFilter(
            Query.CompositeFilterOperator.AND,
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
         results=findByQuery(qw,ctx);
      } else {
         if (ctx == null) {
            logger.severe("UserContext parameter was null.");
         } else if (!ctx.isAuthenticated()) {
            logger.severe("The UserContext is NOT authenticated.");
         } else {
            logger.severe("The statusState parameter was not specified.");
         } // END if (ctx == null)
      } // END if (ctx != null) && (statusReason == null) ... 
      
      if (results == null) {
         results=new ArrayList<W>();
      } // END if (results == null)
      logger.entering(_CLASS, "findByStatus(String,String,Date,UserContext)",
            results);
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
   protected String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentBasicTicketImpl._KIND);
      return PersistentBasicTicketImpl._KIND;
   }
}