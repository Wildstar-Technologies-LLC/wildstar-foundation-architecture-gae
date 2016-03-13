package com.wildstartech.wfa.finance;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.wildstartech.gae.wfa.dao.finance.PersistentPaymentCardImpl;

public class PaymentCardTest {
   private PersistentPaymentCardImpl<PaymentCard> paymentCard=null;
   private PaymentCard referenceCard=SampleCreditCardData.amex[0];
   
   /**
    * Create an instance of the {@code CreditCard} which will be used for the
    * rest of testing.
    */
   @BeforeClass
   public void createInstance() {
      this.paymentCard = new PersistentPaymentCardImpl<PaymentCard>();
   }
   
   @Test
   public void accountNumber() {
      String accountNumber=null;
      String readAccountNumber=null;

      accountNumber=this.referenceCard.getAccountNumber();
      
      this.paymentCard.setAccountNumber(accountNumber);
      readAccountNumber=this.paymentCard.getAccountNumber();
      
      assert readAccountNumber != null;
      assert readAccountNumber.equals(accountNumber);
   }
   
   @Test
   public void brandName() {
      String brandName=null;
      String readBrandName=null;
      
      brandName=this.referenceCard.getBrandName();
      
      this.paymentCard.setBrandName(brandName);
      readBrandName=this.paymentCard.getBrandName();
      
      assert readBrandName != null;
      assert readBrandName.equals(brandName);
   }
   
   @Test
   public void issuingBankName() {
      String issuingBankName=null;
      String readIssuingBankName=null;
      
      issuingBankName=this.referenceCard.getIssuingBankName();
      
      this.paymentCard.setIssuingBankName(issuingBankName);
      readIssuingBankName=this.paymentCard.getIssuingBankName();
      
      assert readIssuingBankName != null;
      assert readIssuingBankName.equals(issuingBankName);
   }
}
