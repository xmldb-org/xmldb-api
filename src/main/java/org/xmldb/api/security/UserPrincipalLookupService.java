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

import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;

/**
 * An object to lookup user and group principals by name. A {@link UserPrincipal}
 * represents an identity that may be used to determine access rights to objects
 * in the XMLDB. A {@link GroupPrincipal} represents a <em>group identity</em>.
 * A {@code UserPrincipalLookupService} defines methods to lookup identities by
 * name or group name (which are typically user or account names). Whether names
 * and group names are case sensitive or not depends on the implementation.
 * The exact definition of a group is implementation specific but typically a
 * group represents an identity created for administrative purposes so as to
 * determine the access rights for the members of the group. In particular it is
 * implementation specific if the <em>namespace</em> for names and groups is the
 * same or is distinct. To ensure consistent and correct behavior across
 * platforms it is recommended that this API be used as if the namespaces are
 * distinct. In other words, the {@link #lookupPrincipalByName
 * lookupPrincipalByName} should be used to lookup users, and {@link
 * #lookupPrincipalByGroupName lookupPrincipalByGroupName} should be used to
 * lookup groups.
 *
 * @see org.xmldb.api.base.Collection#getService
 *
 * @since 2.0
 */
public interface UserPrincipalLookupService extends Service {

    public static final String SERVICE_NAME = "UserPrincipalLookupService";

    /**
     * Lookup a user principal by name.
     *
     * @param   name
     *          the string representation of the user principal to lookup
     *
     * @return  a user principal
     *
     * @throws  XMLDBException
     *          the principal does not exist
     */
    UserPrincipal lookupPrincipalByName(String name) throws XMLDBException;

    /**
     * Lookup a group principal by group name.
     *
     * <p> Where an implementation does not support any notion of group then
     * this method always throws {@link XMLDBException}. Where
     * the namespace for user accounts and groups is the same, then this method
     * is identical to invoking {@link #lookupPrincipalByName
     * lookupPrincipalByName}.
     *
     * @param   group
     *          the string representation of the group to lookup
     *
     * @return  a group principal
     *
     * @throws  XMLDBException
     *          the principal does not exist or is not a group
     */
    GroupPrincipal lookupPrincipalByGroupName(String group) throws XMLDBException;
}
