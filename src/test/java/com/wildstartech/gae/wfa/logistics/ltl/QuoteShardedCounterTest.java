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
package com.wildstartech.gae.wfa.logistics.ltl;

import org.testng.annotations.Test;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.wildstartech.gae.wfa.dao.DAOTest;
import com.wildstartech.gae.wfa.dao.logistics.ltl.QuoteShardedCounter;

public class QuoteShardedCounterTest extends DAOTest {
  
  
  @Test
  public void setupCounter() {
    QuoteShardedCounter counter=null;
    long nextId=Long.MIN_VALUE;
    
    counter=new QuoteShardedCounter();
    nextId=counter.nextId();
    assert nextId == 1;        
  } 
  
  class ThreadHelper implements Runnable {
    QuoteShardedCounter counter=null;
    public ThreadHelper(QuoteShardedCounter counter) {
      this.counter=counter;
    }
    public void run() {
      long nextId=0l;
      StringBuilder sb=new StringBuilder(15);
      for (int i=0; i < 100; i++) {
        nextId=counter.nextId();
        sb.append(nextId);
        sb.append(" [").append(Thread.currentThread().getName());
        System.out.println(sb.toString());
        sb.delete(0,sb.length());
      } // END for (int i=0; i < 100; i++) 
    }
  }
  @Test(dependsOnMethods = {"setupCounter"})
  public void multipleInvocations() {
    QuoteShardedCounter counter=new QuoteShardedCounter();
    ThreadHelper helper=null;
    Thread thread=null;
    
    for (int i=0; i < 10; i++) {
      helper=new ThreadHelper(counter);
      thread=ThreadManager.createBackgroundThread(helper);
      thread.start();
    } // END for (int i=0; i < 10; i++)
  }  
  
  @Test(dependsOnMethods = {"setupCounter", "multipleInvocations"})
  public void report() {
    DatastoreService datastore=null;
    Key key=null;
    PreparedQuery pQuery=null;
    Query query=null;
    QuoteShardedCounter counter=null;
    String shardIndexKind=null;
    String shardKind=null;
    
    datastore=DatastoreServiceFactory.getDatastoreService();
    counter=new QuoteShardedCounter();
    shardKind=counter.getShardKind();
    shardIndexKind=QuoteShardedCounter.SHARD_INDEX_KIND;
    key=KeyFactory.createKey(shardIndexKind, shardKind);
    query=new Query(shardIndexKind,key);
    pQuery=datastore.prepare(query);
    
    System.out.println("Shard Counter Information\n=========================");
    for (Entity entity: pQuery.asIterable()) {
      System.out.print("Kind: {"+entity.getKind());
      System.out.print(entity.getKey());
      System.out.print("} (");
      System.out.print(entity.getKey().getId());
      System.out.print("): ");
      System.out.println(entity.getProperty(
          QuoteShardedCounter.NUMBER_OF_SHARDS_KEY));      
    } // END for (Entity entity: pQuery.asIterable())
    
    // List counter shards
    query=new Query(shardKind);
    pQuery=datastore.prepare(query);
    System.out.println("Listing Shards\n=============");
    for (Entity entity: pQuery.asIterable()) {
      System.out.print("Kind: "+entity.getKind());
      System.out.print("(");
      System.out.print(entity.getKey().getId());
      System.out.print("): ");
      System.out.println(entity.getProperty(QuoteShardedCounter.COUNTER_KEY));      
    } // END for (Entity entity: pQuery.asIterable())
  }  
}