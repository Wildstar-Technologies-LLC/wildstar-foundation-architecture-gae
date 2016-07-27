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
package com.wildstartech.gae.wfa.dao.journal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.Property;
import com.wildstartech.wfa.dao.SortCriterion;
import com.wildstartech.wfa.dao.SortCriterion.ORDER;
import com.wildstartech.wfa.dao.journal.JournalDAO;
import com.wildstartech.wfa.dao.journal.PersistentJournalEntry;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.journal.JournalEntry;

public class JournalDAOImpl 
extends WildDAOImpl<JournalEntry, PersistentJournalEntry> 
implements JournalDAO {
  private final String _CLASS=JournalDAOImpl.class.getName();
  private final Logger logger=Logger.getLogger(_CLASS);
  
  @Override
  public PersistentJournalEntryImpl create() {
    logger.entering(_CLASS,"create()");
    PersistentJournalEntryImpl journalEntry=null;
    
    journalEntry=new PersistentJournalEntryImpl();
    
    logger.exiting(_CLASS,"create()");
    return journalEntry;
  }

  @Override
  public PersistentJournalEntry findInstance(JournalEntry journalEntry,
      UserContext ctx) throws DAOException {
    logger.entering(_CLASS,"findInstance(JournalEntry,UserContext)",
        new Object[] {journalEntry,ctx});
    logger.exiting(_CLASS,"findInstance(JournalEntry,UserContext)");
    return null;
  }

  @Override
  public PersistentJournalEntryImpl create(
      JournalEntry journalEntry, UserContext ctx) {
    logger.entering(_CLASS,"create(JournalEntry,UserContext)",
        new Object[] {journalEntry, ctx});
    PersistentJournalEntryImpl pJournalEntry=null;
    
    logger.exiting(_CLASS,"create(JournalEntry,UserContext)",pJournalEntry);
    return pJournalEntry;
  }

  /**
   * 
   */
  @Override
  public List<PersistentJournalEntry> findEntries(String type, String identifier,
      UserContext ctx) {
    logger.entering(_CLASS,"findEntries(String,String,UserContext)",
          new Object[] {type,identifier,ctx});
    List<PersistentJournalEntry> journals=null;
    List<Filter> filters = null;
    Query query = null;
    Query.Filter filter = null;
    QueryWrapper qw = null;
    SortCriterion sortCriteria=null;
    
    if (
          (!isEmpty(type)) &&
          (!isEmpty(identifier)) &&
          (ctx != null)
       ) {
       sortCriteria = new SortCriterion();
       sortCriteria.setProperty(new Property("dateCreated",Date.class));
       sortCriteria.setSortOrder(ORDER.ASCENDING);
       query = new Query(PersistentJournalEntryImpl._KIND);
       filters=new ArrayList<Filter>();
       filters.add(
          new FilterPredicate(
             "relatedKind",
             FilterOperator.EQUAL,
             type));
       filters.add(
          new FilterPredicate("relatedIdentifier",
             Query.FilterOperator.EQUAL, 
             identifier));
       filter = new Query.CompositeFilter(
             Query.CompositeFilterOperator.AND, filters);
       query.setFilter(filter);
       qw = new QueryWrapper(query);
       journals=findByQuery(qw, ctx);
    } else  {
       if (isEmpty(type)) {
          logger.warning("The type parameter is null.");          
       } // END if (isEmpty(type))
       if (isEmpty(identifier)) {
          logger.warning("The identifier parameter is null.");          
       } // END if (isEmpty(type))
       if (ctx == null) {
          logger.warning("The UserContext parameter is null.");
       } // END if (ctx == null)
    } // END if ((!isEmpty(type)) && (!isEmpty(identifier)) && (ctx != null))
    if (journals == null) {
       journals=new ArrayList<PersistentJournalEntry>();
    } // END if (journals == null)
    logger.exiting(_CLASS,"findEntries(String,String,UserContext)",journals);
    return journals;
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
     logger.exiting(_CLASS, "getKind()",PersistentJournalEntryImpl._KIND);
     return PersistentJournalEntryImpl._KIND;
  }
  
  public PersistentJournalEntry save(
        JournalEntry journalEntry, 
        UserContext ctx, 
        Transaction txn) {
     logger.entering(_CLASS, "save(JournalEntry,UserContext,Transaction)",
           new Object[] {journalEntry, ctx, txn});
     Date tmpDate=null;
     PersistentJournalEntry pJournalEntry=null;
     
     if (journalEntry != null) {
        /* If no date is specified, the current date/time will be used for the
         * journal entry. */
        tmpDate=journalEntry.getEntryDate();
        if (tmpDate == null) {
           journalEntry.setEntryDate(new Date());
        } // END if (tmpDate == null)
        pJournalEntry=super.save(journalEntry, ctx, txn);
     } // END if (journalEntry != null)
     
     logger.exiting(_CLASS, "save(JournalEntry,UserContext,Transaction)",
           pJournalEntry);
     return pJournalEntry;
     
  }
}