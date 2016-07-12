package com.wildstartech.gae.wfa.dao;

import java.util.logging.Level;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.SuiteRunner;

import java.util.logging.Logger;

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

public class DataStoreTestSuiteListener implements ISuiteListener {
	private static final String _CLASS = DataStoreTestSuiteListener.class.getName();
	private static final Logger logger = Logger.getLogger(_CLASS);
	private static final String testUserName = "test.user@wildstartech.com";
	private static final String testUserPassword = "test.user.password";
	
	
	/**
	 * Initialize the datastore environment.
	 */
	@Override
	public void onStart(ISuite suite) {
		LocalServiceTestHelper helper = null;
		LocalDatastoreServiceTestConfig config = null;
		String userName = null;
		String password = null;
		SuiteRunner suiteRunner=null;
		PersistentUser user = null;
		UserDAO uDao = null;
		UserDAOFactory uFactory = null;
		UserContext localCtx = null;
		UserContext adminCtx = null;
		UserData userData=null;
		
		config = new LocalDatastoreServiceTestConfig();
		config.setApplyAllHighRepJobPolicy();
		helper = new LocalServiceTestHelper(config);
		helper.setUp();
		suiteRunner=(SuiteRunner) suite;
		suiteRunner.setAttribute("helper", helper);

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
			user.setName(testUserName);
			user.setPassword(testUserPassword);
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
				testUserName, 
				testUserPassword);
		if (!localCtx.isAuthenticated()) {
			logger.severe("Authentication Failed");
		} // END if (localCtx.isAuthenticated())
	}

	/**
	 * Turn down the datastore environment.
	 */
	@Override
	public void onFinish(ISuite suite) {
		logger.entering(_CLASS, "onFinish(ISuite)");
		LocalServiceTestHelper helper = null;
		Object obj=null;
		SuiteRunner suiteRunner=null;
		
		suiteRunner=(SuiteRunner) suite;
		suiteRunner.setAttribute("helper", helper);
		
		obj=suiteRunner.getAttribute("helper");
		if (
				(obj != null) && 
				(obj instanceof LocalServiceTestHelper)
		   ) {
			helper=(LocalServiceTestHelper) obj;
			helper.tearDown();
		} // END if ((obj != null) && (obj instanceof LocalServiceTestHelper))
		logger.exiting(_CLASS, "onFinish(ISuite)");
	}
}