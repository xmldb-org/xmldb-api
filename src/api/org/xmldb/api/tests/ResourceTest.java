package org.xmldb.api.tests;

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

import junit.framework.*;
import org.apache.xerces.parsers.SAXParser;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.BinaryResource;
import org.xmldb.api.modules.XMLResource;

import java.io.StringReader;

public class ResourceTest extends XMLDBTestCase {

    private static final String resId = "test";

    public ResourceTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(ResourceTest.class);
    }

    private void cleanupExistingResource() throws XMLDBException {
        // cleanup from lefovers due to previous failures etc.
        Resource temp = col.getResource(resId);
        if (temp != null) {
            col.removeResource(temp);
        }
    }

    public void testBinaryResource() {
        try {
            if (supportsBinary) {
                byte[] content = new byte[3];
                content[0] = 0x1;
                content[1] = 0x2;
                content[2] = 0x3;

                cleanupExistingResource();

                BinaryResource res =
                        (BinaryResource) col.createResource(resId, BinaryResource.RESOURCE_TYPE);
                assertTrue(res.getId().equals(resId));
                assertTrue(res.getParentCollection() == col);

                res.setContent(content);
                byte[] result = (byte[]) res.getContent();

                assertTrue(result != null);
                assertTrue(result[0] == 0x1);
                assertTrue(result[1] == 0x2);
                assertTrue(result[2] == 0x3);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testXMLResource() {
        try {
            String content = "<?xml version=\"1.0\"?><tag1><tag2>value</tag2></tag1>";

            cleanupExistingResource();

            XMLResource res =
                    (XMLResource) col.createResource(resId, XMLResource.RESOURCE_TYPE);
            assertTrue(res.getId().equals(resId));
            assertTrue(res.getId().equals(res.getDocumentId()));
            assertTrue(res.getParentCollection() == col);

            res.setContent(content);
            String result = (String) res.getContent();

            assertTrue(result != null);
            // the resource could come back as valid XML but with whitespace
            String processedContent = compressXMLString(content);
            String processedResult = compressXMLString(result);
            //System.out.println("processedContent = " + processedContent);
            //System.out.println("processedResult  = " + processedResult);
            assertTrue(processedContent.equals(processedResult));

            Node node = res.getContentAsDOM();
            assertTrue(node != null);

            res.setContentAsDOM(node);
            Node node2 = res.getContentAsDOM();
            assertTrue(node2 != null);

            // TODO: better validate DOM handling
            // Test creation via DOM
            // Test setContentAsDOM INVALID_RESOURCE exception
            // Test setContentAsDOM WRONG_CONTENT_TYPE exception

            // TODO: add SAX validation
            content = "<?xml version=\"1.0\"?><tag1 name=\"tag1\">";
            content += "<tag2 name=\"tag2\" xmlns:pre=\"http:///pre\">";
            content += "<pre:tag3>value&amp;        &#5030;    </pre:tag3>\n";
            content += "</tag2></tag1><?pi-test value=\"none\"?>";
            XMLReader xr = new SAXParser();
            ContentHandler handler = res.setContentAsSAX();
            xr.setContentHandler(handler);
            if (handler instanceof ErrorHandler) {
                xr.setErrorHandler((ErrorHandler) handler);
            }
            if (content != null) {
                xr.parse(new InputSource(new StringReader(content)));
            }
            // TODO: turn this into a real test case.
            assertNotNull(res.getContent());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private String compressXMLString(String xml) throws Exception {
        XMLReader xr = new SAXParser();
        ContentHandler handler = new StringContentHandler();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(new StringReader(xml)));
        return handler.toString();
    }

    public void testStub() {
        try {

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
