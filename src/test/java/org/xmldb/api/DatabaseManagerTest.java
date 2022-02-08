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
 * =================================================================================================
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * XML:DB Initiative. For more information on the XML:DB Initiative, please see
 * <https://github.com/xmldb-org/>
 */
package org.xmldb.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.xmldb.api.base.ErrorCodes.INSTANCE_NAME_ALREADY_REGISTERED;
import static org.xmldb.api.base.ErrorCodes.INVALID_DATABASE;
import static org.xmldb.api.base.ErrorCodes.INVALID_URI;
import static org.xmldb.api.base.ErrorCodes.NO_SUCH_DATABASE;

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
    assertThat(DatabaseManager.getDatabases()).isEmpty();

    DatabaseManager.databases.put("1", dbOne);
    assertThat(DatabaseManager.getDatabases()).containsExactly(dbOne);

    DatabaseManager.databases.put("2", dbTwo);
    assertThat(DatabaseManager.getDatabases()).containsExactlyInAnyOrder(dbOne, dbTwo);
  }

  @Test
  void testRegisterDatabase_no_or_empty_name() throws XMLDBException {
    when(dbOne.getName()).thenReturn(null);

    assertThatExceptionOfType(XMLDBException.class)
        .isThrownBy(() -> DatabaseManager.registerDatabase(dbOne)).satisfies(e -> {
          assertThat(e.errorCode).isEqualTo(INVALID_DATABASE);
          assertThat(e.vendorErrorCode).isZero();
        });

    assertThat(DatabaseManager.databases).isEmpty();

    when(dbOne.getName()).thenReturn("");

    assertThatExceptionOfType(XMLDBException.class)
        .isThrownBy(() -> DatabaseManager.registerDatabase(dbOne)).satisfies(e -> {
          assertThat(e.errorCode).isEqualTo(INVALID_DATABASE);
          assertThat(e.vendorErrorCode).isZero();
        });

    assertThat(DatabaseManager.databases).isEmpty();
  }

  @Test
  void testRegisterDatabase() throws XMLDBException {
    when(dbOne.getName()).thenReturn("databaseNameOne");

    DatabaseManager.registerDatabase(dbOne);
    assertThat(DatabaseManager.databases.entrySet())
        .containsExactly(entry("databaseNameOne", dbOne));

    when(dbTwo.getName()).thenReturn("databaseNameTwo");

    DatabaseManager.registerDatabase(dbTwo);
    assertThat(DatabaseManager.databases.entrySet()).containsExactlyInAnyOrder(
        entry("databaseNameOne", dbOne), entry("databaseNameTwo", dbTwo));
  }

  @Test
  void testDeregisterDatabase() throws XMLDBException {
    DatabaseManager.databases.put("one", dbOne);
    DatabaseManager.databases.put("databaseNameOne", dbOne);
    DatabaseManager.databases.put("databaseAliasNameOne", dbOne);

    DatabaseManager.deregisterDatabase(dbOne);

    assertThat(DatabaseManager.databases).isEmpty();
  }

  @Test
  void testGetCollectionString() throws XMLDBException {
    DatabaseManager.databases.put("dbName", dbOne);
    Collection collection = mock(Collection.class);

    when(dbOne.getCollection("dbName:collection", null, null)).thenReturn(collection);

    assertThat(DatabaseManager.getCollection("xmldb:dbName:collection")).isEqualTo(collection);
  }

  @Test
  void testGetCollectionStringStringString() throws XMLDBException {
    DatabaseManager.databases.put("dbName", dbOne);
    Collection collection = mock(Collection.class);

    when(dbOne.getCollection("dbName:collection", "username", "password")).thenReturn(collection);

    assertThat(DatabaseManager.getCollection("xmldb:dbName:collection", "username", "password"))
        .isEqualTo(collection);
  }

  @Test
  void testGetConformanceLevel() throws XMLDBException {
    DatabaseManager.databases.put("dbName", dbOne);

    when(dbOne.getConformanceLevel()).thenReturn("1");

    assertThat(DatabaseManager.getConformanceLevel("xmldb:dbName::collection")).isEqualTo("1");
  }

  @Test
  void testGetProperty() {
    DatabaseManager.properties.setProperty("key", "value");

    assertThat(DatabaseManager.getProperty("key")).isEqualTo("value");
    assertThat(DatabaseManager.getProperty("keyTwo")).isNull();
  }

  @Test
  void testSetProperty() {
    DatabaseManager.setProperty("key", "value");

    assertThat(DatabaseManager.properties.getProperty("key")).isEqualTo("value");
  }

  @Test
  void testRegisterDatabase_using_strict_check() throws XMLDBException {
    DatabaseManager.strictRegistrationBehavior = true;

    when(dbOne.getName()).thenReturn("databaseNameOne");
    when(dbTwo.getName()).thenReturn("databaseNameOne");

    DatabaseManager.registerDatabase(dbOne);
    assertThatExceptionOfType(XMLDBException.class)
        .isThrownBy(() -> DatabaseManager.registerDatabase(dbTwo)).satisfies(e -> {
          assertThat(e.errorCode).isEqualTo(INSTANCE_NAME_ALREADY_REGISTERED);
          assertThat(e.vendorErrorCode).isZero();
        });
  }

  @Test
  void testStripURIPrefix() {
    assertThatExceptionOfType(XMLDBException.class)
        .isThrownBy(() -> DatabaseManager.stripURIPrefix("unkown-prefix")).satisfies(e -> {
          assertThat(e.errorCode).isEqualTo(INVALID_URI);
          assertThat(e.vendorErrorCode).isZero();
        });
  }

  @Test
  void testGetDatabaseWrongUri() {
    assertThatExceptionOfType(XMLDBException.class)
        .isThrownBy(() -> DatabaseManager.getDatabase("xmldb:somedb")).satisfies(e -> {
          assertThat(e.errorCode).isEqualTo(INVALID_URI);
          assertThat(e.vendorErrorCode).isZero();
        });
  }

  @Test
  void testGetDatabaseUnkown() {
    assertThatExceptionOfType(XMLDBException.class)
        .isThrownBy(() -> DatabaseManager.getDatabase("xmldb:somedb:collection")).satisfies(e -> {
          assertThat(e.errorCode).isEqualTo(NO_SUCH_DATABASE);
          assertThat(e.vendorErrorCode).isZero();
        });
  }

  private static Entry<String, Database> entry(String key, Database value) {
    return new SimpleEntry<>(key, value);
  }
}
