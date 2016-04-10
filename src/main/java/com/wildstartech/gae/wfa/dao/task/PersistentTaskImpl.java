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
package com.wildstartech.gae.wfa.dao.task;

import java.util.Date;
import java.util.logging.Logger;

import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.task.PersistentTask;
import com.wildstartech.wfa.task.Task;

class PersistentTaskImpl extends WildObjectImpl<Task> 
implements PersistentTask {
  /** Used in object serialization. */
  private static final long serialVersionUID = -501362489240840233L;
  private static final String _CLASS = PersistentTaskImpl.class.getName();
  private static final Logger logger = Logger.getLogger(_CLASS);
  
  private Date startDate = null;
  private Date stopDate = null;
  private String description = null;
  private String title = null;

  protected static final String _KIND = "com.wildstartech.wfa.task.Task";

  /**
   * Default, no-argument constructor
   */
  PersistentTaskImpl() {
    logger.entering(_CLASS, "PersistentTaskImpl()");
    logger.exiting(_CLASS, "PersistentTaskImpl()");
  }
  //***** utility methods
  public void populateFromObject(Task task) {
    logger.entering(_CLASS, "populateFromObject(Task)",task);
    logger.exiting(_CLASS, "populateFromObject(Task)");
  }
  //***** accessor methods
  // ********** description
  public String getDescription() {
    logger.entering(_CLASS, "getDescription()");
    logger.exiting(_CLASS, "getDescription()", this.description);
    return this.description;
  }

  public void setDescription(String description) {
    logger.entering(_CLASS, "setDescription(String)", description);
    if (description != null) {
      this.description = description;
    } else {
      this.description = "";
    } // END if (description != null)
    logger.exiting(_CLASS, "setDescription(String)");
  }

  @Override
  public String getKind() {
    logger.entering(_CLASS, "getKind()");
    logger.exiting(_CLASS, "getKind()", PersistentTaskImpl._KIND);
    return PersistentTaskImpl._KIND;
  }

  // ***** startDate
  public Date getStartDate() {
    logger.entering(_CLASS, "getStartDate()");
    logger.entering(_CLASS, "getStartDate()", this.startDate);
    return this.startDate;
  }

  public void setStartDate(Date startDate) {
    logger.entering(_CLASS, "setStartDate(Date)", startDate);
    this.startDate = startDate;
    logger.exiting(_CLASS, "setStartDate(Date)");
  }

  // ***** stopDate
  public Date getStopDate() {
    logger.entering(_CLASS, "getStopDate()");
    logger.entering(_CLASS, "getStopDate()", this.stopDate);
    return this.stopDate;
  }

  public void setStopDate(Date stopDate) {
    logger.entering(_CLASS, "setStopDate(Date)", stopDate);
    this.stopDate = stopDate;
    logger.entering(_CLASS, "setStopDate(Date)");
  }

  // ***** title
  public String getTitle() {
    logger.entering(_CLASS, "getTitle()");
    logger.exiting(_CLASS, "getTitle()", this.title);
    return this.title;
  }

  public void setTitle(String title) {
    logger.entering(_CLASS, "setTitle(String)", title);
    this.title = defaultValue(title, "");
    logger.exiting(_CLASS, "setTitle(String)");
  }
}