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
 */package com.wildstartech.gae.wfa.dao.user;

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.wildstartech.gae.wfa.dao.MemcacheKeyGenerator;
import com.wildstartech.gae.wfa.dao.PersistentGroupImpl;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.EmailAddress;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.PersistentGroup;
import com.wildstartech.wfa.dao.GroupDAO;
import com.wildstartech.wfa.dao.GroupDAOFactory;
import com.wildstartech.wfa.dao.WildObject;
import com.wildstartech.wfa.dao.user.PersistentUser;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.dao.user.UserDAO;
import com.wildstartech.wfa.group.Group;
import com.wildstartech.wfa.user.User;

public class UserDAOImpl extends WildDAOImpl<User, PersistentUser>
      implements UserDAO {
   private static final String _CLASS = UserDAOImpl.class.getName();
   private static final Logger logger = Logger.getLogger(_CLASS);

   @Override
   public PersistentUserImpl create() {
      logger.entering(_CLASS, "create()");
      PersistentUserImpl user = new PersistentUserImpl();
      logger.exiting(_CLASS, "create()", user);
      return user;
   }

   @Override
   public PersistentUserImpl create(User user, UserContext ctx) {
      logger.entering(_CLASS, "create(User,UserContext)",
            new Object[] { user, ctx });
      PersistentUserImpl pUser = null;
      if (user == null) {
         pUser = new PersistentUserImpl();
      } else if (ctx == null) {
         pUser = new PersistentUserImpl();
         pUser.populateFromObject(user);
      } else {
         pUser = findInstance(user, ctx);
         if (pUser == null) {
            pUser = new PersistentUserImpl();
            pUser.populateFromObject(user);
         } // END if (pUser == null)
      } // END if (user == null)
      logger.exiting(_CLASS, "create(User,UserContext)", pUser);
      return pUser;
   }

   /**
    * Returns the <code>User</code> object associated with an email address.
    * 
    * @param emailAddress
    * @return
    */
   public PersistentUser findByEmailAddress(String emailAddress,
         UserContext ctx) {
      logger.entering(_CLASS, "findByEmailAddress(String)", emailAddress);
      EmailAddress address = null;
      PersistentUser user = null;

      if (emailAddress != null) {
         // Validate the e-mail address to make sure it's a good one
         address = new EmailAddress(emailAddress);
         if (address.isValid()) {
            user = findByName(emailAddress, ctx);
         } else {
            // The e-mail address isn't good, so we're not going to do a
            // lookup.
            logger.warning("Email address specified is not valid.");
         } // END if (address.isValid())
      } else {
         logger.warning("The emailAddress parameter was null.");
      } // END if (emailAddress != null)

      logger.exiting(_CLASS, "findByEmailAddress(String)", user);
      return user;
   }

   /**
    * Uses the <code>User</code> object's name to locate a specific instance.
    */
   public PersistentUser findByName(String name, UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findByName(String,UserContext)",
            new Object[] { name, ctx });
      DatastoreService datastore = null;
      Entity entity = null;
      MemcacheService service = null;
      Object obj = null;
      PersistentUserImpl user = null;
      PreparedQuery pQuery = null;
      Query query = null;
      StringBuilder msg=null;

      if ((name != null) && (ctx != null)) {
         service = MemcacheServiceFactory.getMemcacheService();
         obj = service.get(
               MemcacheKeyGenerator.getKey(PersistentUserImpl._KIND, name));
         if (obj != null) {
            // The memcache service returned an object, is it a UserImpl
            if (obj instanceof PersistentUserImpl) {
               /*
                * Cast the object to UserImpl and associate it with the user
                * obejct for this method.
                */
               user = (PersistentUserImpl) obj;
            } else {
               /*
                * The object returned by memcache is NOT an instance of
                * UserImpl.
                */
               logger.warning("Returned object not of exptected type.");
               logger.warning(obj.getClass().getName());
            } // END if (obj instanceof UserImpl)
         } else {
            /* The object was not found, so search data store for it. */
            datastore = DatastoreServiceFactory.getDatastoreService();
            query = new Query(PersistentUserImpl._KIND);
            query.setFilter(new Query.FilterPredicate("name",
                  Query.FilterOperator.EQUAL, name));
            pQuery = datastore.prepare(query);
            entity = pQuery.asSingleEntity();
            if (entity != null) {
               user = new PersistentUserImpl();
               user.populateFromEntity(entity, ctx);
               // Add the found user to memcache
               service.put(MemcacheKeyGenerator.getKey(PersistentUserImpl._KIND,
                     user.getName()), user);
            } else {
               msg=new StringBuilder(80);
               msg.append("The specified user account, \"");
               msg.append(name);
               msg.append("\" could not be found.");
               logger.severe(msg.toString());               
            } // END if (entity != null)
         } // END if ((obj != null) && (obj instanceof UserImpl))
      } else {
         // Either the name parameter or the context was null.
         if (name == null) {
            logger.warning("The name parameter was null.");
         } // END if (name == null)
         if (ctx == null) {
            logger.warning("The ctx parameter was null.");
         } // END if (ctx == null)
      } // END if ((name != null) && (ctx != null))

      logger.exiting(_CLASS, "findByName(String,UserContext)", user);
      return user;
   }

   /**
    * Locates the persistent equivalent of a given object.
    *
    * <p>
    * There are times when an application may lose reference to the persistent
    * version of a particular entity. For these situations, the
    * <code>findInstance</code> method exists to take a non-persistent instance
    * of a type and search the persistent data store for the persistent
    * equivalent.
    * </p>
    */
   public PersistentUserImpl findInstance(User user, UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findInstance(User)", user);
      String identifier = null;
      String name = null;
      PersistentUserImpl foundObject = null;
      if (user != null) {
         if (user instanceof WildObject) {
            identifier = ((WildObject) user).getIdentifier();
            foundObject = (PersistentUserImpl) findByIdentifier(identifier,
                  ctx);
         } // END if (user instanceof WildObject)
         if (foundObject == null) {
            // foundObject was null, so look for the object by name.
            name = user.getName();
            foundObject = (PersistentUserImpl) findByName(name, ctx);
         } // END if (foundObject == null)
      } // END if (user != null)
      logger.exiting(_CLASS, "findInstance(User)", foundObject);
      return foundObject;
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
      logger.exiting(_CLASS, "getKind()", PersistentUserImpl._KIND);
      return PersistentUserImpl._KIND;
   }

   /**
    * Save the specified User object
    * 
    * @see com.wildstartech.gae.wfa.dao.WildDAOImpl#save(java.lang.Object,
    *      com.wildstartech.wfa.dao.user.UserContext)
    */
   public PersistentUser save(User user, UserContext ctx) {
      logger.entering(_CLASS, "save(User,UserContext)",
            new Object[] { user, ctx });
      PersistentGroup savedGroup = null;
      GroupDAO gDao = null;
      GroupDAOFactory gFactory = null;
      List<Group> groups = null;
      PersistentUser savedUser = null;

      /*
       * Call the save method of the super class to handle the "normal"
       * persistence requirements for saving a user record.
       */
      savedUser = super.save(user, ctx);
      /*
       * Now lets manage the process of saving information on the groups that
       * are related to the user account.
       */
      groups = user.getGroups();
      if (groups.size() > 0) {
         // There are groups to save, so get access to the requisite DAOs
         gFactory = new GroupDAOFactory();
         gDao = gFactory.getDAO();
         for (Group group : groups) {
            // Iterate through the list of groups.
            if (group instanceof PersistentGroupImpl == false) {
               /*
                * The specified group isn't a persistent group, so locate the
                * persistent version.
                */
               savedGroup = gDao.findInstance(group, ctx);
               if (savedGroup == null) {
                  savedGroup = gDao.save(group, ctx);
               } // END if (savedGroup == null)
            } // END if (group instanceof GroupImpl
         } // END for (Group group: groups)
      } else {
         // There are no specified groups, so do nothing.
         logger.finest("No specified groups to save/associate.");
      } // END if (groups.size() > 0)

      logger.exiting(_CLASS, "save(User,UserContext", savedUser);
      return savedUser;
   }
}