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
package com.wildstartech.gae.wfa.dao.logistics;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.logistics.PersistentWarehouseLocation;
import com.wildstartech.wfa.logistics.WarehouseLocation;

public class PersistentWarehouseLocationImpl 
extends WildObjectImpl<WarehouseLocation> 
implements PersistentWarehouseLocation {
   /** Used in object serialization. */
   private static final long serialVersionUID = 2182180639630211554L;
   private static final String _CLASS=
         PersistentWarehouseLocationImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   protected static final String _KIND=
         "com.wildstartech.wfa.logistics.WarehouseLocation";
   
   private String aisle="";
   private String bay="";
   private String level="";
   private String position="";
   
   /**
    * Default, no-argument constructor.
    */
   public PersistentWarehouseLocationImpl() {
      super();
      logger.entering(_CLASS, "PersistentWarehouseLocationImpl()");
      logger.exiting(_CLASS, "PersistentWarehouseLocationImpl()");      
   }
   
   //********** Utility Methods
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentWarehouseLocationImpl._KIND);
      return PersistentWarehouseLocationImpl._KIND;
   }
   
   @Override
   protected void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(Entity)",entity);
      if (entity != null) {
         super.populateEntity(entity);
         entity.setProperty("aisle", getAisle());
         entity.setProperty("bay", getBay());
         entity.setProperty("level", getLevel());
         entity.setProperty("position", getPosition());
      } else {
         logger.warning("The entity object was null.");
      } // END if (entity != null)
      logger.exiting(_CLASS, "populateEntity()");
   }
   
   @Override
   protected void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS,"populateFromEntity(Entity,UserContext)",
            new Object[] {entity,ctx});
      
      if (entity != null) {
         super.populateFromEntity(entity, ctx);
         setAisle(getPropertyAsString(entity,"aisle"));
         setBay(getPropertyAsString(entity,"bay"));
         setLevel(getPropertyAsString(entity,"level"));
         setPosition(getPropertyAsString(entity,"position"));
      } else {
         logger.warning("The entity parameter is null.");
      } // END if (entity != null)
      
      logger.entering(_CLASS, "populateFromEntity(Entity,UserContext)");
   }
   
   @Override
   public void populateFromObject(WarehouseLocation warehouseLocation) {
      logger.entering(_CLASS, "populateFromObject(WarehouseLocation)",
            warehouseLocation);
      
      if (warehouseLocation != null) {
         super.populateFromObject(warehouseLocation);
         setAisle(warehouseLocation.getAisle());
         setBay(warehouseLocation.getBay());
         setLevel(warehouseLocation.getLevel());
         setPosition(warehouseLocation.getPosition());
      } else {
         logger.warning("The warehouseLocation is null.");
      } // END if (warehouseLocation != null)
      
      logger.entering(_CLASS, "populateFromObject(WarehouseLocation)");
   }
   
   //***** aisle
   @Override
   public String getAisle() {
      logger.entering(_CLASS, "getAisle()");
      logger.exiting(_CLASS, "getAisle()",this.aisle);
      return this.aisle;
   }

   @Override
   public void setAisle(String aisle) {
      logger.entering(_CLASS, "setAisle(String)",aisle);
      this.aisle=defaultValue(aisle);
      logger.exiting(_CLASS, "setAisle(String)");
   }

   //***** bay
   @Override
   public String getBay() {
      logger.entering(_CLASS, "getBay()");
      logger.exiting(_CLASS, "getBay()",this.bay);
      return this.bay;
   }

   @Override
   public void setBay(String bay) {
      logger.entering(_CLASS, "setBay(String)",bay);
      this.bay=defaultValue(bay);
      logger.exiting(_CLASS, "setBay(String)");
   }

   //***** level
   @Override
   public String getLevel() {
      logger.entering(_CLASS, "getLevel()");
      logger.exiting(_CLASS, "getLevel()",this.level);
      return this.level;
   }

   @Override
   public void setLevel(String level) {
      logger.entering(_CLASS, "setLevel(String)",level);
      this.level=defaultValue(level);
      logger.exiting(_CLASS, "setLevel(String)");
   }

   //***** position
   @Override
   public String getPosition() {
      logger.entering(_CLASS, "getPosition()");
      logger.exiting(_CLASS, "getPosition()",this.position);
      return this.position;
   }

   @Override
   public void setPosition(String position) {
      logger.entering(_CLASS, "setPosition(String)",position);
      this.position=defaultValue(position);
      logger.exiting(_CLASS, "setPosition(String)");
   }
}