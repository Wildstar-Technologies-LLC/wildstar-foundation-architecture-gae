/*
 * Copyright (c) 2001 - 2016 Wildstar Technologies, LLC.
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

import java.util.List;

import org.testng.annotations.Test;

import com.wildstartech.gae.wfa.UserData;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.UserContextDAOFactory;
import com.wildstartech.wfa.dao.WildDAOTest;
import com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrder;
import com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrderLineItem;

public class ReceiverWorkOrderDAOTest extends WildDAOTest {
   // Utility methods
   public boolean compare(ReceiverWorkOrder source, ReceiverWorkOrder target) {
      boolean result=true;
      int sourceLineItemCount=0;
      int targetLineItemCount=0;
      List<ReceiverWorkOrderLineItem> sourceLineItems=null;
      List<ReceiverWorkOrderLineItem> targetLineItems=null;
      ReceiverWorkOrderLineItem sourceItem=null;
      ReceiverWorkOrderLineItem targetItem=null;
      
      if ((source != null) && (target != null)) {
         // billOfLadingNumber
         if (result) {
            result=isEqual(
                  source.getBillOfLadingNumber(),
                  target.getBillOfLadingNumber());            
         } // END if (result)
         // carrier
         if (result) {
            result=isEqual(source.getCarrier(),target.getCarrier());
         } // END if (result)
         // dateReceived
         if (result) {
            result=isEqual(source.getDateReceived(),target.getDateReceived());
         } // END if (result)
         // purchaseorderNumber
         if (result) {
            result=isEqual(
                  source.getPurchaseOrderNumber(),
                  target.getPurchaseOrderNumber());
         } // END if (result)
         // statusReason
         if (result) {
            result=isEqual(source.getStatusReason(),target.getStatusReason());
         } // END if (result)
         // statusState
         if (result) {
            result=isEqual(source.getStatusState(),target.getStatusState());
         } // END if (result)
         // title
         if (result) {
            result=isEqual(source.getTitle(),target.getTitle());
         } // END if (result)
         
         // Let's look at line items
         if (result) {
            sourceLineItems=source.getLineItems();
            targetLineItems=target.getLineItems();
            sourceLineItemCount=sourceLineItems.size();
            targetLineItemCount=targetLineItems.size();
            if (result) {
               result=(sourceLineItemCount == targetLineItemCount);
               if (result) {
                  /* We have the same count in line items, so let's compare
                   * Let's compare the line items. */
                  for (int i=0; i < sourceLineItemCount; i++) {
                     sourceItem=sourceLineItems.get(i);
                     targetItem=sourceLineItems.get(i);
                     result=compareLineItem(sourceItem,targetItem);
                     if (!result) {
                        break;
                     } // END if (!result)
                  } // END for (int i=0; i < sourceLineItemCount; i++)
               } // END if (result) 
            } // END if (result)
         } // END if (result)         
      } else {
         result=false;
      } // END if ((source != null) && (target != null))
      
      return result;
   }   
   public boolean compareLineItem(
         ReceiverWorkOrderLineItem source,
         ReceiverWorkOrderLineItem target) {
      boolean result=true;
      if ((source != null) && (target != null)) {
         // cube
         if (result) {
            result=(source.getCube() == target.getCube());
         } // END if (result)
         // description
         if (result) {
            result=isEqual(source.getDescription(),target.getDescription());
         } // END if (result)
         // height
         if (result) {
            result=(source.getHeight() == target.getHeight());
         } // END if (result)
         // length
         if (result) {
            result=(source.getLength() == target.getLength());
         } // END if (result)
         // lineItemNumber
         if (result) {
            result=(source.getLineItemNumber() == target.getLineItemNumber());
         } // END if (result)
         // packagingType
         if (result) {
            result=isEqual(source.getPackagingType(),target.getPackagingType());
         } // END if (result)
         // productId
         if (result) {
            result=isEqual(source.getProductId(),target.getProductId());
         } // END if (result)
         // quantity
         if (result) {
            result=(source.getQuantity() == target.getQuantity());
         } // END if (result)
         // totalCube
         if (result) {
            result=(source.getTotalCube() == target.getTotalCube());
         } // END if (result)
         // totalWeight
         if (result) {
            result=(source.getTotalWeight() == target.getTotalWeight());
         } // END if (result)
         // weight
         if (result) {
            result=(source.getWeight() == target.getWeight());
         } // END if (result)
         // width
         if (result) {
            result=(source.getWidth() == target.getWidth());
         } // END if (result)
      } else {
         result=false;
      } // END if ((source != null) && (target != null))
      
      return result;
   }
   @Test
   public void testFactory() {
      ReceiverWorkOrderDAO dao=null;
      ReceiverWorkOrderDAOFactory factory=null;
      
      factory = new ReceiverWorkOrderDAOFactory();
      dao=factory.getDAO();
      assert dao != null;
      assert dao instanceof ReceiverWorkOrderDAO;
   }
   
   @Test(dependsOnMethods={"testFactory"})
   public void testCreate() {
      ReceiverWorkOrder workOrder=null;
      ReceiverWorkOrderDAO dao=null;
      ReceiverWorkOrderDAOFactory factory=null;
      
      factory = new ReceiverWorkOrderDAOFactory();
      dao=factory.getDAO();
      workOrder=dao.create();
      assert workOrder != null;
      assert workOrder instanceof PersistentReceiverWorkOrder;
   }
   
   @Test(dependsOnMethods={"testCreate"})
   public void testSave() {
      PersistentReceiverWorkOrder pWorkOrder=null;
      ReceiverWorkOrder workOrder=null;
      ReceiverWorkOrderDAO dao=null;
      ReceiverWorkOrderDAOFactory factory=null;
      UserContext ctx=null;
      
      factory=new ReceiverWorkOrderDAOFactory();
      dao=factory.getDAO();
      ctx=UserContextDAOFactory.authenticate(
            UserData.getAdminUserName(),
            UserData.getAdminPassword());
      workOrder=new TestCaseReceiverWorkOrder1();
      pWorkOrder=dao.save(workOrder, ctx);
      assert pWorkOrder != null;
      assert compare(workOrder,pWorkOrder) == true;
   }
   @Test(dependsOnMethods={"testSave"})
   public void testModify() {
      PersistentReceiverWorkOrder pWorkOrder=null;
      ReceiverWorkOrder workOrder=null;
      ReceiverWorkOrderDAO dao=null;
      ReceiverWorkOrderDAOFactory factory=null;
      UserContext ctx=null;
      
      factory=new ReceiverWorkOrderDAOFactory();
      dao=factory.getDAO();
      ctx=UserContextDAOFactory.authenticate(
            UserData.getAdminUserName(),
            UserData.getAdminPassword());
      workOrder=new TestCaseReceiverWorkOrder1();
      pWorkOrder=dao.save(workOrder, ctx);
      pWorkOrder.setTitle("Testing Modification.");
      pWorkOrder=dao.save(pWorkOrder, ctx);
      
      assert pWorkOrder.getTitle().equals("Testing Modification.");
   }
   
   @Test(dependsOnMethods={"testModify"})
   public void testModifyLineItem() {
      List<ReceiverWorkOrderLineItem> lineItems=null; 
      PersistentReceiverWorkOrder pWorkOrder=null;
      ReceiverWorkOrder workOrder=null;
      ReceiverWorkOrderDAO dao=null;
      ReceiverWorkOrderDAOFactory factory=null;
      ReceiverWorkOrderLineItem item=null;
      ReceiverWorkOrderLineItem testItem=null;
      UserContext ctx=null;
      
      factory=new ReceiverWorkOrderDAOFactory();
      dao=factory.getDAO();
      ctx=UserContextDAOFactory.authenticate(
            UserData.getAdminUserName(),
            UserData.getAdminPassword());
      workOrder=new TestCaseReceiverWorkOrder1();
      pWorkOrder=dao.save(workOrder, ctx);
      pWorkOrder.setTitle("Testing Modification.");
      pWorkOrder=dao.save(pWorkOrder, ctx);
      lineItems=pWorkOrder.getLineItems();
      item=lineItems.get(0);
      item.setDescription("Modified Description");
      pWorkOrder=dao.save(pWorkOrder, ctx);
      lineItems=pWorkOrder.getLineItems();
      testItem=lineItems.get(0);
      //assert testItem.getDescription().equals(item.getDescription()) == false;
      
   }
}