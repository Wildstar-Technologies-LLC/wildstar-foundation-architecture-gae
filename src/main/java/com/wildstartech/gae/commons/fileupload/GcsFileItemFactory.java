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

import java.util.logging.Logger;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;

import com.wildstartech.wfa.objectcleaning.ObjectCleaningTracker;
/**
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 * @version 0.1, 2015-12-14
 *
 */
public class GcsFileItemFactory implements FileItemFactory {
   private static final String _CLASS=GcsFileItemFactory.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   /**
    * Default size, in bytes,  of an item above which the contents of the item 
    * will be stored in Google Cloud Storage versus memory (2MB).
    */
   public static final int SIZE_THRESHOLD=1024*2048;
   
   /* The size, in bytes, above which the item will be stored to disk. */
   private int sizeThreshold=GcsFileItemFactory.SIZE_THRESHOLD;
   /* The name of the Google Cloud Storage bucket in which objects are stored.*/
   private String gcsBucketName=null;
   /* Reference to the ThreadedObjectCleaningTracker that will be used to clean all
    * temporary objects that are created in the course of uploading files. */
   private ObjectCleaningTracker objectCleaningTracker=null;
   /**
    * When adding FileItems created by this factory to the ThreadedObjectCleaningTracker
    * the reference object will be the key object used to determine when the 
    * GcsFileItem object can be garbage collected and it's associated file in 
    * the Google Cloud Storage filesystem can be removed.
    */
   private Object referenceObject=null;
   
   
   /**
    * Default, no-argument constructor.
    */
   public GcsFileItemFactory() {
      logger.entering(_CLASS, "GcsFileItemFactory()");
      setSizeThreshold(GcsFileItemFactory.SIZE_THRESHOLD);
      setGcsBucketName("");
      logger.exiting(_CLASS, "GcsFileItemFactory()");
   }
   /**
    * Constructor taking the size threshold and bucket name as parameters.
    * @param sizeThreshold The maximum size of a file to be stored in memory
    * after which the file contents will be written to a temporary file in 
    * Google Cloud Storage.
    * @param bucketName The name of the Google Cloud Storage bucket which 
    * should be used as the repository for the temporary file.
    */
   public GcsFileItemFactory(int sizeThreshold, String bucketName) {
      logger.entering(_CLASS, "GcsFileItemFactory(int,String)",
            new Object[] {sizeThreshold, bucketName});
      setSizeThreshold(sizeThreshold);
      setGcsBucketName(bucketName);
      logger.entering(_CLASS, "GcsFileItemFactory(int,String)");
   }
   
   //********** Utility Methods
   @Override
   public FileItem createItem(String fieldName, 
         String contentType, 
         boolean isFormField,
         String fileName) {
      logger.entering(_CLASS, "createItem(String,String,boolean,String)",
            new Object[] {fieldName,contentType,isFormField,fileName});
      int threshold=0;
      GcsFileItem gcsFileItem=null;
      Object referenceObject=null;
      ObjectCleaningTracker tracker=null;
      String gcsBucketName=null;
      
      gcsBucketName=getGcsBucketName();
      referenceObject=getReferenceObject();
      threshold=getSizeThreshold();
      tracker=getObjectCleaningTracker();
      
      gcsFileItem=new GcsFileItem(
            fieldName,
            contentType,
            isFormField,
            fileName,
            threshold,
            gcsBucketName);
      //TODO
      /* 2015-12-21  
       * Disabling because background threads are ONLY available to backend
       * instances of App Engine and we're not running in that way.
      if (tracker != null) {
         tracker.addTracker(gcsFileItem, referenceObject);
      } // END if (tracker != null)
      */
      logger.exiting(_CLASS, 
            "createItem(String,String,boolean,String)",
            gcsFileItem);
      return gcsFileItem;
   }
   //********** Accessor Methods
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
   
   //***** objectCleaningTracker
   public ObjectCleaningTracker getObjectCleaningTracker() {
      logger.entering(_CLASS, "getObjectCleaningTracker()");
      logger.exiting(_CLASS, "getObjectCleaningTracker()",
            this.objectCleaningTracker);
      return this.objectCleaningTracker;
   }
   public void setObjectCleaningTracker(ObjectCleaningTracker tracker) {
      logger.entering(_CLASS, 
            "setObjectCleaningTracker(ThreadedObjectCleaningTracker)",tracker);
      this.objectCleaningTracker=tracker;
      logger.exiting(_CLASS,"setObjectCleaningTracker(ThreadedObjectCleaningTracker)");
   }
   
   //***** referenceObject
   public Object getReferenceObject() {
      logger.entering(_CLASS,"getReferenceObject()");
      logger.exiting(_CLASS,"getReferenceObject()",this.referenceObject);
      return this.referenceObject;
   }
   public void setReferenceObject(Object object) {
      logger.entering(_CLASS, "setReferenceObject(Object)",object);
      this.referenceObject=object;
      logger.exiting(_CLASS, "setReferenceObject(Object)");
   }
   
   //***** sizeThreshold
   /**
    * Returns the size of an item, in bytes, above which the contents will be
    * stored in Google Cloud Storage as opposed to remaining in memory.
    * @return The size of the item, in bytes, above which an item will be stored
    * in Google Cloud Storage as opposed to remaining in memory.
    */
   public int getSizeThreshold() {
      logger.entering(_CLASS, "getSizeThreshold()");
      logger.exiting(_CLASS, "getSizeThreshold()",this.sizeThreshold);
      return this.sizeThreshold;
   }
   /**
    * Sets the size of an item, in bytes, above which the contents will be 
    * stored in a Google Cloud Storage bucket as opposed to remaining in memory.
    *  
    * @param sizeThreshold The size of an item, in bytes, above which the 
    * contents will be stored in a Google Cloud Storage bucket as opposed to 
    * remaining in memory.
    */
   public void setSizeThreshold(int sizeThreshold) {
      logger.entering(_CLASS, "setSizeThreshold(int)",sizeThreshold);
      if (sizeThreshold < 0) {
         this.sizeThreshold=GcsFileItemFactory.SIZE_THRESHOLD;
      } else {
         this.sizeThreshold=sizeThreshold;
      } // END if (sizeThreshold < 0
      logger.exiting(_CLASS, "setSizeThreshold(int)");
   }
}