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

public interface AclEntry {

    /**
     * Returns the principal component.
     *
     * @return the principal of the ACL entry.
     */
    Principal principal();

    /**
     * Returns the ACL entry type.
     *
     * @return the type of the ACL entry.
     */
    AclEntryType type();

    /**
     * Returns a copy of the permissions component.
     *
     * @return the permissions of the ACL entry.
     */
    Set<AclEntryPermission> permissions();

    /**
     * Returns a copy of the flags component.
     *
     * @return the flags of the ACL entry.
     */
    Set<AclEntryFlag> flags();

    /**
     * Compares the specified object with this ACL entry for equality.
     * Returns true if the object passed in matches the ACL entry represented by the implementation of this interface.
     *
     * @param ob - the object to which this object is to be compared
     *
     * @return true if, and only if, the given object is an AclEntry that is identical to this AclEntry
     */
    @Override
    boolean equals(Object ob);

    /**
     * Returns the hash-code value for this ACL entry.
     *
     * @return a hash code value for this object.
     */
    @Override
    int hashCode();

    /**
     * Returns the string representation of this ACL entry.
     *
     * @return a string representation of the ACL entry.
     */
    @Override
    String toString();
}
