package com.wildstartech.wfa.finance;

import java.util.Date;

public class MockCreditCard 
extends MockPaymentCard 
implements CreditCard {

   private String cardHolderName="";
   private String verification="";
   
   /**
    * Default, no-argument constructor.
    */
   public MockCreditCard() {
      
   }
   
   public MockCreditCard(CreditCard creditCard) {
      super(creditCard);
      if (creditCard != null) {
         setCardHolderName(creditCard.getCardHolderName());
         setVerification(creditCard.getVerification());
      } // END if (creditCard != null)
   }
   
   public MockCreditCard(String description,
         String accountNumber,
         String brandName,
         String cardHolderName,
         String issuingBankName,
         String verification,
         int expirationMonth,
         int expirationYear) {
      super(
              accountNumber,
              brandName,
              issuingBankName,
              expirationMonth,
              expirationYear);
      setCardHolderName(cardHolderName);
      setVerification(verification);
   }

   //********** Accessor Methods
   //***** cardHolderName
   @Override
   public String getCardHolderName() {
      return this.cardHolderName;
   }

   @Override
   public void setCardHolderName(String cardHolderName) {
      this.cardHolderName=cardHolderName;
   }
   //***** verificaiton
   @Override
   public String getVerification() {
      return this.verification;
   }

   @Override
   public void setVerification(String verification) {
      this.verification=verification;       
   }
}