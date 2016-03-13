package com.wildstartech.wfa.dao.logistics.ltl;

import com.wildstartech.wfa.logistics.ltl.LineItem;

public class LineItemBaseTestCase 
implements LineItem {
   /*
    * The position of the quote line item on the associated quote. 
    */
   private int lineItemNumber=0;
   
   //***** lineItemNumber
   @Override
   public final int getLineItemNumber() {
      return this.lineItemNumber;
   }
   @Override
   public final void setLineItemNumber(int lineItemNumber) {
      this.lineItemNumber = lineItemNumber;
   }

}
