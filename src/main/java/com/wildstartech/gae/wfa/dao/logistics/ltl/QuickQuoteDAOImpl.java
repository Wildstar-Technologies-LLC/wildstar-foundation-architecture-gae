package com.wildstartech.gae.wfa.dao.logistics.ltl;

import java.util.logging.Logger;

import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentQuickQuote;
import com.wildstartech.wfa.dao.logistics.ltl.QuickQuoteDAO;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.QuickQuote;

public class QuickQuoteDAOImpl
extends QuoteDAOBase<QuickQuote, PersistentQuickQuote>
implements QuickQuoteDAO {
   private static final String _CLASS=QuickQuoteDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   /**
    * Default, no-argument constructor.
    */
   public QuickQuoteDAOImpl() {
      super();
      logger.entering(_CLASS, "QuickQuoteDAOImpl()");
      logger.exiting(_CLASS, "QuickQuoteDAOImpl()");
   }
   
   @Override
   public PersistentQuickQuote findInstance(QuickQuote object, UserContext ctx) throws DAOException {
      logger.entering(_CLASS, "findInstance(QuickQuote, UserContext)");
      logger.exiting(_CLASS, "findInstance(QuickQuote, UserContext)");
      return null;
   }   

   @Override
   public PersistentQuickQuote create() {
      logger.entering(_CLASS, "create()");
      PersistentQuickQuote quote=null;
      
      quote=new PersistentQuickQuoteImpl();
      
      logger.exiting(_CLASS, "create()",quote);
      return quote;
   }

   @Override
   public PersistentQuickQuote create(QuickQuote quote, UserContext ctx) {
      logger.entering(_CLASS,"",new Object[] {quote,ctx});
      PersistentQuickQuoteImpl pQuote=null;
      
      if (quote != null) {
         pQuote=new PersistentQuickQuoteImpl();
         pQuote.populateFromObject(quote);
      } else {
         logger.warning("The specified quote object is null.");
      } // END if (quote != null)
      
      logger.exiting(_CLASS,"",pQuote);
      return pQuote;
   }
}