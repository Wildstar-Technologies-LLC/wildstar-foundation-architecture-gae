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
package com.wildstartech.wfa.dao.logistics.ltl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.wildstartech.wfa.logistics.ltl.Quote;
import com.wildstartech.wfa.logistics.ltl.WorkOrder;
import com.wildstartech.wfa.logistics.ltl.pricemodels.CubeDistancePriceModel;
import com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModel;
import com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModelFactory;

public class PriceModelFactoryImpl extends PriceModelFactory {
  private static final String _CLASS=PriceModelFactoryImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  
  private final static List<String> labels=new ArrayList<String>();
  static {
    labels.add("Cube/Distance");
    labels.add("Weight/Zone");
  }
  private final static List<String> types=new ArrayList<String>();
  static {
    types.add(CubeDistancePriceModel.TYPE);
  }
  @Override
  public PriceModel getDefaultModel() {
    logger.entering(_CLASS,"getDefaultModel()");
    PriceModel pm=new CubeDistancePriceModelImpl();
    logger.exiting(_CLASS,"getDefaultModel()",pm);
    return pm;
  }
  
  @Override
  public PriceModel getModel(Quote quote) {
    logger.entering(_CLASS,"getModel(Quote)",quote);
    PriceModel pm=getDefaultModel();
    logger.exiting(_CLASS,"getModel(Quote)",pm);
    return pm;
  }
  
  @Override
  public PriceModel getModel(WorkOrder workOrder) {
    logger.entering(_CLASS,"getModel(WorkOrder)",workOrder);
    PriceModel pm=getDefaultModel();
    logger.exiting(_CLASS,"getModel(WorkOrder)",pm);
    return pm;
  }
  
  @Override
  public PriceModel getModelByLabel(String modelName) {
    logger.entering(_CLASS,"getNamedModel(String)",modelName);
    PriceModel pm=getDefaultModel();
    logger.exiting(_CLASS,"getNamedModel(String)",pm);
    return pm;
  }

  @Override
  public PriceModel createType(String supportedType) {
    logger.entering(_CLASS, "");
    PriceModel pm=null;
    if (supportedType != null) {
      if (supportedType.equalsIgnoreCase(CubeDistancePriceModel.TYPE)) {
        pm=new CubeDistancePriceModelImpl();
      } // END if (supportedType.equalsIgnoreCase(CubeDistancePriceModel.TYPE))
    } else {
      logger.warning("The supportedType parameter is null.");
    } // END if (supportedType != null)
    logger.entering(_CLASS, "",pm);
    return pm;
  }

  @Override
  public List<String> getModelLabels() {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public List<String> getModelTypes() {
    logger.entering(_CLASS, "getModelTypes()");
    logger.exiting(_CLASS, "getModelTypes()",PriceModelFactoryImpl.types);
    return PriceModelFactoryImpl.types;
  }  
}