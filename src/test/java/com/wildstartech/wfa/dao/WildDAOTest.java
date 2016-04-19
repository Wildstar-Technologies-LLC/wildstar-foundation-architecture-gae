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
package com.wildstartech.wfa.dao;

import java.util.Date;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

@Test
public class WildDAOTest {
  //maximum eventual consistency
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          /* By setting the unapplied job percentage to 100, we are instructing 
           * the local datastore to operate with the maximum amount of eventual
           * consistency. Maximum eventual consistency means writes will commit
           * but always fail to apply, so global (non-ancestor) queries will
           * consistently fail to see changes. This is of course not 
           * representative of the amount of eventual consistency your 
           * application will see when running in production, but for testing
           * purposes, it's very useful to be able to configure the local 
           * datastore to behave this way every time.

          new LocalDatastoreServiceTestConfig()
          .setDefaultHighRepJobPolicyUnappliedJobPercentage(100)
          */
          /* Force HRD mode and all jobs to apply successfully (to test 
           * cross-group transactions, for example). */
          new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy() 
      );

  @BeforeSuite
  public void setUp() {
      helper.setUp();
  }

  @AfterSuite
  public void tearDown() {
      helper.tearDown();
  }
  
  public boolean isEqual(String source, String target) {
     boolean result=false;
     
     if (
           (source != null) && 
           (target != null) &&
           source.equals(target)) {
        result=true;        
     }
     return result;
  }
  public boolean isEqual(Date source, Date target) {
     boolean result=false;
     long sourceTime=0;
     long targetTime=0;
     
     if ((source != null) && (target != null)) {
        sourceTime=source.getTime();
        targetTime=target.getTime();
        if (sourceTime == targetTime) {
           result=true;
        } // END if (sourceTime == targetTime)
     } // END if ((source != null) && (target != null))
     return result;
  }
}