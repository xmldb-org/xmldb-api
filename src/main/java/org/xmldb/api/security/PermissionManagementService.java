/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 *
 * Copyright (c) 2021, 2021 The XML:DB Initiative. All rights reserved.
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

package org.xmldb.api.security;

import java.util.Set;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;

/**
 * This service is providing permission related functions in order to get or
 * change permissions on {@link Collection} or {@link Resource} objects.
 * 
 * @see org.xmldb.api.base.Collection#getService
 *
 * @since 2.0
 */
public interface PermissionManagementService extends Service {

    public static final String SERVICE_NAME = "PermissionManagementService";

    /**
     * Returns an attribute view for the given collection.
     * 
     * @param collection the collection for getting the attributes
     * @return the basic permission attributes of the given collection
     * @throws XMLDBException if an error occurs whilst getting the attributes
     */
    Attributes getAttributes(Collection collection) throws XMLDBException;

    /**
     * Returns an attribute view for the given resource.
     * 
     * @param resource the resource for getting the attributes
     * @return the basic permission attributes of the given resource
     * @throws XMLDBException if an error occurs whilst getting the attributes
     */
    Attributes getAttributes(Resource resource) throws XMLDBException;

    /**
     * Returns a set of currently set permissions for the given collection.
     * 
     * @param collection the collection to get the permissions for
     * @return a set of the current permissions
     * @throws XMLDBException if an error occurs whilst getting the permissions
     */
    Set<Permission> getPermissions(Collection collection) throws XMLDBException;

    /**
     * Replaces the current permissions of the given collection with the given
     * new permission set.
     * 
     * @param collection the collection to replace the existing ones
     * @param perms the new permissions to be set on the collection
     * @throws XMLDBException if an error occurs whilst setting the permissions
     */
    void setPermissions(Collection collection, Set<Permission> perms) throws XMLDBException;

    /**
     * Returns a set of currently set permissions for the given resource.
     * 
     * @param resource the resource to get the permissions for
     * @return a set of the current permissions
     * @throws XMLDBException if an error occurs whilst getting the permissions
     */
    Set<Permission> getPermissions(Resource resource) throws XMLDBException;

    /**
     * Replaces the current permissions of the given resource with the given
     * new permission set.
     * 
     * @param resource the resource to replace the existing ones
     * @param perms the new permissions to be set on the resource
     * @throws XMLDBException if an error occurs whilst setting the permissions
     */
    void setPermissions(Resource resource, Set<Permission> perms) throws XMLDBException;

    /**
     * Returns the current owner of the given collection.
     * 
     * @param collection the collection to get the owner for
     * @return a user principal representing the owner of the collection
     * @throws XMLDBException if an error occurs whilst getting the owner
     */
    UserPrincipal getOwner(Collection collection) throws XMLDBException;

    /**
     * Sets the new owner of the given collection.
     * 
     * @param collection the collection to get the owner for
     * @param owner the user principal the new owner of the collection
     * @throws XMLDBException if an error occurs whilst setting the owner
     */
    void setOwner(Collection collection, UserPrincipal owner) throws XMLDBException;

    /**
     * Returns the current owner of the given resource.
     * 
     * @param resource the resource to get the owner for
     * @return a user principal representing the owner of the resource
     * @throws XMLDBException if an error occurs whilst getting the owner
     */
    UserPrincipal getOwner(Resource resource) throws XMLDBException;

    /**
     * Sets the new owner of the given resource.
     * 
     * @param resource the resource to get the owner for
     * @param owner the user principal the new owner of the resource
     * @throws XMLDBException if an error occurs whilst setting the owner
     */
    void setOwner(Resource resource, UserPrincipal owner) throws XMLDBException;

    /**
     * Returns the current group of the given collection.
     * 
     * @param collection the collection to get the owner for
     * @return a group principal representing the owner of the collection
     * @throws XMLDBException if an error occurs whilst getting the group
     */
    GroupPrincipal getGroup(Collection collection) throws XMLDBException;

    /**
     * Sets the new group of the given collection.
     * 
     * @param collection the collection to get the group for
     * @param group the group principal the new group of the collection
     * @throws XMLDBException if an error occurs whilst setting the group
     */
    void setGroup(Collection collection, GroupPrincipal group) throws XMLDBException;

    /**
     * Returns the current group of the given resource.
     * 
     * @param resource the resource to get the owner for
     * @return a group principal representing the owner of the resource
     * @throws XMLDBException if an error occurs whilst getting the group
     */
    GroupPrincipal getGroup(Resource resource) throws XMLDBException;

    /**
     * Sets the new group of the given resource.
     * 
     * @param resource the resource to get the group for
     * @param group the group principal the new group of the resource
     * @throws XMLDBException if an error occurs whilst setting the group
     */
    void setGroup(Resource resource, GroupPrincipal group) throws XMLDBException;
}
