/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 * Copyright (c) 2000-2026 The XML:DB Initiative. All rights reserved.
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
 * <p>
 * This class provides static methods to parse string and integer based permission modes into a set
 * of permissions.
 *
 * @since 2.0
 */
public final class Permissions {
  private static final byte READ = 04;
  private static final byte WRITE = 02;
  private static final byte EXECUTE = 01;

  private static final char SET_ID_CHAR = 's';
  private static final char SET_ID_CHAR_NO_EXEC = 'S';
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

  private static final Pattern UNIX_SYMBOLIC_MODE_PATTERN =
      Pattern.compile("((?:[augo]*(?:[+\\-=](?:[" + READ_CHAR + SET_ID_CHAR + STICKY_CHAR
          + WRITE_CHAR + EXECUTE_CHAR + "])+)+),?)+");
  private static final Pattern SIMPLE_SYMBOLIC_MODE_PATTERN = Pattern
      .compile("(?:(?:" + READ_CHAR + "|" + UNSET_CHAR + ")(?:" + WRITE_CHAR + "|" + UNSET_CHAR
          + ")(?:[" + EXECUTE_CHAR + SET_ID_CHAR + SET_ID_CHAR_NO_EXEC + "]|" + UNSET_CHAR
          + ")){2}(?:" + READ_CHAR + "|" + UNSET_CHAR + ")(?:" + WRITE_CHAR + "|" + UNSET_CHAR
          + ")(?:[" + EXECUTE_CHAR + STICKY_CHAR + STICKY_CHAR_NO_EXEC + "]|" + UNSET_CHAR + ")");

  private Permissions() {}

  private enum PermType {
    READ, WRITE, EXECUTE, SET_ID, STICKY;
  }

  /**
   * Parses the permissions from the given mode string.
   * <p>
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
    // special bits
    if ((mode & (1 << 11)) != 0) {
      permissions.add(SET_UID);
    }
    if ((mode & (1 << 10)) != 0) {
      permissions.add(SET_GID);
    }
    if ((mode & (1 << 9)) != 0) {
      permissions.add(STICKY_BIT);
    }
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
    return permissions;
  }

  /**
   * Converts a set of permissions into an octal representation.
   *
   * @param permissions the set of {@code Permission} to be converted into the octal representation
   * @return the octal representation of the given permission set
   * @since 2.2
   */
  public static int toOctal(Set<Permission> permissions) {
    int octal = 0;
    // Handle special bits
    if (permissions.contains(SET_UID)) {
      octal |= 04000; // SUID
    }
    if (permissions.contains(SET_GID)) {
      octal |= 02000; // SGID
    }
    if (permissions.contains(STICKY_BIT)) {
      octal |= 01000; // Sticky bit
    }
    // Handle owner permissions
    if (permissions.contains(OWNER_READ)) {
      octal |= 0400; // Owner read
    }
    if (permissions.contains(OWNER_WRITE)) {
      octal |= 0200; // Owner write
    }
    if (permissions.contains(OWNER_EXECUTE)) {
      octal |= 0100; // Owner execute
    }
    // Handle group permissions
    if (permissions.contains(GROUP_READ)) {
      octal |= 040; // Group read
    }
    if (permissions.contains(GROUP_WRITE)) {
      octal |= 020; // Group write
    }
    if (permissions.contains(GROUP_EXECUTE)) {
      octal |= 010; // Group execute
    }
    // Handle others permissions
    if (permissions.contains(OTHERS_READ)) {
      octal |= 04; // Others read
    }
    if (permissions.contains(OTHERS_WRITE)) {
      octal |= 02; // Others write
    }
    if (permissions.contains(OTHERS_EXECUTE)) {
      octal |= 01; // Others execute
    }
    return octal;
  }

  /**
   * Converts the given set of permissions into a string representation. The string format
   * represents the symbolic mode of the permissions.
   *
   * @param permissions the set of {@code Permission} to be converted into a string representation
   * @return a string representation of the permissions in symbolic mode
   * @since 2.2
   */
  public static String toModeString(Set<Permission> permissions) {
    StringBuilder mode = new StringBuilder();
    // Append permissions for owner
    appendReadWritePermissions(mode, permissions, OWNER_READ, OWNER_WRITE);
    appendExecuteAndSpecialPermissions(mode, permissions, OWNER_EXECUTE, SET_UID, "s", "S");
    // Append permissions for group
    appendReadWritePermissions(mode, permissions, GROUP_READ, GROUP_WRITE);
    appendExecuteAndSpecialPermissions(mode, permissions, GROUP_EXECUTE, SET_GID, "s", "S");
    // Append permissions for others
    appendReadWritePermissions(mode, permissions, OTHERS_READ, OTHERS_WRITE);
    appendExecuteAndSpecialPermissions(mode, permissions, OTHERS_EXECUTE, STICKY_BIT, "t", "T");
    return mode.toString();
  }

  private static void appendReadWritePermissions(StringBuilder mode, Set<Permission> permissions,
      Permission read, Permission write) {
    mode.append(permissions.contains(read) ? "r" : "-");
    mode.append(permissions.contains(write) ? "w" : "-");
  }

  private static void appendExecuteAndSpecialPermissions(StringBuilder mode,
      Set<Permission> permissions, Permission execute, Permission special, String withExecute,
      String withoutExecute) {
    if (permissions.contains(special)) {
      mode.append(permissions.contains(execute) ? withExecute : withoutExecute);
    } else {
      mode.append(permissions.contains(execute) ? "x" : "-");
    }
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
          case ALL_CHAR:
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
              if (perms.containsKey(PermType.SET_ID)) {
                permissions.add(SET_UID);
                permissions.add(SET_GID);
              }
              if (perms.containsKey(PermType.STICKY)) {
                permissions.add(STICKY_BIT);
              }
            }
            break;
          case USER_CHAR:
            if (clause.indexOf('+') > -1 || clause.indexOf('=') > -1) {
              setPermissions(permissions, perms, OWNER_READ, OWNER_WRITE, OWNER_EXECUTE);
              if (perms.containsKey(PermType.SET_ID)) {
                permissions.add(SET_UID);
              }
            }
            break;
          case GROUP_CHAR:
            if (clause.indexOf('+') > -1 || clause.indexOf('=') > -1) {
              setPermissions(permissions, perms, GROUP_READ, GROUP_WRITE, GROUP_EXECUTE);
              if (perms.containsKey(PermType.SET_ID)) {
                permissions.add(SET_GID);
              }
            }
            break;
          case OTHER_CHAR:
            if (clause.indexOf('+') > -1 || clause.indexOf('=') > -1) {
              setPermissions(permissions, perms, OTHERS_READ, OTHERS_WRITE, OTHERS_EXECUTE);
              if (perms.containsKey(PermType.STICKY)) {
                permissions.add(STICKY_BIT);
              }
            }
            break;
          default:
            throw new IllegalArgumentException("Unrecognised mode char '" + c + "'");
        }
      }
      perms.clear();
    }
  }

  private static void parseOperation(final String[] whoPerm,
      final EnumMap<PermType, Boolean> perms) {
    for (final char c : whoPerm[1].toCharArray()) {
      switch (c) {
        case READ_CHAR:
          perms.put(PermType.READ, TRUE);
          break;
        case WRITE_CHAR:
          perms.put(PermType.WRITE, TRUE);
          break;
        case EXECUTE_CHAR:
          perms.put(PermType.EXECUTE, TRUE);
          break;
        case SET_ID_CHAR:
          perms.put(PermType.SET_ID, TRUE);
          break;
        case STICKY_CHAR:
          perms.put(PermType.STICKY, TRUE);
          break;
        default:
          throw new IllegalArgumentException("Unrecognised mode char '" + c + "'");
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
        case READ_CHAR:
          selectPermission(permissions, index, OWNER_READ, GROUP_READ, OTHERS_READ);
          break;
        case WRITE_CHAR:
          selectPermission(permissions, index - 1, OWNER_WRITE, GROUP_WRITE, OTHERS_WRITE);
          break;
        case EXECUTE_CHAR:
          selectPermission(permissions, index - 2, OWNER_EXECUTE, GROUP_EXECUTE, OTHERS_EXECUTE);
          break;
        case SET_ID_CHAR_NO_EXEC:
          if (index < 3) {
            permissions.add(SET_UID);
          } else {
            permissions.add(SET_GID);
          }
          break;
        case SET_ID_CHAR:
          if (index < 3) {
            permissions.add(OWNER_EXECUTE);
            permissions.add(SET_UID);
          } else {
            permissions.add(GROUP_EXECUTE);
            permissions.add(SET_GID);
          }
          break;
        case STICKY_CHAR_NO_EXEC:
          permissions.add(STICKY_BIT);
          break;
        case STICKY_CHAR:
          permissions.add(OTHERS_EXECUTE);
          permissions.add(STICKY_BIT);
          break;
        case UNSET_CHAR:
          break;
        default:
          throw new IllegalArgumentException("Unrecognised mode char '" + c + "'");
      }
    }
  }

  private static void selectPermission(EnumSet<Permission> permissions, int index,
      Permission ownerPermission, Permission groupPermission, Permission othersPermission) {
    switch (index) {
      case 0:
        permissions.add(ownerPermission);
        break;
      case 3:
        permissions.add(groupPermission);
        break;
      case 6:
        permissions.add(othersPermission);
        break;
      default:
    }
  }
}
