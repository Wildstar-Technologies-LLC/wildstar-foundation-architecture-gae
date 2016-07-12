package com.wildstartech.gae.wfa.dao;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.testng.IClassListener;
import org.testng.IMethodInstance;
import org.testng.ITestClass;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.wildstartech.wfa.dao.PersistentUser;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.UserContextDAOFactory;
import com.wildstartech.wfa.dao.UserDAO;
import com.wildstartech.wfa.dao.UserDAOFactory;
import com.wildstartech.wfa.dao.UserData;
import com.wildstartech.wfa.user.PasswordTooLongException;
import com.wildstartech.wfa.user.UserNameTooLongException;

public class DataStoreTestClassListener implements IClassListener {
	private static final String _CLASS=DataStoreTestClassListener.class.getName();
	private static final Logger logger=Logger.getLogger(_CLASS);
	
	private LocalServiceTestHelper helper=null;
	
	@Override
	public void onBeforeClass(ITestClass testClass, IMethodInstance mi) {
		LocalDatastoreServiceTestConfig config = null;
		String userName = null;
		String password = null;
		PersistentUser user = null;
		UserDAO uDao = null;
		UserDAOFactory uFactory = null;
		UserContext localCtx = null;
		UserContext adminCtx = null;
		UserData userData=null;
		
		userData=UserData.getInstance();
		config = new LocalDatastoreServiceTestConfig();
		config.setApplyAllHighRepJobPolicy();
		config.setBackingStoreLocation(
				"/Users/derekberube/Documents/Programming/Java/Wildstar Foundation Architecture (GAE)/datastore/localdb.bin");
		config.setNoStorage(false);
		
		this.helper = new LocalServiceTestHelper(config);
		this.helper.setUp();

		// Get the admin user account
		userData=UserData.getInstance();
		userName = userData.getAdminUserName();
		password = userData.getAdminPassword();
		adminCtx = UserContextDAOFactory.authenticate(userName, password);
		// Create the default user.
		uFactory = new UserDAOFactory();
		uDao = uFactory.getDAO();
		user = uDao.create();
		try {
			user.setName(userData.getUserName("testuser"));
			user.setPassword(userData.getUserPassword("testuser"));
		} catch (UserNameTooLongException ex) {
			logger.log(Level.SEVERE, 
					"The specified User Name is Too Long.", ex);
		} catch (PasswordTooLongException ex) {
			logger.log(Level.SEVERE, 
					"The Specified Password is Too Long.", ex);
		} // END try/catch
		uDao.save(user, adminCtx);
		// ***** Now let's login as the user we just created.
		localCtx = UserContextDAOFactory.authenticate(
				userData.getUserName("testuser"), 
				userData.getUserPassword("testuser"));
		if (!localCtx.isAuthenticated()) {
			logger.severe("Authentication Failed");
		} // END if (localCtx.isAuthenticated())

	}

	@Override
	public void onAfterClass(ITestClass testClass, IMethodInstance mi) {
		logger.entering(_CLASS, "onAfterClass(ITestClass,IMethodInstance)",
				new Object[] {testClass,mi});
		try {
			this.helper.tearDown();
		} catch (NullPointerException ex) {
			// NO-Op
		}
		logger.exiting(_CLASS, "onAfterClass(ITestClass,IMethodInstance)");
	}
}
