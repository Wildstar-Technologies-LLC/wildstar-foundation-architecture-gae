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
package com.wildstartech.gae.wfa.objectcleaning;

import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.wildstartech.wfa.objectcleaning.Cleanable;
import com.wildstartech.wfa.objectcleaning.ObjectCleaningTracker;

/**
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 * @version 0.1, 2015-12-21
 */
public class TaskObjectCleaningTracker implements ObjectCleaningTracker {
   private static final String _CLASS=TaskObjectCleaningTracker.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   public TaskObjectCleaningTracker() {
      logger.entering(_CLASS, "TaskObjectCleaningTracker()");
      logger.exiting(_CLASS, "TaskObjectCleaningTracker()");
   }
   
   @Override
   public void addTracker(Cleanable cleanableObject, Object marker) {
      logger.entering(_CLASS, "addTracker(Cleanable,Object",
            new Object[] {cleanableObject, marker});
      Queue queue=null;
      queue=QueueFactory.getDefaultQueue();
      
      logger.exiting(_CLASS, "addTracker(Cleanable,Object");
   }
}