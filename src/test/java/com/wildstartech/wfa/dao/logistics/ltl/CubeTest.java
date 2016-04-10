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

import java.text.DecimalFormat;

import org.testng.annotations.Test;

import com.wildstartech.wfa.logistics.ltl.pricemodels.CubeDistancePriceModel;
import com.wildstartech.wfa.logistics.ltl.pricemodels.PriceModelFactory;

public class CubeTest {
  @Test
  public void cubeCalculationTest() {
    int[] miles={50,100,150,200,250,300,350,400,450,500,550,600};
    int cube=0;
    double baseCharge=0;
    double charge=0;
    double cubeDiscount=0;
    double minCubeCharge=0;
    
    CubeDistancePriceModel model=null;
    PriceModelFactory pmf=null;
    DecimalFormat fmt=null;
    
    
    fmt=new DecimalFormat("#,###.00");
    pmf=PriceModelFactory.getInstance();
    model=(CubeDistancePriceModel) pmf.getDefaultModel();
    model.setBaseCharge(4.72);
    model.setCubeDiscount(0.00169);
    model.setMileageInterval(50);
    model.setMileageStep(1.09);
    model.setMinCube(35);
    model.setMinCubeCharge(2);
    baseCharge=model.getBaseCharge();
    cubeDiscount=model.getCubeDiscount();
    minCubeCharge=model.getMinCubeCharge();
    
    System.out.print("Cube,");
    for (int mile: miles) {
      System.out.printf("%d,",mile);
    } 
    System.out.println("");
    cube=model.getMinCube();
    while (baseCharge > minCubeCharge) {
      System.out.printf("%d,",cube);
      for(int mile: miles) {
        charge=model.calculateCharge(cube,mile);
        System.out.printf("\"%s\",",fmt.format(charge));          
      } // END for(int mile: miles)
      System.out.println("");
      cube++;
      baseCharge=baseCharge * (1-cubeDiscount);
    } // END while (baseCharge > minCubeCharge)
  }  
  
  @Test
  public void testThreeTwentyFive() {
    double charge=0;
    CubeDistancePriceModel model=null;
    PriceModelFactory pmf=null;
    
    pmf=PriceModelFactory.getInstance();
    model=(CubeDistancePriceModel) pmf.getDefaultModel();
    model.setBaseCharge(4.72);
    model.setCubeDiscount(0.00169);
    model.setMileageInterval(50);
    model.setMileageStep(1.05);
    model.setMinCube(35);
    model.setMinCubeCharge(2);
    charge=model.calculateCharge(7, 327.28);
    System.out.println("325miles"+charge);
  }
}
