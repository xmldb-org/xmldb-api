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
package org.xmldb.api.base;

import org.xmldb.api.DatabaseManager;

/**
 * Represents an action performed on a database when it is deregistered from the system. This
 * interface provides a mechanism for databases to define custom procedures or behaviors during the
 * deregistration process.
 * <p>
 * Implementations of this interface should handle the logic necessary to clean up resources, close
 * active connections, or perform any required tasks when the database is removed. The specifics of
 * these actions depend on the database implementation.
 */
public interface DatabaseAction {
  /**
   * Method called by {@linkplain DatabaseManager#deregisterDatabase(Database) } to notify the
   * Database that it was de-registered.
   * <p>
   * The {@code deregister} method is intended only to be used by database and not by applications.
   * Databases are recommended to not implement {@code DatabaseAction} in a public class. If there
   * are active connections to the database at the time that the {@code deregister} method is
   * called, it is implementation specific as to whether the connections are closed or allowed to
   * continue. Once this method is called, it is implementation specific as to whether the database
   * may limit the ability to open collections of a database, invoke other {@code Database} methods
   * or throw a {@code XMLDBException}. Consult your database's documentation for additional
   * information on its behavior.
   * 
   * @see DatabaseManager#registerDatabase(Database, DatabaseAction)
   * @see DatabaseManager#deregisterDatabase(Database)
   * @since 3
   */
  void deregister();
}
