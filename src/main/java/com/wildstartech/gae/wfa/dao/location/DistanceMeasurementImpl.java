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
package com.wildstartech.gae.wfa.dao.location;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;

import com.wildstartech.wfa.location.DistanceMeasurement;
import com.wildstartech.wfa.location.DistanceMeasurement.UNITS;

public class DistanceMeasurementImpl implements DistanceMeasurement {
  private static final String _CLASS=DistanceMeasurementImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  
  private double measurement=0;
  private UNITS units=null;
  
  /**
   * Default, no-argument constructor.
   */
  public DistanceMeasurementImpl() {
    logger.entering(_CLASS,"DistanceMeasurementImpl()");
    logger.exiting(_CLASS,"DistanceMeasurementImpl()");
  }
  
  public DistanceMeasurementImpl(double measurement, UNITS units) {
    logger.entering(_CLASS,"DistanceMeasurementImpl(double,UNITS)",
        new Object[] {measurement, units});
    setMeasurement(measurement);
    setUnits(units);
    logger.exiting(_CLASS,"DistanceMeasurementImpl(double,UNITS)");
  }
  //***** measurement
  @Override
  public double getMeasurement() {
    logger.entering(_CLASS,"getMeasurement()");
    double result=0;
    result=getMeasurementAs(this.units);
    logger.exiting(_CLASS,"getMeasurement()",result);
    return result;
  }
  @Override
  public double getMeasurementAs(UNITS units) {
    logger.entering(_CLASS,"getMeasurementAs(UNITS)",units);
    double modifiedMeasurement=0;
    if (units == UNITS.METRIC) {
      modifiedMeasurement=this.measurement/1000;
    } else {
      switch(units) {
        case NAUTICAL:
          modifiedMeasurement = this.measurement * 0.000539957;
          break;
        default: 
          modifiedMeasurement = this.measurement * 0.000621371;
          break;
      } // END switch(units)
    } // END if (units == UNITS.METRIC)
    logger.exiting(_CLASS,"getMeasurementAs(UNITS)",modifiedMeasurement);
    return modifiedMeasurement;
  }
  @Override
  public void setMeasurement(double measurement) {
    logger.entering(_CLASS,"setMeasurement(double)",measurement);
    if (measurement < 0) {
      this.measurement=0;
    } else {
      this.measurement=measurement;
    } // END if (measurement < 0) 
    logger.exiting(_CLASS,"setMeasurement(double)");
  }
  //***** units
  @Override
  public UNITS getUnits() {
    logger.entering(_CLASS,"getUnits()");
    logger.exiting(_CLASS,"getUnits()",this.units);
    return this.units;
  }
  @Override
  public void setUnits(UNITS units) {
    logger.entering(_CLASS,"setUnits(UNITS)",units);
    this.units=units;
    logger.exiting(_CLASS,"setMeasurement(double)");
  }
  //********** utility methods
  /**
   * Returns the default unit of measurement for the current locale.
   * 
   * <p>Of all the countries in the world, only three backwaters still use the 
   * archaic Imperial system of weights and measures</p>
   * 
   * @return The default unit of measurement that should be employed when 
   * performing distance calculations.
   * 
   * @see http://www.joeydevilla.com/2008/08/13/countries-that-dont-use-the-metric-system
   */
  public UNITS getDefaultUnitOfMeasurement() {
    logger.entering(_CLASS,"getDefaultUnitOfMeasurement()");
    Locale locale=null;
    String countryCode=null;
    DistanceMeasurement.UNITS unit=null;
    
    locale=Locale.getDefault();
    countryCode=locale.getCountry();
    
    if (
          countryCode.equals("LR") ||   // LIberia
          countryCode.equals("MM") ||   // Burma
          countryCode.equals("US")      //US
       ) {
      unit=DistanceMeasurement.UNITS.IMPERIAL;
    } else {
      unit=DistanceMeasurement.UNITS.METRIC;
    } // END if (countryCode.equals("LR") ...
    logger.exiting(_CLASS,"getDefaultUnitOfMeasurement()",unit);
    return unit;
  }
  //***** toString
  /**
   * Returns a string representation of this instance.
   */
  public String toString() {
    logger.entering(_CLASS,"toString()");
    NumberFormat fmt=null;
    String tmpStr=null;
    StringBuilder sb=null;
    
    sb=new StringBuilder();
    fmt=new DecimalFormat("#.#####");
    sb.append("DistanceMeasurementImpl [");
    sb.append("measurement: ").append(fmt.format(getMeasurement()));
    sb.append(", units: ");
    switch (this.units) {
      case METRIC:
        sb.append(METRIC_LABEL);
        break;
      case NAUTICAL:
        sb.append(NAUTICAL_LABEL);
        break;
      default: 
        sb.append(IMPERIAL_LABEL);
        break;
    } // END switch(this.units)
    sb.append("]");
    tmpStr=sb.toString();
    logger.entering(_CLASS, "toString()",tmpStr);
    return tmpStr;    
  }
  
  //***** toFormattedString
  public String toFormattedString() {
    logger.entering(_CLASS,"toFormattedString()");
    NumberFormat fmt=null;
    String tmpStr=null;
    StringBuilder sb=null;
    
    sb=new StringBuilder();
    fmt=new DecimalFormat("#.##");
    sb.append(fmt.format(this.measurement));
    sb.append(" ");
    switch(this.units) {
      case METRIC:
        sb.append(METRIC_ABBREVIATION);
        break;
      case NAUTICAL:
        sb.append(NAUTICAL_ABBREVIATION);
        break;
      default:
        sb.append(IMPERIAL_ABBREVIATION);
        break;      
    } // END switch(this.units)
    tmpStr=sb.toString();
    logger.exiting(_CLASS,"toFormattedString()",tmpStr);
    return tmpStr;
  }

}
