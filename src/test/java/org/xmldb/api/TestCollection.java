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

import static java.util.Collections.emptyList;
import static org.xmldb.api.base.ErrorCodes.NOT_IMPLEMENTED;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;

public class TestCollection extends ConfigurableImpl implements Collection {
  private final String name;
  private final Collection parent;
  private final Instant creation;

  private boolean closed;

  public TestCollection(String name) {
    this(name, null);
  }

  public TestCollection(String name, Collection parent) {
    this.name = name;
    this.parent = parent;
    creation = Instant.now();
  }

  @Override
  public final String getName() throws XMLDBException {
    return name;
  }

  @Override
  public <S extends Service> boolean hasService(Class<S> serviceType) {
    return false;
  }

  @Override
  public <S extends Service> Optional<S> findService(Class<S> serviceType) {
    return Optional.empty();
  }

  @Override
  public <S extends Service> S getService(Class<S> serviceType) throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public Collection getParentCollection() throws XMLDBException {
    return parent;
  }

  @Override
  public int getChildCollectionCount() throws XMLDBException {
    return 0;
  }

  @Override
  public List<String> listChildCollections() throws XMLDBException {
    return emptyList();
  }

  @Override
  public Collection getChildCollection(String collectionName) throws XMLDBException {
    return null;
  }

  @Override
  public int getResourceCount() throws XMLDBException {
    return 0;
  }

  @Override
  public List<String> listResources() throws XMLDBException {
    return emptyList();
  }

  @Override
  public <R extends Resource> R createResource(String id, Class<R> type) throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public void removeResource(Resource res) throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public void storeResource(Resource res) throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public Resource getResource(String id) throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public String createId() throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public boolean isOpen() throws XMLDBException {
    return !closed;
  }

  @Override
  public void close() throws XMLDBException {
    closed = true;
  }

  @Override
  public Instant getCreationTime() throws XMLDBException {
    return creation;
  }
}
