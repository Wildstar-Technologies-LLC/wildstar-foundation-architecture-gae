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
package com.wildstartech.gae.wfa.dao.user;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.user.PersistentUser;
import com.wildstartech.wfa.dao.user.UserContext;

public class UserContextImpl extends WildObjectImpl<UserContext>
      implements UserContext {
   /** Used in object serialization. */
   private static final long serialVersionUID = -6491371391407975390L;
   private static final String _CLASS = UserContextImpl.class.getName();
   private static final Logger logger = Logger.getLogger(_CLASS);
   protected static final String _KIND = "com.wildstartech.wfa.dao.UserContext";

   /* Indicates whether or not the current user has been authenticated. */
   private boolean authenticated = false;
   /* The date/time the user represented by this context was authenticated. */
   private Date authenticationDate = null;
   /* */
   private DateFormat dFmt = null;
   /* */
   private Locale locale = null;
   /* */
   private NumberFormat cFmt = null;
   /* */
   private NumberFormat nFmt = null;
   /* */
   private String password = null;
   /* */
   private String sessionId = null;
   /* */
   private String userName = null;
   /* */
   private TimeZone tz = null;
   /* */
   private PersistentUserImpl user = null;

   { // BEGIN Instance initializer
      this.cFmt = NumberFormat.getCurrencyInstance();
      this.dFmt = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
      this.nFmt = NumberFormat.getNumberInstance();
      this.locale = Locale.getDefault();
      this.tz = TimeZone.getDefault();
   } // END Instance Initializer

   /**
    * Default, no-argument constructor.
    */
   public UserContextImpl() {
      logger.entering(_CLASS, "UserContextImpl()");
      logger.exiting(_CLASS, "UserContextImpl()");
   }

   // ******** Utility Methods
   /**
    * Manages the process of authenticating the user against the persistent.
    * 
    * @return
    */
   @Override
   public boolean authenticate() {
      logger.entering(_CLASS, "authenticate()");
      PasswordEncryptor pe = null;
      String encryptedPassword = null;
      String tmpPassword = null;
      PersistentUserImpl user = null;
      UserContext adminContext = null;
      UserDAOImpl dao = null;

      if (((this.userName != null) && (this.userName.length() > 0))
            && ((this.password != null) && (this.password.length() > 0))) {
         logger.finest("Username and password are specified.");
         // Return a reference to the adminContext
         adminContext = UserContextDAOImpl.getUserContextAdmin();
         if (adminContext != null) {
            logger.finest("adminContext is not null.");
            if ((this.userName.compareTo(adminContext.getUserName()) == 0)
                  && (this.password
                        .compareTo(adminContext.getPassword()) == 0)) {
               // The admin context will be automatically authenticated
               // without going through the persistence layer.
               this.authenticated = true;
               this.authenticationDate = new Date();
            } else {
               logger.finest("Not adminContext, so authenticate.");
               // Get a reference to the password encryptor
               pe = PasswordEncryptor.getInstance();
               // Neither the userName nor password are blank.
               dao = new UserDAOImpl();
               // Perform a loookup
               user = (PersistentUserImpl) dao.findByName(this.userName,
                     adminContext);
               if (user != null) {
                  logger.finest("User found.");
                  tmpPassword = user.getPassword();
                  encryptedPassword = pe.encryptPassword(this.password);
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("tmpPassword=" + tmpPassword);
                     logger.finest("encryptedPassword=" + encryptedPassword);
                  } // END if (logger.isLoggable(Level.FINEST))
                  if (encryptedPassword.compareTo(tmpPassword) == 0) {
                     logger.finest("Passwords match.");
                     // Passwords match
                     // Set the authenticated flag to true
                     this.authenticated = true;
                     // Set the authenticated date
                     this.authenticationDate = new Date();
                     // Store the user reference.
                     this.user = user;
                  } else {
                     logger.finest("Passwords do not match.");
                     // Passwords did not match, so authentication fails.
                     this.authenticated = false;
                     // Clear the unauthenticated date.
                     this.authenticationDate = null;
                  } // END if (this.password.equals(encryptedPassword))
               } else {
                  logger.finest(
                        "Specified user not found, so authenticated is false.");
               } // END if (user != null)
            } // END if ((this.userName.equals(adminContext.getUserName()))
         } else {
            logger.severe("Should never get to this point.");
         } // END if (adminContext != null)
      } else {
         this.authenticated = false;
         // Either the username or password is null or zero length. Lets log.
         if ((userName == null) || (userName.length() == 0)) {
            logger.finest("userName is either null or zero length.");
         } // END if ((userName == null) || (userName.length() == 0))
         if ((password == null) || (password.length() == 0)) {
            logger.finest("password is either null or zero length.");
         } // END if ((password == null) || (password.length() == 0))
      } // END if (((this.userName != null) && (this.userName.length() > 0)...

      logger.exiting(_CLASS, "authenticate()", this.authenticated);
      return this.authenticated;
   }
   
   /**
    * Used to tell the datastore the type of object represented by the entity.
    */
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()", UserContextImpl._KIND);
      return UserContextImpl._KIND;
   }
   
   /**
    * Populate the Entity with information from the current object.
    */
   @Override
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(Entity)");
      super.populateEntity(entity);
      entity.setProperty("username", this.user.getName());
      logger.exiting(_CLASS, "populateEntity(Entity)");
   }

   /**
    * Populate the current object with information contained in the Entity.
    */
   @Override
   protected void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity,UserContext)",
            new Object[] { entity, ctx });
      Object obj = null;
      String username = null;
      PersistentUser user = null;
      UserDAOImpl dao = null;

      if (entity != null) {
         // Call the requisite superclass methods.
         super.populateFromEntity(entity, ctx);
         // Get the value of the username property
         obj = entity.getProperty("username");
         if ((obj != null) && (obj instanceof String)) {
            username = (String) obj;
            // Using the username property, look up the user object
            dao = new UserDAOImpl();
            user = dao.findByEmailAddress(username, ctx);
            if (user != null) {
               setUser(user);
            } else {
               // The specified user could not be found.
            } // END if (user != null)
         } else {
            if (obj == null) {
               logger.severe("Entity did not contain the username property");
            } else {
               logger.severe("Username property is not a string.");
            } // END if (obj == null)
         } // END if ((obj != null) && (obj instanceof String))
      } // END if (entity != null)

      logger.exiting(_CLASS, "populateFromEntity(Entity)");
   }

   /**
    * Populates the current UserContext from the specified template.
    */
   @Override
   public void populateFromObject(UserContext ctx) {
      logger.entering(_CLASS, "populateFromObject(UserContext)", ctx);
      if (ctx != null) {
         //TODO
      } else {
         logger.severe("The UserContext is null.");
      } // END if (ctx != null)

      logger.exiting(_CLASS, "populateFromObject(UserContext)");
   }
   
   /**
    * Returns a string representation consisting of object properties.
    */
   public String toPropertyString() {
      logger.entering(_CLASS, "toPropertyString()");
      String returnValue=null;
      StringBuilder sb=null;
      
      sb=new StringBuilder(1024);
      sb.append(super.toPropertyString());
      sb.append(",userName=\"").append(getUserName()).append("\", ");
      sb.append("authenticated=\"");
      if (isAuthenticated()) {
         sb.append("true\", authenticationDate=\"");
         sb.append(this.dFmt.format(getAuthenticationDate()));
         sb.append("\",");
      } else {
         sb.append("false\",");
      } // END if (this.authenticated)      
      returnValue=sb.toString();
      
      logger.exiting(_CLASS, "toPropertyString()",returnValue);
      return returnValue;
   }
   
   /**
    * Returns a string representation of the object.
    */
   public String toString() {
     logger.entering(_CLASS,"toString()");
     String result=null;
     StringBuilder sb=null;
     
     sb=new StringBuilder(256);
     sb.append("UserContextImpl [");
     sb.append(toPropertyString());
     sb.append("]");
     result=sb.toString();
     logger.exiting(_CLASS,"toString()",result);
     return result;
   }  
   
   //***** Accessor methods
   //***** authentication
   /**
    * Returns whether or not the current use has logged in.
    */
   @Override
   public boolean isAuthenticated() {
      logger.entering(_CLASS, "isAuthenticated()");
      logger.exiting(_CLASS, "isAuthenticated()", this.authenticated);
      return this.authenticated;
   }

   /**
    * The date/time the current user was successfully authenticated.
    */
   @Override
   public Date getAuthenticationDate() {
      logger.entering(_CLASS, "getAuthenticationDate()");
      logger.exiting(_CLASS, "getAuthenticationDate()",
            this.authenticationDate);
      return this.authenticationDate;
   }
   //***** currencyFormat
   public NumberFormat getCurrencyFormat() {
      logger.entering(_CLASS, "getCurrencyFormat()");
      logger.exiting(_CLASS, "getCurrencyFormat()", this.cFmt);
      return this.cFmt;
   }
   // ***** currentDateTime
   public Date getCurrentDateTime() {
      logger.entering(_CLASS, "getCurrentDateTime");
      Date currentDateTime = new Date();
      logger.exiting(_CLASS, "getCurrentDateTime()", currentDateTime);
      return currentDateTime;
   }
   // ***** dateFormat
   public DateFormat getDateFormat() {
      logger.entering(_CLASS, "getDateFormat()");
      logger.exiting(_CLASS, "getDateFormat()", this.dFmt);
      return this.dFmt;
   }
   //***** locale
   @Override
   public Locale getLocale() {
      logger.entering(_CLASS, "getLocale()");
      logger.exiting(_CLASS, "getLocale()", this.locale);
      return this.locale;
   }
   @Override
   public void setLocale(Locale locale) {
      logger.entering(_CLASS, "setLocale(Locale)", locale);
      if (locale != null) {
         this.locale = locale;
      } else {
         logger.warning("Null Locale parameter was ignored.");
      } // END if (locale != null)
      logger.exiting(_CLASS, "setLocale(Locale)");
   }

   @Override
   public void setLocale(String locale) {
      logger.entering(_CLASS, "");
      if (locale != null) {

      } else {
         logger.warning("Null Locale name parameter was ignored.");
      } // END if (locale != null)
      logger.exiting(_CLASS, "");
   }
   //***** numberFormat
   public NumberFormat getNumberFormat() {
      logger.entering(_CLASS, "getNumberFormat()");
      logger.exiting(_CLASS, "getNumberFormat()", this.nFmt);
      return this.nFmt;
   }
   //***** password
   @Override
   public String getPassword() {
      logger.entering(_CLASS, "getPassword()");
      String pwd = null;
      if (this.user != null) {
         pwd = this.user.getPassword();
      } else {
         pwd = this.password;
      } // END if (this.user != null)
      logger.exiting(_CLASS, "getPassword()", pwd);
      return pwd;
   }
   @Override
   public void setPassword(String password) {
      logger.entering(_CLASS, "setPassword(String)", password);
      this.password = defaultValue(password, "");
      logger.exiting(_CLASS, "setPassword(String)");
   }
   //***** sessionId
   @Override
   public String getSessionId() {
      logger.entering(_CLASS, "getSessionId()");
      logger.exiting(_CLASS, "getSessionId()", this.sessionId);
      return this.sessionId;
   }
   @Override
   public void setSessionId(String sessionId) {
      logger.entering(_CLASS, "setSessionId(String)", sessionId);
      if (sessionId == null) {
         this.sessionId = "";
      } else {
         this.sessionId = sessionId;
      } // END if (sessionId==null)
      logger.exiting(_CLASS, "setSessionId(String)");
   }
   //***** timeZone
   public TimeZone getTimeZone() {
      logger.entering(_CLASS, "getTimeZone()");
      logger.exiting(_CLASS, "getTimeZone()", this.tz);
      return this.tz;
   }
   public void setTimeZone(TimeZone tz) {
      logger.entering(_CLASS, "setTimeZone(TimeZone)", tz);
      if (tz != null) {
         this.tz = tz;
      } else {
         this.tz = TimeZone.getDefault();
      } // END if (tz != null)
      logger.exiting(_CLASS, "setTimeZone(TimeZone)");
   }
   public void setTimeZone(String timeZoneName) {
      logger.entering(_CLASS, "setTimeZone(String)", timeZoneName);
      TimeZone tz = null;
      if (timeZoneName != null) {
         tz = TimeZone.getTimeZone(timeZoneName);
      } else {
         tz = TimeZone.getDefault();
      } // END if (timeZoneName != null)
      this.tz = tz;
      logger.exiting(_CLASS, "setTimeZone(String)");
   }
   //***** userName
   public String getUserName() {
      logger.entering(_CLASS, "getUserName()");
      String uName = null;
      if (this.user != null) {
         uName = this.user.getName();
      } else {
         uName = this.userName;
      } // END if (this.user != null)
      logger.exiting(_CLASS, "getUserName()", uName);
      return uName;
   }
   public void setUserName(String userName) {
      logger.entering(_CLASS, "setUserName(String)", userName);
      this.userName = defaultValue(userName, "");
      logger.exiting(_CLASS, "setUserName(String)");
   }
   //***** user
   public PersistentUser getUser() {
      logger.entering(_CLASS, "getUser()");
      logger.exiting(_CLASS, "getUser()", this.user);
      return this.user;
   }

   public void setUser(PersistentUser user) {
      logger.entering(_CLASS, "setUser(User)", user);
      if (user != null) {
         this.user = (PersistentUserImpl) user;
      } // END if (user == null)
      logger.exiting(_CLASS, "setUser(User)");
   }   
}