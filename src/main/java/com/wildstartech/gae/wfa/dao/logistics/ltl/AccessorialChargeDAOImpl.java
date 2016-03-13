/*
 * Copyright (c) 2013 - 2015 Wildstar Technologies, LLC.
 *
 * This file is part of Wildstar Foundation Architecture for Google App Engine.
 *
 * Wildstar Foundation Architecture for Google App Engine is free software: you
 * can redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either version
 * 3 of the License, or (at your option) any later version.
 *
 * Wildstar Foundation Architecture for Google App Engine is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Wildstar Foundation Architecture for Google App Engine.  If not, see 
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
package com.wildstartech.gae.wfa.dao.logistics.ltl;

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
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.logistics.ltl.AccessorialChargeDAO;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentAccessorialCharge;
import com.wildstartech.wfa.logistics.ltl.AccessorialCharge;

public class AccessorialChargeDAOImpl 
extends WildDAOImpl<AccessorialCharge, PersistentAccessorialCharge> 
implements AccessorialChargeDAO {
  private static final String _CLASS=AccessorialChargeDAOImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  
  @Override
  public PersistentAccessorialCharge create() {
    logger.entering(_CLASS,"create()");
    PersistentAccessorialCharge charge=null;
    charge=new PersistentAccessorialChargeImpl();
    logger.exiting(_CLASS,"create()",charge);
    return charge;
  }
  
  @Override
  public PersistentAccessorialCharge create(AccessorialCharge charge, 
      UserContext ctx) {
    logger.entering(_CLASS,"create(AccessorialCharge, UserContext",
        new Object[] {charge,ctx});
    PersistentAccessorialCharge pCharge=null;
    logger.exiting(_CLASS,"create(AccessorialCharge, UserContext",pCharge);
    return pCharge;
  }

  @Override
  public PersistentAccessorialCharge findInstance(AccessorialCharge charge,
      UserContext ctx) throws DAOException {
    logger.entering(_CLASS,"findInstance(AccessorialCharge,UserContext)",
        new Object[] {charge,ctx});
    Filter filter=null;
    Filter amountFilter=null;
    Filter descriptionFilter=null;
    Filter quantityFilter=null;
    Filter quoteIdentifierFilter=null;
    List<PersistentAccessorialCharge> charges=null;
    PersistentAccessorialCharge pCharge=null;
    String identifier=null;
    Query query=null;
    QueryWrapper qw=null;
    
    if ((charge != null) && (ctx != null)) {
      if (charge instanceof PersistentAccessorialCharge) {
        identifier=((PersistentAccessorialCharge)charge).getIdentifier();
        if ((identifier != null) && (identifier.length() > 0)) {
          pCharge=(PersistentAccessorialCharge) 
              findByIdentifier(identifier,ctx);
        } else {
          if (identifier==null) { 
            logger.fine("Identifier is null."); 
          } else {
            logger.fine("Identifier is a zero-length string.");
          } // END if (identifier==null)
        } // END if ((identifier != null) && (identifier.length() > 0))        
      }  // END if (charge instanceof PersistentAccessorialCharge)
      if (pCharge == null) {
        query=new Query(PersistentAccessorialChargeImpl._KIND);
        amountFilter=new FilterPredicate("amount",
            FilterOperator.EQUAL,
            charge.getAmount().toPlainString());
        descriptionFilter=new FilterPredicate("description",
            FilterOperator.EQUAL,
            charge.getDescription());
        quantityFilter=new FilterPredicate("quantity",
            FilterOperator.EQUAL,
            charge.getQuantity());
        if (charge instanceof PersistentAccessorialCharge) {
          quoteIdentifierFilter=new FilterPredicate("quoteIdentifier",
              FilterOperator.EQUAL,
              ((PersistentAccessorialCharge) charge).getQuoteIdentifier());
          filter=CompositeFilterOperator.and(quoteIdentifierFilter,
              amountFilter,
              descriptionFilter,
              quantityFilter);
        } else {
          filter=CompositeFilterOperator.and(amountFilter,
              descriptionFilter,
              quantityFilter);
        } // END if (charge instanceof PersistentAccessorialCharge)
        query.setFilter(filter);
        qw=new QueryWrapper(query);
        charges=findByQuery(qw,ctx);
        if ((charges != null) && (charges.size() >= 1)) {
          pCharge=(PersistentAccessorialCharge) charges.get(0);
        } else {
          // NO match was found.
        } // END if ((charges != null) && (charges.size() >= 1))
      } // END if (pCharge == null)
    } else {
      if (charge == null) logger.severe("AccessorialCharge parameter is null.");
      if (ctx == null) logger.severe("UserContext parameter is null.");
    } // END if ((charge != null) && (ctx != null))
    logger.exiting(_CLASS,"findInstance(AccessorialCharge,UserContext)",pCharge);
    return pCharge;
  }

  @Override
  public List<PersistentAccessorialCharge> 
    findByQuoteIdentifier(String quoteId, UserContext ctx) {
    logger.entering(_CLASS, "findByQuoteIdentifier(String,UserContext)",
        new Object[] {quoteId,ctx});
    List<PersistentAccessorialCharge> charges=null;
    Query query=null;
    QueryWrapper qw=null;
    Filter filter=null;
    String kind=null;
    
    if (((quoteId != null) && (quoteId.length() !=0)) && (ctx != null)) {
      kind=PersistentAccessorialChargeImpl._KIND;
      query=new Query(kind);
      filter=new Query.FilterPredicate(
          "quoteIdentifier",
          FilterOperator.EQUAL,
          quoteId);
      query.setFilter(filter);
      qw=new QueryWrapper(query);
      charges=findByQuery(qw,ctx);
    } else {
      // Either the quoteId or ctx were null, so return an empty list.
      charges=new ArrayList<PersistentAccessorialCharge>();
      if (quoteId == null) logger.warning("The quoteId is null.");
      if ((quoteId != null) && (quoteId.length() == 0))
        logger.warning("The quoteId is a zero-length string.");
      if (ctx == null) logger.warning("The UserContext is null.");
    } // END if ((quoteId != null) && (ctx != null))
    logger.exiting(_CLASS, "findByQuoteIdentifier(String,UserContext)",charges);
    return charges;
  }
  /**
   * Returns the <em>Kind</em> property of the entity which is used for the
   * purpose of querying the Datastore.
   * 
   * @return A string value which is used by the Datastore for the purpose of
   *         categorizing entities of this object's type to provide the ability
   *         to querying the Datastore and retrieve entities.
   */
  @Override
  protected final String getKind() {
     logger.entering(_CLASS, "getKind()");
     logger.exiting(_CLASS, "getKind()", PersistentAccessorialChargeImpl._KIND);
     return PersistentAccessorialChargeImpl._KIND;
  } 
}