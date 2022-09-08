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
package org.xmldb.api.base;

/**
 * A visitor of collections. An implementation of this interface is provided to the
 * {@link Collection#walkCollectionTree} methods to visit each resource in a collection tree.
 *
 * <p>
 * <b>Usage Examples:</b>
 * <p>
 * Suppose we want to delete a collection tree. In that case, each collection should be deleted
 * after the resources in the collection are deleted.
 * 
 * <pre>
 * collection.walkCollectionTree(new SimpleCollectionVisitor() {
 *   &#64;Override
 *   public CollectionVisitResult visitResource(Resource resource) throws XMLDBException {
 *     resource.getParentCollection().removeResource(resource);
 *     return CollectionVisitResult.CONTINUE;
 *   }
 * 
 *   &#64;Override
 *   public CollectionVisitResult postVisitCollection(Collection collection, XMLDBException e)
 *       throws XMLDBException {
 *     if (e == null) {
 *       String collectionName = collection.getName()
 *       resource.getParentCollection().getService(CollectionManagementService.class).removeCollection(collectionName);
 *       return CollectionVisitResult.CONTINUE;
 *     } else {
 *       // collection iteration failed
 *       throw e;
 *     }
 *   }
 * });
 * </pre>
 * <p>
 * Furthermore, suppose we want to copy a collection tree to a target location. In that case,
 * symbolic links should be followed and the target collection should be created before the
 * resources in the collection are copied.
 * 
 * <pre>
 * sourceColelction.walkCollectionTree(Integer.MAX_VALUE, new SimpleFileVisitor&lt;Path&gt;() {
 *   &#64;Override
 *   public CollectionVisitResult preVisitCollection(Collection collection) throws XMLDBException {
 *     Path targetcollection = target.resolve(source.relativize(collection));
 *     try {
 *       Files.copy(collection, targetcollection);
 *     } catch (FileAlreadyExistsException e) {
 *       if (!Files.isDirectory(targetcollection))
 *         throw e;
 *     }
 *     return CONTINUE;
 *   }
 * 
 *   &#64;Override
 *   public CollectionVisitResult visitFile(Resource resource) throws XMLDBException {
 *     Files.copy(resource, target.resolve(source.relativize(resource)));
 *     return CONTINUE;
 *   }
 * });
 * </pre>
 *
 * @since 2.0
 */
public interface CollectionVisitor {
  /**
   * Invoked for a collection before resources in the collection are visited.
   *
   * <p>
   * If this method returns {@link CollectionVisitResult#CONTINUE CONTINUE}, then resources in the
   * collection are visited. If this method returns {@link CollectionVisitResult#SKIP_SUBTREE
   * SKIP_SUBTREE} or {@link CollectionVisitResult#SKIP_SIBLINGS SKIP_SIBLINGS} then resources in
   * the collection (and any descendants) will not be visited.
   *
   * @param collection a reference to the collection
   *
   * @return the visit result
   *
   * @throws XMLDBException if an XML DB error occurs
   */
  Collection preVisitCollection(Collection collection) throws XMLDBException;

  /**
   * Invoked for a resource in a collection.
   *
   * @param resource a reference to the resource
   *
   * @return the visit result
   *
   * @throws XMLDBException if an XML DB error occurs
   */
  Collection visitResource(Resource resource) throws XMLDBException;

  /**
   * Invoked for a resource that could not be visited. This method is invoked if the resource's
   * attributes could not be read, the resource is a collection that could not be opened, and other
   * reasons.
   *
   * @param resource a reference to the resource
   * @param exc the XML DB exception that prevented the resource from being visited
   *
   * @return the visit result
   *
   * @throws XMLDBException if an XML DB error occurs
   */
  Collection visitResourceFailed(Resource resource, XMLDBException exc) throws XMLDBException;

  /**
   * Invoked for a collection after resources in the collection, and all of their descendants, have
   * been visited. This method is also invoked when iteration of the collection completes
   * prematurely (by a {@link #visitResource visitFile} method returning
   * {@link CollectionVisitResult#SKIP_SIBLINGS SKIP_SIBLINGS}, or an XML DB error when iterating
   * over the collection).
   *
   * @param collection a reference to the collection
   * @param exc {@code null} if the iteration of the collection completes without an error;
   *        otherwise the XML DB exception that caused the iteration of the collection to complete
   *        prematurely
   *
   * @return the visit result
   *
   * @throws XMLDBException if an XML DB error occurs
   */
  Collection postVisitCollection(Collection collection, XMLDBException exc) throws XMLDBException;

}
