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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.DAOException;
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
      List<W> pObjects = null;
      Query query = null;
      QueryWrapper qw = null;
      String kind = null;
      W pObject = null;

      if (((requestId != null) && (requestId.length() != 0)) && (ctx != null)) {
         // The requestId is a validString
         pObject = create();
         kind = ((WildObjectImpl<?>) pObject).getKind();
         query = new Query(kind);
         filter = new Query.FilterPredicate("requestId", FilterOperator.EQUAL,
               requestId);
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
      Calendar calendar=null;
      List<Filter> filters=null;
      List<W> results=null;
      Query query=null;
      QueryWrapper qw=null;
      Query.Filter filter=null;
      String currentUser=null;
      String kind=null;
      TimeZone tz=null;
      
      if (ctx != null) {
         kind=getKind();
         query=new Query(kind);
         
      } else {
         logger.severe("UserContext parameter was null.");
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