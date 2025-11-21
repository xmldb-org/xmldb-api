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

import static org.xmldb.api.base.ErrorCodes.INVALID_RESOURCE;
import static org.xmldb.api.base.ErrorCodes.NOT_IMPLEMENTED;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.BinaryResource;
import org.xmldb.api.modules.XMLResource;

public class TestCollection extends ConfigurableImpl implements Collection {
  private final TestCollectionData data;
  private final ConcurrentMap<String, Collection> childCollections;
  private final ConcurrentMap<String, Resource> resources;

  private boolean closed;
  private Collection parent;

  public TestCollection(final TestCollectionData data) {
    this(data, null);
  }

  public TestCollection(final TestCollectionData data, final Collection parent) {
    this.data = data;
    this.parent = parent;
    resources = new ConcurrentHashMap<>();
    childCollections = new ConcurrentHashMap<>();
  }

  public static TestCollection create(String name) {
    return new TestCollection(new TestCollectionData(name));
  }

  public <T extends TestBaseResource> T addResource(String id,
      BiFunction<String, Collection, T> createAction) {
    T resource = createAction.apply(id, this);
    resources.put(resource.getId(), resource);
    return resource;
  }

  public void addCollection(String child, TestCollection childCollection) {
    childCollections.put(child, childCollection);
  }

  @Override
  public final String getName() throws XMLDBException {
    return data.name();
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
  public int getChildCollectionCount() {
    return childCollections.size();
  }

  @Override
  public List<String> listChildCollections() {
    return childCollections.keySet().stream().toList();
  }

  @Override
  public Collection getChildCollection(String collectionName) {
    return childCollections.get(collectionName);
  }

  @Override
  public Collection getParentCollection() {
    return parent;
  }

  @Override
  public int getResourceCount() {
    return resources.size();
  }

  @Override
  public List<String> listResources() {
    return resources.keySet().stream().toList();
  }

  @Override
  public <R extends Resource> R createResource(String id, Class<R> type) throws XMLDBException {
    if (BinaryResource.class.equals(type)) {
      return type.cast(new TestBinaryResource(id, this));
    } else if (XMLResource.class.equals(type)) {
      return type.cast(new TestXMLResource(id, this));
    }
    throw new XMLDBException(INVALID_RESOURCE);
  }

  @Override
  public void removeResource(Resource res) throws XMLDBException {
    resources.remove(res.getId());
  }

  @Override
  public void storeResource(Resource res) throws XMLDBException {
    resources.put(res.getId(), res);
  }

  @Override
  public Resource getResource(String id) {
    return resources.get(id);
  }

  @Override
  public String createId() throws XMLDBException {
    throw new XMLDBException(NOT_IMPLEMENTED);
  }

  @Override
  public boolean isOpen() {
    return !closed;
  }

  @Override
  public void close() {
    closed = true;
  }

  @Override
  public Instant getCreationTime() {
    return data.creation();
  }

  @Override
  public String toString() {
    return "/%s".formatted(data.name());
  }
}
