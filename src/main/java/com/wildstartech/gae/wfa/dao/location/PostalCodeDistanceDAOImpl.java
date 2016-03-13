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

import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.location.PersistentPostalCodeDistance;
import com.wildstartech.wfa.dao.location.PostalCodeDistanceDAO;
import com.wildstartech.wfa.location.PostalCodeDistance;

public class PostalCodeDistanceDAOImpl 
extends WildDAOImpl<PostalCodeDistance, PersistentPostalCodeDistance> 
implements PostalCodeDistanceDAO {
  
  private static final String _CLASS=
      PostalCodeDistanceDAOImpl.class.getName();
  private static final Logger logger=Logger.getLogger(_CLASS);
  
  /**
   * Create a new instance of the <code>PostalCodeDistance</code> interface.  
   */
  @Override
  public PersistentPostalCodeDistance create() {
    logger.entering(_CLASS,"create()");
    PersistentPostalCodeDistance distance=null;
    
    distance=new PersistentPostalCodeDistanceImpl();
    
    logger.exiting(_CLASS,"create()",distance);
    return distance;
  }

  @Override
  public PersistentPostalCodeDistance create(
      PostalCodeDistance referenceDistance,
      UserContext ctx) {
    logger.entering(_CLASS, "create(PostalCodeDistance,UserContext)",
        new Object[] {referenceDistance,ctx});
    PersistentPostalCodeDistanceImpl distance=null;
    
    distance=new PersistentPostalCodeDistanceImpl();
    distance.populateFromObject(referenceDistance);
    
    logger.exiting(_CLASS, "create(PostalCodeDistance,UserContext)",
        distance);
    return distance;
  }
  
  @Override
  public PersistentPostalCodeDistance findInstance(PostalCodeDistance object,
      UserContext ctx) throws DAOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PostalCodeDistance findByPostalCodes(String originPostalCode,
      String destinationPostalCode) {
    logger.entering(_CLASS,"findByPostalCodes(String,String)",
       new Object[] {originPostalCode,destinationPostalCode});
    String tmpStr=null;
    if (
        (originPostalCode != null) && 
        (originPostalCode.length() > 0) &&
        (destinationPostalCode != null) &&
        (destinationPostalCode.length() > 0)
       ) {
      if (destinationPostalCode.compareTo(originPostalCode) < 0) {
        /* The destinationPostalCode is less than the originPostalCode, so flip
         * 'em. */
        tmpStr=destinationPostalCode;
        destinationPostalCode=originPostalCode;
        originPostalCode=tmpStr;
      } // END if (destinationPostalCode.compareTo(originPostalCode) < 0)
    } else {
      // Error handling...
      if ((originPostalCode == null) || (originPostalCode.length() > 0)) {
        if (originPostalCode == null) {
          logger.warning("The originPostalCode parameter was null.");
        } else {
          logger.warning(
              "The originPostalCode parameter was a zero-length String.");
        } // END if (originPostalCode == null)
      } // END if ((originPostalCode != null) || (originPostalCode.length() ...
      if (
            (destinationPostalCode == null) || 
            (destinationPostalCode.length() > 0)
         ) {
        if (destinationPostalCode == null) {
          logger.warning("The destinationPostalCode parameter was null.");
        } else {
          logger.warning(
              "The destinationPostalCode parameter was a zero-length String.");
        } // END if (destinationPostalCode == null)
      } // END if ((destinationPostalCode != null) || ...      
    } // END if ((originPostalCode != null) && ... 
    return null;
  }  

  /**
   * Returns the <em>Kind</em> property of the entity which is used for the 
   * purpose of querying the Datastore.
   * 
   * @return A string value which is used by the Datastore for the purpose of
   * categorizing entities of this object's type to provide the ability to 
   * querying the Datastore and retrieve entities. 
   */
  protected final String getKind() {
     logger.entering(_CLASS, "getKind()");
     logger.exiting(_CLASS, "getKind()",PersistentPostalCodeDistanceImpl._KIND);
     return PersistentPostalCodeDistanceImpl._KIND;
  }
}