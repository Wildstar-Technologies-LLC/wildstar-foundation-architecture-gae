package com.wildstartech.wfa.finance;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MockPaymentCard extends MockPaymentType implements PaymentCard {
   private Date expirationDate=null;
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
         setExpirationDate(card.getExpirationDate());
         setIssuingBankName(card.getIssuingBankName());
      } // END if (card != null)
   }
   
   public MockPaymentCard(
         String accountNumber,
         String brandName,
         String issuingBankName,
         Date expirationDate) {
      setAccountNumber(accountNumber);
      setBrandName(brandName);
      setIssuingBankName(issuingBankName);
      setExpirationDate(expirationDate);
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
   public Date getExpirationDate() {
      return this.expirationDate;
   }

   @Override
   public void setExpirationDate(Date expirationDate) {
      this.expirationDate=expirationDate;
   }

   @Override
   public Date parseExpirationDate(String expirationDate) {
      Date pDate=null;
      DateFormat fmt=null;
      
      if (expirationDate != null) {
         fmt=new SimpleDateFormat("MM / yy");
         try {
            pDate=fmt.parse(expirationDate);
         } catch (ParseException ex) {
            
         } // END try/catch
      } // END if (expirationDate != null)
      
      return pDate;
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