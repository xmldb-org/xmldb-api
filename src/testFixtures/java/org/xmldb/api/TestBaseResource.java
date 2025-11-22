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

import java.time.Instant;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;

/**
 * An abstract base class that implements the {@code Resource} interface and provides a foundational
 * implementation for managing resources within a collection.
 * <p>
 * This class encapsulates basic metadata about a resource, such as its identifier, creation time,
 * last modification time, and parent collection.
 *
 * @param <T> the type of content that this resource manages.
 */
public abstract class TestBaseResource<T> implements Resource<T> {
  private final String id;
  private final Collection parentCollection;
  private final Instant creation;

  private boolean closed;
  private Instant lastChange;

  /**
   * Constructs a protected instance of the {@code TestBaseResource} class with the provided
   * attributes, initializing the resource identifier, creation time, last modification time, and
   * associated parent collection.
   *
   * @param id the unique identifier for the resource.
   * @param creation the timestamp representing the creation time of the resource.
   * @param lastChange the timestamp of the last modification to the resource.
   * @param parentCollection the collection that this resource is a part of.
   */
  protected TestBaseResource(String id, Instant creation, Instant lastChange,
      Collection parentCollection) {
    this.id = id;
    this.creation = creation;
    this.lastChange = lastChange;
    this.parentCollection = parentCollection;
  }

  /**
   * Updates the last modification time of the resource to the current instant.
   * <p>
   * This method sets the {@code lastChange} property to the current system time, effectively
   * marking the resource as having been modified.
   */
  protected void updateLastChange() {
    lastChange = Instant.now();
  }

  @Override
  public Collection getParentCollection() {
    return parentCollection;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  @Override
  public void close() {
    closed = true;
  }

  @Override
  public Instant getCreationTime() {
    return creation;
  }

  @Override
  public Instant getLastModificationTime() {
    return lastChange;
  }
}
