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
package com.wildstartech.gae.wfa.dao.logistics.ltl;

import java.util.Date;
import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.PersistentRuleImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.finance.CreditCardDAO;
import com.wildstartech.wfa.dao.finance.CreditCardDAOFactory;
import com.wildstartech.wfa.dao.finance.PersistentCreditCard;
import com.wildstartech.wfa.finance.CreditCard;

public class ReconcileCreditCard
extends PersistentRuleImpl<PersistentQuoteImpl> {
   private static final String _CLASS=ReconcileCreditCard.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   public ReconcileCreditCard(UserContext ctx) {
      super(ctx);
      logger.entering(_CLASS, "ReconcileCreditCard(UserContext)");
      logger.exiting(_CLASS, "ReconcileCreditCard(UserContext)");
   }
   //********** Utility Methods
   /**
    * Compare the specified account numbers.
    * @param card The {@code accountNumber} associated with the creditCard
    * that was retrieved from the persistent data store.
    * @param quote The (@code creditCardNumber} property from the quote. 
    * @return {@code true} if the account numbers are different or {@code false}
    * if the {@code accountNumber} properties match up.
    */
   private boolean compareAccountNumbers(String card, String quote) {
      logger.entering(_CLASS, "compareAccountNumber(String,String)",
            new Object[] {card,quote});
      boolean changeDetected=false;
      
      if (isEmpty(quote) && (!isEmpty(card))) {
         // If the account number in the quote is blank, but the account
         // number in the card is not blank, then it qualifies as a change.
         changeDetected=true;
      } else if (
            // The quoteProperty DOES NOT start with a masking character
            (!quote.startsWith("*")) &&
            // The quoteProperty is different from the cardProperty
            (!quote.equalsIgnoreCase(card))
         ) {
         changeDetected=true;
      } // END if ((!isEmpty(quoteProperty)) && (!quoteProperty.equals...
      logger.exiting(_CLASS, "",changeDetected);
      return changeDetected;
   }
   /**
    * Compare the specified credit card brand names.
    * @param card The {@code brandName} associated with the creditCard
    * that was retrieved from the persistent data store.
    * @param quote The (@code brandName} property from the quote. 
    * @return {@code true} if the brand names are different or {@code false}
    * if the {@code brandName} properties match up.
    */
   private boolean compareBrandName(String card, String quote) {
      logger.entering(_CLASS, "compareBrandName(String,String)",
            new Object[] {});
      boolean changeDetected=false;
      if (
            (isEmpty(quote)) ||
            (!quote.equalsIgnoreCase(card))
         ) {
         changeDetected=true;         
      } // END if ((!isEmpty(quoteProperty)) && (!quoteProperty.equals...
      
      logger.exiting(_CLASS, "compareBrandName(String,String)",changeDetected);
      return changeDetected;
   }
   /**
    * Compare the specified credit card holder names.
    * @param card The {@code cardHolderName} associated with the creditCard
    * that was retrieved from the persistent data store.
    * @param quote The (@code cardHolderName} property from the quote. 
    * @return {@code true} if the card holder names are different or 
    * {@code false} if the {@code cardHolderName} properties match up.
    */
   private boolean compareCardHolderName(String card, String quote) {
      logger.entering(_CLASS, "compareCardHolderName(String,String)",
            new Object[] {});
      boolean changeDetected=false;
      if (
            (isEmpty(quote)) ||
            (!quote.equalsIgnoreCase(card))
         ) {
         changeDetected=true;         
      } // END if ((!isEmpty(quoteProperty)) && (!quoteProperty.equals...
      
      logger.exiting(_CLASS, "compareCardHolderName(String,String)",
            changeDetected);
      return changeDetected;
   }
   /**
    * Compare the specified credit card expiration dates.
    * @param card The {@code expirationDate} associated with the creditCard
    * that was retrieved from the persistent data store.
    * @param quote The (@code expirationDate} property from the quote. 
    * @return {@code true} if the expiration dates are different or 
    * {@code false} if the {@code expirationDate} properties match up.
    */
   private boolean compareExpirationDate(Date card, Date quote) {
      logger.entering(_CLASS, "compareExpirationDate(Date,Date)",
            new Object[] {});
      boolean changeDetected=false;
      
      if ((card == null) && (quote != null)) {
         changeDetected=true;
      } else {
         // The expiration date stored in the card is NOT null.
         if (
               (quote == null) ||
               (card.compareTo(quote)) != 0) {
            /* The quoteDate is either NULL or the quoteDate is not equal to
             * the cardDate.  If so, the creditCard will be updated to
             * reflect the quoteDate. */
            changeDetected=true;
         } // END if ((quoteDate == null) || (cardDate.compareTo(quoteDate...            
      } // END if (((cardDate == null) && (quoteDate != null)) ||
      
      logger.exiting(_CLASS, "compareExpirationDate(String,String)",
            changeDetected);
      return changeDetected;
   }
   
   /**
    * Compare the specified credit card verification data.
    * @param card The {@code verification} associated with the creditCard
    * that was retrieved from the persistent data store.
    * @param quote The (@code verification} property from the quote. 
    * @return {@code true} if the expiration dates are different or 
    * {@code false} if the {@code verification} properties match up.
    */
   private boolean compareVerification(String card, String quote) {
      logger.entering(_CLASS, "compareVerification(String,String)",
            new Object[] {});
      boolean changeDetected=false;
      
      if (
            (isEmpty(card)) ||
            (!quote.equalsIgnoreCase(card))
         ) {
         changeDetected=true;
      } // END if ((!isEmpty(quoteProperty)) && (!quoteProperty.equals...
      
      logger.exiting(_CLASS, "compareVerification(String,String)",
            changeDetected);
      return changeDetected;
   }
   
   public boolean isCardBlank(CreditCard card) {
      logger.entering(_CLASS, "isCardBlank(CreditCard)",card);
      boolean isEmpty=true;
      
      // accountNumber
      isEmpty=isEmpty(card.getAccountNumber());
      // brandName
      // Don't care about brand name when determining if card record is "blank"
      // cardHolderName
      if (isEmpty) { isEmpty=isEmpty(card.getCardHolderName()); }
      // description
      // Don't care about description.
      // expirationMonth
      if (isEmpty) { 
         if (card.getExpirationMonth() == 0) { 
            isEmpty=true;
         } else{ 
            isEmpty=false;
         } // END if (card.getExpirationMonth() == null) 
      } // END if (isEmpty)
      // expirationYear
      if (isEmpty) { 
         if (card.getExpirationYear() == 0) { 
            isEmpty=true;
         } else{ 
            isEmpty=false;
         } // END if (card.getExpirationYear() == null) 
      } // END if (isEmpty)
      // issuingBankName
      // dont' care about issuing bank name
      // verification
      if (isEmpty) { isEmpty=isEmpty(card.getVerification()); }
      logger.entering(_CLASS, "isCardBlank(CreditCard)",isEmpty);
      return isEmpty;
   }
   //********** Rule Method
   @Override
   public void apply(PersistentQuoteImpl pQuote) {
      logger.entering(_CLASS,"apply(PersistentQuote)",pQuote);
      boolean changeDetected=false;
      boolean result=false;
      int tmpInt=0;
      PersistentCreditCard creditCard=null;
      CreditCardDAO dao=null;
      CreditCardDAOFactory factory=null;
      Date quoteDate=null;
      Date cardDate=null;
      String creditCardIdentifier=null;
      String quoteProperty=null;
      String cardProperty=null; 
      UserContext ctx=null;
      
      if (pQuote != null) {
         // Obtain a reference to the current user.
         ctx=getCurrentUser();
         /* Let's get the a reference to the data access object used to get the 
          * credit card. */     
         factory=new CreditCardDAOFactory();
         dao=factory.getDAO();
         // Let's get the current credit card identiifer information.
         creditCardIdentifier=pQuote.getCreditCardIdentifier();
         if (
               (creditCardIdentifier != null) && 
               (creditCardIdentifier.length() > 0)
            ) {
            // The credit card identifier is NOT null, so let's pull it back
            creditCard=dao.findByIdentifier(creditCardIdentifier,ctx);
         } // END if ((creditCardIdentifier != null) && ...
         if (creditCard == null) {
            creditCard=dao.create();
         } // END if (creditCard == null)
         
         //********** Let's compare differences
         // creditCardNumber (Quote) / accountNumber (CreditCard)
         cardProperty=creditCard.getAccountNumber();
         quoteProperty=pQuote.getCreditCardNumber();
         result=compareAccountNumbers(cardProperty,quoteProperty);
         if (result) {
            changeDetected=true;
            creditCard.setAccountNumber(quoteProperty);
         } // END if (result)
         // creditCardType (Quote) / brandName (CreditCard)
         cardProperty=creditCard.getBrandName();
         quoteProperty=pQuote.getCreditCardType();
         result=compareBrandName(cardProperty,quoteProperty);
         if (result) {
            changeDetected=true;
            creditCard.setBrandName(quoteProperty);
         } // END if (result)
         // cardholderName
         cardProperty=creditCard.getCardHolderName();
         quoteProperty=pQuote.getCreditCardName();
         result=compareCardHolderName(cardProperty,quoteProperty);
         if (result) {
            changeDetected=true;
            creditCard.setCardHolderName(quoteProperty);
         } // END if (result)
         // expirationMonth
         tmpInt=pQuote.getCreditCardExpirationMonth();
         if (tmpInt != creditCard.getExpirationMonth()) {
             changeDetected = true;
             creditCard.setExpirationMonth(tmpInt);
         } // END if (tmpInt != creditCard.getExpirationMonth())
         // expirationYear
         tmpInt=pQuote.getCreditCardExpirationYear();
         if (tmpInt != creditCard.getExpirationYear()) {
             changeDetected = true;
             creditCard.setExpirationYear(tmpInt);
         } // END if (tmpInt != creditCard.getExpirationYear())
         // cardVerification
         cardProperty=creditCard.getVerification();
         quoteProperty=pQuote.getCreditCardVerification();
         result=compareVerification(cardProperty,quoteProperty);
         if (result) {
            changeDetected=true;
            creditCard.setVerification(quoteProperty);
         } // END if (result)
         
         // So let's look at what we need to do.
         if (isCardBlank(creditCard)) {
            if (!isEmpty(creditCard.getIdentifier())) {
               dao.deleteByIdentifier(creditCardIdentifier, ctx);
            } // END if (!isEmpty(creditCard.getIdentifier()))
            pQuote.setCreditCardIdentifier(null);
         } else {
            if (changeDetected) {
               creditCard=dao.save(creditCard, ctx);
               // Update the quote with information.
               pQuote.setCreditCardIdentifier(creditCard.getIdentifier());
               pQuote.setCreditCardExpirationMonth(
                       creditCard.getExpirationMonth());
               pQuote.setCreditCardExpirationYear(
                       creditCard.getExpirationYear());
               pQuote.setCreditCardName(creditCard.getCardHolderName());
               pQuote.setCreditCardNumber(creditCard.getAccountNumber());
               pQuote.setCreditCardType(creditCard.getBrandName());
               pQuote.setCreditCardVerification(creditCard.getVerification());               
            } // END if (changeDetected)
         } // END if (isCardBlank(creditCard))
      } // END if (pQuote != null)      
      
      logger.exiting(_CLASS, "apply(PersistentQuote)");
   }
}