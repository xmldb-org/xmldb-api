package org.xmldb.api.reference.modules;

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
import org.xmldb.api.reference.*;
import org.xmldb.api.sdk.modules.*;

import org.w3c.dom.*;
import org.apache.xpath.*;

/**
 * XPathQueryService is a <code>Service</code> that enables the execution of
 * XPath queries within the context of a <code>Collection</code>.
 */
public class XPathQueryServiceImpl extends SimpleXPathQueryService {
   Collection collection = null;
   
   public XPathQueryServiceImpl() {
      super();
   }
   
   /**
    * Returns the name associated with the Configurable object.
    *
    * @return the name of the object.
    */
   public String getName() throws XMLDBException {
      return "XPathQueryService";
   }
      
   /**
    * Gets the Version attribute of the Service object
    *
    * @return The Version value
    */
   public String getVersion() throws XMLDBException {
      return "1.0";
   }

   /**
    * Sets the Collection attribute of the Service object
    *
    * @param col The new Collection value
    */
   public void setCollection(Collection col) throws XMLDBException {
      this.collection = col;
   }

   /**
    * Run an XPath query againt the <code>Collection</code>. The result is a 
    * <code>ResourceIterator</code> containing the results of the query. 
    *
    * @param query The XPath query string to use.
    * @return A ResourceIterator containing the results of the query.
    */
   public ResourceSet query(String query) throws XMLDBException {
      // iterate over all files in the collection
      String[] resources = collection.listResources();
      
      ResourceSetImpl result = new ResourceSetImpl(collection);
      
      for ( int i = 0; i < resources.length; i++ ) {      
         // make sure the resource is XML
         Resource res = collection.getResource(resources[i]);
         if ( res.getResourceType().equals(XMLResource.RESOURCE_TYPE) ) {
            XMLResource doc = (XMLResource) res;
            Document content = (Document) doc.getContentAsDOM();
            
            try {  
               // Query each document and add the selected nodes to the result
               // set.
               NodeList nl = XPathAPI.selectNodeList(content, query);
               result.addResource(doc.getId(), nl);
            }
            catch (Exception e) {
               e.printStackTrace();
            }
         }
      }

      return result;
   }
}

