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

import static org.xmldb.api.base.ErrorCodes.INSTANCE_NAME_ALREADY_REGISTERED;
import static org.xmldb.api.base.ErrorCodes.NO_SUCH_DATABASE;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.DatabaseAction;
import org.xmldb.api.base.XMLDBException;

/**
 * {@link DatabaseManager} is the entry point for the API and enables you to get the initial
 * {@link Collection} references necessary to do anything useful with the API.
 * {@link DatabaseManager} is intended to be provided as a concrete implementation in a particular
 * programming language. Individual language mappings should define the exact syntax and semantics
 * of its use.
 */
public final class DatabaseManager {
  /**
   * Defines the URI prefix declaring a XML database
   */
  public static final String URI_PREFIX = "xmldb:";

  private static final Map<String, String> properties = new ConcurrentHashMap<>();
  private static final CopyOnWriteArrayList<DatabaseInfo> registeredDatabases =
      new CopyOnWriteArrayList<>();

  private DatabaseManager() {}

  /**
   * Returns a set of all available {@link Database} implementations that have been registered with
   * this {@link DatabaseManager}.
   *
   * @return An array of {@link Database} instances. One for each {@link Database} registered with
   *         the {@link DatabaseManager}. If no {@link Database} instances exist then an empty set
   *         is returned.
   * @since 2.0
   */
  public static Set<Database> getDatabases() {
    return registeredDatabases.stream().map(DatabaseInfo::database).collect(Collectors.toSet());
  }

  /**
   * Registers a new {@link Database} implementation with the {@link DatabaseManager}. The provided
   * database will be registered without an associated action.
   *
   * @param database The {@link Database} instance to register.
   * @throws XMLDBException if the database instance is already registered or if an error occurs
   *         during the registration process.
   */
  public static void registerDatabase(final Database database) throws XMLDBException {
    registerDatabase(database, null);
  }

  /**
   * Registers a new {@link Database} implementation with the {@link DatabaseManager}, along with
   * the specified {@link DatabaseAction} to associate with it.
   *
   * @param database The database instance to register.
   * @param action The action to associate with the database upon registration.
   * @throws XMLDBException if the database instance is already registered or if an error occurs
   *         during the registration process.
   */
  public static void registerDatabase(final Database database, final DatabaseAction action)
      throws XMLDBException {
    if (!registeredDatabases.addIfAbsent(new DatabaseInfo(database, action))) {
      throw new XMLDBException(INSTANCE_NAME_ALREADY_REGISTERED);
    }
  }

  /**
   * Deregisters a {@link Database} implementation from the {@link DatabaseManager}. Once a
   * {@link Database} has been deregistered it can no longer be used to handle requests.
   *
   * @param database The {@link Database} instance to deregister.
   */
  public static void deregisterDatabase(final Database database) {
    registeredDatabases.removeIf(info -> {
      if (info.database.equals(database)) {
        info.deregister();
        return true;
      }
      return false;
    });
  }

  /**
   * Retrieves a {@link Collection} instance from the database for the given URI. The format of the
   * majority of the URI is database implementation specific however the uri must begin with
   * characters xmldb: and be followed by the name of the database instance as returned by
   * {@link Database#getName()} and a colon character. An example would be for the database named
   * "vendordb" the URI handed to getCollection would look something like the following.
   * {@code xmldb:vendordb://host:port/path/to/collection}.
   * <p>
   * This method is called when no authentication is necessary for the database.
   *
   * @param uri The database specific URI to use to locate the collection.
   * @return A {@link Collection} instance for the requested collection or {@code null} if the
   *         collection could not be found.
   * @throws XMLDBException with expected error codes.
   *         {@link org.xmldb.api.base.ErrorCodes#VENDOR_ERROR} for any vendor specific errors that
   *         occur. {@link org.xmldb.api.base.ErrorCodes#INVALID_URI} If the URI is not in a valid
   *         format. {@link org.xmldb.api.base.ErrorCodes#NO_SUCH_DATABASE} If a {@link Database}
   *         instance could not be found to handle the provided URI.
   */
  public static Collection getCollection(final String uri) throws XMLDBException {
    return getCollection(uri, new Properties());
  }

  /**
   * Retrieves a {@link Collection} instance from the database for the given URI. The format of the
   * majority of the URI is database implementation specific however the uri must begin with
   * characters xmldb: and be followed by the name of the database instance as returned by
   * {@link Database#getName()} and a colon character. An example would be for the database named
   * "vendordb" the URI handed to getCollection would look something like the following.
   * {@code xmldb:vendordb://host:port/path/to/collection}.
   *
   * @param uri The database specific URI to use to locate the collection.
   * @param user The username to use for authentication to the database or {@code null} if the
   *        database does not support authentication.
   * @param password The password to use for authentication to the database or {@code null} if the
   *        database does not support authentication.
   * @return A {@code Collection} instance for the requested collection or {@code null} if the
   *         collection could not be found.
   * @throws XMLDBException with expected error codes.
   *         {@link org.xmldb.api.base.ErrorCodes#VENDOR_ERROR} for any vendor specific errors that
   *         occur. {@link org.xmldb.api.base.ErrorCodes#INVALID_URI} If the URI is not in a valid
   *         format. {@link org.xmldb.api.base.ErrorCodes#NO_SUCH_DATABASE} If a {@link Database}
   *         instance could not be found to handle the provided URI.
   *         {@link org.xmldb.api.base.ErrorCodes#PERMISSION_DENIED} If the {@code username} and
   *         {@code password} were not accepted by the database.
   */
  public static Collection getCollection(final String uri, final String user, final String password)
      throws XMLDBException {
    Properties info = new Properties();
    if (user != null) {
      info.put("user", user);
    }
    if (password != null) {
      info.put("password", password);
    }
    return getCollection(uri, info);
  }

  /**
   * Retrieves a {@link Collection} instance from the database for the given URI. The format of the
   * majority of the URI is database implementation specific however the uri must begin with
   * characters xmldb: and be followed by the name of the database instance as returned by
   * {@link Database#getName()} and a colon character. An example would be for the database named
   * "vendordb" the URI handed to getCollection would look something like the following.
   * {@code xmldb:vendordb://host:port/path/to/collection}.
   *
   * @param uri The database specific URI to use to locate the collection.
   * @param info The database specific connection options
   * @return A {@code Collection} instance for the requested collection or {@code null} if the
   *         collection could not be found.
   * @throws XMLDBException with expected error codes.
   *         {@link org.xmldb.api.base.ErrorCodes#VENDOR_ERROR} for any vendor specific errors that
   *         occur. {@link org.xmldb.api.base.ErrorCodes#INVALID_URI} If the URI is not in a valid
   *         format. {@link org.xmldb.api.base.ErrorCodes#NO_SUCH_DATABASE} If a {@link Database}
   *         instance could not be found to handle the provided URI.
   *         {@link org.xmldb.api.base.ErrorCodes#PERMISSION_DENIED} If the {@code username} and
   *         {@code password} were not accepted by the database.
   * @since 3.0
   */
  public static Collection getCollection(final String uri, final Properties info)
      throws XMLDBException {
    return withDatabase(uri, database -> database.getCollection(uri, info));
  }

  /**
   * Returns the Core Level conformance value for the provided URI. The current API defines valid
   * results of "0" or "1" as defined in the XML:DB API specification.
   *
   * @param uri The database specific URI to use to locate the collection.
   * @return The XML:DB Core Level conformance for the uri.
   * @throws XMLDBException with expected error codes.
   *         {@link org.xmldb.api.base.ErrorCodes#VENDOR_ERROR} for any vendor specific errors that
   *         occur. {@link org.xmldb.api.base.ErrorCodes#INVALID_URI} If the URI is not in a valid
   *         format. {@link org.xmldb.api.base.ErrorCodes#NO_SUCH_DATABASE} If a {@link Database}
   *         instance could not be found to handle the provided URI.
   */
  public static String getConformanceLevel(final String uri) throws XMLDBException {
    return withDatabase(uri, Database::getConformanceLevel);
  }

  /**
   * Retrieves a property that has been set for the {@link DatabaseManager}.
   *
   * @param name The property name
   * @return The property value
   */
  public static String getProperty(final String name) {
    return properties.get(name);
  }

  /**
   * Sets a property for the {@link DatabaseManager}.
   *
   * @param name The property name
   * @param value The value to set.
   */
  public static void setProperty(final String name, final String value) {
    if (value == null) {
      properties.remove(name);
    } else {
      properties.put(name, value);
    }
  }

  /**
   * Retrieves the registered {@link Database} instance associated with the provided URI.
   *
   * @param uri The uri containing the database reference.
   * @return the requested {@link Database} instance.
   * @throws XMLDBException if an error occurs whilst getting the database
   */
  static <T> T withDatabase(final String uri, final DatabaseFunction<T> function)
      throws XMLDBException {
    // Walk through the loaded registeredDrivers attempting to make a connection.
    // Remember the first exception that gets raised, so we can re-throw it.
    XMLDBException reason = null;
    for (DatabaseInfo info : registeredDatabases) {
      if (info.acceptsURI(uri)) {
        try {
          return function.apply(info.database);
        } catch (XMLDBException ex) {
          if (reason == null) {
            reason = ex;
          }
        }
      }
    }
    if (reason != null) {
      throw reason;
    }
    throw new XMLDBException(NO_SUCH_DATABASE, "No matching database found for: " + uri);
  }

  @FunctionalInterface
  interface DatabaseFunction<T> {
    T apply(Database database) throws XMLDBException;
  }

  record DatabaseInfo(Database database, DatabaseAction action) {
    boolean acceptsURI(String uri) {
      return database.acceptsURI(uri);
    }

    void deregister() {
      if (action != null) {
        action.deregister();
      }
    }

    @Override
    public int hashCode() {
      return database.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof DatabaseInfo other) {
        return database.equals(other.database);
      }
      return false;
    }
  }
}
