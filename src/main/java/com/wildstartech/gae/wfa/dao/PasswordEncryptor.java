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
 */package com.wildstartech.gae.wfa.dao;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.util.logging.Logger;

public class PasswordEncryptor {
	private static final String _CLASS=PasswordEncryptor.class.getName();
	private static final Logger logger=Logger.getLogger(_CLASS);
	private static PasswordEncryptor pe=new PasswordEncryptor();
	
	String algorithm=MessageDigestAlgorithms.SHA_512;
	
	// Default, no-argument constructor to prevent instantiation.
	private PasswordEncryptor() {
		logger.entering(_CLASS,"PasswordEncryptor()");
		logger.exiting(_CLASS,"PasswordEncryptor()");
	}
	
	protected static PasswordEncryptor getInstance() {
		logger.entering(_CLASS,"getInstance()");
		logger.exiting(_CLASS,"getInstance()",PasswordEncryptor.pe);
		return PasswordEncryptor.pe;
	}
	
	/** Returns the encrypted equivalent version of of the specified string. */
	protected String encryptPassword(String password) {
		logger.entering(_CLASS,"encryptPassword(String)",password);
		String encrypted=null;
		
		if ((password != null) && (password.length() > 0)) {
			encrypted=DigestUtils.sha512Hex(password.getBytes());
		} else  {
			logger.finest("The password is either null or zero-length.");
		} // END if ((password != null) && (password.length() > 0))
		
		logger.exiting(_CLASS,"encryptPassword(String)",encrypted);
		return encrypted;
	}
}
