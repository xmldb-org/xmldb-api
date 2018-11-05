package org.xmldb.api.sdk;

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
 
/**
 * Simple <code>Database</code> implementation intended to be used as a base
 * class for a specific implementation.
 *
 * Extending classes MUST set INSTANCE_NAME and SHOULD set CONFORMANCE_LEVEL to
 * values appropriate for their specific implementation.
 */
public abstract class SimpleDatabase extends SimpleConfigurable 
      implements Database {
   /**
    * Name used in the uri for collections associated with this instance.
    */
   protected static String INSTANCE_NAME = "unknown";   

   /**
    * The XML:DB API Core Level Conformance of this implementation.
    */
   protected static String CONFORMANCE_LEVEL = "0";
 
   protected String[] allNames = { INSTANCE_NAME };  

   /**
    * Returns the name associated with the Configurable object.    
    */
   public String getName() throws XMLDBException {
      return allNames[0];
   }
  
   public String[] getNames() throws XMLDBException {
      return allNames;
   }
 
   /**
    * Retrieves a <code>Collection</code> instance based on the URI provided 
    * in the <code>uri</code> parameter. Implementations must override this
    * method.
    *
    * @param uri the URI to use to locate the collection.
    * @return The <code>Collection</code> instance
    */
   public Collection getCollection(String uri, String username, 
         String password) throws XMLDBException {
      return null;
   }

   /**
    * acceptsURI determines whether this <code>Database</code> implementation 
    * can handle the URI. 
    *
    * @param uri the URI to check for.
    * @return true if the URI can be handled, false otherwise.
    */
   public boolean acceptsURI(String uri) throws XMLDBException {
      if ( uri.startsWith(INSTANCE_NAME) ) {
         return true;
      }

      return false;      
   }

   /**
    * Returns the XML:DB API Conformance level for the implementation. 
    *
    * @return the XML:DB API conformance level for this implementation.
    */
   public String getConformanceLevel() throws XMLDBException {
      return CONFORMANCE_LEVEL;
   }
}

