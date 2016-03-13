package com.wildstartech.gae.wfa.dao.finance;

import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.finance.CreditCardDAO;
import com.wildstartech.wfa.dao.finance.PersistentCreditCard;
import com.wildstartech.wfa.finance.CreditCard;

public class CreditCardDAOImpl 
extends WildDAOImpl<CreditCard, PersistentCreditCard> 
implements CreditCardDAO {
   private static final String _CLASS=CreditCardDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   @Override
   public PersistentCreditCard findInstance(
         CreditCard creditCard, UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findInstance(CreditCard, UserContext)",
            new Object[] {creditCard,ctx});
      PersistentCreditCard pCreditCard=null;
      logger.exiting(_CLASS, "findInstance(CreditCard, UserContext)",
            pCreditCard);
      return pCreditCard;
   }

   @Override
   public PersistentCreditCard create() {
      logger.entering(_CLASS,"create()");
      logger.exiting(_CLASS,"create()");
      return new PersistentCreditCardImpl();
   }

   @Override
   public PersistentCreditCard create(CreditCard creditCard, UserContext ctx) {
      logger.entering(_CLASS, "create(CreditCard,UserContext)",
            new Object[]{ creditCard,ctx});
      PersistentCreditCard pCard=null;
      
      pCard=new PersistentCreditCardImpl(creditCard);
      
      logger.exiting(_CLASS, "create(CreditCard,UserContext)",pCard);
      return pCard;
   }

   @Override
   protected String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentCreditCardImpl._KIND);
      return PersistentCreditCardImpl._KIND;
   }
}