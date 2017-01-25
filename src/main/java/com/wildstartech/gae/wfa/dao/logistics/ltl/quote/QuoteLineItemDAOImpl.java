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
package com.wildstartech.gae.wfa.dao.logistics.ltl.quote;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.logistics.ltl.quote.PersistentQuoteLineItem;
import com.wildstartech.wfa.dao.logistics.ltl.quote.QuoteLineItemDAO;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.quote.QuoteLineItem;

public class QuoteLineItemDAOImpl 
extends WildDAOImpl<QuoteLineItem, PersistentQuoteLineItem> 
implements QuoteLineItemDAO {
  private static final String _CLASS=QuoteLineItemDAOImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  
  @Override
  public PersistentQuoteLineItemImpl create() {
    logger.entering(_CLASS,"create()");
    PersistentQuoteLineItemImpl lineItem=null;
    lineItem=new PersistentQuoteLineItemImpl();
    logger.exiting(_CLASS,"create()",lineItem);
    return lineItem;
  }
  
  @Override
  public PersistentQuoteLineItemImpl create(QuoteLineItem qli, UserContext ctx) {
    logger.entering(_CLASS,"create(QuoteLineItem, UserContext)",
        new Object[] {qli,ctx});
    PersistentQuoteLineItemImpl pQli=null;
    if (qli != null) {
      
    } else {
      pQli=new PersistentQuoteLineItemImpl();
    } // END if (qli != null)
    logger.exiting(_CLASS,"create(QuoteLineItem, UserContext)",pQli);
    return pQli;
  }

  @Override
  public PersistentQuoteLineItem findInstance(QuoteLineItem qli,
      UserContext ctx) throws DAOException {
    logger.entering(_CLASS,"findInstance(QuoteLineItem,UserContext",
        new Object[] {qli,ctx});
    Filter filter=null;
    Filter lengthFilter=null;
    Filter widthFilter=null;
    Filter heightFilter=null;
    Filter weightFilter=null;
    Filter quantityFilter=null;
    Filter descriptionFilter=null;
    Filter quoteIdentifierFilter=null;
    List<PersistentQuoteLineItem> lineItems=null;
    PersistentQuoteLineItem pQli=null;
    String identifier=null;
    Query query=null;
    QueryWrapper qw=null;
    
    if ((qli != null) && (ctx != null)) {
      if (qli instanceof PersistentQuoteLineItem) {
        identifier=((PersistentQuoteLineItem) qli).getIdentifier();
        if (identifier != null) {
          pQli=(PersistentQuoteLineItem) findByIdentifier(identifier, ctx);
        } // END if (identifier != null)
        if (pQli == null) {
          quoteIdentifierFilter=new FilterPredicate(
              "quoteIdentifier",
              FilterOperator.EQUAL,
              ((PersistentQuoteLineItem) qli).getQuoteIdentifier()
          );
        }
      } // END 
      if (pQli==null) {
        query=new Query(PersistentQuoteLineItemImpl._KIND);
        /** The template QuoteLineItem has not already been saved, so search. */
        lengthFilter=new FilterPredicate(
            "length",
            FilterOperator.EQUAL,
            qli.getLength());
        widthFilter=new FilterPredicate(
            "width",
            FilterOperator.EQUAL,
            qli.getWidth()
        );
        heightFilter=new FilterPredicate(
            "height",
            FilterOperator.EQUAL,
            qli.getHeight()
        );
        weightFilter=new FilterPredicate(
            "weight",
            FilterOperator.EQUAL,
            qli.getWeight());
        quantityFilter=new FilterPredicate(
            "quantity",
            FilterOperator.EQUAL,
            qli.getQuantity()
        );
        descriptionFilter=new FilterPredicate(
            "description",
            FilterOperator.EQUAL,
            qli.getDescription()
        );
        if (qli instanceof PersistentQuoteLineItem) {
          // The template is an instance of PersistentQuoteLineItem
          filter=CompositeFilterOperator.and(
              quoteIdentifierFilter,
              lengthFilter,
              widthFilter,
              heightFilter,
              weightFilter,
              quantityFilter,
              descriptionFilter
          );
        } else {
          // The template is NOT an instance of PersistentQuoteLineItem
          filter=CompositeFilterOperator.and(
              lengthFilter,
              widthFilter,
              heightFilter,
              weightFilter,
              quantityFilter,
              descriptionFilter
          );
        } // END if (qli instanceof PersistentQuoteLineItem)
      } // END if (pQli==null)
      query.setFilter(filter);
      qw=new QueryWrapper(query);
      lineItems=findByQuery(qw,ctx);
      if ((lineItems != null) && (lineItems.size() >=1)) {
        pQli=(PersistentQuoteLineItem) lineItems.get(0);
      } // END if ((lineItems != null) && (lineItems.size() >=1))
    } else {
      if (qli == null) logger.warning("QuoteLineItem parameter was null.");
      if (ctx == null) logger.warning("UserContext parameter was null.");
    } // END if ((qli != null) && (ctx != null))
    logger.exiting(_CLASS,"findInstance(QuoteLineItem,UserContext",pQli);
    return pQli;
  }

  @Override
  public List<PersistentQuoteLineItem> 
    findByQuoteIdentifier(String quoteId, UserContext ctx) {
    logger.entering(_CLASS, "findByQuoteIdentifier(String,UserContext)", 
        new Object[] {quoteId,ctx});
        List<PersistentQuoteLineItem> lineItemList=null;
        Query query=null;
        QueryWrapper qw=null;
        Filter filter=null;
        String kind=null;
        
        if (((quoteId != null) && (quoteId.length() !=0)) && (ctx != null)) {
          kind=PersistentQuoteLineItemImpl._KIND;
          query=new Query(kind);
          filter=new Query.FilterPredicate(
              "quoteIdentifier",
              FilterOperator.EQUAL,
              quoteId);
          query.setFilter(filter);
          qw=new QueryWrapper(query);
          lineItemList=findByQuery(qw,ctx);
        } // END if (((quoteId != null) && (quoteId.length() !=0)) ...        
        /**********
         * The above code should be removed once quote line items are all 
         * assigned.
         */       
        // Let's check to see if the list of items were returned.
        if (lineItemList == null) {
          // Either the quoteId or ctx were null, so return an empty list.
          lineItemList=new ArrayList<PersistentQuoteLineItem>();
          if (quoteId == null) logger.warning("The quoteId is null.");
          if ((quoteId != null) && (quoteId.length() == 0))
            logger.warning("The quoteId is a zero-length string.");
          if (ctx == null) logger.warning("The UserContext is null.");
        } // END if ((quoteId != null) && (ctx != null))
        logger.exiting(_CLASS, "findByQuoteIdentifier(String)",lineItemList);
        return lineItemList;
  }
  /**
   * Returns the <em>Kind</em> property of the entity which is used for the 
   * purpose of querying the Datastore.
   * 
   * @return A string value which is used by the Datastore for the purpose of
   * categorizing entities of this object's type to provide the ability to 
   * querying the Datastore and retrieve entities. 
   */
  protected final String getKind() {
     logger.entering(_CLASS, "getKind()");
     logger.exiting(_CLASS, "getKind()",PersistentQuoteLineItemImpl._KIND);
     return PersistentQuoteLineItemImpl._KIND;
  }
}