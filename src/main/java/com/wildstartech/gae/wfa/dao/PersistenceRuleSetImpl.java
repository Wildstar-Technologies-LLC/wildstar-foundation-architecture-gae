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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wildstartech.wfa.dao.PersistenceRule;
import com.wildstartech.wfa.dao.PersistenceRuleSet;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.WildDAO;
import com.wildstartech.wfa.dao.WildObject;
import com.wildstartech.wfa.rules.Result;

/**
 * Extension of the <code>RuleSet&lt;K&gt;</code> 
 * @author Derek Berube, Wildstar Technologies, LLC.
 *
 * @param <F> A sub-class of the <code>WildDAOFactory</code> which is used to
 * provide access to the Data Access Object used to access the persistent data 
 * store for objects of type <code>W</code>. 
 * 
 * @param <D> A sub-class of the <code>WildDAO</code> which is used to access 
 * the persistent data store for the <code>W</code> objects.
 * @param <T>
 * @param <W>
 */
public abstract class PersistenceRuleSetImpl<D extends WildDAO<T,W>, T, W extends WildObject> 
implements PersistenceRuleSet<D, T, W> {
  private static final String _CLASS=PersistenceRuleSetImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  
  private List<PersistenceRule<D, T, W>> rules=null;  
   
  /**
   * Default, no-argument constructor.
   */
  public PersistenceRuleSetImpl() {
    logger.entering(_CLASS,"PersistentRuleSet()");
    this.rules=new ArrayList<PersistenceRule<D, T, W>>();
    // Perform initialization routines. 
    init();
    logger.exiting(_CLASS,"PersistentRuleSet()");
  }
  
  /*
   * Default initialization routine.
   * <p>The purpose of this method will be to use reflection to identify the 
   * actual types that are assigned to the type parameters when this class is 
   * sub-classed.</p>
   */
  private void init() {
    logger.entering(_CLASS,"init()");
    Class<?> _class=null;
    Class<?> _superClass=null;
    Type type=null;
    Type[] typeArguments=null;
    
    _class=getClass();
    _superClass=_class.getSuperclass();
    //*** Let's navigate the class hierarchy...
    while(_superClass != PersistenceRuleSetImpl.class) {
      _class=_superClass;
      _superClass=_class.getSuperclass();      
    } // while(_superClass != PersistentRuleSet.class)
    
    // Get the generic super class which should be this class.
    type=_class.getGenericSuperclass();
    if (type instanceof ParameterizedType) {
      //
      typeArguments=((ParameterizedType) type).getActualTypeArguments();
      if ((typeArguments != null) && (typeArguments.length == 4)) {
        if (logger.isLoggable(Level.FINEST)) {
          // Let's do a little logging...
          logger.finest("DAOFactory: "+typeArguments[0].getClass().getName());
          logger.finest("DAO: "+typeArguments[1].getClass().getName());
          logger.finest("Non-Persistent Object: "+
              typeArguments[2].getClass().getName());
          logger.finest("Persistent Object: "+
              typeArguments[3].getClass().getName());
        } // END if (logger.isLoggable(level.FINEST))
        // We have an array of type arguments and there are four of them.
        // typeArguments[0] = WildDAO sub-class
        // typeArguments[1] = Non-persistent object type
        // typeArguments[2] = Persistent object type
      } else {
        if (typeArguments == null) {
          logger.severe(
              "PersistentRuleSet init() failed to get actual type arguments.");
        } else {
          logger.severe("Unexpected number of type arguments found.");
        } // END if (typeArguments == null)
      } // END if ((typeArguments != null) && (typeArguments.length == 4))
    } else {
      logger.severe(
          "PersistentRuleSet init() Failed to find ParameterizedType.");
    } // END if (type instanceof ParameterizedType)
    logger.exiting(_CLASS,"init()");
  }
  
  /* (non-Javadoc)
   * @see com.wildstartech.gae.wfa.dao.PersistenceRuleSet#getDAO()
   */
  @Override
  public D getDAO() {
    logger.entering(_CLASS,"getDAO()");
    D dao=null;
    
    logger.exiting(_CLASS,"getDAO()",dao);
    return dao;
  }  
  
  /* (non-Javadoc)
   * @see com.wildstartech.gae.wfa.dao.PersistenceRuleSet#evaluate(com.wildstartech.wfa.dao.UserContext, W)
   */
  @Override
  public Result<W> evaluate(UserContext ctx, W wildObject) {
    logger.entering(_CLASS,"evalute(UserContext,W)", 
        new Object[] {ctx,wildObject});
    Result<W> result=null;
    for (PersistenceRule<D, T, W> rule: this.rules) {
      rule.apply(this, ctx, wildObject);
    }
    logger.exiting(_CLASS,"evalute(UserContext,W)",result);
    return result;
  }
  
  /**
   * Registers the specified rule with the <code>PersistenceRuleSet</code>.
   * @param PersistenceRule<W> the rule to be added to the rule set.
   */
  public boolean add(PersistenceRule<D, T, W> rule) {
    logger.entering(_CLASS,"register(PersistenceRule<W>)",rule);
    boolean result=false;
    
    if (rule != null) {
      if (!this.rules.contains(rule)) {
        /* The rule is not null and it has not yet been added to the list, so
         * add it. */
        this.rules.add(rule);
        result=true;
        logger.finest("The rule was added to the RuleSet.");
      } else {
        logger.warning("The rule already exists in the RuleSet.");
      } // END if (!this.rules.contains(rule))
    } else {
      logger.warning("The specified rule was null, so nothing was added.");
    } // END if (rule != null)
    
    logger.exiting(_CLASS,"register(PersistenceRule<W>)",result);
    return result;
  }
}