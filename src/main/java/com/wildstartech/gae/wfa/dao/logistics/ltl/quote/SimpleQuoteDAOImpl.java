package com.wildstartech.gae.wfa.dao.logistics.ltl.quote;

import java.util.logging.Logger;

import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.logistics.ltl.quote.PersistentSimpleQuote;
import com.wildstartech.wfa.dao.logistics.ltl.quote.SimpleQuoteDAO;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.SimpleQuote;

public class SimpleQuoteDAOImpl 
extends QuoteDAOBase<SimpleQuote,PersistentSimpleQuote> 
implements SimpleQuoteDAO {
   private static String _CLASS=SimpleQuoteDAOImpl.class.getName();
   private static Logger logger=Logger.getLogger(_CLASS);
   
   /**
    * Default, no-argument constructor.
    */
   public SimpleQuoteDAOImpl() {
      super();
      logger.entering(_CLASS, "QuickQuoteDAOImpl()");
      logger.exiting(_CLASS, "QuickQuoteDAOImpl()");
   }
   
   @Override
   public PersistentSimpleQuote findInstance(SimpleQuote object, UserContext ctx) throws DAOException {
      logger.entering(_CLASS, "findInstance(QuickQuote, UserContext)");
      logger.exiting(_CLASS, "findInstance(QuickQuote, UserContext)");
      return null;
   }

   @Override
   public PersistentSimpleQuote create() {
      logger.entering(_CLASS, "create()");
      PersistentSimpleQuote quote=null;
      
      quote=new PersistentSimpleQuoteImpl();
      
      logger.exiting(_CLASS, "create()",quote);
      return quote;
   }

   @Override
   public PersistentSimpleQuote create(SimpleQuote quote, UserContext ctx) {
      logger.entering(_CLASS, "create(SimpleQuote,UserContext)",new Object[] {quote,ctx});
      PersistentSimpleQuoteImpl pQuote=null;
      
      pQuote=new PersistentSimpleQuoteImpl();
      pQuote.populateFromObject(pQuote);
      
      logger.exiting(_CLASS, "create(SimpleQuote,UserContext)",pQuote);
      return pQuote;
   }
}