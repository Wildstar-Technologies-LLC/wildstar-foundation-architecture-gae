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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.journal.PersistentJournalEntry;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.journal.JournalCategoryTooLongException;
import com.wildstartech.wfa.journal.JournalDescriptionTooLongException;
import com.wildstartech.wfa.journal.JournalEntry;

public class PersistentJournalEntryImpl extends WildObjectImpl<JournalEntry>
      implements PersistentJournalEntry {
   /** Used in object serialization. */
   private static final long serialVersionUID = 3387467130163669223L;
   private static final String _CLASS = PersistentJournalEntryImpl.class
         .getName();
   private static final Logger logger = Logger.getLogger(_CLASS);
   // The maximum length of a string in the data store is 500 characters.
   private static int MAX_LENGTH = 500;

   protected static final String _KIND = "com.wildstartech.wfa.journal.JournalEntry";

   private Date entryDate = null;
   private MimeType mimeType = null;
   private String category = "";
   private String description = "";
   private String relatedIdentifier = "";
   private String relatedKind = "";
   private StringBuilder content = null;

   /**
    * Default, no-argument constructor.
    */
   public PersistentJournalEntryImpl() {
      logger.entering(_CLASS, "PersistentJournalEntryImpl()");
      this.entryDate = new Date();
      this.category = "";
      this.description = "";
      this.content = new StringBuilder();
      try {
         this.mimeType = new MimeType("text", "plain");
      } catch (MimeTypeParseException ex) {
         logger.log(Level.SEVERE,
               "Unable to create text/plain MimeType instance.", ex);
      } // END try/catch
      logger.exiting(_CLASS, "PersistentJournalEntryImpl()");
   }

   // ********** utility methods
   /**
    * Convenience method to indicate whether or not the {@code JournalEntry}
    * contains and information.
    * <p>
    * The {@code JournalEntry} is considered to be empty if neither the
    * {@code description} nor {@code content} properties contain any data.
    * </p>
    *
    * @return {@code true} if the {@code JournalEntry} does not contain any
    *         information and {@code false} if it does not contain any
    *         information.
    */
   public boolean isEmpty() {
      logger.entering(_CLASS, "isEmpty()");
      boolean result = false;
      if (
            (isEmpty(description)) && 
            (
                  (content == null) ||
                  (content.length() == 0)
            )
         ) {
         result = true;
      } // END if (isEmpty(description)) && (isEmpty(content)) {
      logger.exiting(_CLASS, "isEmpty()", result);
      return result;
   }

   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(Entity)", entity);
      MimeType mimeType = null;
      Text text = null;
      if (entity != null) {
         super.populateEntity(entity);
         // ***** category
         entity.setProperty("category", getCategory());
         // ***** content
         text = new Text(content.toString());
         entity.setProperty("content", text);
         // ***** description
         entity.setProperty("description", getDescription());
         // ***** entryDate
         entity.setProperty("entryDate", getEntryDate());
         // ***** mimeType
         mimeType = getMimeType();
         entity.setProperty("mimeType_primaryType", mimeType.getPrimaryType());
         entity.setProperty("mimeType_subType", mimeType.getSubType());
         // ***** relatedIdentifier
         entity.setProperty("relatedIdentifier", getRelatedIdentifier());
         // ***** relatedKind
         entity.setProperty("relatedKind", getRelatedKind());
      } else {
         logger.severe("The entity parameter is null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateEntity(Entity)");
   }

   protected void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity)", entity);
      Date tmpDate = null;
      MimeType mimeType = null;
      String tmpStr1 = null;
      String tmpStr2 = null;
      if (entity != null) {
         super.populateFromEntity(entity, ctx);
         // ***** category
         tmpStr1 = getPropertyAsString(entity, "category");
         setCategory(tmpStr1);
         // ***** content
         tmpStr1 = getPropertyAsString(entity, "content");
         this.content = new StringBuilder(tmpStr1);
         // ***** description
         tmpStr1 = getPropertyAsString(entity, "description");
         setDescription(tmpStr1);
         // ***** entryDate
         tmpDate = getPropertyAsDate(entity, "entryDate");
         setEntryDate(tmpDate);
         // ***** mimeType
         tmpStr1 = getPropertyAsString(entity, "mimeType_primaryType");
         tmpStr2 = getPropertyAsString(entity, "mimeType_subType");
         try {
            mimeType = new MimeType(tmpStr1, tmpStr2);
            setMimeType(mimeType);
         } catch (MimeTypeParseException ex) {
            logger.log(Level.WARNING,
                  "Unable to derive the MimeType from the datastore.", ex);
         } // END try/catch
         // ***** relatedIdentifier
         setRelatedIdentifier(getPropertyAsString(entity, "relatedIdentifier"));
         // ***** relatedKind
         setRelatedKind(getPropertyAsString(entity, "relatedKind"));
      } else {
         logger.severe("The entity parameter is null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateFromEntity(Entity)");
   }

   @Override
   public void populateFromObject(JournalEntry journalEntry) {
      logger.entering(_CLASS, "populateFromObject(JournalEntry)", journalEntry);

      if (journalEntry != null) {
         // ***** category
         try {
            setCategory(journalEntry.getCategory());
         } catch (JournalCategoryTooLongException ex) {
            logger.log(Level.WARNING, "The Category was too long.", ex);
         } // END try/catch
           // ***** content
         setContent(journalEntry.getContent());
         // ***** description
         try {
            setDescription(journalEntry.getDescription());
         } catch (JournalDescriptionTooLongException ex) {
            logger.log(Level.WARNING, "The Description property was too long.",
                  ex);
         } // END try/catch
           // ***** entryDate
         setEntryDate(journalEntry.getEntryDate());
         // ***** mimeType
         setMimeType(journalEntry.getMimeType());
         // ***** relatedIdentifier
         if (journalEntry instanceof PersistentJournalEntry) {
            setRelatedIdentifier(((PersistentJournalEntry) journalEntry)
                  .getRelatedIdentifier());
         } // END if (journalEntry instanceof PersistentJournalEntry) {
           // ***** relatedKind
         if (journalEntry instanceof PersistentJournalEntryImpl) {
            setRelatedKind(
                  ((PersistentJournalEntryImpl) journalEntry).getRelatedKind());
         } // END if (journalEntry instanceof PersistentJournalEntryImpl)
      } else {
         logger.warning("The journalEntry parameter was null.");
      } // END if (journalEntry != null)
      logger.exiting(_CLASS, "populateFromObject(JournalEntry)");
   }

   // ********** accessor methods
   // ***** category
   @Override
   public String getCategory() {
      logger.entering(_CLASS, "getCategory()");
      logger.exiting(_CLASS, "getCategory()", this.category);
      return this.category;
   }

   @Override
   public void setCategory(String category) {
      logger.entering(_CLASS, "setCategory(String)", category);
      String message = null;
      StringBuilder msg = null;
      JournalCategoryTooLongException ex = null;

      if (category != null) {
         if (category.length() <= MAX_LENGTH) {
            this.category = defaultValue(category, "");
         } else {
            msg = new StringBuilder(80);
            msg.append(
                  "The specified value for the category field exceeds the ");
            msg.append("maximum allowable length of ");
            msg.append(MAX_LENGTH);
            msg.append(" characters.");
            message = (msg.toString());
            ex = new JournalCategoryTooLongException(message, MAX_LENGTH);
            throw ex;
         } // END if (category.length() <= MAX_LENGTH)
      } else {
         logger.warning("A null value was passed as the category parameter.");
      } // END if (category != null)
      logger.exiting(_CLASS, "setCategory(String)");
   }

   // ***** content
   @Override
   public String getContent() {
      logger.entering(_CLASS, "getContent()");
      String tmpContent = null;

      tmpContent = this.content.toString();

      logger.exiting(_CLASS, "getContent()", tmpContent);
      return tmpContent;
   }

   @Override
   public InputStream getContentAsInputStream() {
      logger.entering(_CLASS, "getContentAsInputStream()");
      InputStream in = null;
      if (this.content == null) {
         this.content = new StringBuilder();
      } // END if (this.content == null)
      in = new ByteArrayInputStream(this.content.toString().getBytes());
      logger.exiting(_CLASS, "getContentAsInputStream()", in);
      return in;
   }

   @Override
   public void setContent(String content) {
      logger.entering(_CLASS, "setContent(String)", content);

      this.content = new StringBuilder(defaultValue(content));

      logger.exiting(_CLASS, "setContent(String)");
   }

   @Override
   public OutputStream getContentAsOutputStream() {
      throw new UnsupportedOperationException();
   }

   // ***** description
   @Override
   public String getDescription() {
      logger.entering(_CLASS, "getDescription()");
      logger.exiting(_CLASS, "getDescription()", this.description);
      return this.description;
   }

   @Override
   public void setDescription(String description) {
      String message = null;
      StringBuilder msg = null;
      JournalDescriptionTooLongException ex = null;

      if (description != null) {
         if (description.length() <= MAX_LENGTH) {
            this.description = defaultValue(category, "");
         } else {
            msg = new StringBuilder(80);
            msg.append(
                  "The specified value for the description field exceeds ");
            msg.append("the maximum allowable length of ");
            msg.append(MAX_LENGTH);
            msg.append(" characters.");
            message = (msg.toString());
            ex = new JournalDescriptionTooLongException(message, MAX_LENGTH);
            throw ex;
         } // END if (description.length() <= MAX_LENGTH)
      } else {
         logger.warning("A null value was passed as the category parameter.");
      } // END if (category != null)
   }

   // ***** entryDate
   @Override
   public Date getEntryDate() {
      logger.entering(_CLASS, "getEntryDate()");
      logger.exiting(_CLASS, "getEntryDate()", this.entryDate);
      return this.entryDate;
   }

   @Override
   public void setEntryDate(Date entryDate) {
      logger.entering(_CLASS, "setEntryDate(Date)", entryDate);
      if (entryDate == null) {
         entryDate = new Date();
      } // END if (entryDate == null)
      this.entryDate = entryDate;
      logger.exiting(_CLASS, "setEntryDate(Date)");
   }

   // ***** kind
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()", PersistentJournalEntryImpl._KIND);
      return PersistentJournalEntryImpl._KIND;
   }

   // ***** mimeType
   @Override
   public MimeType getMimeType() {
      logger.entering(_CLASS, "getMimeType()");
      logger.exiting(_CLASS, "getMimeType()", this.mimeType);
      return this.mimeType;
   }

   @Override
   public void setMimeType(MimeType mimeType) {
      logger.entering(_CLASS, "setMimeType(MimeType)", mimeType);
      if (mimeType == null) {
         try {
            this.mimeType = new MimeType("text", "plain");
         } catch (MimeTypeParseException ex) {
            logger.log(Level.WARNING, "MimeTypeParseException thrown.", ex);
         } // END try/catch
      } else {
         this.mimeType = mimeType;
      }
      logger.exiting(_CLASS, "setMimeType(MimeType)");
   }

   // ***** relatedIdentifier
   public String getRelatedIdentifier() {
      logger.entering(_CLASS, "getRelatedIdentifier()");
      logger.exiting(_CLASS, "getRelatedIdentifier()", this.relatedIdentifier);
      return this.relatedIdentifier;
   }

   public void setRelatedIdentifier(String relatedIdentifier) {
      logger.entering(_CLASS, "setRelatedIdentifier(String)",
            relatedIdentifier);
      this.relatedIdentifier = defaultValue(relatedIdentifier);
      logger.exiting(_CLASS, "setRelatedIdentifier(String)");
   }

   // ***** relatedKind
   public String getRelatedKind() {
      logger.entering(_CLASS, "getRelatedKind()");
      logger.exiting(_CLASS, "getRelatedKind()", this.relatedKind);
      return this.relatedKind;
   }

   public void setRelatedKind(String relatedKind) {
      logger.entering(_CLASS, "setRelatedKind(String)", relatedKind);
      this.relatedKind = defaultValue(relatedKind);
      logger.exiting(_CLASS, "setRelatedKing()");
   }

}
