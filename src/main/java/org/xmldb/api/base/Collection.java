/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 *
 * Copyright (c) 2000, 2019 The XML:DB Initiative. All rights reserved.
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
 *
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * XML:DB Initiative. For more information on the XML:DB Initiative, please see
 * <http://www.xmldb.org/>.
 */

package org.xmldb.api.base;

import java.util.Date;

/**
 * A {@code Collection} represents a collection of {@code Resource}s stored within an XML database.
 * An XML database MAY expose collections as a hierarchical set of parent and child collections.
 *
 * A {@code Collection} provides access to the {@code Resource}s stored by the {@code Collection}
 * and to {@code Service} instances that can operate against the {@code Collection} and the
 * {@code Resource}s stored within it. The {@code Service} mechanism provides the ability to extend
 * the functionality of a {@code Collection} in ways that allows optional functionality to be
 * enabled for the {@code Collection}.
 */
public interface Collection extends Configurable, AutoCloseable {
  /**
   * Returns the name associated with the Collection instance.
   *
   * @return the name of the object.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  String getName() throws XMLDBException;

  /**
   * Provides a list of all services known to the collection. If no services are known an empty list
   * is returned.
   *
   * @return An array of registered {@code Service} implementations.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.COLLECTION_CLOSED} if the
   *         {@code close} method has been called on the {@code Collection}
   */
  Service[] getServices() throws XMLDBException;

  /**
   * Returns a {@code Service} instance for the requested service name and version. If no
   * {@code Service} exists for those parameters a null value is returned.
   *
   * @param name Description of Parameter
   * @param version Description of Parameter
   * @return the Service instance or null if no Service could be found.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.COLLECTION_CLOSED} if the
   *         {@code close} method has been called on the {@code Collection}
   */
  Service getService(String name, String version) throws XMLDBException;

  /**
   * Returns the parent collection for this collection or null if no parent collection exists.
   *
   * @return the parent {@code Collection} instance.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.COLLECTION_CLOSED} if the
   *         {@code close} method has been called on the {@code Collection}
   */
  Collection getParentCollection() throws XMLDBException;

  /**
   * Returns the number of child collections under this {@code Collection} or 0 if no child
   * collections exist.
   *
   * @return the number of child collections.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.COLLECTION_CLOSED} if the
   *         {@code close} method has been called on the {@code Collection}
   */
  int getChildCollectionCount() throws XMLDBException;

  /**
   * Returns a list of collection names naming all child collections of the current collection. If
   * no child collections exist an empty list is returned.
   *
   * @return an array containing collection names for all child collections.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.COLLECTION_CLOSED} if the
   *         {@code close} method has been called on the {@code Collection}
   */
  String[] listChildCollections() throws XMLDBException;

  /**
   * Returns a {@code Collection} instance for the requested child collection if it exists.
   *
   * @param name the name of the child collection to retrieve.
   * @return the requested child collection or null if it couldn't be found.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.COLLECTION_CLOSED} if the
   *         {@code close} method has been called on the {@code Collection}
   */
  Collection getChildCollection(String name) throws XMLDBException;

  /**
   * Returns the number of resources currently stored in this collection or 0 if the collection is
   * empty.
   *
   * @return the number of resource in the collection.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.COLLECTION_CLOSED} if the
   *         {@code close} method has been called on the {@code Collection}
   */
  int getResourceCount() throws XMLDBException;

  /**
   * Returns a list of the ids for all resources stored in the collection.
   *
   * @return a string array containing the names for all {@code Resource}s in the collection.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.COLLECTION_CLOSED} if the
   *         {@code close} method has been called on the {@code Collection}
   */
  String[] listResources() throws XMLDBException;

  /**
   * Creates a new empty {@code Resource} with the provided id. The type of {@code Resource}
   * returned is determined by the {@code type} parameter. The XML:DB API currently defines
   * "XMLResource" and "BinaryResource" as valid resource types. The {@code id} provided must be
   * unique within the scope of the collection. If {@code id} is null or its value is empty then an
   * id is generated by calling {@code createId()}. The {@code Resource} created is not stored to
   * the database until {@code storeResource()} is called.
   *
   * @param id the unique id to associate with the created {@code Resource}.
   * @param type the {@code Resource} type to create.
   * @return an empty {@code Resource} instance.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.UNKNOWN_RESOURCE_TYPE} if the
   *         {@code type} parameter is not a known {@code Resource} type.
   *         {@code ErrorCodes.COLLECTION_CLOSED} if the {@code close} method has been called on the
   *         {@code Collection}
   */
  Resource createResource(String id, String type) throws XMLDBException;

  /**
   * Removes the {@code Resource} from the database.
   *
   * @param res the resource to remove.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.INVALID_RESOURCE} if the
   *         {@code Resource} is not valid. {@code ErrorCodes.NO_SUCH_RESOURCE} if the
   *         {@code Resource} is not known to this {@code Collection}.
   *         {@code ErrorCodes.COLLECTION_CLOSED} if the {@code close} method has been called on the
   *         {@code Collection}
   */
  void removeResource(Resource res) throws XMLDBException;

  /**
   * Stores the provided resource into the database. If the resource does not already exist it will
   * be created. If it does already exist it will be updated.
   *
   * @param res the resource to store in the database.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.INVALID_RESOURCE} if the
   *         {@code Resource} is not valid. {@code ErrorCodes.COLLECTION_CLOSED} if the
   *         {@code close} method has been called on the {@code Collection}
   */
  void storeResource(Resource res) throws XMLDBException;

  /**
   * Retrieves a {@code Resource} from the database. If the {@code Resource} could not be located a
   * null value will be returned.
   *
   * @param id the unique id for the requested resource.
   * @return The retrieved {@code Resource} instance.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.COLLECTION_CLOSED} if the
   *         {@code close} method has been called on the {@code Collection}
   */
  Resource getResource(String id) throws XMLDBException;

  /**
   * Creates a new unique ID within the context of the {@code Collection}
   *
   * @return the created id as a string.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur. {@code ErrorCodes.COLLECTION_CLOSED} if the
   *         {@code close} method has been called on the {@code Collection}
   */
  String createId() throws XMLDBException;

  /**
   * Returns true if the {@code Collection} is open false otherwise. Calling the {@code close}
   * method on {@code Collection} will result in {@code isOpen} returning false. It is not safe to
   * use {@code Collection} instances that have been closed.
   *
   * @return true if the {@code Collection} is open, false otherwise.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  boolean isOpen() throws XMLDBException;

  /**
   * Releases all resources consumed by the {@code Collection}. The {@code close} method must always
   * be called when use of a {@code Collection} is complete. It is not safe to use a
   * {@code Collection} after the {@code close} method has been called.
   *
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  @Override
  void close() throws XMLDBException;


  /**
   * Returns the time of creation of the collection.
   *
   * @return the creation date of the current collection
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  Date getCreationTime() throws XMLDBException;

}

