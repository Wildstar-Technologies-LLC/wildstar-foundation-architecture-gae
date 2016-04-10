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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Channels;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.InvalidFileNameException;
import org.apache.commons.fileupload.ParameterParser;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.wildstartech.wfa.io.ActionableInputStream;
import com.wildstartech.wfa.io.IOEvent;
import com.wildstartech.wfa.io.IOEventListener;
import com.wildstartech.wfa.objectcleaning.Cleanable;

public class GcsFileItem 
implements Cleanable, FileItem, IOEventListener {
   /** Used in object serialization. */
   private static final long serialVersionUID = 5063960723470458469L;
   private static final String _CLASS=GcsFileItem.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   /**
    * Default content charset to be used when no explicit charset
    * parameter is provided by the sender. Media subtypes of the
    * "text" type are defined to have a default charset value of
    * "ISO-8859-1" when received via HTTP.
    */
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
   
   /* Indicates if a FileItem instance represents a simple form field.*/
   private boolean isFormField=false;
   /* Cached content */
   private byte[] cachedContent=null;
   /* The size, in bytes, above which the item will be stored to disk. */
   private int sizeThreshold=GcsFileItemFactory.SIZE_THRESHOLD;
   /* The size of the file item, in bytes. */
   private long fileSize=-1l;
   /* The outputStream that will be used to store data. */
   private transient DeferredGcsFileOutputStream outputStream=null;
   /* The FileItem headers. */
   private FileItemHeaders headers=null;
   /* The content type passed by the browser or null if not defined. */
   private String contentType=null;
   /* The name of the form field. */
   private String fieldName=null;
   /* The original filename in the client's filesystem. */
   private String fileName=null;
   /* The name of the object as it exists in the Google Cloud Storage bucket. */
   private String gcsObjectName=null;
   /* The name of the Google Cloud Storage bucket in which this object is 
    * stored. */
   private String gcsBucketName=null;   
   
   /**
    * Constructs a new {@code GcsFileItem} instance.
    *
    * @param fieldName The name of the form field.
    * @param contentType The content type passed by the browser or {@code null}
    * if not specified.
    * @param isFormField Whether or not this item is a plain form field, as
    * opposed to a file upload.
    * @param fileName  The original filename in the client's filesystem, or
    * {@code null} if not specified.                     
    * @param sizeThreshold The threshold, in bytes, below which items will be
    * retained in memory and above which they will be stored as a file.
    * @param gcsBucketName The name of the Google Cloud Storage bucket in
    * which the content will be stored if it exceeds the specified threshold.
    */
   public GcsFileItem(String fieldName, 
         String contentType, 
         boolean isFormField, 
         String fileName,
         int sizeThreshold, 
         String gcsBucketName) {
    setFieldName(fieldName);
    setContentType(contentType);
    setFormField(isFormField);
    setName(fileName);
    setSizeThreshold(sizeThreshold);
    setGcsBucketName(gcsBucketName);
   }
   
   private String buildGcsObjectName() {
      logger.entering(_CLASS, "buildGcsObjectName()");
      int length=0;
      String name=null;
      String objectName=null;
      StringBuilder sb=null;
      UUID uuid=null;
      
      sb=new StringBuilder();
      uuid=UUID.randomUUID();
      sb.append("GcsFileItem_");
      sb.append(uuid.toString().replace('-', '_')).append("-");
      name=this.fileName;
      
      if ((name != null) && (name.length() > 0)) {
         length=sb.length() + 1;
         //name=name.replaceAll("[]*?#", "_");
         if (length + name.length() > 1024) {
            sb.append(name.substring(0,1024 - length));
         } else {
            sb.append(name);
         } // END if (length + fileName.length() > 1024)
      } else {
         sb.append("unspecifiedObjectName");
      } // END if ((fileName != null) && (fileName.length() > 0))
      
      objectName=sb.toString();
      
      logger.exiting(_CLASS, "buildGcsObjectName()",objectName);
      return objectName;
   }
   
   public boolean clean() {
      logger.entering(_CLASS, "clean()");
      boolean result=true;
      
      if (!isInMemory()) {
         /* The contents of this FileItem are stored in Google Cloud Storage, 
          * so remove it. */
         delete();
      } // END if (!isInMemory())
      
      logger.entering(_CLASS, "clean()",result);
      return result;
   }
   /**
    * Deletes the underlying storage for a file item, including deleting any 
    * associated temporary disk file.
    * <p>Deletes the underlying storage for a file item, including deleting any
    * associated temporary disk file. Although this storage will be deleted 
    * automatically when the FileItem instance is garbage collected, this 
    * method can be used to ensure that this is done at an earlier time, thus
    * preserving system resources.</p>
    */
   @Override
   public void delete() {
      logger.entering(_CLASS, "delete()");
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
      } // END if ((gcsObjectName != null) && (gcsBucketName != null)) 
      logger.exiting(_CLASS, "delete()");
   }
   
   /**
    * Returns the content character set passed by the client.
    * @return The character set used for the content as passed by the client. 
    * {@code null} if it is not defined. 
    */
   public String getCharSet() {
      logger.entering(_CLASS, "getCharSet()");
      Map<String, String> parameters=null;
      ParameterParser parser=null;
      String charSet=null;
      String contentType=null;
      
      contentType=getContentType();
      parser=new ParameterParser();
      parameters=parser.parse(contentType, ';');
      charSet=parameters.get("charset");
      
      logger.exiting(_CLASS, "getCharSet()",charSet);
      return charSet;
   }
   /**
    * Returns the contents of the file item as a String, using the default 
    * character encoding.
    * <p>Returns the contents of the file item as a String, using the default 
    * character encoding. This method uses {@code get()} to retrieve the 
    * contents of the item.</p>
    * 
    * @return The contents of the item, as a string.
    */
   @Override
   public String getString() {
      logger.entering(_CLASS, "getString()");
      byte[] data=null;
      String charSet=null;
      String dataStr=null;
      
      charSet=getCharSet();
      if (charSet == null) {
         charSet=GcsFileItem.DEFAULT_CHARSET;
      } // END if (charSet == null)
      data=get();
      try {
         dataStr=new String(data,charSet);
      } catch (UnsupportedEncodingException ex) {
         logger.log(Level.WARNING,
               "UnsupportedEncodingException thrown.",
               ex);
         dataStr=new String(data);
      } // END try/catch
      logger.exiting(_CLASS, "getString()",dataStr);
      return dataStr;
   }
   
   /**
    * Returns the contents of the file item as a String, using the default 
    * character encoding.
    * 
    * <p>Returns the contents of the file item as a String, using the specified 
    * encoding. This method uses {@code get()} to retrieve the contents 
    * of the item.</p>
    * 
    * @param encoding - The character encoding to use.
    * @return The contents of the item, as a {@code String}
    * @throws {@code UnsupportedEncodingException} - if the requested 
    * character encoding is not available.
    */
   @Override
   public String getString(String encoding) 
         throws UnsupportedEncodingException {
      logger.entering(_CLASS, "getString()");
      byte[] data=null;
      String dataStr=null;
      
      data=get();
      dataStr=new String(data,encoding);
      
      logger.exiting(_CLASS, "getString()",dataStr);
      return dataStr;
   }
   /** 
    * IOEventListener
    */
   public void onEvent(IOEvent event) {
      logger.entering(_CLASS, "onEvent(IOEvent)",event);
      DeferredTask task=null;
      Queue taskQueue=null;
      
      if (
            (event != null) && 
            (event.getEventType() == IOEvent.EventType.Close)
         ) {
         taskQueue = QueueFactory.getDefaultQueue();
         // Have the task fire in 5 minutes
         task=new DeleteGcsObjectTask(this);
         taskQueue.add(
               TaskOptions.Builder.withPayload(task)
               .etaMillis(System.currentTimeMillis() + 300000)
         );
      } // END if ((event != null) && (event.getEventType() == IOEvent ...
      
      logger.exiting(_CLASS, "onEvent(IOEvent)");
   }
   /**
    * Returns a {@code String} representation of the object.
    */
   public String toString() {
      logger.entering(_CLASS, "toString()");
      String str=null;
      StringBuilder sb=null;
      
      sb=new StringBuilder(255);
      sb.append(_CLASS);
      sb.append("[name=").append(getName());
      sb.append(", bucket=").append(getGcsBucketName());
      sb.append(", objectName=").append(getGcsObjectName());
      sb.append(", size="+getSize());
      sb.append(", isFormField=").append(Boolean.valueOf(isFormField()));
      sb.append(", fieldName=").append(getFieldName());
      sb.append("]");
      logger.exiting(_CLASS, "toString()",str);
      return str;
   }
   /**
    * A convenience method to write an uploaded item to disk.
    * 
    * <p>A convenience method to write an uploaded item to disk. The client 
    * code is not concerned with whether or not the item is stored in memory, 
    * or on disk in a temporary location. They just want to write the uploaded 
    * item to a file.</p>
    * <p>This method is not guaranteed to succeed if called more than once for
    * the same item. This allows a particular implementation to use, for 
    * example, file renaming, where possible, rather than copying all of the 
    * underlying data, thus gaining a significant performance benefit.</p>
    * @param file - The {@code File} into which the uploaded item should 
    * be stored.
    * @throws Exception - if an error occurs.
    */
   @Override
   public void write(File file) throws Exception {
      throw new UnsupportedOperationException();
   }
   
   //********** Accessor Methods
   /**
    * Returns the contents of the file item as an array of bytes.
    * @return The contents of the file item as an array of bytes.
    */
   @Override
   public byte[] get() {
      logger.entering(_CLASS, "get()");
      byte[] bytesToReturn=null;
      InputStream in=null;
      
      if (isInMemory()) {
         if (
               (this.cachedContent== null) &&
               (this.outputStream != null) 
            ) {
            this.cachedContent=this.outputStream.getData();
         }
         bytesToReturn=this.cachedContent;
      } else {
         bytesToReturn=new byte[(int)getSize()];
         try {
            in=getInputStream();
            IOUtils.readFully(in, bytesToReturn);
         } catch (IOException ex) {
            logger.log(Level.SEVERE,
                  "IOException thrown reading data.",
                  ex);
         } finally {
            IOUtils.closeQuietly(in);
         } // END try/catch
      } // END if (isInMemory())
      logger.exiting(_CLASS, "get()",bytesToReturn);
      return bytesToReturn;
   }
   
   //***** contentType
   /**
    * Returns the content type passed by the browser or null if not defined.
    * @return The content type passed by the browser or {@code null} if not
    * defined.
    */
   @Override
   public String getContentType() {
      logger.entering(_CLASS, "getContentType()");
      logger.exiting(_CLASS, "getContentType()",this.contentType);
      return this.contentType;
   }
   /**
    * Sets the content type passed by the browser or null if not defined.
    * @param contentType The content type passed by the browser or {@code null}
    * if not defined.
    */
   public void setContentType(String contentType) {
      logger.entering(_CLASS, "getContentType()");
      this.contentType=contentType;
      logger.exiting(_CLASS, "getContentType()");
   }
   
   //***** fieldName
   /**
    * Returns the name of the field in the multipart form corresponding to this 
    * file item.
    * <p>Returns the name of the field in the multipart form corresponding to 
    * this file item.</p>
    * @return The name of the form field.
    */
   @Override
   public String getFieldName() {
      logger.entering(_CLASS, "getFieldName()");
      logger.exiting(_CLASS, "getInputStream()",this.fieldName);
      return this.fieldName;
   }
   /**
    * Sets the field name used to reference this file item.
    * @param isFieldName
    */
   @Override
   public void setFieldName(String fieldName) {
      logger.entering(_CLASS, "setFieldName(String)",fieldName);
      this.fieldName=fieldName;
      logger.exiting(_CLASS, "setFieldName(String)");
   }
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
      logger.exiting(_CLASS, "setGcsBucketName(String)");
   }      
   //***** gcsObjectName
   public String getGcsObjectName() {
      logger.entering(_CLASS, "getGcsObjectName()");
      if (this.gcsObjectName == null) {
         setGcsObjectName(buildGcsObjectName());
      } // END if (this.gcsObjectName == null) 
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
   //***** headers
   @Override
   public FileItemHeaders getHeaders() {
      logger.entering(_CLASS, "getHeaders()");
      logger.exiting(_CLASS, "getHeaders()",this.headers);
      return this.headers;
   }
   @Override
   public void setHeaders(FileItemHeaders headers) {
      logger.entering(_CLASS, "setHeaders(FileItemHeaders)",headers);
      this.headers=headers;
      logger.exiting(_CLASS, "setHeaders(FileItemHeaders)");
   }
   
   //***** inputStream
   /**
    * Returns an {@code InputStream} that can be used to retrieve the 
    * contents of the file.
    * @return An {@code InputStream} that can be used to retrieve the 
    * contents of the file.
    * @throws IOException If an error occurs.
    */
   @Override
   public InputStream getInputStream() throws IOException {
      logger.entering(_CLASS, "getInputStream()");
      boolean inMemory=false;
      ActionableInputStream aIn=null;
      GcsFilename gcsFileName=null;
      GcsInputChannel channel=null;
      GcsService service=null;
      InputStream in=null;
      String gcsBucketName=null;
      String gcsObjectName=null;
      
      inMemory=isInMemory();
      if (!inMemory) {
         gcsBucketName=getGcsBucketName();
         gcsObjectName=getGcsObjectName();
         if (
               (gcsObjectName != null) && 
               (gcsObjectName.length() > 0)
            ) {
            gcsFileName=new GcsFilename(gcsBucketName,gcsObjectName);
            service=GcsServiceFactory.createGcsService(
                  RetryParams.getDefaultInstance());
            try {
               channel=service.openReadChannel(gcsFileName,0);
               aIn=new ActionableInputStream(Channels.newInputStream(channel));
               aIn.addEventListener(this);
               in=new GZIPInputStream(aIn);            
            } catch (ZipException ex) {
               // Thrown if the GZIP format error has occurred or the compression 
               // method used is unsupported.
               logger.fine("Not a compressed stream.");
               IOUtils.closeQuietly(channel);
            } catch (IOException ex) {
               logger.log(Level.SEVERE, 
                     "IOException thrown opening GcsInputChannel.", 
                     ex);
               IOUtils.closeQuietly(in);
            } // END try/catch
            
            if (in == null) {
               // The input stream IS NOT a compressed input stream, so let's 
               // open a "normal" one.
               try {
                  channel=service.openReadChannel(gcsFileName,0);
                  in=Channels.newInputStream(channel);
               } catch (IOException ex) {
                  logger.log(Level.SEVERE, 
                        "IOException thrown opening GcsInputChannel.", 
                        ex);
               } // END try/catch               
            } // END if (in == null)
         } else { 
            logger.severe("SHOULD NOT GET HERE.");
         } // END if ((gcsObjectName != null) && (gcsObjectName.length() > 0))
      } else {
         if (this.cachedContent == null) {
            this.cachedContent=this.outputStream.getData();            
         } // END if (this.cachedContent == null)
         in=new ByteArrayInputStream(this.cachedContent);
      } // END if ((gcsObjectName == null) || (gcsObjectName.length() == 0))
      logger.exiting(_CLASS, "getInputStream()",in);
      return in;
   }
   
   //***** isFormField
   /**
    * Determines whether or not a FileItem instance represents a simple form 
    * field.
    * <p>Determines whether or not a FileItem instance represents a simple form
    * field.</p>
    * @return {@code true} if the instance represents a simple form field;
    * {@code false} if it represents an uploaded file.
    */
   @Override
   public boolean isFormField() {
      logger.entering(_CLASS, "isFormField()");
      logger.exiting(_CLASS, "isFormField()",this.isFormField);
      return this.isFormField;
   }
   /**
    * Specifies whether or not a FileItem instance represents a simple form 
    * field.
    * <p>Specifies whether or not a FileItem instance represents a simple form
    * field.</p>
    * @param state - {@code true} if the instance represents a simple form 
    * field; {@code false} if it represents an uploaded file.
    */
   @Override
   public void setFormField(boolean state) {
      logger.entering(_CLASS, "setFormField(boolean)",state);
      this.isFormField=state;
      logger.exiting(_CLASS, "setFormField(boolean)");
   }
   //***** isInMemory
   /**
    * Provides a hint as to whether or not the file contents will be read from 
    * memory.
    * @return {@code true} if the file contents will be read from memory; 
    * {@code false} otherwise.
    */
   @Override
   public boolean isInMemory() {
      logger.entering(_CLASS, "isInMemory()");
      boolean inMemory=false;
      if (this.cachedContent != null) {
         inMemory=true;
      } else {
         inMemory=this.outputStream.isInMemory();
      } // END if (this.cachedContent != null)
      logger.exiting(_CLASS, "isInMemory()",inMemory);
      return inMemory;
   }
   
   //***** name
   /**
    * Returns the original filename in the client's filesystem, as provided by 
    * the browser (or other client software).
    * 
    * <p>Returns the original filename in the client's filesystem, as provided 
    * by the browser (or other client software). In most cases, this will be the
    * base file name, without path information. However, some clients, such as
    * the Opera browser, do include path information.</p>
    * @return The original filename in the client's filesystem.
    * @throws org.apache.commons.fileupload.InvalidFileNameException - The file
    * name contains a NUL character, which might be an indicator of a security 
    * attack. If you intend to use the file name anyways, catch the exception 
    * and use {@code InvalidFileNameException.getName()}.
    */
   @Override
   public String getName() throws InvalidFileNameException {
      logger.entering(_CLASS, "getName()");
      String fileNameToReturn=null;
      fileNameToReturn=Streams.checkFileName(this.fileName);
      return fileNameToReturn;
   }
   
   /**
    * Sets the original filename on the client's filesystem as it was provided
    * by the client.
    * @param fileName The filename on the client's filesystem as it was provided
    * by the client. 
    */
   public void setName(String fileName) {
      logger.entering(_CLASS, "setName(String)",fileName);
      this.fileName=fileName;
      logger.exiting(_CLASS, "setName(String)");
   }
   //***** outputStream
   /**
    * Returns the contents of the file as an array of bytes.  If the
    * contents of the file were not yet cached in memory, they will be
    * loaded from the disk storage and cached.
    *
    * @return The contents of the file as an array of bytes
    * or {@code null} if the data cannot be read
    */
   @Override
   public OutputStream getOutputStream() throws IOException {
      logger.entering(_CLASS, "getOutputStream()");
      int threshold=0;
      String gcsBucketName=null;
      String gcsObjectName=null;
      
      if (this.outputStream == null) {
         threshold=getSizeThreshold();
         gcsBucketName=getGcsBucketName();
         gcsObjectName=getGcsObjectName();
         this.outputStream=new DeferredGcsFileOutputStream(
               threshold,gcsBucketName,gcsObjectName);
      } // END if (this.outputStream == null)
      
      logger.entering(_CLASS, "getOutputStream()",this.outputStream);
      return this.outputStream;
   }
   
   //***** size
   /**
    * Returns the size of the file item.
    * @return The size of the file item, in bytes.
    */
   @Override
   public long getSize() {
      logger.entering(_CLASS, "getSize()");
      long size=-1l;
      
      if (this.fileSize >= 0) {
         size=this.fileSize;
      } else if (this.cachedContent != null) {
         size=this.cachedContent.length;
      } else if (this.outputStream.isInMemory()) {
         size=this.outputStream.getData().length;        
      } else {
         size=this.outputStream.getByteCount();
      } // END if (this.fileSize >= 0)
      logger.exiting(_CLASS, "getSize()",size);
      return size;
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