package org.xmldb.api.reference;

/*
 *  The XML:DB Initiative Software License, Version 1.0
 *
 *
 * Copyright (c) 2000-2003 The XML:DB Initiative.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        XML:DB Initiative (http://www.xmldb.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The name "XML:DB Initiative" must not be used to endorse or
 *    promote products derived from this software without prior written
 *    permission. For written permission, please contact info@xmldb.org.
 *
 * 5. Products derived from this software may not be called "XML:DB",
 *    nor may "XML:DB" appear in their name, without prior written
 *    permission of the XML:DB Initiative.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the XML:DB Initiative. For more information
 * on the XML:DB Initiative, please see <http://www.xmldb.org/>.
 */

import org.xmldb.api.base.*;
import org.xmldb.api.base.Collection;
import org.xmldb.api.modules.*;
import org.xmldb.api.sdk.*;
import org.xmldb.api.reference.modules.*;

import java.io.*;
import java.util.*;

/**
 * A <code>Collection</code> is implemented as a directory in the file system. 
 */
public class CollectionImpl extends SimpleCollection {
   protected File collection = null;
   
   public CollectionImpl(String path) throws FileNotFoundException, 
         XMLDBException {
      super();
      
      collection = new File(path);            
      if ( ! collection.isDirectory() ) {         
         throw new FileNotFoundException();
      }
      
      registerService(new XPathQueryServiceImpl());
      registerService(new CollectionManagementServiceImpl(path));
   }
   
   /**
    * Returns the name of the collection
    *
    * @return the name of the object.
    */
   public String getName() throws XMLDBException {
      return collection.getName();
   }
   
   /**
    * Returns the parent collection for this collection or null if no parent
    * collection exists.
    *
    * @return the parent <code>Collection</code> instance.    
    */
   public Collection getParentCollection() throws XMLDBException {      
      if ( collection.getParent() == null ) {
         return null;
      }
      
      try {
         return (Collection) new CollectionImpl(collection.getParent());
      }
      catch (FileNotFoundException e) {
         return null;
      }
   }

   /**
    * Returns the number of child collections under this 
    * <code>Collection</code>.
    *
    * @return the number of child collections.
    */
   public int getChildCollectionCount() throws XMLDBException {
      return collection.list(new DirectoryFilter()).length;      
   }

   /**
    * Returns a list of collection names naming all child collections
    * of the current collection. 
    *
    * @return an array containing collection names for all child
    *      collections.
    */
   public String[] listChildCollections() throws XMLDBException {
      return collection.list(new DirectoryFilter());
   }

   /**
    * Returns a <code>Collection</code> instance for the requested child collection 
    * if it exists.
    *
    * @param name the name of the child collection to retrieve.
    * @return the requested child collection or null if it couldn't be found.
    */
   public Collection getChildCollection(String name) throws XMLDBException {      
      try {
         return (Collection) new CollectionImpl(collection.getPath() +
            File.separator + name);
      }
      catch (FileNotFoundException e) {
         return null;
      }
   }
   
   /**
    * Returns the number of resources currently stored in this collection or 0
    * if the collection is empty.
    *
    * @return the number of resources in the collection.
    */
   public int getResourceCount() throws XMLDBException {      
      return collection.list(new FileFilter()).length;
   }

   /**
    * Returns a list of the ids for all resources stored in the collection.
    *
    * @return a string array containing the names for all 
    *  <code>Resource</code>s in the collection.
    */
   public String[] listResources() throws XMLDBException {
      return collection.list(new FileFilter());
   }

   /**
    * Creates a new empty <code>Resource</code> with the provided id.     
    *
    * @param id the unique id to associate with the created <code>Resource</code>.
    * @param type the <code>Resource</code> type to create.
    * @return an empty <code>Resource</code> instance.    
    */
   public Resource createResource(String id, String type) throws XMLDBException {
      if ( ( id == null ) || ( id.equals("") ) ) {
         id = createId();
      }
      
      if ( type.equals(XMLResource.RESOURCE_TYPE) ) {
         return new XMLResourceImpl(this, id, id);
      }      
      else if ( type.equals(BinaryResource.RESOURCE_TYPE) ) {
         return new BinaryResourceImpl(this, id);
      }
      else {
         throw new XMLDBException(ErrorCodes.UNKNOWN_RESOURCE_TYPE);
      }      
   }

   /**
    * Removes the <code>Resource</code> from the database.
    *
    * @param res the resource to remove.
    */
   public void removeResource(Resource res) throws XMLDBException {         
      if ( (res.getId() == null) || (res.getId().equals("")) ) {
         throw new XMLDBException(ErrorCodes.INVALID_RESOURCE);
      }
      
      File target = new File(collection, res.getId());
      if ( (target == null) || (target.isDirectory()) ) {
         throw new XMLDBException(ErrorCodes.NO_SUCH_RESOURCE);
      }
            
      target.delete();                                                               
   }

   /**
    * Stores the provided resource into the database. 
    *
    * @param res the resource to store in the database.
    */
   public void storeResource(Resource res) throws XMLDBException {
      try {
         validateResource(res);
         
         File target = new File(collection, res.getId());
                  
         FileOutputStream file = new FileOutputStream(target);
         if ( res.getResourceType().equals(XMLResource.RESOURCE_TYPE) ) {
            file.write(((String) res.getContent()).getBytes());
         }
         else if ( res.getResourceType().equals(BinaryResource.RESOURCE_TYPE) ) {
            file.write((byte[]) res.getContent());
         }
         else {
            throw new XMLDBException(ErrorCodes.INVALID_RESOURCE);
         }
         
         file.close();         
      }
      catch (IOException e) {         
         e.printStackTrace();         
      }
      
   }

   /**
    * Retrieves a <code>Resource</code> from the database.     
    *
    * @param id the unique id for the requested resource.
    * @return The retrieved <code>Resource</code> instance.
    */
   public Resource getResource(String id) throws XMLDBException {
      try {
         File target = new File(collection, id);
         
         if ( target.isFile() ) {
            FileInputStream file = new FileInputStream(target);
            byte[] contents = new byte[file.available()];
            file.read(contents);
            file.close();
            
            // If the file starts with < we'll assume XML. All test data has
            // the XML header. TODO: add a better test 
            if ( contents[0] == '<' ) {
               return new XMLResourceImpl(this, id, id, new String(contents));
            }
            else {
               return new BinaryResourceImpl(this, id, contents);
            }                                    
         }
      }
      catch (IOException e) {         
         e.printStackTrace();         
      }
      
      return null;
   }

   /**
    * Creates a new unique ID within the context of the <code>Collection</code>
    *
    * @return the created id as a string.
    */
   public String createId() throws XMLDBException {
      // This isn't robust but for this impl it is good enough
      return collection.getName() + System.currentTimeMillis() + 
         new Random().nextLong();
   }

   /**
    * Releases all resources consumed by the <code>Collection</code>.     
    */
   public void close() throws XMLDBException {
      // nothing to do here.
   }
   
   /**
    * Makes sure the resource is valid.
    */
   protected void validateResource(Resource res) throws XMLDBException {
      if ( (res.getId() == null) || (res.getId().equals("")) ||
           (res.getContent() == null) ) {
         throw new XMLDBException(ErrorCodes.INVALID_RESOURCE);
      }
   }
}

/**
 * Only accept directories
 */
class DirectoryFilter implements FilenameFilter {
   public boolean accept(File dir, String name) {
      if ( new File(dir, name).isDirectory() ) {
         return true;
      }
      
      return false;
   }
}

/**
 * Only accept files
 */
class FileFilter implements FilenameFilter {
   public boolean accept(File dir, String name) {
      if ( new File(dir, name).isFile() ) {
         return true;
      }
      
      return false;
   }
}
