package org.xmldb.api.reference.modules;

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

import org.w3c.dom.*;

import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;
import org.xmldb.api.sdk.*;
import org.xmldb.api.reference.*;
import org.xmldb.api.base.Collection;

import java.io.*;

/**
 * CollectionManager provides management facilities for a Collection instance.
 * Administrative access is required for retrieval of the CollectionManager
 * interface. User level access is provided through the underlying Collection
 * itself.
 */
public class CollectionManagementServiceImpl extends SimpleConfigurable 
      implements CollectionManagementService {
   
   protected Collection collection = null;
   protected String basePath = null;
   
   /**
    * Creates a new CollectionManager service    
    */
   public CollectionManagementServiceImpl(String basePath) {
      super();
      this.basePath = basePath;
   }

   /**
    * Returns the name of the Service
    *
    * @return the name of the Service
    */
   public String getName() {
      return "CollectionManagementService";
   }

   /**
    * Returns the version of the Service
    *
    * @return the version of the Service
    */
   public String getVersion() {
      return "1.0";
   }

   /**
    * Provides a reference to the XML:DB collection instance that this service
    * is associated with.
    *
    * @param col the XML:DB collection instance associated with this Service
    */
   public void setCollection(org.xmldb.api.base.Collection col) {
      this.collection = col;
   }
   
   /**
    * Creates a simple collection with a basic default configuration. More
    * complex configuration requires using a proprietary interface
    */
   public Collection createCollection(String name) throws XMLDBException {
      String path = basePath + File.separator + name;
      File dir = new File(path);
      dir.mkdir();
      try {
         return new CollectionImpl(path);
      }
      catch (Exception e) {
         return null;
      }
   }
   
   /**
    * Removes the named collection from the system.
    */
   public void removeCollection(String name) throws XMLDBException {
      String path = basePath + File.separator + name;
      File dir = new File(path);
      if ( dir.isDirectory() ) {
         dir.delete();
      }
   }

}

