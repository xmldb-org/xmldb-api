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

import static org.xmldb.api.base.ErrorCodes.INSTANCE_NAME_ALREADY_REGISTERED;
import static org.xmldb.api.base.ErrorCodes.INVALID_DATABASE;
import static org.xmldb.api.base.ErrorCodes.INVALID_URI;
import static org.xmldb.api.base.ErrorCodes.NO_SUCH_DATABASE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.StampedLock;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

/**
 * {@code DatabaseManager} is the entry point for the API and enables you to get the initial
 * {@code Collection} references necessary to do anything useful with the API.
 * {@code DatabaseManager} is intended to be provided as a concrete implementation in a particular
 * programming language. Individual language mappings should define the exact syntax and semantics
 * of its use.
 */
public final class DatabaseManager {
  static final String URI_PREFIX = "xmldb:";

  static final Properties properties = new Properties();
  static final StampedLock dbLock = new StampedLock();
  static final Map<String, Database> databases = new HashMap<>(5);

  static boolean strictRegistrationBehavior =
      Boolean.getBoolean("org.xmldb.api.strictRegistrationBehavior");

  private DatabaseManager() {}

  /**
   * Returns a set of all available {@code Database} implementations that have been registered with
   * this {@code DatabaseManager}.
   *
   * @return An array of {@code Database} instances. One for each {@code Database} registered with
   *         the {@code DatabaseManager}. If no {@code Database} instances exist then an empty set
   *         is returned.
   * @since 2.0
   */
  public static Set<Database> getDatabases() {
    // try optimistic read first
    long stamp = dbLock.tryOptimisticRead();
    if (stamp > 0) {
      final Set<Database> result = new HashSet<>(databases.values());
      if (dbLock.validate(stamp)) {
        return result;
      }
    }

    // fallback to locking read
    stamp = dbLock.readLock();
    try {
      return new HashSet<>(databases.values());
    } finally {
      dbLock.unlockRead(stamp);
    }
  }

  /**
   * Registers a new {@code Database} implementation with the {@code DatabaseManager}.
   *
   * @param database The database instance to register.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.INVALID_DATABASE} if the provided
   *         {@code Database} instance is invalid.
   */
  public static void registerDatabase(final Database database) throws XMLDBException {
    final String name = database.getName();
    if (name == null || name.isEmpty()) {
      throw new XMLDBException(INVALID_DATABASE);
    }
    final long stamp = dbLock.writeLock();
    try {
      updateDatabases(name, database);
    } finally {
      dbLock.unlockWrite(stamp);
    }

  }

  private static void updateDatabases(final String databaseName, final Database database)
      throws XMLDBException {
    final Database existing = databases.putIfAbsent(databaseName, database);
    if (existing != null && existing != database && strictRegistrationBehavior) {
      throw new XMLDBException(INSTANCE_NAME_ALREADY_REGISTERED);
    }
  }

  /**
   * Deregisters a {@code Database} implementation from the {@code DatabaseManager}. Once a
   * {@code Database} has been deregistered it can no longer be used to handle requests.
   *
   * @param database The {@code Database} instance to deregister.
   */
  public static void deregisterDatabase(final Database database) {
    final long stamp = dbLock.writeLock();
    try {
      databases.values().removeIf(database::equals);
    } finally {
      dbLock.unlockWrite(stamp);
    }
  }

  /**
   * Retrieves a {@code Collection} instance from the database for the given URI. The format of the
   * majority of the URI is database implementation specific however the uri must begin with
   * characters xmldb: and be followed by the name of the database instance as returned by
   * {@code Database.getName()} and a colon character. An example would be for the database named
   * "vendordb" the URI handed to getCollection would look something like the following.
   * {@code xmldb:vendordb://host:port/path/to/collection}. The xmldb: prefix will be removed from
   * the URI prior to handing the URI to the {@code Database} instance for handling.
   *
   * This method is called when no authentication is necessary for the database.
   *
   * @param uri The database specific URI to use to locate the collection.
   * @return A {@code Collection} instance for the requested collection or null if the collection
   *         could not be found.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrroCodes.INVALID_URI} If the URI is not in
   *         a valid format. {@code ErrroCodes.NO_SUCH_DATABASE} If a {@code Database} instance
   *         could not be found to handle the provided URI.
   */
  public static Collection getCollection(final String uri) throws XMLDBException {
    return getCollection(uri, null, null);
  }

  /**
   * Retrieves a {@code Collection} instance from the database for the given URI. The format of the
   * majority of the URI is database implementation specific however the uri must begin with
   * characters xmldb: and be followed by the name of the database instance as returned by
   * {@code Database.getName()} and a colon character. An example would be for the database named
   * "vendordb" the URI handed to getCollection would look something like the following.
   * {@code xmldb:vendordb://host:port/path/to/collection}. The xmldb: prefix will be removed from
   * the URI prior to handing the URI to the {@code Database} instance for handling.
   *
   * @param uri The database specific URI to use to locate the collection.
   * @param username The username to use for authentication to the database or null if the database
   *        does not support authentication.
   * @param password The password to use for authentication to the database or null if the database
   *        does not support authentication.
   * @return A {@code Collection} instance for the requested collection or null if the collection
   *         could not be found.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrroCodes.INVALID_URI} If the URI is not in
   *         a valid format. {@code ErrroCodes.NO_SUCH_DATABASE} If a {@code Database} instance
   *         could not be found to handle the provided URI. {@code ErrroCodes.PERMISSION_DENIED} If
   *         the {@code username} and {@code password} were not accepted by the database.
   */
  public static Collection getCollection(final String uri, final String username,
      final String password) throws XMLDBException {
    final Database db = getDatabase(uri);
    return db.getCollection(stripURIPrefix(uri), username, password);
  }

  /**
   * Returns the Core Level conformance value for the provided URI. The current API defines valid
   * resuls of "0" or "1" as defined in the XML:DB API specification.
   *
   * @param uri The database specific URI to use to locate the collection.
   * @return The XML:DB Core Level conformance for the uri.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrroCodes.INVALID_URI} If the URI is not in
   *         a valid format. {@code ErrroCodes.NO_SUCH_DATABASE} If a {@code Database} instance
   *         could not be found to handle the provided URI.
   */
  public static String getConformanceLevel(final String uri) throws XMLDBException {
    final Database database = getDatabase(uri);
    return database.getConformanceLevel();
  }

  /**
   * Retrieves a property that has been set for the {@code DatabaseManager}.
   *
   * @param name The property name
   * @return The property value
   */
  public static String getProperty(final String name) {
    return properties.getProperty(name);
  }

  /**
   * Sets a property for the {@code DatabaseManager}.
   *
   * @param name The property name
   * @param value The value to set.
   */
  public static void setProperty(final String name, final String value) {
    properties.put(name, value);
  }

  /**
   * Retrieves the registered {@code Database} instance associated with the provided URI.
   *
   * @param uri The uri containing the database reference.
   * @return the requested {@code Database} instance.
   * @throws XMLDBException if an error occurs whilst getting the database
   */
  static Database getDatabase(final String uri) throws XMLDBException {
    final String databaseAndCollection = stripURIPrefix(uri); 

    final int end = databaseAndCollection.indexOf(":");
    if (end == -1) {
      throw new XMLDBException(INVALID_URI);
    }

    final String databaseName = databaseAndCollection.substring(0, end);

    // try optimistic read first
    long stamp = dbLock.tryOptimisticRead();
    if (stamp > 0) {
      final Database db = databases.get(databaseName);
      if (dbLock.validate(stamp)) {
        if (db == null) {
          throw new XMLDBException(NO_SUCH_DATABASE);
        }
        return db;
      }
    }

    // fallback to locking read
    final Database db;
    stamp = dbLock.readLock();
    try {
      db = databases.get(databaseName);
    } finally {
      dbLock.unlockRead(stamp);
    }

    if (db == null) {
      throw new XMLDBException(NO_SUCH_DATABASE);
    }

    return db;
  }

  /**
   * Removes the URI_PREFIX from the front of the URI. This is so the database can focus on handling
   * its own URIs.
   *
   * @param uri The full URI to strip.
   * @return The database specific portion of the URI.
   * @throws XMLDBException if an error occurs whilst stripping the URI prefix
   */
  static String stripURIPrefix(final String uri) throws XMLDBException {
    if (!uri.startsWith(URI_PREFIX)) {
      throw new XMLDBException(INVALID_URI);
    }

    return uri.substring(URI_PREFIX.length());
  }
}
