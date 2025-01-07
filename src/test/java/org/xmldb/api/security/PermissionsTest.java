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
package org.xmldb.api.security;

import static org.assertj.core.api.Assertions.*;
import static org.xmldb.api.security.Permission.GROUP_EXECUTE;
import static org.xmldb.api.security.Permission.GROUP_READ;
import static org.xmldb.api.security.Permission.GROUP_WRITE;
import static org.xmldb.api.security.Permission.OTHERS_EXECUTE;
import static org.xmldb.api.security.Permission.OTHERS_READ;
import static org.xmldb.api.security.Permission.OTHERS_WRITE;
import static org.xmldb.api.security.Permission.OWNER_EXECUTE;
import static org.xmldb.api.security.Permission.OWNER_READ;
import static org.xmldb.api.security.Permission.OWNER_WRITE;
import static org.xmldb.api.security.Permission.SET_GID;
import static org.xmldb.api.security.Permission.SET_UID;
import static org.xmldb.api.security.Permission.STICKY_BIT;
import static org.xmldb.api.security.Permissions.fromModeString;
import static org.xmldb.api.security.Permissions.fromOctal;

import org.junit.jupiter.api.Test;

class PermissionsTest {

  @Test
  void fromModeUnixSymbolicAddAll() {
    assertThat(fromModeString("a+rwxst")).containsExactly(Permission.values());
    assertThat(fromModeString("+rwxst")).containsExactly(Permission.values());
    assertThat(fromModeString("+r")).containsExactly(OWNER_READ, GROUP_READ, OTHERS_READ);
    assertThat(fromModeString("+w")).containsExactly(OWNER_WRITE, GROUP_WRITE, OTHERS_WRITE);
    assertThat(fromModeString("+x")).containsExactly(OWNER_EXECUTE, GROUP_EXECUTE, OTHERS_EXECUTE);
  }

  @Test
  void fromModeUnixSymbolicSetAll() {
    assertThat(fromModeString("a=rwxst")).containsExactly(Permission.values());
    assertThat(fromModeString("=rwxst")).containsExactly(Permission.values());
    assertThat(fromModeString("=s")).containsExactly(SET_UID, SET_GID);
    assertThat(fromModeString("=t")).containsExactly(STICKY_BIT);
  }

  @Test
  void fromModeUnixSymbolicRemoveAll() {
    assertThat(fromModeString("a-rwxst")).isEmpty();
    assertThat(fromModeString("-rwxst")).isEmpty();
  }

  @Test
  void fromModeUnixSymbolicUser() {
    assertThat(fromModeString("u=rwx")).containsExactly(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE);
    assertThat(fromModeString("u+st")).containsExactly(SET_UID);
    assertThat(fromModeString("u-r")).isEmpty();
  }

  @Test
  void fromModeUnixSymbolicGroup() {
    assertThat(fromModeString("g+rwx")).containsExactly(GROUP_READ, GROUP_WRITE, GROUP_EXECUTE);
    assertThat(fromModeString("g=st")).containsExactly(SET_GID);
    assertThat(fromModeString("g-w")).isEmpty();
  }

  @Test
  void fromModeUnixSymbolicOthers() {
    assertThat(fromModeString("o=rwx")).containsExactly(OTHERS_READ, OTHERS_WRITE, OTHERS_EXECUTE);
    assertThat(fromModeString("o+st")).containsExactly(STICKY_BIT);
    assertThat(fromModeString("o-x")).isEmpty();
  }

  @Test
  void fromModeUnixSymbolicUnkownMode() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> fromModeString("a=T"));
  }

  @Test
  void fromModeSimpleSymbolicNone() {
    assertThat(fromModeString("---------")).isEmpty();
  }

  @Test
  void fromModeSimpleSymbolicUser() {
    assertThat(fromModeString("rwx------")).containsExactly(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE);
    assertThat(fromModeString("--S------")).containsExactly(SET_UID);
    assertThat(fromModeString("--s------")).containsExactly(OWNER_EXECUTE, SET_UID);
  }

  @Test
  void fromModeSimpleSymbolicGroup() {
    assertThat(fromModeString("---rwx---")).containsExactly(GROUP_READ, GROUP_WRITE, GROUP_EXECUTE);
    assertThat(fromModeString("-----S---")).containsExactly(SET_GID);
    assertThat(fromModeString("-----s---")).containsExactly(GROUP_EXECUTE, SET_GID);
  }

  @Test
  void fromModeSimpleSymbolicOthers() {
    assertThat(fromModeString("------rwx")).containsExactly(OTHERS_READ, OTHERS_WRITE,
        OTHERS_EXECUTE);
    assertThat(fromModeString("--------T")).containsExactly(STICKY_BIT);
    assertThat(fromModeString("--------t")).containsExactly(OTHERS_EXECUTE, STICKY_BIT);
  }

  @Test
  void fromModeStringFailureCases() {
    assertThatIllegalArgumentException().isThrownBy(() -> fromModeString("u+q"));
  }

  @Test
  void fromOctalMode() {
    assertThat(fromOctal(0700)).containsExactly(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE);
    assertThat(fromOctal(04000)).containsExactly(SET_UID);
    assertThat(fromOctal(04100)).containsExactly(OWNER_EXECUTE, SET_UID);

    assertThat(fromOctal(0070)).containsExactly(GROUP_READ, GROUP_WRITE, GROUP_EXECUTE);
    assertThat(fromOctal(02000)).containsExactly(SET_GID);
    assertThat(fromOctal(02010)).containsExactly(GROUP_EXECUTE, SET_GID);

    assertThat(fromOctal(0007)).containsExactly(OTHERS_READ, OTHERS_WRITE, OTHERS_EXECUTE);
    assertThat(fromOctal(01000)).containsExactly(STICKY_BIT);
    assertThat(fromOctal(01001)).containsExactly(OTHERS_EXECUTE, STICKY_BIT);
  }
}
