package org.xmldb.api.reference;

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
import org.xmldb.api.sdk.*;
import org.xmldb.api.reference.modules.*;

import java.io.*;

import org.w3c.dom.*;

import org.apache.xml.serialize.*;

/**
 * <code>ResourceSet</code> contains a set of resources as returned from a query
 * or other operation.
 */
public class ResourceSetImpl extends SimpleResourceSet {
   
   public ResourceSetImpl(org.xmldb.api.base.Collection collection) 
         throws XMLDBException {
      super();
      this.collection = collection;            
   }
   
   public void addResource(String documentId, NodeList nodes) 
         throws XMLDBException {      
      for ( int i = 0; i < nodes.getLength(); i++ ) {
         try {
            Node n = nodes.item(i);
            XMLResource resource = new XMLResourceImpl(collection, null, 
               documentId, serialize(n));

            resources.add(resource);
         }
         catch (Exception e) {
            throw new XMLDBException(ErrorCodes.UNKNOWN_ERROR, e);
         }
      }
   }

   /**
    * Returns a <code>Resource</code> containing an XML representation of all
    * resources stored in the set.        
    *
    * @return A <code>Resource</code> instance containing an XML representation
    *  of all set members.
    * @exception XMLDBException    
    */
   public Resource getMembersAsResource() throws XMLDBException {
      
      Document doc = buildMembersAsResourceDocument();
      XMLResource result = new XMLResourceImpl(collection, null, null, 
         serialize(doc));
      
      return result;
   }
   
   public String serialize(Node node) {
      
      try {
         OutputFormat format = new OutputFormat();
         StringWriter result = new StringWriter();   
         XMLSerializer serializer = new XMLSerializer(result, format);         
         switch (node.getNodeType()) {
            case Node.DOCUMENT_NODE:               
               serializer.serialize((Document) node);
               break;
            case Node.ELEMENT_NODE:
               serializer.serialize((Element) node);
               break;
            case Node.DOCUMENT_FRAGMENT_NODE:
               serializer.serialize((DocumentFragment) node);
               break;
         }
         
         return result.toString();
      }
      catch (IOException e) {
         return null;
      }      
   }   
}

