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
package com.wildstartech.wfa.dao.ticketing;

import java.util.ArrayList;
import java.util.List;

import com.wildstartech.wfa.dao.TestCaseBase;
import com.wildstartech.wfa.ticketing.BasicTicket;

public class TestCaseBasicTicket
extends TestCaseBase 
implements BasicTicket {
   private String requestId="";
   private String shortDescription="";
   private String statusReason="";
   private String statusState="";
   private String title="";
   
   //********** accessor methods
   //***** requestId
   public String getRequestId() {
      return this.requestId;
   }
   public void setRequestId(String requestId) {
      this.requestId = (requestId);      
   }
   //***** shortDescription
   public String getShortDescription() {
     return this.shortDescription;      
   }
   public void setShortDescription(String shortDescription) {
      this.shortDescription = shortDescription;      
   }
   //***** statusReason
   public List<String> getAvailableStatusReasons() {
      List<String> statusReasons=null;
      
      statusReasons=new ArrayList<String>();
      
      return statusReasons;
   }
   public String getStatusReason() {
      return this.statusReason;
   }
   public void setStatusReason(String statusReason) {
      this.statusReason = statusReason;      
   }
   //***** statusState
   public List<String> getAvailableStatusStates() {
      List<String> statusStates=null;
      
      statusStates=new ArrayList<String>();
      
      return statusStates;
   }
   public String getStatusState() {
      return this.statusState;
   }
   public void setStatusState(String statusState) {
      this.statusState = statusState;
   }
   //***** title
   public String getTitle() {
      return this.title;
   }
   public void setTitle(String title) {
      this.title = title;      
   }
}