package com.wildstartech.gae.wfa.dao.finance;

import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.finance.PaymentTypeDAO;
import com.wildstartech.wfa.dao.finance.PersistentPaymentType;
import com.wildstartech.wfa.finance.PaymentType;

public class PaymentTypeDAOImpl<T extends PaymentType> 
extends WildDAOImpl<PaymentType, PersistentPaymentType<T>> 
implements PaymentTypeDAO<PaymentType, PersistentPaymentType<T>> {
   private static final String _CLASS=PaymentTypeDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   @Override
   public PersistentPaymentType<T> create() {
      // TODO Auto-generated method stub
      return null;
   }


   @Override
   public PersistentPaymentType<T> findInstance(PaymentType object,
         UserContext ctx) throws DAOException {
      // TODO Auto-generated method stub
      return null;
   }


   @Override
   public PersistentPaymentType<T> create(
         PaymentType paymentType, 
         UserContext ctx) {
      logger.entering(_CLASS,"create(PaymentType,UserContext)",
            new Object[] {paymentType,ctx});
      PersistentPaymentType<T> pPaymentType=null;
      logger.entering(_CLASS,"create(PaymentType,UserContext)",pPaymentType);
      return pPaymentType;
   }

   @Override
   protected String getKind() {
      logger.entering(_CLASS,"getKind()");
      logger.exiting(_CLASS,"getKind()",PersistentPaymentTypeImpl._KIND);
      return PersistentPaymentTypeImpl._KIND;
   }   
}