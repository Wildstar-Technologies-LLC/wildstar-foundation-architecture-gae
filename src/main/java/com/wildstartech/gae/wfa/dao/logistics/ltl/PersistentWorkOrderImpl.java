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
package com.wildstartech.gae.wfa.dao.logistics.ltl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AjaxBehaviorEvent;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.wildstartech.gae.wfa.dao.journal.JournalDAOImpl;
import com.wildstartech.gae.wfa.dao.journal.PersistentJournalEntryImpl;
import com.wildstartech.gae.wfa.dao.ticketing.PersistentBasicTicketImpl;
import com.wildstartech.wfa.dao.WildObject;
import com.wildstartech.wfa.dao.finance.CreditCardDAO;
import com.wildstartech.wfa.dao.finance.CreditCardDAOFactory;
import com.wildstartech.wfa.dao.finance.PersistentCreditCard;
import com.wildstartech.wfa.dao.journal.PersistentJournalEntry;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentWorkOrder;
import com.wildstartech.wfa.dao.logistics.ltl.WorkOrderLineItemDAO;
import com.wildstartech.wfa.dao.logistics.ltl.WorkOrderLineItemDAOFactory;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.finance.PaymentCard;
import com.wildstartech.wfa.journal.JournalEntry;
import com.wildstartech.wfa.location.DistanceMeasurement;
import com.wildstartech.wfa.location.DistanceServiceProviderFactory;
import com.wildstartech.wfa.location.DistanceMeasurement.UNITS;
import com.wildstartech.wfa.location.address.City;
import com.wildstartech.wfa.location.address.PostalCodeFactory;
import com.wildstartech.wfa.location.spi.DistanceServiceProvider;
import com.wildstartech.wfa.logistics.ltl.AccessorialCharge;
import com.wildstartech.wfa.logistics.ltl.WorkOrder;
import com.wildstartech.wfa.logistics.ltl.WorkOrderLineItem;
import com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModel;
import com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModelFactory;

public class PersistentWorkOrderImpl 
extends PersistentBasicTicketImpl<WorkOrder>
implements PersistentWorkOrder {
   /** Used in object serialization. */
   private static final long serialVersionUID = -7322258683426389155L;
   private static final String _CLASS=PersistentWorkOrderImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   private static final String ADJUSTMENT_TYPE_FIXED_AMOUNT = "Fixed Amount";
   private static final String ADJUSTMENT_TYPE_PERCENTAGE = "Percentage";

   /* Default value for the 'ServiceLevel' field. */
   private static final String SERVICE_LEVEL_DEFAULT = "White Glove";
   private static List<String> serviceLevels = new ArrayList<String>();
   static {
      serviceLevels.add("Threshold");
      serviceLevels.add("Room of Choice");
      serviceLevels.add("White Glove");
   }
   
   private static List<String> internetOptions = new ArrayList<String>();

   static {
      internetOptions.add(new String("Google"));
      internetOptions.add(new String("Yahoo!"));
      internetOptions.add(new String("MSN"));
      internetOptions.add(new String("AOL"));
      internetOptions.add(new String("Ask"));
      internetOptions.add(new String("Other"));
   }

   private static List<String> referralOptions = new ArrayList<String>();

   static {
      referralOptions.add("Magazine");
      referralOptions.add("Newspaper");
      referralOptions.add("Television");
      referralOptions.add("Tradeshow");
      referralOptions.add("Recommendation");
      referralOptions.add("Internet");
      referralOptions.add("Other");
   }
   
   /* The list of available 'Status' fields. */
   private static List<String> statusStates = new ArrayList<String>();

   static {
      statusStates.add("New");
      statusStates.add("Assigned");
      statusStates.add("Pending");
      statusStates.add("Accepted");
      statusStates.add("Resolved");
      statusStates.add("Closed");
   }

   private static List<String> resolvedClosedStatusReasons = 
         new ArrayList<String>();

   static {
      resolvedClosedStatusReasons.add("Accepted");
      resolvedClosedStatusReasons.add("Declined");
      resolvedClosedStatusReasons.add("Canceled");
   }
   
   protected static final String _KIND = 
         "com.wildstartech.wfa.logistics.ltl.WorkOrder";
   
   private boolean initialized = false;
   private boolean assemblyRequired = false;
   private boolean blanketWrapRequired = false;
   private boolean cratingRequired = false;
   private boolean destinationResidential = false;
   private boolean originResidential = false;
   private boolean packagingRequired = false;
   private boolean preview = false;
   private boolean stairCarry = false;
   private boolean valuationDeclined = true;
   private boolean unpackagingRequired = false;
   private double amount = 0.0;
   private double accessorialTotal = 0.0;
   private double adjustmentAmount = 0;
   private double distance = 0.0;
   private double deductible = 0.0;
   private double insuranceCharges = 0.0;
   private double lineItemCharges = 0.0;
   private double fuelSurcharge = 0;
   private double valuation = 0.0;
   private int creditCardExpirationMonth=0;
   private int creditCardExpirationYear=0;
   private int numberOfFlights = 0;
   private AdjustmentType adjustmentType = AdjustmentType.FixedAmount;
   private ArrayList<AccessorialCharge> accessorials = null;
   private ArrayList<WorkOrderLineItem> lineItems = null;
   private ArrayList<WorkOrderLineItem> lineItemsToDelete = null;
   private transient PriceModel priceModel = null;
   private PersistentJournalEntryImpl newJournalEntry=null;
   private String billingCompanyName="";
   private String billingContactEmail="";
   private String billingContactName="";
   private String billingContactPhone="";
   private String billingCity="";
   private String billingMethod = "";
   private String billingState = "";
   private String billingStreetAddress = "";
   private String billingZip = "";
   private String contactCompanyName = "";
   private String contactName = "";
   private String contactPhone = "";
   private String contactPhoneHidden = "";
   private String contactEmail = "";
   private String creditCardIdentifier = "";
   private String creditCardName = "";
   private String creditCardNumber = "";
   private String creditCardType = "";
   private String creditCardVerification = "";
   private String customerReferenceNote = "";
   private String destinationCity = "";
   private String destinationCompanyName = "";
   private String destinationContactEmail = "";
   private String destinationContactName = "";
   private String destinationContactPhone = "";
   private String destinationState = "";
   private String destinationStreetAddress = "";
   private String destinationZip = "";
   private String notes = "";
   private String originCity = "";
   private String originCompanyName = "";
   private String originContactEmail = "";
   private String originContactName = "";
   private String originContactPhone = "";
   private String originState = "";
   private String originStreetAddress = "";
   private String originZip = "";
   private String paymentMethod = "";
   private String priceModelLabel = "";
   private String purchaseOrderNumber = "";
   private String quoteRequestId= "";
   private String referralOther = null;
   private String referralSource = null;
   private String serviceLevel = SERVICE_LEVEL_DEFAULT;
   private Type type=Type.Delivery;
   
   /**
    * Default, no-argument constructor.
    */
   public PersistentWorkOrderImpl() {
      super();
      logger.entering(_CLASS, "PersistentWorkOrderImpl()");
      this.accessorials=new ArrayList<AccessorialCharge>();
      this.lineItems=new ArrayList<WorkOrderLineItem>();
      this.lineItemsToDelete=new ArrayList<WorkOrderLineItem>();
      logger.exiting(_CLASS, "PersistentWorkOrderImpl()");
   }
   
   //********** Utility Methods
   /**
    * Calculate the rate for the quote.
    */
   public void calculateRate() {
      logger.entering(_CLASS, "calculateRate()");
      PriceModel model = null;
      PriceModelFactory factory = null;

      model = getPriceModel();
      if (model == null) {
         factory = PriceModelFactory.getInstance();
         model = factory.getModel(this);
      } // END if (model == null)
      model.calculateTotalCharges(this);
      logger.exiting(_CLASS, "calculateRate()");
   }
   
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      PersistentWorkOrderImpl other = (PersistentWorkOrderImpl) obj;
      if (Double.doubleToLongBits(accessorialTotal) != Double.doubleToLongBits(other.accessorialTotal))
         return false;
      if (accessorials == null) {
         if (other.accessorials != null)
            return false;
      } else if (!accessorials.equals(other.accessorials))
         return false;
      if (Double.doubleToLongBits(adjustmentAmount) != Double.doubleToLongBits(other.adjustmentAmount))
         return false;
      if (adjustmentType != other.adjustmentType)
         return false;
      if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
         return false;
      if (assemblyRequired != other.assemblyRequired)
         return false;
      if (billingCity == null) {
         if (other.billingCity != null)
            return false;
      } else if (!billingCity.equals(other.billingCity))
         return false;
      if (billingCompanyName == null) {
         if (other.billingCompanyName != null)
            return false;
      } else if (!billingCompanyName.equals(other.billingCompanyName))
         return false;
      if (billingContactEmail == null) {
         if (other.billingContactEmail != null)
            return false;
      } else if (!billingContactEmail.equals(other.billingContactEmail))
         return false;
      if (billingContactName == null) {
         if (other.billingContactName != null)
            return false;
      } else if (!billingContactName.equals(other.billingContactName))
         return false;
      if (billingContactPhone == null) {
         if (other.billingContactPhone != null)
            return false;
      } else if (!billingContactPhone.equals(other.billingContactPhone))
         return false;
      if (billingMethod == null) {
         if (other.billingMethod != null)
            return false;
      } else if (!billingMethod.equals(other.billingMethod))
         return false;
      if (billingState == null) {
         if (other.billingState != null)
            return false;
      } else if (!billingState.equals(other.billingState))
         return false;
      if (billingStreetAddress == null) {
         if (other.billingStreetAddress != null)
            return false;
      } else if (!billingStreetAddress.equals(other.billingStreetAddress))
         return false;
      if (billingZip == null) {
         if (other.billingZip != null)
            return false;
      } else if (!billingZip.equals(other.billingZip))
         return false;
      if (blanketWrapRequired != other.blanketWrapRequired)
         return false;
      if (contactCompanyName == null) {
         if (other.contactCompanyName != null)
            return false;
      } else if (!contactCompanyName.equals(other.contactCompanyName))
         return false;
      if (contactEmail == null) {
         if (other.contactEmail != null)
            return false;
      } else if (!contactEmail.equals(other.contactEmail))
         return false;
      if (contactName == null) {
         if (other.contactName != null)
            return false;
      } else if (!contactName.equals(other.contactName))
         return false;
      if (contactPhone == null) {
         if (other.contactPhone != null)
            return false;
      } else if (!contactPhone.equals(other.contactPhone))
         return false;
      if (contactPhoneHidden == null) {
         if (other.contactPhoneHidden != null)
            return false;
      } else if (!contactPhoneHidden.equals(other.contactPhoneHidden))
         return false;
      if (cratingRequired != other.cratingRequired)
         return false;
      if (creditCardExpirationMonth != other.creditCardExpirationMonth)
         return false;
      if (creditCardExpirationYear != other.creditCardExpirationYear)
         return false;
      if (creditCardIdentifier == null) {
         if (other.creditCardIdentifier != null)
            return false;
      } else if (!creditCardIdentifier.equals(other.creditCardIdentifier))
         return false;
      if (creditCardName == null) {
         if (other.creditCardName != null)
            return false;
      } else if (!creditCardName.equals(other.creditCardName))
         return false;
      if (creditCardNumber == null) {
         if (other.creditCardNumber != null)
            return false;
      } else if (!creditCardNumber.equals(other.creditCardNumber))
         return false;
      if (creditCardType == null) {
         if (other.creditCardType != null)
            return false;
      } else if (!creditCardType.equals(other.creditCardType))
         return false;
      if (creditCardVerification == null) {
         if (other.creditCardVerification != null)
            return false;
      } else if (!creditCardVerification.equals(other.creditCardVerification))
         return false;
      if (customerReferenceNote == null) {
         if (other.customerReferenceNote != null)
            return false;
      } else if (!customerReferenceNote.equals(other.customerReferenceNote))
         return false;
      if (Double.doubleToLongBits(deductible) != Double.doubleToLongBits(other.deductible))
         return false;
      if (destinationCity == null) {
         if (other.destinationCity != null)
            return false;
      } else if (!destinationCity.equals(other.destinationCity))
         return false;
      if (destinationCompanyName == null) {
         if (other.destinationCompanyName != null)
            return false;
      } else if (!destinationCompanyName.equals(other.destinationCompanyName))
         return false;
      if (destinationContactEmail == null) {
         if (other.destinationContactEmail != null)
            return false;
      } else if (!destinationContactEmail.equals(other.destinationContactEmail))
         return false;
      if (destinationContactName == null) {
         if (other.destinationContactName != null)
            return false;
      } else if (!destinationContactName.equals(other.destinationContactName))
         return false;
      if (destinationContactPhone == null) {
         if (other.destinationContactPhone != null)
            return false;
      } else if (!destinationContactPhone.equals(other.destinationContactPhone))
         return false;
      if (destinationResidential != other.destinationResidential)
         return false;
      if (destinationState == null) {
         if (other.destinationState != null)
            return false;
      } else if (!destinationState.equals(other.destinationState))
         return false;
      if (destinationStreetAddress == null) {
         if (other.destinationStreetAddress != null)
            return false;
      } else if (!destinationStreetAddress.equals(other.destinationStreetAddress))
         return false;
      if (destinationZip == null) {
         if (other.destinationZip != null)
            return false;
      } else if (!destinationZip.equals(other.destinationZip))
         return false;
      if (Double.doubleToLongBits(distance) != Double.doubleToLongBits(other.distance))
         return false;
      if (Double.doubleToLongBits(fuelSurcharge) != Double.doubleToLongBits(other.fuelSurcharge))
         return false;
      if (initialized != other.initialized)
         return false;
      if (Double.doubleToLongBits(insuranceCharges) != Double.doubleToLongBits(other.insuranceCharges))
         return false;
      if (Double.doubleToLongBits(lineItemCharges) != Double.doubleToLongBits(other.lineItemCharges))
         return false;
      if (lineItems == null) {
         if (other.lineItems != null)
            return false;
      } else if (!lineItems.equals(other.lineItems))
         return false;
      if (lineItemsToDelete == null) {
         if (other.lineItemsToDelete != null)
            return false;
      } else if (!lineItemsToDelete.equals(other.lineItemsToDelete))
         return false;
      if (newJournalEntry == null) {
         if (other.newJournalEntry != null)
            return false;
      } else if (!newJournalEntry.equals(other.newJournalEntry))
         return false;
      if (notes == null) {
         if (other.notes != null)
            return false;
      } else if (!notes.equals(other.notes))
         return false;
      if (numberOfFlights != other.numberOfFlights)
         return false;
      if (originCity == null) {
         if (other.originCity != null)
            return false;
      } else if (!originCity.equals(other.originCity))
         return false;
      if (originCompanyName == null) {
         if (other.originCompanyName != null)
            return false;
      } else if (!originCompanyName.equals(other.originCompanyName))
         return false;
      if (originContactEmail == null) {
         if (other.originContactEmail != null)
            return false;
      } else if (!originContactEmail.equals(other.originContactEmail))
         return false;
      if (originContactName == null) {
         if (other.originContactName != null)
            return false;
      } else if (!originContactName.equals(other.originContactName))
         return false;
      if (originContactPhone == null) {
         if (other.originContactPhone != null)
            return false;
      } else if (!originContactPhone.equals(other.originContactPhone))
         return false;
      if (originResidential != other.originResidential)
         return false;
      if (originState == null) {
         if (other.originState != null)
            return false;
      } else if (!originState.equals(other.originState))
         return false;
      if (originStreetAddress == null) {
         if (other.originStreetAddress != null)
            return false;
      } else if (!originStreetAddress.equals(other.originStreetAddress))
         return false;
      if (originZip == null) {
         if (other.originZip != null)
            return false;
      } else if (!originZip.equals(other.originZip))
         return false;
      if (packagingRequired != other.packagingRequired)
         return false;
      if (paymentMethod == null) {
         if (other.paymentMethod != null)
            return false;
      } else if (!paymentMethod.equals(other.paymentMethod))
         return false;
      if (preview != other.preview)
         return false;
      if (priceModelLabel == null) {
         if (other.priceModelLabel != null)
            return false;
      } else if (!priceModelLabel.equals(other.priceModelLabel))
         return false;
      if (purchaseOrderNumber == null) {
         if (other.purchaseOrderNumber != null)
            return false;
      } else if (!purchaseOrderNumber.equals(other.purchaseOrderNumber))
         return false;
      if (quoteRequestId == null) {
         if (other.quoteRequestId != null)
            return false;
      } else if (!quoteRequestId.equals(other.quoteRequestId))
         return false;
      if (referralOther == null) {
         if (other.referralOther != null)
            return false;
      } else if (!referralOther.equals(other.referralOther))
         return false;
      if (referralSource == null) {
         if (other.referralSource != null)
            return false;
      } else if (!referralSource.equals(other.referralSource))
         return false;
      if (serviceLevel == null) {
         if (other.serviceLevel != null)
            return false;
      } else if (!serviceLevel.equals(other.serviceLevel))
         return false;
      if (stairCarry != other.stairCarry)
         return false;
      if (type != other.type)
         return false;
      if (unpackagingRequired != other.unpackagingRequired)
         return false;
      if (Double.doubleToLongBits(valuation) != Double.doubleToLongBits(other.valuation))
         return false;
      if (valuationDeclined != other.valuationDeclined)
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      long temp;
      temp = Double.doubleToLongBits(accessorialTotal);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((accessorials == null) ? 0 : accessorials.hashCode());
      temp = Double.doubleToLongBits(adjustmentAmount);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((adjustmentType == null) ? 0 : adjustmentType.hashCode());
      temp = Double.doubleToLongBits(amount);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + (assemblyRequired ? 1231 : 1237);
      result = prime * result + ((billingCity == null) ? 0 : billingCity.hashCode());
      result = prime * result + ((billingCompanyName == null) ? 0 : billingCompanyName.hashCode());
      result = prime * result + ((billingContactEmail == null) ? 0 : billingContactEmail.hashCode());
      result = prime * result + ((billingContactName == null) ? 0 : billingContactName.hashCode());
      result = prime * result + ((billingContactPhone == null) ? 0 : billingContactPhone.hashCode());
      result = prime * result + ((billingMethod == null) ? 0 : billingMethod.hashCode());
      result = prime * result + ((billingState == null) ? 0 : billingState.hashCode());
      result = prime * result + ((billingStreetAddress == null) ? 0 : billingStreetAddress.hashCode());
      result = prime * result + ((billingZip == null) ? 0 : billingZip.hashCode());
      result = prime * result + (blanketWrapRequired ? 1231 : 1237);
      result = prime * result + ((contactCompanyName == null) ? 0 : contactCompanyName.hashCode());
      result = prime * result + ((contactEmail == null) ? 0 : contactEmail.hashCode());
      result = prime * result + ((contactName == null) ? 0 : contactName.hashCode());
      result = prime * result + ((contactPhone == null) ? 0 : contactPhone.hashCode());
      result = prime * result + ((contactPhoneHidden == null) ? 0 : contactPhoneHidden.hashCode());
      result = prime * result + (cratingRequired ? 1231 : 1237);
      result = prime * result + creditCardExpirationMonth;
      result = prime * result + creditCardExpirationYear;
      result = prime * result + ((creditCardIdentifier == null) ? 0 : creditCardIdentifier.hashCode());
      result = prime * result + ((creditCardName == null) ? 0 : creditCardName.hashCode());
      result = prime * result + ((creditCardNumber == null) ? 0 : creditCardNumber.hashCode());
      result = prime * result + ((creditCardType == null) ? 0 : creditCardType.hashCode());
      result = prime * result + ((creditCardVerification == null) ? 0 : creditCardVerification.hashCode());
      result = prime * result + ((customerReferenceNote == null) ? 0 : customerReferenceNote.hashCode());
      temp = Double.doubleToLongBits(deductible);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((destinationCity == null) ? 0 : destinationCity.hashCode());
      result = prime * result + ((destinationCompanyName == null) ? 0 : destinationCompanyName.hashCode());
      result = prime * result + ((destinationContactEmail == null) ? 0 : destinationContactEmail.hashCode());
      result = prime * result + ((destinationContactName == null) ? 0 : destinationContactName.hashCode());
      result = prime * result + ((destinationContactPhone == null) ? 0 : destinationContactPhone.hashCode());
      result = prime * result + (destinationResidential ? 1231 : 1237);
      result = prime * result + ((destinationState == null) ? 0 : destinationState.hashCode());
      result = prime * result + ((destinationStreetAddress == null) ? 0 : destinationStreetAddress.hashCode());
      result = prime * result + ((destinationZip == null) ? 0 : destinationZip.hashCode());
      temp = Double.doubleToLongBits(distance);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(fuelSurcharge);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + (initialized ? 1231 : 1237);
      temp = Double.doubleToLongBits(insuranceCharges);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(lineItemCharges);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((lineItems == null) ? 0 : lineItems.hashCode());
      result = prime * result + ((lineItemsToDelete == null) ? 0 : lineItemsToDelete.hashCode());
      result = prime * result + ((newJournalEntry == null) ? 0 : newJournalEntry.hashCode());
      result = prime * result + ((notes == null) ? 0 : notes.hashCode());
      result = prime * result + numberOfFlights;
      result = prime * result + ((originCity == null) ? 0 : originCity.hashCode());
      result = prime * result + ((originCompanyName == null) ? 0 : originCompanyName.hashCode());
      result = prime * result + ((originContactEmail == null) ? 0 : originContactEmail.hashCode());
      result = prime * result + ((originContactName == null) ? 0 : originContactName.hashCode());
      result = prime * result + ((originContactPhone == null) ? 0 : originContactPhone.hashCode());
      result = prime * result + (originResidential ? 1231 : 1237);
      result = prime * result + ((originState == null) ? 0 : originState.hashCode());
      result = prime * result + ((originStreetAddress == null) ? 0 : originStreetAddress.hashCode());
      result = prime * result + ((originZip == null) ? 0 : originZip.hashCode());
      result = prime * result + (packagingRequired ? 1231 : 1237);
      result = prime * result + ((paymentMethod == null) ? 0 : paymentMethod.hashCode());
      result = prime * result + (preview ? 1231 : 1237);
      result = prime * result + ((priceModelLabel == null) ? 0 : priceModelLabel.hashCode());
      result = prime * result + ((purchaseOrderNumber == null) ? 0 : purchaseOrderNumber.hashCode());
      result = prime * result + ((quoteRequestId == null) ? 0 : quoteRequestId.hashCode());
      result = prime * result + ((referralOther == null) ? 0 : referralOther.hashCode());
      result = prime * result + ((referralSource == null) ? 0 : referralSource.hashCode());
      result = prime * result + ((serviceLevel == null) ? 0 : serviceLevel.hashCode());
      result = prime * result + (stairCarry ? 1231 : 1237);
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + (unpackagingRequired ? 1231 : 1237);
      temp = Double.doubleToLongBits(valuation);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + (valuationDeclined ? 1231 : 1237);
      return result;
   }

   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentWorkOrderImpl._KIND);
      return PersistentWorkOrderImpl._KIND;
   }
   
   @Override
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS,"populateEntity(Entity)",entity);
      PriceModel pm=null;
      String tmpStr="";
      Text tmpText = null;
      Type type=null;
      
      if (entity != null) {
         super.populateEntity(entity);
         // accessorialTotal
         entity.setProperty("accessorialTotal", getAccessorialTotal());
         // adjustmentAmount
         entity.setProperty("adjustmentAmount", getAdjustmentAmount());
         // adjustmentType
         switch (getAdjustmentType()) {
         case FixedAmount:
            entity.setProperty("adjustmentType", ADJUSTMENT_TYPE_FIXED_AMOUNT);
            break;
         default:
            entity.setProperty("adjustmentType", ADJUSTMENT_TYPE_PERCENTAGE);
         } // END switch(getAdjustmentType())
           // amount
         entity.setProperty("amount", getAmount());
         // assemblyRequired
         entity.setProperty("assemblyRequired", isAssemblyRequired());
         // billingCity
         entity.setProperty("billingCity", getBillingCity());
         // billingCompanyName
         entity.setProperty("billingCompanyName", getBillingCompanyName());
         // billingContactEmail
         entity.setProperty("billingContactEmail", getBillingContactEmail());
         // billingContactName
         entity.setProperty("billingContactName", getBillingContactName());
         // billingContactPhone
         entity.setProperty("billingContactPhone", getBillingContactPhone());
         // billingState
         entity.setProperty("billingState", getBillingState());
         // billingStreetAddress
         entity.setProperty("billingStreetAddress", getBillingStreetAddress());
         // billingZip
         entity.setProperty("billingZip", getBillingZip());
         // blanketWrapRequired
         entity.setProperty("blanketWrapRequired", isBlanketWrapRequired());
         // contactCompanyName
         entity.setProperty("contactCompanyName", getContactCompanyName());
         // contactEmail
         entity.setProperty("contactEmail", getContactEmail());
         // contactName
         entity.setProperty("contactName", getContactName());
         // contactPhone
         entity.setProperty("contactPhone", getContactPhone());
         // cratingRequired
         entity.setProperty("cratingRequired", isCratingRequired());
         // creditCardIdentifier
         entity.setProperty("creditCardIdentifier", getCreditCardIdentifier());
         // customerReferenceNote
         entity.setProperty("customerReferenceNote",
               getCustomerReferenceNote());
         // deductible
         entity.setProperty("deductible", getDeductible());
         // destinationCity
         entity.setProperty("destinationCity", getDestinationCity());
         // destinationCompanyName
         entity.setProperty("destinationCompanyName",
               getDestinationCompanyName());
         // destinationContactEmail
         entity.setProperty("destinationContactEmail",
               getDestinationContactEmail());
         // destinationContactName
         entity.setProperty("destinationContactName",
               getDestinationContactName());
         // destinationContactPhone
         entity.setProperty("destinationContactPhone",
               getDestinationContactPhone());
         // destinationResidential
         entity.setProperty("destinationResidential", 
               isDestinationResidential());
         // destinationState
         entity.setProperty("destinationState", getDestinationState());
         // destinationStreetAddress
         entity.setProperty("destinationStreetAddress",
               getDestinationStreetAddress());
         // destinationZip
         entity.setProperty("destinationZip", getDestinationZip());
         // distance
         entity.setProperty("distance", getDistance());
         // fuelSurcharge
         entity.setProperty("fuelSurcharge", getFuelSurcharge());
         // notes
         tmpStr = getNotes();
         if (tmpStr != null) {
            if (tmpStr.length() < 500) {
               entity.setProperty("notes", tmpStr);
            } else {
               tmpText = new Text(tmpStr);
               entity.setProperty("notes", tmpText);
            } // END if (tmpNotes.length() < 500)
         } else {
            entity.setProperty("notes", "");
         } // END if (tmpNotes != null)
           // insuranceCharges
         entity.setProperty("insuranceCharges", getInsuranceCharges());
         // lineItemCharges
         entity.setProperty("lineItemCharges", getLineItemCharges());
         // numberOfFlights
         entity.setProperty("numberOfFlights", getNumberOfFlights());
         // originCity
         entity.setProperty("originCity", getOriginCity());
         // originCompanyName
         entity.setProperty("originCompanyName", getOriginCompanyName());
         // originContactEmail
         entity.setProperty("originContactEmail", getOriginContactEmail());
         // originContactName
         entity.setProperty("originContactName", getOriginContactName());
         // originContactPhone
         entity.setProperty("originContactPhone", getOriginContactPhone());
         // originResidential
         entity.setProperty("originResidential", 
               isOriginResidential());
         // originState
         entity.setProperty("originState", getOriginState());
         // originStreetAddress
         entity.setProperty("originStreetAddress", getOriginStreetAddress());
         // originZip
         entity.setProperty("originZip", getOriginZip());
         // packagingRequired
         entity.setProperty("packagingRequired", isPackagingRequired());
         // paymentMethod
         entity.setProperty("paymentMethod", getPaymentMethod());
         // priceModel
         pm = getPriceModel();
         if (pm == null) {
            tmpStr = "";
         } else {
            tmpStr = pm.getLabel();
         } // END if (pm == null)
         entity.setProperty("priceModelLabel", tmpStr);
         // purchaseOrderNumber
         entity.setProperty("purchaseOrderNumber", getPurchaseOrderNumber());
         // quoteRequestId
         entity.setProperty("quoteRequestId", getQuoteRequestId());
         /*
          * There are dependencies with the referralSource and referralOther
          * properties, so referralSource is set first.
          */
         // referralSource
         entity.setProperty("referralSource", getReferralSource());
         // referralOther
         entity.setProperty("referralOther", getReferralOther());
         // serviceLevel
         entity.setProperty("serviceLevel", getServiceLevel());
         // stairCarry
         entity.setProperty("stairCarry", isStairCarry());
         // type
         type=getType();
         switch(type) {
            case Pickup:
               entity.setProperty("type", "Pick-Up");
               break;
            default:
               entity.setProperty("type", "Delivery");
               break;
         }
         // unpackagingRequired
         entity.setProperty("unpackagingRequired", isUnpackagingRequired());
         // valuation
         entity.setProperty("valuation", getValuation());
         // valuationDeclined
         entity.setProperty("valuationDeclined", isValuationDeclined());
         // quoteRequestId
         entity.setProperty("quoteRequestId", getQuoteRequestId());
      } else {
         logger.severe("The entity passed to the method was null.");
      } // END if (entity != null) 
   }
   
   /**
    * Populate the current object from the specified Entity.
    */
   @Override
   public void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity,UserContext)", 
            new Object[] {entity, ctx});
      boolean recalculatePrice = false;
      double tmpDouble = 0;
      CreditCardDAO cDAO=null;
      CreditCardDAOFactory cDAOFactory=null;
      PersistentCreditCard pCard=null;
      PriceModel pm = null;
      PriceModelFactory pmFactory;
      WorkOrderLineItemDAO woliDao = null;
      List<WorkOrderLineItem> pLineItems = null;
      String tmpStr = null;

      if (entity != null) {
         super.populateFromEntity(entity, ctx);
         // accessorialTotal
         setAccessorialTotal(getPropertyAsDouble(entity, "accessorialTotal"));
         // adjustmentAmount
         setAdjustmentAmount(getPropertyAsDouble(entity, "adjustmentAmount"));
         // adjustmentType
         tmpStr = getPropertyAsString(entity, "adjustmentType");
         if ((tmpStr != null)
               && (tmpStr.equalsIgnoreCase(ADJUSTMENT_TYPE_FIXED_AMOUNT))) {
            setAdjustmentType(AdjustmentType.FixedAmount);
         } else {
            setAdjustmentType(AdjustmentType.Percentage);
         } // END if (tmpStr.equalsIgnoreCase(ADJUSTMENT_TYPE_FIXED_AMOUNT))
           // amount
         setAmount(getPropertyAsDouble(entity, "amount"));
         // assemblyRequired
         setAssemblyRequired(getPropertyAsBoolean(entity, "assemblyRequired"));
         // billingCity
         setBillingCity(getPropertyAsString(entity,"billingCity"));
         // billingCompanyName
         setBillingCompanyName(getPropertyAsString(entity,"billingCompanyName"));
         // billingContactEmail
         setBillingContactEmail(getPropertyAsString(entity,"billingContactEmail"));
         // billingContactName
         setBillingContactName(getPropertyAsString(entity,"billingContactName"));
         // billingContactPhone
         setBillingContactPhone(getPropertyAsString(entity,"billingContactPhone"));
         // billingMethod
         setBillingMethod(getPropertyAsString(entity,"billingMethod"));
         // billingState
         setBillingState(getPropertyAsString(entity,"billingState"));
         // billingStreetAddress
         setBillingStreetAddress(
               getPropertyAsString(entity,"billingStreetAddress"));
         // billingZip
         setBillingZip(getPropertyAsString(entity,"billingZip"));
         // blanketWrapRequired
         setBlanketWrapRequired(
               getPropertyAsBoolean(entity, "blanketWrapRequired"));
         // contactCompanyName
         setContactCompanyName(getPropertyAsString(entity, "contactCompanyName"));
         // contactEmail
         setContactEmail(getPropertyAsString(entity, "contactEmail"));
         // contactName
         setContactName(getPropertyAsString(entity, "contactName"));
         // contactPhone
         setContactPhone(getPropertyAsString(entity, "contactPhone"));
         // cratingRequired
         setCratingRequired(getPropertyAsBoolean(entity, "cratingRequired"));
         // creditCardIdentifier
         tmpStr=getPropertyAsString(entity,"creditCardIdentifier");
         setCreditCardIdentifier(
               getPropertyAsString(entity,"creditCardIdentifier"));         
         // ***** CreditCard data
         // creditCardIdentifier
         tmpStr = getPropertyAsString(entity, "creditCardIdentifier");
         setCreditCardIdentifier(getPropertyAsString(entity, "creditCardIdentifier"));
         // read the rest of the credit card data.
         if (!isEmpty(tmpStr)) {
            cDAOFactory=new CreditCardDAOFactory();
            cDAO=cDAOFactory.getDAO();
            pCard=cDAO.findByIdentifier(tmpStr, ctx);
            if (pCard != null) {
               // creditCardExpirationMonth
               setCreditCardExpirationMonth(pCard.getExpirationMonth());
               // creditCardExpirationMonth
               setCreditCardExpirationYear(pCard.getExpirationYear());
               // creditCardName
               setCreditCardName(pCard.getCardHolderName());
               // creditCardNumber
               setCreditCardNumber(pCard.getAccountNumber());
               // creditCardType
               setCreditCardType(pCard.getBrandName());
               // creditCardVerification
               setCreditCardVerification(pCard.getVerification());
            } // END if (pCard != null)
         } // if (!isEmpty(tmpStr))
         // customerReferenceNote
         setCustomerReferenceNote(
               getPropertyAsString(entity, "customerReferenceNote"));
         // deductible 
         setDeductible(getPropertyAsDouble(entity,"deductible"));
         // destinationCity
         setDestinationCity(getPropertyAsString(entity, "destinationCity"));
         // destinationCompanyName
         setDestinationCompanyName(
               getPropertyAsString(entity, "destinationCompanyName"));
         // destinationContactEmail
         setDestinationContactEmail(
               getPropertyAsString(entity, "destinationContactEmail"));
         // destinationContactName
         setDestinationContactName(
               getPropertyAsString(entity, "destinationContactName"));
         // destinationContactPhone
         setDestinationContactPhone(
               getPropertyAsString(entity, "destinationContactPhone"));
         // destinationResidential
         setDestinationResidential(
               getPropertyAsBoolean(entity,"destinationResidential"));
         // destinationState
         setDestinationState(getPropertyAsString(entity, "destinationState"));
         // destinationStreetAddress
         setDestinationStreetAddress(
               getPropertyAsString(entity, "destinationStreetAddress"));
         // destinationZip
         setDestinationZip(getPropertyAsString(entity, "destinationZip"));
         // distance
         setDistance(getPropertyAsFloat(entity, "distance"));
         // fuelSurcharge
         setFuelSurcharge(getPropertyAsFloat(entity, "fuelSurcharge"));
         // insuranceCharges
         tmpDouble = getPropertyAsFloat(entity, "insuranceCharges");
         if (tmpDouble == Long.MIN_VALUE) {
            recalculatePrice = true;
         } // END if (tmpDouble == Long.MIN_VALUE)
         setInsuranceCharges(tmpDouble);
         // lineItemCharges
         tmpDouble = getPropertyAsFloat(entity, "lineItemCharges");
         if (tmpDouble == Long.MIN_VALUE) {
            recalculatePrice = true;
         } // END if (tmpDouble == Double.MINVALUE)
         setLineItemCharges(tmpDouble);
         // fuelSurcharge
         tmpDouble = getPropertyAsDouble(entity, "fuelSurcharge");
         if (tmpDouble == Long.MIN_VALUE) {
            recalculatePrice = true;
         } // END if (tmpDouble == Long.MIN_VALUE)
         setFuelSurcharge(tmpDouble);
         // notes
         setNotes(getPropertyAsString(entity, "notes"));
         // numberOfFlights set after stairCarry
         // originCity
         setOriginCity(getPropertyAsString(entity, "originCity"));
         // originCompanyName
         setOriginCompanyName(getPropertyAsString(entity, "originCompanyName"));
         // originContactEmail
         setOriginContactEmail(getPropertyAsString(entity, "originContactEmail"));
         // originContactName
         setOriginContactName(getPropertyAsString(entity, "originContactName"));
         // originContactPhone
         setOriginContactPhone(getPropertyAsString(entity, "originContactPhone"));
         // originResidential
         setOriginResidential(
               getPropertyAsBoolean(entity,"originResidential"));
         // originState
         setOriginState(getPropertyAsString(entity, "originState"));
         // originStreetAddress
         setOriginStreetAddress(
               getPropertyAsString(entity, "originStreetAddress"));
         // originZip
         setOriginZip(getPropertyAsString(entity, "originZip"));
         // packagingRequired
         setPackagingRequired(getPropertyAsBoolean(entity,
               "packagingRequired"));
         // paymentMethod
         setPaymentMethod(getPropertyAsString(entity,"paymentMethod"));
         // priceModel
         tmpStr = getPropertyAsString(entity, "priceModelLabel");
         pmFactory = PriceModelFactory.getInstance();
         if (tmpStr != null) {
            pm = pmFactory.getModelByLabel(tmpStr);
         } else {
            pm = pmFactory.getDefaultModel();
         } // END if (tmpStr != null)
         setPriceModel(pm);
         // purchaseOrderNumber
         setPurchaseOrderNumber(
               getPropertyAsString(entity, "purchaseOrderNumber"));
         // quoteRequestId
         setQuoteRequestId(getPropertyAsString(entity,"quoteRequestId"));
         /*
          * There are dependencies with the referralSource and referralOther
          * properties, so referralSource is set first.
          */
         // referalSource
         setReferralSource(getPropertyAsString(entity,"referralSource"));
         // referralOther
         setReferralOther(getPropertyAsString(entity,"referralOther"));
         // requestId
         setRequestId(getPropertyAsString(entity, "requestId"));
         // serviceLevel
         setServiceLevel(getPropertyAsString(entity, "serviceLevel"));
         // stairCarry
         setStairCarry(getPropertyAsBoolean(entity, "stairCarry"));
         // numberOfFlights
         setNumberOfFlights(getPropertyAsInteger(entity, "numberOfFlights", 0));
         // type
         tmpStr=getPropertyAsString(entity,"type","Delivery");
         if (tmpStr.equalsIgnoreCase("Pick-Up")) {
            setType(Type.Pickup);
         } else {
            setType(Type.Delivery);
         } // END if (tmpStr.equalsIgnoreCase("Pickup")) 
         // unpackagingRequired
         setUnpackagingRequired(
               getPropertyAsBoolean(entity, "unpackagingRequired"));
         // valuation
         setValuation(getPropertyAsDouble(entity, "valuation", 0));
         // valuationDeclined
         setValuationDeclined(getPropertyAsBoolean(entity, "valuationDeclined"));      

         // lineItems
         woliDao = new WorkOrderLineItemDAOFactory().getDAO();
         pLineItems = woliDao.findByWorkOrderIdentifier(getIdentifier(), ctx);
         this.lineItems = new ArrayList<WorkOrderLineItem>();
         for (WorkOrderLineItem item : pLineItems) {
            this.lineItems.add(item);
         } // END for (PersistentQuoteLineItem item: pLineItems)
         
         // ********** 2015.06.30 - Addressing Pricing Model Algorithm Changes
         // ***** insurance
         boolean additionalValuationDeclined = false;
         double tmpDeductible = 0;
         double tmpInsuranceCost = 0;
         double tmpValue = 0;
         additionalValuationDeclined = isValuationDeclined();
         tmpInsuranceCost = getInsuranceCharges();
         if ((!additionalValuationDeclined) && (tmpInsuranceCost == 0)) {
            /*
             * If additional valuation is NOT declined, the customer MUST pick a
             * deductible amount AND declare a value.
             */
            tmpValue = getValuation();
            tmpDeductible = getDeductible();
            if (tmpDeductible == 0) {
               /* No deductible - insurance cost is $20/ $1000 declared value. */
               tmpInsuranceCost = 20 * (tmpValue / 1000);
            } else if (deductible == 500) {
               /* $500 deductible - insurance cost is $15/$1000 declared value. */
               tmpInsuranceCost = 15 * (tmpValue / 1000);
            } else {
               /* $750 deductible - insurance cost is $10/$1000 declared value. */
               setDeductible(750);
               tmpInsuranceCost = 10 * (tmpValue / 1000);
            } // END if (deductible==0)
            setInsuranceCharges(tmpInsuranceCost);
         } // END if ((!additionalValuationDeclined) && (tmpInsuranceCost == 0))
           // ***** lineItemCharges 0
         double tmpLineItemCharges = 0;
         double tmpAmount = 0;
         tmpInsuranceCost = getInsuranceCharges();
         tmpLineItemCharges = getLineItemCharges();
         tmpAmount = getAmount();
         if ((tmpLineItemCharges == 0) && (tmpAmount != 0)) {
            tmpLineItemCharges = tmpAmount - tmpInsuranceCost;
            setLineItemCharges(tmpLineItemCharges);
         } // END if ((tmpLineItemCharges == 0) && ())
           // ***** Re-calculate the price
         if ((recalculatePrice) || (getLineItemCharges() > getAmount())) {
            this.calculateRate();
         } // END if (recalculatePrice)
      } else {
         logger.warning("The specified entity object was null.");
      } // END if (entity != null)
       
      logger.exiting(_CLASS, "populateFromEntity(Entity,UserContext)");
   }
   /**
    * Populate the work order from a reference object.
    */
   public void populateFromObject(WorkOrder workOrder) {
      List<WorkOrderLineItem> itemsToRemove=null;
      Map<String,PersistentWorkOrderLineItemImpl> targetLineItems=null;
      PersistentWorkOrderLineItemImpl persistentItem=null;
      String sourceId="";
      String identifier="";
      
      if (workOrder != null) {
         super.populateFromObject(workOrder);
         setAmount(workOrder.getAmount());
         setAccessorialTotal(workOrder.getAccessorialTotal());
         setAdjustmentType(workOrder.getAdjustmentType());
         setAdjustmentAmount(workOrder.getAdjustmentAmount());
         setAssemblyRequired(workOrder.isAssemblyRequired());
         setBillingCity(workOrder.getBillingCity());     
         setBillingCompanyName(workOrder.getBillingCompanyName());
         setBillingContactEmail(workOrder.getBillingContactEmail());
         setBillingContactName(workOrder.getBillingContactName());
         setBillingContactPhone(workOrder.getBillingContactPhone());
         setBillingMethod(workOrder.getBillingMethod());
         setBillingState(workOrder.getBillingState());
         setBillingStreetAddress(workOrder.getBillingStreetAddress());
         setBillingZip(workOrder.getBillingZip());
         setBlanketWrapRequired(workOrder.isBlanketWrapRequired());
         setContactCompanyName(workOrder.getContactCompanyName());
         setContactEmail(workOrder.getContactEmail());
         setContactName(workOrder.getContactName());
         setContactPhone(workOrder.getContactPhone());
         setCratingRequired(workOrder.isCratingRequired());
         setCreditCardExpirationMonth(workOrder.getCreditCardExpirationMonth());
         setCreditCardExpirationYear(workOrder.getCreditCardExpirationYear());
         setCreditCardName(workOrder.getCreditCardName());
         setCreditCardNumber(workOrder.getCreditCardNumber());
         setCreditCardType(workOrder.getCreditCardType());
         setCreditCardVerification(workOrder.getCreditCardVerification());
         setCustomerReferenceNote(workOrder.getCustomerReferenceNote());
         setDeductible(workOrder.getDeductible());
         setDestinationCity(workOrder.getDestinationCity());
         setDestinationCompanyName(workOrder.getDestinationCompanyName());
         setDestinationContactEmail(workOrder.getDestinationContactEmail());
         setDestinationContactName(workOrder.getDestinationContactName());
         setDestinationContactPhone(workOrder.getDestinationContactPhone());
         setDestinationResidential(workOrder.isDestinationResidential());
         setDestinationState(workOrder.getDestinationState());
         setDestinationStreetAddress(workOrder.getDestinationStreetAddress());
         setDestinationZip(workOrder.getDestinationZip());
         setDistance(workOrder.getDistance());
         setInsuranceCharges(workOrder.getInsuranceCharges());
         setLineItemCharges(workOrder.getLineItemCharges());
         setNewJournalEntry(workOrder.getNewJournalEntry());
         setNotes(workOrder.getNotes());
         // numberOfFlights set AFTER stairCarry
         setOriginCity(workOrder.getOriginCity());
         setOriginCompanyName(workOrder.getOriginCompanyName());
         setOriginContactEmail(workOrder.getOriginContactEmail());
         setOriginContactName(workOrder.getOriginContactName());
         setOriginContactPhone(workOrder.getOriginContactPhone());
         setOriginResidential(workOrder.isOriginResidential());
         setOriginState(workOrder.getOriginState());
         setOriginStreetAddress(workOrder.getOriginStreetAddress());
         setOriginZip(workOrder.getOriginZip());
         setPackagingRequired(workOrder.isPackagingRequired());
         setPaymentMethod(workOrder.getPaymentMethod());
         setPriceModel(workOrder.getPriceModel());
         setPurchaseOrderNumber(workOrder.getPurchaseOrderNumber());
         /*
          * There are dependencies with the referralSource and referralOther
          * properties, so referralSource is set first.
          */
         setReferralSource(workOrder.getReferralSource());
         setReferralOther(workOrder.getReferralOther());
         setRequestId(workOrder.getRequestId());
         setServiceLevel(workOrder.getServiceLevel());
         setStatusState(workOrder.getStatusState());
         setStatusReason(workOrder.getStatusReason());
         setStairCarry(workOrder.isStairCarry());
         setType(workOrder.getType());
         setNumberOfFlights(workOrder.getNumberOfFlights());
         setUnpackagingRequired(workOrder.isUnpackagingRequired());
         setValuation(workOrder.getValuation());
         setValuationDeclined(workOrder.isValuationDeclined());
         setQuoteRequestId(workOrder.getQuoteRequestId());
         //*********************************************************************
         // Synchronize line items...
         //*** Build a list of identifiers for records in the source data.
         targetLineItems=new TreeMap<String,PersistentWorkOrderLineItemImpl>();
         for (WorkOrderLineItem item: getLineItems()) {
            persistentItem=(PersistentWorkOrderLineItemImpl) item;
            identifier=persistentItem.getIdentifier();
            if (!isEmpty(identifier)) {
               targetLineItems.put(persistentItem.getIdentifier(), 
                     persistentItem);
            } // END if (!isEmpty(identifier))             
         } // END for (WorkOrderLineItem item: getLineItems())
         // Now let's iterate over the list of associated line items.
         for (WorkOrderLineItem source: workOrder.getLineItems()) {
            sourceId="";
            if (source instanceof PersistentWorkOrderLineItemImpl) {
               sourceId=
                     ((PersistentWorkOrderLineItemImpl) source).getIdentifier();
            } else if (source instanceof WildObject) {
               sourceId=((WildObject) source).getIdentifier();
            } // END if (source instanceof PersistentWorkOrderLineItemImpl)
            if ((sourceId == null) || (sourceId.length() == 0)) {
               // The sourceId was null, so create a new object and populate it.
               persistentItem=new PersistentWorkOrderLineItemImpl();
               persistentItem.populateFromObject(source);
               persistentItem.setWorkOrderIdentifier(getIdentifier());
               addLineItem(persistentItem);
            } else {
               // Remove the line item with the matching identifier.
               persistentItem=targetLineItems.remove(sourceId);
               if (persistentItem == null) {
                  // The current quote DOES NOT have the specified line item.
                  // So we will add it.
                  persistentItem=new PersistentWorkOrderLineItemImpl();
                  persistentItem.populateFromObject(source);
                  persistentItem.setWorkOrderIdentifier(getIdentifier());
               } else {
                  persistentItem.updateFromObject(source);
               } // END if (persistentItem == null)               
            } // END if (sourceId == null)
         } // END for (WorkOrderLineItem lineItem: workOrder.getLineItems())
         // Let's iterate through the list of items found and remove those that
         // are no longer associated.
         if ((targetLineItems != null) && (targetLineItems.size() > 0)) {
            // Remove the items that no longer match.
            itemsToRemove=new ArrayList<WorkOrderLineItem>();
            for (String id: targetLineItems.keySet()) {
               itemsToRemove.add(targetLineItems.get(id));
            } // END for (String id: targetLineItems.keySet())
            for (WorkOrderLineItem item: itemsToRemove) {
               removeLineItem(item);
            } // END for (WorkOrderLineItem item: itemsToRemove)
            itemsToRemove = null;
            targetLineItems = null;
         } // END if ((targetLineItems != null) && (targetLineItems.size() > 0))
      } else {
         logger.severe("The specified workOrder object is null.");
      } // END if (entity != null) 
   }
   /**
    * Use the specified PersistentWorkOrder to populate the current object.
    */
   public void populateFromPersistentObject(PersistentWorkOrder workOrder) {
      logger.entering(_CLASS, 
         "populateFromPersistentObject(PersistentWorkOrder)",
            workOrder);
      if (workOrder != null) {
         populateFromObject(workOrder);
      } else {
         logger.warning("The work order parameter was null.");
      } // END if (workOrder != null)
      logger.exiting(_CLASS, 
         "populateFromPersistentObject(PersistentWorkOrder)");
   }
   
   public String toString() {
      logger.entering(_CLASS, "toString()");
      String result = null;
      StringBuilder sb = null;

      sb = new StringBuilder(2048);
      sb.append(_CLASS).append(" [");
      sb.append(toPropertyString()).append("]");
      result = sb.toString();

      logger.entering(_CLASS, "toString()", result);
      return result;
   }
   
   public String toPropertyString() {
      logger.entering(_CLASS, "toPropertyString()");
      NumberFormat cFmt = null;
      NumberFormat fmt=null;
      String result="";
      StringBuilder sb = null;

      sb = new StringBuilder(2048);
      cFmt = NumberFormat.getCurrencyInstance();
      fmt=NumberFormat.getInstance();
      fmt.setMaximumFractionDigits(2);
      fmt.setMinimumIntegerDigits(1);
      
      sb.append(super.toPropertyString());
      if (sb.length() > 0) {
         sb.append(", ");
      } // END if (sb.length() > 0)
      sb.append(", amount=").append(cFmt.format(getAmount()));
      sb.append(", accessorialTotal=").append(
            cFmt.format(getAccessorialTotal()));
      sb.append(", adjustmentType=").append(getAdjustmentType());
      sb.append(", adjustmentAmount=").append(
            cFmt.format(getAdjustmentAmount()));
      sb.append(", assemblyRequired=").append(isAssemblyRequired());
      sb.append(", billingCity=").append(getBillingCity());     
      sb.append(", billingCompanyName=").append(getBillingCompanyName());
      sb.append(", billingContaactEmail=").append(getBillingContactEmail());
      sb.append(", billingContactName=").append(getBillingContactName());
      sb.append(", billingContactPhone=").append(getBillingContactPhone());
      sb.append(", billingMethod=").append(getBillingMethod());
      sb.append(", billingState=").append(getBillingState());
      sb.append(", billingStreetAddress=").append(getBillingStreetAddress());
      sb.append(", billingZip=").append(getBillingZip());
      sb.append(", blanketWrapRequired=").append(isBlanketWrapRequired());
      sb.append(", contactCompanyName=").append(getContactCompanyName());
      sb.append(", contactEmail=").append(getContactEmail());
      sb.append(", contactCompanyName=").append(getContactName());
      sb.append(", contactPhone=").append(getContactPhone());
      sb.append(", cratingRequired=").append(isCratingRequired());
      sb.append(", deductible=").append(cFmt.format(getDeductible()));
      sb.append(", destinationCity=").append(getDestinationCity());
      sb.append(", destinationCompanyName=").append(getDestinationCompanyName());
      sb.append(", destinationContactEmail=").append(
            getDestinationContactEmail());
      sb.append(", destinationContactName=").append(
            getDestinationContactName());
      sb.append(", destinationContactPhone=").append(
            getDestinationContactPhone());
      sb.append(", destinationResidential=").append(isDestinationResidential());
      sb.append(", destinationState=").append(getDestinationState());
      sb.append(", destinationStreetAddress=").append(
            getDestinationStreetAddress());
      sb.append(", destinationZip=").append(getDestinationZip());
      sb.append(", distance=").append(fmt.format(getDistance()));
      sb.append(", insuranceCharges=").append(
            cFmt.format(getInsuranceCharges()));
      sb.append(", lineItemCharges=").append(
            cFmt.format(getLineItemCharges()));
      sb.append(", originCity=").append(getOriginCity());
      sb.append(", originCompanyName=").append(getOriginCompanyName());
      sb.append(", originContactEmail=").append(getOriginContactEmail());
      sb.append(", originContactName=").append(getOriginContactName());
      sb.append(", originContactPhone=").append(getOriginContactPhone());
      sb.append(", originResidential=").append(isOriginResidential());
      sb.append(", originState=").append(getOriginState());
      sb.append(", originStreetAddress=").append(getOriginStreetAddress());
      sb.append(", originZip=").append(getOriginZip());
      sb.append(", packagingRequired=").append(isPackagingRequired());
      sb.append(", paymentMethod=").append(getPaymentMethod());
      sb.append(", priceModel=").append(getPriceModel());
      sb.append(", purchaseOrderNumber=").append(getPurchaseOrderNumber());
      sb.append(", referralSource=").append(getReferralSource());
      sb.append(", referralOther=").append(getReferralOther());
      sb.append(", serviceLevel=").append(getServiceLevel());
      sb.append(", stairCarry=").append(isStairCarry());
      sb.append(", type=").append(getType());
      sb.append(", numberOfFlights=").append(getNumberOfFlights());
      sb.append(", unpackagingReuqired=").append(isUnpackagingRequired());
      sb.append(", numberOfFlights=").append(cFmt.format(getValuation()));
      sb.append(", valuationDeclined=").append(isValuationDeclined());
      sb.append(", quoteRequestId=").append(getQuoteRequestId());
      for (WorkOrderLineItem item: getLineItems()) {
         sb.append(", ").append(item.toString());
      } // END for (WorkOrderLineItem item: getLineItems())
      
      logger.exiting(_CLASS, "toPropertyString()",result);
      return result;
   }
   //********** accessor methods
   //***** billingCity
   public String getBillingCity() {
      logger.entering(_CLASS, "getBillingCity()");
      logger.exiting(_CLASS, "getBillingCity()",this.billingCity);
      return this.billingCity;
   }
   public void setBillingCity(String city) {
      logger.entering(_CLASS,"setBillingCity(String)",city);
      this.billingCity=defaultValue(city);
      logger.exiting(_CLASS,"setBillingCity(String)");
   }
   
   //***** billingCompanyName
   public String getBillingCompanyName() {
      logger.entering(_CLASS, "getBillingCompanyName()");
      logger.exiting(_CLASS, "getBillingCompanyName()",this.billingCompanyName);
      return this.billingCompanyName;
   }
   public void setBillingCompanyName(String companyName) {
      logger.entering(_CLASS,"setBillingCompanyName(String)",companyName);
      this.billingCompanyName=defaultValue(companyName);
      logger.exiting(_CLASS,"setBillingCompanyName(String)");
   }
   
   //***** billingContactEmail
   public String getBillingContactEmail() {
      logger.entering(_CLASS, "getBillingContactEmail()");
      logger.exiting(_CLASS, "getBillingContactEmail()",
            this.billingContactEmail);
      return this.billingContactEmail;
   }
   public void setBillingContactEmail(String contactEmail) {
      logger.entering(_CLASS,"setBillingContactEmail(String)",contactEmail);
      this.billingContactEmail=defaultValue(contactEmail);
      logger.exiting(_CLASS,"setBillingContactEmail(String)");
   }
   
   //***** billingContactName
   public String getBillingContactName() {
      logger.entering(_CLASS, "getBillingContactName()");
      logger.exiting(_CLASS, "getBillingContactName()",
            this.billingContactName);
      return this.billingContactName;
   }
   public void setBillingContactName(String contactName) {
      logger.entering(_CLASS,"setBillingContactPhone(String)",contactName);
      this.billingContactName=defaultValue(contactName);
      logger.exiting(_CLASS,"setBillingContactPhone(String)");
   }
   
   //***** billingContactPhone
   public String getBillingContactPhone() {
      logger.entering(_CLASS, "getBillingContactPhone()");
      logger.exiting(_CLASS, "getBillingContactPhone()",
            this.billingContactPhone);
      return this.billingContactPhone;
   }
   public void setBillingContactPhone(String contactPhone) {
      logger.entering(_CLASS,"setBillingContactPhone(String)",contactPhone);
      this.billingContactPhone=defaultValue(contactPhone);
      logger.exiting(_CLASS,"setBillingContactPhone(String)");
   }
   
   //***** billingMethod
   @Override
   public String getBillingMethod() {
      logger.entering(_CLASS, "getBillingMethod()");
      logger.exiting(_CLASS, "getBillingMethod()",
            this.billingMethod);
      return this.billingMethod;
   }
   @Override
   public void setBillingMethod(String method) {
      logger.entering(_CLASS,"setBillingMethod(String)",method);
      this.billingMethod=defaultValue(method);
      logger.exiting(_CLASS,"setBillingMethod(String)");
   }
   
   //***** billingState
   @Override
   public String getBillingState() {
      logger.entering(_CLASS, "getBillingState()");
      logger.exiting(_CLASS, "getBillingState()",
            this.billingState);
      return this.billingState;
   }
   @Override
   public void setBillingState(String state) {
      logger.entering(_CLASS,"setBillingState(String)",state);
      this.billingState=defaultValue(state);
      logger.exiting(_CLASS,"setBillingState(String)");
   }
   
   //***** billingStreetAddress
   @Override
   public String getBillingStreetAddress() {
      logger.entering(_CLASS, "getBillingStreetAddress()");
      logger.exiting(_CLASS, "getBillingStreetAddress()",
            this.billingStreetAddress);
      return this.billingStreetAddress;
   }
   @Override
   public void setBillingStreetAddress(String address) {
      logger.entering(_CLASS,"setBillingStreetAddress(String)",address);
      this.billingStreetAddress=defaultValue(address);
      logger.exiting(_CLASS,"setBillingStreetAddress(String)");
   }
   
   //***** billingZip
   @Override
   public String getBillingZip() {
      logger.entering(_CLASS, "getBillingZip()");
      logger.exiting(_CLASS, "getBillingZip()",
            this.billingZip);
      return this.billingZip;
   }
   @Override
   public void setBillingZip(String zipCode) {
      logger.entering(_CLASS,"setBillingZip(String)",zipCode);
      this.billingZip=defaultValue(zipCode);
      logger.exiting(_CLASS,"setBillingZip(String)");
   }
   // ***** accessorials
   /*
    * (non-Javadoc)
    * 
    * @see com.wildstartech.servicedesk.QuickQuote#getAccessorialList()
    */
   @Override
   public List<AccessorialCharge> getAccessorialCharges() {
      logger.entering(_CLASS, "getAccessorialList())");
      List<AccessorialCharge> charges = null;

      charges = new ArrayList<AccessorialCharge>();
      for (AccessorialCharge form : this.accessorials) {
         charges.add(form);
      } // END for (AccessorialChargeForm form: this.accessorials)

      logger.exiting(_CLASS, "getAccessorialList())", charges);
      return charges;
   }

   @Override
   public AccessorialCharge addAccessorialCharge(AccessorialCharge charge) {
      logger.entering(_CLASS, "addAccessorialCharge(AccessorialCharge)",
            charge);
      PersistentAccessorialChargeImpl pCharge = null;
      if (charge != null) {
         // Lets make sure the accessorial charge list is NOT null.
         if (this.accessorials == null) {
            this.accessorials = new ArrayList<AccessorialCharge>();
         } // END if (this.accessorials == null)
         pCharge = new PersistentAccessorialChargeImpl();
         pCharge.populateFromObject(charge);
         this.accessorials.add(pCharge);
      } else {
         logger.finest("The AccessorialCharge parameter was null.");
      } // END if (charge != null)
      logger.exiting(_CLASS, "addAccessorialCharge(AccessorialCharge)",
            pCharge);
      return pCharge;
   }

   @Override
   public AccessorialCharge getAccessorialCharge(int pos) {
      AccessorialCharge charge = null;
      logger.entering(_CLASS, "getAccessorialCharge(int)", pos);
      if ((pos >= 0) && (this.accessorials != null)
            && (this.accessorials.size() > 0)
            && (pos < this.accessorials.size())) {
         charge = this.accessorials.get(pos);
      } // END if (pos < 0) && (this.accessorials != null) && ...
      logger.exiting(_CLASS, "getAccessorialCharge(int)", charge);
      return charge;
   }

   @Override
   public AccessorialCharge removeAccessorialCharge(AccessorialCharge charge) {
      logger.entering(_CLASS, "removeAccessorialCharge(AccessorialCharge)",
            charge);
      AccessorialCharge removedCharge = null;
      if ((charge != null) && (this.accessorials != null)) {

      } // END if (charge != null)
      logger.exiting(_CLASS, "removeAccessorialCharge(AccessorialCharge)",
            removedCharge);
      return removedCharge;
   }

   public double getAccessorialTotal() {
      logger.entering(_CLASS, "getAccessorialTotal()");
      logger.exiting(_CLASS, "getAccessorialTotal()", this.accessorialTotal);
      return this.accessorialTotal;
   }

   public void setAccessorialTotal(double total) {
      logger.entering(_CLASS, "setAccessorialTotal(double)", total);
      if (total < 0) {
         this.accessorialTotal = 0;
      } else {
         this.accessorialTotal = total;
      } // END if (total < 0)
      logger.exiting(_CLASS, "setAccessorialTotal(double)");
   }

   // ***** adjustmentAmount
   public double getAdjustmentAmount() {
      logger.entering(_CLASS, "getAdjustmentAmount()");
      logger.exiting(_CLASS, "getAdjustmentAmount()", this.adjustmentAmount);
      return this.adjustmentAmount;
   }

   public void setAdjustmentAmount(double amount) {
      logger.entering(_CLASS, "setAdjustmentAmount(double)", amount);
      this.adjustmentAmount = amount;
      logger.entering(_CLASS, "setAdjustmentAmount(double)");
   }

   // ***** adjustmentType
   public AdjustmentType getAdjustmentType() {
      logger.entering(_CLASS, "getAdjustmentType()");
      logger.exiting(_CLASS, "getAdjustmentType()", this.adjustmentType);
      return this.adjustmentType;
   }

   public void setAdjustmentType(AdjustmentType type) {
      logger.entering(_CLASS, "setAdjustmentType(AdjustmentType)", type);
      this.adjustmentType = type;
      logger.exiting(_CLASS, "setAdjustmentType(AdjustmentType)");
   }

   public boolean isAdjustmentTypeCurrency() {
      logger.entering(_CLASS, "isAdjustmentTypeCurrency()");
      boolean result = false;
      if (this.adjustmentType == AdjustmentType.FixedAmount) {
         result = true;
      } // END if (this.adjustmentType == AdjustmentType.FixedAmount)
      logger.exiting(_CLASS, "isAdjustmentTypeCurrencty()", result);
      return result;
   }
   
   // ***** amount
   @Override
   public double getAmount() {
      logger.entering(_CLASS, "getAmount()");
      logger.exiting(_CLASS, "getAmount()", this.amount);
      return this.amount;
   }

   @Override
   public void setAmount(double amount) {
      logger.entering(_CLASS, "setAmount(double)", amount);
      this.amount = amount;
      logger.exiting(_CLASS, "setAmount(double)");
   }

   // ***** assemblyRequired
   public boolean isAssemblyRequired() {
      logger.entering(_CLASS, "isAssemblyRequired");
      logger.exiting(_CLASS, "isAssemblyRequired", this.assemblyRequired);
      return this.assemblyRequired;
   }

   @Override
   public void setAssemblyRequired(boolean assemblyRequired) {
      logger.entering(_CLASS, "setAssemblyRequired(boolean)", assemblyRequired);
      this.assemblyRequired = assemblyRequired;
      logger.exiting(_CLASS, "setAssemblyRequired(boolean)");
   }

   // ***** blanketWrapRequired
   @Override
   public boolean isBlanketWrapRequired() {
      logger.entering(_CLASS, "isBlanketWrapRequired");
      logger.exiting(_CLASS, "isBlanketWrapRequired", this.blanketWrapRequired);
      return this.blanketWrapRequired;
   }

   @Override
   public void setBlanketWrapRequired(boolean blanketwrap) {
      logger.entering(_CLASS, "setBlanketWrapRequired(boolean)", blanketwrap);
      this.blanketWrapRequired = blanketwrap;
      logger.exiting(_CLASS, "setBlanketWrapRequired(boolean)");
   }

   // ***** contactCompanyName
   @Override
   public String getContactCompanyName() {
      logger.entering(_CLASS, "getContactCompanyName()");
      logger.entering(_CLASS, "getContactCompanyName()",
            this.contactCompanyName);
      return this.contactCompanyName;
   }

   @Override
   public void setContactCompanyName(String companyName) {
      logger.entering(_CLASS, "setContactCompanyName(String)", companyName);
      this.contactCompanyName = defaultValue(companyName);
      logger.exiting(_CLASS, "setContactCompanyName(String)");
   }

   // ***** contactEmail
   /*
    * (non-Javadoc)
    * 
    * @see com.wildstartech.servicedesk.QuickQuote#getContactEmail()
    */
   @Override
   public String getContactEmail() {
      logger.entering(_CLASS, "getContactEmail()");
      logger.exiting(_CLASS, "getContactEmail()", this.contactEmail);
      return contactEmail;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * com.wildstartech.servicedesk.QuickQuote#setContactEmail(java.lang.String)
    */
   @Override
   public void setContactEmail(String contactEmail) {
      logger.entering(_CLASS, "setContactEmail(String)", contactEmail);
      this.contactEmail = defaultValue(contactEmail);
      logger.exiting(_CLASS, "setContactEmail(String)");
   }

   // ***** contactName
   /*
    * (non-Javadoc)
    * 
    * @see com.wildstartech.servicedesk.QuickQuote#getContactName()
    */
   @Override
   public String getContactName() {
      logger.entering(_CLASS, "getContactName()");
      logger.entering(_CLASS, "getContactName()", this.contactName);
      return contactName;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * com.wildstartech.servicedesk.QuickQuote#setContactName(java.lang.String)
    */
   @Override
   public void setContactName(String contactName) {
      logger.entering(_CLASS, "setContactName(String)", contactName);
      this.contactName = defaultValue(contactName);
      logger.exiting(_CLASS, "setContactName(String)");
   }

   // ***** contactPhone
   /*
    * (non-Javadoc)
    * 
    * @see com.wildstartech.servicedesk.QuickQuote#getContactPhone()
    */
   @Override
   public String getContactPhone() {
      logger.entering(_CLASS, "getContactPhone()");
      logger.exiting(_CLASS, "getContactPhone()", this.contactPhone);
      return this.contactPhone;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * com.wildstartech.servicedesk.QuickQuote#setContactPhone(java.lang.String)
    */
   @Override
   public void setContactPhone(String contactPhone) {
      logger.entering(_CLASS, "setContactPhone(String)", contactPhone);
      this.contactPhone = defaultValue(contactPhone);
      logger.exiting(_CLASS, "setContactPhone(String)");
   }

   // ***** contactPhoneHidden
   public String getContactPhoneHidden() {
      logger.entering(_CLASS, "getContactPhoneHidden()");
      logger.exiting(_CLASS, "getContactPhoneHidden()",
            this.contactPhoneHidden);
      return this.contactPhoneHidden;
   }

   /**
   * 
   */
   public void setContactPhoneHidden(String contactPhone) {
      logger.entering(_CLASS, "setContactPhoneHidden(String)", contactPhone);
      this.contactPhoneHidden = defaultValue(contactPhone);
      logger.exiting(_CLASS, "setContactPhoneHidden(String)");
   }

   // ***** cratingRequired
   @Override
   public boolean isCratingRequired() {
      logger.entering(_CLASS, "isCratingRequired()");
      logger.exiting(_CLASS, "isCratingRequired()", this.cratingRequired);
      return this.cratingRequired;
   }

   @Override
   public void setCratingRequired(boolean crating) {
      logger.entering(_CLASS, "setCratingRequired(boolean)", crating);
      this.cratingRequired = crating;
      logger.exiting(_CLASS, "setCratingRequired(boolean)");
   }
   
   //***** creditCardExpirationMonth
   @Override
   public int getCreditCardExpirationMonth() {
      logger.entering(_CLASS, "getCreditCardExpirationMonth()");
      logger.exiting(_CLASS, "getCreditCardExpirationMonth()",
            this.creditCardExpirationMonth);
      return this.creditCardExpirationMonth;
   }
   public void setCreditCardExpirationMonth(int expirationMonth) {
      logger.entering(_CLASS, "setCreditCardExpirationMonth(int)",
              expirationMonth);
      this.creditCardExpirationMonth=expirationMonth;
      logger.exiting(_CLASS, "setCreditCardExpirationMonth(int)");
   }
   //***** creditCardExpirationYear
   @Override
   public int getCreditCardExpirationYear() {
      logger.entering(_CLASS, "getCreditCardExpirationYear()");
      logger.exiting(_CLASS, "getCreditCardExpirationYear()",
            this.creditCardExpirationYear);
      return this.creditCardExpirationYear;
   }
   public void setCreditCardExpirationYear(int expirationYear) {
      logger.entering(_CLASS, "setCreditCardExpirationYear(int)",
              expirationYear);
      if (
              (expirationYear >= PaymentCard.MIN_YEAR) &&
              (expirationYear <= PaymentCard.MAX_YEAR) 
         ) {
          this.creditCardExpirationYear=expirationYear;
      } else {
          this.creditCardExpirationYear=0;
      } // END if ((expirationYear >= PaymentCard.MIN_YEAR) ...
      logger.exiting(_CLASS, "setCreditCardExpirationYear(int)");
   }
   //***** creditCardIdentifier
   protected String getCreditCardIdentifier() {
      logger.entering(_CLASS, "getCreditCardIdentifier()");
      logger.exiting(_CLASS, "getCreditCardIdentifier()",
            this.creditCardIdentifier);
      return this.creditCardIdentifier;
   }
   protected void setCreditCardIdentifier(String identifier) {
      logger.entering(_CLASS, "setCreditCardIdentifier(String)",identifier);
      this.creditCardIdentifier=defaultValue(identifier);
      logger.exiting(_CLASS, "setCreditCardIdentifier(String)");
   }
   
   //***** creditCardName
   public String getCreditCardName() {
      logger.entering(_CLASS, "getCreditCardName()");
      logger.exiting(_CLASS, "getCreditCardName()",
            this.creditCardName);
      return this.creditCardName;
   }
   public void setCreditCardName(String cardName) {
      logger.entering(_CLASS, "setCreditCardName(String)",cardName);
      this.creditCardName=defaultValue(cardName);
      logger.exiting(_CLASS, "setCreditCardName(String)");
   }
   
   //***** creditCardNumber
   public String getCreditCardNumber() {
      logger.entering(_CLASS, "getCreditCardNumber()");
      logger.exiting(_CLASS, "getCreditCardNumber()",
            this.creditCardNumber);
      return this.creditCardNumber;
   }
   public void setCreditCardNumber(String number) {
      logger.entering(_CLASS, "setCreditCardNumber(String)",number);
      this.creditCardNumber=defaultValue(number);
      logger.exiting(_CLASS, "setCreditCardNumber(String)");
   }
   
   //***** creditCardType
   public String getCreditCardType() {
      logger.entering(_CLASS, "getCreditCardType()");
      logger.exiting(_CLASS, "getCreditCardType()",
            this.creditCardType);
      return this.creditCardType;
   }
   public void setCreditCardType(String type) {
      logger.entering(_CLASS, "setCreditCardType(String)",type);
      this.creditCardType=defaultValue(type);
      logger.exiting(_CLASS, "setCreditCardType(String)");
   }
   
   //***** creditCardVerification
   public String getCreditCardVerification() {
      logger.entering(_CLASS, "getCreditCardVerification()");
      logger.exiting(_CLASS, "getCreditCardVerification()",
            this.creditCardVerification);
      return this.creditCardVerification;
   }
   public void setCreditCardVerification(String verification) {
      logger.entering(_CLASS, "setCreditCardVerification(String)",verification);
      this.creditCardVerification=verification;
      logger.entering(_CLASS, "setCreditCardVerification(String)");
   }

   // ***** customerReferenceNote
   @Override
   public String getCustomerReferenceNote() {
      logger.entering(_CLASS, "getCustomerReferenceNote()");
      logger.exiting(_CLASS, "getCustomerReferenceNote()",
            this.customerReferenceNote);
      return this.customerReferenceNote;
   }

   @Override
   public void setCustomerReferenceNote(String note) {
      logger.entering(_CLASS, "setCustomerReferenceNote(String)", note);
      this.customerReferenceNote = defaultValue(note);
      logger.exiting(_CLASS, "setCustomerReferenceNote(String)");
   }

   // ***** deductible
   @Override
   public double getDeductible() {
      logger.entering(_CLASS, "getDeductible()");
      logger.exiting(_CLASS, "getDeductible()", this.deductible);
      return this.deductible;
   }
   
   @Override
   public void setDeductible(double amount) {
      logger.entering(_CLASS, "setDeductible(double)", amount);
      if (amount < 0) {
         this.deductible = 0;
      } else {
         this.deductible = amount;
      } // END if (amount < 0)
      logger.exiting(_CLASS, "setDeductible(double)");
   }

   // ***** destinationCity
   @Override
   public String getDestinationCity() {
      logger.entering(_CLASS, "getDestinationCity()");
      logger.exiting(_CLASS, "getDestinationCity()", this.destinationCity);
      return this.destinationCity;
   }

   @Override
   public void setDestinationCity(String destinationCity) {
      logger.entering(_CLASS, "setDestinationCity(String)", destinationCity);
      this.destinationCity = defaultValue(destinationCity);
      logger.exiting(_CLASS, "setDestinationCity(String)");
   }
   // ***** destinationCompanyName
   @Override
   public String getDestinationCompanyName() {
      logger.entering(_CLASS, "getDestinationCompanyName()");
      logger.exiting(_CLASS, "getDestinationCompanyName()", 
            this.destinationCompanyName);
      return this.destinationCompanyName;
   }

   @Override
   public void setDestinationCompanyName(String destinationCompanyName) {
      logger.entering(_CLASS, "setDestinationCompanyName(String)", 
            destinationCompanyName);
      this.destinationCompanyName = defaultValue(destinationCompanyName);
      logger.exiting(_CLASS, "setDestinationCompanyName(String)");
   }  
   // ***** destinationContactEmail
   @Override
   public String getDestinationContactEmail() {
      logger.entering(_CLASS, "getDestinationContactEmail()");
      logger.exiting(_CLASS, "getDestinationContactEmail()", 
            this.destinationContactEmail);
      return this.destinationContactEmail;
   }

   @Override
   public void setDestinationContactEmail(String destinationContactEmail) {
      logger.entering(_CLASS, "setDestinationContactEmail(String)", 
            destinationContactEmail);
      this.destinationContactEmail = defaultValue(destinationContactEmail);
      logger.exiting(_CLASS, "setDestinationContactEmail(String)");
   }
   // ***** destinationContactName
   @Override
   public String getDestinationContactName() {
      logger.entering(_CLASS, "getDestinationContactName()");
      logger.exiting(_CLASS, "getDestinationContactName()", 
            this.destinationContactName);
      return this.destinationContactName;
   }

   @Override
   public void setDestinationContactName(String destinationContactName) {
      logger.entering(_CLASS, "setDestinationContactName(String)", 
            destinationContactName);
      this.destinationContactName = defaultValue(destinationContactName);
      logger.exiting(_CLASS, "setDestinationContactName(String)");
   }
   // ***** destinationContactPhone
   @Override
   public String getDestinationContactPhone() {
      logger.entering(_CLASS, "getDestinationContactPhone()");
      logger.exiting(_CLASS, "getDestinationContactPhone()", 
            this.destinationContactPhone);
      return this.destinationContactPhone;
   }

   @Override
   public void setDestinationContactPhone(String destinationContactPhone) {
      logger.entering(_CLASS, "setDestinationContactPhone(String)", 
            destinationContactPhone);
      this.destinationContactPhone = defaultValue(destinationContactPhone);
      logger.exiting(_CLASS, "setDestinationContactPhone(String)");
   }
   
   //***** destinationResidential
   @Override
   public boolean isDestinationResidential() {
      logger.entering(_CLASS, "isDestinationResidential()");
      logger.exiting(_CLASS, "isDestinationResidential()", 
            this.destinationResidential);
      return this.destinationResidential;
   }
   @Override
   public void setDestinationResidential(boolean residential) {
      logger.entering(_CLASS, "setDestinationResidential(boolean residential)",
            residential);
      this.destinationResidential=residential;
      logger.exiting(_CLASS, "setDestinationResidential(boolean residential)");
   }

   // ***** destinationState
   @Override
   public String getDestinationState() {
      logger.entering(_CLASS, "getDestinationState()");
      logger.exiting(_CLASS, "getDestinationState()", this.destinationState);
      return this.destinationState;
   }

   @Override
   public void setDestinationState(String destinationState) {
      logger.entering(_CLASS, "setDestinatioState(String)", destinationState);
      this.destinationState = defaultValue(destinationState);
      logger.exiting(_CLASS, "setDestinatioState(String)");
   }

   //***** destinationStreetAddress
   @Override
   public String getDestinationStreetAddress() {
      logger.entering(_CLASS, "getDestinationStreetAddress()");
      logger.exiting(_CLASS, "getDestinationStreetAddress()", 
            this.destinationStreetAddress);
      return this.destinationStreetAddress;
   }
   @Override
   public void setDestinationStreetAddress(String destinationStreetAddress) {
      logger.entering(_CLASS, "setDestinationStreetAddress(String)", 
            destinationStreetAddress);
      this.destinationStreetAddress = defaultValue(destinationStreetAddress);
      logger.exiting(_CLASS, "setDestinationStreetAddress(String)");
   }
   // ***** destinationZip
   /*
    * (non-Javadoc)
    * 
    * @see com.wildstartech.servicedesk.QuickQuote#getDestinationZip()
    */
   @Override
   public String getDestinationZip() {
      logger.entering(_CLASS, "getDestinationZip()");
      logger.exiting(_CLASS, "getDestinationZip()", this.destinationZip);
      return destinationZip;
   }

   /**
    * Stores the zip code of the destination for the delivery estimate.
    */
   @Override
   public void setDestinationZip(String destinationZip) {
      logger.entering(_CLASS, "setDestinationZip(String)", destinationZip);
      City city = null;
      PostalCodeFactory factory = null;

      if ((destinationZip != null) && (destinationZip.length() > 0)
            && ((this.destinationZip == null)
                  || (destinationZip.compareTo(this.destinationZip) != 0))) {
         /*
          * The new zip code is not null, has a non-zero length, and is
          * different from the current destinationZip, so let's do the lookup.
          */
         factory = PostalCodeFactory.getInstance();
         city = factory.getCity(destinationZip);
         if (city != null) {
            setDestinationCity(city.getName());
            setDestinationState(city.getStateAbbreviation());
            setDistance(0);
         } // END if (city != null)
      } // END if ((destinationZip != null) && (destinationZip.length() >0))
      this.destinationZip = defaultValue(destinationZip);
      logger.exiting(_CLASS, "setDestinationZip(String)");
   }

   // ***** distance
   public double getDistance() {
      logger.entering(_CLASS, "getDistance()");
      DistanceMeasurement measurement = null;
      DistanceServiceProviderFactory factory = null;
      DistanceServiceProvider service = null;
      String destination = null;
      String origin = null;

      destination = getDestinationZip();
      origin = getOriginZip();
      if ((this.distance == 0) && ((origin != null) && (origin.length() > 0))
            && ((destination != null) && (destination.length() > 0))) {
         factory = DistanceServiceProviderFactory.getInstance();
         service = factory.getService();
         measurement = service.calculateDistance(origin, destination);
         this.distance = measurement.getMeasurementAs(UNITS.IMPERIAL);
      } // END if ((this.distance == 0) ...
      logger.exiting(_CLASS, "getDistance()", this.distance);
      return this.distance;
   }

   public void setDistance(double distance) {
      logger.entering(_CLASS, "setDistance(float)", distance);
      if (distance < 0) {
         this.distance = 0;
      } else {
         this.distance = distance;
      } // END if (distance < 0)
      logger.exiting(_CLASS, "setDistance()");
   }

   // ***** fuelSurcharge
   public double getFuelSurcharge() {
      logger.entering(_CLASS, "getFuelSurcharge()");
      logger.exiting(_CLASS, "getFuelSurcharge()", this.fuelSurcharge);
      return this.fuelSurcharge;
   }

   public void setFuelSurcharge(double fuelSurcharge) {
      logger.entering(_CLASS, "getFuelSurcharge()", fuelSurcharge);
      if (fuelSurcharge < 0) {
         this.fuelSurcharge = 0;
      } else {
         this.fuelSurcharge = fuelSurcharge;
      } // END if (fuelSurcharge < 0)
      logger.exiting(_CLASS, "getFuelSurcharge()");
   }

   // ***** initialized
   public boolean isInitialized() {
      logger.entering(_CLASS, "isInitialized()");
      logger.exiting(_CLASS, "isInitialized()", this.initialized);
      return this.initialized;
   }

   // ***** insuranceCost
   /**
    * Return an estimated insurance charge.
    * 
    * @return
    */
   public double getInsuranceCharges() {
      logger.entering(_CLASS, "getInsuranceCharges()");
      logger.exiting(_CLASS, "getInsuranceCharges()", this.insuranceCharges);
      return this.insuranceCharges;
   }

   /**
    * Store the insurance charges for the item.
    */
   public void setInsuranceCharges(double charges) {
      logger.entering(_CLASS, "setInsuranceCharges(double)", charges);
      if (charges < 0) {
         // Insurance charges cannot be less than zero.
         this.insuranceCharges = 0;
      } else {
         /*
          * The specified insurance charges are greater than or equal to zero,
          * so set the insuranceCharges field equal to the specified value.
          */
         this.insuranceCharges = charges;
      } // END if (charges < 0)
      logger.exiting(_CLASS, "setInsuranceCharges(double)");
   }
   
   // ***** journalEntry
   @Override
   public JournalEntry getNewJournalEntry() {
      logger.entering(_CLASS, "getNewJournalEntry()");
      if (this.newJournalEntry == null) {
         this.newJournalEntry=new PersistentJournalEntryImpl();
      } // END if (this.newJournalEntry == null)
      logger.exiting(_CLASS, "getNewJournalEntry()",this.newJournalEntry);
      return this.newJournalEntry;
   }
   
   @Override
   public void setNewJournalEntry(JournalEntry entry) {
      logger.entering(_CLASS, "setNewJournalEntry(JournalEntry)",entry);
      if (entry != null) {
         if (entry instanceof PersistentJournalEntryImpl) {
            this.newJournalEntry=(PersistentJournalEntryImpl) entry;
         } else {
            this.newJournalEntry=new PersistentJournalEntryImpl();
            this.newJournalEntry.populateFromObject(entry);
         } // END if (entry instanceof PersistentJournalEntryImpl)
      } else {
         this.newJournalEntry=new PersistentJournalEntryImpl();
      } // END if (entry != null)
      logger.exiting(_CLASS, "setNewJournalEntry(JournalEntry)");      
   }
   
   public List<PersistentJournalEntry> getJournalEntries(UserContext ctx) {
      logger.entering(_CLASS, "getJournalEntries()");
      JournalDAOImpl dao=null;
      List<PersistentJournalEntry> journalEntries=null;
      String identifier=null;
      StringBuilder msg=null;
      
      if (ctx != null) {
         identifier=getIdentifier();
         if (!isEmpty(identifier)) {
            dao=new JournalDAOImpl();
            journalEntries=dao.findEntries(
                  PersistentWorkOrderImpl._KIND, 
                  identifier, 
                  ctx);
         } else {
            msg=new StringBuilder(80);
            msg.append("Journal entries are not available until after the ");
            msg.append("Work Order has been saved the firs time.");
            logger.warning(msg.toString());
         } // END if (!isEmpty(identifier))          
      } else {
         logger.warning("The UserContext parameter was null.");
      } // END if (ctx != null)
      
      // We must make sure that we DO NOT return an empty list.
      if (journalEntries == null) {
         journalEntries=new ArrayList<PersistentJournalEntry>();
      } // END if (journalEntries == null)
      
      logger.exiting(_CLASS, "getJournalEntries()", journalEntries);
      return journalEntries;
   }

   // ***** lineItems
   /*
    * (non-Javadoc)
    * 
    */
   public List<WorkOrderLineItem> getLineItemList() {
      logger.entering(_CLASS, "getLineItemList()");
      List<WorkOrderLineItem> immutableList = null;
      WorkOrderLineItem item = null;

      if ((this.lineItems == null) || (this.lineItems.size() == 0)) {
         item = new PersistentWorkOrderLineItemImpl();
         item.setQuantity(1);
         this.lineItems.add(item);
      } // END if ((this.lineItems== null) || (this.lineItems.size() == 0))
      immutableList = Collections.unmodifiableList(this.lineItems);

      logger.exiting(_CLASS, "getLineItemList()", immutableList);
      return immutableList;
   }

   @Override
   public List<WorkOrderLineItem> getLineItems() {
      logger.entering(_CLASS, "getLineItems()");
      List<WorkOrderLineItem> items = null;

      items = new ArrayList<WorkOrderLineItem>();
      if ((this.lineItems == null) || (this.lineItems.size() == 0)) {
         items.add(new PersistentWorkOrderLineItemImpl());
      } else {
         for (WorkOrderLineItem form : this.lineItems) {
            items.add(form);
         } // END for (WorkOrderLineItemForm itemForm: this.lineItems)
      } // END if ((this.lineItems == null) || (this.lineItems.size() == 0))

      logger.exiting(_CLASS, "getLineItems()", items);
      return items;
   }

   @Override
   public WorkOrderLineItem addLineItem(WorkOrderLineItem item) {
      logger.entering(_CLASS, "addLineItem(WorkOrderLineItem)", item);
      boolean added = false;
      int size = 0;
      PersistentWorkOrderLineItemImpl pWli = null;
      PersistentWorkOrderLineItemImpl tmpPWli = null;
      String id="";
      String tmpId="";
      
      if (item != null) {
         // The item parameter is NOT null, so let's see about adding it.
         // Is it a PersistentWorkOrderLineItemImpl instance?
         if (!(item instanceof PersistentWorkOrderLineItemImpl)) {
            // It isn't, so let's create an instance using item as a reference.
            pWli = new PersistentWorkOrderLineItemImpl();
            pWli.populateFromObject(item);            
         } else {
            // It is, so let's cast appropriately.
            pWli = (PersistentWorkOrderLineItemImpl) item;
         } // END if (item instanceof PersistentWorkOrderLineItemImpl)
         size = this.lineItems.size();
         for (int pos = 0; pos < size; pos++) {
            tmpPWli = (PersistentWorkOrderLineItemImpl) this.lineItems.get(pos);
            id=defaultValue(pWli.getIdentifier());
            tmpId=defaultValue(tmpPWli.getIdentifier());
            
            if (
                  // If the objects are exactly equal.
                  (tmpPWli.equals(pWli)) ||
                  // The objects have the same non-null identifiers.
                  (  
                        (!isEmpty(id)) &&
                        (!isEmpty(tmpId)) &&
                        (tmpId.equals(id))
                  )
               ) {
               /* Replace the item in the list with the specified persistent
                * object. */
               this.lineItems.remove(pos);
               this.lineItems.add(pos, pWli);
               added = true;
               break;
            } // END if (tmpPQli.equals(pQli))
         } // END for (int pos=0; pos < size; pos++)
         if (!added) {
            // The item is not already in the list, so add it.
            this.lineItems.add(pWli);
         } // END if (!added)
      } else {
         logger.finest("The item parameter was null.");
      } // END if (item != null)
      logger.exiting(_CLASS, "addLineItem(WorkOrderLineItem)", pWli);
      return pWli;
   }

   @Override
   public WorkOrderLineItem createLineItem() {
      logger.entering(_CLASS, "createLineItem()");
      WorkOrderLineItem wli = new PersistentWorkOrderLineItemImpl();
      logger.exiting(_CLASS, "createLineItem()", wli);
      return wli;
   }

   @Override
   public WorkOrderLineItem removeLineItem(WorkOrderLineItem item) {
      logger.entering(_CLASS, "removeLineItem(WorkOrderLineItem)", item);
      boolean removed = false;
      int counter = 1;

      if (item != null) {
         removed = this.lineItems.remove(item);
         if (removed) {
            /*
             * The line item was removed, so lets add it to the list to remove
             * when the Quote is saved.
             */
            this.lineItemsToDelete.add(item);
            // Let's re-number the quotes.
            for (WorkOrderLineItem lineItem : this.lineItems) {
               lineItem.setLineItemNumber(counter++);
            } // END for (QuoteLineItem lineItem: this.lineItems)
         } // END if (removed)
      } else {
         logger.warning("The item parameter is null.");
      } // END if (item != null)
      logger.exiting(_CLASS, "removeLineItem(WorkOrderLineItem)");
      return item;
   }

   @Override
   public WorkOrderLineItem getLineItem(int i) {
      logger.entering(_CLASS, "getLineItem(int)", i);
      logger.exiting(_CLASS, "getLineItem(int)");
      return null;
   }

   @Override
   public double getLineItemCharges() {
      logger.entering(_CLASS, "getLineItemCharges()");
      logger.exiting(_CLASS, "getLineItemCharges()", this.lineItemCharges);
      return this.lineItemCharges;
   }

   public void setLineItemCharges(double charges) {
      logger.entering(_CLASS, "setLineItemCharges(double)", charges);
      if (charges < 0) {
         this.lineItemCharges = 0;
      } else {
         this.lineItemCharges = charges;
      } // END if (charges < 0)
   }

   // ***** lineItemsToRemove
   /**
    * Returns a list of lineItems that should be removed.
    */
   public List<WorkOrderLineItem> getLineItemsToDelete() {
      logger.entering(_CLASS, "getLineItemsToDelete()");
      logger.entering(_CLASS, "getLineItemsToDelete()", this.lineItemsToDelete);
      return this.lineItemsToDelete;
   }

   /*
    *
    */
   @Override
   public String getNotes() {
      logger.entering(_CLASS, "getNotes()");
      logger.exiting(_CLASS, "getNotes()", this.notes);
      return this.notes;
   }

   /*
    *
    */
   @Override
   public void setNotes(String notes) {
      logger.entering(_CLASS, "setNotes(String)", notes);
      this.notes = defaultValue(notes, "");
      logger.exiting(_CLASS, "setNotes(String)");
   }

   // ***** numberOfFlights
   @Override
   public int getNumberOfFlights() {
      logger.entering(_CLASS, "getNumberOfFlights()");
      logger.exiting(_CLASS, "getNumberOfFlights()", this.numberOfFlights);
      return this.numberOfFlights;
   }
   
   public boolean isNumberOfFlightsEditable() {
      logger.entering(_CLASS, "isNumberOfFlightsEditable");
      logger.entering(_CLASS, "isNumberOfFlightsEditable()", this.stairCarry);
      return this.stairCarry;
   }

   public void toggleStairCarry(AjaxBehaviorEvent event) {
      logger.entering(_CLASS, "toggleStairCarry(ActionEvent)", event);
      if ((this.stairCarry == false) && (this.numberOfFlights != 0)) {
         /*
          * If the stairCarry flag is set to false, but the numberOfFlights
          * property is set to a value greater than zero, then it will be reset
          * to zero.
          */
         this.numberOfFlights = 0;
      } // END if ((this.stairCarry == false) && (this.numberOfFlights != 0))
      logger.exiting(_CLASS, "toggleStairCarry(ActionEvent)");
   }
   @Override
   public void setNumberOfFlights(int numberOfFlights) {
      logger.entering(_CLASS, "setNumberOfFlights(int)", numberOfFlights);
      this.numberOfFlights = numberOfFlights;
      logger.exiting(_CLASS, "setNumberOfFlights(int)");
   }

   // ***** originCity
   @Override
   public String getOriginCity() {
      logger.entering(_CLASS, "getOriginCity()");
      logger.exiting(_CLASS, "getOriginCity()", this.originCity);
      return this.originCity;
   }
   
   @Override
   public void setOriginCity(String originCity) {
      logger.entering(_CLASS, "setOriginCity(String)", originCity);
      this.originCity = defaultValue(originCity);
      logger.exiting(_CLASS, "setOriginCity(String)");
   }
   // ***** originCompany
   @Override
   public String getOriginCompanyName() {
      logger.entering(_CLASS, "getOriginCompanyName()");
      logger.exiting(_CLASS, "getOriginCompanyName()",
            this.originCompanyName);
      return this.originCompanyName;
   }
   @Override
   public void setOriginCompanyName(String originCompanyName) {
      logger.entering(_CLASS, "setOriginCompanyName(String)",
            originCompanyName);
      this.originCompanyName = defaultValue(originCompanyName);
      logger.exiting(_CLASS, "setOriginCompanyName(String)");
   }
   // ***** originContactEmail
   @Override
   public String getOriginContactEmail() {
      logger.entering(_CLASS, "getOriginContactEmail()");
      logger.exiting(_CLASS, "getOriginContactEmail()",
            this.originContactEmail);
      return this.originContactEmail;
   }
   @Override
   public void setOriginContactEmail(String originContactEmail) {
      logger.entering(_CLASS, "setOriginContactEmail(String)",
            originContactEmail);
      this.originContactEmail = defaultValue(originContactEmail);
      logger.exiting(_CLASS, "setOriginContactEmail(String)");
   }
   // ***** originContactName
   @Override
   public String getOriginContactName() {
      logger.entering(_CLASS, "getOriginContactName()");
      logger.exiting(_CLASS, "getOriginContactName()",
            this.originContactName);
      return this.originContactName;
   }
   @Override
   public void setOriginContactName(String originContactName) {
      logger.entering(_CLASS, "setOriginContactName(String)",
            originContactName);
      this.originContactName = defaultValue(originContactName);
      logger.exiting(_CLASS, "setOriginContactName(String)");
   }
   
   // ***** originContactPhone
   @Override
   public String getOriginContactPhone() {
      logger.entering(_CLASS, "getOriginContactPhone()");
      logger.exiting(_CLASS, "getOriginContactPhone()",
            this.originContactPhone);
      return this.originContactPhone;
   }
   @Override
   public void setOriginContactPhone(String originContactPhone) {
      logger.entering(_CLASS, "setOriginContactPhone(String)",
            originContactPhone);
      this.originContactPhone = defaultValue(originContactPhone);
      logger.exiting(_CLASS, "setOriginContactPhone(String)");
   }
   
   //***** originResidential
   @Override
   public boolean isOriginResidential() {
      logger.entering(_CLASS, "isOriginResidential()");
      logger.exiting(_CLASS, "isOriginResidential()", 
            this.originResidential);
      return this.originResidential;
   }
   @Override
   public void setOriginResidential(boolean residential) {
      logger.entering(_CLASS, "setOriginResidential(boolean residential)",
            residential);
      this.originResidential=residential;
      logger.exiting(_CLASS, "setOriginResidential(boolean residential)");
   }
   
   // ***** originState
   @Override
   public String getOriginState() {
      logger.entering(_CLASS, "getOriginState()");
      logger.exiting(_CLASS, "getOriginState()", this.originState);
      return this.originState;
   }
   @Override
   public void setOriginState(String originState) {
      logger.entering(_CLASS, "setOriginState(String)", originState);
      this.originState = defaultValue(originState);
      logger.exiting(_CLASS, "setOriginState(String)");
   }
   // ***** originStreetAddress
   @Override
   public String getOriginStreetAddress() {
      logger.entering(_CLASS, "getOriginStreetAddress()");
      logger.exiting(_CLASS, "getOriginStreetAddress()", 
            this.originStreetAddress);
      return this.originStreetAddress;
   }
   @Override
   public void setOriginStreetAddress(String originStreetAddress) {
      logger.entering(_CLASS, "setOriginStreetAddress(String)", 
            originStreetAddress);
      this.originStreetAddress = defaultValue(originStreetAddress);
      logger.exiting(_CLASS, "setOriginStreetAddress(String)");
   }

   // ***** originZip
   /*
    * (non-Javadoc)
    * 
    * @see com.wildstartech.servicedesk.QuickQuote#getOriginZip()
    */
   @Override
   public String getOriginZip() {
      logger.entering(_CLASS, "getOriginZip()");
      logger.exiting(_CLASS, "getOriginZip()", this.originZip);
      return originZip;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * com.wildstartech.servicedesk.QuickQuote#setOriginZip(java.lang.String)
    */
   @Override
   public void setOriginZip(String originZip) {
      logger.entering(_CLASS, "setOriginZip(String)", originZip);
      City city = null;
      PostalCodeFactory factory = null;

      if ((originZip != null) && (originZip.length() > 0)
            && ((this.originZip == null)
                  || (originZip.compareTo(this.originZip) != 0))) {
         /*
          * The new zip code is not null, has a non-zero length, and is
          * different from the current destinationZip, so let's do the lookup.
          */
         factory = PostalCodeFactory.getInstance();
         city = factory.getCity(originZip);
         if (city != null) {
            setOriginCity(city.getName());
            setOriginState(city.getStateAbbreviation());
            setDistance(0);
         } // END if (city != null)
      } // END if ((originZip != null) && (originZip.length() >0))
      this.originZip = defaultValue(originZip);
      logger.exiting(_CLASS, "setOriginZip()");
   }

   // ***** packagingRequired
   public boolean isPackagingRequired() {
      logger.entering(_CLASS, "isPackagingRequired()");
      logger.exiting(_CLASS, "isPackagingRequired()", this.packagingRequired);
      return this.packagingRequired;
   }

   public void setPackagingRequired(boolean packagingRequired) {
      logger.entering(_CLASS, "setPackagingRequired(boolean)",
            packagingRequired);
      this.packagingRequired = packagingRequired;
      logger.exiting(_CLASS, "setPackagingRequired(boolean)");
   }
   
   // ***** paymentMethod
   public String getPaymentMethod() {
      logger.entering(_CLASS, "getPaymentMethod()");
      logger.exiting(_CLASS, "getPaymentMethod()",this.paymentMethod);
      return this.paymentMethod;
   }
   public void setPaymentMethod(String paymentMethod) {
      logger.entering(_CLASS, "setPaymentMethod(String)", paymentMethod);
      this.paymentMethod=defaultValue(paymentMethod);
      logger.exiting(_CLASS, "getPaymentMethod(String)");
   }
   

   // ***** preview
   public boolean isPreview() {
      logger.entering(_CLASS, "isPreview()");
      logger.exiting(_CLASS, "isPreview()", this.preview);
      return this.preview;
   }

   public void setPreview(boolean preview) {
      logger.entering(_CLASS, "setPreview(boolean)", preview);
      this.preview = preview;
      logger.exiting(_CLASS, "setPreview(boolean)");
   }

   // ***** priceModel
   /**
    * Returns the pricing model to be used to rate this quote.
    */
   @Override
   public PriceModel getPriceModel() {
      logger.entering(_CLASS, "getPriceModel()");
      PriceModel pm = null;
      PriceModelFactory pmf = null;
      
      /*
       * Determine whether or not a price model has been specifically identified
       * for use with this quote.
       */
      if (this.priceModel == null) {
         /*
          * Obtain a reference to the PriceModelFactory which will be used to
          * return a valid price model for this quote.
          */
         pmf = PriceModelFactory.getInstance();
         /*
          * Pass this quote to the PriceModelFactory in an effort to get the
          * appropriate price model that we should use.
          */
         pm = pmf.getModel(this);
         // Store the price model
         setPriceModel(pm);
      } else {
         // The local priceModel field IS populated with data, so return that.
         pm = this.priceModel;
      } // END if (this.priceModel == null)

      logger.exiting(_CLASS, "getPriceModel()", pm);
      return pm;
   }

   /**
    * Store a local reference to the specified price model.
    */
   @Override
   public void setPriceModel(PriceModel model) {
      logger.entering(_CLASS, "setPriceModel(PriceModel)", model);
      if (model != null) {
         this.priceModel = model;
         this.priceModelLabel = model.getLabel();
      } else {
         this.priceModel = null;
         this.priceModelLabel = "";
      } // END if (model != null)
      logger.exiting(_CLASS, "setPriceModel(PriceModel)");
   }

   // ***** priceModelLabel
   public String getPriceModelLabel() {
      logger.entering(_CLASS, "getPriceModelLabel()");
      logger.exiting(_CLASS, "getPriceModelLabel()", this.priceModelLabel);
      return this.priceModelLabel;
   }

   public void setPriceModelLabel(String modelLabel) {
      logger.entering(_CLASS, "setPriceModelLabel(String)", modelLabel);
      PriceModel pm = null;
      PriceModelFactory pmFactory = null;

      if ((modelLabel != null) && (modelLabel.length() > 0)) {
         pmFactory = PriceModelFactory.getInstance();
         pm = pmFactory.getModelByLabel(modelLabel);
         if (pm != null) {
            this.priceModel = pm;
            this.priceModelLabel = modelLabel;
         } else {
            logger.finest("No PriceModel with specified name found.");
            pm = pmFactory.getModel(this);
            this.priceModelLabel = pm.getLabel();
         } // END if (pm == null)
         if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Using the " + this.priceModelLabel + " PriceModel.");
         } // END if (logger.isLoggable(Level.FINEST))
      } // END if ((modelLabel != null) && (modelLabel.length() > 0))

      logger.entering(_CLASS, "setPriceModelLabel(String)");
   }

   /**
    * Return a list of price model labels supported by this application.
    * 
    * @return
    */
   public List<String> getPriceModelLabels() {
      logger.entering(_CLASS, "getPriceModelLabels()");
      List<String> labels = null;
      labels = PriceModelFactory.getInstance().getModelLabels();
      logger.exiting(_CLASS, "getPriceModelLabels()", labels);
      return labels;
   }

   // ***** purchaseOrderNumber
   @Override
   public String getPurchaseOrderNumber() {
      logger.entering(_CLASS, "getPurchaseOrderNumber()");
      logger.exiting(_CLASS, "getPurchaseOrderNumber()",
            this.purchaseOrderNumber);
      return this.purchaseOrderNumber;
   }

   @Override
   public void setPurchaseOrderNumber(String poNumber) {
      logger.entering(_CLASS, "setPurchaseOrderNumber(String)", poNumber);
      this.purchaseOrderNumber = defaultValue(poNumber);
      logger.exiting(_CLASS, "setPurchaseOrderNumber(String)");
   }
   
   // ***** quoteRequestId
   @Override 
   public String getQuoteRequestId() {
      logger.entering(_CLASS, "getQuoteRequestId()");
      logger.exiting(_CLASS, "getQuoteRequestId()", this.quoteRequestId);
      return this.quoteRequestId;
   }
   @Override
   public void setQuoteRequestId(String requestId) {
      logger.entering(_CLASS, "setQuoteRequestId(String)", requestId);
      this.quoteRequestId = defaultValue(requestId);
      logger.exiting(_CLASS, "setQuoteRequestId(String)");
   }
   // ***** referral
   @Override
   public String getReferralSource() {
      logger.entering(_CLASS, "getReferralSource()");
      logger.exiting(_CLASS, "getReferralSource()", this.referralSource);
      return this.referralSource;
   }

   @Override
   public void setReferralSource(String source) {
      logger.entering(_CLASS, "setReferralSource(String)", source);
      if ((source != null) && (!source.equals(this.referralSource))) {
         this.referralOther = "";
      } // END if (!source.equals(this.referralSource))
      this.referralSource = defaultValue(source);
      logger.exiting(_CLASS, "setReferralSource(String)");
   }

   public List<String> getReferralOptions() {
      logger.entering(_CLASS, "getReferralOptions()");
      logger.exiting(_CLASS, "getReferralOptions()",
            PersistentWorkOrderImpl.referralOptions);
      return PersistentWorkOrderImpl.referralOptions;
   }

   // ***** referralInternet
   public List<String> getInternetOptions() {
      logger.entering(_CLASS, "getInternetOptions()");
      logger.exiting(_CLASS, "getInternetOptions()",
            PersistentWorkOrderImpl.internetOptions);
      return PersistentWorkOrderImpl.internetOptions;
   }

   // ***** referralOther
   @Override
   public String getReferralOther() {
      logger.entering(_CLASS, "getReferralOther()");
      logger.exiting(_CLASS, "getReferralOther()", this.referralOther);
      return this.referralOther;
   }

   @Override
   public void setReferralOther(String other) {
      logger.entering(_CLASS, "setReferralOther(String)", other);
      this.referralOther = defaultValue(other);
      logger.exiting(_CLASS, "setReferralOther(String)");
   }
   // ***** serviceLevel
   @Override
   public String getServiceLevel() {
      logger.entering(_CLASS, "getServiceLeve()");
      logger.exiting(_CLASS, "getServiceLeve()", this.serviceLevel);
      return this.serviceLevel;
   }

   @Override
   public void setServiceLevel(String serviceLevel) {
      logger.entering(_CLASS, "setServiceLevel(String)", serviceLevel);
      if (serviceLevel != null) {
         if ((serviceLevel.equals("Threshold"))
               || (serviceLevel.equals("Room of Choice"))
               || (serviceLevel.equals("White Glove"))) {
            this.serviceLevel = serviceLevel;
         } else {
            this.serviceLevel = SERVICE_LEVEL_DEFAULT;
         } // END if (!QuoteForm.serviceLevels.contains(serviceLevel))
      }
      logger.exiting(_CLASS, "setServiceLevel(String)", serviceLevel);
   }

   public List<String> getAvailableServiceLevels() {
      logger.entering(_CLASS, "getAvailableServiceLevels()");
      logger.exiting(_CLASS, "getAvailableServiceLevels()",
            PersistentWorkOrderImpl.serviceLevels);
      return PersistentWorkOrderImpl.serviceLevels;
   }   

   public List<String> getStatusStates() {
      logger.entering(_CLASS, "getStatusStates()");
      logger.exiting(_CLASS, "getStatusStates()", 
            PersistentWorkOrderImpl.statusStates);
      return PersistentWorkOrderImpl.statusStates;
   }

   // ***** statusReason
   public List<String> getStatusReasons() {
      logger.entering(_CLASS, "getStatusReasons()");
      List<String> reasons = null;
      String status = "";

      status = getStatusState();
      if (status != null) {
         if (status.equalsIgnoreCase("Resolved")) {
            reasons = PersistentWorkOrderImpl.resolvedClosedStatusReasons;
         } else if (status.equalsIgnoreCase("Closed")) {
            reasons = PersistentWorkOrderImpl.resolvedClosedStatusReasons;
         } else {
            reasons = new ArrayList<String>();
         } // END if (status.equalsIgnoreCase("Pending")) ...
      } else {
         logger.warning("The 'Status' field was null.");
      } // END if (status!= null)
      logger.exiting(_CLASS, "getStatusReasons()", reasons);
      return reasons;
   }

   
   // ***** stairCarry
   public boolean isStairCarry() {
      logger.entering(_CLASS, "isStairCarry()");
      logger.exiting(_CLASS, "isStairCarry()", this.stairCarry);
      return this.stairCarry;
   }

   public void setStairCarry(boolean stairCarry) {
      logger.entering(_CLASS, "setStairCarry(boolean)", stairCarry);
      this.stairCarry = stairCarry;
      logger.exiting(_CLASS, "setStairCarry(boolean)");
   }

   // ***** totalCubes
   @Override
   public int getTotalCubes() {
      logger.entering(_CLASS, "getTotalCubes()");
      int cubes = 0;
      for (WorkOrderLineItem item : this.lineItems) {
         cubes += item.getTotalCube();
      } // END for (WorkOrderLineItemForm item: this.lineItems)
      logger.exiting(_CLASS, "getTotalCubes()", cubes);
      return cubes;
   }

   // ***** totalWeight
   @Override
   public int getTotalWeight() {
      logger.entering(_CLASS, "getTotalWeight()");
      int weight = 0;
      int quantity = 0;
      for (WorkOrderLineItem lineItem : this.lineItems) {
         quantity = lineItem.getQuantity();
         if (quantity < 1)
            quantity = 1;
         weight += (lineItem.getWeight() * quantity);
      } // END for (WorkOrderLineItemForm lineItem: this.lineItems)
      logger.exiting(_CLASS, "getTotalWeight()", weight);
      return weight;
   }

   // ***** type
   public Type getType() {
      logger.entering(_CLASS, "getType()");
      logger.exiting(_CLASS, "getType()",this.type);
      return this.type;
   }
   public void setType(Type type) {
      logger.entering(_CLASS, "setType(Type)",type);
      this.type=type;
      logger.exiting(_CLASS, "setType(Type)");
   }
   // ***** unpackagingRequired
   public boolean isUnpackagingRequired() {
      logger.entering(_CLASS, "isUnpackagingRequired()");
      logger.exiting(_CLASS, "isUnpackagingRequired()",
            this.unpackagingRequired);
      return this.unpackagingRequired;
   }

   public void setUnpackagingRequired(boolean unpackagingRequired) {
      logger.entering(_CLASS, "setUnpackagingRequired(boolean)",
            unpackagingRequired);
      this.unpackagingRequired = unpackagingRequired;
      logger.exiting(_CLASS, "setUnpackagingRequired(boolean)");
   }

   // ***** updateFromObject
   /**
    * 
    */
    public void updateFromObject(WorkOrder workOrder) {
       logger.entering(_CLASS, "updateFromObject(WorkOrder)", workOrder);
       if (workOrder != null) {
          populateFromObject(workOrder);
       } else {
          logger.finest("The specified work order object is null.");
       } // END
       logger.exiting(_CLASS, "updateFromObject(WorkOrder)");
    }
   // ***** valuation
   public double getValuation() {
      logger.entering(_CLASS, "getValuation()");
      logger.exiting(_CLASS, "getValuation()", this.valuation);
      return this.valuation;
   }

   public void setValuation(double valuation) {
      logger.entering(_CLASS, "setValuation(boolean)", valuation);
      if (valuation < 0) {
         this.valuation = 0;
      } else {
         this.valuation = valuation;
      } // END if (valuation < 0)
      logger.exiting(_CLASS, "setValuation(boolean)");
   }

   // ***** valuationDeclined
   public boolean isValuationDeclined() {
      logger.entering(_CLASS, "isValuationDeclined()");
      logger.exiting(_CLASS, "isValuationDeclined()", this.valuationDeclined);
      return this.valuationDeclined;
   }

   public void setValuationDeclined(boolean valuationDeclined) {
      logger.entering(_CLASS, "setValuationDeclined(boolean)",
            valuationDeclined);
      this.valuationDeclined = valuationDeclined;
      logger.exiting(_CLASS, "setValuationDeclined(boolean)");
   }
}