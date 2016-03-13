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
package com.wildstartech.gae.wfa.dao;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.WildObject;
/**
 * This object provides the base foundation for all entites that are stored
 * in a persistent data store using the Wildstar Foundation Architecture Data 
 * Access Object (DAO) design pattern.  This specific implementation of the 
 * @{code WildObject} interface is intended for use with the DataStore
 * framework made available through the Google App Engine platform.
 * 
 * <p>Each @{code Entity} in the Datastore should be represented by
 * a concrete subclass of the this class.  Subclasses should provide 
 * an implementation of the @{code getKind()} method which the
 * @{code WildDAOImpl} class will use to manage reading and writing
 * information out to Google's Datastore.</p>
 * 
 * <p>In addition, a concrete sub-class of this class should over-ride the
 * following two methods:</p>
 * <ul>
 * <li>@{code populateEntity(Entity)}</li>
 * <li>@{code populateFromEntity(Entity)}</li>
 * </ul>
 * <p>When a sub-class overrides these methods, the first line in their code
 * should be an invocation of the super class' version of the same method.  For
 * example:</p>
 * <blockquote>
 * @{code 
 * public void populateEntity(Entity entity) {
 *    super.populateEntity(entity);
 *    ...
 * }
 * }
 * </blockquote>
 * <p>or</p>
 * <blockquote>
 * @{code 
 * public void populateFromEntity(Entity entity) {
 *    super.populateFromEntity(entity);
 *    ...
 * }
 * }
 * </blockquote>
 * <p>In doing this, each class is only responsible for managing data that it
 * is aware of.  Inherited fields are managed by the super class.</p>
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 * @version 0.1, 2013.08.17 
 */
public abstract class WildObjectImpl<T> 
implements Serializable, WildObject {
	/** Used in object serialization. */
	private static final long serialVersionUID = 5495762087091689261L;
	private static final String _CLASS=WildObjectImpl.class.getName();
	private static final Logger logger=Logger.getLogger(_CLASS);
	
	/* The date/time the object was originally created. */
	private Date dateCreated=null;
	/* The date/time the object was last modified. */
	private Date dateModified=null;
	/* Name of the user who originally stored the object in the persistent 
	 * data store. */
	private String createdBy = "";
	/* Name of the user who last saved the object in the persistent 
	 * data store. */
	private String modifiedBy = "";
	/* The unique identifier of the persistent object. */
	private String identifier = "";
	/* The KIND of data stored in the datastore. */
	protected static String _KIND="com.wildstartech.wfa.dao.WildObject";
	
	
	/**
	 * Default, no-argument constructor.
	 * @author Derek Berube, Wildstar Technologies, LLC.
	 * @version 2013-08-17, 0.1
	 */
	protected WildObjectImpl() {
		logger.entering(_CLASS,"WildObjectImpl()");
		this.identifier=null;
		this.dateCreated=new Date();
		this.dateModified=new Date(this.dateCreated.getTime());
		this.createdBy=null;
		this.modifiedBy=null;	
		logger.exiting(_CLASS,"WildObjectImpl()");
	}
	
	
	/**
	 * Returns a formatted date or an empty string.
	 */
	public String getFormattedDate(Date date) {
	   logger.entering(_CLASS,"getFormattedDate(Date)",date);
	   DateFormat fmt=null;
	   String returnValue="";
	   if (date != null) {
	      fmt=getDateFormatter();
	      returnValue=fmt.format(date);
	   } // END if (date != null)
	   logger.entering(_CLASS,"getFormattedDate(Date)",returnValue);
	   return returnValue;
	}
	
	/**
	 * Returns a string representation consisting of object properties.
	 */
	public String toPropertyString() {
	   logger.entering(_CLASS, "toPropertyString()");
	   String returnValue=null;
	   StringBuilder sb=null;
	   
	   sb=new StringBuilder(1024);
	   sb.append("identifier=\"").append(getIdentifier()).append("\", ");
	   sb.append("createdBy=\"").append(getCreatedBy()).append("\", ");
	   sb.append("dateCreated=\"").append(
	         getFormattedDate(getDateCreated())).append("\", ");
	   sb.append("modifiedBy=\"").append(getModifiedBy()).append("\", ");
	   sb.append("dateModified=\"").append(
            getFormattedDate(getDateModified()));      
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
	  sb.append("WildObjectImpl[");
	  sb.append(toPropertyString());
	  sb.append("]");
     result=sb.toString();
	  logger.exiting(_CLASS,"toString()",result);
	  return result;
	}
	
	//********** Utility methods
	public DateFormat getDateFormatter() {
	   logger.entering(_CLASS, "getDateFormatter()");
	   DateFormat fmt=null;
	   fmt=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a zzz");
	   logger.exiting(_CLASS, "getDateFormatter()",fmt);
	   return fmt;
	}
	
	/**
    * Utility method to return whether or not the specified string value is a
    * {@code null} value or a zero-length {@code String}.
    * 
    * @param value The value to be tested.
    * @return {@code true} if the value passed is either {@code null} or a 
    * zero-length {@code String} otherwise, {@code false}.
    */
   public boolean isEmpty(String value) {
      logger.entering(_CLASS, "isEmpty(String)",value);
      boolean result=false;
      if ((value == null) || (value.length() == 0)) {
         result=true;
      } // END if ((value == null) || (value.length() == 0))
      logger.exiting(_CLASS, "isEmpty(String)",result);
      return result;
   }
   
	/**
	 * 
	 * @param entity
	 * @param propName
	 * @return
	 */
	private Object getProperty(Entity entity, String propName) {
	  logger.entering(_CLASS,"getProperty(Entity,String)",
          new Object[] {entity,propName});
	  Object obj=null;
	  StringBuilder sb=null;
	  if (entity == null) {
	    logger.warning("A null reference to an Entity object was passed.");
	  } else if (propName == null) {
	    logger.warning("A null reference to a property name was passed.");	    
	  } else if (propName.length() == 0) {
	    logger.warning("A zero length string was passed as a property.");
	  } else {
	    obj=entity.getProperty(propName);
	    if (obj == null) {
	      sb=new StringBuilder(80);
	      sb.append("The Entity did not contain a value for the \"");
	      sb.append(propName).append("\" property.");
	      logger.fine(sb.toString());
	    } // END if (obj == null) 
	  } // END if (entity == null)	    
	  logger.exiting(_CLASS,"getProperty(Entity,String)");
	  return obj;
	}
	/**
	 * Returns the specified property as a @{code boolean} value.
	 * 
	 * <p>The value returned will be false unless it is equal to the word
	 * "true" (case-insensitive without the quotes).</p>
	 * 
	 * @param entity The @{code Entity} object that contains the data 
     * which we are searching through.
     * @param propName The name of the property whose value should be returned
     * as a @{code boolean}
	 * @return The value stored in the identified property represented by a 
     * @{code boolean} primitive.
	 */
	protected boolean getPropertyAsBoolean(Entity entity, String propName) {
	  logger.entering(_CLASS,"getPropertyAsBoolean(Entity,String)",
	      new Object[] {entity, propName});
	  boolean result=false;
	  
	  result=getPropertyAsBoolean(entity,propName,false);
	  
	  logger.exiting(_CLASS, "getPropertyAsBoolean(Entity,String)", result);
	  return result;
	}
	/**
     * Returns the specified property as a @{code boolean} value.
     * 
     * <p>The value returned will be false unless it is equal to the word
     * "true" (case-insensitive without the quotes).</p>
     * 
     * @param entity The @{code Entity} object that contains the data 
     * which we are searching through.
     * @param propName The name of the property whose value should be returned
     * as a @{code boolean}
     * @param defaultValue The @{code boolean} value to be returned in the 
     * event the entity DOES NOT contain the specified property or the specified
     * property cannot be parsed as a @{code boolean} value.
     * @return The value stored in the identified property represented by a 
     * @{code boolean} primitive.
     */
	protected boolean getPropertyAsBoolean(
	    Entity entity, String propName, boolean defaultValue) {
	  logger.entering(_CLASS, "getPropertyAsBoolean(Entity,String,boolean)",
	      new Object[] {entity, propName, defaultValue});
	  boolean value=defaultValue;
	  Object obj=null;
	  
      obj=getProperty(entity,propName);
      if (obj != null) {
        if (obj instanceof Boolean) {
          value=(Boolean) obj;
        } else {
          logger.warning("The specified property is not a Boolean object.");
        } // END if (obj instanceof Boolean)
      } else {
        value=defaultValue;
      } // END if (obj != null)
      
	  logger.exiting(_CLASS," getPropertyAsBoolean(Entity,String,boolean)",
	      value);
	  return value;
	}
	/**
	 * Returns the value of the specified property as a @{code Date}.
	 * @param entity The @{code Entity} object that contains the data 
	 * which we are searching through.
	 * @param propName The name of the property whose value should be returned
	 * as a @{code Date}
	 * @return The value stored in the identified property represented by a 
	 * @{code java.util.Date} object.
	 */
	protected Date getPropertyAsDate(Entity entity, String propName) {
	  logger.entering(_CLASS, "getPropertyAsDate(Entity,String)",
	      new Object[] {entity, propName});
	  Date value=null;
	  value=getPropertyAsDate(entity,propName,null);
	  logger.exiting(_CLASS, "getPropertyAsDate(Entity,String)",value);
	  return value;
	}
	
	/**
	 * The specified property is recorded in the data store as a {@code String}
	 * formatted using the specified {@code DateFormat}; however, the goal is to
	 * have the value returned as a {@code Date}.
	 * 
	 * @param entity The {@code Entity} object that contains the data 
    * which we are searching through.
    * @param propName The name of the property whose value should be returned
    * as a @{code Date}
	 * @param format The $
	 * @return
	 */
	protected Date getPropertyAsFormattedDate(
	      Entity entity, 
	      String propName, 
	      DateFormat dateFormat) {
	   logger.entering(_CLASS, "getPropertyAsDate(Entity,String,DateFormat)",
	         new Object[] {entity, propName, dateFormat});
	   Date result=null;
	   Object obj=null;
	   String tmpStr=null;
	   
	   if (dateFormat != null) {
	      obj=getProperty(entity,propName);
	      if (obj != null) {
	         if (obj instanceof String) {
	            tmpStr=(String) obj;
	            try {
                  result=dateFormat.parse(tmpStr);
               } catch (ParseException ex) {
                  logger.log(
                     Level.SEVERE,
                     "Unable to parse the property using specified DateFormat.",
                     ex);
               } // END try/catch
	         } // END if (obj instanceof String)
	      } // END if (obj != null)
	   } // END if (dateFormat != null)
	   
	   logger.exiting(_CLASS, "getPropertyAsDate(Entity,String,DateFormat)",
	         result);
	   return result;
	}
	/**
	 * Returns the value of the specified property as a @{code Date}.
     * @param entity The @{code Entity} object that contains the data 
     * which we are searching through.
     * @param propName The name of the property whose value should be returned
     * as a @{code Date}
     * @param defaultDate The @{code Date} value to be returned in the 
     * event the entity DOES NOT contain the specified property or the specified
     * property cannot be parsed as a Date value.
	 * @return The value stored in the identified property represented by a 
     * @{code java.util.Date} object.
	 */
	protected Date getPropertyAsDate(
	    Entity entity, 
	    String propName, 
	    Date defaultDate) {
	  logger.entering(_CLASS, "getPropertyAsDate(Entity,String,Date)",
          new Object[] {entity, propName,defaultDate});
      Date value=null;
      Object obj=null;
      obj=getProperty(entity,propName);
      if (obj != null) {
        if (obj instanceof Date) {
          value=(Date) obj;
        } else {
          logger.warning("The specified property is not a Date object.");
          value=defaultDate;
        } // END if (obj instanceof Date)
      } else {
        value=defaultDate;
      } // END if (obj != null)
      logger.exiting(_CLASS, "getPropertyAsDate(Entity,String)",value);
      return value;	  
	}
	/**
     * Returns the value of the specified property as an @{code double}.
     * 
     * <p>If the property is null or contains an unparseable double, this 
     * method will return the value of @{code Double.MIN_VALUE}.</p>
     * @param entity The @{code Entity} object that contains the data which
     * we are searching through.
     * @param propName The name of the property whose value we want to return as
     * an @{code double}
     * @return The value stored in the identified property as represented by a 
     * @{code double}.
     */
    protected double getPropertyAsDouble(Entity entity, String propName) {
      logger.entering(_CLASS,"getPropertyAsDouble(Entity,String)",
          new Object[] {entity,propName});
      double value= Long.MIN_VALUE;
      value=getPropertyAsDouble(entity,propName,value);
      logger.entering(_CLASS,"getPropertyAsDouble(Entity,String)",value);
      return value;
    }
	/**
     * Returns the value of the specified property as an @{code double}.
     * 
     * <p>If the property is null or contains an unparseable double, this 
     * method will return the value of @{code Double.MIN_VALUE}.</p>
     * @param entity The @{code Entity} object that contains the data which
     * we are searching through.
     * @param propName The name of the property whose value we want to return as
     * an @{code double}
     * @param defaultValue The @{code double} value to be returned in the 
     * event the entity DOES NOT contain the specified property or the specified
     * property cannot be parsed as a double value.
     * @return The value stored in the identified property as represented by a 
     * @{code double}.
     */
    protected double getPropertyAsDouble(Entity entity, String propName, double defaultValue) {
      logger.entering(_CLASS,"getPropertyAsDouble(Entity,String,double)",
          new Object[] {entity,propName,defaultValue});
      double value=Long.MIN_VALUE;
      Object obj=null;
      
      obj=getProperty(entity,propName);
      if (obj != null) {
        if (
              (obj instanceof Double) || 
              (obj instanceof Float) || 
              (obj instanceof Integer) || 
              (obj instanceof Long) || 
              (obj instanceof Short)
          ) {
          value=((Number) obj).doubleValue();          
        } else if (obj instanceof String) {
          try {
            value=Double.parseDouble((String) obj);
          } catch (NumberFormatException ex) {
            logger.warning("String value could not be parsed as a Double.");
          } // END try/catch          
        } // END if (obj instanceof ...
      } else {
        if (logger.isLoggable(Level.FINEST)) {
          logger.finest("No entity property labeled, "+propName+" was found.");
        } // END if (logger.isLoggable(Level.FINEST))
      } // END if (obj != null)   
      logger.exiting(_CLASS,"getPropertyAsDouble(Entity,String)",value);
      return value;
    }
    /**
     * Returns the value of the specified property as an @{code float}.
     * 
     * <p>If the property is null or contains an unparseable float, this 
     * method will return the value of @{code Float.MIN_VALUE}.</p>
     * @param entity The @{code Entity} object that contains the data which
     * we are searching through.
     * @param propName The name of the property whose value we want to return as
     * an @{code float}
     * @return The value stored in the identified property as represented by a 
     * @{code float}.
     */
    protected float getPropertyAsFloat(Entity entity, String propName) {
      logger.entering(_CLASS,"getPropertyAsFloat(Entity,String)",
          new Object[] {entity,propName});
      float value=Long.MIN_VALUE;
      value=getPropertyAsFloat(entity,propName,value);
      logger.exiting(_CLASS,"getPropertyAsFloat(Entity,String)",value);
      return value;
    }
    /**
     * Returns the value of the specified property as an @{code float}.
     * 
     * <p>If the property is null or contains an unparseable float, this 
     * method will return the value of @{code Float.MIN_VALUE}.</p>
     * @param entity The @{code Entity} object that contains the data which
     * we are searching through.
     * @param propName The name of the property whose value we want to return as
     * an @{code float}
     * @param defaultValue The @{code float} value to be returned in the 
     * event the entity DOES NOT contain the specified property or the specified
     * property cannot be parsed as a float value.
     * @return The value stored in the identified property as represented by a 
     * @{code float}.
     */
    protected float getPropertyAsFloat(
        Entity entity, String propName, float defaultValue) {
      logger.entering(_CLASS,"getPropertyAsFloat(Entity,String,float)",
          new Object[] {entity,propName,defaultValue});
      float value=Long.MIN_VALUE;
      Object obj=null;
      
      obj=getProperty(entity,propName);
      if (obj != null) {
        if (
              (obj instanceof Double) || 
              (obj instanceof Float) || 
              (obj instanceof Integer) || 
              (obj instanceof Long) || 
              (obj instanceof Short)
          ) {
          value=((Number) obj).floatValue();
        } else if (obj instanceof String) {
          try {
            value=Float.parseFloat((String) obj);
          } catch (NumberFormatException ex) {
            logger.warning("String value could not be parsed as a Float.");            
          } // END try/catch          
        } // END if (obj instanceof ...
      } else {
        if (logger.isLoggable(Level.FINEST)) {
          logger.finest("No entity property labeled, "+propName+" was found.");
        } // END if (logger.isLoggable(Level.FINEST))
      } // END if (obj != null)      
      logger.exiting(_CLASS,"getPropertyAsFloat(Entity,String)",value);
      return value;
    }
    /**
     * Returns the value of the specified property as an @{code int}.
     * <p>If the property is null or contains an unparseable integer, this 
     * method will return the value of @{code Integer.MIN_VALUE}.</p>
     */
    protected int getPropertyAsInteger(Entity entity, String propName) {
      logger.entering(_CLASS,"getPropertyAsInteger(Entity,String)",
          new Object[] {entity,propName});
      int value=Integer.MIN_VALUE;
      
      value=getPropertyAsInteger(entity,propName,value);
      logger.exiting(_CLASS,"getPropertyAsInteger(Entity,String)",value);
      return value;
    }
	/**
	 * Returns the value of the specified property as an @{code int}.
	 * 
	 * <p>If the property is null or contains an unparseable integer, this 
	 * method will return the value specified for the @{code defaultValue}
	 * parameter.</p>
	 * 
     * @param entity The @{code Entity} object that contains the data which
     * we are searching through.
     * @param propName The name of the property whose value we want to return as
     * an @{code int}
     * @param defaultValue The @{code int} value to be returned in the 
     * event the entity DOES NOT contain the specified property or the specified
     * property cannot be parsed as a integer value.
     * @return The value stored in the identified property as represented by a 
     * @{code int}.
	 */
	protected int getPropertyAsInteger(
	    Entity entity, String propName, int defaultValue) {
	  logger.entering(_CLASS,"getPropertyAsInteger(Entity,String,int)",
          new Object[] {entity,propName,defaultValue});
	  int value=defaultValue;
	  Object obj=null;
	  
	  obj=getProperty(entity,propName);
	  if (obj != null) {
	    if (
	          (obj instanceof Double) || 
	          (obj instanceof Float) || 
	          (obj instanceof Integer) || 
              (obj instanceof Long) || 
	          (obj instanceof Short)
	      ) {
	      value=((Number) obj).intValue();
	    } else if (obj instanceof String) {
	      try {
            value=Integer.parseInt((String) obj);
          } catch (NumberFormatException ex) {
            logger.warning("String value could not be parsed as an Integer.");
          } // END try/catch
	    } // END if (obj instanceof ...
	  } else {
	    if (logger.isLoggable(Level.FINEST)) {
	      logger.finest("No entity property labeled, "+propName+" was found.");
	    } // END if (logger.isLoggable(Level.FINEST))
	  } // END if (obj != null)	  
	  logger.exiting(_CLASS,"getPropertyAsInteger(Entity,String)",value);
      return value;
	}
	/**
     * Returns the value of the specified property as an @{code long}.
     * 
     * <p>If the property is null or contains an unparseable long, this 
     * method will return the value of @{code Long.MIN_VALUE}.</p>
     * 
     * @param entity The @{code Entity} object that contains the data which
     * we are searching through.
     * @param propName The name of the property whose value we want to return as
     * an @{code long}
     * @return The value stored in the identified property as represented by a 
     * @{code long}.
     */
    protected long getPropertyAsLong(Entity entity, String propName) {
      logger.entering(_CLASS,"getPropertyAsLong(Entity,String)",
          new Object[] {entity,propName});
      long value=Long.MIN_VALUE;
      value=getPropertyAsLong(entity,propName,value);
      logger.entering(_CLASS,"getPropertyAsLong(Entity,String)",value);
      return value;
    }
	/**
     * Returns the value of the specified property as an @{code long}.
     * 
     * @param entity The @{code Entity} object that contains the data which
     * we are searching through.
     * @param propName The name of the property whose value we want to return as
     * an @{code long}
     * @param defaultValue The @{code long} value to be returned in the 
     * event the entity DOES NOT contain the specified property or the specified
     * property cannot be parsed as a long value.
     * @return The value stored in the identified property as represented by a 
     * @{code long}.
     */
    protected long getPropertyAsLong(
        Entity entity, String propName, long defaultValue) {
      logger.entering(_CLASS,"getPropertyAsLong(Entity,String)",
          new Object[] {entity,propName});
      long value=Long.MIN_VALUE;
      Object obj=null;
      
      value=defaultValue;
      obj=getProperty(entity,propName);
      if (obj != null) {
        if (
              (obj instanceof Double) || 
              (obj instanceof Float) || 
              (obj instanceof Integer) || 
              (obj instanceof Long) || 
              (obj instanceof Short)
          ) {
          value=((Number) obj).longValue();          
        } else if (obj instanceof String) {
          try {
            value=Long.parseLong((String)obj);
          } catch (NumberFormatException ex) {
            logger.warning("String value could not be parsed as an Long.");
          } // END try/catch
        } // END if (obj instanceof ...
      } // END if (obj != null)   
      logger.exiting(_CLASS,"getPropertyAsLong(Entity,String)",value);
      return value;
    }
    /**
     * Returns the value of the specified property as an @{code long}.
     * 
     * <p>If the property is null or contains an unparseable long, this 
     * method will return the value of @{code Long.MIN_VALUE}.</p>
     * @param entity The @{code Entity} object that contains the data which
     * we are searching through.
     * @param propName The name of the property whose value we want to return as
     * an @{code long}
     * @return The value stored in the identified property as represented by a 
     * @{code long}.
     */
    protected short getPropertyAsShort(Entity entity, String propName) {
      logger.entering(_CLASS,"getPropertyAsShort(Entity,String)",
          new Object[] {entity,propName});
      short value=Short.MIN_VALUE;
      value=getPropertyAsShort(entity,propName,value);
      logger.entering(_CLASS,"getPropertyAsShort(Entity,String)",value);
      return value;
    }
    /**
     * Returns the value of the specified property as an @{code long}.
     * 
     * <p>If the property is null or contains an unparseable long, this 
     * method will return the value of @{code Long.MIN_VALUE}.</p>
     * @param entity The @{code Entity} object that contains the data which
     * we are searching through.
     * @param propName The name of the property whose value we want to return as
     * an @{code long}
     * @param defaultValue The @{code short} value to be returned in the 
     * event the entity DOES NOT contain the specified property or the specified
     * property cannot be parsed as a short value.
     * @return The value stored in the identified property as represented by a 
     * @{code long}.
     */
    protected short getPropertyAsShort(
        Entity entity, String propName, short defaultValue) {
      logger.entering(_CLASS,"getPropertyAsShort(Entity,String)",
          new Object[] {entity,propName});
      short value=defaultValue;
      Object obj=null;
      
      obj=getProperty(entity,propName);
      if (obj != null) {
        if (
              (obj instanceof Double) || 
              (obj instanceof Float) || 
              (obj instanceof Integer) || 
              (obj instanceof Long) || 
              (obj instanceof Short)
          ) {
          value=((Number) obj).shortValue();          
        } else if (obj instanceof String) {
          try {
            value=Short.parseShort((String)obj);
          } catch (NumberFormatException ex) {
            logger.warning("String value could not be parsed as an Long.");
          } // END try/catch
        } // END if (obj instanceof ...
      } // END if (obj != null)   
      logger.exiting(_CLASS,"getPropertyAsShort(Entity,String)",value);
      return value;
    }
	/** 
	 * Returns the value of the specified property as a @{code String}.
	 * @param entity The @{code Entity} object that contains the data which
	 * we are searching through.
	 * @param propName The name of the property whose value we want to return as
	 * a String
	 * @return The value stored in the identified property as represented by a 
	 * String.
	 */
	protected String getPropertyAsString(Entity entity, String propName) {
	  logger.entering(_CLASS,"getPropertyAsString(Entity,String)",
	      new Object[] {entity,propName});
	  Object obj=null;
	  String value=null;
	  Text text=null;
	  obj=getProperty(entity,propName);
	  if (obj != null) {
	    if (obj instanceof String) {
	      value=(String) obj;
	    } else if (obj instanceof Text) {
	      text=(Text) obj;
	      value=text.getValue();
	    } else {
	      value=obj.toString();
	    } // END if (obj instanceof String) 
	  } // END if (obj != null)	 
	  logger.exiting(_CLASS,"getPropertyAsString(Entity,String)",value);
	  return value;
	}
	//********** Accessor Methods
	//***** createdBy
	public final String getCreatedBy() {
		logger.entering(_CLASS,"getCreatedBy()");
		logger.exiting(_CLASS,"getCreatedBy()",this.createdBy);
		return this.createdBy;
	}
	final void setCreatedBy(String userName) {
		logger.entering(_CLASS,"setCreatedBy(String)");
		this.createdBy=defaultValue(userName);
		logger.entering(_CLASS,"setCreatedBy(String)");
	}
	//***** dateCreated
	public final Date getDateCreated() {
		logger.entering(_CLASS,"getDateCreated()");
		logger.exiting(_CLASS,"getDateCreated()",this.dateCreated);
		return this.dateCreated;
	}
	final void setDateCreated(Date dateCreated) {
		logger.entering(_CLASS,"setDateCreated(Date)",dateCreated);
		this.dateCreated=dateCreated;
		logger.entering(_CLASS,"setDateCreated(Date)");
	}
	//***** dateModified
	public final Date getDateModified() {
		logger.entering(_CLASS,"getDateModified()");
		logger.exiting(_CLASS,"getDateModified()",this.dateModified);
		return this.dateModified;
	}
	final void setDateModified(Date dateModified) {
		logger.entering(_CLASS,"setDateModified(Date)",dateModified);
		this.dateModified=dateModified;
		logger.entering(_CLASS,"setDateModified(Date)");
	}
	//***** modifiedBy
	public final String getModifiedBy() {
		logger.entering(_CLASS,"getModifiedBy()");
		logger.exiting(_CLASS,"getModifiedBy()",this.modifiedBy);
		return this.modifiedBy;
	}
	final void setModifiedBy(String userName) {
		logger.entering(_CLASS,"setModifiedBy(String)");
		this.modifiedBy=defaultValue(userName);
		logger.entering(_CLASS,"setModifiedBy(String)");
	}
	//***** primaryKey
	/**
	 * Returns the primary key used by the 
	 */
	public final String getIdentifier() {
		logger.entering(_CLASS,"getIdentifier()");
		logger.exiting(_CLASS,"getIdentifier()",this.identifier);
		return this.identifier;
	}
	final void setIdentifier(String identifier) {
		logger.entering(_CLASS,"setIdentifier(String)");
		this.identifier=defaultValue(identifier);
		logger.entering(_CLASS,"setIdentifier(String)");
	}
	private final void setIdentifier(Key key) {
		logger.entering(_CLASS,"setIdentifier(Key key)",key);
		long keyValue=0l;
		
		if (key != null) {
			keyValue=key.getId();
			setIdentifier(String.valueOf(keyValue));
		} // END if (key != null)
		logger.exiting(_CLASS,"setIdentifier(Key key)");
	}
	//********** Utility Methods
	@SuppressWarnings("rawtypes")
   @Override
   public boolean equals(Object obj) {
	   logger.entering(_CLASS, "equals(Object)",obj);
	   boolean returnValue=false;
	   WildObjectImpl other=null;
	   
	   if (this == obj) {
         returnValue=true;
	   } else if ((obj != null) && (getClass() == obj.getClass())) {
	      other = (WildObjectImpl) obj;
	      if (createdBy == null) {
	         if (other.createdBy != null)
	            return false;
	      } else if (!createdBy.equals(other.createdBy))
	         return false;
	      if (dateCreated == null) {
	         if (other.dateCreated != null)
	            return false;
	      } else if (!dateCreated.equals(other.dateCreated))
	         return false;
	      if (dateModified == null) {
	         if (other.dateModified != null)
	            return false;
	      } else if (!dateModified.equals(other.dateModified))
	         return false;
	      if (identifier == null) {
	         if (other.identifier != null)
	            return false;
	      } else if (!identifier.equals(other.identifier))
	         return false;
	      if (modifiedBy == null) {
	         if (other.modifiedBy != null)
	            return false;
	      } else if (!modifiedBy.equals(other.modifiedBy))
	         return false;
	   } // END if (this == obj)
      return returnValue;
   }
	
	public Date defaultValue(Date value) {
	   logger.entering(_CLASS, "defaultValue(Date)",value);
	   Date returnValue=null;
	   returnValue=defaultValue(value,new Date());
	   logger.exiting(_CLASS, "defaultValue(Date)",returnValue);
	   return returnValue;
	}
	
	public Date defaultValue(Date value, Date defaultValue) {
	   logger.entering(_CLASS, "defaultValue(Date,Date)",value);
      Date returnValue=null;
      if (value == null) {
         if (defaultValue != null) {
            returnValue=defaultValue;
         } else {
            returnValue=new Date();
         } // END if (defaultValue != null) 
      } else {
         returnValue=value;
      } // END if (value == null)
      logger.exiting(_CLASS, "defaultValue(Date,Date)",returnValue);
      return returnValue;
	}
	
	public String defaultValue(String value) {
	  logger.entering(_CLASS,"defaultValue(String)",value);
	  String newValue=null;
	  newValue=defaultValue(value,"");
	  logger.exiting(_CLASS,"defaultValue(String)",newValue);
	  return newValue;
	}
	
	public String defaultValue(String value, String defaultValue) {
		logger.entering(_CLASS,"defaultValue(String,String)",
				new Object[] {value, defaultValue});
		String returnValue=defaultValue;
		
		if (value != null) {
			returnValue=value;
		} // END if (value != null)
		
		logger.entering(_CLASS,"defaultValue(String,String)",returnValue);
		return returnValue;
	}
	//***** hashCode
	@Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((createdBy == null) ? 0 : createdBy.hashCode());
      result = prime * result
            + ((dateCreated == null) ? 0 : dateCreated.hashCode());
      result = prime * result
            + ((dateModified == null) ? 0 : dateModified.hashCode());
      result = prime * result
            + ((identifier == null) ? 0 : identifier.hashCode());
      result = prime * result
            + ((modifiedBy == null) ? 0 : modifiedBy.hashCode());
      return result;
   }
	//***** kind
	/**
	 * Used by the DAO to identify the type of entity used to store data. 
	 * @return
	 */
	public abstract String getKind();
	
	//***** populateEntity
	/**
	 * Populate the specified entity with information from the current object.
	 * 
	 * <p>The default implementation of this method will not actually 
	 * populate any information in the specified entity object as the main
	 * properties contained in this object are properties that are immutable
	 * such as @{code dateCreated} and @{code createdBy} or properties
	 * that are going to be updated by the @{code DAO} such as 
	 * @{code dateModified} and @{code modifiedBy}.</p>
	 * 
	 * @param entity The instance of the @{code Entity} class from the 
	 * @{code com.google.appengine.api.datastore.Entity} package.
	 */
	protected void populateEntity(Entity entity) {
		logger.entering(_CLASS,"populateEntity(Entity)",entity);
		if (entity == null) {
			logger.severe("The Entity parameter was null.");
		} // END if (entity != null)
		logger.exiting(_CLASS,"populateEntity(Entity)");
	}
	
	protected void populateFromEntity(Entity entity, UserContext ctx) {
	  logger.entering(_CLASS,"populateFromEntity(Entity,UserContext)",
          new Object[] {entity,ctx});
	  
	  if (entity != null) {
	     setIdentifier(entity.getKey());
        setCreatedBy(getPropertyAsString(entity,"createdBy"));
        setDateCreated(getPropertyAsDate(entity,"dateCreated"));
        setDateModified(getPropertyAsDate(entity,"dateModified"));
        setModifiedBy(getPropertyAsString(entity,"modifiedBy"));        
     } else {
        logger.severe("The Entity parameter was null.");
     } // END if (entity != null)
	  logger.exiting(_CLASS,"populateFromEntity(Entity,UserContext)");
	}	
	
	public void populateFromObject(T object) {
	   logger.entering(_CLASS, "populateFromObject(T)",object);
	   WildObject wObject=null;
	   
	   if ((object != null) && (object instanceof WildObject)) {
	      wObject=(WildObject) object;
	      setIdentifier(wObject.getIdentifier());
	      setCreatedBy(wObject.getCreatedBy());
	      setDateCreated(wObject.getDateCreated());
	      setDateModified(wObject.getDateModified());
	   } // END if (object instanceof WildObject)
	   
	   logger.exiting(_CLASS, "populateFromObject(T)");
	}
}