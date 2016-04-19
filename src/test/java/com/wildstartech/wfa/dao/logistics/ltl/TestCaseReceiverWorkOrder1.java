package com.wildstartech.wfa.dao.logistics.ltl;

import java.util.Date;

import com.wildstartech.wfa.logistics.ltl.MockReceiverWorkOrderLineItem;
import com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrderLineItem;

public class TestCaseReceiverWorkOrder1 extends TestCaseReceiverWorkOrderBase {
   public Date dateReceived=new Date();
   
   public TestCaseReceiverWorkOrder1() {
      ReceiverWorkOrderLineItem lineItem=null;
      
      this.setBillOfLadingNumber("BOL1234");
      this.setCarrier("ACME Furniture Purveyors");
      this.setDateReceived(this.dateReceived);
      this.setPurchaseOrderNumber("PO#4321");
      this.setSalesOrderNumber("SO97531");
      this.setShortDescription("Short Description");
      this.setStatusState("New");
      this.setStatusReason("");
      this.setTitle("Receiver Work Order Test Case");
      
      //***** First LineItem
      lineItem=new MockReceiverWorkOrderLineItem();
      lineItem.setCustomDescription(true);
      lineItem.setCustomDimensions(true);
      lineItem.setCustomWeight(true);
      lineItem.setDescription("FIRST LINE ITEM");
      lineItem.setLength(50);
      lineItem.setWidth(60);
      lineItem.setHeight(70);
      lineItem.setWeight(100);
      addLineItem(lineItem);
      
      //***** Second LineItem
      lineItem=new MockReceiverWorkOrderLineItem();
      lineItem.setCustomDescription(true);
      lineItem.setCustomDimensions(true);
      lineItem.setCustomWeight(true);
      lineItem.setDescription("SECOND LINE ITEM");
      lineItem.setLength(52);
      lineItem.setWidth(62);
      lineItem.setHeight(72);
      lineItem.setWeight(120);
      addLineItem(lineItem);
      
      //***** Third LineItem
      lineItem=new MockReceiverWorkOrderLineItem();
      lineItem.setCustomDescription(true);
      lineItem.setCustomDimensions(true);
      lineItem.setCustomWeight(true);
      lineItem.setDescription("THIRD LINE ITEM");
      lineItem.setLength(53);
      lineItem.setWidth(63);
      lineItem.setHeight(73);
      lineItem.setWeight(130);
      addLineItem(lineItem);
   }
}