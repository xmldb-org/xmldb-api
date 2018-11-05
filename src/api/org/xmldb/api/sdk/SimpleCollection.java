package org.xmldb.api.sdk;

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

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * SimpleCollection provides an easy starting point for implementing the 
 * Collection interface. It provides a default Service management functionality
 * but all other functionality must be implemented by the extending class.   
 */
public abstract class SimpleCollection extends SimpleConfigurable 
      implements Collection {
   protected Hashtable services = null;
   protected boolean isOpen = true;
   
   public SimpleCollection() {
      services = new Hashtable();
   }
   
   /**
    * Returns the name associated with the Configurable object.
    *
    * @return the name of the object.
    */
   public String getName() throws XMLDBException {
      return null;
   }
   
   /**
    * Returns the list of Services supported by this Collection.
    *
    * @return A list of supported Services
    */
   public org.xmldb.api.base.Service[] getServices() throws XMLDBException {
      Enumeration e = services.elements();
      Service[] result = new Service[services.size()];

      int i = 0;
      while (e.hasMoreElements()) {
         result[i] = (Service) e.nextElement();
         i++;
      }

      return result;
   }

   /**
    * Get a Service instance based on the name and version.
    *
    * @param name The Service instance to retrieve
    * @param version The version of the service to retrieve.
    * @return The Service instance or null if no service was found.
    */
   public org.xmldb.api.base.Service getService(String name, String version)
          throws XMLDBException {
      Service result = (Service) services.get(name + version);

      return result;
   }

      /**
    * Registers a new Service with this Collection.
    *
    * @param service Description of Parameter
    * @exception XMLDBException
    */
   public void registerService(org.xmldb.api.base.Service service)
          throws XMLDBException {
      service.setCollection(this);
      services.put(service.getName() + service.getVersion(), service);
   }
   
/**
    * Default behaviour for a non-hierarchical implementation
    *
    * @return the parent <code>Collection</code> instance.    
    */
   public Collection getParentCollection() throws XMLDBException {      
      return null;
   }

   /**
    * Default behaviour for a non-hierarchical implementation
    *
    * @return the number of child collections.
    */
   public int getChildCollectionCount() throws XMLDBException {
      return 0;      
   }

   /**
    * Default behaviour for a non-hierarchical implementation 
    *
    * @return an array containing collection names for all child
    *      collections.
    */
   public String[] listChildCollections() throws XMLDBException {
      return new String[0];
   }

   /**
    * Default behaviour for a non-hierarchical implementation
    *
    * @param name the name of the child collection to retrieve.
    * @return the requested child collection or null if it couldn't be found.
    */
   public Collection getChildCollection(String name) throws XMLDBException {            
      return null;      
   }
   
   
   /**
    * Returns the number of resources currently stored in this collection or 0
    * if the collection is empty.
    *
    * @return the number of resource in the collection.
    * @exception XMLDBException with expected error codes.
    *  <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
    *  specific errors that occur.
    */
   public int getResourceCount() throws XMLDBException {
      return 0;
   }
   
   /**
    * Returns a list of the ids for all resources stored in the collection.
    *
    * @return a string array containing the names for all 
    *  <code>Resource</code>s in the collection.
    * @exception XMLDBException with expected error codes.
    *  <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
    *  specific errors that occur.
    */
   public String[] listResources() throws XMLDBException {
      return null;
   }

   /**
    * Creates a new empty <code>Resource</code> with the provided id. 
    * The type of <code>Resource</code>
    * returned is determined by the <code>type</code> parameter. The XML:DB API currently 
    * defines "XMLResource" and "BinaryResource" as valid resource types.
    * The <code>id</code> provided must be unique within the scope of the 
    * collection. If 
    * <code>id</code> is null or its value is empty then an id is generated by   
    * calling <code>createId()</code>. The
    * <code>Resource</code> created is not stored to the database until 
    * <code>storeResource()</code> is called.
    *
    * @param id the unique id to associate with the created <code>Resource</code>.
    * @param type the <code>Resource</code> type to create.
    * @return an empty <code>Resource</code> instance.    
    * @exception XMLDBException with expected error codes.
    *  <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
    *  specific errors that occur.
    *  <code>ErrorCodes.UNKNOWN_RESOURCE_TYPE</code> if the <code>type</code>
    *   parameter is not a known <code>Resource</code> type.
    */
   public Resource createResource(String id, String type) throws XMLDBException {
      return null;
   }

   /**
    * Removes the <code>Resource</code> from the database.
    *
    * @param res the resource to remove.
    * @exception XMLDBException with expected error codes.
    *  <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
    *  specific errors that occur.
    *  <code>ErrorCodes.INVALID_RESOURCE</code> if the <code>Resource</code> is
    *   not valid.
    *  <code>ErrorCodes.NO_SUCH_RESOURCE</code> if the <code>Resource</code> is
    *   not known to this <code>Collection</code>.
    */
   public void removeResource(Resource res) throws XMLDBException {
   }

   /**
    * Stores the provided resource into the database. If the resource does not
    * already exist it will be created. If it does already exist it will be
    * updated.
    *
    * @param res the resource to store in the database.
    * @exception XMLDBException with expected error codes.
    *  <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
    *  specific errors that occur.
    *  <code>ErrorCodes.INVALID_RESOURCE</code> if the <code>Resource</code> is
    *   not valid.
    */
   public void storeResource(Resource res) throws XMLDBException {
   }

   /**
    * Retrieves a <code>Resource</code> from the database. If the 
    * <code>Resource</code> could not be
    * located a null value will be returned.
    *
    * @param id the unique id for the requested resource.
    * @return The retrieved <code>Resource</code> instance.
    * @exception XMLDBException with expected error codes.
    *  <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
    *  specific errors that occur.
    */
   public Resource getResource(String id) throws XMLDBException {
      return null;
   }

   /**
    * Creates a new unique ID within the context of the <code>Collection</code>
    *
    * @return the created id as a string.
    * @exception XMLDBException with expected error codes.
    *  <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
    *  specific errors that occur.
    */
   public String createId() throws XMLDBException {
      return null;
   }

   public boolean isOpen() throws XMLDBException {
      return isOpen;
   }
   
   /**
    * Releases all resources consumed by the <code>Collection</code>. 
    * The <code>close</code> method must
    * always be called when use of a <code>Collection</code> is complete.
    *
    * @exception XMLDBException with expected error codes.
    *  <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
    *  specific errors that occur.
    */
   public void close() throws XMLDBException {
      isOpen = false;
   }
   
   /**
    * Throws an exception if the collection is not open.
    */
   protected void checkOpen() throws XMLDBException {
      if ( isOpen == false ) {
         throw new XMLDBException(ErrorCodes.COLLECTION_CLOSED);
      }
   }
}

