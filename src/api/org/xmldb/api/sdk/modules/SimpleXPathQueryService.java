package org.xmldb.api.sdk.modules;

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
import org.xmldb.api.modules.*;
import org.xmldb.api.sdk.*;

import java.util.Hashtable;

/**
 * XPathQueryService is a <code>Service</code> that enables the execution of
 * XPath queries within the context of a <code>Collection</code>.
 */
public abstract class SimpleXPathQueryService extends SimpleConfigurable 
      implements XPathQueryService {
   Collection collection = null;
   Hashtable namespaces = null;
   protected String version = "1.0";

   public SimpleXPathQueryService() {
      super();
      namespaces = new Hashtable();
   }
   
   /**
    * Returns the service name 
    */
   public String getName() throws XMLDBException {
      return "XPathQueryService";
   }
      
   /**
    * Returns the version of the Service    
    */
   public String getVersion() throws XMLDBException {
      return version;
   }

   /**
    * Sets the Collection attribute of the Service object
    *
    * @param col The new Collection value
    * @exception XMLDBException with expected error codes.
    *  <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
    *  specific errors that occur.
    */
   public void setCollection( Collection col ) throws XMLDBException {
      this.collection = col;
   }

   /**
    * Sets a namespace mapping in the internal namespace map used to evaluate
    * queries. 
    *
    * @param prefix The prefix to set in the map. If 
    *  <code>prefix</code> is empty or null the
    *  default namespace will be associated with the provided URI.
    * @param uri The URI for the namespace to be associated with prefix.    
    */
   public void setNamespace( String prefix, String uri ) throws XMLDBException {
      namespaces.put(prefix, uri);
   }

   /**   
    * Returns the URI string associated with <code>prefix</code>.
    *
    * @param prefix The prefix to retrieve from the namespace map. 
    * @return The URI associated with <code>prefix</code>
    */
   public String getNamespace( String prefix ) throws XMLDBException {
      return (String) namespaces.get(prefix);
   }
   
   /**   
    * Removes the namespace mapping associated with <code>prefix</code>.
    *
    * @param prefix The prefix to remove from the namespace map. If 
    *  <code>prefix</code> is null or empty the mapping for the default
    *  namespace will be removed.
    */
   public void removeNamespace( String prefix ) throws XMLDBException {
      namespaces.remove(prefix);
   }

   /**
    * Removes all namespace mappings stored in the internal namespace map.
    *
    * @exception XMLDBException with expected error codes.
    *  <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
    *  specific errors that occur.
    */
   public void clearNamespaces() throws XMLDBException {
      namespaces = new Hashtable();
   }

   /**
    * Run an XPath query againt the <code>Collection</code>. The result is a 
    * <code>ResourceIterator</code> containing the results of the query. 
    *
    * @param query The XPath query string to use.
    * @return A ResourceIterator containing the results of the query.
    */
   public ResourceSet query( String query ) throws XMLDBException {
      return null;
   }
   
   /**
    * Run an XPath query against an XML resource stored in the 
    * <code>Collection</code> associated with this service. The result is a 
    * <code>ResourceSet</code> containing the results of the query. 
    
    * @param query The XPath query string to use.
    * @param id The id of the document to run the query against.
    * @return A <code>ResourceSet</code> containing the results of the query.
    */   
   public ResourceSet queryResource( String id, String query ) 
         throws XMLDBException {
      return null;
   }
}

