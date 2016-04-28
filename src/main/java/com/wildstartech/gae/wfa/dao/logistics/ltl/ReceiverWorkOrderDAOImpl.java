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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.ticketing.BasicTicketDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.logistics.ltl.PersistentReceiverWorkOrder;
import com.wildstartech.wfa.dao.logistics.ltl.ReceiverWorkOrderDAO;
import com.wildstartech.wfa.logistics.ltl.ReceiverWorkOrder;

public class ReceiverWorkOrderDAOImpl
extends BasicTicketDAOImpl<ReceiverWorkOrder, PersistentReceiverWorkOrder>
implements ReceiverWorkOrderDAO {
   private static String _CLASS=ReceiverWorkOrderDAOImpl.class.getName();
   private static Logger logger=Logger.getLogger(_CLASS);
   
   @Override
   public PersistentReceiverWorkOrder findInstance(ReceiverWorkOrder workOrder,
         UserContext ctx) throws DAOException {
      logger.entering(_CLASS, "findInstance(ReceiverWorkOrder,UserContext)",
            new Object[] {workOrder,ctx});
      PersistentReceiverWorkOrder pWO=null;
      
      logger.entering(_CLASS, "findInstance(ReceiverWorkOrder,UserContext)",
            pWO);
      return pWO;
   }
   
   @Override
   public PersistentReceiverWorkOrder create() {
      logger.entering(_CLASS, "create()");
      PersistentReceiverWorkOrder workOrder=null;
      workOrder=new PersistentReceiverWorkOrderImpl();
      logger.exiting(_CLASS, "create()",workOrder);
      return workOrder;
   }
   
   @Override
   public PersistentReceiverWorkOrder create(ReceiverWorkOrder workOrder,
         UserContext ctx) {
      logger.entering(_CLASS, "create(ReceiverWorkOrder,UserContext)",
            new Object[] {workOrder,ctx});
      PersistentReceiverWorkOrderImpl pWO=null;
      
      pWO=new PersistentReceiverWorkOrderImpl();
      pWO.populateFromObject(workOrder);
      
      logger.exiting(_CLASS, "create(ReceiverWorkOrder,UserContext)",pWO);
      return pWO;
   }
   
   @Override
   protected String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentReceiverWorkOrderImpl._KIND);
      return PersistentReceiverWorkOrderImpl._KIND;
   }

   @Override
   public List<PersistentReceiverWorkOrder> findActionable(UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findActionable(UserContext)");
      List<PersistentReceiverWorkOrder> workOrders=null;
      
      if (workOrders == null) {
         workOrders=new ArrayList<PersistentReceiverWorkOrder>();
      } // END if (workOrders == null)       
      logger.exiting(_CLASS, "findActionable(UserContext)",workOrders);
      return workOrders;
   }

   @Override
   public List<PersistentReceiverWorkOrder> findAllOpen(UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findAllOpen(UserContext)");
      List<PersistentReceiverWorkOrder> workOrders=null;
      
      if (workOrders == null) {
         workOrders=new ArrayList<PersistentReceiverWorkOrder>();
      } // END if (workOrders == null)       
      logger.exiting(_CLASS, "findAllOpen(UserContext)",workOrders);
      return workOrders;
   }
}