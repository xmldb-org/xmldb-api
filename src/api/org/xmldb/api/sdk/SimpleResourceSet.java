package org.xmldb.api.sdk;

/*
 * The XML:DB Initiative Software License, Version 1.0
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
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 * if any, must include the following acknowledgment:
 * "This product includes software developed by the
 * XML:DB Initiative (http://www.xmldb.org/)."
 * Alternately, this acknowledgment may appear in the software itself,
 * if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The name "XML:DB Initiative" must not be used to endorse or
 * promote products derived from this software without prior written
 * permission. For written permission, please contact info@xmldb.org.
 *
 * 5. Products derived from this software may not be called "XML:DB",
 * nor may "XML:DB" appear in their name, without prior written
 * permission of the XML:DB Initiative.
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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.w3c.dom.*;

import org.apache.xerces.dom.DocumentImpl;

/**
 * Provides a base ResourceSet implementation that utilizes a synchronized list
 * to contain the set of resources.
 */
public abstract class SimpleResourceSet implements ResourceSet {
   public static final String RESOURCE_SET_NS = 
      "http://www.xmldb.org/xapi/ResourceSet";
   protected List resources = null;
   protected org.xmldb.api.base.Collection collection = null;

   public SimpleResourceSet() {
      resources = Collections.synchronizedList(new ArrayList());
   }

   /**
    * Returns an iterator over all <code>Resource</code> instances stored in 
    * the set.
    *
    * @return a ResourceIterator over all <code>Resource</code> instances in the
    *  set.
    * @exception XMLDBException 
    */
   public ResourceIterator getIterator() throws XMLDBException {
      return new SimpleResourceIterator(resources);
   }
   
   /**
    * Returns the <code>Resource</code> instance stored at the index specified
    * by <code>index</code>.<p />
    *
    * @param index the index of the resource to retrieve.
    * @return the <code>Resource</code> instance.
    * @exception XMLDBException 
    */
   public Resource getResource(int index) throws XMLDBException {
      return (XMLResource) resources.get(index);
   }

   /**
    * Returns the number of resources contained in the set.<p /> 
    *
    * @return the number of <code>Resource</code> instances in the set.
    * @exception XMLDBException     
    */
   public int getSize() throws XMLDBException {
      return resources.size();
   }

   /**
    * Adds a <code>Resource</code> instance to the set.
    *
    * @exception XMLDBException     
    */
   public void addResource(Resource res) throws XMLDBException {
      resources.add(res);
   }

    /**
     * Adds all <code>Resource</code> instances in the resourceSet
     * to this set.
     *
     * @param rSet The <code>ResourceSet</code> containing all the <code>Resource</code>'s
     *             to add to the set.
     * @throws org.xmldb.api.base.XMLDBException
     *          with expected error codes.<br />
     *          <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
     *          specific errors that occur.<br />
     */
    public void addAll(ResourceSet rSet) throws XMLDBException {
        ResourceIterator it = rSet.getIterator();
        while (it.hasMoreResources()) {
            addResource(it.nextResource());
        }
    }

   /**
    * Removes all <code>Resource</code> instances from the set.
    *
    * @exception XMLDBException     
    */
   public void clear() throws XMLDBException {
      resources.clear();
   }
   
   /**
    * Removes the <code>Resource</code> located at <code>index</code> from the
    * set.
    *
    * @param index The index of the <code>Resource</code> instance to remove.
    * @exception XMLDBException     
    */
   public void removeResource(int index) throws XMLDBException {
      resources.remove(index);
   }      
   
   /**
    * Returns a Resource containing an XML representation of all resources 
    * stored in the set. <p />
    * TODO: Specify the schema used for this
    *
    * @return A <code>Resource</code> instance containing an XML representation 
    *   of all set members.
    * @exception XMLDBException with expected error codes.<br />
    * <code>ErrorCodes.VENDOR_ERROR</code> for any vendor
    * specific errors that occur.<br />
    */
   public Resource getMembersAsResource() throws XMLDBException {
      return null;
   }
   
   /**
    * Turns the List into the proper XML format to implement
    * getMembersAsResource. This is a helper method to make implementing
    * getMembersAsResource easier. The result is a DOM document that should be
    * converted into a proper XMLResource implementation by the implementing
    * driver.
    */
   public Document buildMembersAsResourceDocument() throws XMLDBException {      
// TODO: use JAXP
      Document doc = new DocumentImpl();
      
      Element set = doc.createElementNS(RESOURCE_SET_NS, "xapi:resourceSet");
// TODO: this probably returns the wrong collection name
// TODO: hardcoded URI prefix is bad.      
      set.setAttributeNS(RESOURCE_SET_NS, "xapi:collectionURI", 
         "xmldb:ref://" + collection.getName());
      set.setAttribute("xmlns:xapi", RESOURCE_SET_NS);
      doc.appendChild(set);
      
      int i = 0;
      while ( i < resources.size() ) {
         XMLResource res = (XMLResource) resources.get(i);
         Element resource = doc.createElementNS(RESOURCE_SET_NS, 
            "xapi:resource");
         resource.setAttributeNS(RESOURCE_SET_NS, "xapi:documentID", 
            res.getDocumentId());
                  
         resource.appendChild(doc.importNode(
            ((Document) res.getContentAsDOM()).getDocumentElement(), true));
         
         set.appendChild(resource);
         
         i++;
      }

      return doc;
   }
}

