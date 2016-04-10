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
package com.wildstartech.gae.commons.fileupload;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GcsFileFilter implements Filter {
   private static final String _CLASS=GcsFileFilter.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   private static final String THRESHOLD_SIZE_PARAMETER="thresholdSize";
   private static final String DEFAULT_BUCKET_PARAMETER="defaultBucket";
   
   private boolean initialized=true;
   private int thresholdSize=-1;
   private String defaultBucket=null;
   
   //********** Filter Methods
   /**
    * Called by the web container to indicate to a filter that it is being taken
    * out of service.
    * <p>This method gives the filter an opportunity to clean up any resources
    * that are being held (for example, memory, file handles, threads) and make
    * sure that any persistent state is synchronized with the filter's current
    * state in memory.</p>
    */
   @Override
   public void destroy() {
      logger.entering(_CLASS, "destroy()");
      logger.exiting(_CLASS, "destroy()");      
   }

   /**
    * The doFilter method of the Filter is called by the container each time a
    * request/response pair is passed through the chain due to a client request
    * for a resource at the end of the chain.
    * <p>The doFilter method of the Filter is called by the container each time
    * a request/response pair is passed through the chain due to a client 
    * request for a resource at the end of the chain. The FilterChain passed in
    * to this method allows the Filter to pass on the request and response to
    * the next entity in the chain.</p>
    * @param request
    * @throws 
    */
   @Override
   public void doFilter(
         ServletRequest request, 
         ServletResponse response,
         FilterChain chain) 
               throws IOException, ServletException {
      logger.entering(_CLASS, 
            "doFilter(ServletRequest,ServletResponse,FilterChain)",
            new Object[] {request,response,chain});
      boolean isMultipart=false;
      int thresholdSize=-1;
      GcsFileItemFactory factory=null;
      GcsMultipartRequest requestWrapper=null;
      HttpServletRequest hRequest=null;
      ServletFileUpload upload=null;
      String gcsBucketName=null;
      
      hRequest=(HttpServletRequest) request;
      isMultipart=ServletFileUpload.isMultipartContent(hRequest);
      
      if ((this.initialized) && (isMultipart)) {
         logger.finest("Is a multi-part request, so parsing it.");
         thresholdSize=getThresholdSize();
         gcsBucketName=getDefaultBucket();
         factory=new GcsFileItemFactory(thresholdSize,gcsBucketName);
         upload=new ServletFileUpload(factory);
         requestWrapper=new GcsMultipartRequest(hRequest,upload);
         chain.doFilter(requestWrapper,response);
      } else {
         chain.doFilter(request, response);
      } // END if ((this.initialized) && (isMultipart))
      
      logger.exiting(_CLASS, 
            "doFilter(ServletRequest,ServletResponse,FilterChain)");   
   }

   /**
    * Filter initialization routine.
    * <p>Called by the web container to indicate to a filter that it is being 
    * placed into service.</p>
    * <p>The servlet container calls the {@code init} method exactly once after
    * instantiating the filter. The init method must complete successfully 
    * before the filter is asked to do any filtering work.</p>
    * <p>The web container cannot place the filter into service if the 
    * {@code init} method either.</p>
    * <ul>
    * <li>Throws a {@code ServletException}</li>
    * <li>Does not return within a time period defined by the web container</li>
    * </ul>
    * 
    * @param The {@code FilterConfig} used by the servlet container to pass
    * information during the initialization process.
    */
   @Override
   public void init(FilterConfig cfg) throws ServletException {
      logger.entering(_CLASS, "init(FilterConfig)",cfg);
      int thresholdSize=-1;
      AppIdentityService identityService=null;
      String tmpStr=null;
      StringBuilder msg=null;
     
      // Get the thresholdSize
      tmpStr=cfg.getInitParameter(THRESHOLD_SIZE_PARAMETER);
      try {
         thresholdSize=Integer.valueOf(tmpStr);
         setThresholdSize(thresholdSize);
      } catch (NumberFormatException ex) {
         logger.log(Level.WARNING, 
               "NumberFormatException translating the thresholdSize parameter.",
               ex);
         setThresholdSize(GcsFileItemFactory.SIZE_THRESHOLD);
      } // END try/catch
      
      // Get the default Google Cloud Storage bucket
      identityService=AppIdentityServiceFactory.getAppIdentityService();
      tmpStr=cfg.getInitParameter(DEFAULT_BUCKET_PARAMETER);
      if (tmpStr == null) {
         msg=new StringBuilder(80);
         msg.append("A value for the \"");
         msg.append(DEFAULT_BUCKET_PARAMETER);
         msg.append("\" was not found in the filter configuration. Using the ");
         msg.append("default Google Cloud Storage Bucket for the application.");
         logger.warning(msg.toString());
         tmpStr=identityService.getDefaultGcsBucketName();
         if (tmpStr == null) {
            logger.warning(
                  "The default Google Cloud Storage Bucket was not found.");
            this.initialized=false;
         } // END if (tmpStr == null)         
      } // if (tmpStr == null) 
      
      logger.exiting(_CLASS, "init(FilterConfig)");
   }
   //********** Accessor Methods
   //***** gcsBucketName
   private String getDefaultBucket() {
      logger.entering(_CLASS, "getBucketName()");
      logger.exiting(_CLASS, "getBucketName()",this.defaultBucket);
      return this.defaultBucket;
   }
   public void setDefaultBucket(String defaultBucket) {
      logger.entering(_CLASS, "setDefaultBucket(String)",defaultBucket);
      this.defaultBucket=defaultBucket;
      logger.exiting(_CLASS, "setDefaultBucket(String)");
   }
   //***** thresholdSize
   private int getThresholdSize() {
      logger.entering(_CLASS, "getThresholdSize()");
      logger.exiting(_CLASS, "getThresholdSize()",this.thresholdSize);
      return this.thresholdSize;
   }
   private void setThresholdSize(int size) {
      logger.entering(_CLASS, "setThresholdSize(int)",size);
      this.thresholdSize=size;
      logger.exiting(_CLASS, "setThresholdSize(String)");
   }
}