package com.wildstartech.gae.wfa.dao.logistics.ltl.receiver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.logistics.ltl.quote.PersistentQuoteLineItem;
import com.wildstartech.wfa.dao.logistics.ltl.receiver.PersistentReceiverWorkOrderLineItem;
import com.wildstartech.wfa.dao.logistics.ltl.receiver.ReceiverWorkOrderLineItemDAO;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.receiver.ReceiverWorkOrderLineItem;

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

	@Override
	public List<PersistentReceiverWorkOrderLineItem> findByWorkOrderId(
			String workOrderId, UserContext ctx) {
		logger.entering(_CLASS, "findByWorkOrderId(String,UserContext)", 
        new Object[] {workOrderId,ctx});
        List<PersistentReceiverWorkOrderLineItem> lineItemList=null;
        Query query=null;
        QueryWrapper qw=null;
        Filter filter=null;
        String kind=null;
        
        if (
        		(
        				(workOrderId != null) && 
        				(workOrderId.length() !=0)
    			) && (ctx != null)
           ) {
          kind=PersistentReceiverWorkOrderLineItemImpl._KIND;
          query=new Query(kind);
          filter=new Query.FilterPredicate(
              "workOrderIdentifier",
              FilterOperator.EQUAL,
              workOrderId);
          query.setFilter(filter);
          qw=new QueryWrapper(query);
          lineItemList=findByQuery(qw,ctx);
        } // END if (((workOrderId != null) && (workOrderId.length() !=0)) ...        
        /**********
         * The above code should be removed once quote line items are all 
         * assigned.
         */       
        // Let's check to see if the list of items were returned.
        if (lineItemList == null) {
          // Either the quoteId or ctx were null, so return an empty list.
          lineItemList=
        		  new ArrayList<PersistentReceiverWorkOrderLineItem>();
          if (workOrderId == null) {
        	  logger.warning("The workOrderId is null.");
          } // END if (workOrderId == null)
          if ((workOrderId != null) && (workOrderId.length() == 0))
            logger.warning("The quoteId is a zero-length string.");
          if (ctx == null) logger.warning("The UserContext is null.");
        } // END if ((quoteId != null) && (ctx != null))
        logger.exiting(_CLASS, "findByWorkOrderId(String)",lineItemList);
        return lineItemList;
	}
   
   
}