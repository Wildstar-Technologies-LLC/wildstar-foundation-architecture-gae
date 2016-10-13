package com.wildstartech.gae.wfa.dao.logistics.ltl;

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
import com.wildstartech.wfa.dao.logistics.ltl.PersistentQuickQuote;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.QuickQuote;

public abstract class QuoteDAOBase<T extends QuickQuote, W extends PersistentQuickQuote> extends WildDAOImpl<T, W> {
   private static final String _CLASS=QuoteDAOBase.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   private static final QuoteShardedCounter requestIdGenerator = new QuoteShardedCounter();
   
   /**
    * Returns the <em>Kind</em> property of the entity which is used for the
    * purpose of querying the Datastore.
    * 
    * @return A string value which is used by the Datastore for the purpose of
    *         categorizing entities of this object's type to provide the ability
    *         to querying the Datastore and retrieve entities.
    */
   protected final String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()", PersistentQuoteImpl._KIND);
      return PersistentQuoteImpl._KIND;
   }
   
   // ********** Save method 
   /**
    * Save method responsible for persisting the Quote entity.
    * 
    * <p>
    * Overriding the WildDAOImpl superclass method because we want to intercept
    * that method as there are children that need to be saved.
    * </p>
    */
   @SuppressWarnings("unchecked")
   @Override
   public W save(T quote, UserContext ctx,
         Transaction txn) {
      logger.entering(_CLASS, "save(T,UserContext,Transaction)",
            new Object[] { quote, ctx, txn });
      JournalDAOImpl journalDAO=null;
      PersistentJournalEntryImpl pJournal=null;
      String nextIdValue = "";
      String requestId = "";
      String requestIdTemplate = "Q00000000000000";
      StringBuilder sb = null;
      W pQuote=null;

      if ((quote != null) && (ctx != null)) {
         // Get the quote identifier
         requestId = quote.getRequestId();
         // Get a reference to the current, saved version of the quote.
         if (!isEmpty(requestId)) {
            // Yes there is a request Id, let's use it to get the current
            // version of the object.
            pQuote=findByRequestId(requestId,ctx);            
         } else {
            // No, there is no requestId, so let's generate one....
            sb = new StringBuilder(30);
            nextIdValue = String.valueOf(QuoteDAOBase.requestIdGenerator.nextId());
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
         pQuote.updateFromObject((T)quote);
         pQuote.setRequestId(requestId);
          
         /* Get the journal entry. */
         pJournal=(PersistentJournalEntryImpl) pQuote.getNewJournalEntry();
         
         /* Saving the quote will return ONLY the parent object.  The children
          * will not be present in the object that is returned. */
         pQuote = (W) super.save((T) pQuote, ctx, txn);
         
         // ******************** Journal Entry ********************
         if (!pJournal.isEmpty()) {
            // The journal contains data, so let's save it.
            journalDAO=new JournalDAOImpl();
            pJournal.setRelatedKind(getKind());
            pJournal.setRelatedIdentifier(pQuote.getIdentifier());
            pJournal=(PersistentJournalEntryImpl)
                  journalDAO.save(pJournal, ctx);
         } // END if (!pJournalEntry.isEmpty())
                  
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
   
   // ********** Search Methods
   /**
    * Returns a list of quotes that require action.
    * <p>
    * Returns a list of all quotes that are in a "New" status state or a
    * "Pending" status state with a value of "Approval (Justo)".
    * </p>
    
    */
   public final List<W> findActionable(UserContext ctx) {
      logger.entering(_CLASS, "findActionable(UserContext)", ctx);
      List<Filter> filters = null;
      List<Filter> tmpFilters = null;
      List<W> quotes = null;
      Query query = null;
      QueryWrapper qw = null;
      Filter filter = null;
      Filter userFilter=null;
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
   public final List<W> findAll(UserContext ctx) {
      logger.entering(_CLASS, "findAll(UserContext)", ctx);
      Filter userFilter=null;
      List<Filter> tmpFilters=null;
      List<W> results = null;
      Query query=null;
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
            tmpFilters = new ArrayList<Filter>();
            tmpFilters.add(new FilterPredicate("createdBy",
                  FilterOperator.EQUAL, currentUser));
            tmpFilters.add(new FilterPredicate("contactEmail",
                  FilterOperator.EQUAL, currentUser));
            userFilter = new Query.CompositeFilter(
                  Query.CompositeFilterOperator.OR, tmpFilters);
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
   public final List<W> findAllActive(UserContext ctx) {
      logger.entering(_CLASS, "findAllActive(UserContext)", ctx);
      Filter filter = null;
      Filter userFilter=null;
      List<Filter> filters = null;
      List<W> quotes = null;
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
      logger.exiting(_CLASS, "findAllActive(UserContext)");
      return quotes;
   }

   /**
    * Finds the quote represented by the specific request id.
    * <p>
    * Locates a specific quote whose value for the <code>Request Id</code> field
    * matches the value passed as the <code>requestId</code> parameter.
    * </p>
    */
   public final W findByRequestId(String requestId, UserContext ctx) {
      logger.entering(_CLASS, "findByRequestId(String)", requestId);
      W quote = null;
      List<W> quotes = null;
      List<Filter> tmpFilters = null;
      Query query = null;
      Filter filter = null;
      Filter userFilter=null;
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
            tmpFilters = new ArrayList<Filter>();
            tmpFilters.add(new FilterPredicate("createdBy",
                  FilterOperator.EQUAL, currentUser));
            tmpFilters.add(new FilterPredicate("contactEmail",
                  FilterOperator.EQUAL, currentUser));
            userFilter = new Query.CompositeFilter(
                  Query.CompositeFilterOperator.OR, tmpFilters);
            tmpFilters.clear();
            tmpFilters.add(filter);
            tmpFilters.add(userFilter);
            filter=new Query.CompositeFilter(
                  Query.CompositeFilterOperator.AND,
                  tmpFilters);
         } // END if ((currentUser != null) && ...
         /* ***** END: User Filtering */

         query.setFilter(filter);
         qw = new QueryWrapper(query);
         quotes = findByQuery(qw, ctx);
         if ((quotes != null) && (quotes.size() != 0)) {
            quote = (W) quotes.get(0);
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
   public final List<W> findByStatus(String status, String statusReason,
         Date minDateModified, UserContext ctx) {
      logger.entering(_CLASS, "findByStatus(String,String,Date,UserContext)",
            new Object[] { status, statusReason, minDateModified, ctx });
      Calendar calendar = null;
      Filter userFilter=null;
      List<Filter> filters = null;
      List<W> quotes = null;
      Query query = null;
      QueryWrapper qw = null;
      Query.Filter filter = null;
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
         quotes = new ArrayList<W>();
      } // END if (quotes == null)
      logger.exiting(_CLASS, "findByStatus(String,String,Date,UserContext)",
            quotes);
      return quotes;
   }   
}