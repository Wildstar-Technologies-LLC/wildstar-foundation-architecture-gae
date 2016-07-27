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
package com.wildstartech.gae.wfa.dao.logistics.ltl.pricemodels;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.wfa.dao.WildObject;
import com.wildstartech.wfa.dao.logistics.ltl.pricemodels.PersistentCubeDistancePriceModel;
import com.wildstartech.wfa.dao.logistics.ltl.pricemodels.PersistentPriceModel;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.logistics.ltl.QuickQuote;
import com.wildstartech.wfa.logistics.ltl.Quote;
import com.wildstartech.wfa.logistics.ltl.SimpleQuote;
import com.wildstartech.wfa.logistics.ltl.WorkOrder;
import com.wildstartech.wfa.logistics.ltl.pricemodels.CubeDistancePriceModel;
import com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModel;

public class PersistentCubeDistancePriceModelImpl
extends PersistentPriceModelImpl
implements PersistentCubeDistancePriceModel {
  /** Used in object serialization. */
  private static final long serialVersionUID = 4078923734523510709L;
  private static final String _CLASS = 
      PersistentCubeDistancePriceModelImpl.class.getName();
  private static final Logger logger = Logger.getLogger(_CLASS);
  protected static final String _KIND=
      "com.wildstartech.wfa.logistics.ltl.pricemodels.CubeDistancePriceModel";
  
  private int mileageInterval=0;
  private int minCube=0;
  private double baseCharge=0;
  private double cubeDiscount=0;
  private double mileageStep=1;
  private double minCubeCharge=0;
  /**
   * Default, no-argument constructor.
   */
  public PersistentCubeDistancePriceModelImpl() {
    logger.entering(_CLASS,"PersistentCubeDistancePriceModelImpl()");
    logger.exiting(_CLASS,"PersistentCubeDistancePriceModelImpl()");
  }
  
  //***** mileageInterval
  public int getMileageInterval() {
    logger.entering(_CLASS,"getMileageInterval()");
    logger.exiting(_CLASS,"getMileageInterval()",this.mileageInterval);
    return this.mileageInterval;
  }
  public void setMileageInterval(int mileageInterval) {
    logger.entering(_CLASS,"setMileageInterval(int)",mileageInterval);
    this.mileageInterval = mileageInterval;
    logger.exiting(_CLASS,"setMileageInterval(int)");
  }
  //***** minCube
  public int getMinCube() {
    logger.entering(_CLASS,"getMinCube()");
    logger.exiting(_CLASS,"getMinCube()",this.minCube);
    return this.minCube;
  }  
  public void setMinCube(int minCube) {
    logger.entering(_CLASS,"setMinCube(int)",minCube);
    this.minCube = minCube;
    logger.exiting(_CLASS,"setMinCube(int)");
  }
  //***** baseCharge
  public double getBaseCharge() {
    logger.entering(_CLASS,"getBaseCharge()");
    logger.exiting(_CLASS,"getBaseCharge()",this.baseCharge);
    return this.baseCharge;
  }
  /**
   * Returns the base charge 
   * @param cube
   * @param mileage
   * @return
   */
  public double getBaseCharge(int cube, double mileage) {
    logger.entering(_CLASS, "getBaseCharge(int)",cube);
    double computedBaseCharge=0;
    double mileageMultiplier=0;
    double mileageStep=0;
    double minimumBaseCharge=0;
    int roundedMileage=0;
    int mileageExponent=0;
    int mileageInterval=0;
    
    computedBaseCharge=getBaseCubeCharge(cube);
    // Normalize the mileage
    roundedMileage=new Long(Math.round(mileage)).intValue();
    // Get the mileage Interval
    mileageInterval=getMileageInterval();
    // Get the mileageStep
    mileageStep=getMileageStep();
    // Determine mileage exponent
    if (roundedMileage < mileageInterval) {
      mileageExponent=0;
    } else if ((roundedMileage % mileageInterval) == 0) {
      mileageExponent=(roundedMileage/mileageInterval) - 1;
    } else {
      mileageExponent=(roundedMileage/mileageInterval);
    } // END if (roundedMileage < mileageInterval)
    mileageMultiplier=Math.pow(mileageStep,mileageExponent);
    // Apply mileageMultiplier
    computedBaseCharge=computedBaseCharge * mileageMultiplier;
    // Check computed computedBaseCharge against minimum base charge.
    if (computedBaseCharge < minimumBaseCharge) {
      computedBaseCharge=minimumBaseCharge;
    } // END if (computedBaseCharge < minimumBaseCharge)
    logger.exiting(_CLASS,"getBaseCharge(int)", computedBaseCharge);
    return computedBaseCharge;
  }
  /**
   * Returns the base charge for the specified number of cubes.
   * 
   * @param cube
   * @return
   */
  public double getBaseCubeCharge(int cube) {
    logger.entering(_CLASS,"getBaseCubeCharge(int)",cube);
    double baseCharge;
    double cubeDiscount=0;
    double computedBaseCharge=0;
    double computedCubeDiscount=0;
    int minCube=0;
    
    // Obtain the base Charge
    baseCharge=getBaseCharge();
    // Normalize the cube
    minCube=getMinCube();
    if (cube < minCube) {
      cube=minCube;
    } // END if (cube < this.model.getMinCube())
    // Determine the cube discount rate.
    cubeDiscount=getCubeDiscount();
    computedCubeDiscount=1-cubeDiscount;
    if (cube == minCube) {
      computedCubeDiscount=1;
    } else {
      computedCubeDiscount=Math.pow(computedCubeDiscount, (cube - minCube));
    } // END if (cube == minCube)
    // Calculate base charge.
    computedBaseCharge=baseCharge * computedCubeDiscount;
    logger.exiting(_CLASS,"getBaseCubeCharge(int)",baseCharge);
    return computedBaseCharge;
  }
  
  public void setBaseCharge(double baseCharge) {
    logger.entering(_CLASS,"setBaseCharge(double)",baseCharge);
    this.baseCharge = baseCharge;
    logger.exiting(_CLASS,"setBaseCharge(double)");
  }
  //***** cubeDiscount
  public double getCubeDiscount() {
    logger.entering(_CLASS,"getCubeDiscount()");
    logger.exiting(_CLASS,"getCubeDiscount()",this.cubeDiscount);
    return this.cubeDiscount;
  }
  public void setCubeDiscount(double cubeDiscount) {
    logger.entering(_CLASS,"setCubeDiscount(double)",cubeDiscount);
    this.cubeDiscount = cubeDiscount;
    logger.exiting(_CLASS,"setCubeDiscount(double)");    
  }
  //***** isDefault
  public void setDefault() {
    logger.entering(_CLASS,"setDefault()");
    boolean isDefault=false;
    
    isDefault=isDefault();
    if (!isDefault) {
      
    } // END if (!isDefault)
    
    logger.exiting(_CLASS,"setDefault()");
  }
  //***** mileageStep 
  public double getMileageStep() {
    logger.entering(_CLASS,"getMileageStep()");
    logger.exiting(_CLASS,"getMileageStep()",this.mileageStep);
    return this.mileageStep;
  }
  public void setMileageStep(double mileageStep) {
    logger.entering(_CLASS,"setMileageStep(double)",mileageStep);
    this.mileageStep = mileageStep;
    logger.exiting(_CLASS,"setMileageStep(double)");
  }
  //***** minCubeCharge
  public double getMinCubeCharge() {
    logger.entering(_CLASS,"getMinCubeCharge()");
    logger.exiting(_CLASS,"getMinCubeCharge()",this.minCubeCharge);
    return this.minCubeCharge;
  }
  public void setMinCubeCharge(double minCubeCharge) {
    logger.entering(_CLASS,"setMinCubeCharge(double)",minCubeCharge);
    this.minCubeCharge = minCubeCharge;
    logger.exiting(_CLASS,"setMinCubeCharge(double)");    
  }  
  //***** calculateCharge
  /**
   * Return an estimated rate for the specified cube and mileage.
   * @param cube
   * @param mileage
   * @return
   */
  public double calculateCharge(int cube, double mileage) {
    logger.entering(_CLASS,"calculateCharge(int,int)",
        new Object[] {cube,mileage});
    int minCube=0;
    double charge=0;
    double baseCharge=0;
    // Calculate the base charge.
    baseCharge=getBaseCharge(cube,mileage);
    /* Determine if the specified cube meets the minimum.  If it does not, then
     * adjust the cube to relfect the minimum. */
    minCube=getMinCube();
    if (cube < minCube) cube=minCube;
    charge=baseCharge*cube;
    logger.exiting(_CLASS,"calculateCharge(int,int)",charge);
    return charge;
  }

  @Override
  public double calculateTotalCharges(QuickQuote quote) {
    logger.entering(_CLASS,"calculateCharge(Quote)",quote);
    double charges=0;
    double distance=0;
    int cubes=0;
    if (quote != null) {
      distance=quote.getDistance();
      cubes=quote.getTotalCubes();
    } else {
      logger.severe("The specified quote parameter is null.");
    } // END if (quote != null)
    charges=calculateCharge(cubes,distance);
    logger.exiting(_CLASS,"calculateCharge(Quote)",charges);
    return charges;
  }
  
  @Override
  public double calculateTotalCharges(SimpleQuote quote) {
	  logger.entering(_CLASS,"calculateCharge(SimpleQuote)",quote);
	  double charges=0;
	  charges=calculateTotalCharges((QuickQuote) quote);
	  logger.exiting(_CLASS,"calculateCharge(SimpleQuote)",charges);
	  return charges;
  }
  
  @Override
  public double calculateTotalCharges(Quote quote) {
	  logger.entering(_CLASS,"calculateCharge(Quote)",quote);
	  double charges=0;
	  charges=calculateTotalCharges((QuickQuote) quote);
	  logger.exiting(_CLASS,"calculateCharge(Quote)",charges);
	  return charges;
  }
  
  
  @Override
  public double calculateTotalCharges(WorkOrder workOrder) {
     logger.entering(_CLASS, "calculateTotalCharges(WorkOrder)",
           workOrder);
     double charges=0;
     double distance=0;
     int cubes=0;
     
     if (workOrder != null) {
        distance=workOrder.getDistance();
        cubes=workOrder.getTotalCubes();        
     } else {
        logger.severe("The specified Work Order parameter is null.");
     } // END if (workOrder != null)
     charges=calculateCharge(cubes,distance);
     
     logger.exiting(_CLASS, "calculateTotalCharges(WorkOrder)",charges);
     return charges;
  }

  @Override
  public String getType() {
    logger.entering(_CLASS,"getType()");
    logger.entering(_CLASS,"getType()",CubeDistancePriceModel.TYPE);
    return CubeDistancePriceModel.TYPE;
  }
  //********** Utility Methods
  public String getKind() {
    logger.entering(_CLASS,"getKind()");
    logger.exiting(_CLASS,"getKind()",
        PersistentCubeDistancePriceModelImpl._KIND);
    return PersistentCubeDistancePriceModelImpl._KIND;
  }
  /**
   * Returns a hash
   */
  public int hashCode() {
    logger.entering(_CLASS,"hashCode()");
    int code=0;
    String tmpStr="";
    
    tmpStr=toString();
    code=tmpStr.hashCode();
    
    logger.exiting(_CLASS, "hashCode()",code);
    return code;
  }
  /**
   * Return a String representation of the object.
   */
  public String toString() {
    logger.entering(_CLASS,"toString()");
    Date tmpDate=null;
    DateFormat dFmt=null; 
    NumberFormat currencyFormat=null;
    NumberFormat decimalFormat=null;
    String tmpStr="";
    StringBuilder sb=null;
    
    currencyFormat=NumberFormat.getCurrencyInstance();
    decimalFormat=NumberFormat.getNumberInstance();
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    dFmt=DateFormat.getDateInstance();
    sb=new StringBuilder(2048);
    sb.append(PersistentCubeDistancePriceModelImpl.class.getName());
    sb.append(" ['");
    sb.append(getIdentifier());
    sb.append("','");
    sb.append(getLabel());
    sb.append("','");
    sb.append(currencyFormat.format(getBaseCharge()));
    sb.append("','");
    sb.append(decimalFormat.format(getCubeDiscount()));
    sb.append("','");
    sb.append(decimalFormat.format(getMileageStep()));
    sb.append("','");
    sb.append(getMileageStep());
    sb.append("','");
    sb.append(getMinCube());
    sb.append("','");
    sb.append(currencyFormat.format(getMinCubeCharge()));
    sb.append("','");
    tmpDate=getDateCreated();
    if (tmpDate != null) {
      sb.append(dFmt.format(tmpDate));
    } // END if (tmpDate != null)
    sb.append("','");
    sb.append(getCreatedBy());
    sb.append("','");
    tmpDate=getDateModified();
    if (tmpDate != null) {
      sb.append(dFmt.format(tmpDate));
    } // END if (tmpDate != null)
    sb.append("','");
    sb.append(getModifiedBy());
    sb.append("']'");
    tmpStr=sb.toString();
    logger.exiting(_CLASS,"toString()",tmpStr);
    return tmpStr;
  }
  //***** Utility methods
  
  @Override
  protected void populateEntity(Entity entity) {
    logger.entering(_CLASS,"populateEntity(Entity)");
    if (entity != null) {
      super.populateEntity(entity);
      entity.setProperty("baseCharge", getBaseCharge());
      entity.setProperty("cubeDiscount", getCubeDiscount());
      entity.setProperty("mileageInterval", getMileageInterval());
      entity.setProperty("minCube", getMinCube());
      entity.setProperty("minCubeCharge",getMinCubeCharge());
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
      setBaseCharge(getPropertyAsDouble(entity,"baseCharge"));
      setCubeDiscount(getPropertyAsDouble(entity,"cubeDiscount"));
      setMileageInterval(getPropertyAsInteger(entity,"mileageInterval"));
      setMinCube(getPropertyAsInteger(entity,"minCube"));
      setMinCubeCharge(getPropertyAsDouble(entity,"minCubeCharge"));      
    } else {
      logger.warning("The entity parameter is null.");
    } // END if (entity != null)
    
    logger.exiting(_CLASS,"populateEntity(Entity,UserContext)");
  }
  
  @Override
public void populateFromObject(PriceModel model) {
    logger.entering(_CLASS,"populateFromObject(PriceModel)",model);
    CubeDistancePriceModel cpm=null;
    
    if (model != null) {
      if ((model instanceof WildObject) || 
          (model instanceof PersistentPriceModel)) {
        super.populateFromObject(model);
      } // END if ((model instanceof WildObject) || (model instanceof ...
      if (model instanceof CubeDistancePriceModel) {
        cpm=(CubeDistancePriceModel) model;
        setBaseCharge(cpm.getBaseCharge());
        setCubeDiscount(cpm.getCubeDiscount());
        setMileageInterval(cpm.getMileageInterval());
        setMinCube(cpm.getMinCube());
        setMinCubeCharge(cpm.getMinCubeCharge());
      } // END if (model instanceof CubeDistancePriceModel)
    } else {
      logger.warning("The specified model parameter is null.");
    } // END if (model != null)
    
    logger.exiting(_CLASS,"populateFromObject(PriceModel)");
  }
}
