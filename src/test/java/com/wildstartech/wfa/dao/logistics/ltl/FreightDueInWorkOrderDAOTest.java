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

import com.wildstartech.gae.wfa.UserData;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.UserContextDAOFactory;
import com.wildstartech.wfa.dao.WildDAOTest;
import com.wildstartech.wfa.logistics.ltl.FreightDueInWorkOrder;
import com.wildstartech.wfa.logistics.ltl.FreightDueInWorkOrderLineItem;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FreightDueInWorkOrderDAOTest 
extends WildDAOTest {
   private Map<String,PersistentFreightDueInWorkOrder> workOrders=null;
   
   public FreightDueInWorkOrderDAOTest() {
      workOrders=new TreeMap<String,PersistentFreightDueInWorkOrder>();
   }
   //********** BEGIN: TEST CASES
   //***** testCreate
   @Test
   public void testCreate() {
      Date tmpDate=null;
      Date dateCreated=null;
      Date dateModified=null;
      FreightDueInWorkOrder workOrder=null;
      FreightDueInWorkOrderLineItem lineItem=null;
      FreightDueInWorkOrderDAO dao=null;
      FreightDueInWorkOrderDAOFactory factory=null;
      TestCaseFreightDueInWorkOrder1 testCase=null;
      TestCaseFreightDueInWorkOrderLineItem1 lineItemTestCase1=null;
      TestCaseFreightDueInWorkOrderLineItem2 lineItemTestCase2=null;
      PersistentFreightDueInWorkOrder pWorkOrder=null;
      String testString="";
      UserContext ctx=null;
      
      ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
          UserData.getAdminPassword());
      assert ctx != null;
      factory=new FreightDueInWorkOrderDAOFactory();
      assert factory != null;
      dao=factory.getDAO();
      assert dao != null;
      
      //***** setup the workOrder we will save
      testCase=new TestCaseFreightDueInWorkOrder1();
      workOrder=dao.create();
      assert workOrder != null;
      assert workOrder instanceof FreightDueInWorkOrder;
      assert workOrder instanceof PersistentFreightDueInWorkOrder;
      workOrder.setEstimatedShipDate(testCase.getEstimatedShipDate());
      tmpDate=workOrder.getEstimatedShipDate();
      assert tmpDate != null;
      assert tmpDate.getTime() == testCase.getEstimatedShipDate().getTime();
      workOrder.setDateShipped(testCase.getDateShipped());
      tmpDate=workOrder.getDateShipped();
      assert tmpDate != null;
      assert tmpDate.getTime() == testCase.getDateShipped().getTime();
      workOrder.setCarrierName(testCase.getCarrierName());
      testString=workOrder.getCarrierName();
      workOrder.setCarrierTrackingNumber(testCase.getCarrierTrackingNumber());
      workOrder.setContactCompanyName(testCase.getContactCompanyName());
      workOrder.setContactName(testCase.getContactName());
      workOrder.setContactEmail(testCase.getContactEmail());
      workOrder.setContactPreferredTelephoneNumber(
            testCase.getContactPreferredTelephoneNumber());
      workOrder.setManufacturerName(testCase.getManufacturerName());
      workOrder.setReferenceIdentifier(testCase.getReferenceIdentifier());
      workOrder.setSpecialHandling(testCase.getSpecialHandling());
      //***** Create and test line item #1
      lineItemTestCase1=new TestCaseFreightDueInWorkOrderLineItem1();
      lineItem=workOrder.createLineItem();
      lineItem.setDescription(lineItemTestCase1.getDescription());
      assert lineItem.getDescription().equalsIgnoreCase(
            lineItemTestCase1.getDescription());
      lineItem.setLength(lineItemTestCase1.getLength());
      assert lineItem.getLength() == lineItemTestCase1.getLength();
      lineItem.setWidth(lineItemTestCase1.getWidth());
      assert lineItem.getWidth() == lineItemTestCase1.getWidth();
      lineItem.setHeight(lineItemTestCase1.getHeight());
      assert lineItem.getHeight() == lineItemTestCase1.getHeight();
      lineItem.setWeight(lineItemTestCase1.getWeight());
      assert lineItem.getWeight() == lineItemTestCase1.getWeight();
      lineItem.setQuantity(lineItemTestCase1.getQuantity());
      assert lineItem.getQuantity() == lineItemTestCase1.getQuantity(); 
      // Add the line first Item
      workOrder.addLineItem(lineItem);
      //***** Create and test line item #2
      lineItemTestCase2=new TestCaseFreightDueInWorkOrderLineItem2();
      lineItem=workOrder.createLineItem();
      lineItem.setDescription(lineItemTestCase2.getDescription());
      assert lineItem.getDescription().equalsIgnoreCase(
            lineItemTestCase2.getDescription());
      lineItem.setLength(lineItemTestCase2.getLength());
      assert lineItem.getLength() == lineItemTestCase2.getLength();
      lineItem.setWidth(lineItemTestCase2.getWidth());
      assert lineItem.getWidth() == lineItemTestCase2.getWidth();
      lineItem.setHeight(lineItemTestCase2.getHeight());
      assert lineItem.getHeight() == lineItemTestCase2.getHeight();
      lineItem.setWeight(lineItemTestCase2.getWeight());
      assert lineItem.getWeight() == lineItemTestCase2.getWeight();
      lineItem.setQuantity(lineItemTestCase2.getQuantity());
      assert lineItem.getQuantity() == lineItemTestCase2.getQuantity(); 
      // Add the line first Item
      workOrder.addLineItem(lineItem);
      // Test the lineItem count
      assert workOrder.getLineItems().size() == 2;
      
      
      //***** save the work order
      pWorkOrder=dao.save(workOrder, ctx);
      
      testString=pWorkOrder.getIdentifier();
      assert testString != null;
      dateCreated=pWorkOrder.getDateCreated();
      assert dateCreated != null;
      dateModified=pWorkOrder.getDateModified();
      assert dateModified != null;
      assert dateCreated.getTime() == dateModified.getTime();
      
      testString=pWorkOrder.getCarrierName();
      assert testCase.getCarrierName().equalsIgnoreCase(testString);
      testString=pWorkOrder.getCarrierTrackingNumber();
      assert testCase.getCarrierTrackingNumber().equalsIgnoreCase(testString);
      testString=pWorkOrder.getContactCompanyName();
      assert testCase.getContactCompanyName().equalsIgnoreCase(testString);
      testString=pWorkOrder.getContactEmail();
      assert testCase.getContactEmail().equalsIgnoreCase(testString);
      testString=pWorkOrder.getContactName();
      assert testCase.getContactName().equalsIgnoreCase(testString);
      testString=pWorkOrder.getContactPreferredTelephoneNumber();
      assert testCase.getContactPreferredTelephoneNumber().equalsIgnoreCase(testString);
      testString=pWorkOrder.getManufacturerName();
      assert testCase.getManufacturerName().equalsIgnoreCase(testString);
      testString=pWorkOrder.getReferenceIdentifier();
      assert testCase.getReferenceIdentifier().equalsIgnoreCase(testString);
      testString=pWorkOrder.getSpecialHandling();
      assert testCase.getSpecialHandling().equalsIgnoreCase(testString);
      //***** Test getting the line items
      // Test line Item #1
      lineItem=pWorkOrder.getLineItem(1);
      assert lineItem != null;
      assert lineItem.getDescription().equalsIgnoreCase(
            lineItemTestCase1.getDescription());
      assert lineItem.getLength() == lineItemTestCase1.getLength();
      assert lineItem.getWidth() == lineItemTestCase1.getWidth();
      assert lineItem.getHeight() == lineItemTestCase1.getHeight();
      assert lineItem.getWeight() == lineItemTestCase1.getWeight();
      assert lineItem.getQuantity() == lineItemTestCase1.getQuantity(); 
      // Test line Item #2
      lineItem=pWorkOrder.getLineItem(2);
      assert lineItem != null;
      assert lineItem.getDescription().equalsIgnoreCase(
            lineItemTestCase2.getDescription());
      assert lineItem.getLength() == lineItemTestCase2.getLength();
      assert lineItem.getWidth() == lineItemTestCase2.getWidth();
      assert lineItem.getHeight() == lineItemTestCase2.getHeight();
      assert lineItem.getWeight() == lineItemTestCase2.getWeight();
      assert lineItem.getQuantity() == lineItemTestCase2.getQuantity();
      // Save the workorder
      this.workOrders.put("TESTCASE1", pWorkOrder);
   }
   
   @Test(dependsOnMethods="testCreate")
   public void testUpdate() {
      int tmpInt=0;
      FreightDueInWorkOrderDAO dao=null;
      FreightDueInWorkOrderDAOFactory factory=null;
      TestCaseFreightDueInWorkOrder2 testCase2=null;
      List<PersistentFreightDueInWorkOrder> workOrders=null;
      PersistentFreightDueInWorkOrder pWorkOrder=null;
      String tmpStr=null;
      String workOrderIdentifier=null;
      UserContext ctx=null;
      
      factory=new FreightDueInWorkOrderDAOFactory();
      assert factory != null;
      dao=factory.getDAO();
      assert dao != null;
      ctx=UserContextDAOFactory.authenticate(UserData.getAdminUserName(),
            UserData.getAdminPassword());
      pWorkOrder=dao.save(pWorkOrder, ctx);
      testCase2=new TestCaseFreightDueInWorkOrder2();
      pWorkOrder=this.workOrders.get("TESTCASE1");
      //***** lets'start testing.
      workOrderIdentifier=pWorkOrder.getIdentifier();
      assert (
            (workOrderIdentifier != null) && 
            (workOrderIdentifier.length() > 0)
      );
      pWorkOrder.setCarrierName(testCase2.getCarrierName());
      pWorkOrder.setCarrierTrackingNumber(testCase2.getCarrierTrackingNumber());
      pWorkOrder.setContactCompanyName(testCase2.getContactCompanyName());
      pWorkOrder=dao.save(pWorkOrder, ctx);
      // Let's count the work orders
      workOrders=dao.findAll(ctx);
      tmpInt=workOrders.size();
      assert tmpInt == 1;
      
   }
}