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

import static java.lang.Boolean.TRUE;
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

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for {@link Permission} values.
 * 
 * This class provides static methods to parse string and integer based permission modes into a set
 * of permissions.
 * 
 * @since 2.0
 */
public final class Permissions {
  private static final byte READ = 04;
  private static final byte WRITE = 02;
  private static final byte EXECUTE = 01;

  private static final char SETID_CHAR = 's';
  private static final char SETID_CHAR_NO_EXEC = 'S';
  private static final char STICKY_CHAR = 't';
  private static final char STICKY_CHAR_NO_EXEC = 'T';
  private static final char READ_CHAR = 'r';
  private static final char WRITE_CHAR = 'w';
  private static final char EXECUTE_CHAR = 'x';
  private static final char UNSET_CHAR = '-';
  private static final char ALL_CHAR = 'a';
  private static final char USER_CHAR = 'u';
  private static final char GROUP_CHAR = 'g';
  private static final char OTHER_CHAR = 'o';

  public static final Pattern UNIX_SYMBOLIC_MODE_PATTERN =
      Pattern.compile("((?:[augo]*(?:[+\\-=](?:[" + READ_CHAR + SETID_CHAR + STICKY_CHAR
          + WRITE_CHAR + EXECUTE_CHAR + "])+)+),?)+");
  public static final Pattern SIMPLE_SYMBOLIC_MODE_PATTERN = Pattern
      .compile("(?:(?:" + READ_CHAR + "|" + UNSET_CHAR + ")(?:" + WRITE_CHAR + "|" + UNSET_CHAR
          + ")(?:[" + EXECUTE_CHAR + SETID_CHAR + SETID_CHAR_NO_EXEC + "]|" + UNSET_CHAR
          + ")){2}(?:" + READ_CHAR + "|" + UNSET_CHAR + ")(?:" + WRITE_CHAR + "|" + UNSET_CHAR
          + ")(?:[" + EXECUTE_CHAR + STICKY_CHAR + STICKY_CHAR_NO_EXEC + "]|" + UNSET_CHAR + ")");

  private Permissions() {}

  private enum PermType {
    READ, WRITE, EXECUTE, SETID, STICKY;
  }

  /**
   * Parses the permissions from the given mode string.
   * 
   * The string can either be in one of two formats:
   * <ol>
   * <li>Unix Symbolic format as given to 'chmod' on Unix/Linux</li>
   * <li>Simple Symbolic format e.g. "rwxr-xr-x"</li>
   * </ol>
   * 
   * @param modeStr the mode to be parsed
   * @return the set of parsed permissions
   * @throws IllegalArgumentException if the given mode string is in the wrong format
   */
  public static Set<Permission> fromModeString(final String modeStr) {
    final Matcher simpleSymbolicModeMatcher = SIMPLE_SYMBOLIC_MODE_PATTERN.matcher(modeStr);
    final EnumSet<Permission> permissions = EnumSet.noneOf(Permission.class);
    if (simpleSymbolicModeMatcher.matches()) {
      setSimpleSymbolicMode(permissions, modeStr);
    } else {
      final Matcher unixSymbolicModeMatcher = UNIX_SYMBOLIC_MODE_PATTERN.matcher(modeStr);
      if (unixSymbolicModeMatcher.matches()) {
        setUnixSymbolicMode(permissions, modeStr);
      } else {
        throw new IllegalArgumentException("Unknown mode String: " + modeStr);
      }
    }
    return permissions;
  }


  /**
   * Parses the permissions from the given octal mode value.
   * 
   * @param mode the octal permission mode
   * @return the set of parsed permissions
   */
  public static Set<Permission> fromOctal(int mode) {
    final EnumSet<Permission> permissions = EnumSet.noneOf(Permission.class);
    // user operations
    if ((mode & (READ << 6)) != 0) {
      permissions.add(OWNER_READ);
    }
    if ((mode & (WRITE << 6)) != 0) {
      permissions.add(OWNER_WRITE);
    }
    if ((mode & (EXECUTE << 6)) != 0) {
      permissions.add(OWNER_EXECUTE);
    }
    if ((mode & (1 << 11)) != 0) {
      permissions.add(SET_UID);
    }
    // group operations
    if ((mode & (READ << 3)) != 0) {
      permissions.add(GROUP_READ);
    }
    if ((mode & (WRITE << 3)) != 0) {
      permissions.add(GROUP_WRITE);
    }
    if ((mode & (EXECUTE << 3)) != 0) {
      permissions.add(GROUP_EXECUTE);
    }
    if ((mode & (1 << 10)) != 0) {
      permissions.add(SET_GID);
    }

    // others operations
    if ((mode & READ) != 0) {
      permissions.add(OTHERS_READ);
    }
    if ((mode & WRITE) != 0) {
      permissions.add(OTHERS_WRITE);
    }
    if ((mode & EXECUTE) != 0) {
      permissions.add(OTHERS_EXECUTE);
    }
    if ((mode & (1 << 9)) != 0) {
      permissions.add(STICKY_BIT);
    }
    return permissions;
  }

  @SuppressWarnings("StringSplitter")
  private static void setUnixSymbolicMode(final EnumSet<Permission> permissions,
      final String symbolicMode) {
    for (final String clause : symbolicMode.split(",")) {
      final String[] whoPerm = clause.split("[+\\-=]");
      final EnumMap<PermType, Boolean> perms = new EnumMap<>(PermType.class);
      // process the operation first
      parseOperation(whoPerm, perms);

      final char[] whoose;
      if (!whoPerm[0].isEmpty()) {
        whoose = whoPerm[0].toCharArray();
      } else {
        whoose = new char[] {ALL_CHAR};
      }

      for (final char c : whoose) {
        switch (c) {
          case ALL_CHAR -> handleAllChar(permissions, clause, perms);
          case USER_CHAR -> handleUserChar(permissions, clause, perms);
          case GROUP_CHAR -> handleGroupChar(permissions, clause, perms);
          case OTHER_CHAR -> handleOtherChar(permissions, clause, perms);
          default -> throw new IllegalArgumentException("Unrecognised mode char '" + c + "'");
        }
      }
      perms.clear();
    }
  }

  private static void handleOtherChar(final EnumSet<Permission> permissions, final String clause,
      final EnumMap<PermType, Boolean> perms) {
    if (clause.indexOf('+') > -1 || clause.indexOf('=') > -1) {
      setPermissions(permissions, perms, OTHERS_READ, OTHERS_WRITE, OTHERS_EXECUTE);
      if (perms.containsKey(PermType.STICKY)) {
        permissions.add(STICKY_BIT);
      }
    }
  }

  private static void handleGroupChar(final EnumSet<Permission> permissions, final String clause,
      final EnumMap<PermType, Boolean> perms) {
    if (clause.indexOf('+') > -1 || clause.indexOf('=') > -1) {
      setPermissions(permissions, perms, GROUP_READ, GROUP_WRITE, GROUP_EXECUTE);
      if (perms.containsKey(PermType.SETID)) {
        permissions.add(SET_GID);
      }
    }
  }

  private static void handleUserChar(final EnumSet<Permission> permissions, final String clause,
      final EnumMap<PermType, Boolean> perms) {
    if (clause.indexOf('+') > -1 || clause.indexOf('=') > -1) {
      setPermissions(permissions, perms, OWNER_READ, OWNER_WRITE, OWNER_EXECUTE);
      if (perms.containsKey(PermType.SETID)) {
        permissions.add(SET_UID);
      }
    }
  }

  private static void handleAllChar(final EnumSet<Permission> permissions, final String clause,
      final EnumMap<PermType, Boolean> perms) {
    if (clause.indexOf('+') > -1 || clause.indexOf('=') > -1) {
      if (perms.containsKey(PermType.READ)) {
        permissions.add(OWNER_READ);
        permissions.add(GROUP_READ);
        permissions.add(OTHERS_READ);
      }
      if (perms.containsKey(PermType.WRITE)) {
        permissions.add(OWNER_WRITE);
        permissions.add(GROUP_WRITE);
        permissions.add(OTHERS_WRITE);
      }
      if (perms.containsKey(PermType.EXECUTE)) {
        permissions.add(OWNER_EXECUTE);
        permissions.add(GROUP_EXECUTE);
        permissions.add(OTHERS_EXECUTE);
      }
      if (perms.containsKey(PermType.SETID)) {
        permissions.add(SET_UID);
        permissions.add(SET_GID);
      }
      if (perms.containsKey(PermType.STICKY)) {
        permissions.add(STICKY_BIT);
      }
    }
  }

  private static void parseOperation(final String[] whoPerm,
      final EnumMap<PermType, Boolean> perms) {
    for (final char c : whoPerm[1].toCharArray()) {
      switch (c) {
        case READ_CHAR -> perms.put(PermType.READ, TRUE);
        case WRITE_CHAR -> perms.put(PermType.WRITE, TRUE);
        case EXECUTE_CHAR -> perms.put(PermType.EXECUTE, TRUE);
        case SETID_CHAR -> perms.put(PermType.SETID, TRUE);
        case STICKY_CHAR -> perms.put(PermType.STICKY, TRUE);
        default -> throw new IllegalArgumentException("Unrecognised mode char '" + c + "'");
      }
    }
  }

  private static void setPermissions(final EnumSet<Permission> permissions,
      final EnumMap<PermType, Boolean> perms, final Permission read, final Permission write,
      final Permission execute) {
    if (perms.containsKey(PermType.READ)) {
      permissions.add(read);
    }
    if (perms.containsKey(PermType.WRITE)) {
      permissions.add(write);
    }
    if (perms.containsKey(PermType.EXECUTE)) {
      permissions.add(execute);
    }
  }

  private static void setSimpleSymbolicMode(final EnumSet<Permission> permissions,
      final String simpleModeStr) {
    final char[] modeArray = simpleModeStr.toCharArray();
    for (int index = 0; index < modeArray.length; index++) {
      final char c = modeArray[index];
      switch (c) {
        case READ_CHAR -> selectPermission(permissions, index, OWNER_READ, GROUP_READ, OTHERS_READ);
        case WRITE_CHAR ->
          selectPermission(permissions, index - 1, OWNER_WRITE, GROUP_WRITE, OTHERS_WRITE);
        case EXECUTE_CHAR ->
          selectPermission(permissions, index - 2, OWNER_EXECUTE, GROUP_EXECUTE, OTHERS_EXECUTE);
        case SETID_CHAR_NO_EXEC -> {
          if (index < 3) {
            permissions.add(SET_UID);
          } else {
            permissions.add(SET_GID);
          }
        }
        case SETID_CHAR -> {
          if (index < 3) {
            permissions.add(OWNER_EXECUTE);
            permissions.add(SET_UID);
          } else {
            permissions.add(GROUP_EXECUTE);
            permissions.add(SET_GID);
          }
        }
        case STICKY_CHAR_NO_EXEC -> permissions.add(STICKY_BIT);
        case STICKY_CHAR -> {
          permissions.add(OTHERS_EXECUTE);
          permissions.add(STICKY_BIT);
        }
        case UNSET_CHAR -> {
        }
        default -> throw new IllegalArgumentException("Unrecognised mode char '" + c + "'");
      }
    }
  }

  private static void selectPermission(final EnumSet<Permission> permissions, final int index,
      Permission ownerPermission, Permission groupPermission, Permission othersPermission) {
    switch (index) {
      case 0 -> permissions.add(ownerPermission);
      case 3 -> permissions.add(groupPermission);
      case 6 -> permissions.add(othersPermission);
      default -> {
      }
    }
  }
}
