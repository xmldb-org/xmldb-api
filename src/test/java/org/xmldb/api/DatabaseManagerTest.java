/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 * Copyright (c) 2000-2022 The XML:DB Initiative. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must include the
 * following acknowledgment: "This product includes software developed by the XML:DB Initiative
 * (http://www.xmldb.org/)." Alternately, this acknowledgment may appear in the software itself, if
 * and wherever such third-party acknowledgments normally appear.
 *
 * 4. The name "XML:DB Initiative" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please contact info@xmldb.org.
 *
 * 5. Products derived from this software may not be called "XML:DB", nor may "XML:DB" appear in
 * their name, without prior written permission of the XML:DB Initiative.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * XML:DB Initiative. For more information on the XML:DB Initiative, please see
 * <https://github.com/xmldb-org/>.
 */
package org.xmldb.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.XMLDBException;

@ExtendWith(MockitoExtension.class)
class DatabaseManagerTest {
  @Mock
  Database dbOne;
  @Mock
  Database dbTwo;

  @BeforeEach
  void setUp() {
    DatabaseManager.strictRegistrationBehavior = false;
  }

  @AfterEach
  void tearDown() throws Exception {
    DatabaseManager.databases.clear();
    DatabaseManager.properties.clear();
  }

  @Test
  void testGetDatabases() {
    assertThat(DatabaseManager.getDatabases(), arrayWithSize(0));

    DatabaseManager.databases.put("1", dbOne);
    assertThat(DatabaseManager.getDatabases(), arrayContaining(dbOne));

    DatabaseManager.databases.put("2", dbTwo);
    assertThat(DatabaseManager.getDatabases(), arrayContainingInAnyOrder(dbOne, dbTwo));
  }

  @Test
  void testRegisterDatabase_no_or_empty_name() throws XMLDBException {
    when(dbOne.getNames()).thenReturn(null);

    XMLDBException xmldbEx1 = assertThrows(XMLDBException.class, () -> {
      DatabaseManager.registerDatabase(dbOne);
    });
    assertEquals(ErrorCodes.INVALID_DATABASE, xmldbEx1.errorCode);
    assertEquals(0, DatabaseManager.databases.size());

    when(dbOne.getNames()).thenReturn(new String[] {""});

    XMLDBException xmldbEx2 = assertThrows(XMLDBException.class, () -> {
      DatabaseManager.registerDatabase(dbOne);
    });
    assertEquals(ErrorCodes.INVALID_DATABASE, xmldbEx2.errorCode);
    assertEquals(0, DatabaseManager.databases.size());
  }

  @Test
  void testRegisterDatabase_no_or_empty_names() throws XMLDBException {
    when(dbOne.getNames()).thenReturn(null);

    XMLDBException xmldbEx1 = assertThrows(XMLDBException.class, () -> {
      DatabaseManager.registerDatabase(dbOne);
    });
    assertEquals(ErrorCodes.INVALID_DATABASE, xmldbEx1.errorCode);
    assertEquals(0, DatabaseManager.databases.size());

    when(dbOne.getNames()).thenReturn(new String[0]);
    XMLDBException xmldbEx2 = assertThrows(XMLDBException.class, () -> {
      DatabaseManager.registerDatabase(dbOne);
    });
    assertEquals(ErrorCodes.INVALID_DATABASE, xmldbEx2.errorCode);
    assertEquals(0, DatabaseManager.databases.size());
  }

  @Test
  void testRegisterDatabase() throws XMLDBException {
    when(dbOne.getNames())
        .thenReturn(new String[] {"one", "databaseNameOne", "databaseAliasNameOne"});

    DatabaseManager.registerDatabase(dbOne);
    assertThat(DatabaseManager.databases.entrySet(), containsInAnyOrder(entry("one", dbOne),
        entry("databaseNameOne", dbOne), entry("databaseAliasNameOne", dbOne)));

    when(dbTwo.getNames()).thenReturn(new String[] {"databaseNameTwo", "databaseAliasNameTwo"});

    DatabaseManager.registerDatabase(dbTwo);
    assertThat(DatabaseManager.databases.entrySet(),
        containsInAnyOrder(entry("one", dbOne), entry("databaseNameOne", dbOne),
            entry("databaseAliasNameOne", dbOne), entry("databaseNameTwo", dbTwo),
            entry("databaseAliasNameTwo", dbTwo)));
  }

  @Test
  void testDeregisterDatabase() throws XMLDBException {
    DatabaseManager.databases.put("one", dbOne);
    DatabaseManager.databases.put("databaseNameOne", dbOne);
    DatabaseManager.databases.put("databaseAliasNameOne", dbOne);

    DatabaseManager.deregisterDatabase(dbOne);

    assertEquals(0, DatabaseManager.databases.size());
  }

  @Test
  void testGetCollectionString() throws XMLDBException {
    DatabaseManager.databases.put("dbName", dbOne);
    Collection collection = mock(Collection.class);

    when(dbOne.getCollection("dbName:collection", null, null)).thenReturn(collection);

    assertEquals(collection, DatabaseManager.getCollection("xmldb:dbName:collection"));
  }

  @Test
  void testGetCollectionStringStringString() throws XMLDBException {
    DatabaseManager.databases.put("dbName", dbOne);
    Collection collection = mock(Collection.class);

    when(dbOne.getCollection("dbName:collection", "username", "password")).thenReturn(collection);

    assertEquals(collection,
        DatabaseManager.getCollection("xmldb:dbName:collection", "username", "password"));
  }

  @Test
  void testGetConformanceLevel() throws XMLDBException {
    DatabaseManager.databases.put("dbName", dbOne);

    when(dbOne.getConformanceLevel()).thenReturn("1");

    assertEquals("1", DatabaseManager.getConformanceLevel("xmldb:dbName::collection"));
  }

  @Test
  void testGetProperty() {
    DatabaseManager.properties.setProperty("key", "value");

    assertEquals(DatabaseManager.getProperty("key"), "value");
    assertNull(DatabaseManager.getProperty("keyTwo"));
  }

  @Test
  void testSetProperty() {
    DatabaseManager.setProperty("key", "value");

    assertEquals(DatabaseManager.properties.getProperty("key"), "value");
  }

  @Test
  void testRegisterDatabase_using_strict_check() throws XMLDBException {
    DatabaseManager.strictRegistrationBehavior = true;

    when(dbOne.getNames()).thenReturn(new String[] {"one", "databaseNameOne"});
    when(dbTwo.getNames()).thenReturn(new String[] {"one", "databaseNameOne"});

    DatabaseManager.registerDatabase(dbOne);
    XMLDBException failure = assertThrows(XMLDBException.class, () -> {
      DatabaseManager.registerDatabase(dbTwo);
    });
    assertEquals(ErrorCodes.INSTANCE_NAME_ALREADY_REGISTERED, failure.errorCode);
  }

  private static Entry<String, Database> entry(String key, Database value) {
    return new SimpleEntry<>(key, value);
  }
}
