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
package com.wildstartech.wfa.dao.ticketing;

import java.util.Date;

import org.testng.annotations.Test;

import com.wildstartech.gae.wfa.UserData;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.UserContextDAOFactory;
import com.wildstartech.wfa.dao.WildDAOTest;
import com.wildstartech.wfa.ticketing.BasicTicket;

public class BasicTicketDAOTest extends WildDAOTest {
   
   @Test
   public void testCreate() {
      BasicTicket ticket=null;
      BasicTicketDAO<BasicTicket, PersistentBasicTicket<?>> dao=null;
      BasicTicketDAOFactory factory=null;
      
      factory=new BasicTicketDAOFactory();
      assert factory != null;
      dao=factory.getDAO();
      assert dao != null;
      ticket=dao.create();
      assert ticket !=null;
   }

   
   @Test(dependsOnMethods = {"testCreate"})
   public void testSave() {
      BasicTicket ticket=null;
      BasicTicketDAO<BasicTicket,PersistentBasicTicket<?>> dao=null;
      BasicTicketDAOFactory factory=null;
      BasicTicketTestCase testCase=null;
      PersistentBasicTicket<?> pTicket=null;
      UserContext ctx=null;
      
      ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
            UserData.getAdminPassword());
      
      factory=new BasicTicketDAOFactory();
      assert factory != null;
      dao=factory.getDAO();
      assert dao != null;
      ticket=dao.create();
      assert ticket != null;
      testCase=new BasicTicketTestCase1();
      ticket.setTitle(testCase.getTitle());
      ticket.setShortDescription(testCase.getShortDescription());
      ticket.setStatusState(testCase.getStatusState());
      ticket.setStatusReason(testCase.getStatusReason());
      pTicket=dao.save(ticket, ctx);
      //** requestId
      assert pTicket.getRequestId() != null;
      //** createdBy
      assert pTicket.getCreatedBy() != null;
      assert pTicket.getCreatedBy().equals(UserData.getAdminUserName());
      //** dateCreated
      assert pTicket.getDateCreated() != null;
      assert pTicket.getDateModified().getTime() > new Date().getTime() - 1000;
      //** modifiedBy
      assert pTicket.getModifiedBy() != null;
      assert pTicket.getModifiedBy().equals(UserData.getAdminUserName());
      //** dateModified
      assert pTicket.getDateModified() != null;
      assert pTicket.getDateModified().getTime() > new Date().getTime() - 1000;
      //** title
      assert pTicket.getTitle() != null;
      assert pTicket.getTitle().equals(testCase.getTitle());
      //** shortDescription
      assert pTicket.getShortDescription() != null;
      assert pTicket.getShortDescription().equals(
            testCase.getShortDescription());
      //** statusState
      assert pTicket.getStatusState() != null;
      assert pTicket.getStatusState().equals(testCase.getStatusState());
      //** statusReason
      assert pTicket.getStatusReason() != null;
      assert pTicket.getStatusReason().equals(testCase.getStatusReason());
   }
}