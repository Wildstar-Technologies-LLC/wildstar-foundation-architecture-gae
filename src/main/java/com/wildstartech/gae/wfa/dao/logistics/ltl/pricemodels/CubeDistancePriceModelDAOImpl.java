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

import java.util.List;
import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.logistics.ltl.pricemodels.CubeDistancePriceModelDAO;
import com.wildstartech.wfa.dao.logistics.ltl.pricemodels.PersistentCubeDistancePriceModel;
import com.wildstartech.wfa.logistics.ltl.pricemodels.CubeDistancePriceModel;

public class CubeDistancePriceModelDAOImpl 
extends WildDAOImpl<CubeDistancePriceModel, PersistentCubeDistancePriceModel> 
implements CubeDistancePriceModelDAO {
  private static final String _CLASS=
      CubeDistancePriceModelDAOImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);

  /**
   * Create a default instance of the <code>CubeDistancePriceModel</code>.
   * @return A concrete implementation of the 
   * <code>CubeDistancePriceModel</code> interface. 
   */
  @Override
  public PersistentCubeDistancePriceModel create() {
    logger.entering(_CLASS,"create()");
    PersistentCubeDistancePriceModel model=null;
    model=new PersistentCubeDistancePriceModelImpl();
    logger.entering(_CLASS,"create()",model);
    return model;
  }

  @Override
  public PersistentCubeDistancePriceModel create(
      CubeDistancePriceModel model, UserContext ctx) {
    logger.entering(_CLASS,"create(CubeDistancePriceModel,UserContext)",
        new Object[] {model, ctx});
    PersistentCubeDistancePriceModelImpl pModel=null;
    
    logger.exiting(_CLASS,"create(CubeDistancePriceModel,UserContext)",pModel);
    return pModel;
  }

  @Override
  public PersistentCubeDistancePriceModel findInstance(
      CubeDistancePriceModel object, UserContext ctx) throws DAOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public List<CubeDistancePriceModel> findByLabel(String label) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not yet implemented.");
  }
  /**
   * Returns the <em>Kind</em> property of the entity which is used for the 
   * purpose of querying the Datastore.
   * 
   * @return A string value which is used by the Datastore for the purpose of
   * categorizing entities of this object's type to provide the ability to 
   * querying the Datastore and retrieve entities. 
   */
  protected String getKind() {
     logger.entering(_CLASS, "getKind()");
     logger.exiting(_CLASS, "getKind()",
           PersistentCubeDistancePriceModelImpl._KIND);
     return PersistentCubeDistancePriceModelImpl._KIND;
  }
}