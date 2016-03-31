package com.wildstartech.wfa.finance;

import java.util.Date;

public class MockPaymentCard extends MockPaymentType implements PaymentCard {
   private int expirationMonth=0;
   private int expirationYear=0;
   private String accountNumber="";
   private String brandName="";   
   private String issuingBankName=null;
   
   public MockPaymentCard() {
      
   }
   
   public MockPaymentCard(PaymentCard card) {
      super(card);
      if (card != null) {
         setAccountNumber(card.getAccountNumber());
         setBrandName(card.getBrandName());
         setExpirationMonth(card.getExpirationMonth());
         setExpirationYear(card.getExpirationYear());
         setIssuingBankName(card.getIssuingBankName());
      } // END if (card != null)
   }
   
   public MockPaymentCard(
         String accountNumber,
         String brandName,
         String issuingBankName,
         int expirationMonth,
         int expirationYear) {
      setAccountNumber(accountNumber);
      setBrandName(brandName);
      setIssuingBankName(issuingBankName);
      setExpirationMonth(expirationMonth);
      setExpirationYear(expirationYear);
   }
   
   @Override
   public String getAccountNumber() {
      return this.accountNumber;
   }

   @Override
   public void setAccountNumber(String accountNumber) {
      if (accountNumber != null) {
         this.accountNumber=accountNumber;;
      } else {
         this.accountNumber="";
      } // END if (accountNumber != null)      
   }

   @Override
   public String getBrandName() {
      return this.brandName;
   }

   @Override
   public void setBrandName(String brandName) {
      if (brandName != null) {
         this.brandName=brandName;
      } else {
         this.brandName="";
      } // END if (brandName != null)      
   }

   @Override
   public int getExpirationMonth() {
      return this.expirationMonth;
   }

   @Override
   public void setExpirationMonth(int expirationMonth) {
      this.expirationMonth=expirationMonth;
   }
   
   @Override
   public int getExpirationYear() {
      return this.expirationYear;
   }

   @Override
   public void setExpirationYear(int expirationYear) {
      this.expirationYear=expirationYear;
   }

   @Override
   public String getIssuingBankName() {
      return this.issuingBankName;
   }

   @Override
   public void setIssuingBankName(String issuingBankName) {
      if (issuingBankName != null) {
         this.issuingBankName=issuingBankName;
      } else {
         this.issuingBankName="";
      } // END if (accountNumber != null)      
   }
}