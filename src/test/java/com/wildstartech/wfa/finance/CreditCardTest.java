package com.wildstartech.wfa.finance;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.wildstartech.wfa.dao.finance.CreditCardDAO;
import com.wildstartech.wfa.dao.finance.CreditCardDAOFactory;
import com.wildstartech.wfa.finance.CreditCard;

public class CreditCardTest {
   CreditCard creditCard = null;   
   CreditCard referenceCard=SampleCreditCardData.amex[0];
   
   /**
    * Create an instance of the {@code CreditCard} which will be used for the
    * rest of testing.
    */
   @BeforeClass
   public void createInstance() {
      CreditCardDAO dao = null;
      CreditCardDAOFactory factory = null;

      factory = new CreditCardDAOFactory();
      dao = factory.getDAO();
      this.creditCard = dao.create();
   }
   
   @Test
   public void accountNumber() {
      String accountNumber=null;
      String readAccountNumber=null;

      accountNumber=this.referenceCard.getAccountNumber();
      
      this.creditCard.setAccountNumber(accountNumber);
      readAccountNumber=this.creditCard.getAccountNumber();
      
      assert readAccountNumber != null;
      assert readAccountNumber.equals(accountNumber);
   }
   
   @Test
   public void brandName() {
      String brandName=null;
      String readBrandName=null;
      
      brandName=this.referenceCard.getBrandName();
      
      this.creditCard.setBrandName(brandName);
      readBrandName=this.creditCard.getBrandName();
      
      assert readBrandName != null;
      assert readBrandName.equals(brandName);
   }
   
   @Test
   public void issuingBankName() {
      String issuingBankName=null;
      String readIssuingBankName=null;
      
      issuingBankName=this.referenceCard.getIssuingBankName();
      
      this.creditCard.setIssuingBankName(issuingBankName);
      readIssuingBankName=this.creditCard.getIssuingBankName();
      
      assert readIssuingBankName != null;
      assert readIssuingBankName.equals(issuingBankName);
   }
   
   @Test
   public void cardHolderName() {
      String cardHolderName=null;
      String readCardHolderName=null;
      
      cardHolderName=this.referenceCard.getCardHolderName();
      
      this.creditCard.setCardHolderName(cardHolderName);
      readCardHolderName=this.creditCard.getCardHolderName();
      
      assert readCardHolderName != null;
      assert readCardHolderName.equals(cardHolderName);
   }
   
   @Test
   public void verification() {
      String verification=null;
      String readVerification=null;
      
      verification=this.referenceCard.getVerification();
      
      this.creditCard.setVerification(verification);
      readVerification=this.creditCard.getVerification();
      
      assert readVerification != null;
      assert readVerification.equals(verification);
   }   
}