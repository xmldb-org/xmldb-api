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

import org.xmldb.api.modules.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.*;

/**
 * Simple ContentHandler that just converts the SAX event stream into a text
 * representation of the document and stores it in the associated resource.
 */
public class SetContentHandler extends DefaultHandler {
   protected XMLResource resource = null;
   protected StringBuffer newContent = null;
   protected Hashtable namespaces = null;

   public SetContentHandler(XMLResource resource) {
      this.resource = resource;
      namespaces = new Hashtable();
   }

   /**
    * Receive notification of the beginning of the document.
    *
    * @exception SAXException Description of Exception
    * @see org.xml.sax.ContentHandler#startDocument
    */
   public void startDocument()
          throws SAXException {
      newContent = new StringBuffer();
      // TODO: what is the proper way to set this?
      newContent.append("<?xml version=\"1.0\"?>");
   }


   /**
    * Receive notification of the end of the document.
    *
    * @exception SAXException Description of Exception
    * @see org.xml.sax.ContentHandler#endDocument
    */
   public void endDocument()
          throws SAXException {
      try {       
         resource.setContent(newContent.toString());
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   /**
    * Receive notification of the start of a Namespace mapping.
    *
    * @param prefix The Namespace prefix being declared.
    * @param uri The Namespace URI mapped to the prefix.
    * @exception SAXException Description of Exception
    * @see org.xml.sax.ContentHandler#startPrefixMapping
    */
   public void startPrefixMapping(String prefix, String uri)
          throws SAXException {
      namespaces.put(prefix, uri);
   }


   /**
    * Receive notification of the end of a Namespace mapping.
    *
    * @param prefix The Namespace prefix being declared.
    * @exception SAXException Description of Exception
    * @see org.xml.sax.ContentHandler#endPrefixMapping
    */
   public void endPrefixMapping(String prefix)
          throws SAXException {
      namespaces.remove(prefix);
   }


   /**
    * Receive notification of the start of an element.
    *
    * @param attributes The specified or defaulted attributes.
    * @param uri Description of Parameter
    * @param localName Description of Parameter
    * @param qName Description of Parameter
    * @exception SAXException Description of Exception
    * @see org.xml.sax.ContentHandler#startElement
    */
   public void startElement(String uri, String localName,
         String qName, Attributes attributes)
          throws SAXException {
      newContent.append("<");
      newContent.append(qName);

      for (int i = 0; i < attributes.getLength(); i++) {
         String qn = attributes.getQName(i);
         newContent.append(" ");
         newContent.append(qn);
         newContent.append("=");
         newContent.append("\"");
         newContent.append(attributes.getValue(i));
         newContent.append("\"");

         //Avoid duplicate namespace declarations
         if (qn.startsWith("xmlns")) {
            String ln = attributes.getLocalName(i);
            if (ln.equals("xmlns")) {
               namespaces.remove("");
            } else {
               namespaces.remove(ln);
            }
         }
      }

      Enumeration enum = namespaces.keys();
      while ( enum.hasMoreElements() ) {
         String key = (String) enum.nextElement();
         newContent.append(" xmlns");
         if (key.length() > 0) {
            newContent.append(":");
            newContent.append(key);
         }
         newContent.append("=");
         newContent.append("\"");
         newContent.append(namespaces.get(key));
         newContent.append("\"");
         namespaces.remove(key);
      }

      newContent.append(">");
   }


   /**
    * Receive notification of the end of an element.
    *
    * @param uri Description of Parameter
    * @param localName Description of Parameter
    * @param qName Description of Parameter
    * @exception SAXException Description of Exception
    * @see org.xml.sax.ContentHandler#endElement
    */
   public void endElement(String uri, String localName, String qName)
          throws SAXException {
      newContent.append("</");
      newContent.append(qName);
      newContent.append(">");
   }


   /**
    * Receive notification of character data inside an element.
    *
    * @param ch The characters.
    * @param start The start position in the character array.
    * @param length The number of characters to use from the
    * character array.
    * @exception SAXException Description of Exception
    * @see org.xml.sax.ContentHandler#characters
    */
   public void characters(char ch[], int start, int length)
          throws SAXException {
      int i = 0;
      while ( i < length ) {
         char c = ch[start + i];
         switch (c) {
            case '&':
               newContent.append("&amp;");
               break;
            case '<':
               newContent.append("&lt;");
               break;
            case '>':
               newContent.append("&gt;");
               break;
            case '"':
               newContent.append("&quot;");
               break;
            case '\'':
               newContent.append("&apos;");
               break;
            default:
               // If we're outside 7 bit ascii encode as a character ref.
               // Not sure what the proper behavior here should be.
               if ((int) c > 127) {
                  newContent.append("&#" + (int) c + ";");
               }
               else {
                  newContent.append(c);
               }
         }

         i++;
      }
   }


   /**
    * Receive notification of ignorable whitespace in element content.
    *
    * @param ch The whitespace characters.
    * @param start The start position in the character array.
    * @param length The number of characters to use from the
    * character array.
    * @exception SAXException Description of Exception
    * @see org.xml.sax.ContentHandler#ignorableWhitespace
    */
   public void ignorableWhitespace(char ch[], int start, int length)
          throws SAXException {
      int i = 0;
      while ( i < length ) {
         newContent.append(ch[start + i]);
         i++;
      }
   }


   /**
    * Receive notification of a processing instruction.
    *
    * @param target The processing instruction target.
    * @param data The processing instruction data, or null if
    * none is supplied.
    * @exception SAXException Description of Exception
    * @see org.xml.sax.ContentHandler#processingInstruction
    */
   public void processingInstruction(String target, String data)
          throws SAXException {
      newContent.append("<?");
      newContent.append(target);
      newContent.append(" ");

      if (data != null) {
         newContent.append(data);
      }

      newContent.append("?>");
   }


   /**
    * Receive notification of a skipped entity.
    *
    * @param name The name of the skipped entity.
    * @exception SAXException Description of Exception
    * @see org.xml.sax.ContentHandler#processingInstruction
    */
   public void skippedEntity(String name)
          throws SAXException {
      // no op
   }

}

