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
package com.wildstartech.gae;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.wildstartech.gae.wfa.UserData;
import com.wildstartech.wfa.dao.PersistentUser;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.UserContextDAOFactory;
import com.wildstartech.wfa.dao.UserDAO;
import com.wildstartech.wfa.dao.UserDAOFactory;
import com.wildstartech.wfa.user.PasswordTooLongException;
import com.wildstartech.wfa.user.UserNameTooLongException;

public class DatastoreTest {
   private static final String _CLASS=DatastoreTest.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   public static final String testUserName="test.user@wildstartech.com";
   public static final String testUserPassword="test.user.password";
   private LocalServiceTestHelper helper = null;
   private LocalDatastoreServiceTestConfig config=null;
   private UserContext ctx=null;
   
   @BeforeClass
   public void setup() {
     logger.entering(_CLASS,"setup()");
     String userName=null;
     String password=null;
     PersistentUser user=null;
     UserDAO uDao=null;
     UserDAOFactory uFactory=null;
     UserContext localCtx=null;
     UserContext adminCtx=null;
     
     this.config=new LocalDatastoreServiceTestConfig();
     this.config.setApplyAllHighRepJobPolicy();
     this.helper=new LocalServiceTestHelper(config);
     this.helper.setUp();
     // Get the admin user account
     userName=UserData.getAdminUserName();
     password=UserData.getAdminPassword();
     adminCtx=UserContextDAOFactory.authenticate(userName,password);
     // Create the default user.
     uFactory=new UserDAOFactory();
     uDao=uFactory.getDAO();
     user=uDao.create();
     try {
       user.setName(testUserName);
       user.setPassword(testUserPassword);
     } catch (UserNameTooLongException ex) {
       logger.log(Level.SEVERE,
           "The specified User Name is Too Long.",
           ex);
     } catch (PasswordTooLongException ex) {
       logger.log(Level.SEVERE,
           "The Specified Password is Too Long.",
           ex);
     } // END try/catch
     uDao.save(user,adminCtx);
     //***** Now let's login as the user we just created.
     localCtx=UserContextDAOFactory.authenticate(testUserName,testUserPassword);
     if (localCtx.isAuthenticated()) {
       this.ctx=localCtx;
     } else {
       logger.severe("Authentication Failed");
     } // END if (localCtx.isAuthenticated())
     
     logger.exiting(_CLASS,"setup()");
   }
   
   @AfterClass
   public void tearDown() {
     logger.entering(_CLASS,"tearDown()");
     this.helper.tearDown();    
     logger.exiting(_CLASS,"tearDown()");
   }
   
   public UserContext getTestUserContext() {
      logger.entering(_CLASS,"getTestUserContext()");
      logger.exiting(_CLASS,"getTestUserContext()",this.ctx);
      return this.ctx;
    }
}