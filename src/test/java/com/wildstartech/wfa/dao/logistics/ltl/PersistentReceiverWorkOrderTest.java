package com.wildstartech.wfa.dao.logistics.ltl;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PersistentReceiverWorkOrderTest {
   private ReceiverWorkOrderDAO dao;
   
   @BeforeClass
   public void setup() {
      ReceiverWorkOrderDAOFactory factory=null;
      
      factory=new ReceiverWorkOrderDAOFactory();
      this.dao=factory.getDAO();
   }
   
   @Test
   public void test() {
      PersistentReceiverWorkOrder workOrder=null;
      
      workOrder=this.dao.create();
      workOrder.setBillOfLadingNumber("");
   }
}
