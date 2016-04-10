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
package com.wildstartech.gae.wfa.dao.ticketing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.ticketing.PersistentBasicTicket;
import com.wildstartech.wfa.ticketing.BasicTicket;

public class PersistentBasicTicketImpl<T extends BasicTicket> 
extends WildObjectImpl<T>
implements PersistentBasicTicket<T> {
   /** Used in object serialization. */
   private static final long serialVersionUID = 6899140692176513689L;
   /** Used in object serialization */
   private static final String _CLASS=PersistentBasicTicketImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   public static final String _KIND=
         "com.wildstartech.wfa.ticketing.BasicTicket";
   private static String statusStateIndexOutOfBoundsTemplate = 
         "The status state index must be between 0 and {0}";
   private static String statusReasonIndexOutOfBoundsTemplate = 
         "The status state index must be between 0 and {0}";
   
   private List<String> statusStates=null;
   private Map<String,List<String>> statusReasons=null;
   private String requestId="";
   private String shortDescription="";
   private String statusReason="";
   private String statusState="";
   private String title="";
   
   /**
    * Default, no-argument constructor.
    */
   public PersistentBasicTicketImpl() {
      super();
      logger.entering(_CLASS, "PersistentBasicTicketImpl()");
      this.statusStates=new ArrayList<String>();
      this.statusReasons=new TreeMap<String,List<String>>();
      logger.exiting(_CLASS, "PersistentBasicTicketImpl()");
   }
   
   //********** Utility methods
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentBasicTicketImpl._KIND);
      return PersistentBasicTicketImpl._KIND;
   }
   /**
    * 
    */
   @Override
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS,"populateEntity(Entity)",entity);
      if (entity != null) {
         super.populateEntity(entity);
         entity.setProperty("requestId", getRequestId());
         entity.setProperty("shortDescription", getShortDescription());
         entity.setProperty("statusReason", getStatusReason());
         entity.setProperty("statusState",getStatusState());
         entity.setProperty("title", getTitle());
      } else {
         logger.severe("The specified Entity object was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS,"populateEntity(Entity)");
   }
   @Override
   protected void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity)",entity);
      
      if (entity != null) {
         super.populateFromEntity(entity, ctx);
         setRequestId(getPropertyAsString(entity,"requestId"));
         setShortDescription(getPropertyAsString(entity,"shortDescription"));
         setStatusState(getPropertyAsString(entity,"statusState"));
         setStatusReason(getPropertyAsString(entity,"statusReason"));
         setTitle(getPropertyAsString(entity,"title"));
      } else {
         logger.severe("The specified Entity object was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateFromEntity(Entity)");
   }
   
   /**
    * Populate the current object using data from the specified ticket.
    * @param An <code>T</code> object whose data should be used to 
    * populate the current object. <code>T</code> must descend from the
    * <code>BasicTicket</code> class.
    */
   public void populateFromObject(T ticket) {
      logger.entering(_CLASS, "populateFromObject(T)",ticket);
      if (ticket != null) {
         super.populateFromObject(ticket);
         setRequestId(ticket.getRequestId());
         setShortDescription(ticket.getShortDescription());
         setStatusState(ticket.getStatusState());
         setStatusReason(ticket.getStatusReason());
         setTitle(ticket.getTitle());
      } else {
         logger.warning("The specified ticket was null.");
      } // END if (ticket != null)
      logger.exiting(_CLASS, "populateFromObject(T)");
   }
   
   /**
    * Returns a string representation of properties.
    */
   public String toPropertyString() {
      logger.entering(_CLASS, "toPropertyString()");
      String returnValue=null;
      StringBuilder sb=null;
      
      sb=new StringBuilder(1024);
      sb.append(super.toPropertyString());
      if (sb.length() > 0) sb.append(", ");
      sb.append("requestId=\"").append(getRequestId()).append("\", ");
      sb.append("shortDescription=\"").append(
            getShortDescription()).append("\", ");
      sb.append("statusReason=\"").append(getStatusReason()).append("\", ");
      sb.append("statusState=\"").append(getStatusState()).append("\", ");
      sb.append("title =\"").append(getTitle()).append("\", ");
      returnValue=sb.toString();
      logger.exiting(_CLASS, "toPropertyString()",returnValue);
      return returnValue;
   }
   /** 
    * Return a string representation of the class.
    */
   public String toString() {
      logger.entering(_CLASS, "toString()");
      String returnValue=null;
      StringBuilder sb=null;
      
      sb=new StringBuilder(1024);
      sb.append(_CLASS);
      sb.append(" [");
      sb.append(toPropertyString());
      sb.append("]");
      returnValue=sb.toString();
      
      logger.exiting(_CLASS, "toString()",returnValue);
      return returnValue;
   }
   //********** accessor methods
   //***** requestId
   public final String getRequestId() {
      logger.entering(_CLASS, "getRequestId()");
      logger.exiting(_CLASS, "getRequestId()",this.requestId);
      return this.requestId;
   }
   public final void setRequestId(String requestId) {
      logger.entering(_CLASS,"setRequestId(String)",requestId);
      this.requestId = defaultValue(requestId);
      logger.exiting(_CLASS, "setRequestId(String)");
   }
   //***** shortDescription
   public final String getShortDescription() {
      logger.entering(_CLASS, "getShortDescription()");
      logger.exiting(_CLASS, "getShortDescription()",this.shortDescription);
      return this.shortDescription;
   }
   public final void setShortDescription(String shortDescription) {
      logger.entering(_CLASS,"setShortDescription(String)",shortDescription);
      this.shortDescription = defaultValue(shortDescription);
      logger.exiting(_CLASS, "setShortDescription(String)");
   }
   
   //***** statusReason
   public final String getStatusReason() {
     logger.entering(_CLASS,"getStatusReason()");
     logger.exiting(_CLASS,"getStatusreason()",this.statusReason);
     return this.statusReason;
   }
   public final void setStatusReason(String statusReason) {
     logger.entering(_CLASS,"setStatusReason(String)",statusReason);
     this.statusReason=defaultValue(statusReason);
     logger.exiting(_CLASS,"setStatusReason(String)");
   }

   //***** statusReasons
   /**
    * Appends the specified status state to the end of the list.
    * @param statusState - the status state to be added to the list.
    * @return <code>true</code> if the status state was added to the end of the
    * list
    */
   public final boolean addStatusReason(String statusState, String statusReason) {
      logger.entering(_CLASS, "addStatusReason(String,String)",
            new Object[] {statusState, statusReason});
      boolean result=false;
      List<String> statusReasonList=null;
      
      if ((statusState != null) && (statusReason != null)) {
         if (this.statusReasons == null) {
            this.statusReasons=new TreeMap<String,List<String>>();            
         } // END if (this.statusReasons == null)
         
         statusReasonList=this.statusReasons.get(statusState);
         /* Let's make sure there are status reasons for the specified status
          * state. */
         if (statusReasonList == null) {
            statusReasonList=new ArrayList<String>();
         } // END if (statusReasonList == null)
         
         result=statusReasonList.add(statusReason);
         this.statusReasons.put(statusState,statusReasonList);
         
      } // END if ((statusState != null) && (statusReason != null))
      logger.exiting(_CLASS, "addStatusReason(String)",result);
      return result;
   }
   
   /**
    * Appends the specified status reason to the end of the list for the
    * specified statusState.  Any status reason at the specified position and 
    * any subsequent status reasons will be shifted ot the right.
    * @param statusState - the status state for which the specified status 
    * reason will be added.
    * @param position - the position in the status reason where the 
    * @param statusReason - the value to be added to at the position in the 
    * status reason list for the specified status state.
    * @return <code>true</code> if the status reason was added to the end of the
    * list
    */
   public final boolean addStatusReason(String statusState, 
         int position, 
         String statusReason) {
      logger.entering(_CLASS, "addStatusReason(String,int,String)",
            new Object[] {statusState, position, statusReason});
      boolean result=false;
      List<String> statusReasonList=null;
      String msg=null;
      
      if ((statusState != null) && (statusReason != null)) {
         if (this.statusReasons == null) {
            this.statusReasons=new TreeMap<String,List<String>>();            
         } // END if (this.statusReasons == null)
         statusReasonList=this.statusReasons.get(statusState);
         /* Let's make sure there are status reasons for the specified status
          * state. */
         if (statusReasonList == null) {
            statusReasonList=new ArrayList<String>();
         } // END if (statusReasonList == null)
         // Let's check the bounds for the desired insert operation.
         if ((position >= 0) && (position < statusReasonList.size())) {
            // The position is within the bounds of the list, so add it.
            statusReasonList.add(position,statusReason);
            result=true;
         } else {
            msg=MessageFormat.format(
                  PersistentBasicTicketImpl.statusReasonIndexOutOfBoundsTemplate,
                  statusReasonList.size());
            throw new IndexOutOfBoundsException(msg);
         } // END if ((position >= 0) && (position < statusReasonList.size()))         
      } // END if ((statusState != null) && (statusReason != null))
      logger.exiting(_CLASS, "addStatusReason(String)",result);
      return result;
   }
   /**
    * Returns a list of available status reason values for the specified 
    * <code>Status</code> field.
    * @return A list of acceptable values for the <code>Status</code> field.
    */
   public List<String> getAvailableStatusReasons(String statusState) {
      logger.entering(_CLASS, "getAvailableStatusReasons(String)",
            statusState);
      List<String> unmodifiableList=null;
      
      if (statusState != null) {
        if (this.statusReasons != null) {
           unmodifiableList=this.statusReasons.get(statusState);
        } // END if (this.statusReasons != null)
      } else {
         throw new IllegalArgumentException(
               "A valid status state must be passed as a parameter.");         
      } // END if (statusState != null)
      if (unmodifiableList == null) {
         unmodifiableList=new ArrayList<String>();
      } // END if (unmodifiableList == null)
      unmodifiableList=Collections.unmodifiableList(unmodifiableList);
      
      logger.exiting(_CLASS, "getAvailableStatusStates()",unmodifiableList);
      return unmodifiableList;
   }
   /**
    * Returns the status reason for the specified status state at the specified 
    * position in the list.
    * @param position - the index of the element to return
    * @return the element at the specified position in the list.
    * @throws java.lang.IndexOutOfBoundsException - if the position is out
    * of range (position < 0 || position >= size)
    */
   public final String getStatusReason(String statusState, int position) {
      logger.entering(_CLASS, "getStatusState(int)",position);
      List<String> statusReasons=null;
      String msg="";
      String statusReason="";
      
      if ((statusState != null) && (statusState.length() > 0)) {
         if (this.statusReasons != null) {
            statusReasons=this.statusReasons.get(statusState);
            if (
                  (statusReasons != null) &&
                  (position > 0) &&
                  (position < statusReasons.size())
               ) {
               statusReason=statusReasons.get(position);
            } else {
              if (statusReasons == null) {
                 throw new UnsupportedOperationException(
                    "The specified status state as no defined status reasons.");
              } else {
                 msg=MessageFormat.format(
                   PersistentBasicTicketImpl.statusReasonIndexOutOfBoundsTemplate,
                   statusReasons.size()-1);
                 throw new IndexOutOfBoundsException(msg);
              }
            } // END if ((statusReasons != null) && (position > 0) && ...
         } // END if (this.statusReasons != null)          
      } else {
         throw new UnsupportedOperationException(
               "Status states cannot be either null or empty strings.");
      } // END if ((statusState != null) && (statusState.length() > 0)) 
      
      logger.exiting(_CLASS, "getStatusState(int)",statusReason);
      return statusReason;
   }
   /**
    * Removes the status reason value at the specified position in the list.
    * @param position - the index of the element to be removed.
    * @return the element that was previously at the specified position.
    */
   public final String removeStatusReason(String statusState, int position) {
      logger.entering(_CLASS, "removeStatusReason(String,int)",
            new Object[] {statusState,position});
      List<String> statusReasons=null;
      String removedValue="";
      String msg="";
      
      if ((statusState != null) && (statusState.length() > 0)) {
         if (this.statusReasons != null) {
            statusReasons=this.statusReasons.get(statusState);
            if (
                  (statusReasons != null) &&
                  (position > 0) &&
                  (position < statusReasons.size())
               ) {
               removedValue=statusReasons.remove(position);
            } else {
              if (statusReasons == null) {
                 throw new UnsupportedOperationException(
                    "The specified status state as no defined status reasons.");
              } else {
                 msg=MessageFormat.format(
                   PersistentBasicTicketImpl.statusReasonIndexOutOfBoundsTemplate,
                   statusReasons.size()-1);
                 throw new IndexOutOfBoundsException(msg);
              }
            } // END if ((statusReasons != null) && (position > 0) && ...
         } else {
            throw new UnsupportedOperationException(                 
                  "The specified status state as no defined status reasons.");
         } // END if (this.statusReasons != null)          
      } else {
         throw new UnsupportedOperationException(
               "Status states cannot be either null or empty strings.");
      } // END if ((statusState != null) && (statusState.length() > 0)) 

      logger.exiting(_CLASS, "removeStatusReason(String,int)",removedValue);
      return removedValue;
   }
   /**
    * Removes the specified status reason from the list of values for the 
    * specified status state
    * @param statusState - the status state which has the status resons that 
    * are to be modified by this method call.
    * @param statusReason - the status reason to be removed from the list.
    * @return <code>true</code> if the status reason is currently contained in
    * the list.
    */
   public final boolean removeStatusReason(String statusState, String statusReason) {
      logger.entering(_CLASS,"removeStatusReason(String,String)",
            new Object[] {statusState, statusReason});
      boolean result=false;
      List<String> statusReasonList=null;
      
      if ((statusState != null) && (statusState.length() > 0)) {
         if (this.statusReasons != null) {
            statusReasonList=this.statusReasons.get(statusState);
            if (statusReasonList != null) {
               result=statusReasonList.remove(statusReason);
            } // END if (statusReasonList != null)            
         } // END if (this.statusReasons != null)          
      } else {
         throw new UnsupportedOperationException(
               "Status states cannot be either null or empty strings.");
      } // END if ((statusState != null) && (statusState.length() > 0)) 
      
      logger.exiting(_CLASS,"removeStatusReason(String,String)",result);
      return result;
   }
   /**
    * Replaces the status reason value at the specified position with the 
    * specified element.
    * @param statusState
    * @param position the index of the status state to replace
    * @param statusReason the new status reason to be stored at the specified
    * position.
    * @return The status state value that was previously stored at the 
    * specified position.
    */
   public final String setStatusReason(String statusState, int position, 
         String statusReason) {
      logger.entering(_CLASS,"setStatusReason(String,int,String)",
            new Object[] {statusState,position,statusReason});
      List<String> statusReasonList=null;
      String msg="";
      String replacedValue="";
      UnsupportedOperationException exception=null;
      
      if ((statusState != null) && (statusState.length() > 0)) {
         if (this.statusReasons != null) {
            statusReasonList=this.statusReasons.get(statusState);
            if (statusReasonList != null) {
               if ((position > 0) && (position < statusReasonList.size())) {
                  replacedValue=statusReasonList.set(position,  statusReason);
               } else {
                  msg=MessageFormat.format(
                   PersistentBasicTicketImpl.statusReasonIndexOutOfBoundsTemplate,
                   statusReasonList.size());
                  throw new IndexOutOfBoundsException(msg);
               } // END if ((position > 0) && (position < ...
            } // END if (statusReasonList != null)
         } else {
            logger.finest("THere are NO defined status reason values.");
         } // END if (this.statusReasons != null)
      } else {
         exception=new UnsupportedOperationException(
            "The Status state cannot be either a null value or empty string. ");
         logger.log(Level.SEVERE,
               "Unable to update status reason value.",
               exception);
      } // END if ((statusState != null) && (statusState.length() > 0))
      logger.exiting(_CLASS, "setStatusState(int,String)",replacedValue);
      return replacedValue;
   }
      
   /**
    * Returns available values for the <code>StatusReason</code> field.
    * @return A list of acceptable values for the <code>StatusReason</code> 
    * field.
    */
   public List<String> getAvailableStatusReasons() {
      logger.entering(_CLASS,"getAvailableStatusReasons()");
      List<String> statusReasons=null;
      String statusState=null;
      
      statusState=getStatusState();
      statusReasons=this.statusReasons.get(statusState);
      if (statusReasons == null) statusReasons=new ArrayList<String>();
      
      logger.exiting(_CLASS,"getAvailableStatusReasons()",statusReasons);
      return statusReasons;
   }
   
   //***** statusState
   public final String getStatusState() {
     logger.entering(_CLASS, "getStatusState()");
     logger.exiting(_CLASS,"getStatusState()",this.statusState);
     return this.statusState;
   }   
   public final void setStatusState(String statusState) {
     logger.entering(_CLASS, "setStatusState(String)",statusState);
     this.statusState=defaultValue(statusState);
     logger.exiting(_CLASS, "setStatusState(String)");
   }
   //***** statusStates
   /**
    * Appends the specified status state to the end of the list.
    * @param statusState - the status state to be added to the list.
    * @return <code>true</code> if the status state was added to the end of the
    * list
    */
   public final boolean addStatusState(String statusState) {
      logger.entering(_CLASS, "addStatusState(String)",statusState);
      boolean result=false;
      
      if (statusState != null) {
         if (this.statusStates == null) {
            this.statusStates=new ArrayList<String>();
            result=this.statusStates.add(statusState);            
         } else {
            if (!this.statusStates.contains(statusState)) {
               /* The exisitng list of status states does not already contain 
                * the specified status state, so we'll add it. */
               result=this.statusStates.add(statusState);
            } // END if (!this.statusStates.contains(statusState))
         } // END if (this.statusStates == null)          
      }
      logger.exiting(_CLASS, "addStatusState(String)",result);
      return result;
   }
   /**
    * Returns a list of available values for the <code>Status</code> field.
    * @return A list of acceptable values for the <code>Status</code> field.
    */
   public List<String> getAvailableStatusStates() {
      logger.entering(_CLASS, "getAvailableStatusStates()");
      List<String> unmodifiableList=null;
      
      unmodifiableList=Collections.unmodifiableList(this.statusStates);
      
      logger.exiting(_CLASS, "getAvailableStatusStates()",unmodifiableList);
      return unmodifiableList;
   }
   /**
    * Returns the status state at the specified position in the list.
    * @param position - the index of the element to return
    * @return the element at the specified position in the list.
    * @throws java.lang.IndexOutOfBoundsException - if the position is out
    * of range (position < 0 || position >= size)
    */
   public final String getStatusState(int position) {
      logger.entering(_CLASS, "getStatusState(int)",position);
      String msg="";
      String statusState="";
      
      if (
            (position >= 0) &&
            (this.statusStates != null) &&
            (position < this.statusStates.size())
         ) {
         statusState=this.statusStates.get(position);
      } else {
         msg=MessageFormat.format(
               PersistentBasicTicketImpl.statusStateIndexOutOfBoundsTemplate,
               position);
         throw new IndexOutOfBoundsException(msg);
      } // END if ((position >= 0) && (this.statusStates != null) && ...
      
      logger.exiting(_CLASS, "getStatusState(int)",statusState);
      return statusState;
   }
   /**
    * Removes the element at the specified position in the list.
    * @param position - the index of the element to be removed.
    * @return the element that was previously at the specified position.
    */
   public final String removeStatusState(int position) {
      logger.entering(_CLASS, "removeStatusState(int)",position);
      String removedValue="";
      String msg="";
      
      if (
            (position >= 0) || 
            (
                  (this.statusStates != null) &&
                  (position > this.statusStates.size())
            )
         ) {
         removedValue=this.statusStates.remove(position);
      } else {
         // The index is out of bounds.
         msg=MessageFormat.format(
               PersistentBasicTicketImpl.statusStateIndexOutOfBoundsTemplate, 
               position);
         throw new IndexOutOfBoundsException(msg);
      } // END if ((position >= 0) || ((this.statusStates != null) ...
      logger.exiting(_CLASS, "removeStatusState(int)",removedValue);
      return removedValue;
   }
   /**
    * Removes the specified status state from the list.
    * @param statusState - The status state to be removed from the list.
    * @return <code>true</code> if the status state is currently contained in
    * the list.
    */
   public final boolean removeStatusState(String statusState) {
      logger.entering(_CLASS,"removeStatusState(String)",statusState);
      boolean result=false;
      if (statusState != null) {
         result=this.statusStates.remove(statusState);
      } // END if (statusState != null)
      logger.exiting(_CLASS,"removeStatusState(String)",result);
      return result;
   }
   /**
    * Replaces the status state value at the specified position with the 
    * specified element.
    * @param position the index of the status state to replace
    * @param statusState the new status state to be stored at the specified
    * position.
    * @return The status state value that was previously stored at the 
    * specified position.
    */
   public final String setStatusState(int position, String statusState) {
      logger.entering(_CLASS,"setStatusState(int,String)",
            new Object[] {position,statusState});
      String msg="";
      String replacedValue="";
      
      if (
            (position > 0) &&
            (this.statusStates != null) &&
            (position < this.statusStates.size())
         ) {
         if ((statusState != null) && (statusState.length() > 0)) {
            replacedValue=this.statusStates.set(position,statusState);
         } else {
            throw new UnsupportedOperationException(
             "A status state can be neither a null value nor an empty string.");
         } // END if ((statusState != null) && (statusState.length() > 0))
      } else {
         msg=MessageFormat.format(
               PersistentBasicTicketImpl.statusStateIndexOutOfBoundsTemplate,
               position);
         throw new IndexOutOfBoundsException(msg);
      } // END 
      logger.exiting(_CLASS, "setStatusState(int,String)",replacedValue);
      return replacedValue;
   }
   
   //***** title
   public final String getTitle() {
      logger.entering(_CLASS, "getTitle()");
      logger.exiting(_CLASS, "getTitle()",this.title);
      return this.title;
   }
   public final void setTitle(String title) {
      logger.entering(_CLASS,"setTitle(String)",title);
      this.title = defaultValue(title);
      logger.exiting(_CLASS, "setTitle(String)");
   }
}