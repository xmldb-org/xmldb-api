/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 *
 * Copyright (c) 2000, 2019 The XML:DB Initiative. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must include the
 * following acknowledgment: "This product includes software developed by the XML:DB Initiative
 * (http://www.xmldb.org/)." Alternately, this acknowledgment may appear in the software itself, if
 * and wherever such third-party acknowledgments normally appear.
 *
 * 4. The name "XML:DB Initiative" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please contact info@xmldb.org.
 *
 * 5. Products derived from this software may not be called "XML:DB", nor may "XML:DB" appear in
 * their name, without prior written permission of the XML:DB Initiative.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * XML:DB Initiative. For more information on the XML:DB Initiative, please see
 * <http://www.xmldb.org/>.
 */

package org.xmldb.api.modules;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;

/**
 * Provides access to XML resources stored in the database. An XMLResource can be accessed either as
 * text XML or via the DOM or SAX APIs.
 *
 * The default behavior for getContent and setContent is to work with XML data as text so these
 * methods work on {@code String} content.
 */
public interface XMLResource extends Resource {

  public static final String RESOURCE_TYPE = "XMLResource";

  /**
   * Returns the unique id for the parent document to this {@code Resource} or null if the
   * {@code Resource} does not have a parent document. {@code getDocumentId()} is typically used
   * with {@code Resource} instances retrieved using a query. It enables accessing the parent
   * document of the {@code Resource} even if the {@code Resource} is a child node of the document.
   * If the {@code Resource} was not obtained through a query then {@code getId()} and
   * {@code getDocumentId()} will return the same id.
   *
   * @return the id for the parent document of this {@code Resource} or null if there is no parent
   *         document for this {@code Resource}.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  String getDocumentId() throws XMLDBException;

  /**
   * Returns the content of the {@code Resource} as a DOM Node.
   *
   * @return The XML content as a DOM {@code Node}
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  Node getContentAsDOM() throws XMLDBException;

  /**
   * Sets the content of the {@code Resource} using a DOM Node as the source.
   *
   * @param content The new content value
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.INVALID_RESOURCE} if the content
   *         value provided is null. {@code ErrorCodes.WRONG_CONTENT_TYPE} if the content provided
   *         in not a valid DOM {@code Node}.
   */
  void setContentAsDOM(Node content) throws XMLDBException;

  /**
   * Allows you to use a {@code ContentHandler} to parse the XML data from the database for use in
   * an application.
   *
   * @param handler the SAX {@code ContentHandler} to use to handle the {@code Resource} content.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.INVALID_RESOURCE} if the
   *         {@code ContentHandler} provided is null.
   */
  void getContentAsSAX(ContentHandler handler) throws XMLDBException;

  /**
   * Sets the content of the {@code Resource} using a SAX {@code ContentHandler}.
   *
   * @return a SAX {@code ContentHandler} that can be used to add content into the {@code Resource}.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  ContentHandler setContentAsSAX() throws XMLDBException;

  /**
   * Sets a SAX feature that will be used when this {@code XMLResource} is used to produce SAX
   * events (through the getContentAsSAX() method)
   *
   * @param feature Feature name. Standard SAX feature names are documented at
   *        <a href="http://sax.sourceforge.net/">http://sax.sourceforge.net/</a>.
   * @param value Set or unset feature
   * @throws SAXNotRecognizedException if the feature is not recognized.
   * @throws SAXNotSupportedException if the feature is not supported.
   */
  void setSAXFeature(String feature, boolean value)
      throws SAXNotRecognizedException, SAXNotSupportedException;

  /**
   * Returns current setting of a SAX feature that will be used when this {@code XMLResource} is
   * used to produce SAX events (through the getContentAsSAX() method)
   *
   * @param feature Feature name. Standard SAX feature names are documented at
   *        <a href="http://sax.sourceforge.net/">http://sax.sourceforge.net/</a>.
   * @return whether the feature is set
   * @throws SAXNotRecognizedException if the feature is not recognized.
   * @throws SAXNotSupportedException if the feature is not supported.
   */
  boolean getSAXFeature(String feature) throws SAXNotRecognizedException, SAXNotSupportedException;

  /**
   * Sets the external XMLReader to use when doing content handler operations.
   *
   * @param xmlReader the XMLReader
   */
  void setXMLReader(XMLReader xmlReader);
}

