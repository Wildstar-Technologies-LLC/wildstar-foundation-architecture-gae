/*
 * Copyright (c) 2013 - 2016 Wildstar Technologies, LLC.
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.WildObject;
import com.wildstartech.wfa.dao.finance.CreditCardDAO;
import com.wildstartech.wfa.dao.finance.CreditCardDAOFactory;
import com.wildstartech.wfa.dao.finance.PersistentCreditCard;
import com.wildstartech.wfa.dao.journal.PersistentJournalEntry;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentQuote;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentQuoteLineItem;
import com.wildstartech.wfa.dao.logistics.ltl.QuoteLineItemDAO;
import com.wildstartech.wfa.dao.logistics.ltl.QuoteLineItemDAOFactory;
import com.wildstartech.wfa.journal.JournalEntry;
import com.wildstartech.gae.wfa.dao.journal.JournalDAOImpl;
import com.wildstartech.gae.wfa.dao.journal.PersistentJournalEntryImpl;
import com.wildstartech.gae.wfa.dao.ticketing.PersistentBasicTicketImpl;
import com.wildstartech.wfa.logistics.ltl.AccessorialCharge;
import com.wildstartech.wfa.logistics.ltl.Quote;
import com.wildstartech.wfa.logistics.ltl.QuoteLineItem;
import com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModel;
import com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModelFactory;

public class PersistentQuoteImpl 
extends PersistentBasicTicketImpl<Quote>
implements PersistentQuote {
   /** Used in object serialization. */
   private static final long serialVersionUID = 4721130451301097838L;
   private static final String _CLASS = PersistentQuoteImpl.class.getName();
   private static final Logger logger = Logger.getLogger(_CLASS);

   private static final String ADJUSTMENT_TYPE_FIXED_AMOUNT = "Fixed Amount";
   private static final String ADJUSTMENT_TYPE_PERCENTAGE = "Percentage";

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

   /* Default value for the 'ServiceLevel' field. */
   private static final String SERVICE_LEVEL_DEFAULT = "White Glove";
   private static List<String> serviceLevels = new ArrayList<String>();

   static {
      serviceLevels.add("Threshold");
      serviceLevels.add("Room of Choice");
      serviceLevels.add("White Glove");
   }

   protected static final String _KIND = 
         "com.wildstartech.wfa.logistics.ltl.Quote";

   private boolean assemblyRequired = false;
   private boolean blanketWrapRequired = true;
   private boolean cratingRequired = false;
   private boolean destinationResidential = false;
   private boolean originResidential = false;
   private boolean packagingRequired = false;
   private boolean stairCarry = false;
   private boolean unpackagingRequired = false;
   private boolean valuationDeclined = true;
   private double accessorialTotal = 0.0;
   private double adjustmentAmount = 0.0;
   private double amount = 0.0;
   private double deductible = 0.0;
   private double distance = 0.0;
   private double fuelSurcharge = 0.0;
   private double insuranceCharges = 0;
   private double lineItemCharges = 0;
   private double valuation = 0;
   private int numberOfFlights;
   private AdjustmentType adjustmentType = AdjustmentType.FixedAmount;
   private Date creditCardExpiration=null;
   private List<QuoteLineItem> lineItems = null;
   private List<PersistentQuoteLineItem> lineItemsToDelete = null;
   private List<AccessorialCharge> accessorials = null;
   private PersistentJournalEntryImpl newJournalEntry=null;
   private PriceModel priceModel = null;
   private String billingCity = "";
   private String billingCompanyName = "";
   private String billingContactEmail = "";
   private String billingContactName = "";
   private String billingContactPhone = "";
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
   private String orderType = "";
   private String originCity = "";
   private String originCompanyName = "";
   private String originContactEmail = "";
   private String originContactName = "";
   private String originContactPhone = "";
   private String originState = "";
   private String originStreetAddress = "";
   private String originZip = "";
   private String paymentMethod = "";
   private String purchaseOrderNumber = "";
   private String referralOther = "";
   private String referralSource = "";
   private String serviceLevel = "";
   private String workOrderRequestId = "";

   /**
    * Default, no-argument constructor.
    */
   public PersistentQuoteImpl() {
      logger.entering(_CLASS, "PersistentQuoteImpl()");
      initialize();
      logger.exiting(_CLASS, "PersistentQuoteImpl()");
   }

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

   @PostConstruct
   public void initialize() {
      logger.entering(_CLASS, "initialize()");
      setContactEmail("");
      setContactName("");
      setContactPhone("");
      setDestinationZip("");
      setNotes("");
      setOriginZip("");
      setReferralSource("");
      setServiceLevel(SERVICE_LEVEL_DEFAULT);
      setStatusState("");
      setStatusReason("");
      this.accessorials = new ArrayList<AccessorialCharge>();
      this.lineItems = new ArrayList<QuoteLineItem>();

      logger.exiting(_CLASS, "initialize()");
   }

   public int hashCode() {
      logger.entering(_CLASS, "hashCode()");
      int hashCode = 0;
      StringBuilder sb = null;

      sb = new StringBuilder(512);
      sb.append(toString());
      if (this.lineItems != null) {
         for (QuoteLineItem item : this.lineItems) {
            sb.append(item.toString());
         } // END for (QuoteLineItem item: this.lineItems)
      } // END if (this.lineItems != null)

      logger.exiting(_CLASS, "hashCode()", hashCode);
      return hashCode;
   }
   /**
    * Returns a representation of the properties associated with the quote for
    * inclusion in a toString() method call. 
    * 
    */
   @Override
   public String toPropertyString() {
      logger.entering(_CLASS, "toPropertyString()");
      AdjustmentType adjustmentType=null;
      NumberFormat cFmt = null;
      NumberFormat pFmt = null;
      String result=null;
      StringBuilder sb=null;

      sb=new StringBuilder(2048);
      cFmt=NumberFormat.getCurrencyInstance();
      pFmt=NumberFormat.getPercentInstance();
      sb.append(super.toPropertyString());
      if (sb.length() > 0) {
         sb.append(", ");
      } // END if (sb.length() > 0)
      sb.append("accessorialTotal=").append(cFmt.format(getAccessorialTotal()));
      adjustmentType=getAdjustmentType();
      sb.append(", adjustmentAmount=");
      if (adjustmentType == AdjustmentType.FixedAmount) {
         sb.append(cFmt.format(getAdjustmentAmount()));
         sb.append(", adjustmentType=").append(ADJUSTMENT_TYPE_FIXED_AMOUNT);
      } else {
         sb.append(pFmt.format(getAdjustmentAmount()));
         sb.append(", adjustmentType=").append(ADJUSTMENT_TYPE_PERCENTAGE);
      } // END if (adjustmentType == AdjustmentType.FixedAmount)
      sb.append(", amount=").append(cFmt.format(getAmount()));
      sb.append(", assemblyRequired=").append(isAssemblyRequired());
      sb.append(", billingCity=").append(getBillingCity());
      sb.append(", billingCompanyName=").append(getBillingCompanyName());
      sb.append(", billingContactEmail=").append(getBillingContactEmail());
      sb.append(", billingContactName=").append(getBillingContactName());
      sb.append(", billingContactPhone=").append(getBillingContactPhone());
      sb.append(", billingMethod=").append(getBillingMethod());
      sb.append(", billingState=").append(getBillingState());
      sb.append(", billingStreetAddress=").append(getBillingStreetAddress());
      sb.append(", billingZip=").append(getBillingZip());
      sb.append(", blanketWrapRequired=").append(isBlanketWrapRequired());
      sb.append(", contactCompanyName=").append(getContactName());
      sb.append(", contactName=").append(getContactName());
      sb.append(", contactPhone=").append(getContactPhone());
      sb.append(", cratingRequired=").append(isCratingRequired());
      sb.append(", creditCardNumber=").append(getCreditCardNumber());
      sb.append(", creditCardType=").append(getCreditCardType());
      sb.append(", creditCardVerification=").append(getCreditCardVerification());;
      sb.append(", customerReferenceNote=").append(getCustomerReferenceNote());
      sb.append(", deductible=").append(cFmt.format(getDeductible()));
      sb.append(", destinationCity=").append(getDestinationCity());
      sb.append(", destinationCompanyName=").append(
            getDestinationCompanyName());
      sb.append(", destinationContactEmail=").append(
            getDestinationContactEmail());
      sb.append(", destinationContactName=").append(
            getDestinationContactName());
      sb.append(", destinationContactPhone=").append(
            getDestinationContactPhone());
      sb.append(", destinationState=").append(getDestinationState());
      sb.append(", destinationStreetAddress=").append(
            getDestinationStreetAddress());
      sb.append(", destinationZip=").append(getDestinationZip());
      sb.append(", distance=").append(getDistance());
      sb.append(", fuelSurcharge=").append(cFmt.format(getFuelSurcharge()));
      sb.append(", insuranceCharges=").append(
            cFmt.format(getInsuranceCharges()));
      sb.append(", lineItemCharges=").append(cFmt.format(getLineItemCharges()));
      sb.append(", notes=").append(getNotes());
      sb.append(", numberOfFlights=").append(getNumberOfFlights());
      sb.append(", orderType=").append(getOrderType());
      sb.append(", originCity=").append(getOriginCity());
      sb.append(", originCompanyName=").append(getOriginCompanyName());
      sb.append(", originContactEmail=").append(getOriginContactEmail());
      sb.append(", originContactName=").append(getOriginContactName());
      sb.append(", originContactPhone=").append(getOriginContactPhone());
      sb.append(", originState=").append(getOriginState());
      sb.append(", originStreetAddress=").append(getOriginStreetAddress());
      sb.append(", originZip=").append(getOriginZip());
      sb.append(", paymentMethod=").append(getPaymentMethod());
      sb.append(", purchaseOrderNumber=").append(getPurchaseOrderNumber());
      sb.append(", referralOther=").append(getReferralOther());
      sb.append(", referralSource=").append(getReferralSource());
      sb.append(", serviceLevel=").append(getServiceLevel());
      sb.append(", stairCarry=").append(isStairCarry());
      sb.append(", unpackagingRequired=").append(isUnpackagingRequired());
      sb.append(", valuation=").append(cFmt.format(getValuation()));
      sb.append(", valuationDeclined=").append(isValuationDeclined());
      sb.append(", workOrderRequestId=").append(getWorkOrderRequestId());
      result=sb.toString();
      
      logger.exiting(_CLASS, "toPropertyString()",result);
      return result;
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

   // ***** Utility methods
   @Override
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(Entity)", entity);
      PriceModel pm = null;
      String tmpStr = null;
      Text tmpText = null;

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
         // billingContactPhone
         entity.setProperty("billingContactPhone", getBillingContactPhone());
         // billingMethod
         entity.setProperty("billingMethod", getBillingMethod());
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
         // orderTYpe
         entity.setProperty("orderType", getOrderType());
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
         // referralOther
         entity.setProperty("referralOther", getReferralOther());
         // referralSource
         entity.setProperty("referralSource", getReferralSource());
         // serviceLevel
         entity.setProperty("serviceLevel", getServiceLevel());
         // stairCarry
         entity.setProperty("stairCarry", isStairCarry());
         // unpackagingRequired
         entity.setProperty("unpackagingRequired", isUnpackagingRequired());
         // valuation
         entity.setProperty("valuation", getValuation());
         // valuationDeclined
         entity.setProperty("valuationDeclined", isValuationDeclined());
         // workOrderRequestId
         entity.setProperty("workOrderRequestId", getWorkOrderRequestId());
      } else {
         logger.severe("The entity passed to the method was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateEntity(Entity)");
   }

   @Override
   public void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity)", 
            new Object[] {entity, ctx});
      boolean recalculatePrice = false;
      double tmpDouble = 0;
      CreditCardDAO cDAO=null;
      CreditCardDAOFactory cDAOFactory=null;
      PersistentCreditCard pCard=null;      
      PriceModel pm = null;
      PriceModelFactory pmFactory;
      QuoteLineItemDAO qliDao = null;
      List<PersistentQuoteLineItem> pLineItems = null;
      String tmpStr = null;      

      /* First lets invoke the populateFromEntity object from the
       * parent objects in the object graph. */
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
      //***** CreditCard data      
      // creditCardIdentifier
      tmpStr=getPropertyAsString(entity,"creditCardIdentifier");
      setCreditCardIdentifier(
            getPropertyAsString(entity,"creditCardIdentifier"));
      // read the rest of the credit card data.
      if (!isEmpty(tmpStr)) {
         cDAOFactory=new CreditCardDAOFactory();
         cDAO=cDAOFactory.getDAO();
         pCard=cDAO.findByIdentifier(tmpStr, ctx);
         if (pCard != null) {
            // creditCardExpiration
            setCreditCardExpiration(pCard.getExpirationDate());
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
      // orderType
      setOrderType(getPropertyAsString(entity,"orderType"));
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
      setPackagingRequired(getPropertyAsBoolean(entity, "packagingRequired"));
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
      // referralSource
      setReferralSource(getPropertyAsString(entity, "referralSource"));
      /*
       * referralSource must be populated before referralOther because there is
       * logic in the accessor method that blanks out the referralOther
       * property.
       */
      // referralOther
      setReferralOther(getPropertyAsString(entity, "referralOther"));
      // requestId
      setRequestId(getPropertyAsString(entity, "requestId"));
      // serviceLevel
      setServiceLevel(getPropertyAsString(entity, "serviceLevel"));
      // stairCarry
      setStairCarry(getPropertyAsBoolean(entity, "stairCarry"));
      // numberOfFlights
      setNumberOfFlights(getPropertyAsInteger(entity, "numberOfFlights", 0));
      // unpackagingRequired
      setUnpackagingRequired(
            getPropertyAsBoolean(entity, "unpackagingRequired"));
      // valuation
      setValuation(getPropertyAsDouble(entity, "valuation", 0));
      // valuationDeclined
      setValuationDeclined(getPropertyAsBoolean(entity, "valuationDeclined"));
      // workOrderRequestId
      setWorkOrderRequestId(getPropertyAsString(entity, "workOrderRequestId"));

      // lineItems
      qliDao = new QuoteLineItemDAOFactory().getDAO();
      pLineItems = qliDao.findByQuoteIdentifier(getIdentifier(), ctx);
      this.lineItems = new ArrayList<QuoteLineItem>();
      for (PersistentQuoteLineItem item : pLineItems) {
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
        // ********** END: 2015.06.30 - Addressing Pricing Model Algorithm
        // Changes
      logger.exiting(_CLASS, "populateFromEntity(Entity)");
   }

   @Override
   public void populateFromObject(Quote quote) {
      logger.entering(_CLASS, "populateFromObject(Quote)", quote);
      List<QuoteLineItem> itemsToRemove = null;
      Map<String, PersistentQuoteLineItemImpl> targetLineItems = null;
      PersistentQuoteLineItemImpl persistentLineItem = null;
      String sourceId = "";

      if (quote != null) {
         super.populateFromObject(quote);
         setAccessorialTotal(quote.getAccessorialTotal());
         setAdjustmentAmount(quote.getAdjustmentAmount());
         setAdjustmentType(quote.getAdjustmentType());
         setAmount(quote.getAmount());
         setAssemblyRequired(quote.isAssemblyRequired());
         setBillingCity(quote.getBillingCity());     
         setBillingCompanyName(quote.getBillingCompanyName());
         setBillingContactEmail(quote.getBillingContactEmail());
         setBillingContactName(quote.getBillingContactName());
         setBillingContactPhone(quote.getBillingContactPhone());
         setBillingMethod(quote.getBillingMethod());
         setBillingState(quote.getBillingState());
         setBillingStreetAddress(quote.getBillingStreetAddress());
         setBillingZip(quote.getBillingZip());
         setBlanketWrapRequired(quote.isBlanketWrapRequired());
         setContactCompanyName(quote.getContactCompanyName());
         setContactEmail(quote.getContactEmail());
         setContactName(quote.getContactName());
         setContactPhone(quote.getContactPhone());
         setCratingRequired(quote.isCratingRequired());
         setCreditCardExpiration(quote.getCreditCardExpiration());
         setCreditCardName(quote.getCreditCardName());
         setCreditCardNumber(quote.getCreditCardNumber());
         setCreditCardType(quote.getCreditCardType());
         setCreditCardVerification(quote.getCreditCardVerification());
         setCustomerReferenceNote(quote.getCustomerReferenceNote());
         setDeductible(quote.getDeductible());
         setDestinationCity(quote.getDestinationCity());
         setDestinationCompanyName(quote.getDestinationCompanyName());
         setDestinationContactEmail(quote.getDestinationContactEmail());
         setDestinationContactName(quote.getDestinationContactName());
         setDestinationContactPhone(quote.getDestinationContactPhone());
         setDestinationResidential(quote.isDestinationResidential());
         setDestinationState(quote.getDestinationState());
         setDestinationStreetAddress(quote.getDestinationStreetAddress());
         setDestinationZip(quote.getDestinationZip());
         setDistance(quote.getDistance());
         setFuelSurcharge(quote.getFuelSurcharge());
         setInsuranceCharges(quote.getInsuranceCharges());
         setLineItemCharges(quote.getLineItemCharges());
         setNewJournalEntry(quote.getNewJournalEntry());
         setNotes(quote.getNotes());
         // setNumberOfFlights - set after setStairCarry
         setOrderType(quote.getOrderType());
         setOriginCity(quote.getOriginCity());
         setOriginCompanyName(quote.getOriginCompanyName());
         setOriginContactEmail(quote.getOriginContactEmail());
         setOriginContactName(quote.getOriginContactName());
         setOriginContactPhone(quote.getOriginContactPhone());
         setOriginResidential(quote.isOriginResidential());
         setOriginState(quote.getOriginState());
         setOriginStreetAddress(quote.getOriginStreetAddress());
         setOriginZip(quote.getOriginZip());
         setPackagingRequired(quote.isPackagingRequired());
         setPaymentMethod(quote.getPaymentMethod());
         setPriceModel(quote.getPriceModel());
         setPurchaseOrderNumber(quote.getPurchaseOrderNumber());
         /*
          * There are dependencies with the referralSource and referralOther
          * properties, so referralSource is set first.
          */
         setReferralSource(quote.getReferralSource());
         setReferralOther(quote.getReferralOther());
         setServiceLevel(quote.getServiceLevel());
         setStairCarry(quote.isStairCarry());
         setNumberOfFlights(quote.getNumberOfFlights());
         setUnpackagingRequired(quote.isUnpackagingRequired());
         setValuation(quote.getValuation());
         setValuationDeclined(quote.isValuationDeclined());
         setWorkOrderRequestId(quote.getWorkOrderRequestId());
         // Accessorials
         // ************************************************************
         // Let's synchronize line items.
         // ***** Build a list of identifiers for records in the source data
         // set.
         targetLineItems = new TreeMap<String, PersistentQuoteLineItemImpl>();
         for (QuoteLineItem item : getLineItems()) {
            persistentLineItem = (PersistentQuoteLineItemImpl) item;
            targetLineItems.put(persistentLineItem.getIdentifier(),
                  persistentLineItem);
         } // for (QuoteLineItem source: quote.getLineItems())
         // Now let's iterate over the list of associated line items.
         for (QuoteLineItem source : quote.getLineItems()) {
            sourceId = "";
            if (source instanceof PersistentQuoteLineItem) {
               sourceId = ((PersistentQuoteLineItem) source).getIdentifier();
            } else if (source instanceof WildObject) {
               sourceId = ((WildObject) source).getIdentifier();
            } // END if (source instanceof PersistentQuoteLineItem)

            if (sourceId == null) {
               // The source ID was NULL, so create a new object and populate
               // it.
               persistentLineItem = new PersistentQuoteLineItemImpl();
               persistentLineItem.populateFromObject(source);
               persistentLineItem.setQuoteIdentifier(getIdentifier());
               addLineItem(persistentLineItem);
            } else {
               // Remove the Line Item with the matching identifier
               persistentLineItem = targetLineItems.remove(sourceId);
               if (persistentLineItem == null) {
                  // The current quote DOES NOT have the specified line item.
                  // So we will add it.
                  persistentLineItem = new PersistentQuoteLineItemImpl();
                  persistentLineItem.populateFromObject(source);
                  persistentLineItem.setQuoteIdentifier(getIdentifier());
                  this.lineItems.add(persistentLineItem);
               } else {
                  persistentLineItem.updateFromObject(source);
               } // END if (persistentLineItem == null)
            } // END if (sourceId == null)
         } // END for (QuoteLineItem source: quote.getLineItems())
         /*
          * Let's iterate through the list of line items found and remove those
          * which are no longer associated.
          */
         if ((targetLineItems != null) && (targetLineItems.size() > 0)) {
            // Remove the line items that were no longer a match...
            itemsToRemove = new ArrayList<QuoteLineItem>();
            for (String id : targetLineItems.keySet()) {
               itemsToRemove.add(targetLineItems.get(id));
            } // END for (String id: targetLineItems.keySet())
            for (QuoteLineItem item : itemsToRemove) {
               removeLineItem(item);
            } // END for (QuoteLineItem item: itemsToRemove)
            itemsToRemove = null; // Free the reference.
            targetLineItems = null; // Free the reference.
         } // END if (targetLineItems != null)
      } else {
         logger.warning("The specified Quote object was null.");
      } // END if (quote != null)
      logger.exiting(_CLASS, "populateFromObject(Quote)");
   }

  /**
   * 
   */
   public void updateFromObject(Quote quote) {
      logger.entering(_CLASS, "updateFromObject(Quote)", quote);
      if (quote != null) {
         populateFromObject(quote);
      } else {
         logger.finest("The specified quote object is null.");
      } // END
      logger.exiting(_CLASS, "updateFromObject(Quote)");
   }

   // ***** Accessor methods
   // ***** accessorials
   /*
    * (non-Javadoc)
    * 
    * @see com.wildstartech.servicedesk.QuickQuote#getAccessorialList()
    */
   @Override
   public List<AccessorialCharge> getAccessorialCharges() {
      logger.entering(_CLASS, "getAccessorialCharges())");
      logger.exiting(_CLASS, "getAccessorialCharges())", this.accessorials);
      return this.accessorials;
   }

   @Override
   public AccessorialCharge addAccessorialCharge(AccessorialCharge charge) {
      logger.entering(_CLASS, "addAccessorialCharge(charge)", charge);

      logger.exiting(_CLASS, "addAccessorialCharge(charge)", null);
      return null;
   }

   /**
    * Returns the accessorial charge stored at the specified index.
    */
   @Override
   public AccessorialCharge getAccessorialCharge(int i) {
      logger.entering(_CLASS, "getAccessorialCharge(int)", i);
      AccessorialCharge charge = null;

      if (i < 0) {
         i = 0;
      } else if (i >= this.accessorials.size()) {
         i = this.accessorials.size() - 1;
      } // END if (i < 0)
      charge = this.accessorials.get(i);
      logger.exiting(_CLASS, "getAccessorialCharge(int)", charge);
      return charge;
   }

   @Override
   public AccessorialCharge removeAccessorialCharge(AccessorialCharge charge) {
      return null;

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
      // See CRM00066
      if (
            (getOriginZip().equalsIgnoreCase("96145")) ||
            (getDestinationZip().equalsIgnoreCase("96145"))
         ) {
         this.adjustmentAmount=30;         
      } // END if ((getOriginZip().equalsIgnoreCase("96145")) || ...
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
      
      // See CRM00066
      if (
            (getOriginZip().equalsIgnoreCase("96145")) ||
            (getDestinationZip().equalsIgnoreCase("96145"))
         ) {
         this.adjustmentType=AdjustmentType.Percentage;         
      } // END if ((getOriginZip().equalsIgnoreCase("96145")) || ...
      
      logger.exiting(_CLASS, "getAdjustmentType()", this.adjustmentType);
      return this.adjustmentType;
   }

   public void setAdjustmentType(AdjustmentType type) {
      logger.entering(_CLASS, "setAdjustmentType(AdjustmentType)", type);
      this.adjustmentType = type;
      logger.exiting(_CLASS, "setAdjustmentType(AdjustmentType)");
   }

   // ***** amount
   public double getAmount() {
      logger.entering(_CLASS, "getAmount()");
      logger.exiting(_CLASS, "getAmount()", this.amount);
      return this.amount;
   }

   public void setAmount(double amount) {
      logger.entering(_CLASS, "setAmount(double)", amount);
      if (amount < 0) {
         this.amount = 0;
      } else {
         this.amount = amount;
      } // END if (amount < 0)
      logger.exiting(_CLASS, "setAmount(double)");
   }

   // ***** assemblyRequired
   @Override
   public boolean isAssemblyRequired() {
      logger.entering(_CLASS, "isAssemblyRequired()");
      logger.exiting(_CLASS, "isAssemblyRequired()", this.assemblyRequired);
      return this.assemblyRequired;
   }

   @Override
   public void setAssemblyRequired(boolean assemblyRequired) {
      logger.entering(_CLASS, "setAssemblyRequired(boolean)", assemblyRequired);
      this.assemblyRequired = assemblyRequired;
      logger.exiting(_CLASS, "setAssemblyRequired(boolean)");
   }
   
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
      return contactPhone;
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
   //***** creditCardExpiration
   @Override
   public Date getCreditCardExpiration() {
      logger.entering(_CLASS, "getCreditCardExpiration()");
      logger.exiting(_CLASS, "getCreditCardExpiration()",
            this.creditCardExpiration);
      return this.creditCardExpiration;
   }
   public void setCreditCardExpiration(Date expirationDate) {
      logger.entering(_CLASS, "setCreditCardExpiration(Date)",expirationDate);
      this.creditCardExpiration=expirationDate;
      logger.exiting(_CLASS, "setCreditCardExpiration(String)");
   }
   
   //***** creditCardName
   @Override
   public String getCreditCardName() {
      logger.entering(_CLASS, "getCreditCardName()");
      logger.exiting(_CLASS, "getCreditCardName()",
            this.creditCardName);
      return this.creditCardName;
   }
   @Override
   public void setCreditCardName(String cardName) {
      logger.entering(_CLASS, "setCreditCardName(String)",cardName);
      this.creditCardName=defaultValue(cardName);
      logger.exiting(_CLASS, "setCreditCardName(String)");
   }
   
   //***** creditCardNumber
   @Override
   public String getCreditCardNumber() {
      logger.entering(_CLASS, "getCreditCardNumber()");
      logger.exiting(_CLASS, "getCreditCardNumber()",
            this.creditCardNumber);
      return this.creditCardNumber;
   }
   @Override
   public void setCreditCardNumber(String number) {
      logger.entering(_CLASS, "setCreditCardNumber(String)",number);
      this.creditCardNumber=defaultValue(number);
      logger.exiting(_CLASS, "setCreditCardNumber(String)");
   }
   
   //***** creditCardType
   @Override
   public String getCreditCardType() {
      logger.entering(_CLASS, "getCreditCardType()");
      logger.exiting(_CLASS, "getCreditCardType()",
            this.creditCardType);
      return this.creditCardType;
   }
   @Override
   public void setCreditCardType(String type) {
      logger.entering(_CLASS, "setCreditCardType(String)",type);
      this.creditCardType=defaultValue(type);
      logger.exiting(_CLASS, "setCreditCardType(String)");
   }
   
   //***** creditCardVerification
   @Override
   public String getCreditCardVerification() {
      logger.entering(_CLASS, "getCreditCardVerification()");
      logger.exiting(_CLASS, "getCreditCardVerification()",
            this.creditCardVerification);
      return this.creditCardVerification;
   }
   @Override
   public void setCreditCardVerification(String verification) {
      logger.entering(_CLASS, "setCreditCardVerification(String)",verification);
      this.creditCardVerification=verification;
      logger.entering(_CLASS, "setCreditCardVerification(String)");
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

   // ***** totalCubes
   @Override
   public int getTotalCubes() {
      logger.entering(_CLASS, "getTotalCubes()");
      int cubes = 0;
      for (QuoteLineItem item : this.lineItems) {
         cubes += item.getCube() * item.getQuantity();
      } // END for (QuoteLineItem item: this.lineItems)
      logger.exiting(_CLASS, "getTotalCubes()", cubes);
      return cubes;
   }

   // ***** totalWeight
   @Override
   public int getTotalWeight() {
      logger.entering(_CLASS, "getTotalWeight()");
      int weight = 0;
      for (QuoteLineItem lineItem : this.lineItems) {
         weight += lineItem.getWeight();
      } // END for (QuoteLineItem lineItem: this.lineItems)
      logger.exiting(_CLASS, "getTotalWeight()", weight);
      return weight;
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
      this.deductible = amount;
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
   public boolean isDestinationResidential() {
      logger.entering(_CLASS, "isDestinationResidential()");
      logger.exiting(_CLASS, "isDestinationResidential()", 
            this.destinationResidential);
      return this.destinationResidential;
   }
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

   // ***** destinationStreetAddress
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

   /*
    * (non-Javadoc)
    * 
    * @see com.wildstartech.servicedesk.QuickQuote#setDestinationZip(java.lang.
    * String)
    */
   @Override
   public void setDestinationZip(String destinationZip) {
      logger.entering(_CLASS, "setDestinationZip(String)", destinationZip);
      this.destinationZip = defaultValue(destinationZip);
      logger.exiting(_CLASS, "setDestinationZip(String)");
   }

   // *****
   @Override
   public double getDistance() {
      logger.entering(_CLASS, "getDistance()");
      logger.exiting(_CLASS, "getDistance()", this.distance);
      return this.distance;
   }

   @Override
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
   @Override
   public double getFuelSurcharge() {
      logger.entering(_CLASS, "getFuelSurcharge()");
      logger.exiting(_CLASS, "getFuelSurcharge()", this.fuelSurcharge);
      return this.fuelSurcharge;
   }

   @Override
   public void setFuelSurcharge(double fuelSurcharge) {
      logger.entering(_CLASS, "getFuelSurcharge()", fuelSurcharge);
      if (fuelSurcharge < 0) {
         this.fuelSurcharge = 0;
      } else {
         this.fuelSurcharge = fuelSurcharge;
      } // END if (fuelSurcharge < 0)
      logger.exiting(_CLASS, "getFuelSurcharge()");
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
                  getKind(), 
                  identifier, 
                  ctx);
         } else {
            msg=new StringBuilder(80);
            msg.append("Journal entries are not available until after the ");
            msg.append("Quote has been saved the firs time.");
            logger.warning(msg.toString());
         } // END if (!isEmpty(requestId))          
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
   // ***** insuranceCost
   @Override
   public double getInsuranceCharges() {
      logger.entering(_CLASS, "getInsuranceCharges()");
      logger.exiting(_CLASS, "getInsuranceCharges()", this.insuranceCharges);
      return this.insuranceCharges;
   }

   @Override
   public void setInsuranceCharges(double charges) {
      logger.entering(_CLASS, "setInsuranceCharges(double)", charges);
      if (charges < 0) {
         this.insuranceCharges = 0;
      } else {
         this.insuranceCharges = charges;
      } // END if (charges < 0)
      logger.exiting(_CLASS, "setInsuranceCharges(double)");
   }
   
   // ***** kind
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.entering(_CLASS, "getKind()", PersistentQuoteImpl._KIND);
      return PersistentQuoteImpl._KIND;
   }

   // ***** lineItems
   @Override
   public List<QuoteLineItem> getLineItems() {
      logger.entering(_CLASS, "getLineItems()");
      logger.exiting(_CLASS, "getLineItems()", this.lineItems);
      return this.lineItems;
   }

   protected List<PersistentQuoteLineItem> getLineItemsToDelete() {
      logger.entering(_CLASS, "getLineItemsToDelete()");
      if (this.lineItemsToDelete == null) {
         this.lineItemsToDelete = new ArrayList<PersistentQuoteLineItem>();
      } // END if (this.lineItemsToDelete == null)
      logger.entering(_CLASS, "getLineItemsToDelete()", this.lineItemsToDelete);
      return this.lineItemsToDelete;
   }

   /**
    * Associate the specified QuoteLineItem with the quote.
    * <p>
    * If the specified <code>QuoteLineItem</code> is already present in the
    * list, then it will replace the existing item. If the
    * <code>QuoteLineItem</code> is a <code>PersistentQuoteLineItem</code>, then
    * the current version will be retrieved from the database, updated with the
    * information found in the <code>QuoteLineItem</code> passed as a parameter,
    * and then added to the list.
    * </p>
    * <p>
    * If the <code>QuoteLineItem</code> passed as a parameter DOES NOT already
    * exist in the persistent data store, then a new
    * <code>PersistentQuoteLineItem</code> will be created an populated with the
    * information contained in the <code>QuoteLineItem</code> passed as a,
    * parameter and then added to the list.
    * </p>
    * 
    * @param item
    *           The item to add to the list of associated line items.
    * @return The line item that was added to the list.
    */
   @Override
   public QuoteLineItem addLineItem(QuoteLineItem item) {
      logger.entering(_CLASS, "addLineItem(QuoteLineItem)", item);
      boolean added = false;
      int size = 0;
      PersistentQuoteLineItemImpl pQli = null;
      PersistentQuoteLineItemImpl tmpPQli = null;
      String id="";
      String tmpId="";
      
      if (item != null) {
         // The item parameter is NOT null, so let's see about adding it.
         // Is it a PersistentQuoteLineItemImpl instance?
         if (!(item instanceof PersistentQuoteLineItemImpl)) {
            pQli = new PersistentQuoteLineItemImpl();
            pQli.populateFromObject(item);
         } else {
            pQli = (PersistentQuoteLineItemImpl) item;
         } // END if (!(item instanceof PersistentQuoteLineItemImpl))
         /*
          * Loop through the list of associated line items to see if the one
          * passed is already in the list.
          */
         size = this.lineItems.size();
         for (int pos = 0; pos < size; pos++) {
            tmpPQli = (PersistentQuoteLineItemImpl) this.lineItems.get(pos);
            id=pQli.getIdentifier();
            tmpId=tmpPQli.getIdentifier();
            
            if (
                  // If the objects are exactly equal.
                  (tmpPQli.equals(pQli)) ||
                  // The objects have the same identifier.
                  (
                        (!isEmpty(id)) &&
                        (!isEmpty(tmpId)) &&
                        (tmpId.equals(id))
                  )
               ) {
               /* Replace the item in the list with the specified persistent
                * object. */
               this.lineItems.remove(pos);
               this.lineItems.add(pos, pQli);
               added = true;
               break;
            } // END if (tmpPQli.equals(pQli))
         } // END for (int pos=0; pos < size; pos++)
         if (!added) {
            // The item is not already in the list, so add it.
            this.lineItems.add(pQli);
         } // END if (!added)
      } else {
         logger.warning("QuoteLineItem is null.");
      } // END if (item != null)
      logger.exiting(_CLASS, "addLineItem(QuoteLineItem)", pQli);
      return pQli;
   }

   @Override
   public QuoteLineItem createLineItem() {
      logger.entering(_CLASS, "createLineItem()");
      PersistentQuoteLineItemImpl lineItem = null;

      lineItem = new PersistentQuoteLineItemImpl();
      this.lineItems.add(lineItem);

      logger.exiting(_CLASS, "createLineItem()", lineItem);
      return lineItem;
   }

   @Override
   public QuoteLineItem removeLineItem(QuoteLineItem item) {
      logger.entering(_CLASS, "removeLineItem(QuoteLineItem)", item);
      boolean removed = false;
      int counter = 1;

      if (item != null) {
         removed = this.lineItems.remove(item);
         if (removed) {
            /*
             * The line item was removed, so lets add it to the list to remove
             * when the Quote is saved.
             */
            if (this.lineItemsToDelete == null) {
               this.lineItemsToDelete = new ArrayList<PersistentQuoteLineItem>();
            } // END if (this.lineItemsToDelete == null)
            if (item instanceof PersistentQuoteLineItem) {
               this.lineItemsToDelete.add((PersistentQuoteLineItem) item);
            } // END if (item instanceof PersistentQuoteLineItem)
              // Re-number the line items.
            for (QuoteLineItem lineItem : this.lineItems) {
               lineItem.setLineItemNumber(counter++);
            } // END for (QuoteLineItem lineItem: this.lineItems)
         } // END if (removed)

      } else {
         logger.warning("The item parameter is null.");
      } // END if (item != null)
      logger.exiting(_CLASS, "removeLineItem(QuoteLineItem)");
      return item;
   }

   @Override
   public QuoteLineItem getLineItem(int i) {
      logger.entering(_CLASS, "getLineItem(int)");
      if (i < 0) {
         i = 0;
      } else if (i >= this.lineItems.size()) {
         if (this.lineItems.size() > 0) {
            i = this.lineItems.size() - 1;
         } // END if (this.lineItems.size() > 0)
      }
      logger.exiting(_CLASS, "getLineItem(int)");
      return null;
   }

   /**
    * Returns a sum of the line item charges.
    */
   @Override
   public double getLineItemCharges() {
      logger.entering(_CLASS, "getLineItemCharges()");
      logger.exiting(_CLASS, "getLineItemCharges()", this.lineItemCharges);
      return this.lineItemCharges;
   }

   @Override
   public void setLineItemCharges(double charges) {
      logger.entering(_CLASS, "setLineItemCharges(double)", charges);
      if (charges < 0) {
         charges = 0;
      } // END if (charges < 0)
      this.lineItemCharges = charges;
      logger.exiting(_CLASS, "setLineItemCharges(double)");
   }
   
   // ***** notes
   /*
    * (non-Javadoc)
    * 
    * @see com.wildstartech.servicedesk.QuickQuote#getNotes()
    */
   @Override
   public String getNotes() {
      logger.entering(_CLASS, "getNotes()");
      logger.exiting(_CLASS, "getNotes()", this.notes);
      return this.notes;
   }

   /*
    * (non-Javadoc)
    * 
    * @see com.wildstartech.servicedesk.QuickQuote#setNotes(java.lang.String)
    */
   @Override
   public void setNotes(String notes) {
      logger.entering(_CLASS, "setNotes(String)", notes);
      this.notes = defaultValue(notes);
      logger.exiting(_CLASS, "setNotes(String)");
   }

   // ***** numberOfFlights
   @Override
   public int getNumberOfFlights() {
      logger.entering(_CLASS, "getNumberOfFlights()");
      logger.exiting(_CLASS, "getNumberOfFlights()", this.numberOfFlights);
      return this.numberOfFlights;
   }

   @Override
   public void setNumberOfFlights(int numberOfFlights) {
      logger.entering(_CLASS, "setNumberOfFlights(int)", numberOfFlights);
      if (numberOfFlights < 0) {
         this.numberOfFlights = 0;
      } else {
         this.numberOfFlights = numberOfFlights;
      } // END if (numberOfFlights < 0)
      logger.exiting(_CLASS, "setNumberOfFlights(int)");
   }

   // ***** orderType
   public String getOrderType() {
      logger.entering(_CLASS, "getOrderType()");
      logger.exiting(_CLASS, "getOrderType()",this.orderType);
      return this.orderType;
   }
   public void setOrderType(String orderType) {
      logger.entering(_CLASS, "setOrderType(String)",orderType);
      this.orderType=defaultValue(orderType);
      logger.exiting(_CLASS, "setOrderType(String)");
   }
   
   // ***** originCity
   public String getOriginCity() {
      logger.entering(_CLASS, "getOriginCity()");
      logger.exiting(_CLASS, "getOriginCity()", this.originCity);
      return this.originCity;
   }

   public void setOriginCity(String originCity) {
      logger.entering(_CLASS, "setOriginCity(String)", originCity);
      this.originCity = defaultValue(originCity);
      logger.exiting(_CLASS, "setOriginCity(String)");
   }

   // ***** originCompany
   @Override
   public String getOriginCompanyName() {
      logger.entering(_CLASS, "getOriginCompanyName()");
      logger.exiting(_CLASS, "getOriginCompanyName()", this.originCompanyName);
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
      logger.exiting(_CLASS, "getOriginContactName()", this.originContactName);
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
      this.originZip = defaultValue(originZip);
      logger.exiting(_CLASS, "setOriginZip()");
   }

   // ***** packagingRequired
   @Override
   public boolean isPackagingRequired() {
      logger.entering(_CLASS, "isPackagingRequired()");
      logger.exiting(_CLASS, "isPackagingRequired()", this.packagingRequired);
      return this.packagingRequired;
   }

   @Override
   public void setPackagingRequired(boolean packaging) {
      logger.entering(_CLASS, "setPackagingRequired(boolean)", packaging);
      this.packagingRequired = packaging;
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
   
   // ***** priceModel
   public PriceModel getPriceModel() {
      logger.entering(_CLASS, "getPriceModel()");
      PriceModelFactory pmFactory = null;
      if (this.priceModel == null) {
         /*
          * If the priceModel has NOT yet been specified, the system will obtain
          * a reference from the PriceModelFactory.
          */
         pmFactory = PriceModelFactory.getInstance();
         /* Store a local reference to the PriceModel */
         this.priceModel = pmFactory.getModel(this);
      } // END if (this.priceModel == null)
      logger.exiting(_CLASS, "getPriceModel()", this.priceModel);
      return this.priceModel;
   }

   public void setPriceModel(PriceModel model) {
      logger.entering(_CLASS, "setPriceModel(PriceModel)", model);
      this.priceModel = model;
      logger.exiting(_CLASS, "setPriceModel(PriceModel)");
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
            PersistentQuoteImpl.referralOptions);
      return PersistentQuoteImpl.referralOptions;
   }

   // ***** referralInternet
   public List<String> getInternetOptions() {
      logger.entering(_CLASS, "getInternetOptions()");
      logger.exiting(_CLASS, "getInternetOptions()",
            PersistentQuoteImpl.internetOptions);
      return PersistentQuoteImpl.internetOptions;
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
      this.serviceLevel = defaultValue(serviceLevel);
      logger.exiting(_CLASS, "setServiceLevel(String)", serviceLevel);
   }

   public List<String> getAvailableServiceLevels() {
      logger.entering(_CLASS, "getAvailableServiceLevels()");
      logger.exiting(_CLASS, "getAvailableServiceLevels()",
            PersistentQuoteImpl.serviceLevels);
      return PersistentQuoteImpl.serviceLevels;
   }

   // ***** stairCarry
   @Override
   public boolean isStairCarry() {
      logger.entering(_CLASS, "isStairCarry()");
      logger.exiting(_CLASS, "isStairCarry()", this.stairCarry);
      return this.stairCarry;
   }

   @Override
   public void setStairCarry(boolean value) {
      logger.entering(_CLASS, "setStairCarry(boolean)", value);
      this.stairCarry = value;
      logger.exiting(_CLASS, "setStairCarry(boolean)");
   }

   // ***** unpackagingRequired
   @Override
   public boolean isUnpackagingRequired() {
      logger.entering(_CLASS, "isUnpackagingRequired()");
      logger.exiting(_CLASS, "isUnpackagingRequired()",
            this.unpackagingRequired);
      return this.unpackagingRequired;
   }

   @Override
   public void setUnpackagingRequired(boolean unpackaging) {
      logger.entering(_CLASS, "setUnpackagingRequired(boolean)", unpackaging);
      this.unpackagingRequired = unpackaging;
      logger.exiting(_CLASS, "setUnpackagingRequired(boolean)");
   }

   // ***** valuation
   @Override
   public double getValuation() {
      logger.entering(_CLASS, "getValuation()");
      logger.exiting(_CLASS, "getValuation()", this.valuation);
      return this.valuation;
   }

   @Override
   public void setValuation(double amount) {
      logger.entering(_CLASS, "setValuation(double)", amount);
      if (amount < 0) {
         this.valuation = 0;
      } else {
         this.valuation = amount;
      } // END if (amount < 0)
      logger.exiting(_CLASS, "setValuation(double)");
   }

   // ***** valuationDeclined
   @Override
   public boolean isValuationDeclined() {
      logger.entering(_CLASS, "isValuationDeclined()");
      logger.exiting(_CLASS, "isValuationDeclined()", this.valuationDeclined);
      return this.valuationDeclined;
   }

   @Override
   public void setValuationDeclined(boolean value) {
      logger.entering(_CLASS, "setValuationDeclined(boolean)", value);
      this.valuationDeclined = value;
      logger.exiting(_CLASS, "setValuationDeclined(boolean)");
   }

   // ***** workOrderRequestId
   public String getWorkOrderRequestId() {
      logger.entering(_CLASS, "getWorkOrderRequestId()");
      logger.exiting(_CLASS, "getWorkOrderRequestId()",
            this.workOrderRequestId);
      return this.workOrderRequestId;
   }

   public void setWorkOrderRequestId(String workOrderRequestId) {
      logger.entering(_CLASS, "setWorkOrderRequestId(String)",
            workOrderRequestId);
      this.workOrderRequestId = defaultValue(workOrderRequestId);
      logger.exiting(_CLASS, "setWorkOrderRequestId(String)");
   }
}