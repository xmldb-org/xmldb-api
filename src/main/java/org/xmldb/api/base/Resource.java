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
package org.xmldb.api.base;

import java.io.OutputStream;
import java.time.Instant;

/**
 * {@code Resource} is a container for data stored within the database. Raw resources are not
 * particulary useful. It is necessary to have a resource implementation that provides handling for
 * a specific content type before anything useful can be done.
 */
public interface Resource extends AutoCloseable {

  /**
   * Returns the resource type for this Resource.
   *
   * XML:DB defined resource types are: XMLResource - all XML data stored in the database
   * BinaryResource - Binary blob data stored in the database
   *
   * @return the resource type for the Resource.
   */
  ResourceType getResourceType();

  /**
   * Returns the {@code Collection} instance that this resource is associated with. All resources
   * must exist within the context of a {@code collection}.
   *
   * @return the collection associated with the resource.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  Collection getParentCollection() throws XMLDBException;

  /**
   * Returns the unique id for this {@code Resource} or null if the {@code Resource} is anonymous.
   * The {@code Resource} will be anonymous if it is obtained as the result of a query.
   *
   * @return the id for the Resource or null if no id exists.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  String getId() throws XMLDBException;

  /**
   * Retrieves the content from the resource. The type of the content varies depending what type of
   * resource is being used.
   *
   * @return the content of the resource.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  Object getContent() throws XMLDBException;

  /**
   * Retrieves the content from the resource. The type of the content varies depending what type of
   * resource is being used.
   *
   * @param stream the output stream to write the resource content to
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  void getContentAsStream(OutputStream stream) throws XMLDBException;

  /**
   * Sets the content for this resource. The type of content that can be set depends on the type of
   * resource being used.
   *
   * @param value the content value to set for the resource.
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  void setContent(Object value) throws XMLDBException;

  /**
   * Returns whenever the current resource has been closed or not.
   * 
   * @return {@code true} when the resource has been closed, {@code false} otherwise.
   */
  boolean isClosed();

  /**
   * Releases all resources consumed by the {@code Resource}. The {@code close} method must always
   * be called when use of a {@code Resource} is complete. It is not safe to use a {@code Resource}
   * after the {@code close} method has been called.
   *
   * @throws XMLDBException with expected error codes. {@link ErrorCodes#VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  @Override
  void close() throws XMLDBException;

  /**
   * Returns the time of creation of the resource.
   *
   * @return the creation date of the current resource
   * @throws XMLDBException with expected error codes. {@link ErrorCodes#VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  Instant getCreationTime() throws XMLDBException;

  /**
   * Returns the time of last modification of the resource.
   *
   * @return the last modification date of the current resource
   * @throws XMLDBException with expected error codes. {@link ErrorCodes#VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  Instant getLastModificationTime() throws XMLDBException;
}
