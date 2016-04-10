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
package com.wildstartech.wfa.dao.logistics.ltl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.wildstartech.wfa.dao.TestCaseBase;

public class TestCaseFreightDueInWorkOrderBase extends TestCaseBase {
   private Date estimatedShipDate=null;
   private Date dateShipped=null;
   private String carrierName="";
   private String carrierTrackingNumber="";
   private String contactCompanyName="";
   private String contactName="";
   private String contactEmail="";
   private String contactPreferredTelephoneNumber="";
   private String manufacturerName="";
   private String referenceIdentifier="";
   private String specialHandling="";
   
   /**
    * Default no-argument test case.
    */
   public TestCaseFreightDueInWorkOrderBase() {
      
   }
   
   //***** estimatedShipDate
   public Date getEstimatedShipDate() {
      return this.estimatedShipDate;
   }
   public void setEstimatedShipDate(Date estimatedShipDate) {
      this.estimatedShipDate = estimatedShipDate;
   }
   public void setEstimatedShipDate(String parseableDate) {
      this.estimatedShipDate=parseDate(parseableDate);
   }
   //***** dateShipped
   public Date getDateShipped() {
      return this.dateShipped;
   }
   public void setDateShipped(Date dateShipped) {
      this.dateShipped = dateShipped;
   }
   public void setDateShipped(String parseableDate) {
      this.dateShipped = parseDate(parseableDate);
   }
   //***** carrierName
   public String getCarrierName() {
      return this.carrierName;
   }
   public void setCarrierName(String carrierName) {
      this.carrierName = carrierName;
   }
   //***** carrierTrackingNumber
   public String getCarrierTrackingNumber() {
      return this.carrierTrackingNumber;
   }
   public void setCarrierTrackingNumber(String carrierTrackingNumber) {
      this.carrierTrackingNumber = carrierTrackingNumber;
   }
   //***** contactCompanyName
   public String getContactCompanyName() {
      return this.contactCompanyName;
   }
   public void setContactCompanyName(String contactCompanyName) {
      this.contactCompanyName = contactCompanyName;
   }
   //***** contactName
   public String getContactName() {
      return this.contactName;
   }
   public void setContactName(String contactName) {
      this.contactName = contactName;
   }
   //***** contactEmail
   public String getContactEmail() {
      return this.contactEmail;
   }
   public void setContactEmail(String contactEmail) {
      this.contactEmail = contactEmail;
   }
   //***** contactPreferredTelephoneNumber
   public String getContactPreferredTelephoneNumber() {
      return this.contactPreferredTelephoneNumber;
   }
   public void setContactPreferredTelephoneNumber(
         String contactPreferredTelephoneNumber) {
      this.contactPreferredTelephoneNumber = contactPreferredTelephoneNumber;
   }
   //***** manufacturerName
   public String getManufacturerName() {
      return this.manufacturerName;
   }
   public void setManufacturerName(String manufacturerName) {
      this.manufacturerName = manufacturerName;
   }
   //***** referenceIdentifier
   public String getReferenceIdentifier() {
      return this.referenceIdentifier;
   }
   public void setReferenceIdentifier(String referenceIdentifier) {
      this.referenceIdentifier = referenceIdentifier;
   }
   //***** specialHandling
   public String getSpecialHandling() {
      return this.specialHandling;
   }
   public void setSpecialHandling(String specialHandling) {
      this.specialHandling = specialHandling;
   }
}