package com.wildstartech.gae.wfa.dao.logistics.ltl.quote;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.wildstartech.wfa.dao.logistics.ltl.quote.PersistentSimpleQuote;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.quote.SimpleQuote;

public class PersistentSimpleQuoteImpl 
extends PersistentQuickQuoteImpl 
implements PersistentSimpleQuote {
	/** Used in object serialization. */
	private static final long serialVersionUID = -4516673322889029910L;
	private static final String _CLASS = PersistentSimpleQuoteImpl.class.getName();
	private static Logger logger = Logger.getLogger(_CLASS);
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

	private boolean assemblyRequired = false;
	private boolean blanketWrapRequired = true;
	private boolean cratingRequired = false;
	private boolean packagingRequired = false;
	private boolean stairCarry = false;
	private boolean unpackagingRequired = false;
	private boolean destinationResidential = false;
	private boolean originResidential = false;
	private int numberOfFlights;
	private String destinationCity = "";
	private String destinationCompanyName = "";
	private String destinationContactEmail = "";
	private String destinationContactName = "";
	private String destinationContactPhone = "";
	private String destinationState = "";
	private String destinationStreetAddress = "";
	private String notes = "";
	private String originCity = "";
	private String originCompanyName = "";
	private String originContactEmail = "";
	private String originContactName = "";
	private String originContactPhone = "";
	private String originState = "";
	private String originStreetAddress = "";
	private String purchaseOrderNumber = "";
	private String referralOther = "";
	private String referralSource = "";

	/**
	 * Default, no-argument constructor.
	 */
	public PersistentSimpleQuoteImpl() {
		super();
		logger.entering(_CLASS, "PersistentSimpleQuoteImpl()");
		logger.exiting(_CLASS, "PersistentSimpleQuoteImpl()");
	}

	// ********** Utility Methods
	protected void populateEntity(Entity entity) {
		logger.entering(_CLASS, "populateEntity(Entity)",entity);
		String tmpStr=null;
		
		if (entity != null) {
			super.populateEntity(entity);
			/// assemblyRequired
			entity.setProperty("assemblyRequired", isAssemblyRequired());
			// blanketWrapRequired
			entity.setProperty("blanketWrapRequired", isBlanketWrapRequired());
			// cratingRequired
			entity.setProperty("cratingRequired", isCratingRequired());
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
			// destinationStreetAddress
			entity.setProperty("destinationStreetAddress", 
					getDestinationStreetAddress());
			// destinationState
			entity.setProperty("destinationState", getDestinationState());
			// notes
			/*
			 * Datastore entities can store up to 500 characters of text.
			 * Anything larger must be stored as a Text object and is NOT 
			 * indexable for query purposes.
			 */
			tmpStr=getNotes();
			if (tmpStr == null) {
				tmpStr="";
			} // END if (tmpStr == null)		
			if (tmpStr.length() <= 500) {
				entity.setProperty("notes", tmpStr);
			   
			} else {
				entity.setProperty("notes", new Text(tmpStr));
			} // END if (tmpStr.length() <= 500
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
			entity.setProperty("originResidential", isOriginResidential());
			// originState
			entity.setProperty("originState", getOriginState());
			// originStreetAddress
			entity.setProperty("originStreetAddress", getOriginStreetAddress());
			// packagingRequired
			entity.setProperty("packagingRequired", isPackagingRequired());
			// purchaseOrderNumber
			entity.setProperty("purchaseOrderNumber", getPurchaseOrderNumber());
			// referralOther
			entity.setProperty("referralOther", getReferralOther());
			// referralSource
			entity.setProperty("referralSource", getReferralSource());
			// stairCarry
			entity.setProperty("stairCarry", isStairCarry());
			// unpackagingRequired
			entity.setProperty("unpackagingRequired", isUnpackagingRequired());
		} else {
			logger.warning("Entity parameter is null.");
		} // END if (entity != null)
		logger.exiting(_CLASS, "populateEntity(Entity)");		
	}
	@Override
	public void populateFromEntity(Entity entity, UserContext ctx) {
		logger.entering(_CLASS, "populateFromEntity(Entity)", 
				new Object[] { entity, ctx });
		if (entity != null) {
			super.populateFromEntity(entity, ctx);
			// assemblyRequired
			setAssemblyRequired(
					getPropertyAsBoolean(entity,"assemblyRequired"));
			// blanketWrapRequired
			setBlanketWrapRequired(
					getPropertyAsBoolean(entity,"blanketWrapRequired"));
			// cratingRequired
			setCratingRequired(getPropertyAsBoolean(entity,"cratingRequired"));
			// destinationCity
			setDestinationCity(getPropertyAsString(entity,"destinationCity"));
			// destinationCompanyName
			setDestinationCompanyName(
					getPropertyAsString(entity,"destinationCompanyName"));
			// destinationContactEmail
			setDestinationContactEmail(
			      getPropertyAsString(entity,"destinationContactEmail"));
			// destinationContactName
			setDestinationContactName(
					getPropertyAsString(entity,"destinationContactName"));
			// destinationContactPhone
			setDestinationContactPhone(
					getPropertyAsString(entity,"destinationContactPhone"));
			// destinationResidential
			setDestinationResidential(
					getPropertyAsBoolean(entity,"destinationResidential"));
			// destinationState
			setDestinationState(getPropertyAsString(entity,"destinationState"));
			// destinationStreetAddress
			setDestinationStreetAddress(
					getPropertyAsString(entity,"destinationStreetAddress"));
			// notes
			setNotes(getPropertyAsString(entity,"notes"));
			// numberOfFlights
			setNumberOfFlights(getPropertyAsInteger(entity,"numberOfFlights"));
			// originCity
			setOriginCity(getPropertyAsString(entity,"originCity"));
			// originCompanyName
			setOriginCompanyName(
					getPropertyAsString(entity,"originCompanyName"));
			// originContactEmail
			setOriginContactEmail(
					getPropertyAsString(entity,"originContactEmail"));
			// originContactName
			setOriginContactName(
					getPropertyAsString(entity,"originContactName"));
			// originContactPhone
			setOriginContactPhone(
					getPropertyAsString(entity,"originContactPhone"));
			// originResidential
			setOriginResidential(
					getPropertyAsBoolean(entity,"originResidential"));
			// originState
			setOriginState(getPropertyAsString(entity,"originState"));		
			// originStreetAddress
			setOriginStreetAddress(
					getPropertyAsString(entity,"originStreetAddress"));
			// packagingRequired
			setPackagingRequired(
					getPropertyAsBoolean(entity,"packagingRequired"));
			// purchaseOrderNumber
			setPurchaseOrderNumber(
					getPropertyAsString(entity,"purchaseOrderNumber"));		
			// referralSource
			setReferralSource(getPropertyAsString(entity,"referralSource"));		
			// referralOther
			setReferralOther(getPropertyAsString(entity,"referralOther"));	
			// stairCarry
			setStairCarry(getPropertyAsBoolean(entity,"stairCarry"));
			// unpackagingRequired
			setUnpackagingRequired(
					getPropertyAsBoolean(entity,"unpackagingRequired"));
		} else {
			logger.warning("Entity parameter is null.");
		} // END if (entity != null)
		logger.exiting(_CLASS, "populateFromEntity(Entity)");
	}
	
	public void populateFromObject(SimpleQuote quote) {
		logger.entering(_CLASS, "populateFromObject(SimpleQuote)");
		if (quote != null) {
			super.populateFromObject(quote);
			setAssemblyRequired(quote.isAssemblyRequired());
			setBlanketWrapRequired(quote.isBlanketWrapRequired());
			setCratingRequired(quote.isCratingRequired());
			setDestinationCity(quote.getDestinationCity());
			setDestinationCompanyName(quote.getDestinationCompanyName());
			setDestinationContactEmail(quote.getDestinationContactEmail());
			setDestinationContactName(quote.getDestinationContactName());
			setDestinationContactPhone(quote.getDestinationContactPhone());
			setDestinationResidential(quote.isDestinationResidential());
			setDestinationState(quote.getDestinationState());
			setDestinationStreetAddress(quote.getDestinationStreetAddress());
			setNotes(quote.getNotes());
			setNumberOfFlights(quote.getNumberOfFlights());
			setOriginCity(quote.getOriginCity());
			setOriginCompanyName(quote.getOriginCompanyName());
			setOriginContactEmail(quote.getOriginContactEmail());
			setOriginContactName(quote.getOriginContactName());
			setOriginContactPhone(quote.getOriginContactPhone());
			setOriginResidential(quote.isOriginResidential());
			setOriginState(quote.getOriginState());
			setOriginStreetAddress(quote.getOriginStreetAddress());
			setPackagingRequired(quote.isPackagingRequired());
			setPurchaseOrderNumber(quote.getPurchaseOrderNumber());
			setReferralSource(quote.getReferralSource());
			setReferralOther(quote.getReferralOther());
			setStairCarry(quote.isStairCarry());
			setUnpackagingRequired(quote.isUnpackagingRequired());			
		} else {
			logger.warning("Quote parameter is null.");
		} // END if (quote != null)
			
	}
	
	/**
	 * Returns a representation of the properties associated with the quote for
	 * inclusion in a toString() method call.
	 * 
	 */
	@Override
	public String toPropertyString() {
		logger.entering(_CLASS, "toPropertyString()");
		NumberFormat cFmt = null;
		String result = null;
		StringBuilder sb = null;

		sb = new StringBuilder(2048);
		cFmt = NumberFormat.getCurrencyInstance();
		sb.append(super.toPropertyString());
		if (sb.length() > 0) {
			sb.append(", ");
		} // END if (sb.length() > 0)
		sb.append(", assemblyRequired=").append(isAssemblyRequired());
		sb.append(", blanketWrapRequired=").append(isBlanketWrapRequired());
		sb.append(", contactCompanyName=").append(getContactName());
		sb.append(", contactName=").append(getContactName());
		sb.append(", contactPhone=").append(getContactPhone());
		sb.append(", cratingRequired=").append(isCratingRequired());
		sb.append(", destinationCity=").append(getDestinationCity());
		sb.append(", destinationCompanyName=").append(getDestinationCompanyName());
		sb.append(", destinationContactEmail=").append(getDestinationContactEmail());
		sb.append(", destinationContactName=").append(getDestinationContactName());
		sb.append(", destinationContactPhone=").append(getDestinationContactPhone());
		sb.append(", destinationState=").append(getDestinationState());
		sb.append(", destinationStreetAddress=").append(getDestinationStreetAddress());
		sb.append(", notes=").append(getNotes());
		sb.append(", numberOfFlights=").append(getNumberOfFlights());
		sb.append(", originCity=").append(getOriginCity());
		sb.append(", originCompanyName=").append(getOriginCompanyName());
		sb.append(", originContactEmail=").append(getOriginContactEmail());
		sb.append(", originContactName=").append(getOriginContactName());
		sb.append(", originContactPhone=").append(getOriginContactPhone());
		sb.append(", originState=").append(getOriginState());
		sb.append(", originStreetAddress=").append(getOriginStreetAddress());
		sb.append(", originZip=").append(getOriginZip());
		sb.append(", purchaseOrderNumber=").append(getPurchaseOrderNumber());
		sb.append(", referralOther=").append(getReferralOther());
		sb.append(", referralSource=").append(getReferralSource());
		sb.append(", stairCarry=").append(isStairCarry());
		sb.append(", unpackagingRequired=").append(isUnpackagingRequired());
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
	
	public void updateFromObject(SimpleQuote quote) {
		logger.entering(_CLASS, "updateFromObject(SimpleQuote)",quote);
		if (quote != null) {
			super.updateFromObject(quote);
			setAssemblyRequired(quote.isAssemblyRequired());
			setBlanketWrapRequired(quote.isBlanketWrapRequired());
			setCratingRequired(quote.isCratingRequired());
			setDestinationCity(quote.getDestinationCity());
			setDestinationCompanyName(quote.getDestinationCompanyName());
			setDestinationContactEmail(quote.getDestinationContactEmail());
			setDestinationContactName(quote.getDestinationContactName());
			setDestinationContactPhone(quote.getDestinationContactPhone());
			setDestinationResidential(quote.isDestinationResidential());
			setDestinationState(quote.getDestinationState());
			setDestinationStreetAddress(quote.getDestinationStreetAddress());
			setNotes(quote.getNotes());
			setNumberOfFlights(quote.getNumberOfFlights());
			setOriginCity(quote.getOriginCity());
			setOriginCompanyName(quote.getOriginCompanyName());
			setOriginContactEmail(quote.getOriginContactEmail());
			setOriginContactPhone(quote.getOriginContactPhone());
			setOriginResidential(quote.isOriginResidential());
			setOriginState(quote.getOriginState());
			setOriginStreetAddress(quote.getOriginStreetAddress());
			setPackagingRequired(quote.isPackagingRequired());
			setPurchaseOrderNumber(quote.getPurchaseOrderNumber());
			setReferralSource(quote.getReferralSource());
			setReferralOther(quote.getReferralOther());
			setStairCarry(quote.isStairCarry());
			setUnpackagingRequired(quote.isUnpackagingRequired());
		} else {
			logger.warning("quote parameter is null.");
		} // END if (quote != null)		
		logger.exiting(_CLASS, "updateFromObject(SimpleQuote)");
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
		logger.exiting(_CLASS, "getDestinationCompanyName()", this.destinationCompanyName);
		return this.destinationCompanyName;
	}

	@Override
	public void setDestinationCompanyName(String destinationCompanyName) {
		logger.entering(_CLASS, "setDestinationCompanyName(String)", destinationCompanyName);
		this.destinationCompanyName = defaultValue(destinationCompanyName);
		logger.exiting(_CLASS, "setDestinationCompanyName(String)");
	}

	// ***** destinationContactEmail
	@Override
	public String getDestinationContactEmail() {
		logger.entering(_CLASS, "getDestinationContactEmail()");
		logger.exiting(_CLASS, "getDestinationContactEmail()", this.destinationContactEmail);
		return this.destinationContactEmail;
	}

	@Override
	public void setDestinationContactEmail(String destinationContactEmail) {
		logger.entering(_CLASS, "setDestinationContactEmail(String)", destinationContactEmail);
		this.destinationContactEmail = defaultValue(destinationContactEmail);
		logger.exiting(_CLASS, "setDestinationContactEmail(String)");
	}

	// ***** destinationContactName
	@Override
	public String getDestinationContactName() {
		logger.entering(_CLASS, "getDestinationContactName()");
		logger.exiting(_CLASS, "getDestinationContactName()", this.destinationContactName);
		return this.destinationContactName;
	}

	@Override
	public void setDestinationContactName(String destinationContactName) {
		logger.entering(_CLASS, "setDestinationContactName(String)", destinationContactName);
		this.destinationContactName = defaultValue(destinationContactName);
		logger.exiting(_CLASS, "setDestinationContactName(String)");
	}

	// ***** destinationContactPhone
	@Override
	public String getDestinationContactPhone() {
		logger.entering(_CLASS, "getDestinationContactPhone()");
		logger.exiting(_CLASS, "getDestinationContactPhone()", this.destinationContactPhone);
		return this.destinationContactPhone;
	}

	@Override
	public void setDestinationContactPhone(String destinationContactPhone) {
		logger.entering(_CLASS, "setDestinationContactPhone(String)", destinationContactPhone);
		this.destinationContactPhone = defaultValue(destinationContactPhone);
		logger.exiting(_CLASS, "setDestinationContactPhone(String)");
	}

	// ***** destinationResidential
	public boolean isDestinationResidential() {
		logger.entering(_CLASS, "isDestinationResidential()");
		logger.exiting(_CLASS, "isDestinationResidential()", this.destinationResidential);
		return this.destinationResidential;
	}

	public void setDestinationResidential(boolean residential) {
		logger.entering(_CLASS, "setDestinationResidential(boolean residential)", residential);
		this.destinationResidential = residential;
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
		logger.exiting(_CLASS, "getDestinationStreetAddress()", this.destinationStreetAddress);
		return this.destinationStreetAddress;
	}

	@Override
	public void setDestinationStreetAddress(String destinationStreetAddress) {
		logger.entering(_CLASS, "setDestinationStreetAddress(String)", destinationStreetAddress);
		this.destinationStreetAddress = defaultValue(destinationStreetAddress);
		logger.exiting(_CLASS, "setDestinationStreetAddress(String)");
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
		logger.entering(_CLASS, "setOriginCompanyName(String)", originCompanyName);
		this.originCompanyName = defaultValue(originCompanyName);
		logger.exiting(_CLASS, "setOriginCompanyName(String)");
	}

	// ***** originContactEmail
	@Override
	public String getOriginContactEmail() {
		logger.entering(_CLASS, "getOriginContactEmail()");
		logger.exiting(_CLASS, "getOriginContactEmail()", this.originContactEmail);
		return this.originContactEmail;
	}

	@Override
	public void setOriginContactEmail(String originContactEmail) {
		logger.entering(_CLASS, "setOriginContactEmail(String)", originContactEmail);
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
		logger.entering(_CLASS, "setOriginContactName(String)", originContactName);
		this.originContactName = defaultValue(originContactName);
		logger.exiting(_CLASS, "setOriginContactName(String)");
	}

	// ***** originContactPhone
	@Override
	public String getOriginContactPhone() {
		logger.entering(_CLASS, "getOriginContactPhone()");
		logger.exiting(_CLASS, "getOriginContactPhone()", this.originContactPhone);
		return this.originContactPhone;
	}

	@Override
	public void setOriginContactPhone(String originContactPhone) {
		logger.entering(_CLASS, "setOriginContactPhone(String)", originContactPhone);
		this.originContactPhone = defaultValue(originContactPhone);
		logger.exiting(_CLASS, "setOriginContactPhone(String)");
	}

	// ***** originResidential
	@Override
	public boolean isOriginResidential() {
		logger.entering(_CLASS, "isOriginResidential()");
		logger.exiting(_CLASS, "isOriginResidential()", this.originResidential);
		return this.originResidential;
	}

	@Override
	public void setOriginResidential(boolean residential) {
		logger.entering(_CLASS, "setOriginResidential(boolean residential)", residential);
		this.originResidential = residential;
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
		logger.exiting(_CLASS, "getOriginStreetAddress()", this.originStreetAddress);
		return this.originStreetAddress;
	}

	@Override
	public void setOriginStreetAddress(String originStreetAddress) {
		logger.entering(_CLASS, "setOriginStreetAddress(String)", originStreetAddress);
		this.originStreetAddress = defaultValue(originStreetAddress);
		logger.exiting(_CLASS, "setOriginStreetAddress(String)");
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

	// ***** purchaseOrderNumber
	@Override
	public String getPurchaseOrderNumber() {
		logger.entering(_CLASS, "getPurchaseOrderNumber()");
		logger.exiting(_CLASS, "getPurchaseOrderNumber()", this.purchaseOrderNumber);
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
		logger.exiting(_CLASS, "getReferralOptions()", PersistentSimpleQuoteImpl.referralOptions);
		return PersistentSimpleQuoteImpl.referralOptions;
	}

	// ***** referralInternet
	public List<String> getInternetOptions() {
		logger.entering(_CLASS, "getInternetOptions()");
		logger.exiting(_CLASS, "getInternetOptions()", PersistentSimpleQuoteImpl.internetOptions);
		return PersistentSimpleQuoteImpl.internetOptions;
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
		logger.exiting(_CLASS, "isUnpackagingRequired()", this.unpackagingRequired);
		return this.unpackagingRequired;
	}

	@Override
	public void setUnpackagingRequired(boolean unpackaging) {
		logger.entering(_CLASS, "setUnpackagingRequired(boolean)", unpackaging);
		this.unpackagingRequired = unpackaging;
		logger.exiting(_CLASS, "setUnpackagingRequired(boolean)");
	}
}