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
package com.wildstartech.gae.wfa.dao;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.PersistentGroup;
import com.wildstartech.wfa.dao.GroupDAO;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.group.Group;

public class GroupDAOImpl 
extends WildDAOImpl<Group, PersistentGroup> 
implements GroupDAO {
  private static final String _CLASS = GroupDAOImpl.class.getName();
  private static final Logger logger = Logger.getLogger(_CLASS);

  /**
   * Returns a default, blank instance of a Group object.
   */
  public PersistentGroupImpl create() {
    logger.entering(_CLASS, "create()");
    PersistentGroupImpl group = new PersistentGroupImpl();
    logger.exiting(_CLASS, "create()", group);
    return group;
  }

  public PersistentGroupImpl create(PersistentGroup group, UserContext ctx) {
    logger.entering(_CLASS, "create(Group)", group);
    PersistentGroupImpl pGroup = null;
    if (group == null) {
      pGroup = new PersistentGroupImpl();
    } else if (ctx == null) {
      pGroup = new PersistentGroupImpl();
      pGroup.populateFromObject(group);
    } else {
      pGroup = (PersistentGroupImpl) findInstance(group, ctx);
      if (pGroup == null) {
        pGroup = new PersistentGroupImpl();
        pGroup.populateFromObject(group);
      } // END if (pGroup == null)
    } // END if (group == null)
    logger.exiting(_CLASS, "create(Group)", pGroup);
    return pGroup;
  }

  public PersistentGroupImpl findInstance(PersistentGroup group, UserContext ctx)
      throws DAOException {
    logger.entering(_CLASS, "findInstance(Group,UserContext)", new Object[] {
        group, ctx });
    PersistentGroupImpl foundGroup = null;
    logger.exiting(_CLASS, "findInstance(Group,UserContext)", foundGroup);
    return foundGroup;
  }

  /**
   * Locates the instance of the group using the specified name.
   */
  @Override
  public PersistentGroupImpl findByName(String name, UserContext ctx)
      throws DAOException {
    logger.entering(_CLASS, "findByName(String,UserContext)", new Object[] {
        name, ctx });
    DatastoreService datastore = null;
    Entity entity = null;
    MemcacheService service = null;
    Object obj = null;
    PreparedQuery pQuery = null;
    Query query = null;
    PersistentGroupImpl group = null;

    if ((name != null) && (ctx != null)) {
      service = MemcacheServiceFactory.getMemcacheService();
      obj = service.get(MemcacheKeyGenerator.getKey(PersistentGroupImpl._KIND,
          name));
      if (obj != null) {
        // The memcache service returned an object, is it a GroupImpl
        if (obj instanceof PersistentGroupImpl) {
          /*
           * Cast the object to GroupImpl and associate it with the user obejct
           * for this method.
           */
          group = (PersistentGroupImpl) obj;
        } else {
          /*
           * The object returned by memcache is NOT an instance of GroupImpl.
           */
          logger.warning("Returned object not of exptected type.");
          logger.warning(obj.getClass().getName());
        } // END if (obj instanceof UserImpl)
      } else {
        /* The object was not found, so search data store for it. */
        datastore = DatastoreServiceFactory.getDatastoreService();
        query = new Query(PersistentGroupImpl._KIND);
        query.setFilter(new Query.FilterPredicate("name",
            Query.FilterOperator.EQUAL, name));
        pQuery = datastore.prepare(query);
        entity = pQuery.asSingleEntity();
        if (entity != null) {
          group = new PersistentGroupImpl();
          group.populateFromEntity(entity, ctx);
          // Add the found user to memcache
          service.put(
              MemcacheKeyGenerator.getKey(PersistentGroupImpl._KIND,
                  group.getName()), group);
        } // END if (entity != null)
      } // END if ((obj != null) && (obj instanceof GroupImpl))
    } else {
      // Either the name parameter or the context was null.
      if (name == null) {
        logger.warning("The name parameter was null.");
      } // END if (name == null)
      if (ctx == null) {
        logger.warning("The ctx parameter was null.");
      } // END if (ctx == null)
    } // END if ((name != null) && (ctx != null))
    logger.entering(_CLASS, "findByName(String,UserContext)", group);
    return group;
  }

  @Override
  public PersistentGroup findInstance(Group object, UserContext ctx)
      throws DAOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PersistentGroupImpl create(Group object, UserContext ctx) {
    // TODO Auto-generated method stub
    return null;
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
     logger.exiting(_CLASS, "getKind()",PersistentGroupImpl._KIND);
     return PersistentGroupImpl._KIND;
  }
}
