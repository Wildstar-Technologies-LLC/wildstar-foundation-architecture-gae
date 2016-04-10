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
package com.wildstartech.wfa.dao.logistics.ltl;

import org.testng.annotations.Test;

import com.wildstartech.wfa.logistics.ltl.FreightDueInWorkOrder;

public class PersistentFreightDueInWorkOrderImplTest {
   private FreightDueInWorkOrderDAO dao=null;
   private TestCaseFreightDueInWorkOrder1 testCase=null;
         

   public PersistentFreightDueInWorkOrderImplTest() {
      FreightDueInWorkOrderDAOFactory factory=null;
      
      factory=new FreightDueInWorkOrderDAOFactory();
      assert factory != null;
      this.dao=factory.getDAO();
      assert this.dao != null;
      this.testCase=new TestCaseFreightDueInWorkOrder1();
      assert this.testCase != null;
   }
   //***** carrierName
   @Test
   public void testCarrierName() {
      FreightDueInWorkOrder workOrder=null;
      String testString="";
      
      workOrder=this.dao.create();
      workOrder.setCarrierName(this.testCase.getCarrierName());
      testString=workOrder.getCarrierName();
      
      assert testString.equalsIgnoreCase(this.testCase.getCarrierName());
   }
   
   //***** carrierTrackingNumber
   @Test
   public void testCarrierTrackingNumber() {
      FreightDueInWorkOrder workOrder=null;
      String testString="";
      
      workOrder=this.dao.create();
      workOrder.setCarrierTrackingNumber(this.testCase.getCarrierTrackingNumber());
      testString=workOrder.getCarrierTrackingNumber();
      
      assert testString.equalsIgnoreCase(this.testCase.getCarrierTrackingNumber());
   }
   
   //***** contactCompanyName
   @Test
   public void testContactCompanyName() {
      FreightDueInWorkOrder workOrder=null;
      String testString="";
      
      workOrder=this.dao.create();
      workOrder.setContactCompanyName(this.testCase.getContactCompanyName());
      testString=workOrder.getContactCompanyName();
      
      assert testString.equalsIgnoreCase(this.testCase.getContactCompanyName());
   }
   
   //***** contactName
   @Test
   public void testContactName() {
      FreightDueInWorkOrder workOrder=null;
      String testString="";
      
      workOrder=this.dao.create();
      workOrder.setContactName(this.testCase.getContactName());
      testString=workOrder.getContactName();
      
      assert testString.equalsIgnoreCase(this.testCase.getContactName());
   }
   
   //***** contactEmail
   @Test
   public void testContactEmail() {
      FreightDueInWorkOrder workOrder=null;
      String testString="";
      
      workOrder=this.dao.create();
      workOrder.setContactEmail(this.testCase.getContactEmail());
      testString=workOrder.getContactEmail();
      
      assert testString.equalsIgnoreCase(this.testCase.getContactEmail());
   }
   
   //***** test1ContactPreferredTelephoneNumber
   @Test
   public void testContactPreferredTelephoneNumber() {
      FreightDueInWorkOrder workOrder=null;
      String testString="";
      
      workOrder=this.dao.create();
      workOrder.setContactPreferredTelephoneNumber(
            this.testCase.getContactPreferredTelephoneNumber());
      testString=workOrder.getContactPreferredTelephoneNumber();
      
      assert testString.equalsIgnoreCase(
            this.testCase.getContactPreferredTelephoneNumber());
   }   
   
   //***** test1ManufacturerName
   @Test
   public void testManufacturerName() {
      FreightDueInWorkOrder workOrder=null;
      String testString="";
      
      workOrder=this.dao.create();
      workOrder.setManufacturerName(this.testCase.getManufacturerName());
      testString=workOrder.getManufacturerName();
      
      assert testString.equalsIgnoreCase(this.testCase.getManufacturerName());
   }
   
   //***** test1ReferenceIdentifier
   @Test
   public void testReferenceIdentifier() {
      FreightDueInWorkOrder workOrder=null;
      String testString="";
      
      workOrder=this.dao.create();
      workOrder.setReferenceIdentifier(this.testCase.getReferenceIdentifier());
      testString=workOrder.getReferenceIdentifier();
      
      assert testString.equalsIgnoreCase(
            this.testCase.getReferenceIdentifier());
   }
   
   //***** test1SpecialHandling
   @Test
   public void testSpecialHandling() {
      FreightDueInWorkOrder workOrder=null;
      String testString="";
      
      workOrder=this.dao.create();
      workOrder.setSpecialHandling(this.testCase.getSpecialHandling());
      testString=workOrder.getSpecialHandling();
      
      assert testString.equalsIgnoreCase(this.testCase.getSpecialHandling());
   }   
}
