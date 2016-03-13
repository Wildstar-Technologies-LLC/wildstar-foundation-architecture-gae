/*
 * Copyright (c) 2013 - 2015 Wildstar Technologies, LLC.
 *
 * This file is part of Wildstar Foundation Architecture for Google App Engine.
 *
 * Wildstar Foundation Architecture for Google App Engine is free software: you
 * can redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either version
 * 3 of the License, or (at your option) any later version.
 *
 * Wildstar Foundation Architecture for Google App Engine is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Wildstar Foundation Architecture for Google App Engine.  If not, see 
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

import com.google.appengine.api.datastore.Query;

public class QueryWrapper implements com.wildstartech.wfa.dao.Query {
  private static final String _CLASS=QueryWrapper.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  
  private Query query=null;
  
  /**
   * Default, no-argument constructor.
   */
  public QueryWrapper() {
    logger.entering(_CLASS,"QueryWrapper()");
    logger.exiting(_CLASS,"QueryWrapper()");
  }
  /**
   * Constructor taking a Datastore Query object as a paramter.
   * @param query The object wrapped by this class.
   */
  public QueryWrapper(Query query) {
    logger.entering(_CLASS,"QueryWrapper(Query)",query);
    this.query=query;
    logger.exiting(_CLASS,"QueryWrapper(Query)");
  }
  
  //***** query
  public Query getQuery() {
    logger.entering(_CLASS,"getQuery()");
    logger.exiting(_CLASS,"getQuery()",this.query);
    return this.query;
  }
  public void setQuery(Query query) {
    logger.entering(_CLASS,"setQuery(Query)",query);
    this.query=query;
    logger.exiting(_CLASS,"setQuery(Query)");
  }
}
