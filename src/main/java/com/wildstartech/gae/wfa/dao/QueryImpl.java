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

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.wildstartech.wfa.dao.Filter;
import com.wildstartech.wfa.dao.PropertyFilter;
import com.wildstartech.wfa.dao.Query;

public class QueryImpl implements Query {
   private static final String _CLASS=QueryImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   private List<Filter> filters=null;
   
   public QueryImpl() {
      logger.entering(_CLASS, "QueryImpl()");
      init();
      logger.exiting(_CLASS, "QueryImpl()");
   }
   
   private void init() {
      logger.entering(_CLASS, "init()");
      this.filters=new ArrayList<Filter>();
      logger.exiting(_CLASS, "init()");
   }
   
   //***** Filter
   @Override
   public boolean addFilter(Filter filter) {
      logger.entering(_CLASS, "addFilter(Filter)",filter);
      boolean result=false;
      logger.exiting(_CLASS, "addFilter(Filter)",result);
      return result;
   }
   @Override
   public List<Filter> addFilters(List<Filter> filters) {
      logger.entering(_CLASS, "addFilter(List<Filter>)",filters);
      List<Filter> returnFilters=null;
      logger.exiting(_CLASS, "addFilter(List<Filter>)",returnFilters);
      return returnFilters;
   }
   @Override
   public void clearFilters() {
      logger.entering(_CLASS,"clearFilters()");
      
      this.filters.clear();
      
      logger.exiting(_CLASS,"clearFilters()");
   }
   @Override
   public List<Filter> getFilters() {
      logger.entering(_CLASS, "getFilters()");
      List<Filter> returnList=null;
      
      logger.exiting(_CLASS, "getFilters()",returnList);
      return returnList;
   }
   @Override
   public boolean removeFilter(Filter filter) {
      logger.entering(_CLASS, "removeFilter(Filter)",filter);
      boolean result=false;
      logger.exiting(_CLASS, "removeFilter(Filter)",result);
      return result;
   }
   @Override
   public List<Filter> removeFilters(List<Filter> filters) {
      logger.entering(_CLASS,"removeFilters(List<Filter>)",filters);
      List<Filter> filtersRemoved=null;
      logger.exiting(_CLASS,"removeFilters(List<Filter>)",filtersRemoved);
      return filtersRemoved;
   }   
}