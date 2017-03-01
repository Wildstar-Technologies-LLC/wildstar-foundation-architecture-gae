package com.wildstartech.gae.wfa.dao.logistics.ltl.quote;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.journal.JournalDAOImpl;
import com.wildstartech.gae.wfa.dao.journal.PersistentJournalEntryImpl;
import com.wildstartech.gae.wfa.dao.ticketing.PersistentBasicTicketImpl;
import com.wildstartech.wfa.dao.journal.PersistentJournalEntry;
import com.wildstartech.wfa.dao.logistics.ltl.quote.PersistentQuickQuote;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.journal.JournalEntry;
import com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModel;
import com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModelFactory;
import com.wildstartech.wfa.logistics.ltl.quote.QuickQuote;

public class PersistentQuickQuoteImpl 
extends PersistentBasicTicketImpl<QuickQuote> 
implements PersistentQuickQuote {
	/** Used in object serialization. */
	private static final long serialVersionUID = -893624294240299966L;
	private static final String _CLASS = PersistentQuickQuoteImpl.class.getName();
	private static final Logger logger = Logger.getLogger(_CLASS);
	
	private static final QuoteMethod _QuoteMethodDefault=QuoteMethod.ByCube;
	private static List<String> serviceLevels = new ArrayList<String>();
	static {
		serviceLevels.add("Threshold");
		serviceLevels.add("Room of Choice");
		serviceLevels.add("White Glove");
	}
	public static final String ADJUSTMENT_TYPE_FIXED_AMOUNT = "Fixed Amount";
	public static final String ADJUSTMENT_TYPE_PERCENTAGE = "Percentage";
	/* Default value for the 'ServiceLevel' field. */
	private static final String SERVICE_LEVEL_DEFAULT = "White Glove";
	protected static final String _KIND = "com.wildstartech.wfa.logistics.ltl.Quote";

	private boolean valuationDeclined = true;
	private double adjustmentAmount = 0.0;
	private double amount = 0.0;
	private double deductible = 0.0;
	private double distance = 0.0;
	private double fuelSurcharge = 0.0;
	private double insuranceCharges = 0;
	private double valuation = 0;
	private int totalCubes = 0;
	private int totalWeight = 0;
	private AdjustmentType adjustmentType = AdjustmentType.FixedAmount;
	private PersistentJournalEntryImpl newJournalEntry = null;
	private PriceModel priceModel = null;
	private QuoteMethod quoteMethod=
	      PersistentQuickQuoteImpl._QuoteMethodDefault;
	private String contactCompanyName = "";
	private String contactName = "";
	private String contactPhone = "";
	private String contactPhoneHidden = "";
	private String contactEmail = "";
	private String customerReferenceNote = "";
	private String destinationZip = "";
	private String orderType = "";
	private String originZip = "";
	private String serviceLevel = "";

	/**
	 * Default, no-argument constructor.
	 */
	public PersistentQuickQuoteImpl() {
		super();
		logger.entering(_CLASS, "PersistentQuickQuoteImpl()");
		logger.exiting(_CLASS, "PersistentQuickQuoteImpl()");
	}

	@PostConstruct
	public void initialize() {
		logger.entering(_CLASS, "initialize()");
		setContactEmail("");
		setContactName("");
		setContactPhone("");
		setDestinationZip("");
		setOriginZip("");
		setQuoteMethod(_QuoteMethodDefault);
		setServiceLevel(SERVICE_LEVEL_DEFAULT);
		setStatusState("");
		setStatusReason("");

		logger.exiting(_CLASS, "initialize()");
	}

	// ***** Utility methods
	// **********
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
	// ***** kind
	@Override
	public final String getKind() {
		logger.entering(_CLASS, "getKind()");
		logger.entering(_CLASS, "getKind()", PersistentQuickQuoteImpl._KIND);
		return PersistentQuickQuoteImpl._KIND;
	}
	@Override
	protected void populateEntity(Entity entity) {
		logger.entering(_CLASS, "populateEntity(Entity)", entity);
		AdjustmentType adjustmentType=null;
		PriceModel pm = null;
		String tmpStr = null;

		if (entity != null) {
			super.populateEntity(entity);
			// adjustmentAmount
			entity.setProperty("adjustmentAmount", getAdjustmentAmount());
			// adjustmentType
			adjustmentType=getAdjustmentType();
			if (adjustmentType != null) {
   			switch (adjustmentType) {
   			case FixedAmount:
   				entity.setProperty("adjustmentType", ADJUSTMENT_TYPE_FIXED_AMOUNT);
   				break;
   			default:
   				entity.setProperty("adjustmentType", ADJUSTMENT_TYPE_PERCENTAGE);
   			} // END switch(getAdjustmentType())
			} // END if (adjustmentType != null)
   		// amount
			entity.setProperty("amount", getAmount());
			// contactCompanyName
			entity.setProperty("contactCompanyName", getContactCompanyName());
			// contactEmail
			entity.setProperty("contactEmail", getContactEmail());
			// contactName
			entity.setProperty("contactName", getContactName());
			// contactPhone
			entity.setProperty("contactPhone", getContactPhone());
			// customerReferenceNote
			entity.setProperty("customerReferenceNote", getCustomerReferenceNote());
			// deductible
			entity.setProperty("deductible", getDeductible());
			// destinationZip
			entity.setProperty("destinationZip", getDestinationZip());
			// distance
			entity.setProperty("distance", getDistance());
			// fuelSurcharge
			entity.setProperty("fuelSurcharge", getFuelSurcharge());
			// insuranceCharges
			entity.setProperty("insuranceCharges", getInsuranceCharges());
			// orderTYpe
			entity.setProperty("orderType", getOrderType());
			// originZip
			entity.setProperty("originZip", getOriginZip());
			// priceModel
			pm = getPriceModel();
			if (pm == null) {
				tmpStr = "";
			} else {
				tmpStr = pm.getLabel();
			} // END if (pm == null)
			entity.setProperty("priceModelLabel", tmpStr);
			// quoteMethod
			entity.setProperty("quoteMethod", getQuoteMethodLabel());
			// serviceLevel
			entity.setProperty("serviceLevel", getServiceLevel());
			// totalCubes
			entity.setProperty("totalCubes", getTotalCubes());
			// totalWeight
			entity.setProperty("totalWeight", getTotalWeight());
			// valuation
			entity.setProperty("valuation", getValuation());
			// valuationDeclined
			entity.setProperty("valuationDeclined", isValuationDeclined());			
		} else {
			logger.severe("The entity passed to the method was null.");
		} // END if (entity != null)
		logger.exiting(_CLASS, "populateEntity(Entity)");
	}

	@Override
	public void populateFromEntity(Entity entity, UserContext ctx) {
		logger.entering(_CLASS, "populateFromEntity(Entity)", 
				new Object[] { entity, ctx });
		PriceModel pm = null;
		PriceModelFactory pmFactory;
		String tmpStr = null;

		/*
		 * First lets invoke the populateFromEntity object from the parent
		 * objects in the object graph.
		 */
		super.populateFromEntity(entity, ctx);
		// adjustmentAmount
		setAdjustmentAmount(getPropertyAsDouble(entity, "adjustmentAmount"));
		// adjustmentType
		tmpStr = getPropertyAsString(entity, "adjustmentType");
		if (
				(tmpStr != null) && 
				(tmpStr.equalsIgnoreCase(ADJUSTMENT_TYPE_FIXED_AMOUNT))
			) {
			setAdjustmentType(AdjustmentType.FixedAmount);
		} else {
			setAdjustmentType(AdjustmentType.Percentage);
		} // END if (tmpStr.equalsIgnoreCase(ADJUSTMENT_TYPE_FIXED_AMOUNT))
			// amount
		setAmount(getPropertyAsDouble(entity, "amount"));
		// contactCompanyName
		setContactCompanyName(getPropertyAsString(entity, "contactCompanyName"));
		// contactEmail
		setContactEmail(getPropertyAsString(entity, "contactEmail"));
		// contactName
		setContactName(getPropertyAsString(entity, "contactName"));
		// contactPhone
		setContactPhone(getPropertyAsString(entity, "contactPhone"));
		// customerReferenceNote
		setCustomerReferenceNote(
				getPropertyAsString(entity, "customerReferenceNote"));
		// deductible
		setDeductible(getPropertyAsDouble(entity,"deductible"));
		// destinationZip
		setDestinationZip(getPropertyAsString(entity, "destinationZip"));
		// distance
		setDistance(getPropertyAsFloat(entity, "distance"));
		// fuelSurcharge
		setFuelSurcharge(getPropertyAsFloat(entity, "fuelSurcharge"));
		// insuranceCharges
		setInsuranceCharges(getPropertyAsFloat(entity, "insuranceCharges"));
		// numberOfFlights set after stairCarry
		// orderType
		setOrderType(getPropertyAsString(entity, "orderType"));
		// originZip
		setOriginZip(getPropertyAsString(entity, "originZip"));
		// priceModel
		tmpStr = getPropertyAsString(entity, "priceModelLabel");
		pmFactory = PriceModelFactory.getInstance();
		if (tmpStr != null) {
			pm = pmFactory.getModelByLabel(tmpStr);
		} else {
			pm = pmFactory.getDefaultModel();
		} // END if (tmpStr != null)
		setPriceModel(pm);
		// quoteMethod
		setQuoteMethod(getPropertyAsString(entity, "quoteMethod"));
		// serviceLevel
		setServiceLevel(getPropertyAsString(entity, "serviceLevel"));
		// totalCubes
		setTotalCubes(getPropertyAsInteger(entity,"totalCubes"));
		// totalWeight
		setTotalWeight(getPropertyAsInteger(entity,"totalWeight"));
		// valuation
		setValuation(getPropertyAsDouble(entity, "valuation", 0));
		// valuationDeclined
		setValuationDeclined(getPropertyAsBoolean(entity, "valuationDeclined"));
		logger.exiting(_CLASS, "populateFromEntity(Entity)");
	}

	@Override
	public void populateFromObject(QuickQuote quote) {
		logger.entering(_CLASS, "populateFromObject(QuickQuote)", quote);
		
		if (quote != null) {
			super.populateFromObject(quote);		
			setAdjustmentType(quote.getAdjustmentType());
			setAdjustmentAmount(quote.getAdjustmentAmount());
			setAmount(quote.getAmount());
			setContactCompanyName(quote.getContactCompanyName());
			setContactEmail(quote.getContactEmail());
			setContactName(quote.getContactName());
			setContactPhone(quote.getContactPhone());
			setCustomerReferenceNote(quote.getCustomerReferenceNote());
			setDeductible(quote.getDeductible());
			setDestinationZip(quote.getDestinationZip());
			setDistance(quote.getDistance());
			setFuelSurcharge(quote.getFuelSurcharge());
			setInsuranceCharges(quote.getInsuranceCharges());
			setNewJournalEntry(quote.getNewJournalEntry());
			// setNumberOfFlights - set after setStairCarry
			setOrderType(quote.getOrderType());
			setOriginZip(quote.getOriginZip());
			setPriceModel(quote.getPriceModel());
			setQuoteMethod(quote.getQuoteMethod());
			setServiceLevel(quote.getServiceLevel());
			setTotalCubes(quote.getTotalCubes());
			setTotalWeight(quote.getTotalWeight());
			setValuation(quote.getValuation());
			setValuationDeclined(quote.isValuationDeclined());			
		} else {
			logger.warning("The specified QuickQuote object was null.");
		} // END if (quote != null)
		logger.exiting(_CLASS, "populateFromObject(QuickQuote)");
	}

	/**
	 * Returns a representation of the properties associated with the quote for
	 * inclusion in a toString() method call.
	 * 
	 */
	@Override
	public String toPropertyString() {
		logger.entering(_CLASS, "toPropertyString()");
		AdjustmentType adjustmentType = null;
		NumberFormat cFmt = null;
		NumberFormat pFmt = null;
		String result = null;
		StringBuilder sb = null;

		sb = new StringBuilder(2048);
		cFmt = NumberFormat.getCurrencyInstance();
		pFmt = NumberFormat.getPercentInstance();
		sb.append(super.toPropertyString());
		if (sb.length() > 0) {
			sb.append(", ");
		} // END if (sb.length() > 0)
		adjustmentType = getAdjustmentType();
		sb.append(", adjustmentAmount=");
		if (adjustmentType == AdjustmentType.FixedAmount) {
			sb.append(cFmt.format(getAdjustmentAmount()));
			sb.append(", adjustmentType=").append(ADJUSTMENT_TYPE_FIXED_AMOUNT);
		} else {
			sb.append(pFmt.format(getAdjustmentAmount()));
			sb.append(", adjustmentType=").append(ADJUSTMENT_TYPE_PERCENTAGE);
		} // END if (adjustmentType == AdjustmentType.FixedAmount)
		sb.append(", amount=").append(cFmt.format(getAmount()));
		sb.append(", contactCompanyName=").append(getContactName());
		sb.append(", contactName=").append(getContactName());
		sb.append(", contactPhone=").append(getContactPhone());
		sb.append(", customerReferenceNote=").append(getCustomerReferenceNote());
		sb.append(", deductible=").append(cFmt.format(getDeductible()));
		sb.append(", destinationZip=").append(getDestinationZip());
		sb.append(", distance=").append(getDistance());
		sb.append(", fuelSurcharge=").append(cFmt.format(getFuelSurcharge()));
		sb.append(", insuranceCharges=").append(cFmt.format(getInsuranceCharges()));
		sb.append(", orderType=").append(getOrderType());
		sb.append(", originZip=").append(getOriginZip());
		sb.append(", quoteMethod=").append(getQuoteMethodLabel());
		sb.append(", serviceLevel=").append(getServiceLevel());
		sb.append(", valuation=").append(cFmt.format(getValuation()));
		sb.append(", valuationDeclined=").append(isValuationDeclined());
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
	
	/**
	 * 
	 */
	public void updateFromObject(QuickQuote quote) {
		logger.entering(_CLASS, "updateFromObject(Quote)", quote);
		if (quote != null) {
			populateFromObject(quote);
			setAdjustmentType(quote.getAdjustmentType());
			setAdjustmentAmount(quote.getAdjustmentAmount());
			setAmount(quote.getAmount());
			setContactCompanyName(quote.getContactCompanyName());
			setContactEmail(quote.getContactEmail());
			setContactName(quote.getContactName());
			setContactPhone(quote.getContactPhone());
			setCustomerReferenceNote(quote.getCustomerReferenceNote());
			setDeductible(quote.getDeductible());
			setDestinationZip(quote.getDestinationZip());
			setDistance(quote.getDistance());
			setFuelSurcharge(quote.getFuelSurcharge());
			setInsuranceCharges(quote.getInsuranceCharges());
			setNewJournalEntry(quote.getNewJournalEntry());
			setOrderType(quote.getOrderType());
			setOriginZip(quote.getOriginZip());
			setPriceModel(quote.getPriceModel());
			setServiceLevel(quote.getServiceLevel());
			setTotalCubes(quote.getTotalCubes());
			setTotalWeight(quote.getTotalWeight());
			setValuation(quote.getValuation());
			setValuationDeclined(quote.isValuationDeclined());
		} else {
			logger.finest("The specified quote object is null.");
		} // END
		logger.exiting(_CLASS, "updateFromObject(Quote)");
	}

	// ***** adjustmentAmount
	public double getAdjustmentAmount() {
		logger.entering(_CLASS, "getAdjustmentAmount()");
		// See CRM00066
		if ((getOriginZip().equalsIgnoreCase("96145")) || (getDestinationZip().equalsIgnoreCase("96145"))) {
			this.adjustmentAmount = 30;
		} // END if ((getOriginZip().equalsIgnoreCase("96145")) || ...
		logger.exiting(_CLASS, "getAdjustmentAmount()", this.adjustmentAmount);
		return this.adjustmentAmount;
	}

	public final void setAdjustmentAmount(double amount) {
		logger.entering(_CLASS, "setAdjustmentAmount(double)", amount);
		this.adjustmentAmount = amount;
		logger.entering(_CLASS, "setAdjustmentAmount(double)");
	}

	// ***** adjustmentType
	public final AdjustmentType getAdjustmentType() {
		logger.entering(_CLASS, "getAdjustmentType()");

		// See CRM00066
		if ((getOriginZip().equalsIgnoreCase("96145")) || (getDestinationZip().equalsIgnoreCase("96145"))) {
			this.adjustmentType = AdjustmentType.Percentage;
		} // END if ((getOriginZip().equalsIgnoreCase("96145")) || ...

		logger.exiting(_CLASS, "getAdjustmentType()", this.adjustmentType);
		return this.adjustmentType;
	}

	public final void setAdjustmentType(AdjustmentType type) {
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

	// ***** contactCompanyName
	@Override
	public final String getContactCompanyName() {
		logger.entering(_CLASS, "getContactCompanyName()");
		logger.entering(_CLASS, "getContactCompanyName()", this.contactCompanyName);
		return this.contactCompanyName;
	}

	@Override
	public final void setContactCompanyName(String companyName) {
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
	public final String getContactEmail() {
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
	public final void setContactEmail(String contactEmail) {
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
	public final String getContactName() {
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
	public final void setContactName(String contactName) {
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
	public final String getContactPhone() {
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
	public final void setContactPhone(String contactPhone) {
		logger.entering(_CLASS, "setContactPhone(String)", contactPhone);
		this.contactPhone = defaultValue(contactPhone);
		logger.exiting(_CLASS, "setContactPhone(String)");
	}

	// ***** contactPhoneHidden
	public String getContactPhoneHidden() {
		logger.entering(_CLASS, "getContactPhoneHidden()");
		logger.exiting(_CLASS, "getContactPhoneHidden()", this.contactPhoneHidden);
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

	// ***** customerReferenceNote
	@Override
	public final String getCustomerReferenceNote() {
		logger.entering(_CLASS, "getCustomerReferenceNote()");
		logger.exiting(_CLASS, "getCustomerReferenceNote()", this.customerReferenceNote);
		return this.customerReferenceNote;
	}

	@Override
	public final void setCustomerReferenceNote(String note) {
		logger.entering(_CLASS, "setCustomerReferenceNote(String)", note);
		this.customerReferenceNote = defaultValue(note);
		logger.exiting(_CLASS, "setCustomerReferenceNote(String)");
	}

	// ***** deductible
	@Override
	public final double getDeductible() {
		logger.entering(_CLASS, "getDeductible()");
		logger.exiting(_CLASS, "getDeductible()", this.deductible);
		return this.deductible;
	}

	@Override
	public final void setDeductible(double amount) {
		logger.entering(_CLASS, "setDeductible(double)", amount);
		this.deductible = amount;
		logger.exiting(_CLASS, "setDeductible(double)");
	}

	// ***** destinationZip
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wildstartech.servicedesk.QuickQuote#getDestinationZip()
	 */
	@Override
	public final String getDestinationZip() {
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
	public final void setDestinationZip(String destinationZip) {
		logger.entering(_CLASS, "setDestinationZip(String)", destinationZip);
		this.destinationZip = defaultValue(destinationZip);
		logger.exiting(_CLASS, "setDestinationZip(String)");
	}

	// *****
	@Override
	public final double getDistance() {
		logger.entering(_CLASS, "getDistance()");
		logger.exiting(_CLASS, "getDistance()", this.distance);
		return this.distance;
	}

	@Override
	public final void setDistance(double distance) {
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
	public final double getFuelSurcharge() {
		logger.entering(_CLASS, "getFuelSurcharge()");
		logger.exiting(_CLASS, "getFuelSurcharge()", this.fuelSurcharge);
		return this.fuelSurcharge;
	}

	@Override
	public final void setFuelSurcharge(double fuelSurcharge) {
		logger.entering(_CLASS, "getFuelSurcharge()", fuelSurcharge);
		if (fuelSurcharge < 0) {
			this.fuelSurcharge = 0;
		} else {
			this.fuelSurcharge = fuelSurcharge;
		} // END if (fuelSurcharge < 0)
		logger.exiting(_CLASS, "getFuelSurcharge()");
	}

	// ***** insuranceCost
	@Override
	public final double getInsuranceCharges() {
		logger.entering(_CLASS, "getInsuranceCharges()");
		logger.exiting(_CLASS, "getInsuranceCharges()", this.insuranceCharges);
		return this.insuranceCharges;
	}

	@Override
	public final void setInsuranceCharges(double charges) {
		logger.entering(_CLASS, "setInsuranceCharges(double)", charges);
		if (charges < 0) {
			this.insuranceCharges = 0;
		} else {
			this.insuranceCharges = charges;
		} // END if (charges < 0)
		logger.exiting(_CLASS, "setInsuranceCharges(double)");
	}

	// ***** journalEntry
	@Override
	public final JournalEntry getNewJournalEntry() {
		logger.entering(_CLASS, "getNewJournalEntry()");
		if (this.newJournalEntry == null) {
			this.newJournalEntry = new PersistentJournalEntryImpl();
		} // END if (this.newJournalEntry == null)
		logger.exiting(_CLASS, "getNewJournalEntry()", this.newJournalEntry);
		return this.newJournalEntry;
	}

	@Override
	public final void setNewJournalEntry(JournalEntry entry) {
		logger.entering(_CLASS, "setNewJournalEntry(JournalEntry)", entry);
		if (entry != null) {
			if (entry instanceof PersistentJournalEntryImpl) {
				this.newJournalEntry = (PersistentJournalEntryImpl) entry;
			} else {
				this.newJournalEntry = new PersistentJournalEntryImpl();
				this.newJournalEntry.populateFromObject(entry);
			} // END if (entry instanceof PersistentJournalEntryImpl)
		} else {
			this.newJournalEntry = new PersistentJournalEntryImpl();
		} // END if (entry != null)
		logger.exiting(_CLASS, "setNewJournalEntry(JournalEntry)");
	}

	public final List<PersistentJournalEntry> getJournalEntries(UserContext ctx) {
		logger.entering(_CLASS, "getJournalEntries()");
		JournalDAOImpl dao = null;
		List<PersistentJournalEntry> journalEntries = null;
		String identifier = null;
		StringBuilder msg = null;

		if (ctx != null) {
			identifier = getIdentifier();
			if (!isEmpty(identifier)) {
				dao = new JournalDAOImpl();
				journalEntries = dao.findEntries(getKind(), identifier, ctx);
			} else {
				msg = new StringBuilder(80);
				msg.append("Journal entries are not available until after the ");
				msg.append("Quote has been saved the firs time.");
				logger.warning(msg.toString());
			} // END if (!isEmpty(requestId))
		} else {
			logger.warning("The UserContext parameter was null.");
		} // END if (ctx != null)

		// We must make sure that we DO NOT return an empty list.
		if (journalEntries == null) {
			journalEntries = new ArrayList<PersistentJournalEntry>();
		} // END if (journalEntries == null)

		logger.exiting(_CLASS, "getJournalEntries()", journalEntries);
		return journalEntries;
	}

	// ***** orderType
	public final String getOrderType() {
		logger.entering(_CLASS, "getOrderType()");
		logger.exiting(_CLASS, "getOrderType()", this.orderType);
		return this.orderType;
	}

	public final void setOrderType(String orderType) {
		logger.entering(_CLASS, "setOrderType(String)", orderType);
		this.orderType = defaultValue(orderType);
		logger.exiting(_CLASS, "setOrderType(String)");
	}

	// ***** originZip
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wildstartech.servicedesk.QuickQuote#getOriginZip()
	 */
	@Override
	public final String getOriginZip() {
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
	public final void setOriginZip(String originZip) {
		logger.entering(_CLASS, "setOriginZip(String)", originZip);
		this.originZip = defaultValue(originZip);
		logger.exiting(_CLASS, "setOriginZip()");
	}

	// ***** priceModel
	public final PriceModel getPriceModel() {
		logger.entering(_CLASS, "getPriceModel()");
		PriceModelFactory pmFactory = null;
		if (this.priceModel == null) {
			/*
			 * If the priceModel has NOT yet been specified, the system will
			 * obtain a reference from the PriceModelFactory.
			 */
			pmFactory = PriceModelFactory.getInstance();
			/* Store a local reference to the PriceModel */
			this.priceModel = pmFactory.getModel(this);
		} // END if (this.priceModel == null)
		logger.exiting(_CLASS, "getPriceModel()", this.priceModel);
		return this.priceModel;
	}

	public final void setPriceModel(PriceModel model) {
		logger.entering(_CLASS, "setPriceModel(PriceModel)", model);
		this.priceModel = model;
		logger.exiting(_CLASS, "setPriceModel(PriceModel)");
	}
	
	// ***** quoteMethod
	@Override
	public final QuoteMethod getQuoteMethod() {
	   logger.entering(_CLASS, "getQuoteMethod()");
	   logger.exiting(_CLASS, "getQuoteMethod()",this.quoteMethod);
	   return this.quoteMethod;
	}
	
	@Override
	public final String getQuoteMethodLabel() {
	   logger.entering(_CLASS, "getQuoteMethodLabel()");
	   String label=null;
	   if ((this.quoteMethod != null) && (this.quoteMethod == QuoteMethod.ByWeight)) {
	      label="By Weight";
	   } else {
	      label="By Cube";
	   } // END if ((this.quoteMethod != null) 	      
	   logger.exiting(_CLASS, "getQuoteMethodLabel()",label);
	   return label;
	}
	
	@Override
	public final void setQuoteMethod(QuoteMethod method) {
	   logger.entering(_CLASS, "setQuoteMethod(QuoteMethod)",method);
	   if (method != null) {
	      this.quoteMethod=method;
	   } else {
	      this.quoteMethod=PersistentQuickQuoteImpl._QuoteMethodDefault;
	   } // END if (method != null)
	}
	
	@Override
	public final void setQuoteMethod(String method) {
	   logger.entering(_CLASS,"setQuoteMethod(String)",method);
	   if ((method != null) && (method.equals("By Weight"))) {
	      this.quoteMethod=QuoteMethod.ByWeight;
	   } else {
	      this.quoteMethod=QuoteMethod.ByCube;
	   } // if ((method != null) && (method.equals("By Weight"))
	   logger.exiting(_CLASS, "setQuoteMethod(String");
	}

	// ***** serviceLevel
	@Override
	public final String getServiceLevel() {
		logger.entering(_CLASS, "getServiceLeve()");
		logger.exiting(_CLASS, "getServiceLeve()", this.serviceLevel);
		return this.serviceLevel;
	}

	@Override
	public final void setServiceLevel(String serviceLevel) {
		logger.entering(_CLASS, "setServiceLevel(String)", serviceLevel);
		this.serviceLevel = defaultValue(serviceLevel);
		logger.exiting(_CLASS, "setServiceLevel(String)", serviceLevel);
	}

	public List<String> getAvailableServiceLevels() {
		logger.entering(_CLASS, "getAvailableServiceLevels()");
		logger.exiting(_CLASS, "getAvailableServiceLevels()", PersistentQuickQuoteImpl.serviceLevels);
		return PersistentQuickQuoteImpl.serviceLevels;
	}

	// ***** totalCubes
	@Override
	public int getTotalCubes() {
		logger.entering(_CLASS, "getTotalCubes()");
		logger.exiting(_CLASS, "getTotalCubes()", this.totalCubes);
		return this.totalCubes;
	}

	public void setTotalCubes(int totalCubes) {
		logger.entering(_CLASS, "setTotalCubes(int)", totalCubes);
		this.totalCubes = totalCubes;
		logger.exiting(_CLASS, "setTotalCubes(int)");
	}

	// ***** totalWeight
	@Override
	public int getTotalWeight() {
		logger.entering(_CLASS, "getTotalWeight()");
		logger.exiting(_CLASS, "getTotalWeight()", this.totalWeight);
		return this.totalWeight;
	}

	public void setTotalWeight(int totalWeight) {
		logger.entering(_CLASS, "setTotalWeight(int)", totalWeight);
		this.totalWeight = totalWeight;
		logger.exiting(_CLASS, "setTotalWeight(int)");
	}

	// ***** valuation
	@Override
	public final double getValuation() {
		logger.entering(_CLASS, "getValuation()");
		logger.exiting(_CLASS, "getValuation()", this.valuation);
		return this.valuation;
	}

	@Override
	public final  void setValuation(double amount) {
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
	public final boolean isValuationDeclined() {
		logger.entering(_CLASS, "isValuationDeclined()");
		logger.exiting(_CLASS, "isValuationDeclined()", this.valuationDeclined);
		return this.valuationDeclined;
	}

	@Override
	public final void setValuationDeclined(boolean value) {
		logger.entering(_CLASS, "setValuationDeclined(boolean)", value);
		this.valuationDeclined = value;
		logger.exiting(_CLASS, "setValuationDeclined(boolean)");
	}
}