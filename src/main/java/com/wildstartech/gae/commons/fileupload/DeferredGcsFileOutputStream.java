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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.channels.Channels;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.output.ThresholdingOutputStream;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

/**
 * An output stream that will be retained in memory until the specified 
 * threshold is reached.  After the threshold is reached, the contents of the
 * outputStream will be committed to Google Cloud Storage.
 * <p>This class is used as part of the Apache Commons FileUpload processing
 * where the persistent storage is a Google Cloud Storage volume as opposed to 
 * some physical disk that is accessible to the application server.  If the 
 * size of the file is below the specified threshold, the contents of the file
 * will be retained in memory (which is faster).  Otherwise, the contents of the
 * file will be written to the specified Google Cloud Storage bucket (identified
 * by the {@code gcsBucketName} property) using the {@code gcsObjectName}.
 * 
 * <p>The following are items to be aware of:</p>
 * <ul>
 * <li>If the {@code OutputStream} is closed before reaching the specified
 * threshold, then the data will NEVER be written to Google Cloud Storage.</li>
 * </ul> 
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 * @version 0.1, 2015-12-14
 *
 */
public class DeferredGcsFileOutputStream 
extends ThresholdingOutputStream 
implements Serializable {
   /** Used in object serialization. */
   private static final long serialVersionUID = 3882500836779911566L;
   private static final String _CLASS=
         DeferredGcsFileOutputStream.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   /* Indicates whether or not the specified threshold has been exceeded. */
   private boolean thresholdExceeded=false;
   /* The stream to which data will be written prior to the threshold being 
    * reached. */
   private transient ByteArrayOutputStream memoryCache=null;
   /* The current {@code OutputStream} to which data will be written to. */
   private transient OutputStream currentOutputStream=null;
   /* The name of the object as it exists in the Google Cloud Storage bucket. */
   private String gcsObjectName=null;
   /* The name of the Google Cloud Storage bucket in which this object is 
    * stored. */
   private String gcsBucketName=null;
   
   public DeferredGcsFileOutputStream(int sizeThreshold) {
      super(sizeThreshold);
      logger.entering(_CLASS, "DeferredGcsFileOutputStream(int)",sizeThreshold);
      this.memoryCache=new ByteArrayOutputStream(sizeThreshold);
      this.currentOutputStream=this.memoryCache;
      logger.exiting(_CLASS, "DeferredGcsFileOutputStream(int)",sizeThreshold);
   }
   
   public DeferredGcsFileOutputStream(int sizeThreshold,
         String gcsBucketName,
         String gcsObjectName) {
      super(sizeThreshold);
      logger.entering(_CLASS, "DeferredGcsFileOutputStream(int,String,String)",
            new Object[] {sizeThreshold,gcsBucketName,gcsObjectName});
      this.memoryCache=new ByteArrayOutputStream(sizeThreshold);
      this.currentOutputStream=this.memoryCache;
      setGcsBucketName(gcsBucketName);
      setGcsObjectName(gcsObjectName);
      logger.exiting(_CLASS, "DeferredGcsFileOutputStream(int,String,String)");
   }
   
   //********** Utility Methods
   /**
    * Closes underlying output stream, and mark this as closed
    *
    * @exception IOException if an error occurs.
    */
   public void close() throws IOException {
      logger.entering(_CLASS, "close()");
      if (!isInMemory()) {
         /* There is no point flushing and closing a ByteArrayOutputStream, so
          * we will only attempt to close the outputStream if it is NOT in 
          * memory. */
         this.currentOutputStream.flush();
         this.currentOutputStream.close();
      } // END  if (!isInMemory())
      logger.exiting(_CLASS, "close()");
   }
   
   /**
    * Returns data contained in this {@code OutputStream} as an array of 
    * {@code bytes} if it is stored in memory.  If the data has been
    * written to Google Cloud Storage, then {@code null} is returned.
    */
   
   public byte[] getData() {
      logger.entering(_CLASS,"getData()");
      boolean cached=false;
      byte[] content=null;
      
      cached=isInMemory();
      if (cached) {
         content=this.memoryCache.toByteArray();
      } // END if (cached)
      
      logger.exiting(_CLASS,"getData()",content);
      return content;
   }
   
   /**
    * Indicates whether the data for this {@code OutputStream} resides in 
    * memory or in a Google Cloud Storage bucket.
    * 
    * @return {@code true} if the data is stored in memory; {@code false} if
    * the data is stored in a Google Cloud Storage bucket.
    */
   public boolean isInMemory() {
      logger.entering(_CLASS, "isInMemory()");
      logger.exiting(_CLASS, "isInMemory()",!this.thresholdExceeded);
      return !this.thresholdExceeded;
   }
   
   /**
    * Switches the underlying output stream from a memory based stream to one
    * that is backed by disk. This is the point at which we realise that too
    * much data is being written to keep in memory, so we elect to switch to
    * disk-based storage.
    *
    * @exception IOException if an error occurs.
    */
   @Override
   protected void thresholdReached() throws IOException {
      logger.entering(_CLASS, "thresholdReached()");
      GcsFilename gcsFileName=null;
      GcsOutputChannel channel=null;
      GcsService service=null;
      OutputStream out=null;
      String gcsBucketName=null;
      String gcsObjectName=null;
      
      setThresholdExceeded(true);
      gcsBucketName=getGcsBucketName();
      gcsObjectName=getGcsObjectName();
      gcsFileName=new GcsFilename(gcsBucketName,gcsObjectName); 
      service=GcsServiceFactory.createGcsService(
            RetryParams.getDefaultInstance());
      try {
         channel=service.createOrReplace(gcsFileName, 
               GcsFileOptions.getDefaultInstance());
         out=new GZIPOutputStream(Channels.newOutputStream(channel));         
      } catch (IOException ex) {
         logger.log(Level.SEVERE, 
               "IOException thrown writing to Google Cloud Storage.", 
               ex);
      } // END try/catch
      this.memoryCache.writeTo(out);
      // Set the currentOutputStream equal to the GCS outputstream
      this.currentOutputStream=out;
      logger.exiting(_CLASS, "thresholdReached()");
   }
   
   /**
    * I don't want this method here, but I'm going to check to see if it is
    * used anywhere.
    * @param out
    */
   public void writeTo(OutputStream out) {
      throw new UnsupportedOperationException();
   }
   
   //********** Accessor methods
   //***** gcsBucketName
   public String getGcsBucketName() {
      logger.entering(_CLASS, "getGcsBucketName()");
      logger.entering(_CLASS, "getGcsBucketName()",this.gcsBucketName);
      return this.gcsBucketName;
   }
   public void setGcsBucketName(String gcsBucketName) {
      logger.entering(_CLASS, "setGcsBucketName(String)",gcsBucketName);
      if (gcsBucketName == null) {
         this.gcsBucketName="";
      } else {
         this.gcsBucketName=gcsBucketName;
      } // END if (gcsBucketName == null) 
      logger.entering(_CLASS, "setGcsBucketName(String)");
   }      
   //***** gcsObjectName
   public String getGcsObjectName() {
      logger.entering(_CLASS, "getGcsObjectName()");
      logger.entering(_CLASS, "getGcsObjectName()",this.gcsObjectName);
      return this.gcsObjectName;
   }
   public void setGcsObjectName(String objectName) {
      logger.entering(_CLASS, "setGcsObjectName(String)",objectName);
      if (objectName == null) {
         this.gcsObjectName="";
      } else {
         this.gcsObjectName=objectName;
      } // END if (objectName == null)
      logger.entering(_CLASS, "setGcsObjectName(String)");
   }
   
   //***** stream
   /**
    * Returns the current output stream. This may be memory based or disk
    * based, depending on the current state with respect to the threshold.
    *
    * @return The underlying output stream.
    *
    * @exception IOException if an error occurs.
    */
   @Override
   protected OutputStream getStream() throws IOException {
      logger.entering(_CLASS, "getStream()");
      logger.exiting(_CLASS, "getStream()",this.currentOutputStream);
      return this.currentOutputStream;
   } 
   
   //***** thresholdExceeded
   public boolean isThresholdExceeded() {
      logger.entering(_CLASS, "isThresholdExceeded()");
      logger.exiting(_CLASS, "isThresholdExceeded()",this.thresholdExceeded);
      return this.thresholdExceeded;
   }
   
   public void setThresholdExceeded(boolean thresholdExceeded) {
      logger.entering(_CLASS, "setThresholdExceeded(boolean)",
            thresholdExceeded);
      this.thresholdExceeded=thresholdExceeded;
      logger.exiting(_CLASS, "setThresholdExceeded(boolean)");
   }
}