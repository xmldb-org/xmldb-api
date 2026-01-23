/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 * Copyright (c) 2000-2026 The XML:DB Initiative. All rights reserved.
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
package org.xmldb.api.base;

import java.util.Properties;

/**
 * The interface that every XMLDB base class must implement.
 * <P>
 * The {@code DatabaseManager} allows for multiple databases.
 *
 * <P>
 * Each XMLDB should supply a class that implements the {@linkplain Database} interface.
 *
 * <P>
 * The {@code DatabaseManager} will try to load as many drivers as it can find and then for any
 * given {@code Database} request, it will ask each {@code Database} in turn to try to connect to
 * the target URI.
 *
 * <P>
 * It is strongly recommended that each {@code Database} class should be small and standalone so
 * that the {@code Database} class can be loaded and queried without bringing in vast quantities of
 * supporting code.
 *
 * <P>
 * When a {@code Database} class is loaded, it should create an instance of itself and register it
 * with the {@code DatabaseManager}. This means that a user can load and register a driver by
 * calling:
 * <p>
 * {@code Class.forName("foo.bah.Database")}
 * <p>
 * A {@code Database} may create a {@linkplain DatabaseAction} implementation in order to receive
 * notifications when {@linkplain org.xmldb.api.DatabaseManager#deregisterDatabase} has been called.
 */
public interface Database extends Configurable {
  /**
   * Returns the name associated with the Database instance.
   *
   * @return the name of the object.
   * @throws XMLDBException with expected error codes. {@link ErrorCodes#VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  String getName() throws XMLDBException;

  /**
   * Attempts to make a connection to the given database URI and return its root {@code Collection}.
   * The {@code Database} should return "null" if it realizes it is the wrong kind of driver to
   * connect to the given URI. This will be common, as when the {@code DatabaseManager} is asked to
   * connect to a given URI it passes the URI to each loaded database in turn.
   * <p>
   * The driver should throw an {@code XMLDBException} if it is the right database to connect for
   * the given URI but has trouble connecting to the database.
   * <p>
   * The {@code Properties} argument can be used to pass arbitrary string tag/value pairs as
   * connection arguments. Normally at least "user" and "password" properties should be included in
   * the {@code Properties} object.
   * <p>
   * <B>Note:</B> If a property is specified as part of the {@code uri} and is also specified in the
   * {@code Properties} object, it is implementation-defined as to which value will take precedence.
   * For maximum portability, an application should only specify a property once.
   *
   * @param uri the URI of the database to which to connect and return the root collection
   * @param info a list of arbitrary string tag/value pairs as connection arguments. Normally at
   *        least a "user" and "password" property should be included.
   * @return a {@code Collection} object that represents a connection to the URI
   * @throws XMLDBException with expected error codes. {@link ErrorCodes#VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@link ErrorCodes#INVALID_URI} If the URI is not in
   *         a valid format. {@link ErrorCodes#PERMISSION_DENIED} If the {@code username} and
   *         {@code password} were not accepted by the database.
   * @since 3.0
   */
  Collection getCollection(String uri, Properties info) throws XMLDBException;

  /**
   * acceptsURI determines whether this {@link Database} implementation can handle the URI. It
   * should return {@code true} if the Database instance knows how to handle the URI and
   * {@code false} otherwise.
   *
   * @param uri the URI to check for.
   * @return {@code true} if the URI can be handled, {@code false} otherwise.
   */
  boolean acceptsURI(String uri);

  /**
   * Returns the XML:DB API Conformance level for the implementation. This can be used by client
   * programs to determine what functionality is available to them.
   *
   * @return the XML:DB API conformance level for this implementation.
   * @throws XMLDBException with expected error codes. {@link ErrorCodes#VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  String getConformanceLevel() throws XMLDBException;
}
