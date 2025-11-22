/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 * Copyright (c) 2000-2025 The XML:DB Initiative. All rights reserved.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

/**
 * TestDatabase is an in-memory implementation of the Database interface. It provides support for
 * managing collections and simulates database operations without requiring a physical or external
 * database system.
 * <p>
 * This class extends ConfigurableImpl, inheriting mechanisms to manage configuration properties
 * using key-value pairs.
 * <p>
 * TestDatabase assigns a default name if no name is provided during construction. It supports
 * operations to add and retrieve collections and implements the necessary Database interface
 * methods.
 */
public class TestDatabase extends ConfigurableImpl implements Database {
  private static final String DETAULT_NAME = "testdatabase";

  private final String name;
  private final Map<String, TestCollection> collections;

  /**
   * Default constructor for the TestDatabase class. Constructs a new TestDatabase instance using
   * the default name.
   */
  public TestDatabase() {
    this(null);
  }

  /**
   * Constructs a new TestDatabase instance with the specified name. If the provided name is null or
   * empty, a default name is assigned.
   *
   * @param name The name of the database. If null or empty, a default name is used.
   */
  public TestDatabase(String name) {
    if (name == null || name.isEmpty()) {
      this.name = DETAULT_NAME;
    } else {
      this.name = name;
    }
    collections = new HashMap<>();
  }

  /**
   * Adds a new collection to the database if it does not already exist. If the collection with the
   * specified name exists, the existing instance is returned.
   *
   * @param collectionName The name of the collection to be added. Must not be null or empty.
   * @return The TestCollection instance corresponding to the given name. Either a newly created
   *         collection or an existing one.
   */
  public TestCollection addCollection(String collectionName) {
    return collections.computeIfAbsent(collectionName, TestCollection::create);
  }

  @Override
  public final String getName() throws XMLDBException {
    return name;
  }

  @Override
  public Collection getCollection(String uri, Properties info) throws XMLDBException {
    return collections.get(uri);
  }

  @Override
  public boolean acceptsURI(String uri) {
    return uri.startsWith(DatabaseManager.URI_PREFIX + "test");
  }

  @Override
  public String getConformanceLevel() {
    return "0";
  }
}
