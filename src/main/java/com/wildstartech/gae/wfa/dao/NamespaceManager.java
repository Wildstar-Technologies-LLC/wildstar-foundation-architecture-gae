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
package com.wildstartech.gae.wfa.dao;

import java.util.logging.Logger;

/**
 * Determines the appropriate Namespace for the current user.
 *  
 * <p>With the Google App Engine, a <code>Namespace</code> is are used to 
 * support multi-tenancy in applications deployed on the platform.</p>
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 * @version 0.1, 2013-09-22
 */
public class NamespaceManager {
	// Returns a static reference to the name of the class, used in logging.
	private static final String _CLASS=NamespaceManager.class.getName();
	// Returns a static reference to the name of the logger.
	private static final Logger logger=Logger.getLogger(_CLASS);
	
	// Private constructor to prevent instantiation.
	private NamespaceManager() {
		logger.entering(_CLASS,"NamespaceManager()");
		logger.exiting(_CLASS,"NamespaceManager()");
	}
	
	protected static final NamespaceManager getInstance() {
		logger.entering(_CLASS,"getInstance()");
		NamespaceManager manager=new NamespaceManager();
		logger.exiting(_CLASS,"getInstance()",manager);
		return manager;
	}
	
	protected String getCurrentNamespace(String namespaceKey) { 
		logger.entering(_CLASS,"getCurrentNamespace()",namespaceKey);
		String namespace=null;
		if (namespaceKey != null) {
			if (namespaceKey.contains("justologistics")) {
				namespaceKey="com.justologistics.crm";
			} else {
				// The deafult namespace is com.wildstartech.servicedesk
				namespace="com.wildstartech.servicedesk";
			} // END if (namespaceKey.contains("justologistics"))
		} else {
			namespace="com.wildstartech.servicedesk";
		} // END 
		logger.exiting(_CLASS,"getCurrentNamespace()",namespace);
		return namespace;
	}
}