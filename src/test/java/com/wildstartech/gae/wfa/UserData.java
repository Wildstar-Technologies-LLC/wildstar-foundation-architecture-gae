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
package com.wildstartech.gae.wfa;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.wildstartech.wfa.dao.UserContextDAO;

public class UserData {
  private static UserData data=new UserData();
  private String userName="";
  private String password="";
  
  public static String getAdminUserName() {
    return UserData.data.getUserName();
  }
  
  public static String getAdminPassword() {
    return UserData.data.getPassword();
  }
  
  private UserData() {
    ClassLoader cl=null;
    InputStream in=null;
    Properties props=null;
    
    cl=UserData.class.getClassLoader();
    in=cl.getResourceAsStream("wild-dao-admin.properties");
    if (in != null) {
      props=new Properties();
      try {
        props.load(in);
        this.userName=props.getProperty(UserContextDAO.PROPKEY_ADMIN_USER);
        this.password=props.getProperty(UserContextDAO.PROPKEY_ADMIN_PASSWORD);
      } catch (IOException ex) {        
        ex.printStackTrace();
      }
    }
    
  }
  
  public String getUserName() {
    return this.userName;
  }
  
  public String getPassword() {
    return this.password;
  }
}