/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 * Copyright (c) 2000-2023 The XML:DB Initiative. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.xmldb.api.base.ErrorCodes.COLLECTION_CLOSED;
import static org.xmldb.api.base.ErrorCodes.INVALID_COLLECTION;
import static org.xmldb.api.base.ErrorCodes.INVALID_DATABASE;
import static org.xmldb.api.base.ErrorCodes.INVALID_RESOURCE;
import static org.xmldb.api.base.ErrorCodes.INVALID_URI;
import static org.xmldb.api.base.ErrorCodes.NOT_IMPLEMENTED;
import static org.xmldb.api.base.ErrorCodes.NO_SUCH_DATABASE;
import static org.xmldb.api.base.ErrorCodes.PERMISSION_DENIED;
import static org.xmldb.api.base.ErrorCodes.UNKNOWN_ERROR;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Tests the {@link XMLDBException}
 */
class XMLDBExceptionTest {

  @Test
  void testXMLDBException() {
    XMLDBException ex = new XMLDBException();
    assertThat(ex).hasMessage("").satisfies(e -> {
      assertThat(e.errorCode).isEqualTo(UNKNOWN_ERROR);
      assertThat(e.vendorErrorCode).isZero();
    });
  }

  @Test
  void testXMLDBExceptionInt() {
    XMLDBException ex = new XMLDBException(COLLECTION_CLOSED);
    assertThat(ex).hasMessage("").satisfies(e -> {
      assertThat(e.errorCode).isEqualTo(COLLECTION_CLOSED);
      assertThat(e.vendorErrorCode).isZero();
    });
  }

  @Test
  void testXMLDBExceptionIntString() {
    XMLDBException ex = new XMLDBException(INVALID_COLLECTION, "message 1");
    assertThat(ex).hasMessage("message 1").satisfies(e -> {
      assertThat(e.errorCode).isEqualTo(INVALID_COLLECTION);
      assertThat(e.vendorErrorCode).isZero();
    });
  }

  @Test
  void testXMLDBExceptionIntInt() {
    XMLDBException ex = new XMLDBException(INVALID_DATABASE, 123);
    assertThat(ex).hasMessage("").satisfies(e -> {
      assertThat(e.errorCode).isEqualTo(INVALID_DATABASE);
      assertThat(e.vendorErrorCode).isEqualTo(123);
    });
  }

  @Test
  void testXMLDBExceptionIntIntString() {
    XMLDBException ex = new XMLDBException(INVALID_RESOURCE, 234, "message 2");
    assertThat(ex).hasMessage("message 2").satisfies(e -> {
      assertThat(e.errorCode).isEqualTo(INVALID_RESOURCE);
      assertThat(e.vendorErrorCode).isEqualTo(234);
    });
  }

  @Test
  void testXMLDBExceptionIntThrowable() {
    Throwable cause = new IOException("error 1");
    XMLDBException ex = new XMLDBException(NOT_IMPLEMENTED, cause);
    assertThat(ex).hasMessage("").hasCause(cause).satisfies(e -> {
      assertThat(e.errorCode).isEqualTo(NOT_IMPLEMENTED);
      assertThat(e.vendorErrorCode).isZero();
    });
  }

  @Test
  void testXMLDBExceptionIntStringThrowable() {
    Throwable cause = new IOException("error 2");
    XMLDBException ex = new XMLDBException(INVALID_URI, "message 3", cause);
    assertThat(ex).hasMessage("message 3").hasCause(cause).satisfies(e -> {
      assertThat(e.errorCode).isEqualTo(INVALID_URI);
      assertThat(e.vendorErrorCode).isZero();
    });
  }

  @Test
  void testXMLDBExceptionIntIntThrowable() {
    Throwable cause = new IOException("error 3");
    XMLDBException ex = new XMLDBException(NO_SUCH_DATABASE, 345, cause);
    assertThat(ex).hasMessage("").hasCause(cause).satisfies(e -> {
      assertThat(e.errorCode).isEqualTo(NO_SUCH_DATABASE);
      assertThat(e.vendorErrorCode).isEqualTo(345);
    });
  }

  @Test
  void testXMLDBExceptionIntIntStringThrowable() {
    Throwable cause = new IOException("error 3");
    XMLDBException ex = new XMLDBException(PERMISSION_DENIED, 456, "message 4", cause);
    assertThat(ex).hasMessage("message 4").hasCause(cause).satisfies(e -> {
      assertThat(e.errorCode).isEqualTo(PERMISSION_DENIED);
      assertThat(e.vendorErrorCode).isEqualTo(456);
    });
  }
}
