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

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.wildstartech.gae.wfa.dao.MemcacheKeyGenerator;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.dao.user.UserContextDAO;

public class UserContextDAOImpl 
extends WildDAOImpl<UserContext, UserContext> 
implements UserContextDAO {

  private static final String _CLASS = UserContextDAOImpl.class.getName();
  private static final Logger logger = Logger.getLogger(_CLASS);

  /**
   * Returns the <code>UserContext</code> object for the specified user name.
   */
  public UserContextImpl findByUserName(String userName, UserContext ctx) {
    logger.entering(_CLASS, "findByUserName(String)");
    Filter filter=null;
    List<UserContext> items=null;
    MemcacheService cache = null;
    Query query = null;
    QueryWrapper qw=null;
    UserContextImpl foundCtx = null;

    if (userName != null) {
      if (ctx != null) {
        // Obtain a reference to the memcache instance.
        cache = MemcacheServiceFactory.getMemcacheService();
        // Check the cache to see if it contains an instance of the user.
        foundCtx = (UserContextImpl) cache.get(MemcacheKeyGenerator.getKey(
            UserContextImpl._KIND, userName));
        if (foundCtx == null) {
          // The context was NOT in memcache, so let's look it up.
          filter=new FilterPredicate(
              "username",
              FilterOperator.EQUAL,
              userName);
          query=new Query(UserContextImpl._KIND);
          query.setFilter(filter);
          qw=new QueryWrapper(query);
          items=this.findByQuery(qw, ctx);
          if (items.size() > 0) {
            if (items.size() == 1) {
              foundCtx=(UserContextImpl) items.get(0);
            } else {
              logger.warning(
                  "There should be only one match, but there are multiple.");
            } // END if (items.size() > 1)         
          } else {
            logger.warning("No user context exits for the specified user.");
          } // END if (items.size() == 0)
        } // END if (ctx == null)
      } else {
        // The specified UserContext was null.
        logger.warning("UserContext parameter specified is null.");
      } // END if (ctx != null)
    } else {
      logger.warning("userName parameter specified is null.");
    } // END if (userName != null)

    logger.exiting(_CLASS, "findByUserName(String)", ctx);
    return foundCtx;
  }

  public UserContextImpl findInstance(UserContext context, UserContext ctx)
      throws DAOException {
    logger.entering(_CLASS, "findInstance(UserContext)", context);
    UserContextImpl foundContext = null;
    logger.exiting(_CLASS, "findInstance(UserContext)", foundContext);
    return foundContext;
  }
  
  protected static UserContext getUserContextAdmin() {
     //logger.entering(_CLASS, "getUserContextAdmin()");
     UserContext ctx=null;
     ctx=UserContextDAOImpl.getAdminContext();
     //logger.exiting(_CLASS, "getUserContextAdmin()",ctx);
     return ctx;     
  }

  @Override
  public UserContextImpl create() {
    logger.entering(_CLASS, "create()");
    UserContextImpl context = null;
    context = new UserContextImpl();
    logger.exiting(_CLASS, "create()", context);
    return context;
  }
  
  @Override
  public UserContextImpl create(UserContext tCtx, UserContext ctx) {
    logger.entering(_CLASS,"create(UserContext,UserContext)",
        new Object[] {tCtx, ctx});
    UserContextImpl pCtx=null;
    if (tCtx == null) {
      pCtx=new UserContextImpl();
    } else {
      pCtx=new UserContextImpl();
      pCtx.setUserName(tCtx.getUserName());      
    } // END if (tCtx == null)    
    logger.exiting(_CLASS,"create(UserContext,UserContext)",pCtx);
    return pCtx;
  }
  
  /**
   * Returns the <em>Kind</em> property of the entity which is used for the
   * purpose of querying the Datastore.
   * 
   * @return A string value which is used by the Datastore for the purpose of
   *         categorizing entities of this object's type to provide the ability
   *         to querying the Datastore and retrieve entities.
   */
  protected final String getKind() {
     logger.entering(_CLASS, "getKind()");
     logger.exiting(_CLASS, "getKind()", UserContextImpl._KIND);
     return UserContextImpl._KIND;
  }
}