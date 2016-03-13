package com.wildstartech.gae.wfa.dao.finance;

import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.finance.PersistentPaymentCard;
import com.wildstartech.wfa.finance.PaymentCard;

public class PaymentCardDAOImpl 
extends WildDAOImpl<PaymentCard,PersistentPaymentCard<?>> {
   private static final String _CLASS=PaymentCardDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   @Override
   public PersistentPaymentCard<?> findInstance(PaymentCard object,
         UserContext ctx) throws DAOException {
      // TODO Auto-generated method stub
      return null;
   }
   @Override
   public PersistentPaymentCard<?> create() {
      // TODO Auto-generated method stub
      return null;
   }
   @Override
   public PersistentPaymentCard<?> create(
         PaymentCard paymentCard, 
         UserContext ctx) {
      logger.entering(_CLASS, "create(PaymentCard,UserContext)");
      PersistentPaymentCard<?> pPaymentCard=null;
      
      pPaymentCard=(PersistentPaymentCard<?>) new PersistentPaymentCardImpl<>();
      logger.exiting(_CLASS, "create(PaymentCard,UserContext)",pPaymentCard);
      return pPaymentCard;
   }
   @Override
   protected String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentPaymentCardImpl._KIND);
      return PersistentPaymentCardImpl._KIND;
   }
}