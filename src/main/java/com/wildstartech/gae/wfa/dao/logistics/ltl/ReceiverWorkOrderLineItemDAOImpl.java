package com.wildstartech.gae.wfa.dao.logistics.ltl;

import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentReceiverWorkOrderLineItem;
import com.wildstartech.wfa.dao.logistics.ltl.ReceiverWorkOrderLineItemDAO;
import com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrderLineItem;

public class ReceiverWorkOrderLineItemDAOImpl 
extends WildDAOImpl<ReceiverWorkOrderLineItem, 
                    PersistentReceiverWorkOrderLineItem>
implements ReceiverWorkOrderLineItemDAO {
   private static final String _CLASS=
         ReceiverWorkOrderLineItemDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   @Override
   public PersistentReceiverWorkOrderLineItem findInstance(
         ReceiverWorkOrderLineItem lineItem, UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS,
            "findInstance(ReceiverWorkOrderLineItem,UserContext)",
            new Object[] {lineItem,ctx});
      logger.exiting(_CLASS,"");
      return null;
   }

   @Override
   public PersistentReceiverWorkOrderLineItem create() {
      logger.entering(_CLASS, "create()");
      PersistentReceiverWorkOrderLineItem pLineItem=null;
      pLineItem=new PersistentReceiverWorkOrderLineItemImpl();
      logger.exiting(_CLASS, "create()",pLineItem);
      return pLineItem;
   }

   @Override
   public PersistentReceiverWorkOrderLineItem create(
         ReceiverWorkOrderLineItem lineItem, UserContext ctx) {
      logger.entering(_CLASS, "create(ReceiverWorkOrderLineItem,UserContext)",
            new Object[] {lineItem,ctx});
      PersistentReceiverWorkOrderLineItemImpl pLineItem=null;
      
      pLineItem=new PersistentReceiverWorkOrderLineItemImpl();
      pLineItem.populateFromObject(lineItem);
      
      logger.entering(_CLASS, "create(ReceiverWorkOrderLineItem,UserContext)",
            pLineItem);
      return pLineItem;
   }

   @Override
   protected String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",
            PersistentReceiverWorkOrderLineItemImpl._KIND);
      return PersistentReceiverWorkOrderLineItemImpl._KIND;
   }
}