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
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.document.DocumentDAO;
import com.wildstartech.wfa.dao.document.PersistentDocument;
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