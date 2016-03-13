/*
 * Copyright 2015 Wildstar Technologies, LLC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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