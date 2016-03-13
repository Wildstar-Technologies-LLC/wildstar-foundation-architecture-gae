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
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates proxy implementations of interfaces with a WildObject base.
 * @author Derek Berube, Wildstar Technologies, LLC.
 *
 * @param <T>
 */
public abstract class WildObjectProxyFactory<T> {
  private static final String _CLASS=WildObjectProxyFactory.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  
  private Class<?> _interfaceClass=null;
  
  /**
   * Default, no-argument constructor.
   */
  public WildObjectProxyFactory() {
    logger.entering(_CLASS, "WildObjectProxyFactory()");
    init();
    logger.exiting(_CLASS, "WildObjectProxyFactory()");
  }
  
  /*
   * Default initialization routine for the factory.
   */
  private void init() {
    logger.entering(_CLASS,"identifyKind()");
    Class<?> _class=null;
    Class<?> _superClass=null;
    Type type=null;
    Type[] typeArguments=null;
    
    _class=this.getClass();
    _superClass=_class.getSuperclass();
    //*** Let's navigate the class hierarchy...
    while(_superClass != WildObjectProxyFactory.class) {
      _class=_superClass;
      _superClass=_class.getSuperclass();      
    } // while(_superClass != PersistentRuleSet.class)
    
    // Get the generic super class which should be this class.
    type=_class.getGenericSuperclass();
    if (type instanceof ParameterizedType) {
      //  The type is a generic type...
      typeArguments=((ParameterizedType) type).getActualTypeArguments();
      if ((typeArguments != null) && (typeArguments.length == 1)) {
        type=typeArguments[0];
        _class=(Class<?>) type;
        if (_class.isInterface()) {
          this._interfaceClass=_class;
        } else {
          logger.severe("The type is NOT an interface and it should be.");
        } // END  if (((Class<?>)type).isInterface())                
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
    logger.exiting(_CLASS, "identifyKind()");
  }
  
  /**
   * Creates an instance of class T.
   */
  @SuppressWarnings("unchecked")
  public T createInstance() {
    logger.entering(_CLASS,"createInstance()");
    ClassLoader cl=null;
    T object=null;
    
    cl=this.getClass().getClassLoader();
    try {
      object=(T) Proxy.newProxyInstance(
          cl,
          new Class[] {this._interfaceClass},
          new WildObjectProxyImpl<T>()
          );
    } catch (IllegalArgumentException ex) {
      logger.log(Level.SEVERE,
          "IllegalArgumentException thrown.",
          ex);
    } // END try/catch
    logger.exiting(_CLASS,"createInstance()");
    return object;
  }
  
  @SuppressWarnings("unchecked")
  public T createInstance(T referenceObject) {
    logger.entering(_CLASS,"createInstance(T)", 
        new Object[] {referenceObject});
    T object=null;
    WildObjectImpl<T> wildObject=null;
    
    object=createInstance();
    wildObject=(WildObjectImpl<T>) object;
    wildObject.populateFromObject(referenceObject);
    
    logger.exiting(_CLASS,"createInstance(T)", object);
    return object;
  }
}
