package org.xmldb.common.xml.queries;
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

import java.io.Serializable;


/**
 * @version $Revision: 1.3 $ $Date: 2003/04/24 14:14:10 $
 * @author <a href="http://www.softwarebuero.de">SMB</a>
 */
public interface XObject extends Serializable {

    //
    // Constants
    //

    /** Result is null. */
    public final static int CLASS_NULL = -1;

    /** Result type is not known. */
    public final static int CLASS_UNKNOWN = 0;

    /** Result type is a boolean. */
    public final static int CLASS_BOOLEAN = 1;

    /** Result type is a Integer. */
    public final static int CLASS_NUMBER = 2;

    /** Result type is a String. */
    public final static int CLASS_STRING = 3;

    /** Result type is a NodeSet. */
    public final static int CLASS_NODESET = 4;

    /** Result type is a Document Fragment. */
    public final static int CLASS_RTREEFRAG = 5;


    /**
     */
    public int getType() throws Exception;


    /**
     * Cast result object to a boolean.
     * @return The Object casted to boolean
     * @exception org.xml.sax.SAXException If any error occurs.
     */
    public boolean bool() throws Exception;


    /**
     * Cast result object to a number.
     * @return The Object casted to double.
     * @exception org.xml.sax.SAXException If any error occurs.
     */
    public double num() throws Exception;


    /**
     * Cast result object to a string.
     * @return The Object casted to string.
     */
    public String str() throws Exception;


    /**
     * Cast result object to a nodelist.
     * @return The Object casted to NodeList.
     * @exception org.xml.sax.SAXException If any error occurs.
     */
    public NodeList nodeset() throws Exception;


    /**
     * Cast result object to a result tree fragment.
     * @return The Object casted to DocumentFragment.
     */
    public DocumentFragment rtree() throws Exception;

}
