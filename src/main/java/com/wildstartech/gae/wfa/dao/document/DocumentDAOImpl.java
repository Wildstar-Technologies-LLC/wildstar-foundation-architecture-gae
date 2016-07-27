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
package com.wildstartech.gae.wfa.dao.document;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.wildstartech.gae.wfa.dao.QueryWrapper;
import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.document.DocumentDAO;
import com.wildstartech.wfa.dao.document.PersistentDocument;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.document.Document;

public class DocumentDAOImpl 
extends WildDAOImpl<Document, PersistentDocument> 
implements DocumentDAO {
   private static final String _CLASS=DocumentDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);   
   
   /**
    * Default, no-argument constructor.
    */
   public DocumentDAOImpl() {
      super();
      logger.entering(_CLASS, "DocumentDAOImpl()");
      logger.exiting(_CLASS, "DocumentDAOImpl()");
   }
   
   @Override
   public PersistentDocument findInstance(Document document, UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findInstance(Document,UserContext)",
            new Object[] {document, ctx});
      logger.exiting(_CLASS, "findInstance(Document,UserContext)");
      return null;
   }

   @Override
   public PersistentDocument create() {
      logger.entering(_CLASS, "create()");
      PersistentDocument pDoc=new PersistentDocumentImpl();
      logger.exiting(_CLASS, "create()",pDoc);
      return pDoc;
   }

   @Override
   public PersistentDocument create(Document document, UserContext ctx) {
      logger.entering(_CLASS, "create(Document,UserContext)",
            new Object[] {document,ctx});
      PersistentDocument pDoc=null;
      pDoc=new PersistentDocumentImpl();
      logger.exiting(_CLASS, "create(Document,UserContext)",pDoc);
      return pDoc;
   }
   
   @Override
   public PersistentDocument deleteByIdentifier(
         String identifier, UserContext ctx) {
      logger.entering(_CLASS, "deleteByIdentifier(String,UserContext)",
            new Object[] {identifier,ctx});
      boolean contentDeleted=false;
      int nullPropertyCount=0;
      PersistentDocument document=null;
      StringBuilder msg=null;
      
      if ( 
            (identifier != null) &&
            (identifier.length() > 0) &&
            (ctx != null) 
         ) {
         document=findByIdentifier(identifier,ctx);
         if (document != null) {
            if (document instanceof PersistentDocumentImpl) {
               contentDeleted=
                     ((PersistentDocumentImpl) document).deleteContent();
               if (contentDeleted) {
                  logger.info("The GCS content for the document was removed.");
               } else {
                  logger.warning(
                        "The GCS content for the document was NOT removed.");
               } // END if (contentDeleted)
            } // END if (document instanceof PersistentDocumentImpl)
            super.deleteByIdentifier(identifier, ctx);
         } else {
            logger.info("Document with the specified identifer was not found.");
         } // END if (document != null)
      } else {
         msg=new StringBuilder(255);
         if (identifier == null) {
            nullPropertyCount++;
            msg.append("identifier property was null");
         } else if (identifier.length() == 0) {
            msg.append("identifier parameter was a zero-length string");
         } // END if (identifier == null)
         if (ctx == null) {
            nullPropertyCount++;
            if (nullPropertyCount > 1) {
               msg.append(", ");
            } // END if (nullPropertyCount > 1)
            msg.append("ctx parameter was null");
         } // END if (ctx == null)
         if (nullPropertyCount == 2) {
            msg.insert(0, "Method failed due to the following problems: ");
         } else {
            msg.insert(0, "Method failed beacuase the ");            
         } // END if (nullPropertyCount == 2)
         msg.append(".");
      } // END if ((identifier != null) && (identifier.length() > 0) && ...
      return document;
   }

   @Override
   public List<PersistentDocument> findByName(String name,
         UserContext ctx) throws DAOException {
      logger.entering(_CLASS, "findByName(String,UserContext)", 
            new Object[] {name,ctx});
      List<PersistentDocument> documents=null;
      Query query=null;
      QueryWrapper qw=null;
      Query.Filter filter=null;
      
      if (
            (name != null)  && 
            (name.length() > 0) && 
            (ctx != null)
         ) {
         query=new Query(PersistentDocumentImpl._KIND);
         filter=new FilterPredicate("name",FilterOperator.EQUAL,name);
         query.setFilter(filter);
         qw=new QueryWrapper(query);
         documents=findByQuery(qw,ctx);
      } else {
         if (name == null) {
            logger.severe("The name parameter was null.");
         } else if (name.length() == 0) {
            logger.severe("The name parameter was a zero-length string.");
         } // END if (name == null)
         if (ctx == null) {
            logger.severe("The UserContext paramter was null.");
         } // END if (ctx == null)
      } // END if (name != null)
      
      logger.exiting(_CLASS, "findByName(String,UserContext)",documents);
      return documents;
   }
   
   @Override
   public List<PersistentDocument> findByRelatedIdentifier(
         String type,
         String identifier,
         UserContext ctx) {
      logger.entering(_CLASS, 
            "findByRelatedIdentifier(String,String,UserContext)",
            new Object[] {identifier,type,ctx});
      List<Filter> filters=null;
      List<PersistentDocument> documents=null;
      Query query=null;
      QueryWrapper qw=null;
      Query.Filter filter=null;
      
      if (ctx != null) {
         if (!isEmpty(identifier)) {
            query=new Query(getKind());
            filters=new ArrayList<Filter>();
            if (!isEmpty(type)) {
               filters.add(
                     new FilterPredicate(
                        "relatedType",
                        FilterOperator.EQUAL,
                        type)
                     );
               filters.add(
                     new FilterPredicate(
                        "relatedIdentifier",
                        FilterOperator.EQUAL,
                        identifier)
                     );
               filter=new Query.CompositeFilter(
                     Query.CompositeFilterOperator.AND, filters);
            } else {
               filter=new FilterPredicate(
                     "relatedIdentifier",
                     FilterOperator.EQUAL,
                     identifier);
            } // END if (!isEmpty(kind))
            query.setFilter(filter);
            qw=new QueryWrapper(query);
            documents=findByQuery(qw,ctx);
         } else {
            logger.warning("The identifier parameter is missing.");
         } // END if (!isEmpty(identifier))         
      } else {
         logger.warning("The UserContext parameter was not specified.");         
      } // END if ((identifier != null) && (ctx != null))
      
      logger.exiting(_CLASS, "findByRelatedIdentifier(String,UserContext)",
            documents);
      return documents;
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
      logger.exiting(_CLASS, "getKind()", PersistentDocumentImpl._KIND);
      return PersistentDocumentImpl._KIND;
   }   
}