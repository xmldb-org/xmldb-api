package org.xmldb.common.xml.queries.test;
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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xmldb.common.xml.queries.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * @version $Revision: 1.3 $ $Date: 2003/04/24 14:14:10 $
 * @author <a href="http://www.softwarebuero.de">SMB</a>
 */
public class XPathTest {

    public static void main(String[] args) throws Exception {

//        System.getProperties().put( "org.xmldb.common.xml.queries.XPathQueryFactory",
//                                    "org.xmldb.common.xml.queries.xt.XPathQueryFactoryImpl");

        XPathQueryFactory factory = XPathQueryFactory.newInstance();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(args[0]);

        long start = System.currentTimeMillis();
        XPathQuery query = factory.newXPathQuery();
        System.out.println(query.getClass().getName());
        query.setQString("/*");
        System.out.println("init: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        XObject result = query.execute(doc);
        System.out.println("execute: " + (System.currentTimeMillis() - start));

        printResult(result);

        result = query.execute(doc);
        printResult(result);
    }


    protected static void printResult(XObject result) throws Exception {
        if (result == null) {
            System.out.println("XPath query: result: null");
        } else {
            System.out.print("XPATH query: result: ");
            // cast the query result to
            switch (result.getType()) {
                case XObject.CLASS_BOOLEAN:
                    System.out.println("(Boolean): " + result.bool());
                    break;
                case XObject.CLASS_NUMBER:
                    System.out.println("(Number): " + result.num());
                    break;
                case XObject.CLASS_STRING:
                    System.out.println("(String): " + result.str());
                    break;
                case XObject.CLASS_RTREEFRAG:
                    System.out.println("(DocumentFragment): -");
                    break;
                case XObject.CLASS_NODESET:
                    NodeList nodeList = result.nodeset();
                    System.out.println("(NodeList): " + nodeList.getLength() + " Entries");

                    for (int i = 0; i < nodeList.getLength(); i++) {
                        System.out.print(i + 1 + " Entry: ");
                        System.out.println("        value=" + nodeList.item(i).getNodeName());
                    }
                    break;
                default:
                    System.out.println("(Unknown): -");
                    break;
            }
        }
    }

}
