package com.wildstartech.wfa.finance;

public class MockPaymentType implements PaymentType {
   private String description="";
   
   public MockPaymentType() {
      
   }
   
   public MockPaymentType(PaymentType paymentType) {
      if (paymentType != null) {
         setDescription(paymentType.getDescription());
      } // END if (paymentType != null)
   }
   
   public MockPaymentType(String description) {
      setDescription(description);
   }
   
   @Override
   public String getDescription() {
      return this.description;
   }

   @Override
   public void setDescription(String description) {
      if (description != null) {
         this.description=description;
      } else {
         this.description="";
      } // END if (description != null)
   }
}
