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

import java.io.*;

import junit.framework.*;

import org.xmldb.api.*;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;

public class XMLDBTestCase extends TestCase {
   protected Collection col = null;
   protected Document config = null;
   protected String driver = "org.xmldb.api.reference.DatabaseImpl";
   protected String rootCollection = "child1";
   protected String collectionURI = null;
   protected String uriPrefix = null;
   protected String username = null;
   protected String password = null;
   
   protected boolean quiet = false;
   
   // Capabilities flags
   protected boolean supportsXPath = false;
   protected boolean supportsBinary = false;
   protected boolean supportsTransactions = false;
   protected boolean supportsManagement = false;
   protected boolean supportsXUpdate = false;
   protected boolean supportsNested = false;
   
   public XMLDBTestCase(String name) {
      super(name);
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();      
         factory.setValidating(false);
         factory.setNamespaceAware(false);
         DocumentBuilder builder = factory.newDocumentBuilder();
         config = builder.parse("config.xml");
         
         NodeList nodes = config.getElementsByTagName("test");
         Element test = (Element) nodes.item(0);
         
         driver = test.getAttribute("driver");;
         collectionURI = test.getAttribute("uriPrefix") + rootCollection;
         uriPrefix = test.getAttribute("uriPrefix");
         
         username = test.getAttribute("username");
         password = test.getAttribute("password");
         
         String quietMode = test.getAttribute("quietMode");
         if ( ( quietMode != null ) && quietMode.equals("true") ) {
            quiet = true;
         }
         
         String nested = test.getAttribute("supportsNested");
         if ( ( nested != null ) && nested.equals("true") ) {
            supportsNested = true;
         }
      }
      catch (Exception e) {
         System.out.println("Could not parse the configuration config.xml");
         System.exit(1);
      }
   }
      
   public void setUp() throws Exception {

      Class c = Class.forName(driver);
      
      Database database = (Database) c.newInstance();
      System.out.println( "starting to register database" );
      DatabaseManager.registerDatabase(database);
      
      col =
         DatabaseManager.getCollection(uriPrefix, username, password);
      
      checkCapabilities(database);            
      initRepository();

      col.close();
            
      col =
         DatabaseManager.getCollection(collectionURI, username, password);
      
      if (col == null) {
         System.out.println("Collection could not be created");
         System.exit(-1);
      }      
   }
   
   public void tearDown() {
      try {
         if ( supportsManagement ) {
            col.close();
            
            col =
               DatabaseManager.getCollection(uriPrefix, username, password);
   
            CollectionManagementService man = (CollectionManagementService)
               col.getService("CollectionManagementService", "1.0");
   
            // Removing the collections should clear out all data
            for (int i = 1; i <= 3; i++) {
               man.removeCollection("child" + i);
            }
         }
            
         col.close();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }     

   protected void initRepository() throws Exception {
      Collection child = null;
      
      if ( supportsManagement ) {
         // Create Collection hierarchy.
         try {
            CollectionManagementService man = (CollectionManagementService)
               col.getService("CollectionManagementService", "1.0");
            for (int i = 1; i <= 3; i++) {
               man.createCollection("child" + i);
            }
            
            if ( supportsNested ) {   
               child = DatabaseManager.getCollection(uriPrefix + "child2", 
                  username, password);
      
               man = (CollectionManagementService)
                  child.getService("CollectionManagementService", "1.0");
               
               man.createCollection("subchild1"); 
            }
                        
            child = DatabaseManager.getCollection(uriPrefix + "child1", 
               username, password);
      
            if ( supportsNested ) {
               man = (CollectionManagementService)
                  child.getService("CollectionManagementService", "1.0");
               for (int i = 1; i <= 2; i++) {
                  man.createCollection("subchild" + i);
               }
            }
         }
         catch (Exception e) {
            // If we get here the collections probably already exist.  
            // TODO: we mat want to handle this differently
            child = DatabaseManager.getCollection(uriPrefix + "child1", 
               username, password);
         }
         
         // Add the sample data.
         insertResource(child, "test1.xml", XMLResource.RESOURCE_TYPE);
         insertResource(child, "test2.xml", XMLResource.RESOURCE_TYPE);
         insertResource(child, "test3.xml", XMLResource.RESOURCE_TYPE);
         if ( supportsBinary ) {
            insertResource(child, "image.gif", BinaryResource.RESOURCE_TYPE);
         }
      }
   }
   
   protected void insertResource(Collection col, String key, String type) 
         throws Exception {
            
      FileInputStream file = new FileInputStream("tests/files/" + key);
      byte[] contents = new byte[file.available()];
      file.read(contents);
      file.close();
      
      Resource res = col.createResource(key, type);
      if ( type.equals(XMLResource.RESOURCE_TYPE) ) {
         res.setContent(new String(contents));
      }
      else {
         res.setContent(contents);
      }
      
      col.storeResource(res);
   }
   
   protected void checkCapabilities(Database database) throws Exception {

      // Check for CollectionManagement support
      if ( col.getService("CollectionManagementService", "1.0") != null ) {
         supportsManagement = true;
      }
      else {
         if ( ! quiet ) {
            System.out.println("This driver does not appear to support the " +
               "CollectionManagement Service. No auto set of the repository will " +
               "possible. Make sure your repository has the necessary collections " +
               "already created.");
         }
      }
      
      // Check to see if the driver supports XPath
      if ( Integer.parseInt(database.getConformanceLevel()) >= 1 ) {
         supportsXPath = true;
      }
      else {
         if ( ! quiet ) {
            System.out.println("This driver does not appear to support the" +
               "XPathQueryService. No XPath tests will be performed.");
         }
      }
      
      // Check for BinaryResource support
      try {
         col.createResource(null, BinaryResource.RESOURCE_TYPE);
         supportsBinary = true;
      }
      catch (XMLDBException e) {
         if ( e.errorCode == ErrorCodes.UNKNOWN_RESOURCE_TYPE ) {
            if ( ! quiet ) {
               System.out.println("This driver does not appear to support" +
                  "BinaryResource. No BinaryResource tests will be performed.");
            }
         }
      }
      
      // Check for Transaction Support
      if ( col.getService("TransactionService", "1.0") != null ) {
         supportsTransactions = true;
      }
      else {
         if ( ! quiet ) {
            System.out.println("This driver does not appear to support the " +
               "TransactionService. No transaction functionality will be tested.");
         }
      }
            
      // Check for XUpdate support
      if ( col.getService("XUpdateQueryService", "1.0") != null ) {
         supportsXUpdate = true;
      }
      else {
         if ( ! quiet ) {
            System.out.println("This driver does not appear to support the " +
               "XUpdateQueryService. No XUpdate functionality will be tested.");
         }
      }      
   }
}
