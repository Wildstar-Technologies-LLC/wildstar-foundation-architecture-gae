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
package com.wildstartech.gae.wfa.dao.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.Localization;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.user.PasswordResetRequestDAO;
import com.wildstartech.wfa.dao.user.PasswordResetRequestDAOResources;
import com.wildstartech.wfa.dao.user.PersistentPasswordResetRequest;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.user.PasswordResetRequest;

/**
 * Data Access Object used to create, find, and save instances of the
 * {@code PersistentPasswordResetRequest} object in the persistent
 * data store.
 * 
 * <p>There can be only one instance of the 
 * {@code PersistentPasswordResetRequest} object for a given {@code userName}
 * value in the persistent data store at any given time.</p>
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 * @version 0.1, 2016-09-11
 *
 */
public class PasswordResetRequestDAOImpl 
extends WildDAOImpl<PasswordResetRequest, PersistentPasswordResetRequest> 
implements PasswordResetRequestDAO {
   private static final String _CLASS=
         PasswordResetRequestDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   /**
    * Default, no-argument constructor.
    */
   public PasswordResetRequestDAOImpl() {
      super();
      logger.entering(_CLASS, "PasswordResetRequestDAOImpl()");
      logger.exiting(_CLASS, "PasswordResetRequestDAOImpl()");
   }
   
   /**
    * Find an instance of the {@code PersistentPasswordResetRequest} object
    * using the specified {@code request} parameter as a template. 
    */
   @Override
   public PersistentPasswordResetRequest findInstance(
         PasswordResetRequest request, UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, 
            "findInstance(PasswordResetRequest,UserContext)",
            new Object[] {request,ctx});
      PersistentPasswordResetRequest pRequest=null;
      String userName=null;
      
      if (request != null) {
         userName=request.getUserName();
         if (!isEmpty(userName)) {
            pRequest=findByUserName(userName,ctx);
         } // END if (!isEmpty(userName))
      } // END if (request != null)
      
      logger.exiting(_CLASS, "PasswordResetRequestDAOImpl()",pRequest);
      return pRequest;
   }

   /**
    * Returns a {@code List} of {@code PersistentPasswordRequest} objects 
    * that exist in the persistent data store with an expiration date 
    * equal to the specified value.
    * 
    * <p>If the caller specifies a value for both the {@code startDate}
    * and {@code endDate} parameters, the system will look for 
    * {@code PersistentPasswordResetRequest} objects that have a value
    * for the {@code expirationDate} property that is within the specified
    * date/time range (inclusive of the specified {@code startDate} and
    * {@code endDate} values.</p>
    * <p>If the {@code startDate} is greater than the {@code endDate}, the
    * utility will swap the parameters before performing the search.</p>
    * <p>If the caller specifies a value for the {@code startDate} parameter
    * but no value for the {@code endDate} parameter, the system will look
    * for all records that have a value for the {@code expirationDate} that
    * is later than the specified {@code startDate} value.</p>
    * <p>If the caller does not specify a {@code startDate}, but they do 
    * specify a {@code endDate} then the system will look for all 
    * {@code PasswordResetRequest} objects that have a value in the
    * {@code expirationDate} property that is greater than or equal to
    * the current date/time and less than or equal to the value passed as the
    * {@code endDate} parameter.  In this scenario, if the {@code endDate}
    * parameter value is less than the current date/time, then an 
    * {@code IllegalArgumentException} will be thrown.</p>
    * 
    */
   @Override
   public List<PersistentPasswordResetRequest> 
   findByExpirationDate(Date startDate, Date endDate, UserContext ctx) {
      logger.entering(_CLASS, "findByExpirationDate(Date,Date)",
            new Object[] {startDate,endDate});
      Query.CompositeFilter compositeFilter=null;
      Filter filter=null;
      List<PersistentPasswordResetRequest> results=null;
      Query query=null;
      QueryWrapper wrapper=null;
      String msg=null;
      
      
      if ((startDate == null) && (endDate == null)) {
         msg=getLocalizedMessage(
               PasswordResetRequestDAOResources.MSGKEY_FINDBYDATE_REQUIREDATE,
               null);
         logger.warning(msg);
         throw new IllegalArgumentException(msg);
      } else { 
         query=new Query(getKind());
         if ((startDate != null) && (endDate != null)) {
            // Both dates were specified.
            if (startDate.getTime() > endDate.getTime()) {
               compositeFilter=new Query.CompositeFilter(
                     CompositeFilterOperator.AND,
                     Arrays.<Filter>asList(
                           new FilterPredicate("expirationDate",
                                 FilterOperator.LESS_THAN_OR_EQUAL,
                                 startDate),
                           new FilterPredicate("expirationDate",
                                 FilterOperator.GREATER_THAN_OR_EQUAL,
                                 endDate)));               
            } else {
               compositeFilter=new Query.CompositeFilter(
                     CompositeFilterOperator.AND,
                     Arrays.<Filter>asList(
                           new FilterPredicate("expirationDate",
                                 FilterOperator.GREATER_THAN_OR_EQUAL,
                                 startDate),
                           new FilterPredicate("expirationDate",
                                 FilterOperator.LESS_THAN_OR_EQUAL,
                                 endDate)));
            } // END
            query.setFilter(compositeFilter);
         } else if ((startDate != null)) {
            // Only the start date was specified
            filter=new FilterPredicate("expirationDate",
                  FilterOperator.GREATER_THAN_OR_EQUAL,
                  startDate);
            query.setFilter(filter);
         } else {
            // Only the end date is specified.     
            if (endDate.compareTo(new Date()) >= 0) {
               compositeFilter=new Query.CompositeFilter(
                     CompositeFilterOperator.AND,
                     Arrays.<Filter>asList(
                           new FilterPredicate("expirationDate",
                                 FilterOperator.GREATER_THAN_OR_EQUAL,
                                 new Date()),
                           new FilterPredicate("expirationDate",
                                 FilterOperator.LESS_THAN_OR_EQUAL,
                                 endDate)));
               query.setFilter(filter);
            } else {
               /* The end date is set to some point in the past, so display
                * an error message. */
               msg=getLocalizedMessage(
                     PasswordResetRequestDAOResources
                        .MSGKEY_FINDBYDATE_PASTENDDATE,
                     null);
               logger.warning(msg);
               throw new IllegalArgumentException(msg);
            } // END if (endDate.compareTo(new Date()) >= 0)            
         } // END if if ((startDate != null) && (endDate != null))
         
         wrapper=new QueryWrapper(query);
         results=_findByQuery(wrapper,ctx);
      } // END if ((startDate == null) && (endDate == null))
      if (results == null) {
         results=new ArrayList<PersistentPasswordResetRequest>();
      } // END if (results == null)
      
      logger.exiting(_CLASS, "findByExpirationDate(Date,Date)",results);
      return results;
   }

   @Override
   public PersistentPasswordResetRequest findByUserName(
         String userName, UserContext ctx) {
      logger.entering(_CLASS, "findByUserName(String)",userName);
      PersistentPasswordResetRequest request=null;
      Filter filter=null;
      List<PersistentPasswordResetRequest> requests=null;
      Query query=null;
      QueryWrapper wrapper=null;
      String msg=null;
      
      filter=new FilterPredicate("userName",
            FilterOperator.EQUAL, userName);
      query=new Query(getKind());
      query.setFilter(filter);
      wrapper=new QueryWrapper();
      wrapper.setQuery(query);
      requests=findByQuery(wrapper, ctx);
      if (requests.size() > 1) {
         logger.severe(msg);
      } else if (requests.size() == 1) {
         request=requests.get(0);
      } else {
         msg=getLocalizedMessage(
               PasswordResetRequestDAOResources.MSGKEY_REQUEST_NOT_FOUND,
               new Object[] {userName});
         logger.finest(msg);
      } // END if (requests.size() > 1)
      
      logger.exiting(_CLASS, "findByUserName(String)",userName);
      return request;
   }

   @Override
   public PersistentPasswordResetRequest create() {
      logger.entering(_CLASS, "create()");
      PersistentPasswordResetRequest request=null;
      request=new PersistentPasswordResetRequestImpl();
      logger.exiting(_CLASS, "create()",request);
      return request;
   }

   @Override
   public PersistentPasswordResetRequest create(PasswordResetRequest request, UserContext ctx) {
      logger.entering(_CLASS, "create(PasswordResetRequest,UserContext)",
            new Object[] {request,ctx});
      PersistentPasswordResetRequest pRequest=null;
      
      pRequest=new PersistentPasswordResetRequestImpl();
      pRequest.updateFromObject(request);
      
      logger.exiting(_CLASS, "create(PasswordResetRequest,UserContext)",
            pRequest);
      return pRequest;
   }

   @Override
   protected String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentPasswordResetRequestImpl._KIND);
      return PersistentPasswordResetRequestImpl._KIND;
   }
   
   @Override
   /**
    * Returns a localized message from the {@code ResourceBundle} instance
    * that is defined for the {@code PasswordResetRequestDAO} interface.
    * @param resourceId The value which identifies the message template 
    * in the ResourceBundle that should be formatted and returned.
    * @param params An {@code Object} array containing values that should
    * be used to customize the message template obtianed from the resource 
    * bundle.    
    * @return A localized message that has been fully customized using 
    * values provided via the {@code params} {@code Object} array.  
    */
   public String getLocalizedMessage(String resourceId, Object[] params) {
      logger.entering(_CLASS, "getLocalizedMessage(String,Object[])",
            new Object[] {resourceId,params});
      String msg="";
      
      msg=Localization.getString(PasswordResetRequestDAO.RESOURCE_BUNDLE, 
            resourceId, params);
      if ((msg == null) || (msg.isEmpty())) {
         // Unable to obtain a message using local Resource Bundle
         msg=super.getLocalizedMessage(resourceId,params);
      } // END if ((msg == null) || (msg.isEmpty()))
      if (msg == null) msg="";
      logger.exiting(_CLASS, "getLocalizedMessage(String,Object[])",msg);
      return msg;
   }
}