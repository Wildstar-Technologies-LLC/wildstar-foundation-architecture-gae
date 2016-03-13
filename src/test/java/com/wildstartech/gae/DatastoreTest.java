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