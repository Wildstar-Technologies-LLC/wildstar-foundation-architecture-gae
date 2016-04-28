/*
 * Copyright (c) 2013 - 2016 Wildstar Technologies, LLC.
 *
 * This file is part of Wildstar Foundation Architecture.
 *
 * Wildstar Foundation Architecture is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Wildstar Foundation Architecture is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Wildstar Foundation Architecture.  If not, see 
 * <http://www.gnu.org/licenses/>.
 * 
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the 
 * GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you 
 * permission to link this library with independent modules to produce an 
 * executable, regardless of the license terms of these independent modules, 
 * and to copy and distribute the resulting executable under terms of your 
 * choice, provided that you also meet, for each linked independent module, the
 * terms and conditions of the license of that module. An independent module is
 * a module which is not derived from or based on this library. If you modify 
 * this library, you may extend this exception to your version of the library, 
 * but you are not obliged to do so. If you do not wish to do so, delete this 
 * exception statement from your version.
 * 
 * If you need additional information or have any questions, please contact:
 *
 *      Wildstar Technologies, LLC.
 *      63 The Greenway Loop
 *      Panama City Beach, FL 32413
 *      USA
 *
 *      derek.berube@wildstartech.com
 *      www.wildstartech.com
 */
package com.wildstartech.gae.wfa.dao.finance;

import org.testng.annotations.Test;

import com.wildstartech.gae.wfa.UserData;
import com.wildstartech.gae.wfa.dao.DAOTest;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.UserContextDAOFactory;
import com.wildstartech.wfa.dao.WildDAO;
import com.wildstartech.wfa.dao.finance.CreditCardDAO;
import com.wildstartech.wfa.dao.finance.CreditCardDAOFactory;
import com.wildstartech.wfa.dao.finance.PersistentCreditCard;
import com.wildstartech.wfa.finance.CreditCard;
import com.wildstartech.wfa.finance.SampleCreditCardData;

public class CreditCardDAOTest extends DAOTest {
   
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