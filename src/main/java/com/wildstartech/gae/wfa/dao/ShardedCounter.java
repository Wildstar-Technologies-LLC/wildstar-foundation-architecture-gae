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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * A counter capable of being incremented rapidly.
 *
 * Capable of incrementing the counter and increasing the number of shards. When
 * incrementing, a random shard is selected to prevent a single shard from being
 * written too frequently. If increments are being made too quickly, increase
 * the number of shards to divide the load. Performs DATASTORE operations using
 * the low level DATASTORE API.
 */
public abstract class ShardedCounter<T extends Object> {
   /* The name of the class to be used for logging purposes. */
   private static final String _CLASS = ShardedCounter.class.getName();
   /* Static reference to the class-level logging. */
   private static final Logger logger = Logger.getLogger(_CLASS);
   /** Property used to store the counter value stored in individual shards. */
   public static final String COUNTER_KEY = "counter";
   /* Static reference to the DATASTORE service */
   private static final DatastoreService DATASTORE = DatastoreServiceFactory
         .getDatastoreService();
   /** The initial number of shards that will be created for a given KIND. */
   public static final int INITIAL_SHARD_COUNT = 5;
   /** The prefix that will be used **/
   public static final String KIND_PREFIX = "__SHARDEDCOUNTER__";
   /** Set a maximum number of shards that can be created for a given KIND. */
   public static final int MAX_SHARD_COUNT = 20;
   /** The amount of time for which a value should be cached. */
   public static final int MEMCACHE_PERIOD = 60;
   /** Property used to store the number of a particular instance of shards. */
   public static final String NUMBER_OF_SHARDS_KEY = "numberOfShards";
   /** The Kind used to store ShardCounter instances. **/
   public static final String SHARD_INDEX_KIND = 
         "com.wildstartech.gae.ShardedCounter";
   
   /* Reference to the Memcache service instance */
   private final MemcacheService memcache = 
         MemcacheServiceFactory.getMemcacheService();
   /* Used in randomly selecting a given shard. */
   private final Random random = new Random();
   /* The kind for which the shard is used as a counter. */
   private String kind = null;
   /*
    * An identifier used for storing instances of this ShardedCounter. This
    * value will adhere to the following pattern: KIND_PREFIX + kind
    * 
    * For example, with this class definition, the value of "KIND_PREFIX" is a
    * <code>static</code> <code>String</code> with a value of
    * "__SHARDEDCOUNTER__". If the value of the <code>kind</code> instance field
    * is <code>com.wildstartech.wfa.customer.Customer</code> then the value of
    * the <code>shardKind</code> field will be as follows:
    * 
    * <code>__SHARDEDCOUNTER__com.wildstartech.wfa.customer.Customer</code>
    */
   private String shardKind = null;

   /**
    * Default, no-argument constructor.
    */
   public ShardedCounter() {
      logger.entering(_CLASS, "ShardedCounter()");
      init();
      logger.exiting(_CLASS, "ShardedCounter()");
   }

   /*
    * Initialization routine for the <code>ShardedCounter</code>.
    * 
    * <p>The <code>ShardedCounter</code> is an <code>abstract</code> class which
    * means that in order to create an instance, it must be sub-classed. Given
    * the <code>ShardedCounter</code> features a generic signature, a type
    * parameter <em>should</em> be passed when it is sub-classed. This method
    * uses reflection to identify the type parameter that was specified.</p>
    */
   private void init() {
      Class<?> _class = null;
      Class<?> _superClass = null;
      Type type = null;
      Type[] typeArguments = null;

      _class = this.getClass();
      _superClass = _class.getSuperclass();
      // *** Let's navigate the class hierarchy...
      while (_superClass != ShardedCounter.class) {
         _class = _superClass;
         _superClass = _class.getSuperclass();
      } // while(_superClass != PersistentRuleSet.class)

      // Get the generic super class which should be this class.
      type = _class.getGenericSuperclass();
      if (type instanceof ParameterizedType) {
         // The type is a generic type...
         typeArguments = ((ParameterizedType) type).getActualTypeArguments();
         if ((typeArguments != null) && (typeArguments.length == 1)) {
            type = typeArguments[0];
            _class = (Class<?>) type;
            this.kind = _class.getCanonicalName();
            this.shardKind = KIND_PREFIX + this.kind;
         } else {
            if (typeArguments == null) {
               logger.severe(
                  "ShardedCounter init() failed to get actual type arguments.");
            } else {
               logger.severe("Unexpected number of type arguments found.");
            } // END if (typeArguments == null)
         } // END if ((typeArguments != null) && (typeArguments.length == 4))
      } else {
         logger.severe(
               "ShardedCounter init() Failed to find ParameterizedType.");
      } // END if (type instanceof ParameterizedType)
   }

   // ***** Accessor methods
   /**
    * Identifies the type of object the <code>ShardedCounter</code> is used for.
    * 
    * <p>
    * Invocation of the <code>getKind()</code> method results in a
    * <code>String</code> being returned that identifies the canonical name of
    * the object for which the counter will be used to generate identifiers. For
    * example, if <code>getKind()</code> returns
    * <code>com.wildstartech.wfa.customer.Customer</code> then it should be
    * inferred that this sub-class of <code>ShardedCounter</code> is used in the
    * process of persisting instances of
    * <code>com.wildstartech.wfa.customer.Customer</code> objects.
    * </p>
    * 
    * @return the kind of object with which the <code>ShardedCounter</code>
    *         should be used.
    */
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()", this.kind);
      return this.kind;
   }

   /**
    * Identifies the <code>Kind</code> for this <code>ShardedCounter</code>.
    * 
    * @return a <code>String</code> value that represents the <code>Kind</code>
    *         of <code>Entity</code> used to store data for this
    *         <code>ShardedCounter</code>.
    */
   public String getShardKind() {
      logger.entering(_CLASS, "getShardKind()");
      logger.exiting(_CLASS, "getShardKind()", this.shardKind);
      return this.shardKind;
   }

   // ***** utility methods
   /**
    * Add a shardCounter.
    */
   private void addShard(int numToAdd) {
      logger.entering(_CLASS, "addShard(int)", numToAdd);
      int maxShards = 0;
      int numShards = 0;
      Entity entity = null;
      Entity shardIndex = null;
      Key key = null;
      Transaction tx = null;

      if (numToAdd > 0) {
         // The numToAdd value is greater than zero, so sallyforth.
         try {
            /*
             * Create a key for the ShardedCounter instance that we use to track
             * the number of shards of a given type.
             */
            key = KeyFactory.createKey(SHARD_INDEX_KIND, this.shardKind);
            /*
             * Get the entity that stores the count of the number of shards that
             * exist for a given counter.
             */
            try {
               shardIndex = ShardedCounter.DATASTORE.get(key);
            } catch (EntityNotFoundException ex) {
               logger.log(Level.INFO,
                     "The ShardedCounter Entity for the shardKind couldn't be found.",
                     ex);
               shardIndex = new Entity(key);
               shardIndex.setUnindexedProperty(NUMBER_OF_SHARDS_KEY, 0);
            } // END try/catch
              // Get the current number of shards.
            numShards = (Integer) shardIndex.getProperty(NUMBER_OF_SHARDS_KEY);
            // Let's go from numShards to numShards + numToAdd
            maxShards = numShards + numToAdd;
            // Let's make sure we don't go over.
            if (maxShards >= MAX_SHARD_COUNT) {
               maxShards = MAX_SHARD_COUNT;
            } // END if ((numShards + numToAdd) > _MAX_SHARD_COUNT)

            // Set the ShardedCounter "index" entity into the DATASTORE.
            // ** Saving the shardIndex
            // Create the transaction.
            tx = ShardedCounter.DATASTORE.beginTransaction();
            shardIndex.setUnindexedProperty(NUMBER_OF_SHARDS_KEY, maxShards);
            // Store the index ShardedCounter in the DATASTORE.
            ShardedCounter.DATASTORE.put(shardIndex);
            // Commit the transaction
            tx.commit();
            // ** Done saving the shardIndex

            // Lets add the shards to the DATASTORE.
            for (int counter = numShards + 1; counter <= maxShards; counter++) {
               // Create a new key.
               key = KeyFactory.createKey(shardKind, new Long(counter));
               // Create the new Entity
               entity = new Entity(key);
               // Populate the new entity
               entity.setUnindexedProperty(COUNTER_KEY, new Long(0));
               // Save the entity.
               ShardedCounter.DATASTORE.put(entity);
            } // END for (int counter=numShards+1; counter <= maxShards;
              // counter++)
         } catch (Exception ex) {
            logger.log(Level.SEVERE, "Some Type of Exception", ex);
         } finally {
            if ((tx != null) && (tx.isActive())) {
               tx.rollback();
            } // END if ((tx != null) && (tx.isActive())
         } // END try/catch
      } // END if (numToAdd > 0)
      logger.exiting(_CLASS, "addShard(int)");
   }
   
   /**
    * Returns sum of all defined instances of this <code>ShardedCounter</code>.
    * 
    * @return A <code>long</code> value that represents the sum of the values
    *         taken from the <code>count</code> property of every
    *         <code>Entity</code> in the <code>Datastore</code> that features a
    *         <code>kind</code> equal to the value stored in the
    *         <code>shardKind</code> field.
    */
   public long getCount() {
      logger.entering(_CLASS, "getCount()");
      boolean result=false;
      long count = Long.MIN_VALUE;
      Object obj=null;
      Object propValue = null;
      PreparedQuery pQuery = null;
      Query query = null;
      String shardKind = null;
      StringBuilder msg=null;

      // Let's check the cache.
      shardKind = getShardKind();
      obj=this.memcache.get(shardKind);
      if (obj != null) {
         if (obj instanceof Long) {
            count=(Long) obj;
         } // END if (obj instanceof Long) 
      } else {
         count=Long.MIN_VALUE;
      } // END if (obj != null)
      
      /* Count will NOT be equal to Long.MIN_VALUE if it was in memcache, so
       * let's start counting. */
      if (count == Long.MIN_VALUE) {
         count=0l;
         // Create the query
         query = new Query(getShardKind());
         // Prepare the query for execution
         pQuery = ShardedCounter.DATASTORE.prepare(query);
         for (Entity entity : pQuery.asIterable()) {
            propValue = entity.getProperty(COUNTER_KEY);
            if (propValue != null) {
               count += (Long) propValue;
            } // END if (propValue != null)
         } // END for (Entity entity: pQuery.asIterable())
         // Store the resulting count in memcache
         result=this.memcache.put(
               shardKind,
            count,
            Expiration.byDeltaSeconds(MEMCACHE_PERIOD),
            SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
         // This is for logging purposes only.
         if (logger.isLoggable(Level.FINEST)) {
            msg=new StringBuilder();
            msg.append("The ");
            msg.append(shardKind);
            if (result) {
               msg.append(" memcache entry was stored.");
            } else {
               msg.append(" memcache entry was NOT stored.");
            } // END if (result)
         } // END if (logger.isLoggable(Level.FINEST))         
      } if (count != Long.MIN_VALUE) 
      logger.exiting(_CLASS, "getCount()", count);
      return count;
   }

   /**
    * Returns the count of the number of shards that exist for a counter.
    */
   public int getShardCount() {
      logger.entering(_CLASS, "getShardCount()");
      int shardCount = ShardedCounter.INITIAL_SHARD_COUNT;
      Entity entity = null;
      Key key = null;

      key = KeyFactory.createKey(SHARD_INDEX_KIND, this.shardKind);
      try {
         entity = ShardedCounter.DATASTORE.get(key);
         shardCount = ((Long) entity
               .getProperty(ShardedCounter.NUMBER_OF_SHARDS_KEY)).intValue();
      } catch (EntityNotFoundException ex) {
         logger.log(Level.SEVERE, "Error Returing Shard Count", ex);
         addShard(INITIAL_SHARD_COUNT);
      } // END try/catch

      logger.exiting(_CLASS, "getShardCount()", shardCount);
      return shardCount;
   }

   /**
    * Increments the counter by one.
    * 
    * @return
    */
   private void increment() {
      logger.entering(_CLASS, "increment()");
      int shardCount = 0;
      int shardKey=0;
      long counter = 0;
      Long memcacheValue=Long.MIN_VALUE;
      Entity entity = null;
      Key key = null;
      String shardKind = null;
      StringBuilder msg=null;
      Transaction tx = null;

      try {
         // Get a count of the numbers of shards.
         shardCount = getShardCount();
         // Pick one of the existing shards at random.
         shardKey = this.random.nextInt(shardCount);
         // Check to see if the number of shards is zero
         if (shardKey == 0) {
            // Keys cannot be zero
            shardKey = 1;
         } // END if (shardNumber == 0)
         
         // Get the shardKind
         shardKind = getShardKind();
         if (! this.memcache.contains(shardKind)) {
            // The current count is NOT cached, so grab it.
            getCount();
         } // if (!this.memcache.contains(shardKind))
         
         // Begin the transaction.
         tx = ShardedCounter.DATASTORE.beginTransaction();
         key = KeyFactory.createKey(shardKind, new Long(shardKey));
         try {
            // Let's get the counter entity
            entity = ShardedCounter.DATASTORE.get(tx, key);
            counter = (Long) entity.getProperty("counter");
         } catch (EntityNotFoundException ex) {
            // The entity didn't exist, so let's create a new one.
            msg=new StringBuilder(80);
            msg.append("Unable to locate ").append(shardKind);
            msg.append(" instance # ").append(shardKey).append(".");
            logger.log(
                  Level.WARNING,
                  msg.toString(),
                  ex);
            entity = new Entity(key);
         } // END try/catch
         // Increment the counter
         counter++;
         // Store the updated counter in the Entiy object
         entity.setUnindexedProperty("counter", counter);
         // Put the entity back into the datastore.
         ShardedCounter.DATASTORE.put(tx, entity);
         // Commit the transaction
         tx.commit();
         
         /* Perform atomic increment of the count in memcache because we just 
          * incremented the counter. */
         memcacheValue=this.memcache.increment(shardKind, 1);
         
         // Check the memory cache
         if (memcacheValue == null) {
            logger.finest("memcacheValue is null - nothing to increment.");           
         } else {
            logger.finest("memcacheValue is NOT null.  Incremented.");            
         } // END if (memcacheValue == null)         
      } catch (ConcurrentModificationException ex) {
         logger.log(Level.SEVERE, 
               "ConcurrentModificationException Thrown.",
               ex);
      } finally {
         if (tx.isActive()) {
            tx.rollback();
         } // END if (tx.isActive())
      } // END try/catch
      logger.exiting(_CLASS, "increment()");
   }
   
   /**
    * Return the next value for the <code>ShardedCounter</code>.
    */
   public long nextId() {
      logger.entering(_CLASS, "nextId()");
      long nextId = Long.MIN_VALUE;
      
      increment();
      nextId=getCount();
      
      logger.exiting(_CLASS, "nextId()", nextId);
      return nextId;
   }
}