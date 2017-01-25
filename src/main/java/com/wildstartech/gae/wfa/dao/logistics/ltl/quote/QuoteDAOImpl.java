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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.gae.wfa.dao.journal.JournalDAOImpl;
import com.wildstartech.gae.wfa.dao.journal.PersistentJournalEntryImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.logistics.ltl.quote.PersistentQuote;
import com.wildstartech.wfa.dao.logistics.ltl.quote.PersistentQuoteLineItem;
import com.wildstartech.wfa.dao.logistics.ltl.quote.QuoteDAO;
import com.wildstartech.wfa.dao.logistics.ltl.quote.QuoteLineItemDAO;
import com.wildstartech.wfa.dao.logistics.ltl.quote.QuoteLineItemDAOFactory;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.quote.Quote;
import com.wildstartech.wfa.logistics.ltl.quote.QuoteLineItem;

/**
 * Save a copy of the <code>Quote</code> to the persistent data store.
 * <table border="0">
 * <tr>
 * <td><strong>NOTE:</strong></td>
 * <td><strong>
 * <p>
 * This Data Access Object ONLY persists the quote itself. It DOES NOT persist
 * any associated line item and/or accessorial charges.
 * </p>
 * <p>
 * It is the responsibility of the caller to ensure that those entities are
 * saved.
 * </p>
 * </td>
 * </tr>
 * </table>
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 *
 */
public class QuoteDAOImpl extends WildDAOImpl<Quote, PersistentQuote>
implements QuoteDAO {
   private static final String _CLASS = QuoteDAOImpl.class.getName();
   private static final Logger logger = Logger.getLogger(_CLASS);

   private static final QuoteShardedCounter requestIdGenerator = new QuoteShardedCounter();

   // ********** Implementation of super-class methods
   @Override
   public PersistentQuoteImpl create() {
      logger.entering(_CLASS, "create()");
      PersistentQuoteImpl quote = null;
      quote = new PersistentQuoteImpl();
      logger.exiting(_CLASS, "create()", quote);
      return quote;
   }

   @Override
   public PersistentQuoteImpl create(Quote quote, UserContext ctx) {
      logger.entering(_CLASS, "create(Quote,UserContext)",
            new Object[] { quote, ctx });
      PersistentQuoteImpl pQuote = null;
      if ((quote != null) && (ctx != null)) {
         // Neither the Quote nor UserContext were null.
         // Let's see if it is an existing instance.
         pQuote = (PersistentQuoteImpl) findInstance(quote, ctx);
         if (pQuote == null) {
            pQuote = (PersistentQuoteImpl) create();
            pQuote.populateFromObject(quote);
         } // END if (pQuote == null)
      } else {
         if (quote == null)
            logger.warning("Quote parameter is null.");
         if (ctx == null)
            logger.warning("UserContext parameter is null.");
      }
      logger.exiting(_CLASS, "create(Quote,UserContext)");
      return pQuote;
   }

   /**
    * Save method responsible for persisting the Quote entity.
    * 
    * <p>
    * Overriding the WildDAOImpl superclass method because we want to intercept
    * that method as there are children that need to be saved.
    * </p>
    */
   @Override
   public PersistentQuoteImpl save(Quote quote, UserContext ctx,
         Transaction txn) {
      logger.entering(_CLASS, "save(Quote,UserContext,Transaction)",
            new Object[] { quote, ctx, txn });
      JournalDAOImpl journalDAO=null;
      List<QuoteLineItem> lineItems = null;
      List<PersistentQuoteLineItem> lineItemsToDelete = null;
      PersistentJournalEntryImpl pJournalEntry=null;
      PersistentQuoteImpl pQuote = null;
      PersistentQuoteLineItemImpl pQli = null;
      QuoteLineItemDAO qliDAO = null;
      QuoteLineItemDAOFactory qliFactory = null;
      ReconcileCreditCard reconcileCreditCard=null;      
      String identifier = "";
      String nextIdValue = "";
      String quoteIdentifier = "";
      String requestId = "";
      String requestIdTemplate = "Q00000000000000";
      StringBuilder sb = null;

      if ((quote != null) && (ctx != null)) {
         // Get the quote identifier
         requestId = quote.getRequestId();
         // Get a reference to the current, saved version of the quote.
         if (!isEmpty(requestId)) {
            // Yes there is a request Id, let's use it to get the current
            // version of the object.
            pQuote=(PersistentQuoteImpl) findByRequestId(requestId,ctx);            
         } else {
            // No, there is no requestId, so let's generate one....
            sb = new StringBuilder(30);
            nextIdValue = String.valueOf(requestIdGenerator.nextId());
            sb.append(
                  requestIdTemplate.substring(0, 15 - nextIdValue.length()));
            sb.append(nextIdValue);
            requestId=sb.toString();       
         } // END if (!isEmpty(requestId))
         // Let's check to see if the persistent quote was found...
         if (pQuote == null) {
            // Nope, so let's create a new persistent object.
            pQuote=create();
         } // END if (pQuote == null)
         // So let's update the persistent quote from the specified quote.
         pQuote.updateFromObject(quote);
         pQuote.setRequestId(requestId);
         // ******************** Process Rules
         reconcileCreditCard=new ReconcileCreditCard(ctx);
         reconcileCreditCard.apply(pQuote);
         
         /* Get the list of line items that will need to be saved */
         lineItems = pQuote.getLineItems();
         /* Get the list of line items that will need to be removed. */
         lineItemsToDelete = pQuote.getLineItemsToDelete(); 
         /* Get the journal entry. */
         pJournalEntry=(PersistentJournalEntryImpl) pQuote.getNewJournalEntry();
         
         /* Saving the quote will return ONLY the parent object.  The children
          * will not be present in the object that is returned. */
         pQuote = (PersistentQuoteImpl) super.save(pQuote, ctx, txn);
         
         // ******************** Journal Entry ********************
         if (!pJournalEntry.isEmpty()) {
            // The journal contains data, so let's save it.
            journalDAO=new JournalDAOImpl();
            pJournalEntry.setRelatedKind(getKind());
            pJournalEntry.setRelatedIdentifier(pQuote.getIdentifier());
            pJournalEntry=(PersistentJournalEntryImpl)
                  journalDAO.save(pJournalEntry, ctx);
         } // END if (!pJournalEntry.isEmpty())
         
         // ******************** Line Items ********************
         /* quoteIdentifier will be saved with the QuoteLineItem instances */
         quoteIdentifier = pQuote.getIdentifier();
         qliFactory = new QuoteLineItemDAOFactory();
         qliDAO = qliFactory.getDAO();
         // ***** Iterate through the QuoteLineItems and save them.
         for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem qli = lineItems.get(i);
            if (!(qli instanceof PersistentQuoteLineItemImpl)) {
               // The Quote Line Item IS NOT a persistent entity.
               pQli = (PersistentQuoteLineItemImpl) qliDAO.findInstance(qli,
                     ctx);
            } else {
               identifier = ((PersistentQuoteLineItemImpl) qli).getIdentifier();
               if (!isEmpty(identifier)) {
                  pQli = (PersistentQuoteLineItemImpl) qliDAO
                        .findByIdentifier(identifier, ctx);
               } else {
                  logger.info(
                   "The entity is a persistent one, but it hasn't been saved.");
               } // END if (identifier != null)
            } // END if (!(qli instanceof PersistentQuoteLineItemImpl))
            if (pQli == null) {
               // An existing line item WAS NOT found.
               pQli = (PersistentQuoteLineItemImpl) qliDAO.create();
            } // END if (pQli == null)
              // Popluate with line item data
              // Populate the object with information from the passsed object.
            pQli.populateFromObject(qli);
            // Associate the QuoteLineItem with the quote.
            pQli.setQuoteIdentifier(quoteIdentifier);
            // Save the QuoteLineItem
            pQli = (PersistentQuoteLineItemImpl) ((QuoteLineItemDAOImpl) qliDAO)
                  .save(pQli, ctx, txn);
            // Add the saved line item to the persistent quote
            pQuote.addLineItem(pQli);
            qli = null;
            pQli = null;
         } // END for (QuoteLineItem qli: lineItems)
         
         // Remove QuoteLineItems No longer associated.
         for (PersistentQuoteLineItem item : lineItemsToDelete) {
            // Get the identifier.
            identifier = item.getIdentifier();
            // Remove the object.
            qliDAO.deleteByIdentifier(identifier, ctx);
         } // END for (PersistentQuoteLineItem item:
      } else {
         // Either the Quote object was null or the UserContext object was null.
         if (quote == null)
            logger.warning("The Quote object was null.");
         if (ctx == null)
            logger.warning("The UserContext object was null.");
      } // END if ((quote != null) && (ctx !=null))

      logger.entering(_CLASS, "save(Quote,UserContext,Transaction)", pQuote);
      return pQuote;
   }

   /**
    * Find the persistent version of the specified quote.
    */
   @Override
   public PersistentQuote findInstance(Quote quote, UserContext ctx)
         throws DAOException {
      // TODO Auto-generated method stub
      return null;
   }

// ********** Search Methods
   /**
    * Returns a list of quotes that require action.
    * <p>
    * Returns a list of all quotes that are in a "New" status state or a
    * "Pending" status state with a value of "Approval (Justo)".
    * </p>
    
    */
   public final List<PersistentQuote> findActionable(UserContext ctx) {
      logger.entering(_CLASS, "findActionable(UserContext)", ctx);
      Filter userFilter=null;
      List<Filter> filters = null;
      List<Filter> tmpFilters = null;
      List<PersistentQuote> quotes = null;
      Query query = null;
      QueryWrapper qw = null;
      Query.Filter filter = null;
      String currentUser = null;

      if (ctx != null) {
         query = new Query(PersistentQuoteImpl._KIND);
         // Let's build the list of filters for the query.
         filters = new ArrayList<Filter>();
         
         
         // We want ALL quotes with a value of "New" for the 'Status' field.
         filters.add(
               new FilterPredicate("statusState", FilterOperator.EQUAL, "New"));
         // We want ALL quotes with a value of "Assigned" for the 'Status'
         // field.
         filters.add(new FilterPredicate("statusState", FilterOperator.EQUAL,
               "Assigned"));
         /*
          * We want all quotes with a value of "Pending" for the "Status" field
          * and "Approval (Justo)" as the value for the 'Status Reason' field.
          */
         tmpFilters = new ArrayList<Filter>();
         tmpFilters.add(new FilterPredicate("statusState", FilterOperator.EQUAL,
               "Pending"));
         tmpFilters.add(new FilterPredicate("statusReason",
               FilterOperator.EQUAL, "Approval (Justo)"));
         filters.add(new Query.CompositeFilter(
               Query.CompositeFilterOperator.AND, tmpFilters));
         filter = new Query.CompositeFilter(Query.CompositeFilterOperator.OR,
               filters);

         /* ***** BEGIN: User Filtering */
         currentUser = ctx.getUserName();
         if (
               (currentUser != null) && 
               (!currentUser.equalsIgnoreCase("transit.systems@justodelivery.com")) &&
               (currentUser.endsWith("justodelivery.com"))
            ) {
            // No-Op
            // This is a Justo Employee, so ALL records are welcome.
         } else {
            tmpFilters = new ArrayList<Filter>();
            tmpFilters.add(new FilterPredicate("createdBy",
                  FilterOperator.EQUAL, currentUser));
            tmpFilters.add(new FilterPredicate("contactEmail",
                  FilterOperator.EQUAL, currentUser));
            userFilter = new Query.CompositeFilter(
                  Query.CompositeFilterOperator.OR, tmpFilters);
         } // END if ((currentUser != null) && ...
         /* END: Add filter for transit.systems@justodelivery.com */
         if (userFilter != null) {
            tmpFilters=new ArrayList<Filter>();
            tmpFilters.add(filter);
            tmpFilters.add(userFilter);
            filter=new Query.CompositeFilter(
                  Query.CompositeFilterOperator.AND, 
                  tmpFilters);
         } // END if (userFilter != null)
         /* ***** END: User Filtering */
         
         query.setFilter(filter);
         qw = new QueryWrapper(query);
         quotes = findByQuery(qw, ctx);
      } else {
         logger.severe("The UserContext is null.");
      } // END if (ctx != null)

      logger.exiting(_CLASS, "findActionable(UserContext)", quotes);
      return quotes;
   }
   
   /**
    * Finds a list of all quotes in the system.
    * 
    * <p>
    * This method was deployed in an effort to provide the ability to filter out
    * quotes for specific users.
    * </p>
    */
   @Override
   public final List<PersistentQuote> findAll(UserContext ctx) {
      logger.entering(_CLASS, "findAll(UserContext)", ctx);
      Query query = null;
      Filter filter = null;
      Filter userFilter=null;
      List<Filter> filters=null;
      List<PersistentQuote> results = null;
      QueryWrapper qw = null;
      String currentUser = null;
      String kind = null;
      String msg = null;
      StringBuilder sb = null;

      if ((ctx != null) && (ctx.isAuthenticated())) {
         kind = getKind();
         query = new Query(kind);

         /* ***** BEGIN: User Filtering */
         currentUser = ctx.getUserName();
         if (
               (currentUser != null) && 
               (!currentUser.equalsIgnoreCase("transit.systems@justodelivery.com")) &&
               (currentUser.endsWith("justodelivery.com"))
            ) {
            // No-Op
            // This is a Justo Employee, so ALL records are welcome.
         } else {
            filters = new ArrayList<Filter>();
            filters.add(new FilterPredicate("createdBy",
                  FilterOperator.EQUAL, currentUser));
            filters.add(new FilterPredicate("contactEmail",
                  FilterOperator.EQUAL, currentUser));
            userFilter = new Query.CompositeFilter(
                  Query.CompositeFilterOperator.OR, filters);
         } // END if ((currentUser != null) && ...
         if (userFilter != null) {
            query.setFilter(userFilter);
         } // END if (userFilter != null)
         /* ***** END: User Filtering */

         qw = new QueryWrapper(query);
         results = findByQuery(qw, ctx);
      } else {
         // The specified context was either null or has not been authenticated.
         if (ctx == null) {
            msg = ("The UserContext parameter was null.");
         } else if (!ctx.isAuthenticated()) {
            sb = new StringBuilder(80);
            sb.append("The specified UserContext, ").append(ctx.getUserName());
            sb.append(", is not authenticated.");
            msg = sb.toString();
         } // END if (ctx == null)
         logger.fine(msg);

      } // END if ((ctx != null) && (ctx.isAuthenticated()))

      logger.exiting(_CLASS, "findAll(UserContext)", results);
      return results;
   }

   /**
    * Finds a list of all active quotes.
    * <p>
    * Returns a list of all active quotes that are in one of the following
    * status states.
    * </p>
    * <ul>
    * <li>New</li>
    * <li>Assigned</li>
    * <li>Pending</li>
    * <li>Accepted</li>
    * </ul>
    */
   public final List<PersistentQuote> findAllActive(UserContext ctx) {
      logger.entering(_CLASS, "findAllActive(UserContext)", ctx);
      Filter filter = null;
      Filter userFilter=null;
      List<Filter> filters = null;
      List<PersistentQuote> quotes = null;
      Query query = null;
      QueryWrapper qw = null;
      String currentUser = null;
      String kind=null;

      if (ctx != null) {
         kind=getKind();
         query = new Query(kind);
         
         // Let's build the list of composite queries.
         filters = new ArrayList<Filter>();
         filters.add(
               new FilterPredicate("statusState", FilterOperator.EQUAL, "New"));
         filters.add(new FilterPredicate("statusState", FilterOperator.EQUAL,
               "Assigned"));
         filters.add(new FilterPredicate("statusState", FilterOperator.EQUAL,
               "Pending"));
         filters.add(new FilterPredicate("statusState", FilterOperator.EQUAL,
               "Accepted"));
         filter = new Query.CompositeFilter(Query.CompositeFilterOperator.OR,
               filters);
         
         /* ***** BEGIN: User Filtering */
         currentUser = ctx.getUserName();
         if (
               (currentUser != null) && 
               (!currentUser.equalsIgnoreCase("transit.systems@justodelivery.com")) &&
               (currentUser.endsWith("justodelivery.com"))
            ) {
            // No-Op
            // This is a Justo Employee, so ALL records are welcome.
         } else {
            filters = new ArrayList<Filter>();
            filters.add(new FilterPredicate("createdBy",
                  FilterOperator.EQUAL, currentUser));
            filters.add(new FilterPredicate("contactEmail",
                  FilterOperator.EQUAL, currentUser));
            userFilter = new Query.CompositeFilter(
                  Query.CompositeFilterOperator.OR, filters);
            filters=new ArrayList<Filter>();
            filters.add(filter);
            filters.add(userFilter);
            filter= new Query.CompositeFilter(
                  Query.CompositeFilterOperator.AND, filters);
         } // END if ((currentUser != null) && ...
         /* ***** END: User Filtering */
         
         query.setFilter(filter);
         // Run the query
         qw = new QueryWrapper(query);
         quotes = findByQuery(qw, ctx);
      } else {
         logger.severe("The UserContext parameter was null.");
      } // END if (ctx != null)
      return quotes;
   }

   /**
    * Finds the quote represented by the specific request id.
    * <p>
    * Locates a specific quote whose value for the <code>Request Id</code> field
    * matches the value passed as the <code>requestId</code> parameter.
    * </p>
    */
   public PersistentQuote findByRequestId(String requestId, UserContext ctx) {
      logger.entering(_CLASS, "findByRequestId(String)", requestId);
      Filter filter = null;
      Filter userFilter=null;
      List<PersistentQuote> quotes = null;
      List<Filter> filters = null;
      PersistentQuote quote = null;
      Query query = null;
      QueryWrapper qw = null;
      String currentUser = null;
      String kind=null;

      if (
            (requestId != null) && 
            (requestId.length() > 0) && 
            (ctx != null)
         ) {
         kind=getKind();
         query = new Query(kind);

         filter=new Query.FilterPredicate("requestId",
               Query.FilterOperator.EQUAL, requestId);
         
         /* ***** BEGIN: User Filtering */
         currentUser = ctx.getUserName();
         if (
               (currentUser != null) && 
               (!currentUser.equalsIgnoreCase("transit.systems@justodelivery.com")) &&
               (currentUser.endsWith("justodelivery.com"))
            ) {
            // No-Op
            // This is a Justo Employee, so ALL records are welcome.
         } else {
            // Filter based upon name of the currently logged in user.
            filters = new ArrayList<Filter>();
            filters.add(new FilterPredicate("createdBy",
                  FilterOperator.EQUAL, currentUser));
            filters.add(new FilterPredicate("contactEmail",
                  FilterOperator.EQUAL, currentUser));
            userFilter = new Query.CompositeFilter(
                  Query.CompositeFilterOperator.OR,filters);
            filters=new ArrayList<Filter>();
            filters.add(filter);
            filters.add(userFilter);
            filter=new Query.CompositeFilter(
                  Query.CompositeFilterOperator.AND,
                  filters);
         } // END if ((currentUser != null) && ...
         /* ***** END: User Filtering */

         query.setFilter(filter);
         qw = new QueryWrapper(query);
         quotes = findByQuery(qw, ctx);
         if (
               (quotes != null) && 
               (quotes.size() != 0)
            ) {
            quote = quotes.get(0);
         } else {
            logger.warning(
                  "A quote with the specified requestId was not found.");
         } // END if ((quotes != null) && (quotes.size() != 0))
      } else {
         if ((requestId == null) || (requestId.length() == 0)) {
            logger.warning("No requestId parameter was specified.");
         } else {
            logger.warning("The UserContext parameter was null.");
         } // END if ((requestId == null) || (requestId.length() == 0))
      } // END if ((requestId != null) && (requestId.length() > 0) && ...

      logger.entering(_CLASS, "findByRequestId(String)", quote);
      return quote;
   }

   /*
    * Displays quotes with specified status modified since the specified date.
    * 
    * <p>If a <code>null</code> value is passed for the "<code>status</code>"
    * parameter, then results will NOT be filtered based upon any value in the
    * "<code>status</code>" property.</p> <p>If a <code>null</code> value is
    * passed for the "<code>statusReason</code>" parameter, then the results
    * will NOT be filtered based upon any value in the
    * "<code>statusReason</code>" property.</p> <p>If a <code>null</code> value
    * is passed for the "<code>minDateModified</code>" parameter, then a value
    * that is ninety days prior to the current date/time will be used.</p>
    * 
    */
   public final List<PersistentQuote> findByStatus(String status, String statusReason,
         Date minDateModified, UserContext ctx) {
      logger.entering(_CLASS, "findByStatus(String,String,Date,UserContext)",
            new Object[] { status, statusReason, minDateModified, ctx });
      Calendar calendar = null;
      Filter filter = null;
      Filter userFilter=null;
      List<Filter> filters = null;
      List<PersistentQuote> quotes = null;
      Query query = null;
      QueryWrapper qw = null;
      String currentUser = null;
      TimeZone tz = null;

      if (ctx != null) {
         query = new Query(getKind());
         // Let's build the list of composite queries.
         if (
               (status == null) && 
               (statusReason == null) && 
               (minDateModified == null)
            ) {
            logger.warning("The search parameters are all null.");
         } else {
            // At least one of the three critical search parameters is NOT
            // null...
            // Create a list of filters
            filters = new ArrayList<Filter>();

            // If the status parameter is specified, then use it.
            if (status != null) {
               filters.add(new FilterPredicate("statusState",
                     FilterOperator.EQUAL, status));
            } // END if (status != null)

            // If the statusReason parameter is specified, then use it.
            if (statusReason != null) {
               filters.add(new FilterPredicate("statusReason",
                     FilterOperator.EQUAL, statusReason));
            } // END if (statusReason != null)

            // If the minDate parameter is not specified, use last 90 days.
            if (minDateModified == null) {
               // The 'minDateModified' parameter was null, so use the last 90
               // days.
               tz = ctx.getTimeZone();
               calendar = new GregorianCalendar(tz);
               calendar.add(Calendar.DAY_OF_YEAR, -90);
               minDateModified = calendar.getTime();
            } // END if (minDateModified == null)
            filters.add(new FilterPredicate("dateModified",
                  FilterOperator.GREATER_THAN, minDateModified));
            
            /* Lets add the filters together */
            filter=new Query.CompositeFilter(
                  Query.CompositeFilterOperator.AND,
                  filters);
            

            /* ***** BEGIN: User Filtering */
            currentUser = ctx.getUserName();
            if (
                  (currentUser != null) && 
                  (!currentUser.equalsIgnoreCase("transit.systems@justodelivery.com")) &&
                  (currentUser.endsWith("justodelivery.com"))
               ) {
               // No-Op
               // This is a Justo Employee, so ALL records are welcome.
            } else {
               filters = new ArrayList<Filter>();
               filters.add(new FilterPredicate("createdBy",
                     FilterOperator.EQUAL, currentUser));
               filters.add(new FilterPredicate("contactEmail",
                     FilterOperator.EQUAL, currentUser));
               userFilter = new Query.CompositeFilter(
                     Query.CompositeFilterOperator.OR, filters);
            } // END if ((currentUser != null) && ...
            /* ***** END: User Filtering */
            
            if (userFilter != null) {
               filters = new ArrayList<Filter>();
               filters.add(filter);
               filters.add(userFilter);
               filter= new Query.CompositeFilter(
                     Query.CompositeFilterOperator.AND, filters);
            } // END if (userFilter != null)
            
            query.setFilter(filter);

            /*
             * The initial sort of dateModified in ascending order was added
             * because of the following message displayed by App Engine:
             * 
             * The first sort property must be the same as the property to which
             * the inequality filter is applied. In your query the first sort
             * property is dateCreated but the inequality filter is on
             * dateModified
             */
            query.addSort("dateModified", SortDirection.ASCENDING);
         } // END if ((status == null) &&
         qw = new QueryWrapper(query);
         quotes = findByQuery(qw, ctx);
      } else {
         logger.severe("UserContext parameter was null.");
      } // END if (ctx != null) && (statusReason == null) ...
      
      if (quotes == null) {
         quotes = new ArrayList<PersistentQuote>();
      } // END if (quotes == null)
      logger.exiting(_CLASS, "findByStatus(String,String,Date,UserContext)",
            quotes);
      return quotes;
   }

   @Override
   protected String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentQuoteImpl._KIND);
      return PersistentQuoteImpl._KIND;
   } 
   
}