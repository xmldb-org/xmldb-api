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
package org.xmldb.api.security;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * @since 2.0
 */
public final class AclEntry {
    private final UserPrincipal principal;
    private final AclEntryType type;
    private final Set<AclEntryPermission> permissions;
    private final Set<AclEntryFlag> flags;
    // cached hash code
    private volatile int hash;

    private AclEntry(AclEntryType type, UserPrincipal principal,
            Set<AclEntryPermission> permissions, Set<AclEntryFlag> flags) {
        this.type = type;
        this.principal = principal;
        this.permissions = permissions;
        this.flags = flags;
    }

    /**
     * A builder of {@link AclEntry} objects.
     *
     * <p> A {@code Builder} object is obtained by invoking one of the {@link
     * AclEntry#newBuilder newBuilder} methods defined by the {@code AclEntry}
     * class.
     *
     * <p> Builder objects are mutable and are not safe for use by multiple
     * concurrent threads without appropriate synchronization.
     *
     * @since 2.0
     */
    public static final class Builder {
        private AclEntryType type;
        private UserPrincipal principal;
        private Set<AclEntryPermission> permissions;
        private Set<AclEntryFlag> flags;

        private Builder(AclEntryType type, UserPrincipal principal,
                Set<AclEntryPermission> permissions, Set<AclEntryFlag> flags) {
            assert permissions != null && flags != null;
            this.type = type;
            this.principal = principal;
            this.permissions = permissions;
            this.flags = flags;
        }

        /**
         * Constructs an {@link AclEntry} from the components of this builder.
         * The type and principal components are required to have been set in order
         * to construct an {@code AclEntry}.
         *
         * @return  a new ACL entry
         *
         * @throws  IllegalStateException
         *          if the type or principal component have not been set
         */
        public AclEntry build() {
            if (type == null)
                throw new IllegalStateException("Missing type component");
            if (principal == null)
                throw new IllegalStateException("Missing principal component");
            return new AclEntry(type, principal, permissions, flags);
        }

        /**
         * Sets the type component of this builder.
         *
         * @param   type  the component type
         * @return  this builder
         */
        public Builder setType(AclEntryType type) {
            if (type == null)
                throw new NullPointerException();
            this.type = type;
            return this;
        }

        /**
         * Sets the principal component of this builder.
         *
         * @param   principal the principal component
         * @return  this builder
         */
        public Builder setPrincipal(UserPrincipal principal) {
            if (principal == null)
                throw new NullPointerException();
            this.principal = principal;
            return this;
        }

        // check set only contains elements of the given type
        private static void checkSet(Set<?> set, Class<?> type) {
            for (Object e: set) {
                if (e == null)
                    throw new NullPointerException();
                type.cast(e);
            }
        }

        /**
         * Sets the permissions component of this builder. On return, the
         * permissions component of this builder is a copy of the given set.
         *
         * @param   perms  the permissions component
         * @return  this builder
         *
         * @throws  ClassCastException
         *          if the set contains elements that are not of type {@code
         *          AclEntryPermission}
         */
        public Builder setPermissions(Set<AclEntryPermission> perms) {
            if (perms.isEmpty()) {
                // EnumSet.copyOf does not allow empty set
                perms = Collections.emptySet();
            } else {
                // copy and check for erroneous elements
                perms = EnumSet.copyOf(perms);
                checkSet(perms, AclEntryPermission.class);
            }

            this.permissions = perms;
            return this;
        }

        /**
         * Sets the permissions component of this builder. On return, the
         * permissions component of this builder is a copy of the permissions in
         * the given array.
         *
         * @param   perms  the permissions component
         * @return  this builder
         */
        public Builder setPermissions(AclEntryPermission... perms) {
            Set<AclEntryPermission> set = EnumSet.noneOf(AclEntryPermission.class);
            // copy and check for null elements
            for (AclEntryPermission permission: perms) {
                set.add(requireNonNull(permission));
            }
            this.permissions = set;
            return this;
        }

        /**
         * Sets the flags component of this builder. On return, the flags
         * component of this builder is a copy of the given set.
         *
         * @param   flags  the flags component
         * @return  this builder
         *
         * @throws  ClassCastException
         *          if the set contains elements that are not of type {@code
         *          AclEntryFlag}
         */
        public Builder setFlags(Set<AclEntryFlag> flags) {
            if (flags.isEmpty()) {
                // EnumSet.copyOf does not allow empty set
                flags = Collections.emptySet();
            } else {
                // copy and check for erroneous elements
                flags = EnumSet.copyOf(flags);
                checkSet(flags, AclEntryFlag.class);
            }

            this.flags = flags;
            return this;
        }

        /**
         * Sets the flags component of this builder. On return, the flags
         * component of this builder is a copy of the flags in the given
         * array.
         *
         * @param   flags  the flags component
         * @return  this builder
         */
        public Builder setFlags(AclEntryFlag... flags) {
            Set<AclEntryFlag> set = EnumSet.noneOf(AclEntryFlag.class);
            // copy and check for null elements
            for (AclEntryFlag flag: flags) {
                set.add(requireNonNull(flag));
            }
            this.flags = set;
            return this;
        }
    }

    /**
     * Constructs a new builder. The initial value of the type and principal
     * components is {@code null}. The initial value of the permissions and
     * flags components is the empty set.
     *
     * @return  a new builder
     */
    public static Builder newBuilder() {
        Set<AclEntryPermission> perms = Collections.emptySet();
        Set<AclEntryFlag> flags = Collections.emptySet();
        return new Builder(null, null, perms, flags);
    }

    /**
     * Constructs a new builder with the components of an existing ACL entry.
     *
     * @param   entry  an ACL entry
     * @return  a new builder
     */
    public static Builder newBuilder(AclEntry entry) {
        return new Builder(entry.type, entry.principal, entry.permissions, entry.flags);
    }

    /**
     * Returns the principal component.
     *
     * @return the principal of the ACL entry.
     */
    public AccountPrincipal principal() {
        return principal;
    }

    /**
     * Returns the ACL entry type.
     *
     * @return the type of the ACL entry.
     */
    public AclEntryType type() {
        return type;
    }

    /**
     * Returns a copy of the permissions component.
     *
     * @return the permissions of the ACL entry.
     */
    public Set<AclEntryPermission> permissions() {
        return permissions;
    }

    /**
     * Returns a copy of the flags component.
     *
     * @return the flags of the ACL entry.
     */
    public Set<AclEntryFlag> flags() {
        return flags;
    }

    /**
     * Compares the specified object with this ACL entry for equality.
     * Returns true if the object passed in matches the ACL entry represented by the implementation of this interface.
     *
     * @param obj - the object to which this object is to be compared
     *
     * @return true if, and only if, the given object is an AclEntry that is identical to this AclEntry
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AclEntry) {
            AclEntry other = (AclEntry)obj;
            return principal.equals(other.principal)
                    && type.equals(other.type)
                    && permissions.equals(other.permissions)
                    && flags.equals(other.flags);
        }
        return false;
    }

    /**
     * Returns the hash-code value for this ACL entry.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        // return cached hash if available
        if (hash != 0) {
            return hash;
        }
        hash = hash(type, principal, permissions, flags);
        return hash;
    }

    /**
     * Returns the string representation of this ACL entry.
     *
     * @return a string representation of the ACL entry.
     */
    @Override
    public String toString() {
        return null;
    }
}
