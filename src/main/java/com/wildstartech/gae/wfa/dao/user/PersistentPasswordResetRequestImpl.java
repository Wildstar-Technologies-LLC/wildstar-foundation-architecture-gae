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
package com.wildstartech.gae.wfa.dao.user;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.validator.routines.InetAddressValidator;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.user.PersistentPasswordResetRequest;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.user.PasswordResetRequest;
import com.wildstartech.wfa.user.PasswordResetRequestResources;
import com.wildstartech.wfa.user.UserNameTooLongException;

public class PersistentPasswordResetRequestImpl 
extends WildObjectImpl<PasswordResetRequest>
      implements PersistentPasswordResetRequest {
   /** Used in object serialization. */
   private static final long serialVersionUID = -1945728719812311925L;
   private static final String _CLASS=
         PersistentPasswordResetRequestImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   public final static String _KIND="com.wildstartech.wfa.user.PasswordResetRequest";
   
   private Date dateSubmitted=null;
   private Date expirationDate=null;
   private InetAddress addressOfRequest=null;
   private String userName=null;
   
   /**
    * Default, no-argument constructor.
    */
   public PersistentPasswordResetRequestImpl() {
      logger.entering(_CLASS,"PersistentPasswordResetRequestImpl()");
      logger.exiting(_CLASS,"PersistentPasswordResetRequestImpl()");
   }
   //********** Utility Methods
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",
            PersistentPasswordResetRequestImpl._KIND);
      return PersistentPasswordResetRequestImpl._KIND;
   }
   
   @Override
   public String getResourceBundleBaseName() {
      logger.entering(_CLASS, "getResourceBundleBaseName()");
      logger.exiting(_CLASS, "getResourceBundleBaseName()",
            PasswordResetRequest.RESOURCE_BUNDLE);
      return PasswordResetRequest.RESOURCE_BUNDLE;
   }
   
   public void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity)",entity);
      
      super.populateFromEntity(entity, ctx);
      setAddressOfRequest(getPropertyAsInetAddress(entity,"addressOfRequest"));
      setDateSubmitted(getPropertyAsDate(entity,"dateSubmitted"));
      setExpirationDate(getPropertyAsDate(entity,"expirationDate"));
      try {
         setUserName(getPropertyAsString(entity,"userName"));
      } catch (UserNameTooLongException ex) {
         logger.log(Level.SEVERE, 
               "This should not happen.", 
               ex);
      } // END try/catch
      
      logger.exiting(_CLASS, "populateFromEntity(Entity)");
   }
   
   @Override
   public void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(Entity)",entity);
      InetAddress address=null;
      String addressStr="";
      if (entity != null) {
         super.populateEntity(entity);
         address=getAddressOfRequest();
         if (address != null) {
            addressStr=address.getHostAddress();
         } // END if (address != null)
         entity.setProperty("addressOfRequest", addressStr);
         entity.setProperty("dateSubmitted", getDateSubmitted());
         entity.setProperty("expirationDate", getExpirationDate());
         entity.setProperty("userName", getUserName());         
      } else {
         logger.warning("The entity parameter is null.");
      } // END if (enityt != null)
      
      logger.exiting(_CLASS, "populateEntity(Entity)");
   }
   
   public void populateFromObject(PasswordResetRequest request) {
      logger.entering(_CLASS, 
            "populateFromObject(PasswordResetRequest",request);
      setAddressOfRequest(request.getAddressOfRequest());
      setDateSubmitted(request.getDateSubmitted());
      setExpirationDate(request.getExpirationDate());
      try {
         setUserName(request.getUserName());
      } catch (UserNameTooLongException ex) {
         logger.warning("The specified user name is too long.");
      } // END try/catch
      logger.exiting(_CLASS, "populateFromObject(PasswordResetRequest)");
   }
   
   /**
    * Returns a string representation consisting of object properties.
    */
   public String toPropertyString() {
      logger.entering(_CLASS, "toPropertyString()");
      String returnValue=null;
      StringBuilder sb=null;
      
      sb=new StringBuilder(1024);
      sb.append(super.toPropertyString());
      sb.append(",userName=\"").append(getUserName()).append("\", ");
      sb.append("addressOfRequest,=\"").append(getAddressOfRequest());
      sb.append("\",dateSubmitted=\"");
      sb.append(getFormattedDate(getDateSubmitted()));
      sb.append("\",expirationDate=\"");
      sb.append(getFormattedDate(getExpirationDate()));
      sb.append("\"");
        
      returnValue=sb.toString();
      
      logger.exiting(_CLASS, "toPropertyString()",returnValue);
      return returnValue;
   }
   
   /**
    * Returns a string representation of the object.
    */
   public String toString() {
     logger.entering(_CLASS,"toString()");
     String result=null;
     StringBuilder sb=null;
     
     sb=new StringBuilder(256);
     sb.append("PersistentPasswordResetRequestImpl [");
     sb.append(toPropertyString());
     sb.append("]");
     result=sb.toString();
     logger.exiting(_CLASS,"toString()",result);
     return result;
   }  
   @Override
   public void updateFromObject(PasswordResetRequest source) {
      logger.entering(_CLASS, 
            "updateFromObject(PasswordResetRequest)",
            source);
      populateFromObject(source);
      
      logger.exiting(_CLASS, "updateFromObject(PasswordResetRequest)");
   }
   //********** Accessor Methods
   @Override
   public Date getDateSubmitted() {
      logger.entering(_CLASS, "getDateSubmitted()");
      logger.exiting(_CLASS, "getDateSubmitted()",this.dateSubmitted);
      return this.dateSubmitted;
   }

   @Override
   public void setDateSubmitted(Date submitDate) {
      logger.entering(_CLASS, "setDateSubmitted(Date)",submitDate);
      this.dateSubmitted=submitDate;
      logger.exiting(_CLASS, "setDateSubmitted(Date)");
   }

   @Override
   public Date getExpirationDate() {
      logger.entering(_CLASS, "getExpirationDate()");
      logger.exiting(_CLASS, "getExpirationDate()",this.expirationDate);
      return this.expirationDate;
   }

   @Override
   public void setExpirationDate(Date expirationDate) {
      logger.entering(_CLASS, "setExpirationDate(Date)",expirationDate);
      this.expirationDate=expirationDate;
      logger.exiting(_CLASS, "setExpirationDate(Date)");
   }

   @Override
   public InetAddress getAddressOfRequest() {
      logger.entering(_CLASS, "getAddressOfRequest()");
      logger.exiting(_CLASS, "getAddressOfRequest()",this.addressOfRequest);
      return this.addressOfRequest;
   }

   @Override
   public void setAddressOfRequest(InetAddress address) {
      logger.entering(_CLASS, "setAddressOfRequest(InetAddress)",address);
      this.addressOfRequest=address;
      logger.exiting(_CLASS, "setAddressOfRequest(InetAddress)");
   }
   
   @Override
   public void setAddressOfRequest(String addressStr) {
      logger.entering(_CLASS, "setAddressOfRequest(String)", addressStr);
      InetAddressValidator validator=null;
      InetAddress address=null;
      String msg="";
      
      if (addressStr != null) {
         validator=InetAddressValidator.getInstance();
         // Confirms the specified string is a well-formed IP address
         if (validator.isValidInet4Address(addressStr)) {
            try {
               // Let's parse the string into an InetAddress
               address=InetAddress.getByName(addressStr);
               setAddressOfRequest(address);
            } catch (UnknownHostException ex) {
               logger.log(Level.SEVERE,
                     "UnknownHostException thrown - shouldn't happen.",
                     ex);
            } // END try/catch
         } else {
            msg=getLocalizedMessage(
                  PasswordResetRequestResources.MSGKEY_INVALID_INET,
                  new Object[] {addressStr});
            logger.severe(msg);
            throw new IllegalArgumentException(msg);            
         } // END if (validator.isValidInet4Address(addressStr))
      } else {
         logger.warning("The addressStr parameter was null.");
      } // END if (addressStr != null)
      
      logger.exiting(_CLASS, "setAddressOfRequest(String)", address);
   }

   @Override
   public String getUserName() {
      logger.entering(_CLASS, "getUserName()");
      logger.exiting(_CLASS, "getUserName()",this.userName);
      return this.userName;
   }

   @Override
   public void setUserName(String userName) throws UserNameTooLongException {
      logger.entering(_CLASS, "setUserName(String)",userName);
      this.userName=defaultValue(userName);
      logger.exiting(_CLASS, "setUserName(String)");      
   }
}