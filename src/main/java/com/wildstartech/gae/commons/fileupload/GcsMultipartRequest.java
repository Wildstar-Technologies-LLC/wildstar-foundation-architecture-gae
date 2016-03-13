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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class GcsMultipartRequest extends HttpServletRequestWrapper {
   private static final String _CLASS=GcsMultipartRequest.class.getName();
   private static final Logger logger=Logger.getLogger(_CLASS);
   
   // A collection of lists of parameter values returned from the client.
   private Map<String,List<String>> formParameters=null;
   // A coolection of lists of FileItem objects returned from the client.
   private Map<String,List<FileItem>> files=null;
   /* An unmodifiable list of parameter values used for HttpServletRequesWrapper
    * compatibility. */
   private Map<String,String[]> parameterMap=null;
   
   /**
    * Constructor taking a simple {@code HttpServletRequset} as a parameter.
    * @param request
    */
   public GcsMultipartRequest(HttpServletRequest request) {
      super(request);
      logger.entering(_CLASS, "GcsMultipartRequest(HttpServletRequest)",
            request);
      init();     
      logger.exiting(_CLASS, "GcsMultipartRequest(HttpServletRequest)");
   }
   
   /**
    * 
    * @param request
    * @param upload
    */
   public GcsMultipartRequest(
         HttpServletRequest request, 
         ServletFileUpload upload) throws IOException {
      super(request);
      logger.entering(_CLASS, 
            "GcsMultipartRequest(HttpServletRequest,ServletFileUpload)", 
            new Object[] {request, upload});
      init();
      parseRequest(request,upload);
      logger.exiting(_CLASS, 
            "GcsMultipartRequest(HttpServletRequest,ServletFileUpload)");
   }
   
   //********** Utility Methods (Private)
   /**
    * Adds the {@code FileItem} read from the client as a file.
    * 
    * @param item The {@code FileItem} representation of a file uploaded by the 
    * client.
    */
   private void addFile(FileItem item) {
      logger.entering(_CLASS,"addFile(FileItem)",item);
      List<FileItem> files=null;
      String fieldName=null;
      
      if (item != null) {
         // The item is NOT null, so let's add it to our list.
         fieldName=item.getFieldName();
         if (this.files.containsKey(fieldName)) {
            // The files Map already has a list for the specified key name
            files=this.files.get(fieldName);
            // So let's add this one to the end.
            files.add(item);
         } else {
            // The Map DOES NOT have a list for the specified field name
            files=new ArrayList<FileItem>();
            // A new list is created and the item is added to it.
            files.add(item);
            // The new list is stored in the Map.
            this.files.put(fieldName, files);
         } // END if (this.files.containsKey(fieldName))
      } else {
         logger.warning("The FileItem parameter was null.");
      } // END if (item != null) 
      

      logger.exiting(_CLASS,"addFile(FileItem)");
   }
   
   /**
    * Adds the {@code FileItem} read from the client to as an element of a form.
    * @param item The {@code FileItem} to be added to the list of field values.
    */
   private void addFormElement(FileItem item) {
      logger.entering(_CLASS, "addFormElement(FileItem)",item);
      List<String> elements=null;
      String parameterValue=null;
      String fieldName=null;
      
      if (item != null) {
         // The item is NOT null, so let's add it to our list.
         fieldName=item.getFieldName();
         // Get the value of the field as a String 
         parameterValue=getFileItemAsString(item);
         if (this.formParameters.containsKey(fieldName)) {
            // A list of values for the field name IS in the Map
            elements=this.formParameters.get(fieldName);
            // Add the current value to the existing list.
            elements.add(parameterValue);
         } else {
            // No list of values for the field name is in the Map
            elements=new ArrayList<String>();
            // The parameter value is added to a new List
            elements.add(parameterValue);
            // The new list is put into the parameter Map
            this.formParameters.put(fieldName, elements);
         } // END if (this.parameters.containsKey(fieldName))
      } else {
         throw new NullPointerException(
               "The FileItem parameter was not specified.");
      } // END  if (item != null)
      logger.exiting(_CLASS, "addFormElement(FileItem)");
   }
   
   /**
    * Returns the contents of the {@code FileItem} as a {@code String}.
    * 
    * <p>The method will attempt to obtain the encoding from the 
    * {@code ServletRequest} and use that to return the contents of the 
    * {@code FileItem} as a {@code String}.  If the {@code ServletRequest}
    * reutrns a {@code null} value or the specified encoding is unsupported, 
    * then the default character encoding will be used.</p>
    * 
    * @param item The {@code FileItem} from which the {@code String} value 
    * should be read.
    * @return A {@code String} representation of the parameter value.
    */
   private String getFileItemAsString(FileItem item) {
      logger.entering(_CLASS, "getFileItemAsString(FileItem)");
      ServletRequest request=null;
      String str=null;
      String characterEncoding=null;
      
      if (item != null) {
         request=getRequest();
         characterEncoding=request.getCharacterEncoding();
         if (characterEncoding == null) {
            str=item.getString();
         } else {
            // Try the custom character encoding.
            try {
               str=item.getString(characterEncoding);
            } catch (UnsupportedEncodingException ex) {
               logger.log(
                     Level.WARNING,
                     "Exception thrown encoding parameter value as a String.",
                     ex);
               // Resort to the default encoding.
               str=item.getString();
            } // END try/catch
         } // if (characterEncoding == null)
      } else {
         logger.warning("The FileItem parameter was null.");
      } // END if (item != null)
      
      logger.exiting(_CLASS, "getFileItemAsString(FileItem)", str);
      return str;
   }
   
   /**
    * Initialization method responsible for initializing the class.
    */
   private void init() {
      logger.entering(_CLASS,"init()");
      // Create the Map that is going to store "normal" form values.
      this.formParameters=new LinkedHashMap<String, List<String>>();
      // Create the Map that is going to store files.
      this.files=new LinkedHashMap<String, List<FileItem>>();
      logger.exiting(_CLASS, "init()");
   }
   
   /**
    * 
    * @param request
    * @param upload
    * @throws IOException
    */
   private void parseRequest(
         HttpServletRequest request, 
         ServletFileUpload upload) 
         throws IOException {
      logger.entering(_CLASS, 
            "parseRequest(HttpServletRequest,ServletFileUpload)", 
            new Object[] { request, upload});
      List<FileItem> fileItems=null;
      
      try {
         fileItems=upload.parseRequest(request);
         for (FileItem item: fileItems) {
            if (item.isFormField()) {
               addFormElement(item);
            } else {
               addFile(item);
            }
         }
      } catch (FileUploadException ex) {
         logger.severe("Error processing the file upload request.");
      }
      logger.exiting(_CLASS, 
            "parseRequest(HttpServletRequest,ServletFileUpload)");
   }
   
   //********** Utility Methods (Public)
   /**
    * Returns the first value associated with the specified parameter.
    * <p>If the specified parameter does not identify a value that is found in 
    * the local {@code formParameters} {@code Map}, then information will be 
    * returned from the {@code HttpServletRequestWrappper} superclass.  If
    * the local {@code Map} contains a key by the specified 
    * {@code parameterName}, but no actual {@code String} value, then an empty
    * {@code String} will be returned.
    * @param The name of the form parameter whose value should be returned.
    * @return An empty {@code String} if no value for the specified parameter
    * could be found.  If there specified form parameter has multiple values, 
    * then the first value will be returned.
    */
   @Override
   public String getParameter(String parameterName) {
      logger.entering(_CLASS, "getParameter(String)",parameterName);
      List<String> values=null;
      String parameterValue="";
      
      if (parameterName != null) {
         if (this.formParameters.containsKey(parameterName)) {
            values=this.formParameters.get(parameterName);
            if ((values == null) || (values.isEmpty())) {
               parameterValue="";
            } else {
               parameterValue=values.get(0);
            } // END if ((values == null) || (values.isEmpty()))
         } else {
            parameterValue=super.getParameter(parameterName);
         } // if (this.formParameters.containsKey(parameterName))
      } else {
         logger.warning("No parameterName value is present.");
      } // END if (parameterName != null)
      
      logger.exiting(_CLASS, "getParameter(String)",parameterValue);
      return parameterValue;
   }
   
   /**
    * @return The {@code Map} of parameter values.
    */
   @Override
   @SuppressWarnings({"rawtypes","unchecked"})
   public Map getParameterMap() {
      logger.entering(_CLASS, "getParameterMap()");
      List<String> parameterValues=null;
      Map<String,String[]> paramMap=null;
      Set<String> keys=null;
      String[] parameterValueArray=null;
      
      if (this.parameterMap == null) {
          paramMap=new LinkedHashMap<String,String[]>();
          keys=this.formParameters.keySet();
          
          for (String parameterName: keys) {
             parameterValues=this.formParameters.get(parameterName);
             parameterValueArray=parameterValues.toArray(new String[0]);
             paramMap.put(parameterName, parameterValueArray);
          } // END for (String parameterName: this.formParameters.keySet())
          
          // Add the list of parameters found in the HttpServletRequestWrapper
          paramMap.putAll(super.getParameterMap());
          this.parameterMap=Collections.unmodifiableMap(paramMap);
      } // END if (this.formParameters == null)
      
      logger.exiting(_CLASS, "getParameterMap()",this.parameterMap); 
      return this.parameterMap;
   }
   
   /**
    * Returns a list of parameter names.
    */
   @Override
   @SuppressWarnings({"rawtypes","unchecked"})
   public Enumeration getParameterNames() {
      logger.entering(_CLASS, "getParameterNames()");
      Enumeration<String> originalNames=null;
      Enumeration<String> namesToReturn=null;
      Set<String> parameterNames=null;
      
      parameterNames=new LinkedHashSet<String>();
      parameterNames.addAll(this.formParameters.keySet());
      originalNames=super.getParameterNames();
      while (originalNames.hasMoreElements()) {
         parameterNames.add(originalNames.nextElement());
      } // END while (originalNames.hasMoreElements())
      namesToReturn=Collections.enumeration(parameterNames);
      
      logger.entering(_CLASS, "getParameterNames()",namesToReturn);
      return namesToReturn;
   }
   
   @Override
   public String[] getParameterValues(String fieldName) {
      logger.entering(_CLASS, "getParameterValues(String)",fieldName);
      List<String> parameterValues=null;
      String[] values=null;
      
      if (fieldName != null) {
         if (this.formParameters.containsKey(fieldName)) {
            parameterValues=this.formParameters.get(fieldName);
            if (parameterValues.isEmpty()) {
               values=new String[0];               
            } else {
               values=parameterValues.toArray(
                     new String[parameterValues.size()]);
            } // END if (parameterValues.isEmpty())
         } else {
            // The local formParameters Map does not have any data, so get it
            // from the superclass.
            values=super.getParameterValues(fieldName);
            if (values == null) {
               values=new String[0];
            } // END if (values == null)
         } // END if (this.formParameters.containsKey(parameterName))
      } else {
         logger.warning("The parameterName is null.");
         values=new String[0];
      } // END if (parameterName != null)
      
      logger.exiting(_CLASS, "getParameterValues(String)",values);
      return values;
   }
   
   /**
    * Returns a {@code FileItem} belonging to the specified fieldName. 
    * @param fieldName
    * @return
    */
   public FileItem getFileItem(String fieldName) {
      logger.entering(_CLASS, "getFileItem(String)",fieldName);
      FileItem fileItem=null;
      List<FileItem> items=null;
      
      if (fieldName != null) {
         if (this.files.containsKey(fieldName)) {
            items=this.files.get(fieldName);
            fileItem=items.get(0);
         } // END if (this.files.containsKey(fileName))
      } else {
         logger.warning("The fileName parameter was null.");
      } // END if (fileName != null)
      logger.exiting(_CLASS, "getFileItem(String)",fileItem);
      return fileItem;
   }
}