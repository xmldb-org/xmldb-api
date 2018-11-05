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

import org.w3c.dom.*;
import org.xml.sax.*;

// These shouldn't be used because it ties us to Xerces
import org.apache.xerces.parsers.*;
import org.apache.xml.serialize.*;

import java.io.*;

/**
 * Provides access to XML resources stored in the database. An XMLResource can
 * be accessed either as text XML or via the DOM or SAX APIs.
 *
 * The default behavior for getContent and setContent is to work with XML data
 * as text so these methods work on <code>String</code> content.
 */
public abstract class SimpleXMLResource extends BaseResource 
      implements XMLResource {   
   protected String content = null;
   protected String documentID = null;
   protected XMLReader xmlReader;


   /**
    * Create a new XMLResource without any content.
    */
   public SimpleXMLResource (Collection parent, String id, String documentID) {
      this(parent, id, documentID, null);
   }

   /**
    * Create a fully initialized XMLResource
    */
   public SimpleXMLResource (Collection parent, String id, String documentID, 
         String content) {
      super(parent, id);
      this.content = content;
      this.documentID = documentID;
   }

   /** 
    * Returns the ID for the parent document of this resource.
    */
   public String getDocumentId() throws XMLDBException {
      return documentID;
   }
   
   /**
    * Returns the resource type for this Resource. 
    * 
    * @return the resource type for the Resource.     
    */
   public String getResourceType() throws XMLDBException {
      return RESOURCE_TYPE;
   } 
      
   /**
    * Retrieves the content from the resource. The type of the content varies
    * depending what type of resource is being used.
    *
    * @return the content of the resource.
    */
   public Object getContent() throws XMLDBException {
      return content;
   }

   /**
    * Sets the content for this resource. The type of content that can be set
    * depends on the type of resource being used.
    *
    * @param value the content value to set for the resource.
    */
   public void setContent(Object value) throws XMLDBException {
      if ( ! (value instanceof String) ) {
         throw new XMLDBException(ErrorCodes.WRONG_CONTENT_TYPE);
      }
      
      this.content = (String) value;
   }
   
   /**
    * Returns the content of the <code>Resource</code> as a DOM Node.
    *
    * @return The XML content as a DOM <code>Node</code>
    */
   public Node getContentAsDOM() throws XMLDBException {
      if (content == null) {
         throw new XMLDBException(ErrorCodes.INVALID_RESOURCE);
      }
      
      try {
         DOMParser parser = new DOMParser();
         parser.parse(new InputSource(new StringReader(content)));
         return (Node) parser.getDocument();
      }
      catch (Exception e) {
         //throw new XMLDBException(ErrorCodes.VENDOR_ERROR,
         e.printStackTrace();
      }
      
      return null;
   }

   /**
    * Sets the content of the <code>Resource</code> using a DOM Node as the
    * source.
    *
    * @param content The new content value
    */
   public void setContentAsDOM(Node content) throws XMLDBException {
      try {
         OutputFormat format = new OutputFormat();
         StringWriter result = new StringWriter();
         XMLSerializer serializer = new XMLSerializer(result, format);         
         switch (content.getNodeType()) {
            case Node.DOCUMENT_NODE:               
               serializer.serialize((Document) content);
               break;
            case Node.ELEMENT_NODE:
               serializer.serialize((Element) content);
               break;
            case Node.DOCUMENT_FRAGMENT_NODE:
               serializer.serialize((DocumentFragment) content);
               break;
         }
         
         this.content = result.toString();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Allows you to use a <code>ContentHandler</code> to parse the XML data from
    * the database for use in an application.
    *
    * @param handler the SAX <code>ContentHandler</code> to use to handle the
    *  <code>Resource</code> content.    
    */
   public void getContentAsSAX(ContentHandler handler) throws XMLDBException {
         // TODO: should probably use JAXP
      try {
         XMLReader xr = getSAXParser();
         xr.setContentHandler(handler);
         xr.setErrorHandler((ErrorHandler) handler);
         if (content != null) {
            xr.parse(new InputSource(new StringReader(content)));
         }
         //TODO not sure how reuse of XMLReader will affect things (e.g. using getSAXParser())
         //might have to set it to null here to ensure we get a new XMLReader
      }
      catch (Exception e) {
         throw new XMLDBException(ErrorCodes.UNKNOWN_ERROR, e);
      }
   }

    private XMLReader getSAXParser() {
        if (xmlReader == null) {
            xmlReader = new SAXParser();
        }
        return xmlReader;
    }

    /**
    * Sets the content of the <code>Resource</code> using a SAX 
    * <code>ContentHandler</code>.
    *
    * @return a SAX <code>ContentHandler</code> that can be used to add content
    *  into the <code>Resource</code>.
    */
   public ContentHandler setContentAsSAX() throws XMLDBException {
      return new SetContentHandler(this);
   }

    /**
     * Sets a SAX feature that will be used when this <code>XMLResource</code>
     * is used to produce SAX events (through the getContentAsSAX() method)
     *
     * @param feature Feature name. Standard SAX feature names are documented at
     * <a href="http://sax.sourceforge.net/">http://sax.sourceforge.net/</a>.
     * @param value Set or unset feature
     */
    public void setSAXFeature(String feature, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        XMLReader parser = getSAXParser();
        parser.setFeature(feature, value);
    }

    /**
     * Returns current setting of a SAX feature that will be used when this
     * <code>XMLResource</code> is used to produce SAX events (through the
     * getContentAsSAX() method)
     *
     * @param feature Feature name. Standard SAX feature names are documented at
     * <a href="http://sax.sourceforge.net/">http://sax.sourceforge.net/</a>.
     * @return whether the feature is set
     */
    public boolean getSAXFeature(String feature)
            throws SAXNotRecognizedException, SAXNotSupportedException {
       XMLReader parser = getSAXParser();
       return parser.getFeature(feature);
    }
}

