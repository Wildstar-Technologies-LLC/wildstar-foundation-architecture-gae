package com.wildstartech.gae.commons.fileupload;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public class DeleteGcsObjectTask implements DeferredTask {
   /** Used in object serialization. */
   private static final long serialVersionUID = 6764799084941377613L;
   private static final String _CLASS=DeleteGcsObjectTask.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   private String gcsBucketName=null;
   private String gcsObjectName=null;
   
   /**
    * Default, no-argument constructor.
    */
   public DeleteGcsObjectTask() {
      logger.entering(_CLASS, "DeleteGcsObjectTask()");
      logger.exiting(_CLASS, "DeleteGcsObjectTask()");
   }
   public DeleteGcsObjectTask(String gcsBucketName, String gcsObjectName) {
      logger.entering(_CLASS, "DeleteGcsObjectTask(String, String)", 
            new Object[] {gcsBucketName, gcsObjectName});
      setGcsBucketName(gcsBucketName);
      setGcsObjectName(gcsObjectName);
      logger.entering(_CLASS, "DeleteGcsObjectTask(String, String)");
   }
   /**
    * Constructor taking a {@code GcsFileItem} object as an initialization 
    * parameter.
    * @param fileItem - The {@code GcsFileItem} that should be removed when
    * the task expires.
    */
   public DeleteGcsObjectTask(GcsFileItem fileItem) {
      logger.entering(_CLASS, "DeleteGcsObjectTask(GcsFileItem)", fileItem);
      if (fileItem != null) {
         setGcsBucketName(fileItem.getGcsBucketName());
         setGcsObjectName(fileItem.getGcsObjectName());
      } // END if (fileItem != null)
      logger.exiting(_CLASS, "DeleteGcsObjectTask(GcsFileItem)");
   }
   //********** Utility Methods
   @Override
   public void run() {
      logger.entering(_CLASS, "run()");
      GcsFilename gcsFileName=null;
      GcsService service=null;
      String gcsBucketName=null;
      String gcsObjectName=null;
      
      gcsBucketName=getGcsBucketName();
      gcsObjectName=getGcsObjectName();
      
      if ((gcsObjectName != null) && (gcsBucketName != null)) {
         gcsFileName=new GcsFilename(gcsBucketName,gcsObjectName);
         service=GcsServiceFactory.createGcsService(
               RetryParams.getDefaultInstance());
         try {
            service.delete(gcsFileName);
         } catch (IOException ex) {
            logger.log(Level.SEVERE,
                  "IOException thrown deleting GcsFile",
                  ex);            
         } // END try/catch
      } else {
         if (gcsBucketName == null) {
            logger.warning(
                  "No delete performed as the gcsBucketName was null.");
         } // END if (gcsBucketName == null)
         if (gcsObjectName == null) {
            logger.warning(
                  "No delete performed as the gcsObjectName was null.");
         } // END if (gcsObjectName == null)
      } // END if ((gcsObjectName != null) && (gcsBucketName != null)) 
      logger.exiting(_CLASS, "run()");
   }
   public String toString() {
      logger.entering(_CLASS, "toString()");
      String str=null;
      StringBuilder sb=null;
      sb=new StringBuilder(80);
      sb.append(_CLASS);
      sb.append(" [gcsBucketName=\"").append(getGcsBucketName());
      sb.append("\", gcsObjectName=\"").append(getGcsObjectName());
      sb.append("\"]");
      str=sb.toString();
      logger.exiting(_CLASS, "toString()",str);
      return str;
   }
   //********** Accessor Methods
   //***** gcsBucketName
   public String getGcsBucketName() {
      logger.entering(_CLASS, "getGcsBucketName()");
      logger.exiting(_CLASS, "getGcsBucketName()",this.gcsBucketName);
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
      logger.exiting(_CLASS, "getGcsObjectName()",this.gcsObjectName);
      return this.gcsObjectName;
   }
   public void setGcsObjectName(String objectName) {
      logger.entering(_CLASS, "setGcsObjectName(String)",objectName);
      if (objectName == null) {
         this.gcsObjectName="";
      } else {
         this.gcsObjectName=objectName;
      } // END if (objectName == null)
      logger.exiting(_CLASS, "setGcsObjectName(String)");
   }
}
