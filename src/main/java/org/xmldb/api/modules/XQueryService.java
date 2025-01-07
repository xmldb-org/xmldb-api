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
package org.xmldb.api.modules;

import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;

public interface XQueryService extends Service {

  /**
   * Sets a namespace mapping in the internal namespace map used to evaluate queries. If
   * {@code prefix} is null or empty the default namespace is associated with the provided URI. A
   * null or empty {@code uri} results in an exception being thrown.
   *
   * @param prefix The prefix to set in the map. If {@code prefix} is empty or null the default
   *        namespace will be associated with the provided URI.
   * @param uri The URI for the namespace to be associated with prefix.
   * @throws XMLDBException if an error occurs whilst setting the namespace.
   */
  void setNamespace(String prefix, String uri) throws XMLDBException;

  /**
   * Returns the URI string associated with {@code prefix} from the internal namespace map. If
   * {@code prefix} is null or empty the URI for the default namespace will be returned. If a
   * mapping for the {@code prefix} can not be found null is returned.
   *
   * @param prefix The prefix to retrieve from the namespace map.
   * @return The URI associated with {@code prefix}
   * @throws org.xmldb.api.base.XMLDBException with expected error codes.
   *         {@code ErrorCodes.VENDOR_ERROR} for any vendor specific errors that occur.
   * @throws XMLDBException if an error occurs whilst getting the namespace.
   */
  String getNamespace(String prefix) throws XMLDBException;

  /**
   * Removes the namespace mapping associated with {@code prefix} from the internal namespace map.
   * If {@code prefix} is null or empty the mapping for the default namespace will be removed.
   *
   * @param prefix The prefix to remove from the namespace map. If {@code prefix} is null or empty
   *        the mapping for the default namespace will be removed.
   * @throws org.xmldb.api.base.XMLDBException with expected error codes.
   *         {@code ErrorCodes.VENDOR_ERROR} for any vendor specific errors that occur.
   */
  void removeNamespace(String prefix) throws XMLDBException;

  /**
   * Clears all namespace mappings defined.
   *
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  void clearNamespaces() throws XMLDBException;

  /**
   * Executes the given query and returns the result as a resource set.
   *
   * @param query The query to be executed
   * @return The query result
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  ResourceSet query(String query) throws XMLDBException;

  /**
   * Executes the given query and returns the result as a resource set.
   *
   * @param id The id of a resource
   * @param query The query to be executed
   * @return The query result
   * @throws XMLDBException with expected error codes. {@code ErrorCodes.VENDOR_ERROR} for any
   *         vendor specific errors that occur.
   */
  ResourceSet queryResource(String id, String query) throws XMLDBException;

  /**
   * Compiles the specified XQuery and returns a handle to the compiled code, which can then be
   * passed to {@link #execute(org.xmldb.api.base.CompiledExpression)}.
   *
   * @param query the query to compile.
   * @return the compiled query expression
   * @throws XMLDBException if an error occurs whilst compiling the query.
   */
  CompiledExpression compile(String query) throws XMLDBException;

  /**
   * Execute a compiled XQuery.
   *
   * The implementation should pass all namespaces and variables declared through
   * {@link XQueryService} to the compiled XQuery code.
   *
   * @param expression the compiled query expression
   * @return the result of the query
   * @throws XMLDBException if an error occurs whilst executing the query.
   */
  ResourceSet execute(CompiledExpression expression) throws XMLDBException;

  /**
   * Declare a global, external XQuery variable and assign a value to it. The variable has the same
   * status as a variable declare through the {@code declare variable} statement in the XQuery
   * prolog.
   *
   * The variable can be referenced inside the XQuery expression as {@code $variable}. For example,
   * if you declare a variable with
   *
   * <pre>
   * declareVariable("name", "HAMLET");
   * </pre>
   *
   * you may use the variable in an XQuery expression as follows:
   *
   * <pre>
   * // SPEECH[SPEAKER=$name]
   * </pre>
   *
   *
   * @param qname a valid QName by which the variable is identified. Any prefix should have been
   *        mapped to a namespace, using {@link #setNamespace(String, String)}. For example, if a
   *        variable is called <b>x:name</b>, a prefix/namespace mapping should have been defined
   *        for prefix {@code x} before calling this method.
   * @param initialValue the initial value, which is assigned to the variable
   * @throws XMLDBException if an error occurs whilst declaring the variable.
   */
  void declareVariable(String qname, Object initialValue) throws XMLDBException;

  /**
   * Enable or disable XPath 1.0 compatibility mode. In XPath 1.0 compatibility mode, some XQuery
   * expressions will behave different. In particular, additional automatic type conversions will be
   * applied to the operands of numeric operators.
   *
   * @param backwardsCompatible true it XPath 1.0 compatibility mode should be enabled.
   */
  void setXPathCompatibility(boolean backwardsCompatible);

  /**
   * Sets the new module load path.
   *
   * @param path The module load path to be set.
   */
  void setModuleLoadPath(String path);
}
