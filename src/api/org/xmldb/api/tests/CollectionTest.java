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

public class CollectionTest extends XMLDBTestCase {   
   public CollectionTest(String name) {
      super(name);
   }
   
   public static Test suite() {
      return new TestSuite(CollectionTest.class);
   }   
   
   public void testGetName( ) {
      try {             
         assertTrue(col.getName().equals("child1"));
      } catch (Exception e) {
         fail( e.getMessage( ) );
      }
   }
   
   public void testGetParentCollection() {
      try {     
         if ( supportsNested ) {
            Collection child = col.getChildCollection("subchild1");
            
            Collection parent = child.getParentCollection();
                     
            assertTrue(parent.getName().equals("child1"));
            
            // TODO: need to test behavior on the root collection.
            assertTrue(parent.getParentCollection().getParentCollection() == null);
         }
      } catch (Exception e) {
         fail( e.getMessage( ) );
      }   
   }
   
   public void testGetChildCollectionCount() {
      try {    
         if ( supportsNested ) {         
            assertTrue(col.getChildCollectionCount() == 2);
         }
         else {
            assertTrue(col.getChildCollectionCount() == 0);
         }
      } catch (Exception e) {
         fail( e.getMessage( ) );
      }   
   }
   
   public void testListChildCollections() {
      try {             
         String[] children = col.listChildCollections();
         
         if ( supportsNested ) {
            // We don't want to assume order here
            boolean match1 = false;
            boolean match2 = false;         
            for ( int i = 0; i < children.length; i++ ) {
               String child = children[i];
               if ( child.equals("subchild1") )
                  match1 = true;            
               if ( child.equals("subchild2") )
                  match2 = true;       
            }
            
            assertTrue(match1 && match2);
         
            try {
               assertTrue(children[2] == null);
            }
            catch (ArrayIndexOutOfBoundsException e) {
               // we should pass if we get this exception            
            }
         }
         else {
            assertTrue(children.length == 0);
         }
      } catch (Exception e) {
         fail( e.getMessage( ) );
      }   
   }
   
   
   public void testGetChildCollection() {
      try {             
         if ( supportsNested ) {
            Collection child = col.getChildCollection("subchild1");
            assertTrue(child.getName().equals("subchild1"));
         }
         else {
            assertTrue(col.getChildCollection("subchild1") == null);
         }
      } catch (Exception e) {
         fail( e.getMessage( ) );
      }   
   }
   
   public void testGetResourceCount() {
      try { 
         // Account for the fact the binary resources are optional  
         if ( supportsBinary ) {          
            assertTrue(col.getResourceCount() == 4);
         }
         else {
            assertTrue(col.getResourceCount() == 3);
         }
      } catch (Exception e) {
         fail( e.getMessage( ) );
      }   
   }
   
   public void testListResources() {
      try {             
         String[] resources = col.listResources();
         // We don't want to assume order here
         boolean match1 = false;
         boolean match2 = false;
         boolean match3 = false;
         for ( int i = 0; i < resources.length; i++ ) {
            String res = resources[i];
            if ( res.equals("test1.xml") )
               match1 = true;            
            if ( res.equals("test2.xml") )
               match2 = true;
            if ( res.equals("test3.xml") )
               match3 = true;                  
         }
         
         assertTrue(match1 && match2 && match3);
         
         try {
            // TODO: should account for the fact the binary resources are
            // optional
            assertTrue(resources[4] == null);
         }
         catch (ArrayIndexOutOfBoundsException e) {
            // we should pass if we get this exception            
         }
      } catch (Exception e) {
         fail( e.getMessage( ) );
      }   
   }
   
   public void testCreateResource() {
      try {             
         // Test for XMLResource and explicit id
         XMLResource res = (XMLResource) col.createResource("test4.xml", 
            XMLResource.RESOURCE_TYPE);         
         assertTrue(res.getResourceType().equals(XMLResource.RESOURCE_TYPE));
         assertTrue(res.getId().equals("test4.xml"));
         
         // Test for BinaryResource and auto generate id
         if ( supportsBinary ) {
            BinaryResource res2 = (BinaryResource) col.createResource("", 
               BinaryResource.RESOURCE_TYPE);
            assertTrue(res2.getResourceType().equals(BinaryResource.RESOURCE_TYPE));
            assertTrue(! res2.getId().equals(""));
         }

         // Test for unknown resource type
         try {
            Resource res3 = col.createResource("", "GoogleResource");
            assertTrue(false);
         }
         catch (XMLDBException e) {
            assertTrue(e.errorCode == ErrorCodes.UNKNOWN_RESOURCE_TYPE);
         }
      } catch (Exception e) {
         fail( e.getMessage( ) );
      }   
   }
   
   
   public void testStoreResource() {
      String content = "<?xml version=\"1.0\"?><tag1><tag2>value</tag2></tag1>";
      try {             
         XMLResource res = (XMLResource) col.createResource("", 
            XMLResource.RESOURCE_TYPE);
         res.setContent(content);
         col.storeResource(res);
         
         XMLResource res2 = (XMLResource) col.getResource(res.getId());
         assertTrue(res2 != null);
         assertTrue(res2.getId().equals(res.getId()));
         assertTrue(res2.getContent() != null);
         
         col.removeResource(res);
      } catch (Exception e) {       
         fail( e.getMessage( ) );
      }   
   }
   
   public void testRemoveResource() {
      String content = "<?xml version=\"1.0\"?><tag1><tag2>value</tag2></tag1>"; 
      try {
         // Create a new resource             
         XMLResource res = (XMLResource) col.createResource("", 
            XMLResource.RESOURCE_TYPE);
         res.setContent(content);
         col.storeResource(res);
         
         // Verify the resource exists
         XMLResource res2 = (XMLResource) col.getResource(res.getId());
         assertTrue(res2 != null);
         assertTrue(res2.getId().equals(res.getId()));
         assertTrue(res2.getContent() != null);
         
         // Remove the resource
         col.removeResource(res2);
         Resource res3 = col.getResource(res.getId());
         assertTrue(res3 == null);
         
         // Make sure the resource is gone.
         try {
            col.removeResource(res);
         }
         catch (XMLDBException e) {
            assertTrue(e.errorCode == ErrorCodes.NO_SUCH_RESOURCE);
         }         
      } catch (Exception e) {
         fail( e.getMessage( ) );
      }   
   }
   
   public void testGetResource() {
      try {             
         // Check for an XML resource
         Resource res = col.getResource("test1.xml");
         assertTrue(res != null);
         assertTrue(res.getResourceType().equals(XMLResource.RESOURCE_TYPE));
         assertTrue(res.getContent() != null);
         
         // Check for a binary resource
         if ( supportsBinary ) {
            res = col.getResource("image.gif");
            assertTrue(res != null);
            assertTrue(res.getResourceType().equals(BinaryResource.RESOURCE_TYPE));
            assertTrue(res.getContent() != null);
         }
         
         // Check for a missing resource
         res = col.getResource("missing.xml");
         assertTrue(res == null);
         
      } catch (Exception e) {
         fail( e.getMessage( ) );
      }   
   }
   
   public void testXPathQueryService() {
      try {             
         if ( supportsXPath ) {
            XPathQueryService serv = 
               (XPathQueryService) col.getService("XPathQueryService", "1.0");
            assertTrue(serv != null);
            
            ResourceSet resultSet = serv.query("/data");
            ResourceIterator result = resultSet.getIterator();
            
            int i = 0;
            while (result.hasMoreResources()) {
               result.nextResource();        
               i++;
            }         
            assertTrue(i == 3);
            
            resultSet = serv.query("/data/child/subchild[@name='subchild1']");
            result = resultSet.getIterator();
            i = 0;
            while (result.hasMoreResources()) {
               result.nextResource();        
               i++;
            }                  
            assertTrue(i == 6);
            
            resultSet = serv.query("/data/child/subchild/subsubchild[@name='subsubchild1']");
            result = resultSet.getIterator();
            i = 0;
            while (result.hasMoreResources()) {
               result.nextResource();        
               i++;
            }                  
            assertTrue(i == 1);
         }
      } catch (Exception e) {         
         fail( e.getMessage( ) );
      }   
   }
   
   public void testStub() {
      try {             
    
      } catch (Exception e) {
         fail( e.getMessage( ) );
      }   
   }
}
