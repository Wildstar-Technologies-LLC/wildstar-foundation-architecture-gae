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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.WildObject;
import com.wildstartech.wfa.dao.finance.CreditCardDAO;
import com.wildstartech.wfa.dao.finance.CreditCardDAOFactory;
import com.wildstartech.wfa.dao.finance.PersistentCreditCard;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentQuote;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentQuoteLineItem;
import com.wildstartech.wfa.dao.logistics.ltl.QuoteLineItemDAO;
import com.wildstartech.wfa.dao.logistics.ltl.QuoteLineItemDAOFactory;
import com.wildstartech.wfa.logistics.ltl.AccessorialCharge;
import com.wildstartech.wfa.logistics.ltl.Quote;
import com.wildstartech.wfa.logistics.ltl.QuoteLineItem;

public class PersistentQuoteImpl 
extends PersistentSimpleQuoteImpl
implements PersistentQuote {
	/** Used in object serialization. */
	private static final long serialVersionUID = 4721130451301097838L;
	private static final String _CLASS = PersistentQuoteImpl.class.getName();
	private static final Logger logger = Logger.getLogger(_CLASS);

	private double accessorialTotal = 0.0;
	private double lineItemCharges = 0;
	private int creditCardExpirationMonth = 0;
	private int creditCardExpirationYear = 0;
	private List<QuoteLineItem> lineItems = null;
	private List<PersistentQuoteLineItem> lineItemsToDelete = null;
	private List<AccessorialCharge> accessorials = null;
	private String billingCity = "";
	private String billingCompanyName = "";
	private String billingContactEmail = "";
	private String billingContactName = "";
	private String billingContactPhone = "";
	private String billingMethod = "";
	private String billingState = "";
	private String billingStreetAddress = "";
	private String billingZip = "";
	private String creditCardIdentifier = "";
	private String creditCardName = "";
	private String creditCardNumber = "";
	private String creditCardType = "";
	private String creditCardVerification = "";
	private String paymentMethod = "";
	private String workOrderRequestId = "";

	/**
	 * Default, no-argument constructor.
	 */
	public PersistentQuoteImpl() {
		logger.entering(_CLASS, "PersistentQuoteImpl()");
		initialize();
		logger.exiting(_CLASS, "PersistentQuoteImpl()");
	}

	@PostConstruct
	public void initialize() {
		logger.entering(_CLASS, "initialize()");
		super.initialize();
		setNotes("");
		setReferralSource("");
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
		int tmpInt = 0;
		NumberFormat cFmt = null;
		String result = null;
		StringBuilder sb = null;

		sb = new StringBuilder(2048);
		cFmt = NumberFormat.getCurrencyInstance();
		sb.append(super.toPropertyString());
		if (sb.length() > 0) {
			sb.append(", ");
		} // END if (sb.length() > 0)
		sb.append(", billingCity=").append(getBillingCity());
		sb.append(", billingCompanyName=").append(getBillingCompanyName());
		sb.append(", billingContactEmail=").append(getBillingContactEmail());
		sb.append(", billingContactName=").append(getBillingContactName());
		sb.append(", billingContactPhone=").append(getBillingContactPhone());
		sb.append(", billingMethod=").append(getBillingMethod());
		sb.append(", billingState=").append(getBillingState());
		sb.append(", billingStreetAddress=").append(getBillingStreetAddress());
		sb.append(", billingZip=").append(getBillingZip());
		sb.append(", creditCardNumber=").append(getCreditCardNumber());
		tmpInt = getCreditCardExpirationMonth();
		sb.append(", creditCardExpirationMonth=");
		switch (tmpInt) {
		case 1:
			sb.append("January");
			break;
		case 2:
			sb.append("February");
			break;
		case 3:
			sb.append("March");
			break;
		case 4:
			sb.append("April");
			break;
		case 5:
			sb.append("May");
			break;
		case 6:
			sb.append("June");
			break;
		case 7:
			sb.append("July");
			break;
		case 8:
			sb.append("August");
			break;
		case 9:
			sb.append("September");
			break;
		case 10:
			sb.append("October");
			break;
		case 11:
			sb.append("November");
			break;
		case 12:
			sb.append("December");
			break;
		default:
		} // END switch(tmpInt)
		sb.append(", creditCardExpirationYear=");
		sb.append(getCreditCardExpirationYear());
		sb.append(", creditCardType=").append(getCreditCardType());
		sb.append(", creditCardVerification=").append(getCreditCardVerification());
		sb.append(", lineItemCharges=").append(cFmt.format(getLineItemCharges()));
		sb.append(", paymentMethod=").append(getPaymentMethod());
		sb.append(", workOrderRequestId=").append(getWorkOrderRequestId());
		result = sb.toString();

		logger.exiting(_CLASS, "toPropertyString()", result);
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
		if (entity != null) {
			super.populateEntity(entity);
			// accessorialTotal
			entity.setProperty("accessorialTotal",getAccessorialTotal());
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
			// billingMethod
			entity.setProperty("billingMethod", getBillingMethod());
			// billingState
			entity.setProperty("billingState", getBillingState());
			// billingStreetAddress
			entity.setProperty("billingStreetAddress", getBillingStreetAddress());
			// billingZip
			entity.setProperty("billingZip", getBillingZip());
			// creditCardIdentifier
			entity.setProperty("creditCardIdentifier", getCreditCardIdentifier());
			// lineItemCharges
			entity.setProperty("lineItemCharges", getLineItemCharges());
			// paymentMethod
			entity.setProperty("paymentMethod", getPaymentMethod());
			// workOrderRequestId
			entity.setProperty("workOrderRequestId", getWorkOrderRequestId());
		} else {
			logger.severe("The entity passed to the method was null.");
		} // END if (entity != null)
		logger.exiting(_CLASS, "populateEntity(Entity)");
	}

	@Override
	public void populateFromEntity(Entity entity, UserContext ctx) {
		logger.entering(_CLASS, "populateFromEntity(Entity)", new Object[] { entity, ctx });
		CreditCardDAO cDAO = null;
		CreditCardDAOFactory cDAOFactory = null;
		PersistentCreditCard pCard = null;
		QuoteLineItemDAO qliDao = null;
		List<PersistentQuoteLineItem> pLineItems = null;
		String tmpStr = null;

		/*
		 * First lets invoke the populateFromEntity object from the parent
		 * objects in the object graph.
		 */
		super.populateFromEntity(entity, ctx);
		// accessorialTotal
      setAccessorialTotal(getPropertyAsFloat(entity,"accessorialTotal"));
		// billingCity
		setBillingCity(getPropertyAsString(entity, "billingCity"));
		// billingCompanyName
		setBillingCompanyName(getPropertyAsString(entity, "billingCompanyName"));
		// billingContactEmail
		setBillingContactEmail(getPropertyAsString(entity, "billingContactEmail"));
		// billingContactName
		setBillingContactName(getPropertyAsString(entity, "billingContactName"));
		// billingContactPhone
		setBillingContactPhone(getPropertyAsString(entity, "billingContactPhone"));
		// billingMethod
		setBillingMethod(getPropertyAsString(entity, "billingMethod"));
		// billingState
		setBillingState(getPropertyAsString(entity, "billingState"));
		// billingStreetAddress
		setBillingStreetAddress(getPropertyAsString(entity, "billingStreetAddress"));
		// billingZip
		setBillingZip(getPropertyAsString(entity, "billingZip"));
		// ***** CreditCard data
		// creditCardIdentifier
		tmpStr = getPropertyAsString(entity, "creditCardIdentifier");
		setCreditCardIdentifier(getPropertyAsString(entity, "creditCardIdentifier"));
		// read the rest of the credit card data.
		if (!isEmpty(tmpStr)) {
			cDAOFactory = new CreditCardDAOFactory();
			cDAO = cDAOFactory.getDAO();
			pCard = cDAO.findByIdentifier(tmpStr, ctx);
			if (pCard != null) {
				// creditCardExpirationMonth
				setCreditCardExpirationMonth(pCard.getExpirationMonth());
				// creditCardExpirationYear
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
		// lineItemCharges
		setLineItemCharges(getPropertyAsFloat(entity, "lineItemCharges"));
		// paymentMethod
		setPaymentMethod(getPropertyAsString(entity, "paymentMethod"));
		// workOrderRequestId
		setWorkOrderRequestId(getPropertyAsString(entity, "workOrderRequestId"));

		// lineItems
		qliDao = new QuoteLineItemDAOFactory().getDAO();
		pLineItems = qliDao.findByQuoteIdentifier(getIdentifier(), ctx);
		this.lineItems = new ArrayList<QuoteLineItem>();
		for (PersistentQuoteLineItem item : pLineItems) {
			this.lineItems.add(item);
		} // END for (PersistentQuoteLineItem item: pLineItems)

		logger.exiting(_CLASS, "populateFromEntity(Entity)");
	}

	public void populateFromObject(Quote quote) {
		logger.entering(_CLASS, "populateFromObject(Quote)", quote);
		List<QuoteLineItem> itemsToRemove = null;
		Map<String, PersistentQuoteLineItemImpl> targetLineItems = null;
		PersistentQuoteLineItemImpl persistentLineItem = null;
		String sourceId = "";

		if (quote != null) {
			super.populateFromObject(quote);
			setAccessorialTotal(quote.getAccessorialTotal());
			setBillingCity(quote.getBillingCity());
			setBillingCompanyName(quote.getBillingCompanyName());
			setBillingContactEmail(quote.getBillingContactEmail());
			setBillingContactName(quote.getBillingContactName());
			setBillingContactPhone(quote.getBillingContactPhone());
			setBillingMethod(quote.getBillingMethod());
			setBillingState(quote.getBillingState());
			setBillingStreetAddress(quote.getBillingStreetAddress());
			setBillingZip(quote.getBillingZip());
			setCreditCardExpirationMonth(quote.getCreditCardExpirationMonth());
			setCreditCardExpirationYear(quote.getCreditCardExpirationYear());
			setCreditCardName(quote.getCreditCardName());
			setCreditCardNumber(quote.getCreditCardNumber());
			setCreditCardType(quote.getCreditCardType());
			setCreditCardVerification(quote.getCreditCardVerification());
			setLineItemCharges(quote.getLineItemCharges());
			setPaymentMethod(quote.getPaymentMethod());
			setWorkOrderRequestId(quote.getWorkOrderRequestId());
			// Accessorials
			// ************************************************************
			// Let's synchronize line items.
			// ***** Build a list of identifiers for records in the source data
			// set.
			targetLineItems = new TreeMap<String, PersistentQuoteLineItemImpl>();
			for (QuoteLineItem item : getLineItems()) {
				persistentLineItem = (PersistentQuoteLineItemImpl) item;
				targetLineItems.put(persistentLineItem.getIdentifier(), persistentLineItem);
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
					// The source ID was NULL, so create a new object and
					// populate
					// it.
					persistentLineItem = new PersistentQuoteLineItemImpl();
					persistentLineItem.populateFromObject(source);
					persistentLineItem.setQuoteIdentifier(getIdentifier());
					addLineItem(persistentLineItem);
				} else {
					// Remove the Line Item with the matching identifier
					persistentLineItem = targetLineItems.remove(sourceId);
					if (persistentLineItem == null) {
						// The current quote DOES NOT have the specified line
						// item.
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
			 * Let's iterate through the list of line items found and remove
			 * those which are no longer associated.
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
		AccessorialCharge aCharge=null;
		logger.exiting(_CLASS, "addAccessorialCharge(charge)", aCharge);
		return aCharge;
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
	public boolean removeAccessorialCharge(AccessorialCharge charge) {
		logger.entering(_CLASS, "removeAccessorialCharge(AccessorialCharge)",
				charge);
		boolean result=false;
		logger.exiting(_CLASS, "removeAccessorialCharge(AccessorialCharge)",
				result);
		return result;		
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

	// ***** billingCity
	public String getBillingCity() {
		logger.entering(_CLASS, "getBillingCity()");
		logger.exiting(_CLASS, "getBillingCity()", this.billingCity);
		return this.billingCity;
	}

	public void setBillingCity(String city) {
		logger.entering(_CLASS, "setBillingCity(String)", city);
		this.billingCity = defaultValue(city);
		logger.exiting(_CLASS, "setBillingCity(String)");
	}

	// ***** billingCompanyName
	public String getBillingCompanyName() {
		logger.entering(_CLASS, "getBillingCompanyName()");
		logger.exiting(_CLASS, "getBillingCompanyName()", this.billingCompanyName);
		return this.billingCompanyName;
	}

	public void setBillingCompanyName(String companyName) {
		logger.entering(_CLASS, "setBillingCompanyName(String)", companyName);
		this.billingCompanyName = defaultValue(companyName);
		logger.exiting(_CLASS, "setBillingCompanyName(String)");
	}

	// ***** billingContactEmail
	public String getBillingContactEmail() {
		logger.entering(_CLASS, "getBillingContactEmail()");
		logger.exiting(_CLASS, "getBillingContactEmail()", this.billingContactEmail);
		return this.billingContactEmail;
	}

	public void setBillingContactEmail(String contactEmail) {
		logger.entering(_CLASS, "setBillingContactEmail(String)", contactEmail);
		this.billingContactEmail = defaultValue(contactEmail);
		logger.exiting(_CLASS, "setBillingContactEmail(String)");
	}

	// ***** billingContactName
	public String getBillingContactName() {
		logger.entering(_CLASS, "getBillingContactName()");
		logger.exiting(_CLASS, "getBillingContactName()", this.billingContactName);
		return this.billingContactName;
	}

	public void setBillingContactName(String contactName) {
		logger.entering(_CLASS, "setBillingContactPhone(String)", contactName);
		this.billingContactName = defaultValue(contactName);
		logger.exiting(_CLASS, "setBillingContactPhone(String)");
	}

	// ***** billingContactPhone
	public String getBillingContactPhone() {
		logger.entering(_CLASS, "getBillingContactPhone()");
		logger.exiting(_CLASS, "getBillingContactPhone()", this.billingContactPhone);
		return this.billingContactPhone;
	}

	public void setBillingContactPhone(String contactPhone) {
		logger.entering(_CLASS, "setBillingContactPhone(String)", contactPhone);
		this.billingContactPhone = defaultValue(contactPhone);
		logger.exiting(_CLASS, "setBillingContactPhone(String)");
	}

	// ***** billingMethod
	@Override
	public String getBillingMethod() {
		logger.entering(_CLASS, "getBillingMethod()");
		logger.exiting(_CLASS, "getBillingMethod()", this.billingMethod);
		return this.billingMethod;
	}

	@Override
	public void setBillingMethod(String method) {
		logger.entering(_CLASS, "setBillingMethod(String)", method);
		this.billingMethod = defaultValue(method);
		logger.exiting(_CLASS, "setBillingMethod(String)");
	}

	// ***** billingState
	@Override
	public String getBillingState() {
		logger.entering(_CLASS, "getBillingState()");
		logger.exiting(_CLASS, "getBillingState()", this.billingState);
		return this.billingState;
	}

	@Override
	public void setBillingState(String state) {
		logger.entering(_CLASS, "setBillingState(String)", state);
		this.billingState = defaultValue(state);
		logger.exiting(_CLASS, "setBillingState(String)");
	}

	// ***** billingStreetAddress
	@Override
	public String getBillingStreetAddress() {
		logger.entering(_CLASS, "getBillingStreetAddress()");
		logger.exiting(_CLASS, "getBillingStreetAddress()", this.billingStreetAddress);
		return this.billingStreetAddress;
	}

	@Override
	public void setBillingStreetAddress(String address) {
		logger.entering(_CLASS, "setBillingStreetAddress(String)", address);
		this.billingStreetAddress = defaultValue(address);
		logger.exiting(_CLASS, "setBillingStreetAddress(String)");
	}

	// ***** billingZip
	@Override
	public String getBillingZip() {
		logger.entering(_CLASS, "getBillingZip()");
		logger.exiting(_CLASS, "getBillingZip()", this.billingZip);
		return this.billingZip;
	}

	@Override
	public void setBillingZip(String zipCode) {
		logger.entering(_CLASS, "setBillingZip(String)", zipCode);
		this.billingZip = defaultValue(zipCode);
		logger.exiting(_CLASS, "setBillingZip(String)");
	}

	// ***** creditCardIdentifier
	protected String getCreditCardIdentifier() {
		logger.entering(_CLASS, "getCreditCardIdentifier()");
		logger.exiting(_CLASS, "getCreditCardIdentifier()", this.creditCardIdentifier);
		return this.creditCardIdentifier;
	}

	protected void setCreditCardIdentifier(String identifier) {
		logger.entering(_CLASS, "setCreditCardIdentifier(String)", identifier);
		this.creditCardIdentifier = defaultValue(identifier);
		logger.exiting(_CLASS, "setCreditCardIdentifier(String)");
	}

	// ***** creditCardExpirationMonth
	@Override
	public int getCreditCardExpirationMonth() {
		logger.entering(_CLASS, "getCreditCardExpirationMonth()");
		logger.exiting(_CLASS, "getCreditCardExpirationMonth()", this.creditCardExpirationMonth);
		return this.creditCardExpirationMonth;
	}

	public void setCreditCardExpirationMonth(int expirationMonth) {
		logger.entering(_CLASS, "setCreditCardExpirationMonth(int)", expirationMonth);
		switch (expirationMonth) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
			this.creditCardExpirationMonth = expirationMonth;
			break;
		default:
			this.creditCardExpirationMonth = 0;
		} // END switch(expirationMonth)
		logger.exiting(_CLASS, "setCreditCardExpirationMonth(int)");
	}

	@Override
	public int getCreditCardExpirationYear() {
		logger.entering(_CLASS, "getCreditCardExpirationYear()");
		logger.exiting(_CLASS, "getCreditCardExpirationYear()", this.creditCardExpirationYear);
		return this.creditCardExpirationYear;
	}

	@Override
	public void setCreditCardExpirationYear(int expirationYear) {
		logger.entering(_CLASS, "setCreditCardExpirationYear(int)", expirationYear);
		if ((expirationYear >= 1950) && (expirationYear <= 2100)) {
			this.creditCardExpirationYear = expirationYear;
		} else {
			this.creditCardExpirationYear = 0;
		} // END if ((expirationYear >= 1950) && (expirationYear <= 2100))
		logger.exiting(_CLASS, "setCreditCardExpirationMonth(int)");
	}

	// ***** creditCardName
	@Override
	public String getCreditCardName() {
		logger.entering(_CLASS, "getCreditCardName()");
		logger.exiting(_CLASS, "getCreditCardName()", this.creditCardName);
		return this.creditCardName;
	}

	@Override
	public void setCreditCardName(String cardName) {
		logger.entering(_CLASS, "setCreditCardName(String)", cardName);
		this.creditCardName = defaultValue(cardName);
		logger.exiting(_CLASS, "setCreditCardName(String)");
	}

	// ***** creditCardNumber
	@Override
	public String getCreditCardNumber() {
		logger.entering(_CLASS, "getCreditCardNumber()");
		logger.exiting(_CLASS, "getCreditCardNumber()", this.creditCardNumber);
		return this.creditCardNumber;
	}

	@Override
	public void setCreditCardNumber(String number) {
		logger.entering(_CLASS, "setCreditCardNumber(String)", number);
		this.creditCardNumber = defaultValue(number);
		logger.exiting(_CLASS, "setCreditCardNumber(String)");
	}

	// ***** creditCardType
	@Override
	public String getCreditCardType() {
		logger.entering(_CLASS, "getCreditCardType()");
		logger.exiting(_CLASS, "getCreditCardType()", this.creditCardType);
		return this.creditCardType;
	}

	@Override
	public void setCreditCardType(String type) {
		logger.entering(_CLASS, "setCreditCardType(String)", type);
		this.creditCardType = defaultValue(type);
		logger.exiting(_CLASS, "setCreditCardType(String)");
	}

	// ***** creditCardVerification
	@Override
	public String getCreditCardVerification() {
		logger.entering(_CLASS, "getCreditCardVerification()");
		logger.exiting(_CLASS, "getCreditCardVerification()", this.creditCardVerification);
		return this.creditCardVerification;
	}

	@Override
	public void setCreditCardVerification(String verification) {
		logger.entering(_CLASS, "setCreditCardVerification(String)", verification);
		this.creditCardVerification = verification;
		logger.entering(_CLASS, "setCreditCardVerification(String)");
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
	 * <code>QuoteLineItem</code> is a <code>PersistentQuoteLineItem</code>,
	 * then the current version will be retrieved from the database, updated
	 * with the information found in the <code>QuoteLineItem</code> passed as a
	 * parameter, and then added to the list.
	 * </p>
	 * <p>
	 * If the <code>QuoteLineItem</code> passed as a parameter DOES NOT already
	 * exist in the persistent data store, then a new
	 * <code>PersistentQuoteLineItem</code> will be created an populated with
	 * the information contained in the <code>QuoteLineItem</code> passed as a,
	 * parameter and then added to the list.
	 * </p>
	 * 
	 * @param item
	 *            The item to add to the list of associated line items.
	 * @return The line item that was added to the list.
	 */
	@Override
	public QuoteLineItem addLineItem(QuoteLineItem item) {
		logger.entering(_CLASS, "addLineItem(QuoteLineItem)", item);
		boolean added = false;
		int size = 0;
		PersistentQuoteLineItemImpl pQli = null;
		PersistentQuoteLineItemImpl tmpPQli = null;
		String id = "";
		String tmpId = "";

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
				id = pQli.getIdentifier();
				tmpId = tmpPQli.getIdentifier();

				if (
				// If the objects are exactly equal.
				(tmpPQli.equals(pQli)) ||
				// The objects have the same identifier.
						((!isEmpty(id)) && (!isEmpty(tmpId)) && (tmpId.equals(id)))) {
					/*
					 * Replace the item in the list with the specified
					 * persistent object.
					 */
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
	public boolean removeLineItem(QuoteLineItem item) {
		logger.entering(_CLASS, "removeLineItem(QuoteLineItem)", item);
		boolean result=false;
		boolean removed = false;
		int counter = 1;

		if (item != null) {
			removed = this.lineItems.remove(item);
			if (removed) {
				/*
				 * The line item was removed, so lets add it to the list to
				 * remove when the Quote is saved.
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
		logger.exiting(_CLASS, "removeLineItem(QuoteLineItem)",result);
		return result;
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

	

	// ***** paymentMethod
	public String getPaymentMethod() {
		logger.entering(_CLASS, "getPaymentMethod()");
		logger.exiting(_CLASS, "getPaymentMethod()", this.paymentMethod);
		return this.paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		logger.entering(_CLASS, "setPaymentMethod(String)", paymentMethod);
		this.paymentMethod = defaultValue(paymentMethod);
		logger.exiting(_CLASS, "getPaymentMethod(String)");
	}

	

	

	// ***** workOrderRequestId
	public String getWorkOrderRequestId() {
		logger.entering(_CLASS, "getWorkOrderRequestId()");
		logger.exiting(_CLASS, "getWorkOrderRequestId()", this.workOrderRequestId);
		return this.workOrderRequestId;
	}

	public void setWorkOrderRequestId(String workOrderRequestId) {
		logger.entering(_CLASS, "setWorkOrderRequestId(String)", workOrderRequestId);
		this.workOrderRequestId = defaultValue(workOrderRequestId);
		logger.exiting(_CLASS, "setWorkOrderRequestId(String)");
	}
}