/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 * Copyright (c) 2000-2024 The XML:DB Initiative. All rights reserved.
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
package org.xmldb.api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.xmldb.api.security.AclEntryFlag.COLLECTION_INHERIT;
import static org.xmldb.api.security.AclEntryFlag.INHERIT_ONLY;
import static org.xmldb.api.security.AclEntryFlag.RESOURCE_INHERIT;
import static org.xmldb.api.security.AclEntryPermission.EXECUTE;
import static org.xmldb.api.security.AclEntryPermission.READ;
import static org.xmldb.api.security.AclEntryPermission.WRITE;
import static org.xmldb.api.security.AclEntryType.ALLOW;
import static org.xmldb.api.security.AclEntryType.DENY;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class AclEntryTest {
  @Mock
  UserPrincipal principal;

  AclEntry.Builder builder;

  @BeforeEach
  void prepare() {
    builder = AclEntry.newBuilder();
  }

  @Test
  void testBuildBasic() {
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(builder::build)
        .withMessage("Missing type component");
    assertThat(builder.setType(ALLOW)).isSameAs(builder);
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(builder::build)
        .withMessage("Missing principal component");
    assertThat(builder.setPrincipal(principal)).isSameAs(builder);

    assertThat(builder.build()).satisfies(aclEntry -> {
      assertThat(aclEntry.type()).isEqualTo(ALLOW);
      assertThat(aclEntry.principal()).isEqualTo(principal);
      assertThat(aclEntry.flags()).isEmpty();
      assertThat(aclEntry.permissions()).isEmpty();
    });
  }

  @Test
  void testBuildWithEmptyPermissionsSet() {
    builder.setType(DENY).setPrincipal(principal);
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> builder.setPermissions((Set<AclEntryPermission>) null));
    assertThat(builder.setPermissions(new HashSet<>())).isSameAs(builder);
    assertThat(builder.build()).satisfies(aclEntry -> {
      assertThat(aclEntry.type()).isEqualTo(DENY);
      assertThat(aclEntry.permissions()).isSameAs(Collections.emptySet());
    });
  }

  @Test
  void testBuildWithPermissionsSet() {
    Set<AclEntryPermission> perms = new HashSet<>();
    perms.add(READ);
    perms.add(WRITE);
    builder.setType(DENY).setPrincipal(principal).setPermissions(perms);
    assertThat(builder.build()).satisfies(aclEntry -> {
      assertThat(aclEntry.type()).isEqualTo(DENY);
      assertThat(aclEntry.permissions()).containsExactlyInAnyOrderElementsOf(perms)
          .isInstanceOf(EnumSet.class);
    });
  }

  @Test
  void testBuildWithPermissionsVarargs() {
    builder.setType(ALLOW).setPrincipal(principal).setPermissions(READ, EXECUTE);
    assertThat(builder.build()).satisfies(aclEntry -> {
      assertThat(aclEntry.type()).isEqualTo(ALLOW);
      assertThat(aclEntry.permissions()).containsExactlyInAnyOrder(READ, EXECUTE)
          .isInstanceOf(EnumSet.class);
    });
  }

  @Test
  void testBuildWithPermissionsModeString() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> builder.setPermissions((String) null))
        .withMessage("Invalid mode string 'null'");
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> builder.setPermissions("")).withMessage("Invalid mode string ''");
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> builder.setPermissions("xxxx")).withMessage("Invalid mode string 'xxxx'");
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> builder.setPermissions("a"))
        .withMessage("Unknown char 'a' in mode string 'a'");
    builder.setType(ALLOW).setPrincipal(principal).setPermissions("rw-");
    assertThat(builder.build()).satisfies(aclEntry -> {
      assertThat(aclEntry.type()).isEqualTo(ALLOW);
      assertThat(aclEntry.permissions()).containsExactlyInAnyOrder(READ, WRITE)
          .isInstanceOf(EnumSet.class);
    });
    builder.setType(ALLOW).setPrincipal(principal).setPermissions("r-x");
    assertThat(builder.build()).satisfies(aclEntry -> {
      assertThat(aclEntry.type()).isEqualTo(ALLOW);
      assertThat(aclEntry.permissions()).containsExactlyInAnyOrder(READ, EXECUTE)
          .isInstanceOf(EnumSet.class);
    });
  }

  @Test
  void testBuildWithEmpptyFlagsSet() {
    builder.setType(DENY).setPrincipal(principal);
    assertThat(builder.setFlags(new HashSet<>())).isSameAs(builder);
    assertThat(builder.build())
        .satisfies(aclEntry -> assertThat(aclEntry.flags()).isSameAs(Collections.emptySet()));
  }

  @Test
  void testBuildWithFlagsSet() {
    Set<AclEntryFlag> flags = new HashSet<>();
    flags.add(RESOURCE_INHERIT);
    flags.add(COLLECTION_INHERIT);
    builder.setType(DENY).setPrincipal(principal);
    assertThat(builder.setFlags(flags)).isSameAs(builder);
    assertThat(builder.build()).satisfies(aclEntry -> assertThat(aclEntry.flags())
        .containsExactlyInAnyOrderElementsOf(flags).isInstanceOf(EnumSet.class));
  }

  @Test
  void testBuildWithEmptyFlagsVarargs() {
    builder.setType(ALLOW).setPrincipal(principal);
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> builder.setFlags((AclEntryFlag) null));
    assertThat(builder.setFlags()).isSameAs(builder);
    assertThat(builder.build())
        .satisfies(aclEntry -> assertThat(aclEntry.flags()).isInstanceOf(EnumSet.class).isEmpty());
  }

  @Test
  void testBuildWithFlagsVarargs() {
    builder.setType(ALLOW).setPrincipal(principal).setFlags(RESOURCE_INHERIT, INHERIT_ONLY);
    assertThat(builder.build()).satisfies(aclEntry -> assertThat(aclEntry.flags())
        .containsExactlyInAnyOrder(RESOURCE_INHERIT, INHERIT_ONLY).isInstanceOf(EnumSet.class));
  }

  @Test
  void testToString() {
    AclEntry aclEntry = builder.setType(DENY).setPrincipal(principal).build();
    assertThat(aclEntry).hasToString("org.xmldb.api.security.AclEntry(principal)");
  }

  @Test
  void testBuilderFromExistingEntry() {
    final AclEntry aclEntry = builder.setType(ALLOW).setPrincipal(principal)
        .setPermissions(READ, EXECUTE).setFlags(RESOURCE_INHERIT, INHERIT_ONLY).build();
    assertThat(aclEntry).isNotNull();

    final UserPrincipal principal2 = mock(UserPrincipal.class);
    assertThat(AclEntry.newBuilder(aclEntry).setPrincipal(principal2).build())
        .satisfies(tested -> nonEquals(tested, aclEntry));
    assertThat(AclEntry.newBuilder(aclEntry).setType(DENY).build())
        .satisfies(tested -> nonEquals(tested, aclEntry));
    assertThat(AclEntry.newBuilder(aclEntry).setPermissions(READ).build())
        .satisfies(tested -> nonEquals(tested, aclEntry));
    assertThat(AclEntry.newBuilder(aclEntry).setFlags(INHERIT_ONLY).build())
        .satisfies(tested -> nonEquals(tested, aclEntry));

    assertThat(AclEntry.newBuilder(aclEntry).build()).satisfies(tested -> {
      assertThat(tested).isEqualTo(aclEntry);
      assertThat(tested).hasSameHashCodeAs(aclEntry);
    });
  }

  static void nonEquals(AclEntry tested, AclEntry aclEntry) {
    assertThat(tested).isNotEqualTo(aclEntry);
    assertThat(tested).doesNotHaveSameHashCodeAs(aclEntry);
  }
}
