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


import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.wildstartech.gae.wfa.dao.logistics.PersistentWarehouseLocationImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentReceiverWorkOrderLineItem;
import com.wildstartech.wfa.journal.JournalEntry;
import com.wildstartech.wfa.logistics.WarehouseLocation;
import com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrderLineItem;

public class PersistentReceiverWorkOrderLineItemImpl
extends PersistentLineItemImpl<ReceiverWorkOrderLineItem>
implements PersistentReceiverWorkOrderLineItem {
   /** Used in object serializaiton. */
   private static final long serialVersionUID = -1506383620683576337L;
   private static final String _CLASS=
         PersistentReceiverWorkOrderLineItemImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   protected static final String _KIND=
         "com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrderLineItem";
   
   private boolean hasException=false;
   private JournalEntry entry=null;
   private String aisle="";
   private String bay="";
   private String level="";
   private String position="";
   private String exceptionDescription="";
   private String workOrderIdentifier="";
   
   /**
    * Default, no-argument constructor.
    */
   public PersistentReceiverWorkOrderLineItemImpl() {
      super();
      logger.entering(_CLASS, "PersistentReceiverWorkOrderLineItemImpl()");
      logger.exiting(_CLASS, "PersistentReceiverWorkOrderLineItemImpl()");
   }
   //********** Utility Methods
   
   @Override
   public int hashCode() {
   	final int prime = 31;
   	int result = super.hashCode();
   	result = prime * result + ((aisle == null) ? 0 : aisle.hashCode());
   	result = prime * result + ((bay == null) ? 0 : bay.hashCode());
   	result = prime * result + ((entry == null) ? 0 : entry.hashCode());
   	result = prime * result + ((exceptionDescription == null) ? 0 : 
   		exceptionDescription.hashCode());
   	result = prime * result + (hasException ? 1231 : 1237);
   	result = prime * result + ((level == null) ? 0 : level.hashCode());
   	result = prime * result + ((position == null) ? 0 : position.hashCode());
   	result = prime * result + ((workOrderIdentifier == null) ? 0 : 
   		workOrderIdentifier.hashCode());
   	return result;
   }
   
   /**
    * Compares the non-persistent properties of the current object with those
    * of the specified object to determine if they are equivalent.
    * @param item
    * @return
    */
   public boolean isNonPersistentEquivalent(ReceiverWorkOrderLineItem item) {
	   logger.entering(_CLASS, 
			   "isNonPersistentEquivalent(ReceiverWorkOrderLineItem)",item);
	   boolean result=true;
	   String tmpThis=null;
	   String tmpThat=null;
	   
	   if (item != null) {
		   //** lineItemNumber
		   tmpThis=String.valueOf(this.getLineItemNumber());
		   tmpThat=String.valueOf(item.getLineItemNumber());
		   if (result) {
			   result=tmpThis.equals(tmpThat);
		   } // END if (result)
		   //** description
		   if (result) {
			   tmpThis=this.getDescription();
			   tmpThat=item.getDescription();
			   result=tmpThis.equals(tmpThat);
		   } // END if (result)
		   //** exception
		   if (result) {
			   result=(hasException() == item.hasException()) ? true : false;
		   } // END if (result)
		   //** exceptionDescription
		   if (result) {
			   tmpThis=getExceptionDescription();
			   tmpThat=item.getExceptionDescription();
			   result=tmpThis.equals(tmpThat);
		   } // END if (result)
		   //** aisle
		   if (result) {
			   tmpThis=getAisle();
			   tmpThat=item.getLocation().getAisle();
			   result=tmpThis.equals(tmpThat);
		   } // END if (result)
		   //** bay
		   if (result) {
			   tmpThis=getBay();
			   tmpThat=item.getLocation().getBay();
			   result=tmpThis.equals(tmpThat);
		   } // END if (result)
		   //** level
		   if (result) {
			   tmpThis=getLevel();
			   tmpThat=item.getLocation().getLevel();
			   result=tmpThis.equals(tmpThat);
		   } // END if (result)
		   //** position
		   if (result) {
			   tmpThis=getPosition();
			   tmpThat=item.getLocation().getPosition();
			   result=tmpThis.equals(tmpThat);			   
		   } // END if (result)
	   } // END if (item != null)
	   
	   logger.exiting(_CLASS,
			   "isNonPersistentEquivalent(ReceiverWorkOrderLineItem)",
			   result);
	   return result;
   }
   
   @Override
   public boolean equals(Object obj) {
   	if (this == obj)
   		return true;
   	if (!super.equals(obj))
   		return false;
   	if (getClass() != obj.getClass())
   		return false;
   	PersistentReceiverWorkOrderLineItemImpl other = (PersistentReceiverWorkOrderLineItemImpl) obj;
   	if (aisle == null) {
   		if (other.aisle != null)
   			return false;
   	} else if (!aisle.equals(other.aisle))
   		return false;
   	if (bay == null) {
   		if (other.bay != null)
   			return false;
   	} else if (!bay.equals(other.bay))
   		return false;
   	if (entry == null) {
   		if (other.entry != null)
   			return false;
   	} else if (!entry.equals(other.entry))
   		return false;
   	if (exceptionDescription == null) {
   		if (other.exceptionDescription != null)
   			return false;
   	} else if (!exceptionDescription.equals(other.exceptionDescription))
   		return false;
   	if (hasException != other.hasException)
   		return false;
   	if (level == null) {
   		if (other.level != null)
   			return false;
   	} else if (!level.equals(other.level))
   		return false;
   	if (position == null) {
   		if (other.position != null)
   			return false;
   	} else if (!position.equals(other.position))
   		return false;
   	if (workOrderIdentifier == null) {
   		if (other.workOrderIdentifier != null)
   			return false;
   	} else if (!workOrderIdentifier.equals(other.workOrderIdentifier))
   		return false;
   	return true;
   }
   
   @Override
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(entity)",entity);
      Text text = null;
      
      if (entity != null) {
         super.populateEntity(entity);
         entity.setProperty("aisle", getAisle());
         entity.setProperty("bay", getBay());
         text=new Text(getExceptionDescription());
         entity.setProperty("exceptionDescription",text);
         entity.setProperty("hasException", hasException());
         entity.setProperty("level", getLevel());
         entity.setProperty("position", getPosition());
         entity.setProperty("workOrderIdentifier", getWorkOrderIdentifier());
      } else {
         logger.warning("The entity parameter is null.");
      } // END if (entity != null)
      
      logger.exiting(_CLASS, "populateEntity(entity)");
   }
   
   @Override
   protected void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity,UserContext)",
            new Object[] {entity,ctx});
      
      if (entity != null) {
         super.populateFromEntity(entity, ctx);
         setAisle(getPropertyAsString(entity,"aisle"));
         setBay(getPropertyAsString(entity,"bay"));
         setExceptionDescription(
               getPropertyAsString(entity,"exceptionDescription"));
         setException(getPropertyAsBoolean(entity,"hasException"));
         setLevel(getPropertyAsString(entity,"level"));
         setPosition(getPropertyAsString(entity,"position"));
         setWorkOrderIdentifier(
               getPropertyAsString(entity,"workOrderidentifier"));
      } else {
         logger.warning("The entity parameter is null.");
      } // END if (entity != null)
      
      logger.exiting(_CLASS, "populateFromEntity(Entity,UserContext)");
   }
   
   @Override
   public void populateFromObject(ReceiverWorkOrderLineItem item) {
      logger.entering(_CLASS,
            "populateFromObject(ReceiverWorkOrderLineItem)",
            item);
      PersistentReceiverWorkOrderLineItem pItem=null;
      WarehouseLocation location=null;
      
      if (item != null) {
         super.populateFromObject(item);
         setException(item.hasException());
         setExceptionDescription(item.getExceptionDescription());
         location=item.getLocation();
         if (location != null) {
            setAisle(location.getAisle());
            setBay(location.getBay());
            setLevel(location.getLevel());
            setPosition(location.getPosition());
         } // END if (location != null)
         if (item instanceof PersistentReceiverWorkOrderLineItem) {
            pItem=(PersistentReceiverWorkOrderLineItem) item;
            setWorkOrderIdentifier(pItem.getWorkOrderIdentifier());
         } // END if (item instanceof PersistentReceiverWorkOrderLineItem)
      } else {
         logger.warning("The ReceiverWorkOrderLineItem was null.");
      }
      logger.exiting(_CLASS,"populateFromObject(ReceiverWorkOderLineItem)");
   }
   
   //********** Accessor Methods
   //***** aisle
   public String getAisle() {
      logger.entering(_CLASS, "getAisle()");
      logger.exiting(_CLASS, "getAisle()",this.aisle);
      return this.aisle;
   }
   
   public void setAisle(String aisle) {
      logger.entering(_CLASS, "setAisle(String)",aisle);
      this.aisle=aisle;
      logger.exiting(_CLASS, "setAisle(String)");
   }
   //***** bay
   public String getBay() {
      logger.entering(_CLASS, "getBay()");
      logger.exiting(_CLASS, "getBay()",this.bay);
      return this.bay;
   }
   
   public void setBay(String bay) {
      logger.entering(_CLASS, "setBay(String)",bay);
      this.bay=bay;
      logger.exiting(_CLASS, "setBay(String)");
   }
   //***** exception
   @Override
   public boolean hasException() {
      logger.entering(_CLASS, "hasException()");
      logger.exiting(_CLASS, "hasException()",this.hasException);
      return this.hasException;
   }
   @Override
   public void setException(boolean exception) {
      logger.entering(_CLASS, "setException(boolean)",exception);
      this.hasException=exception;
      logger.exiting(_CLASS, "setException(boolean)");
   }
   
   //***** exceptionDescription
   @Override
   public String getExceptionDescription() {
      logger.entering(_CLASS, "getExceptionDescription()");
      logger.exiting(_CLASS, "getExceptionDescription()",
            this.exceptionDescription);
      return this.exceptionDescription;
   }
   @Override
   public void setExceptionDescription(String description) {
      logger.entering(_CLASS, "setExceptionDescription(String)",description);
      this.exceptionDescription=defaultValue(description);
      logger.exiting(_CLASS, "setExceptionDescription(String)");
   }
   //***** kind
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",
            PersistentReceiverWorkOrderLineItemImpl._KIND);
      return PersistentReceiverWorkOrderLineItemImpl._KIND;
   }
   //***** level 
   public String getLevel() {
      logger.entering(_CLASS, "getLevel()");
      logger.exiting(_CLASS, "getLevel()",this.level);
      return this.level;
   }
   
   public void setLevel(String level) {
      logger.entering(_CLASS, "setLevel(String)",level);
      this.level=level;
      logger.exiting(_CLASS, "setLevel(String)");
   }
   
   //***** location
   @Override
   public WarehouseLocation getLocation() {
      logger.entering(_CLASS, "getLocation()");
      PersistentWarehouseLocationImpl location=null;
      
      location=new PersistentWarehouseLocationImpl();
      location.setAisle(getAisle());
      location.setBay(getBay());
      location.setLevel(getLevel());
      location.setPosition(getPosition());
      
      logger.exiting(_CLASS, "getLocation()", location);
      return location;
   }
   @Override
   public void setLocation(WarehouseLocation warehouseLocation) {
      logger.entering(_CLASS, "setLocation(WarehouseLocation)",
            warehouseLocation);
      if (warehouseLocation != null) {
    	  this.aisle=warehouseLocation.getAisle();
    	  this.bay=warehouseLocation.getBay();
    	  this.level=warehouseLocation.getLevel();
      } else {
    	  this.aisle="";
    	  this.bay="";
    	  this.level="";
      } // END if (warehouseLocation != null)
      logger.entering(_CLASS, "setLocation(WarehouseLocation)");
   }
   
   //***** position
   public String getPosition() {
      logger.entering(_CLASS, "getPosition()");
      logger.exiting(_CLASS, "getPosition()",this.position);
      return this.position;
   }
   public void setPosition(String position) {
      logger.entering(_CLASS, "setPosition(String)",position);
      this.position=position;
      logger.exiting(_CLASS, "setPosition(String)");
   }
   
   //***** workOrderIdentifier
   @Override
   public String getWorkOrderIdentifier() {
      logger.entering(_CLASS, "getWorkOrderIdentifier()");
      logger.exiting(_CLASS, "getWorkOrderIdentifier()",
            this.workOrderIdentifier);
      return this.workOrderIdentifier;
   }   
   @Override
   public void setWorkOrderIdentifier(String identifier) {
      logger.entering(_CLASS, "setWorkOrderIdentifier(String)",identifier);
      this.workOrderIdentifier=defaultValue(identifier);
      logger.exiting(_CLASS, "setWorkOrderIdentifier(String)");
   }
   
   //***** newJournalEntry
   @Override
   public JournalEntry getNewJournalEntry() {
      logger.entering(_CLASS, "getNewJournalEntry()");
      logger.exiting(_CLASS, "getNewJournalEntry()");
      return this.entry;
   }
   @Override
   public void setNewJournalEntry(JournalEntry entry) {
      logger.entering(_CLASS, "setNewJournalEntry(entry)",entry);
      this.entry=entry;
      logger.exiting(_CLASS, "setNewJournalEntry(entry)");
   }
}