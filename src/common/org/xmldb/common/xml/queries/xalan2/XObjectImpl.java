package org.xmldb.common.xml.queries.xalan2;
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
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.xmldb.common.xml.queries.XObject;


/**
 * Wrapper for the Xalan XObject and maybe other XPath implementation
 * specific things.
 * @version $Revision: 1.3 $ $Date: 2003/05/16 16:01:36 $
 * @author <a href="http://www.softwarebuero.de">SMB</a>
 */
public final class XObjectImpl implements XObject {

    //
    // Data
    //
    private org.apache.xpath.objects.XObject _xobj = null;

    /**
     * Creates a new XalanXObject.
     * @param xobj Xalans native XObject that should be wrapped.
     * @exception IllegalArgumentException If the given XObject was null.
     */
    public XObjectImpl (org.apache.xpath.objects.XObject xobj) throws IllegalArgumentException {
        if (xobj == null)
            throw new IllegalArgumentException ("XalanXObject(): Argument was null!");
        _xobj = xobj;
    }

    public int getType() {
        return _xobj.getType();
    }

    public boolean bool()
        throws javax.xml.transform.TransformerException {
        return _xobj.bool();
    }

    public double num()
        throws javax.xml.transform.TransformerException {
        return _xobj.num();
    }

    public String str() {
        return _xobj.str();
    }

    public NodeList nodeset()
        throws javax.xml.transform.TransformerException {
        return (NodeList) _xobj.nodelist();
    }

    public DocumentFragment rtree() {
        return _xobj.rtree();
    }
}

