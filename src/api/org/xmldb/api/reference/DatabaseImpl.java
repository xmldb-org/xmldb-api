package org.xmldb.api.reference;

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
import org.xmldb.api.sdk.*;

import java.io.*;

/**
 * Reference Database implmentation. The reference Database simply resides on
 * the file system. It isn't speedy but it serves the purpose of illustrating 
 * how a driver should work..
 *
 * The path where the data files are located is specified through the property
 * xmldb.data.dir. If this property is not specified it defaults to a directory
 * named data in the current working directory. If the directory does not exist
 * an attempt will be made to create it.
 */
public class DatabaseImpl extends SimpleDatabase {      
   /**
    * The characters expected to separate the INSTANCE_NAME from the file system
    * path
    */
   protected static final String SEP = "://";
   
   /**
    * The property to look for to set the directory
    */
   protected static final String DIR_PROP = "xmldb.data.dir";
   
   /** 
    * The default directory to look for to find the data files.
    */
   protected static final String DATA_PREFIX = "data";
   
   static {
      INSTANCE_NAME = "ref";
      CONFORMANCE_LEVEL = "1";
   }
   
   /**
    * Retrieves a <code>Collection</code> instance based on the URI provided 
    * in the <code>uri</code> parameter. The URI format for this implementation
    * is ref:///path where path is a path in the file system. To locate the data
    * files the database expects a directory data to exist in the current 
    * directory. 
    *
    * @param uri the URI to use to locate the collection.
    * @return The <code>Collection</code> instance
    */
   public Collection getCollection(String uri, String username, 
         String password) throws XMLDBException {
      if ( uri == null ) {
         throw new XMLDBException(ErrorCodes.INVALID_URI);
      }

      if ( ! uri.startsWith(INSTANCE_NAME) ) {
         throw new XMLDBException(ErrorCodes.INVALID_DATABASE);
      }
         
      // Figure out where the repository is
      String dir = "";         
      if ( ( dir = getProperty(DIR_PROP) ) == null ) {
         dir = DATA_PREFIX;
      }
         
      // Make sure the root of the data hierarchy exists
      File root = new File(dir);
      if ( ! root.isDirectory() ) {
         root.mkdir();
      }
      
      // Extract the path from the URI.
      String path = uri.substring(INSTANCE_NAME.length() + SEP.length(), 
         uri.length());      

      try {
         return new CollectionImpl(dir + path);
      }
      catch (java.io.FileNotFoundException e) {
         return null;
      }
   }  
}

