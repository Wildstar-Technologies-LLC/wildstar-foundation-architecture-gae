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

import java.util.Map;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.wildstartech.efa.jsf.Constants;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.WildObject;
import com.wildstartech.wfa.dao.rules.PersistentRule;

public abstract class PersistentRuleImpl<W extends WildObject> 
implements PersistentRule<W> {
   private static final String _CLASS=PersistentRuleImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   private UserContext ctx=null;
   
   /**
    * Default, no-argumnet constructor.
    */
   public PersistentRuleImpl() {
      logger.entering(_CLASS, "PersistentRuleImpl()");
      logger.exiting(_CLASS, "PersistentRuleImpl()");
   }
   /**
    * Constructor allowing for the specification of a particular 
    * {@code UserContext} to be used when the {@code apply} method is invoked.
    * @param ctx The {@code UserContext} that should be used in the course of 
    * invoking the rule.
    */
   public PersistentRuleImpl(UserContext ctx) {
      logger.entering(_CLASS, "PersistentRuleImpl(UserContext)",ctx);
      setUserContext(ctx);
      logger.entering(_CLASS, "PersistentRuleImpl(UserContext)",ctx);      
   }
   
   //***** Utility Methods
   public boolean isEmpty(String value) {
      logger.entering(_CLASS, "isEmpty(String)",value);
      boolean result=false;
      
      if (
            (value == null) ||
            (value.length() == 0)
         ) {
         result=true;
      }
      logger.exiting(_CLASS, "isEmpty(String)",value);
      return result;
   }
   
   //***** Accessor Methods
   
   /**
    * Returns a reference to the value of the {@code ctx} field.
    */
   private UserContext getUserContext() {
      logger.entering(_CLASS, "getUserContext()");
      logger.exiting(_CLASS, "getUserContext()",this.ctx);
      return this.ctx;
   }
   
   /**
    * Set the {@code UserContext} that the {@code apply} method should use.
    * @param ctx
    */
   private void setUserContext(UserContext ctx) {
      logger.entering(_CLASS, "setUserContext(UserContext)",ctx);
      this.ctx=ctx;
      logger.exiting(_CLASS, "setUserContext(UserContext)");      
   }
   
   /**
    * Returns the <code>UserContext</code> for the currently logged in user.
    * 
    * @return
    */
   @Override
   public final UserContext getCurrentUser() {
       logger.entering(_CLASS,"getCurrentUser()");
       ExternalContext eCtx=null;
       FacesContext fCtx=null;
       Map<String,Object> sessionMap=null;
       UserContext ctx=null;
       
       ctx=getUserContext();
       if (ctx == null) {
          logger.finest("Using the UserContext from the current session.");
          fCtx=FacesContext.getCurrentInstance();
          eCtx=fCtx.getExternalContext();
          sessionMap=eCtx.getSessionMap();
          ctx=(UserContext) sessionMap.get(Constants.CURRENT_USER_CONTEXT);          
       } else {
          logger.finest("Using the UserContext passed at instantiation.");
       } // END if (ctx == null) 
       
       if (ctx == null) {
           logger.severe("UserContext not found as request attribute.");
       } // END if (ctx == null)
       logger.exiting(_CLASS,"getCurrentUser()",ctx);
       return ctx;
   }   
}