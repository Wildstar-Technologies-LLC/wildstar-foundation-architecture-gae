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
 */package com.wildstartech.gae.wfa.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.appengine.api.datastore.FetchOptions.Builder.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.Property;
import com.wildstartech.wfa.dao.Query;
import com.wildstartech.wfa.dao.SortCriterion;
import com.wildstartech.wfa.dao.PersistentUser;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.WildDAO;
import com.wildstartech.wfa.dao.WildObject;

/**
 * Google Datastore implementation of the <code>WildDAO</code> interface.
 * 
 * <h1>Projection Queries</h1>
 * <p>This <code>Data Access Object (DAO)</code> provides users with the ability
 * to return partial entities as opposed to fully reading all properties of
 * an entity when objects are retrieved from the datastore.  The 
 * <code>DAO</code> will allow users to add a list of 
 * <code>projectionFields</code> prior to performing any query.  Once the query 
 * is executed and the results are returned, the list of 
 * <code>projectionFields</code> will be cleared unless the 
 * <code>retainProjectionFields<code> property is set equal to 
 * <code>true</code>.</p>
 * 
 * @author Derek Berube, Wildstar Technologies, LLC. 
 * @version 0.2, 2015-03-16
 *
 * @param <T>
 * @param <W>
 */
public abstract class WildDAOImpl<T, W extends WildObject>
implements WildDAO<T, W> {
  public static final String PROPKEY_RULESET="com.wildstartech.wfa.dao.ruleset";
  private static final String _CLASS = WildDAOImpl.class.getName();
  private static final Logger logger = Logger.getLogger(_CLASS);
  private static final String DEFAULT_USERNAME = "WildAdmin";
  private static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;
  private static final String DEFAULT_PASSWORD = "WildAdmin";

  private static String adminUserName = null;
  private static String adminPassword = null;
  private static UserContextImpl adminContext = null;
  
  private boolean retainProjectionFields=false;
  private int pageNumber=0;
  private int pageSize=WildDAOImpl.DEFAULT_PAGE_SIZE;
  private List<Property> projectionFields=null;
  private List<SortCriterion> sortCriteria=null;
  //private PersistenceRuleSet<? extends WildDAO<T,W>,T,W> ruleset=null;  
  
  // Instance initializer
  static {
    logger.entering(_CLASS,"Static initialization of WildDAOImpl.");
    loadProperties();
    WildDAOImpl.adminContext = new UserContextImpl();
    adminContext.setUserName(adminUserName);
    adminContext.setPassword(adminPassword);
    adminContext.authenticate();
    logger.exiting(_CLASS,"Static initialization of WildDAOImpl.");
  }
  
  /**
   * Loads static configuration data from the wild-dao-admin.properties file.
   */
  private static void loadProperties() {
    logger.entering(_CLASS, "loadProperties()");
    ClassLoader cl = null;
    InputStream in = null;
    Properties props = null;
    String password = null;
    String userName = null;
    
    cl = WildDAOImpl.class.getClassLoader();
    props = new Properties();
    in = cl.getResourceAsStream("wild-dao-admin.properties");
    try {
      if (in != null) {
        props.load(in);       
        //***** password
        password = props.getProperty(WildDAO.PROPKEY_ADMIN_PASSWORD);
        if (password == null) {
          logger.warning("Admin Password not specified. Using default");
          userName = WildDAOImpl.DEFAULT_PASSWORD;
        } // END if (userName == null)
        //***** userName
        userName = props.getProperty(WildDAO.PROPKEY_ADMIN_USER);
        if (userName == null) {
          logger.warning("Admin User Name not specified. Using default");
          userName = WildDAOImpl.DEFAULT_USERNAME;
        } // END if (userName == null)
        
        WildDAOImpl.adminUserName = userName;
        WildDAOImpl.adminPassword = password;
      } else {
        logger.log(Level.SEVERE, "Unable to find the properties file.");
      } // END if (in != null)
    } catch (IOException ex) {
      logger.log(Level.SEVERE, "Error loading properties file.", ex);
    } // END try/catch
    logger.exiting(_CLASS, "loadProperties()");
  }
  
  protected static UserContextImpl getAdminContext() {
    logger.entering(_CLASS, "getAdminContext()");
    logger.exiting(_CLASS, "getAdminContext()", WildDAOImpl.adminContext);
    return WildDAOImpl.adminContext;
  }

  /**
   * Default, no-argument constructor.
   * 
   * The constructor is protected which ensures that it is only visible to
   * classes in the same package.
   */
  protected WildDAOImpl() {
    logger.entering(_CLASS, "WildDAOImpl()");
    logger.exiting(_CLASS, "WildDAOImpl()");
  }  

  /**
   * DAO initialization routine for persistence rules
   */
  /*
  private void loadRules() {
    logger.entering(_CLASS,"loadRules()");
    ClassLoader cl=null;
    InputStream in=null;
    Properties props=null;
    String className=null;
    
    cl=WildDAOImpl.class.getClassLoader();
    in=cl.getResourceAsStream(className);
    if (logger.isLoggable(Level.FINEST)) {
      logger.finest("Attempting to read DAO Configuration File");
    }
    if (in != null) {
      props=new Properties();
      try {
        props.load(in);
      } catch (IOException ex) {
        logger.log(Level.SEVERE,
            "Error loading DAO properties file.",
            ex);
      } // END try/catch      
    } else {
      logger.warning("Unable to locate "+className+".properties file.");
    } // END if (in != null)    
    logger.exiting(_CLASS,"loadRules()");
  }
  */
  /**
   * Checks to see if the specified string is null or zero-length.
   */
  public boolean isEmpty(String value) {
     logger.entering(_CLASS,"isEmpty(String)",value);
     boolean result=false;
     if ((value == null) || (value.length() == 0)) {
        result=true;
     } // END if ((value == null) || (value.length() == 0))
     logger.exiting(_CLASS, "isEmpty(String)",result);
     return result;
  }
  /**
   * Determine if the specified user is a valid user.
   * 
   * @param user
   * @return True if the specified user IS a valid user, otherwise "False" is
   *         returned.
   */
  protected boolean isUserValid(PersistentUser user) {
    logger.entering(_CLASS, "isUserValid(User)", user);
    boolean result = false;
    String userName = null;
    String password = null;

    if (user != null) {
      // The specified user object is NOT NULL
      userName = user.getName();
      password = user.getPassword();
      // *** Let's check to see if the user is the admin user.
      if ((userName.compareTo(WildDAOImpl.adminUserName) == 0)
          && (password.compareTo(WildDAOImpl.adminPassword) == 0)) {
        result = true;
      } else {
        // The specified user IS NOT the admin user.

      } // END if ((userName.compareTo(this.adminUserName) == 0) ...
    } else {
      logger.warning("The user parameter was null.");
    } // END if (user != null)

    logger.exiting(_CLASS, "isUserValid(User)", result);
    return result;
  }
  //********** accessor methods  
  //***** projectionFields
  /**
   * Add a specific property to the list of properties to be returned.
   * 
   * @param fieldName The property to be added to the list of projection fields
   * to be returned by the next query executed by the data access object.
   * @return A boolean value indicating whether or not the property was added
   * to the list.
   */
  public boolean addProjectionField(Property property) {
    logger.entering(_CLASS,"addProjectionField(Property)",property);
    boolean result=false;
    if (property != null) {
      /* The property is not null, so let's check the internal projectionFields 
       * list to see if it is null. */
      if (this.projectionFields == null) {
        // It is, so let's initialize it.
        this.projectionFields=new ArrayList<Property>();
      } // END if (this.projectionFields == null)
      if (!this.projectionFields.contains(property)) {
        /* Only add the item if it does not already exist - properties can only
         * be added once. */
        this.projectionFields.add(property);
        result=true;
      } // END if (!this.projectionFields.contains(property))
    } // END if (property != null)
    logger.exiting(_CLASS,"addProjectionField(String)",result);
    return result;
  }
  /**
   * Removes any currently defined list of <code>projectionFields</code>.
   */
  public void clearProjectionFields() {
    logger.entering(_CLASS,"clearProjectionFields()");
    if (this.projectionFields != null) {
      this.projectionFields.clear(); 
    } // END if (this.projectionFields != null)
    logger.exiting(_CLASS,"clearProjectionFields()");
  }
  /**
   * Returns a reference to the fields in the projection list.
   */
  public List<Property> getProjectionFields() {
    logger.entering(_CLASS,"getProjectionFields()");
    List<Property> immutableList=null;
    if (this.projectionFields == null) {
      this.projectionFields=new ArrayList<Property>();
    } // END if (this.projectionPropertyNames == null)
    immutableList=Collections.unmodifiableList(this.projectionFields);
    logger.entering(_CLASS,"getProjectionFields()",immutableList);
    return immutableList;
  }
  public boolean removeProjectionField(Property property) {
    logger.entering(_CLASS,"removeProjectionField(Property)",property);
    boolean result=false;
    if ((property != null) &&
        (this.projectionFields != null) &&
        (this.projectionFields.contains(property))) {
      this.projectionFields.remove(property);
      result=true;
    } // END if ((fieldName != null) && (this.projectionFields != null) ...
    logger.exiting(_CLASS,"removeProjectionField(String)",result);
    return result;
  }
  /**
   * Replaces the contents of the <code>projectionFields</code>.
   * <p>The <code>projectionFields</code> stored in the data access object are 
   * replaced with the contents of the list passed as a parameter.</p> 
   * @param fields The properties to use in the projection query.
   */
  public void setProjectionFields(List<Property> fields) {
    logger.entering(_CLASS,"setProjectionFields(List<Property>)",fields);
    if (this.projectionFields == null) {
      this.projectionFields=new ArrayList<Property>();
    } else {
      this.projectionFields.clear();
    } // END if (this.projectionPropertyNames == null)
    for (Property property: fields) {
      this.projectionFields.add(property);
    } // END for (Property property: fields)
    logger.exiting(_CLASS,"setProjectionFields(List<Property>)");
  }
  //***** retainProjectionFields
  public boolean isRetainProjectionFields() {
    logger.entering(_CLASS,"isRetainProjectionFields()");
    logger.exiting(_CLASS,"isRetainProjectionFields()",
        this.retainProjectionFields);
    return this.retainProjectionFields;
  }
  public void setRetainProjectionFields(boolean retainProjectionFields) {
    logger.entering(_CLASS, "setRetainProjectionFields(boolean)",
        retainProjectionFields);
    this.retainProjectionFields=retainProjectionFields;
    logger.exiting(_CLASS, "setRetainProjectionFields(boolean)");
  }  
  //********** utility methods
  /**
   * Creates a default, empty instance of an object.
   */
  public abstract W create();

  /**
   * Creates an instance of the object based upon the specified template.
   * 
   */
  public abstract W create(T object, UserContext ctx);
  /**
   * Removes the specified object from the persistent data store.
   */
  @SuppressWarnings("unchecked")
  public W delete(T object, UserContext ctx) throws DAOException {
    logger.entering(_CLASS, "delete(T,UserContext<Key>)", new Object[] {
        object, ctx });
    String identifier = null;
    W wildObject = null;

    if (object != null) {
      if (object instanceof WildObjectImpl) {
        wildObject = (W) object;
        // The object is not null, so let's see if it's been saved.
        identifier = wildObject.getIdentifier();
        if ((identifier != null) && (identifier.length() > 0)) {
          wildObject=deleteByIdentifier(identifier,ctx);
        } else {
          logger.warning("No key, so object hasn't been saved.");
        } // END if ((identifier != null) && (identifier.length() > 0))
      } // END if (object instanceof WildObject)
    } else {
      logger.warning("The alleged object to be deleted is null.");
    } // END if(wildObject != null)

    logger.exiting(_CLASS, "delete(W,UserContext<Key>", wildObject);
    return wildObject;
  }

  public W deleteByIdentifier(String identifier, UserContext ctx) {
    logger.entering(_CLASS,"deleteByIdentifier(String,UserContext)",
        new Object[] {identifier,ctx});
    long id=Long.MIN_VALUE;
    DatastoreService ds = null;
    Key key = null;
    String kind = null;
    W wildObject = null;
    
    if ((identifier != null) &&
        (identifier.length() > 0) &&
        (ctx != null)) {
      wildObject=findByIdentifier(identifier,ctx);
      if (wildObject != null) {
         kind=((WildObjectImpl<?>) wildObject).getKind();
         ds=this.getDatastore();
         id=Long.valueOf(identifier);
         key=KeyFactory.createKey(kind,id);
         // Remove the object by key
         ds.delete(key);
       } // END if (wildObject != null)
      
    } // END if ((identifier != null) && (identifier.length() > 0) && ...
    logger.exiting(_CLASS,"deleteByIdentifier(String,UserContext)",wildObject);
    return wildObject;
  }
  
  /**
   * Returns a reference to the Datastore service.
   */
  protected DatastoreService getDatastore() {
    logger.entering(_CLASS, "getDatastore()");
    DatastoreService ds = null;

    ds = DatastoreServiceFactory.getDatastoreService();
    logger.exiting(_CLASS, "getDatastore()", ds);
    return ds;
  }
  /**
   * Returns the <em>Kind</em> property of the entity which is used for the 
   * purpose of querying the Datastore.
   * 
   * @return A string value which is used by the Datastore for the purpose of
   * categorizing entities of this object's type to provide the ability to 
   * querying the Datastore and retrieve entities. 
   */
  protected abstract String getKind();
  
  /**
   * Saves the specified object to the datastore.
   * 
   * @param object
   * @param ctx
   * @return The persistent version of the datastore.
   * @throws DAOException
   */
  public W save(T object, UserContext ctx) throws DAOException {
    logger.entering(_CLASS, "save(T,UserContext<Key>)", new Object[] {
        object, ctx });
    W savedObject = null;
    
    // Let's make sure the specified object isn't null.
    if (object != null) {
      try {
        savedObject = save(object, ctx, null);        
      } finally {
        /*No-txn
        if (txn.isActive()) {
          txn.rollback();
          logger.severe("Transaction rolling back.");
        } // END if (txn.isActive())
        */
      } // END try/finally block
    } else {
      logger.severe("wildObject parameter is null.");
    } // END if (wildObject != null)
    logger.exiting(_CLASS, "save(T,UserContext<Key>", savedObject);
    return savedObject;
  }

  @SuppressWarnings("unchecked")
  public W save(T object, UserContext ctx, Transaction txn) 
        throws DAOException {
    logger.entering(_CLASS, "save(W,UserContext,Transaction)", new Object[] {
        object, ctx, txn });
    boolean newObject = false;
    Date currentTime = null;
    DatastoreService datastore = null;
    Entity entity = null;
    Key key = null;
    MemcacheService memcache = null;
    String currentUserName=null;
    String identifier = null;
    String kind = "";
    W wildObject = null;

    // Let's make sure the specified object isn't null.
    if (object != null) {
      if (object instanceof WildObject) {
        wildObject=(W) object;        
      } else {
        wildObject=(W) create(object,ctx);                
      } // END if (object instanceof WildObject)
      // Get a reference to the datastore.
      datastore = getDatastore();
      // Record the current time.
      currentTime = new Date();
      // Get the kind.
      kind = ((WildObjectImpl<?>) wildObject).getKind();
      // Get the key
      identifier = wildObject.getIdentifier();
      if (!isEmpty(identifier)) {
        // The key is NOT null, so we're updating an existing object.        
        entity=findEntityByIdentifier(identifier,ctx);
        if (entity == null) {
          entity = new Entity(key);
        } // END if (entity == null) 
      } else {
        // The key is null, so we're saving a new object.
        newObject = true;
        entity = new Entity(kind);
      } // END if (key != null)
      // Populate the entity
      ((WildObjectImpl<?>) wildObject).populateEntity(entity);
      currentUserName=ctx.getUserName();
      if (newObject) {
        // New object so let's populate the date/user creation information.
        entity.setProperty("dateCreated", currentTime);
        entity.setProperty("createdBy",currentUserName);        
      } // END if (newObject)
      // Modifying the entity, let's set the date/user modification info 
      entity.setProperty("dateModified", currentTime);
      entity.setProperty("modifiedBy",currentUserName);
      // Put the object in the datastore
      key = datastore.put(entity);
      // Update the object to indicate the identifier that was assigned.
      if (newObject) {
        ((WildObjectImpl<?>) wildObject).setIdentifier(
            String.valueOf(key.getId()));        
      } // END if (newObject)
      
      /* Have the wild object re-populate itself from the entity.
       * At a minimum this will cause the create/modification fields to be
       * re-populated. 
       */
      ((WildObjectImpl<?>) wildObject).populateFromEntity(entity, ctx);
      // Let's add the entity to the memcache
      memcache = MemcacheServiceFactory.getMemcacheService();
      memcache.put(key, entity);      
      
    } else {
      logger.severe("wildObject parameter is null.");
    } // END if (wildObject != null)
    logger.exiting(_CLASS, "save(W,UserContext,Transaction)", wildObject);
    return wildObject;
  }

  public List<W> findAll(UserContext ctx) throws DAOException {
    logger.entering(_CLASS, "findAll(UserContext<Key>)", ctx);
    List<W> objectList = null;
    com.google.appengine.api.datastore.Query gQuery=null;
    QueryWrapper qw=null;
    String kind = null;
    String msg=null;
    StringBuilder sb=null;
    W persistentObject = null;

    objectList = new ArrayList<W>();

    if ((ctx != null) && (ctx.isAuthenticated())) {
      persistentObject = (W) create();
      kind = ((WildObjectImpl<?>) persistentObject).getKind();
      gQuery = new com.google.appengine.api.datastore.Query(kind);
      qw=new QueryWrapper(gQuery);
      objectList=findByQuery(qw,ctx);
    } else {
      // The specified context was either null or has not been authenticated.
      if (ctx == null) {
        msg=("The UserContext parameter was null.");        
      } else if (! ctx.isAuthenticated()) {
        sb=new StringBuilder(80);
        sb.append("The specified UserContext, ").append(ctx.getUserName());
        sb.append(", is not authenticated.");
        msg=sb.toString();        
      } // END if (ctx == null)
      logger.fine(msg);      
      
    } // END if ((ctx != null) && (ctx.isAuthenticated()))

    logger.exiting(_CLASS, "findAll(UserContext<Key>)", objectList);
    return objectList;
  }
  
  /**
   * Returns an Entity object based upon the specified identifier.
   * @param identifier
   * @param ctx
   * @return
   * @throws DAOException
   */
  protected Entity findEntityByIdentifier(String identifier, UserContext ctx) 
  throws DAOException {
    logger.entering(_CLASS, "findEntityByIdentifier(String,UserContext)",
        new Object[] { identifier, ctx });
    long longId = 0l;
    DatastoreService datastore = null;
    Entity entity = null;
    Key key = null;
    MemcacheService memcache = null;
    String kind = null;
    W persistentObject = null;
    
    if (identifier != null) {
      // Build the key
      persistentObject = (W) create();
      kind = ((WildObjectImpl<?>) persistentObject).getKind();
      longId = Long.valueOf(identifier);
      key = KeyFactory.createKey(kind, longId);
      // Obtain a reference to the Memcache service.
      memcache = MemcacheServiceFactory.getMemcacheService();
      if (memcache.contains(key)) {
        entity = (Entity) memcache.get(key);
      } else {
        datastore = getDatastore();
        try {
          entity = datastore.get(key);
        } catch (EntityNotFoundException ex) {
          logger.log(Level.SEVERE,
              "Unable to find the entity for the specified key.", ex);
        } // END try/catch
      } // END if (memcache.contains(key))
    } else {
      logger.severe("Identifier property is null.");
    }
    logger.exiting(_CLASS, "findEntityByIdentifier(String,UserContext)", 
        entity);
    return entity;
  }

  public W findByIdentifier(String identifier, UserContext ctx)
      throws DAOException {
    logger.entering(_CLASS, "findByIdentifier(String,UserContext)",
        new Object[] { identifier, ctx });
    Entity entity = null;
    String msg=null;
    StringBuilder sb=null;
    W wildObject = null;
    
    if ((identifier != null) && (ctx != null) && (ctx.isAuthenticated())) {
      entity=findEntityByIdentifier(identifier,ctx);
      if (entity != null) {
        // If the entity was found, build the requisite object
        wildObject = create();
        ((WildObjectImpl<?>) wildObject).populateFromEntity(entity, ctx);
      } // END if (entity != null)
    } else {
      sb=new StringBuilder(80);
      if (identifier == null) { 
        sb.append("The identifier parameter was null.");
      } // END if (identifier == null)
      if (ctx == null) {
        if (sb.length() > 0) sb.append(" ");
        sb.append("The UserContext is null.");
      } else {
        if (sb.length() > 0) sb.append(" ");
        sb.append("The specified UserContext, ").append(ctx.getUserName());
        sb.append(", is not authenticated.");
        msg=sb.toString();                
      } // END if (ctx == null)
      logger.severe(msg); 
    } // END if ((identifier != null) && (ctx != null)&&(ctx.isAuthenticated()))

    logger.exiting(_CLASS, "findByIdentifier(String,UserContext)", wildObject);
    return wildObject;
  }
  
  /**
   * Returns a list of type-specific objects from a given query.
   * 
   * <p>The <code>Query</code> object is a Google App Engine specific entity 
   * that is used to search for data in the <code>DataStore</code>.  The 
   * <code>UserContext</code> is used to ensure that the user requesting the
   * data has access to see the results of the query.  A variable series of 
   * parameters will follow the specified <code>UserContext</code> and are used
   * in managing the amount of data returned as part of a given query.</p>
   * <h1>Variable Arguments</h1>
   *  
   * 
   * @param query The query to be used to search for the specified objects.
   * @param ctx The credentials that should be used to access persistent data.
   * @param args Variable set of optional arguments that will be used to
   * manage the number of records returned.
   * @return
   */
  @SuppressWarnings("unchecked")
  protected List<W> _findByQuery(QueryWrapper query, UserContext ctx) {
    logger.entering(_CLASS, "findByQuery(Query)", query);
    int pageNumber=0;
    int pageSize=0;
    DatastoreService datastore = null;
    List<Entity> entities=null;
    List<SortCriterion> sortCriteria=null;
    List<W> results = null;
    PreparedQuery pQuery = null;
    Property property=null;
    com.google.appengine.api.datastore.Query gQuery=null;
    com.google.appengine.api.datastore.Query.SortDirection sortDirection=null;
    String propertyName=null;
    W element = null;

    if ((query != null) && (ctx != null)) {
      // Let's check to ensure the specified user is an authenticated user.
      if (ctx.isAuthenticated()) {
        // The user is authenticated, so continue.
        //********** Let's start initializing things we'll need...
        // Obtain a reference to the datastore.
        datastore = DatastoreServiceFactory.getDatastoreService();
        // Obtain a reference to Memcache.
        //cache = MemcacheServiceFactory.getMemcacheService();
        // Get the sortCriteria to use while retrieving data.
        sortCriteria=getSortCriteria();
        // Get the pageNumber to determine which results to return.
        pageNumber=getPageNumber();
        // Get the number of objects to return when building the results.
        pageSize=getPageSize();
        //********** Let's get to work
        //***** Prepare the Datastore Query
        // Get the wrapped Query object.
        gQuery=query.getQuery();
        /* Set a flag on the query object to ONLY return keys IF there are NO
         * sort criteria. */
        /*
        if (sortCriteria == null) {
          gQuery.setKeysOnly();
        } // END if (sortCriteria == null)
        */
        
        // Let's add the sorting.
        for (SortCriterion criterion: sortCriteria) {
          property=criterion.getProperty();
          if (property != null) {
            propertyName=property.getName();
            if ((propertyName != null) && (propertyName.length() > 0)) {
              switch (criterion.getSortOrder()) {
              case DESCENDING:
                sortDirection=
                  com.google.appengine.api.datastore.Query.SortDirection.DESCENDING;
                break;
              default:
                sortDirection=
                  com.google.appengine.api.datastore.Query.SortDirection.ASCENDING;
                break;
              } // END switch (criterion.getSortOrder())
              
              /* The property is NOT null, so let's add the sort in the order
               * they were specified. */
              gQuery.addSort(propertyName,sortDirection);
              
              /* Fields used in any sort operation MUST be included in a 
               * projection query.
               */
              if (
                  (this.projectionFields != null) && 
                  (this.projectionFields.size() > 0)
                  ) {
                // There are projection fields declared.
                if (!projectionFields.contains(property)) {
                  addProjectionField(property);
                } // END if (!projectionFields.contains(propertyName))
              } // END if ((projectionFields != null) ....              
            } // END if ((propertyName != null) && (propertyName.length() > 0))
          } // END 
        } // END for (SortCriterion criterion: sortCriteria)
        
        // Let's look to see if this is a projection query
        if ((this.projectionFields != null) && 
            (this.projectionFields.size() > 0)) {
          for (Property prop: this.projectionFields) {
            gQuery.addProjection(
                new PropertyProjection(prop.getName(),
                    prop.getType()));                
          } // END for (Property prop: this.projectionFields)
        } // END if ((this.projectionFields != null) ...
        // Prepare the query for execution.
        pQuery = datastore.prepare(gQuery);
        
        /* Fetch the results as a List<Entity>
         * Specifying the page size and offset to use when retrieving the data.
         */
        entities=pQuery.asList(withLimit(pageSize).offset(pageSize*pageNumber));
        
        /* Now let's build the results ordering based upon how the query was
         * returned. */
        results=new ArrayList<W>();
        for (Entity entity: entities) {
          // Create an instance of the object to be returned.
          element=create();
          // Populate that object with data.
          ((WildObjectImpl<T>) element).populateFromEntity(entity, ctx);
          // Add the populated object to the list.
          results.add(element);
        } // END for (Entity entity: entities)        
      } else {
        logger.warning("The UserContext has not been authenticated.");
      } // END if (ctx.isAuthenticated())
    } else {
      if (query == null) logger.warning("The Query parameter is null.");
      if (ctx == null) logger.warning("The UserContext parameter is null.");
    } // END if (query != null)
    
    // Lets check to see if the projectionFields should be cleared
    if ((!this.retainProjectionFields) && (this.projectionFields != null)) {
      /* The retainProjectionFields property is false and the 
       * projectionFields list is not null, so clear it. */
      this.projectionFields.clear();
    } // END if ((!this.retainProjectionFields) ...
        
    // If no data was found, return an empty array
    if (results == null) {
      results = new ArrayList<W>(0);
    } // END if (results == null)
    
    logger.exiting(_CLASS, "findByQuery(Query)", results);
    return results;
  }
  
  public List<W> findByQuery(Query query, UserContext ctx) {
    logger.entering(_CLASS,"findByQuery(Query,UserContext)",
        new Object[] {query,ctx});
    List<W> list=null;
    if (query instanceof QueryWrapper) {
      list=_findByQuery((QueryWrapper)query,ctx);
    } else {
      logger.severe("The Query object is NOT an instance of QueryWrapper.");
    } // END if (query instanceof QueryWrapper)
    logger.exiting(_CLASS,"findByQuery(Query,UserContext)",list);
    return list;
  }
  
  //***** pageNumber
  public int getPageNumber() {
    logger.entering(_CLASS,"getPageNumber()");
    logger.exiting(_CLASS,"getPageNumber()",this.pageNumber);
    return this.pageNumber;
  }
  public void setPageNumber(int pageNumber) {
    logger.entering(_CLASS,"setPageNumber(int)",pageNumber);
    if (this.pageNumber < 0) {
      this.pageNumber = 0;
    } else {
      this.pageNumber=pageNumber;
    } // END if (this.pageNumber < 0)
    logger.exiting(_CLASS,"setPageNumber(int)");
  }

  //***** pageSize
  public int getPageSize() {
    logger.entering(_CLASS, "getPageSize()");
    logger.exiting(_CLASS, "getPageSize()",this.pageSize);
    return this.pageSize;
  }
  public void setPageSize(int pageSize) {
    logger.entering(_CLASS,"setPageSize(int)",pageSize);
    if (pageSize <= 0 ) {
      this.pageSize=WildDAOImpl.DEFAULT_PAGE_SIZE;
    } else {
      this.pageSize=pageSize;
    } // END if (pageSize <= 0 ) 
    logger.exiting(_CLASS,"setPageSize(int)");
  }
  
  //***** sortCriteria
  public List<SortCriterion> getSortCriteria() {
    logger.entering(_CLASS,"getSortCriteria()");
    if (this.sortCriteria == null) {
      this.sortCriteria = new ArrayList<SortCriterion>();
    } // END if (this.sortCriteria == null)
    logger.exiting(_CLASS,"getSortCriteria()",this.sortCriteria);
    return this.sortCriteria;
  }
  public void setSortCriteria(List<SortCriterion> sortCriteria) {
    logger.entering(_CLASS,"setSortCriteria(List<SortCriterion>)",sortCriteria);
    if (sortCriteria == null) {
      if (this.sortCriteria != null) {
        // Clear the sort criteria list.
        this.sortCriteria.clear();
      } else {
        // Use the specified sortCriteria.
        this.sortCriteria=new ArrayList<SortCriterion>();
      } // END if (this.sortCriteria != null)
    } else {
      this.sortCriteria=sortCriteria;
    } // END if (sortCriteria == null)
    logger.exiting(_CLASS,"setSortCriteria(List<SortCriterion>)");
  }  
}