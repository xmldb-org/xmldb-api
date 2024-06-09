/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 * Copyright (c) 2000-2024 The XML:DB Initiative. All rights reserved.
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.xmldb.api.base.ErrorCodes.INSTANCE_NAME_ALREADY_REGISTERED;
import static org.xmldb.api.base.ErrorCodes.NO_SUCH_DATABASE;

import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.DatabaseAction;
import org.xmldb.api.base.XMLDBException;

@ExtendWith(MockitoExtension.class)
class DatabaseManagerTest {
  @Mock
  Database dbOne;
  @Mock
  Database dbTwo;
  @Mock
  DatabaseAction dbAction;
  @Mock
  Collection collection;

  @AfterEach
  void tearDown() {
    DatabaseManager.getDatabases().forEach(DatabaseManager::deregisterDatabase);
    DatabaseManager.setProperty("key", null);
    verifyNoMoreInteractions(dbOne, dbTwo, dbAction, collection);
  }

  @Test
  void testGetDatabases() throws XMLDBException {
    assertThat(DatabaseManager.getDatabases()).isEmpty();

    DatabaseManager.registerDatabase(dbOne);
    assertThat(DatabaseManager.getDatabases()).containsExactly(dbOne);

    DatabaseManager.registerDatabase(dbTwo);
    assertThat(DatabaseManager.getDatabases()).containsExactlyInAnyOrder(dbOne, dbTwo);
  }

  @Test
  void testRegisterDatabase() throws XMLDBException {
    DatabaseManager.registerDatabase(dbOne);
    assertThat(DatabaseManager.getDatabases()).containsExactly(dbOne);

    DatabaseManager.registerDatabase(dbTwo);
    assertThat(DatabaseManager.getDatabases()).containsExactlyInAnyOrder(dbOne, dbTwo);
  }

  @Test
  void testDeregisterDatabase() throws XMLDBException {
    DatabaseManager.registerDatabase(dbOne);
    DatabaseManager.registerDatabase(dbTwo, dbAction);

    DatabaseManager.deregisterDatabase(dbOne);
    DatabaseManager.deregisterDatabase(dbTwo);

    assertThat(DatabaseManager.getDatabases()).isEmpty();
    verify(dbAction).deregister();
  }

  @Test
  void testGetCollection() throws XMLDBException {
    DatabaseManager.registerDatabase(dbOne);
    Properties info = new Properties();

    when(dbOne.acceptsURI("xmldb:dbName:collection")).thenReturn(true);
    when(dbOne.getCollection("xmldb:dbName:collection", info)).thenReturn(collection);

    assertThat(DatabaseManager.getCollection("xmldb:dbName:collection")).isEqualTo(collection);
  }

  @Test
  void testGetCollectionUserPassword() throws XMLDBException {
    DatabaseManager.registerDatabase(dbOne);
    Properties info = new Properties();
    info.setProperty("user", "username1");
    info.setProperty("password", "password1");

    when(dbOne.acceptsURI("xmldb:dbName:collection")).thenReturn(true);
    when(dbOne.getCollection("xmldb:dbName:collection", info)).thenReturn(collection);

    assertThat(DatabaseManager.getCollection("xmldb:dbName:collection", "username1", "password1"))
        .isEqualTo(collection);
  }

  @Test
  void testGetCollectionConnectInfo() throws XMLDBException {
    DatabaseManager.registerDatabase(dbOne);
    Properties info = new Properties();
    info.setProperty("user", "username2");
    info.setProperty("password", "password2");

    when(dbOne.acceptsURI("xmldb:dbName:collection")).thenReturn(true);
    when(dbOne.getCollection("xmldb:dbName:collection", info)).thenReturn(collection);

    assertThat(DatabaseManager.getCollection("xmldb:dbName:collection", info))
        .isEqualTo(collection);
  }

  @Test
  void testGetConformanceLevel() throws XMLDBException {
    DatabaseManager.registerDatabase(dbOne);

    when(dbOne.acceptsURI("xmldb:dbName:collection")).thenReturn(true);
    when(dbOne.getConformanceLevel()).thenReturn("1");

    assertThat(DatabaseManager.getConformanceLevel("xmldb:dbName:collection")).isEqualTo("1");
  }

  @Test
  void testGetProperty() {
    DatabaseManager.setProperty("key", "value");

    assertThat(DatabaseManager.getProperty("key")).isEqualTo("value");
    assertThat(DatabaseManager.getProperty("keyTwo")).isNull();
  }

  @Test
  void testSetProperty() {
    DatabaseManager.setProperty("key", "value");

    assertThat(DatabaseManager.getProperty("key")).isEqualTo("value");
  }

  @Test
  void testRegisterDatabase_alreadyRegistered() throws XMLDBException {
    DatabaseManager.registerDatabase(dbOne);
    assertThatExceptionOfType(XMLDBException.class)
        .isThrownBy(() -> DatabaseManager.registerDatabase(dbOne)).satisfies(e -> {
          assertThat(e.errorCode).isEqualTo(INSTANCE_NAME_ALREADY_REGISTERED);
          assertThat(e.vendorErrorCode).isZero();
        });
  }

  @Test
  void testWithDatabaseConnectionError() throws XMLDBException {
    Properties info = new Properties();
    XMLDBException error = new XMLDBException();
    DatabaseManager.registerDatabase(dbOne);

    when(dbOne.acceptsURI("xmldb:somedb:collection")).thenReturn(true);
    when(dbOne.getCollection("xmldb:somedb:collection", info)).thenThrow(error);

    assertThatExceptionOfType(XMLDBException.class)
        .isThrownBy(() -> DatabaseManager.withDatabase("xmldb:somedb:collection",
            db -> db.getCollection("xmldb:somedb:collection", info)))
        .isEqualTo(error);
  }

  @Test
  void testWithDatabaseNull() throws XMLDBException {
    Properties info = new Properties();
    DatabaseManager.registerDatabase(dbOne);
    DatabaseManager.registerDatabase(dbTwo);

    when(dbOne.acceptsURI("xmldb:somedb:collection")).thenReturn(true);
    when(dbOne.getCollection("xmldb:somedb:collection", info)).thenReturn(null);

    assertThat((Collection) DatabaseManager.withDatabase("xmldb:somedb:collection",
        db -> db.getCollection("xmldb:somedb:collection", info))).isNull();
  }

  @Test
  void testWithDatabaseUnknown() {
    assertThatExceptionOfType(XMLDBException.class)
        .isThrownBy(() -> DatabaseManager.withDatabase("xmldb:somedb:collection", db -> "check"))
        .satisfies(e -> {
          assertThat(e.errorCode).isEqualTo(NO_SUCH_DATABASE);
          assertThat(e.vendorErrorCode).isZero();
        });
  }
}
