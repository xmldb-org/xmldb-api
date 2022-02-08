/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 * Copyright (c) 2000-2022 The XML:DB Initiative. All rights reserved.
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
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * XML:DB Initiative. For more information on the XML:DB Initiative, please see
 * <https://github.com/xmldb-org/>.
 */
package org.xmldb.api;

import static org.xmldb.api.base.ErrorCodes.NOT_IMPLEMENTED;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

public class TestXMLResource extends TestBaseResource implements XMLResource {
  public TestXMLResource() {
    this(null);
  }

  public TestXMLResource(Collection parentCollection) {
    super(parentCollection);
  }

  @Override
  public String getDocumentId() throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public Node getContentAsDOM() throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public void setContentAsDOM(Node content) throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public void getContentAsSAX(ContentHandler handler) throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public ContentHandler setContentAsSAX() throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public void setSAXFeature(String feature, boolean value)
      throws SAXNotRecognizedException, SAXNotSupportedException {
    throw new SAXNotSupportedException();
  }

  @Override
  public boolean getSAXFeature(String feature)
      throws SAXNotRecognizedException, SAXNotSupportedException {
    return false;
  }

  @Override
  public void setXMLReader(XMLReader xmlReader) {
    // no action
  }
}
