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
 */package com.wildstartech.gae.wfa.dao.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.group.PersistentGroup;
import com.wildstartech.wfa.dao.user.PersistentUser;
import com.wildstartech.wfa.dao.user.UserContext;
import com.wildstartech.wfa.group.Group;
import com.wildstartech.wfa.user.PasswordTooLongException;
import com.wildstartech.wfa.user.User;
import com.wildstartech.wfa.user.UserNameTooLongException;

public class PersistentUserImpl 
extends WildObjectImpl<User> 
implements PersistentUser {
	/** Used in object serialization. */
	private static final long serialVersionUID = 6054802148071698917L;
	private static final String _CLASS=PersistentUserImpl.class.getName();
	private static final Logger logger=Logger.getLogger(_CLASS);
	private static final byte[] roaster=
			"Supercalifragilisticexpialidocious".getBytes();
	protected static final String _KIND="com.wildstartech.wfa.dao.user";
	/** 
	 * The maximum allowable length of a user name.
	 * 
	 * <p>The maximum, total allowable length of an e-mail address is 254 
	 * characters (see section 4.5.3.1 of RFC5321).  As an e-mail address will
	 * be used as the user name for persistence frameworks using the Wildstar 
	 * Foundation Architecture Data Access Object (DAO) classes, the maximum
	 * allowable user name length will be 254 characters.</p>  
	 * 
	 * @see http://tools.ietf.org/html/rfc5321#section-4.5.3.1 */
	public static final int MAX_LENGTH_NAME=254;
	/** 
	 * The maximum allowable length of a password.
	 * 
	 * <p>The actual password text for a user account will not be stored in the
	 * persistent data store when using the Google App Engine platform. 
	 * Ideally, authentication will be delegated to an OpenID provider; however,
	 * if it is necessary to store password information with an account, then a
	 * SHA-256 digest of the password will be stored.</p>
	 * <p>The authentication process will involve the client creating a digest
	 * of the password and then sending that digest across the wire for 
	 * authentication.  The user's actual credentials should never actually
	 * leave their local machine.</p>
	 */
	public static final int MAX_LENGTH_PASSWORD=64;
	
	private boolean federated=false;
	private String name=null;
	private String newPassword=null;
	private String password=null;
	private List<Group> groupList=null;
	
	/**
	 * Default, no-argument constructor.
	 */
	public PersistentUserImpl() {
		super();
		logger.entering(_CLASS,"UserImpl()");
		this.name="";
		this.password="";
		this.groupList=new ArrayList<Group>();
		logger.exiting(_CLASS,"UserImpl()");
	}
	
	protected void populateEntity(Entity entity) {
		logger.entering(_CLASS,"populateEntity(Entity)");
		PasswordEncryptor pe=null;
		if (entity != null) {
			super.populateEntity(entity);
			// Federated
			entity.setProperty("federated", this.federated);
			// Name
			entity.setProperty("name",this.name);
			// Password
			if (this.newPassword != null) {
				pe=PasswordEncryptor.getInstance();
				entity.setProperty("password",
						pe.encryptPassword(this.newPassword));
			} else {
				entity.setProperty("password",this.password);
			}			
		}
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
			//***** federated
			obj=entity.getProperty("federated");
			if (obj instanceof Boolean) {
				this.federated=(boolean) obj;
			} // END if (obj instanceof Boolean)
			//***** name
			obj=entity.getProperty("name");
			if (obj instanceof String){
				this.name=(String) obj;
			} // END if (obj instanceof String)
			//***** password
			obj=entity.getProperty("password");
			if (obj instanceof String){
				this.password=(String) obj;
				this.newPassword=null;
			} // END if (obj instanceof String)			
		} // END if (entity != null) 
		logger.exiting(_CLASS,"populateFromEntity(Entity)");
	}
	/**
	 * Populate the current object from the template specified.
	 */
	protected void populateFromObject(PersistentUser user) {
	  logger.entering(_CLASS, "populateFromObject(User)",user);
	  logger.exiting(_CLASS, "populateFromObject(User)");
	}
	/**
	 * Utility method used to roast the password.
	 * @param password
	 * @return
	 */
	protected String roastPassword(String password) {
		logger.entering(_CLASS,"roastPassword(String)",password);
		int xorIndex=0;
		int passwordLength=0;
		String tmpStr=null;
		String roasted=null;
		StringBuilder sb=null;
				
		if (password != null) {
			passwordLength=password.length();
			sb=new StringBuilder();
			for (int i=0; i < passwordLength; i++) {
				tmpStr=Integer.toHexString(
					roaster[xorIndex]^(int)password.charAt(i));
				if (tmpStr.length() == 1) {
					sb.append("0").append(tmpStr);					
				} else {
					sb.append(tmpStr);
				} // END if (tmpStr.length() == 1)
			} // END for (int i=0; i < passwordLength; i++)
			roasted=sb.toString();
			if (roasted.length() <= MAX_LENGTH_PASSWORD) {
				roasted=roasted.substring(0,MAX_LENGTH_PASSWORD);
			} // END if ((roasted != null) && (roasted.length() > MAX_LENGTH_PASSWORD))
		} else {
			roasted="";
		} // END if (password != null)				
		logger.exiting(_CLASS,"roastPassword(String)",roasted);
		return roasted;
	}
	/**
	 * Comparison.
	 * @param user
	 * @return
	 */
	public int compareTo(User user) {
		logger.entering(_CLASS,"compareTo(User)",user);
		int result=Integer.MIN_VALUE;
		String otherName=null;
		if (user != null) {
			otherName=user.getName();
			result=this.name.compareTo(otherName);
		} // END if (user != null)
		logger.exiting(_CLASS,"compareTo(User)",result);
		return result;
	}
	//*************** Accessor Methods
	//***** federated
	public boolean isFederated() {
		logger.entering(_CLASS,"isFederated()");
		logger.exiting(_CLASS,"isFederated()",this.federated);
		return this.federated;
	}
	public void setFederated(boolean fededrated) {
		logger.entering(_CLASS,"setFederated(boolean)",federated);
		logger.exiting(_CLASS,"setFederated(boolean)");
	}
	//***** groups
	public void addGroup(PersistentGroup group) {
		logger.entering(_CLASS,"addGroup(Group)",group);
		if (group != null) {
			if (! this.groupList.contains(group)) {
				this.groupList.add(group);
			} else {
				logger.warning("The specified group is already in the list.");
			} // END if (! this.groupList.contains(group))
		} else {
			logger.warning("The Group parameter was null.");
		} // END if (group != null)
		logger.exiting(_CLASS,"addGroup(Group)");
	}
	public void removeGroup(PersistentGroup group) {
		logger.entering(_CLASS,"removeGroup(PersistentGroup)",group);
		if (group != null) {
			if (! this.groupList.contains(group)) {
				this.groupList.remove(group);
			} else {
				logger.warning("The specified was not in the list.");
			} // END if (! this.groupList.contains(group))
		} else {
			logger.warning("The Group parameter was null.");
		} // END if (group != null)
		logger.exiting(_CLASS,"removeGroup(Group<Key>)");		
	}
	
	/**
	 * Returns an unmodifiable list of groups to which the user belongs.
	 */
	public List<Group> getGroups() {
		logger.entering(_CLASS,"getGroups()");
		List<Group> groups;
		/*
		PersistentGroup group=null;
		List<Association> relationships=null;
		RelationshipDAO dao=null;
		RelationshipDAOFactory factory=null;
		//TODO Must Find way to obtain reference to UserContext 
		UserContext ctx=null;
		WildObject wildObj=null;
		*/
		// Initialize the groupList
		this.groupList=new ArrayList<Group>();
		/**********
		// Manage the groups to which the user belongs.
		factory=new RelationshipDAOFactory();
		dao=factory.getDAO();
		// Get a list of objects that are associated with the current User
		relationships=dao.findByPrimaryObject(this,ctx);
		if (relationships.size() > 0) {
			// Groups are found, so let's add them to the user's list.
			for (Association relationship: relationships) {
				wildObj=relationship.getSecondaryEntity();
				if(wildObj instanceof Group) {
					this.groupList.add(group);
				} // END if(obj instanceof Group)
			} // END for (Association relationship: relationships)
		} // END if (relationships.size() > 0)
		**********/
		groups=Collections.unmodifiableList(this.groupList);
		logger.exiting(_CLASS,"getGroups()",groups);
		return groups;
	}	
	//***** name
	//*
	/**
	 * Returns the name assigned to the user.
	 */
	public String getName() {
		logger.entering(_CLASS,"getName()");
		logger.exiting(_CLASS,"getName()",this.name);
		return this.name;
	}
	
	/**
	 * 
	 */
	public void setName(String name) throws UserNameTooLongException {
		logger.entering(_CLASS,"setName(String)",name);
		if (name == null) {
			this.name="";
		} else {
			if (name.length() > MAX_LENGTH_NAME) {
				throw new UserNameTooLongException(name,MAX_LENGTH_NAME);				
			} else {
				this.name=name;				
			} // END if (name.length() > MAX_LENGTH_NAME)
		} // END if (name == null)
		logger.exiting(_CLASS,"name(String)",name);		
	}
	
	//***** password
	/**
	 * 
	 */
	public String getPassword() {
		logger.entering(_CLASS,"getPassword()");
		String pwd=null;
		if (this.newPassword != null) {
			pwd=this.newPassword;
		} else {
			pwd=this.password;
		}
		logger.entering(_CLASS,"getPassword()",pwd);
		return pwd;
	}
	/**
	 * 
	 */
	public void setPassword(String password) throws PasswordTooLongException {
		logger.entering(_CLASS,"setPassword(String)",password);
		if (password.compareTo(this.password) != 0) {
		  this.newPassword=password;
		} // END if (password.compareTo(this.password) != 0)
		logger.exiting(_CLASS,"setPassword(String)");
	}

	@Override
	public String getKind() {
		logger.entering(_CLASS,"getKind()");
		logger.exiting(_CLASS,"getKind()",PersistentUserImpl._KIND);
		return PersistentUserImpl._KIND;
	}

  @Override
  public void addGroup(Group group) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeGroup(Group group) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void populateFromObject(User obj) {
    // TODO Auto-generated method stub
    
  }
}
