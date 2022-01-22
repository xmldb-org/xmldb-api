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
package org.xmldb.api.modules;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;

/**
 * CollectionManagementService is a {@code Service} that enables the basic management of collections
 * within a database. The functionality provided is very basic because collection management varies
 * widely among databases. This service simply provides functionality for those databases that are
 * able to implement this basic functionality.
 */
public interface CollectionManagementService extends Service {

  public static final String SERVICE_NAME = "CollectionManagementService";

  /**
   * Creates a new {@code Collection} in the database. The default configuration of the database is
   * determined by the implementer. The new {@code Collection} will be created relative to the
   * {@code Collection} from which the {@code CollectionManagementService} was retrieved.
   *
   * @param name The name of the collection to create.
   * @return The created {@code Collection} instance.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  Collection createCollection(String name) throws XMLDBException;

  /**
   * Removes a named {@code Collection} from the system. The name for the {@code Collection} to
   * remove is relative to the {@code Collection} from which the {@code CollectionManagementService}
   * was retrieved.
   *
   * @param name The name of the collection to remove.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  void removeCollection(String name) throws XMLDBException;

  /**
   * Moves either a {@code collection} or {@code }
   *
   * @param collection The source collection
   * @param destination The destination collection
   * @param newName The new name of the moved collection in the destination collection
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  void move(String collection, String destination, String newName) throws XMLDBException;

  /**
   * Moves the resource specified by the {@code resourcePath} to the given {@code destinationPath}
   * and {@code newName}.
   *
   * @param resourcePath The source document
   * @param destinationPath The destination collection
   * @param newName The new name of the moved source in the destination collection
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  void moveResource(String resourcePath, String destinationPath, String newName)
      throws XMLDBException;

  /**
   * Copy the resource specified by the {@code resourcePath} to the given {@code destinationPath}
   * and {@code newName}.
   *
   * @param resourcePath The source document
   * @param destinationPath The destination collection
   * @param newName The new name of the copied source in the destination collection
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  void copyResource(String resourcePath, String destinationPath, String newName)
      throws XMLDBException;

  /**
   * Copy the collection specified by {@code collection} to the given {@code destination} and
   * {@code newName}.
   *
   * @param collection The source collection
   * @param destination The destination collection
   * @param newName The new name of the copied collection in the destination collection
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  void copy(String collection, String destination, String newName) throws XMLDBException;
}

