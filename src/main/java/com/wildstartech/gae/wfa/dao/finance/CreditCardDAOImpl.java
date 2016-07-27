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
package com.wildstartech.gae.wfa.dao.finance;

import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.WildDAOImpl;
import com.wildstartech.wfa.dao.DAOException;
import com.wildstartech.wfa.dao.finance.CreditCardDAO;
import com.wildstartech.wfa.dao.finance.PersistentCreditCard;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.finance.CreditCard;

public class CreditCardDAOImpl 
extends WildDAOImpl<CreditCard, PersistentCreditCard> 
implements CreditCardDAO {
   private static final String _CLASS=CreditCardDAOImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   @Override
   public PersistentCreditCard findInstance(
         CreditCard creditCard, UserContext ctx)
         throws DAOException {
      logger.entering(_CLASS, "findInstance(CreditCard, UserContext)",
            new Object[] {creditCard,ctx});
      PersistentCreditCard pCreditCard=null;
      logger.exiting(_CLASS, "findInstance(CreditCard, UserContext)",
            pCreditCard);
      return pCreditCard;
   }

   @Override
   public PersistentCreditCard create() {
      logger.entering(_CLASS,"create()");
      logger.exiting(_CLASS,"create()");
      return new PersistentCreditCardImpl();
   }

   @Override
   public PersistentCreditCard create(CreditCard creditCard, UserContext ctx) {
      logger.entering(_CLASS, "create(CreditCard,UserContext)",
            new Object[]{ creditCard,ctx});
      PersistentCreditCard pCard=null;
      
      pCard=new PersistentCreditCardImpl(creditCard);
      
      logger.exiting(_CLASS, "create(CreditCard,UserContext)",pCard);
      return pCard;
   }

   @Override
   protected String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentCreditCardImpl._KIND);
      return PersistentCreditCardImpl._KIND;
   }
}