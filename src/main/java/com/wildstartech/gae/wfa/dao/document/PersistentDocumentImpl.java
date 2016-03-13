package com.wildstartech.gae.wfa.dao.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.wildstartech.gae.wfa.dao.WildObjectImpl;
import com.wildstartech.wfa.dao.UserContext;
import com.wildstartech.wfa.dao.document.PersistentDocument;
import com.wildstartech.wfa.document.Document;
import com.wildstartech.wfa.document.DocumentNameTooLongException;

public class PersistentDocumentImpl
extends WildObjectImpl<Document>
implements PersistentDocument {
   /** Used in object serialization. */
   private static final long serialVersionUID = 2310551660483810607L;
   private static final String _CLASS=PersistentDocumentImpl.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   protected static final String _KIND="com.wildstartech.wfa.document.Document";
   
   private static final String GCSBUCKET="wildstarservicedesk-bkt000000001013";
   
   /** The size, in bytes, of the document. */
   private long size=0l;
   /** The MimeType of the document. */
   private String contentType=null;
   /** The MD5 hash associated with the document. */
   private String crc32c=null;
   /** The name of the file. **/
   private String gcsObjectName="";
   /** The Google Cloud storage bucket in which the file is stored. */
   private String gcsBucketName="";
   /** The kind of object this document is associated with. */
   private String relatedKind="";
   /** The identifier for the object this document is associated with. */
   private String relatedIdentifier = "";
   /** The name of the document. */
   private String name="";
   
   /**
    * Default, no-argumnet constructor.
    */
   public PersistentDocumentImpl() {
      super();
      logger.entering(_CLASS, "PersistentDocumentImpl()");
      setGcsBucketName(GCSBUCKET);
      logger.exiting(_CLASS, "PersistentDocumentImpl()");
   }
   
   //***** Utility Methods
   public static String buildGcsObjectName(Document document) {
      logger.entering(_CLASS, "buildGcsObjectName(Document)",document);
      int length=0;
      String name=null;
      String objectName=null;
      StringBuilder sb=null;
      UUID uuid=null;
      
      sb=new StringBuilder();
      uuid=UUID.randomUUID();
      sb.append(uuid.toString()).append("-");
      if (document != null) {
         name=document.getName();
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
      } else {
         logger.warning("The document parameter is null.");
         sb.append("unspecifiedObjectName");
      } // END if (document != null)
      objectName=sb.toString();
      
      logger.exiting(_CLASS, "buildGcsObjectName()",objectName);
      return objectName;
   }
   
   @Override
   public String getKind() {
      logger.entering(_CLASS, "getKind()");
      logger.exiting(_CLASS, "getKind()",PersistentDocumentImpl._KIND);
      return PersistentDocumentImpl._KIND;
   }
   
   @Override
   public void populateEntity(Entity entity) {
      logger.entering(_CLASS, "populateEntity(Entity)",entity);
      if (entity != null) {
         super.populateEntity(entity);
         entity.setProperty("contentType", getContentType());
         entity.setProperty("hash", getHash());
         entity.setProperty("gcsBucketName", getGcsBucketName());
         entity.setProperty("gcsObjectName", getGcsObjectName());
         entity.setProperty("name", getName());
         entity.setProperty("relatedIdentifier", getRelatedIdentifier());
         entity.setProperty("relatedKind", getRelatedKind());
         entity.setProperty("size", getSize());
      } else {
         logger.severe("The entity was null.");
      } // END if (entity != null)
      logger.entering(_CLASS, "populateEntity(Entity)");
   }
   
   @Override
   public void populateFromEntity(Entity entity, UserContext ctx) {
      logger.entering(_CLASS, "populateFromEntity(Entity)",entity);
      if (entity != null) {
         super.populateFromEntity(entity,ctx);
         setContentType(getPropertyAsString(entity,"contentType"));
         setGcsBucketName(getPropertyAsString(entity,"gcsBucketName"));
         setGcsObjectName(getPropertyAsString(entity,"gcsObjectName"));
         setHash(getPropertyAsString(entity,"hash"));
         setName(getPropertyAsString(entity,"name"));
         setRelatedIdentifier(getPropertyAsString(entity,"relatedIdentifier"));
         setRelatedKind(getPropertyAsString(entity,"relatedKind"));
         setSize(getPropertyAsLong(entity,"size"));
      }
      logger.exiting(_CLASS, "populateFromEntity(Entity)");
   }
   @Override
   public void populateFromObject(Document document) {
      logger.entering(_CLASS, "populateFromObject()",document);
      PersistentDocument pDocument=null;
      InputStream in=null;
      OutputStream out=null;
      
      if (document != null) {
         if (document instanceof PersistentDocument) {
            pDocument=(PersistentDocument) document;
            setRelatedIdentifier(pDocument.getRelatedIdentifier());
         } // END if (document instanceof PersistentDocument)
         if (document instanceof PersistentDocumentImpl) {
            setRelatedKind(
                  ((PersistentDocumentImpl) document).getRelatedKind()
            );
         } // END if (document instanceof PersistentDocumentImpl)
         setContentType(document.getContentType());
         setHash(document.getHash());
         setName(document.getName());
         setSize(document.getSize());
         in=document.getInputStream();
         out=getOutputStream();
         if ((in != null) && (out != null)) {
            try {
               IOUtils.copy(in, out);
            } catch (IOException ex) {
               logger.log(
                     Level.SEVERE, 
                     "IOException thrown copying document content.",
                     ex);
            } // END try/catch
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
         } // END if ((in != null) && (out != null))
         
      } // END if (document != null)
      logger.exiting(_CLASS, "populateFromObject()");
   }
      
   //********** Accessor Methods
   //***** contentType 
   @Override
   public String getContentType() {
      logger.entering(_CLASS, "getContentType()");
      logger.exiting(_CLASS, "getContentType()",this.contentType);
      return this.contentType;
   }
   @Override
   public void setContentType(String contentType) {
      logger.entering(_CLASS, "setContentType(String)",contentType);
      this.contentType=defaultValue(contentType);
      logger.exiting(_CLASS, "setContentType(String)");
   }
   //***** CRC32C
   /**
    * Returns the CRC32c 
    * @return
    */
   @Override
   public String getHash() {
      logger.entering(_CLASS, "getHash()");
      logger.exiting(_CLASS, "getHash()",this.crc32c);
      return this.crc32c;
   }
   @Override
   public void setHash(String hash) {
      logger.entering(_CLASS, "setHash(String)",hash);
      this.crc32c=defaultValue(hash);
      logger.exiting(_CLASS, "setHash(String)");
   }
   //***** gcsBucketName
   public String getGcsBucketName() {
      logger.entering(_CLASS, "getGcsBucketName()");
      logger.entering(_CLASS, "getGcsBucketName()",this.gcsBucketName);
      return this.gcsBucketName;
   }
   public void setGcsBucketName(String gcsBucketName) {
      logger.entering(_CLASS, "setGcsBucketName(String)",gcsBucketName);
      this.gcsBucketName=defaultValue(gcsBucketName);
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
      this.gcsObjectName=defaultValue(objectName);
      logger.entering(_CLASS, "setGcsObjectName(String)");
   }   
   
   //***** name
   @Override
   public String getName() {
      logger.entering(_CLASS, "getName()");
      logger.exiting(_CLASS, "getName()",this.name);
      return this.name;
   }
   @Override
   public void setName(String name) throws DocumentNameTooLongException {
      logger.entering(_CLASS, "setName(String)",name);
      this.name=defaultValue(name);
      logger.exiting(_CLASS, "setName(String)");
   }
   
   // ***** relatedIdentifier
   @Override
   public String getRelatedIdentifier() {
      logger.entering(_CLASS, "getRelatedIdentifier()");
      logger.exiting(_CLASS, "getRelatedIdentifier()", this.relatedIdentifier);
      return this.relatedIdentifier;
   }
   @Override
   public void setRelatedIdentifier(String relatedIdentifier) {
      logger.entering(_CLASS, "setRelatedIdentifier(String)",
            relatedIdentifier);
      this.relatedIdentifier = defaultValue(relatedIdentifier);
      logger.exiting(_CLASS, "setRelatedIdentifier(String)");
   }

   // ***** relatedKind
   public String getRelatedKind() {
      logger.entering(_CLASS, "getRelatedKind()");
      logger.exiting(_CLASS, "getRelatedKind()", this.relatedKind);
      return this.relatedKind;
   }

   public void setRelatedKind(String relatedKind) {
      logger.entering(_CLASS, "setRelatedKind(String)", relatedKind);
      this.relatedKind = defaultValue(relatedKind);
      logger.exiting(_CLASS, "setRelatedKing()");
   }
   
   //***** size 
   @Override
   public long getSize() {
      logger.entering(_CLASS, "getSize()");
      logger.exiting(_CLASS, "getSize()",this.size);
      return this.size;
   }
   @Override
   public void setSize(long size) {
      logger.entering(_CLASS, "setSize(long)",size);
      if (size < 0) {
         this.size=0;
      } else {
         this.size=size;
      } // END if (size < 0)
   }
   //***** streams
   /**
    * 
    */
   @Override
   public OutputStream getOutputStream() {
      logger.entering(_CLASS, "getOutputStream()");
      GcsFilename gcsFileName=null;
      GcsOutputChannel channel=null;
      GcsService service=null;
      OutputStream out=null;
      String gcsBucketName=null;
      String gcsObjectName=null;
      
      gcsBucketName=getGcsBucketName();
      gcsObjectName=getGcsObjectName();
      if ((gcsObjectName == null) || (gcsObjectName.length() == 0)) {
         gcsObjectName=buildGcsObjectName(this);
         setGcsObjectName(gcsObjectName);         
      } // END if ((gcsObjectName == null) || (gcsObjectName.length() == 0))
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
      
      logger.entering(_CLASS, "getOutputStream()",out);
      return out;
   }
   /**
    * Returns an InputStream that can be used to read the blobstore. 
    */
   @Override
   public InputStream getInputStream() {
      logger.entering(_CLASS, "getInputStream()");
      byte[] bytes=null;
      GcsFilename gcsFileName=null;
      GcsInputChannel channel=null;
      GcsService service=null;
      InputStream in=null;
      String gcsBucketName=null;
      String gcsObjectName=null;
      
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
            in=new GZIPInputStream(Channels.newInputStream(channel));            
         } catch (ZipException ex) {
            // Thrown if the GZIP format error has occurred or the compression 
            // method used is unsupported.
            logger.warning("Not a compressed stream.");
            channel.close();
         } catch (IOException ex) {
            logger.log(Level.SEVERE, 
                  "IOException thrown opening GcsInputChannel.", 
                  ex);
         } finally {
            if (in == null) {
               channel.close();
            }
         } // END try/catch
         
         if (in == null) {
            // The input stream IS NOT a compressed input stream, so let's open
            // a "normal" one.
            try {
               channel=service.openReadChannel(gcsFileName,0);
               in=Channels.newInputStream(channel);
            } catch (IOException ex) {
               logger.log(Level.SEVERE, 
                     "IOException thrown opening GcsInputChannel.", 
                     ex);
            } // END try/catch
            
         }
      } else {
         bytes=new byte[1];
         in=new ByteArrayInputStream(bytes);
      } // END if ((gcsObjectName == null) || (gcsObjectName.length() == 0))
      
      logger.exiting(_CLASS, "getInputStream()",in);
      return in;
   }
}