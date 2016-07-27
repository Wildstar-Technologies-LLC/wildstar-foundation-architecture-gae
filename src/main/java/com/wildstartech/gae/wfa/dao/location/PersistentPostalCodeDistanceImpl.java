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
package com.wildstartech.gae.wfa.dao.location;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.location.PersistentPostalCodeDistance;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.location.DistanceMeasurement;
import com.wildstartech.wfa.location.DistanceMeasurement.UNITS;
import com.wildstartech.wfa.location.PostalCodeDistance;

public class PersistentPostalCodeDistanceImpl 
extends WildObjectImpl<PostalCodeDistance> 
implements PersistentPostalCodeDistance {
  /** Used in object serialization. */
  private static final long serialVersionUID = -1918108171671722111L;
  private static final String _CLASS=
      PersistentPostalCodeDistanceImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  protected static final String _KIND=
      "com.wildstartech.wfa.dao.location.PostalCodeDistance";
  
  private double measurement=0;
  private UNITS units=UNITS.IMPERIAL;
  private String originPostalCode=null;
  private String destinationPostalCode=null;
  
  /**
   * Default, no-argument constructor.
   */
  public PersistentPostalCodeDistanceImpl() {
    super();
    logger.entering(_CLASS,"PersistentPostalCodeDistanceImpl()");
    logger.exiting(_CLASS,"PersistentPostalCodeDistanceImpl()");
  }
  @Override
  public String getKind() {
    logger.entering(_CLASS,"getKind()");
    logger.exiting(_CLASS,"getKind()",PersistentPostalCodeDistanceImpl._KIND);
    return PersistentPostalCodeDistanceImpl._KIND;
  }

  //********** accessor Methods
  //***** destinationPostalCode
  @Override
  public String getDestinationPostalCode() {
    logger.entering(_CLASS,"getDestinationPostalCode()");
    logger.exiting(_CLASS,"getDestinationPostalCode()",
        this.destinationPostalCode);
    return this.destinationPostalCode;
  }

  @Override
  public void setDestinationPostalCode(String destination) {
    logger.entering(_CLASS,"setDestinationPostalCode(String)",destination);
    if (destination != null) {
      this.destinationPostalCode=destination;
    } else {
      this.destinationPostalCode="";
    } // END if (origin != null)
    logger.exiting(_CLASS,"setDestinationPostalCode(String)");  
  }
  //***** originPostalCode
  @Override
  public String getOriginPostalCode() {
    logger.entering(_CLASS,"getOriginPostalCode()");
    logger.exiting(_CLASS,"getOriginPostalCode()",this.originPostalCode);
    return this.originPostalCode;
  }
  @Override
  public void setOriginPostalCode(String origin) {
    logger.entering(_CLASS,"setOriginPostalCode(String)",origin);
    if (origin != null) {
      this.originPostalCode=origin;
    } else {
      this.originPostalCode="";
    } // END if (origin != null)
    logger.exiting(_CLASS,"setOriginPostalCode(String)");    
  }
  //***** distanceMeasurement
  @Override
  public DistanceMeasurement getDistanceMeasurement() {
    logger.entering(_CLASS,"getDistanceMeasurement()");
    DistanceMeasurement distance=null;
    
    distance=new DistanceMeasurementImpl();
    distance.setMeasurement(getMeasurement());
    distance.setUnits(getUnits());
    
    logger.exiting(_CLASS,"getDistanceMeasurement()",distance);
    return distance;
  }
  @Override
  public void setDistanceMeasurement(DistanceMeasurement distance) {
    logger.entering(_CLASS,"setDistanceMeasurement(DistanceMeasurement)",
        distance);
    if (distance != null) {
      this.measurement=distance.getMeasurement();
      this.units=distance.getUnits();
    } else {
      this.measurement=0;
      this.units=new DistanceMeasurementImpl().getDefaultUnitOfMeasurement();
    } // if (distance != null)
    logger.entering(_CLASS,"setDistanceMeasurement(DistanceMeasurement)");
  }
  //***** measurement
  protected double getMeasurement() {
    logger.entering(_CLASS,"getMeasurement()");
    logger.exiting(_CLASS,"getMeasurement()",this.measurement);
    return this.measurement;
  }
  protected void setMeasurement(double measurement) {
    logger.entering(_CLASS,"setMeasurement(double)",measurement);
    if ( measurement < 0 ) {
      this.measurement=0;
    } else {
      this.measurement=measurement;
    } // END if ( measurement < 0 )
    logger.exiting(_CLASS,"setMeasurment(double)");
  }
  //***** units
  protected UNITS getUnits() {
    logger.entering(_CLASS,"getUnits()");
    logger.exiting(_CLASS,"getUnits()",this.units);
    return this.units;
  }
  protected void setUnits(UNITS units) {
    logger.entering(_CLASS,"setUnits(UNITS)",units);
    logger.exiting(_CLASS,"setUnits(UNITS)",units);
  }
  //********** utility Methods
  /**
   * Populate the specified entity with information from the current object.
   * 
   * @param entity The instance of the <code>Entity</code> class from the 
   * <code>com.google.appengine.api.datastore.Entity</code> package.
   */
  @Override
  protected void populateEntity(Entity entity) {
    logger.entering(_CLASS, "populateEntity(Entity)",entity);
    String unitsLabel=null;
    
    if (entity != null) {
      super.populateEntity(entity);
      entity.setProperty("destinationPostalCode", getDestinationPostalCode());
      entity.setProperty("originPostalCode", getOriginPostalCode());
      entity.setProperty("measurement",this.measurement);
      switch(this.units) {
        case METRIC:
          unitsLabel=DistanceMeasurement.METRIC_LABEL;
          break;
        case NAUTICAL:
          unitsLabel=DistanceMeasurement.NAUTICAL_LABEL;
          break;
        default:
          unitsLabel=DistanceMeasurement.IMPERIAL_LABEL;
          break;
      } // END switch(this.units)
      entity.setProperty("units", unitsLabel);
    } else {
      logger.warning("The specified Entity object was null.");
    }
    logger.exiting(_CLASS, "populateEntity(Entity)");   
  }
  @Override
  protected void populateFromEntity(Entity entity, UserContext ctx) {
    logger.entering(_CLASS,"populateFromEntity(Entity,UserContext)",
        new Object[] {entity, ctx});
    
    String tmpStr=null;
    if (entity != null) {
      super.populateFromEntity(entity, ctx);
      setDestinationPostalCode(getPropertyAsString(
          entity,"destinationPostalCode"));
      setOriginPostalCode(getPropertyAsString(entity,"originPostalCode"));
      setMeasurement(getPropertyAsDouble(entity,"measurement"));
      tmpStr=getPropertyAsString(entity,"units");
      if (tmpStr != null) {
        if (tmpStr.equals(DistanceMeasurement.METRIC_LABEL)) {
          setUnits(UNITS.METRIC);
        } else if (tmpStr.equals(DistanceMeasurement.NAUTICAL_LABEL)) {
          setUnits(UNITS.NAUTICAL);
        } else {
          setUnits(UNITS.IMPERIAL);          
        } // END if (tmpStr.equals(DistanceMeasurement.METRIC_LABEL))
      } else {
        logger.warning("No property with a value of \"units\" was found.");
        setUnits(UNITS.IMPERIAL);
      } //END
    }
    logger.exiting(_CLASS,"populateFromEntity(Entity,UserContext)");
  }
  @Override
  public void populateFromObject(PostalCodeDistance distance) {
    logger.entering(_CLASS,"populateFromObject(PostalCodeDistance)",distance);
    if (distance != null) {
      setDestinationPostalCode(distance.getDestinationPostalCode());
      setOriginPostalCode(distance.getOriginPostalCode());
      setDistanceMeasurement(distance.getDistanceMeasurement());
    } else {
      logger.warning("The distance parameter was null.");
    } // END if (distance != null)
    logger.exiting(_CLASS,"populateFromObject(PostalCodeDistance)");
  }
}