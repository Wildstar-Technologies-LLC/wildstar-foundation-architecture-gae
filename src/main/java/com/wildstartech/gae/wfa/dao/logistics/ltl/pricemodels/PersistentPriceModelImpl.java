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
package com.wildstartech.gae.wfa.dao.logistics.ltl.pricemodels;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.logistics.ltl.pricemodels.PersistentPriceModel;
import com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModel;

public abstract class PersistentPriceModelImpl 
extends WildObjectImpl<PriceModel> 
implements PersistentPriceModel {
  /** used in object serialization. */
  private static final long serialVersionUID = -8243627391149424984L;
  private static final String _CLASS=PersistentPriceModelImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  
  private boolean isDefault=false;
  private String label=null;
  
  protected static final String _KIND=
      "com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModel";
  
  /**
   * Default, no-argument constructor.
   */
  public PersistentPriceModelImpl() {
    logger.entering(_CLASS,"PersistentPriceModelImpl()");
    logger.exiting(_CLASS,"PersistentPriceModelImpl()");
  }
  
  //***** isDefault
  public boolean isDefault() {
    logger.entering(_CLASS, "isDefault()");
    logger.exiting(_CLASS, "isDefault()",this.isDefault);
    return this.isDefault;
  }
  public abstract void setDefault();
  //***** label
  public String getLabel() {
    logger.entering(_CLASS,"getLabel()");
    logger.exiting(_CLASS,"getLabel()",this.label);
    return this.label;
  }
  public void setLabel(String label) {
    logger.entering(_CLASS,"setLabel(String)",label);
    if (label != null) {
      this.label=label;
    } // END if (label != null)
    logger.exiting(_CLASS,"setLabel(String)");
  }
  
  //***** Utility methods
  @Override
  public abstract String getKind();
  
  @Override
  protected void populateEntity(Entity entity) {
    logger.entering(_CLASS,"populateEntity(Entity)");
    if (entity != null) {
      super.populateEntity(entity);
      entity.setProperty("isDefault", this.isDefault);
      entity.setProperty("label", getLabel());
    } else {
      logger.warning("The entity parameter is null.");
    } // END if (entity != null)
    logger.exiting(_CLASS,"populateEntity(Entity)");
  }
  @Override
  protected void populateFromEntity(Entity entity, UserContext ctx) {
    logger.entering(_CLASS,"populateEntity(Entity,UserContext)",
        new Object[] {entity, ctx});
    
    if (entity != null) {
      super.populateFromEntity(entity, ctx);
      // default
      this.isDefault=getPropertyAsBoolean(entity,"isDefault");
    } else {
      logger.warning("The entity parameter is null.");
    } // END if (entity != null)
    
    logger.exiting(_CLASS,"populateEntity(Entity,UserContext)");
  }
  @Override
  public void populateFromObject(PriceModel model) {
    logger.entering(_CLASS,"populateFromObject(PriceModel)",model);
    if (model != null) {
      // TODO Not yet implemented.
    } else {
      logger.warning("The specified model parameter is null.");
    } // END if (model != null)
    logger.exiting(_CLASS,"populateFromObject(PriceModel)");
  }
}