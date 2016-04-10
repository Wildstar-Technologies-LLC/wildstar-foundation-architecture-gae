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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class NamespaceFilter implements Filter {
	private static final String _CLASS=NamespaceFilter.class.getName();
	private static final Logger logger=Logger.getLogger(_CLASS);
	
	public void destroy() {
		logger.entering(_CLASS,"destroy()");
		logger.exiting(_CLASS,"destroy()");
	}

	public void doFilter(ServletRequest request, 
			ServletResponse response,
			FilterChain chain) 
			throws IOException, ServletException {
		logger.entering(_CLASS,
				"doFilter(ServletRequest,ServletResponse,FilterChain)",
				new Object[] {request, response, chain});
		NamespaceManager manager=null;
		String hostname =null;
		String namespace=null;
		
		// Read the hostname
		hostname=request.getServerName();

		// Get a reference to the WFA GAE Namespace Manager
		manager=NamespaceManager.getInstance();
		// Get the current namespace for the specified host.
		namespace=manager.getCurrentNamespace(hostname);
		
		if (logger.isLoggable(Level.FINEST)) {
         logger.finest("The hostname is: "+hostname);
         logger.finest("The mapped namespace for this host is: "+namespace);
      } // END if (logger.isLoggable(Level.FINEST))
		
		// Set the current namespace.
		com.google.appengine.api.NamespaceManager.set(namespace);
		chain.doFilter(request, response);
		
		logger.exiting(_CLASS,
				"doFilter(ServletRequest,ServletResponse,FilterChain)");		
	}

	public void init(FilterConfig config) throws ServletException {
		logger.entering(_CLASS,"init(FilterConfig)",config);
		logger.exiting(_CLASS,"init(FilterConfig)");
		
	}

}
