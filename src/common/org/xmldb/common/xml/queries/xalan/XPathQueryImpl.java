package org.xmldb.common.xml.queries.xalan;
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

import org.apache.xalan.xpath.*;
import org.apache.xalan.xpath.xml.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.xmldb.common.xml.queries.XObject;
import org.xmldb.common.xml.queries.XPathQuery;


/**
 * @version $Revision: 1.3 $ $Date: 2003/04/24 14:14:10 $
 * @author <a href="http://www.softwarebuero.de">SMB</a>
 */
public final class XPathQueryImpl implements XPathQuery {

    //
    // Data
    //

    private String qstring;

    private Node rootNode;

    private Node namespace;

    private NodeFilter filter;

    private boolean hasChanged = true;

    private XPathSupport xpathSupport;

    private XPathProcessorImpl parser;

    private XPath xpath;

    private PrefixResolver prefixResolver;


    public XPathQueryImpl() {
        // Since we don't have a XML Parser involved here, install some
        // default support for things like namespaces, etc.
        xpathSupport = new XMLParserLiaisonDefault();

        // Create a XPath parser.
        parser = new org.apache.xalan.xpath.XPathProcessorImpl(xpathSupport);

        // Create the XPath object.
        xpath = new XPath();
    }


    public void setQString(String qstring) throws Exception {
        this.qstring = qstring;
        this.hasChanged = true;
    }


    public void setNamespace(Node namespace) throws Exception {
        this.namespace = namespace;
        this.hasChanged = true;
    }


    public void setNodeFilter(NodeFilter filter) throws Exception {
        this.filter = filter;
        this.hasChanged = true;
    }


    protected void prepare() throws Exception {
        // Create an object to resolve namespace prefixes.
        // XPath namespaces are resolved from the input root node's
        // document element if it is a root node, or else the current root node.
        prefixResolver = namespace != null ? new PrefixResolverDefault(namespace):
                new PrefixResolverDefault(rootNode);

        // parse the specified Query-String and build an Parse-Tree
        parser.initXPath(xpath, qstring, prefixResolver);

        hasChanged = false;
    }


    /**
     * Execute the xpath.
     *
     * @param rootNode The node from which the query should start or null.
     * @return The XObject insulating the query result.
     */
    public XObject execute(Node rootNode) throws Exception {
        if (rootNode.getNodeType() == Node.DOCUMENT_NODE) {
            rootNode = ((Document) rootNode).getDocumentElement();
        }

        this.rootNode = rootNode;
        prepare();

        // execute the XPath query on the specified root node
        return new XObjectImpl(xpath.execute(xpathSupport, rootNode, prefixResolver));
    }

}

