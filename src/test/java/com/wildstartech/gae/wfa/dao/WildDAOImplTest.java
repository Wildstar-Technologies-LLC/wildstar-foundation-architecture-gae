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

import org.testng.annotations.Test;
import org.testng.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.wildstartech.gae.wfa.dao.UserDAOImpl;
import com.wildstartech.gae.wfa.UserData;
import com.wildstartech.wfa.dao.PersistentUser;
import com.wildstartech.wfa.dao.UserDAO;
import com.wildstartech.wfa.dao.UserDAOFactory;
import com.wildstartech.wfa.dao.WildDAO;
import com.wildstartech.wfa.user.PasswordTooLongException;
import com.wildstartech.wfa.user.UserNameTooLongException;

public class WildDAOImplTest {

  @Test
  public void loadProperties() {
    ClassLoader cl = null;
    InputStream in = null;
    Properties props = null;
    String userName = null;
    String password = null;

    cl = WildDAOImplTest.class.getClassLoader();
    props = new Properties();
    in = cl.getResourceAsStream("wild-dao-admin.properties");
    assert in != null;
    try {
      props.load(in);
      userName = props.getProperty(WildDAO.PROPKEY_ADMIN_USER);
      assert userName != null;
      password = props.getProperty(WildDAO.PROPKEY_ADMIN_PASSWORD);
      assert password != null;
    } catch (IOException ex) {
      // IOException thrown reading properties file.
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void isUserValidTest() {
    String userName = UserData.getAdminUserName();
    String password = UserData.getAdminPassword();

    PersistentUser user = null;
    UserDAO dao = null;
    UserDAOFactory daoFactory = null;

    daoFactory = new UserDAOFactory();
    assert daoFactory != null;
    dao = daoFactory.getDAO();
    assert dao != null;
    user = dao.create();
    assert user != null;
    try {
      user.setName(userName);
      user.setPassword(password);
      assert ((UserDAOImpl) dao).isUserValid(user) == true;
    } catch (UserNameTooLongException | PasswordTooLongException ex) {
      Assert.fail("Exception thrown");
    } // END try/catch
  }
}