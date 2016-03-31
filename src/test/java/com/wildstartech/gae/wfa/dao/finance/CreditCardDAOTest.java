package com.wildstartech.gae.wfa.dao.finance;

import java.util.Date;

import org.testng.annotations.Test;

import com.wildstartech.gae.wfa.UserData;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.UserContextDAOFactory;
import com.wildstartech.wfa.dao.WildDAO;
import com.wildstartech.wfa.dao.WildDAOTest;
import com.wildstartech.wfa.dao.finance.CreditCardDAO;
import com.wildstartech.wfa.dao.finance.CreditCardDAOFactory;
import com.wildstartech.wfa.dao.finance.PersistentCreditCard;
import com.wildstartech.wfa.finance.CreditCard;
import com.wildstartech.wfa.finance.SampleCreditCardData;

public class CreditCardDAOTest extends WildDAOTest {
   
   @Test
   public void testCreate() {
      CreditCard creditCard=null;
      CreditCardDAO dao=null;
      CreditCardDAOFactory factory=null;
    
      factory=new CreditCardDAOFactory();
      assert factory != null;
      dao=factory.getDAO();
      assert dao != null;
      assert dao instanceof WildDAO;
      assert dao instanceof CreditCardDAO;
      creditCard=dao.create();
      assert creditCard !=null;
      assert creditCard instanceof PersistentCreditCard;            
   }
   
   @Test
   public void testSave() {
      int savedInt=0;
      int referencedInt=0;
      CreditCard creditCard=null;
      CreditCardDAO dao=null;
      CreditCardDAOFactory factory=null;
      Date referenceDate=null;
      Date savedDate=null;
      PersistentCreditCard pCreditCard=null;
      String identifier="";
      String referenceValue="";
      String savedValue="";
      UserContext ctx=null;
      
      factory=new CreditCardDAOFactory();
      dao=factory.getDAO();
      creditCard=SampleCreditCardData.amex[0];
      ctx=UserContextDAOFactory.authenticate(
            UserData.getAdminUserName(),
            UserData.getAdminPassword());
      pCreditCard=dao.save(creditCard, ctx);
      
      assert pCreditCard != null;
      // Let's get the identifier
      identifier=pCreditCard.getIdentifier();
      assert identifier != null;
      assert identifier.length() > 0;
      // The identifier is a non-null, non-zero length string, so look it up.
      pCreditCard=dao.findByIdentifier(identifier, ctx);
      
      //***** Check persistent fields
      // Check createdBy
      savedValue=pCreditCard.getCreatedBy();
      referenceValue=ctx.getUserName();
      assert savedValue != null;
      assert savedValue.length() == referenceValue.length();
      assert savedValue.equalsIgnoreCase(referenceValue);
      // Check modifiedBy
      savedValue=pCreditCard.getModifiedBy();
      referenceValue=ctx.getUserName();
      assert savedValue != null;
      assert savedValue.length() == referenceValue.length();
      assert savedValue.equalsIgnoreCase(referenceValue);
      // Check identifier
      savedValue=pCreditCard.getIdentifier();
      assert savedValue != null;
      assert savedValue.length() > 0;
      //***** Check object-specific fields
      // Check accountNumber
      savedValue=pCreditCard.getAccountNumber();
      referenceValue=SampleCreditCardData.amex[0].getAccountNumber();
      assert savedValue != null;
      assert savedValue.length() == referenceValue.length();
      assert savedValue.equalsIgnoreCase(referenceValue);
      // Check brandName
      savedValue=pCreditCard.getBrandName();
      referenceValue=SampleCreditCardData.amex[0].getBrandName();
      assert savedValue != null;
      assert savedValue.length() == referenceValue.length();
      assert savedValue.equalsIgnoreCase(referenceValue);
      // Check cardHolderName
      savedValue=pCreditCard.getCardHolderName();
      referenceValue=SampleCreditCardData.amex[0].getCardHolderName();
      assert savedValue != null;
      assert savedValue.length() == referenceValue.length();
      assert savedValue.equalsIgnoreCase(referenceValue);
      // Check description
      savedValue=pCreditCard.getDescription();
      referenceValue=SampleCreditCardData.amex[0].getDescription();
      assert savedValue != null;
      assert savedValue.length() == referenceValue.length();
      assert savedValue.equalsIgnoreCase(referenceValue);
      // Check expirationMonth
      savedInt=pCreditCard.getExpirationMonth();
      referencedInt=SampleCreditCardData.amex[0].getExpirationMonth();
      assert savedInt == referencedInt;
      // Check expirationYear
      savedInt=pCreditCard.getExpirationYear();
      referencedInt=SampleCreditCardData.amex[0].getExpirationYear();
      assert savedInt == referencedInt;
      // Check issuingBankName
      savedValue=pCreditCard.getIssuingBankName();
      referenceValue=SampleCreditCardData.amex[0].getIssuingBankName();
      assert savedValue != null;
      assert savedValue.length() == referenceValue.length();
      assert savedValue.equalsIgnoreCase(referenceValue);
      // Check verification
      savedValue=pCreditCard.getVerification();
      referenceValue=SampleCreditCardData.amex[0].getVerification();
      assert savedValue != null;
      assert savedValue.length() == referenceValue.length();
      assert savedValue.equalsIgnoreCase(referenceValue);
   }
}