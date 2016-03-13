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
package com.wildstartech.gae.wfa.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.wfa.dao.PersistentGroup;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.group.Group;
import com.wildstartech.wfa.group.GroupNameTooLongException;
import com.wildstartech.wfa.user.User;

class PersistentGroupImpl 
extends WildObjectImpl<Group> 
implements PersistentGroup {
	/** Used in object serialization. */
	private static final long serialVersionUID = 7955375908228974828L;
	private static final String _CLASS=PersistentGroupImpl.class.getName();
	private static final Logger logger=Logger.getLogger(_CLASS);
	
	public static final int MAX_LENGTH_NAME=254;
	
	private List<User> userList=null;
	private String name=null;
	
	protected static final String _KIND="com.wildstartech.wfa.dao.group";
	
	/**
	 * Default, no-argument constructor.
	 */
	public PersistentGroupImpl() {
		logger.entering(_CLASS,"");
		this.name="";
		this.userList=new ArrayList<User>();
		logger.exiting(_CLASS,"");
	}
	
	protected void populateEntity(Entity entity) {
		logger.entering(_CLASS,"populateEntity(Entity)");
		
		if (entity != null) {
			// Call the super-class' method
			super.populateEntity(entity);	
			// Populate the name property.
			entity.setProperty("name",this.name);
		} // END if (entity != null)
		logger.exiting(_CLASS,"populateEntity(Entity)");
	}
	
	/**
	 * Populates the current object from the entity
	 */
	protected void populateFromEntity(Entity entity,UserContext ctx) {
		logger.entering(_CLASS,"populateFromEntity(Entity,UserContext)",
				new Object[] {entity,ctx});
		Object obj=null;
		if (entity != null) {
			super.populateFromEntity(entity,ctx);
			//***** name
			obj=entity.getProperty("name");
			if (obj instanceof String){
				this.name=(String) obj;
			} // END if (obj instanceof String)			
		} // END if (entity != null) 
		logger.exiting(_CLASS,"populateFromEntity(Entity)");
	}
	/**
	 * Populates the current object from the specified object.
	 */
	public void populateFromObject(Group group) {
	  logger.entering(_CLASS,"populateFromObject(Group)",group);
	  if (group != null) {
	    this.name=group.getName();
	  } // END if (group != null) 
	  logger.exiting(_CLASS,"populateFromObject(Group)",group);
	}
	
	public int compareTo(PersistentGroup group) {
		logger.entering(_CLASS,"");
		int result=Integer.MIN_VALUE;
		String otherName=null;
		if (group != null) {
			otherName=group.getName();
			result=this.name.compareTo(otherName);
		} // END if (user != null)
		logger.exiting(_CLASS,"");
		return result;
	}

	public void setName(String name) throws GroupNameTooLongException {
		logger.entering(_CLASS,"");
		if (name == null) {
			this.name="";
		} else {
			if (name.length() > MAX_LENGTH_NAME) {
				throw new GroupNameTooLongException(name,MAX_LENGTH_NAME);				
			} else {
				this.name=name;				
			} // END if (name.length() > MAX_LENGTH_NAME)
		} // END if (name == null)
		logger.exiting(_CLASS,"");		
	}

	public String getName() {
		logger.entering(_CLASS,"getName()");
		logger.exiting(_CLASS,"getName()",this.name);
		return this.name;
	}

	public void addUser(User user) {
		logger.entering(_CLASS,"");
		if (user != null) {
			if (! this.userList.contains(user)) {
				this.userList.add(user);
			} else {
				logger.warning("Specified user already in userList.");
			} // END if (! this.userList.contains(user))
		} else {
			logger.warning("User parameter was null.");
		} // END if (user != null)
		logger.exiting(_CLASS,"");		
	}

	public List<User> getUsers() {
		logger.entering(_CLASS,"getUsers()");
		List<User> users=null;
		users=Collections.unmodifiableList(this.userList);
		logger.exiting(_CLASS,"getUsers()",users);
		return users;
	}

	public boolean contains(User user) {
		logger.entering(_CLASS,"contains(User<Key>)",user);
		boolean result=false;
		if (user !=null) {
			result=this.userList.contains(user);
		} // END if (user !=null)
		logger.exiting(_CLASS,"contains(User<Key>)",result);
		return result;
	}

	public void removeUser(User user) {
		logger.entering(_CLASS,"");
		if (user != null) {
			if (this.userList.contains(user)) {
				this.userList.remove(user);
			} else {
				logger.info("The specified user isn't in the user list.");
			} // END if (this.userList.contains(user))
		} else {
			logger.warning("Users parameter is null.");
		} // END if (user != null)
		logger.exiting(_CLASS,"");
	}

	@Override
	public String getKind() {
		logger.entering(_CLASS,"getKind()");
		logger.exiting(_CLASS,"getKind()",PersistentGroupImpl._KIND);
		return PersistentGroupImpl._KIND;
	}
}