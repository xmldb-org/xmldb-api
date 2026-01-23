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

import java.io.Serial;

/**
 * XMLDBException is thrown for all errors in the XML:DB API.
 * <p>
 * It contains two error codes, one an XML:DB error code as defined in ErrorCodes and one vendor
 * specific.
 * <p>
 * If the error being thrown is only vendor specific, then errorCode MUST be set to
 * ErrorCodes.VENDOR_ERROR.
 *
 * @see org.xmldb.api.base.ErrorCodes
 */
public final class XMLDBException extends Exception {
  @Serial
  private static final long serialVersionUID = 8841586061740517362L;

  /**
   * Represents the XML:DB error code associated with a specific error condition.
   * <p>
   * The {@code errorCode} field is used to identify the type of error that occurred during XML:DB
   * operations.
   * <p>
   * It corresponds to predefined error codes within the XML:DB framework.
   */
  public final int errorCode;
  /**
   * Represents a vendor-specific error code that provides additional information about an error
   * condition specific to the underlying database or driver implementation.
   * <p>
   * This {@code  vendorErrorCode} field is primarily used to include vendor-defined error codes
   * alongside the standard XML:DB error codes for more detailed diagnostics.
   */
  public final int vendorErrorCode;

  /**
   * Constructs a new XMLDBException instance with a default error code set to
   * {@link ErrorCodes#UNKNOWN_ERROR}.
   *
   * @see ErrorCodes#UNKNOWN_ERROR
   */
  public XMLDBException() {
    this(ErrorCodes.UNKNOWN_ERROR);
  }

  /**
   * Constructs a new XMLDBException instance with a specific errorCode.
   *
   * @param errorCode the XML:DB error code representing the specific error condition.
   */
  public XMLDBException(int errorCode) {
    this(errorCode, 0, null, null);
  }

  /**
   * Constructs a new XMLDBException instance with a specific errorCode and message. This
   * constructor is used to provide a more descriptive exception by combining an XML:DB error code
   * with a custom error message.
   *
   * @param errorCode the XML:DB error code representing the specific error condition.
   * @param message a custom message providing additional details about the error.
   */
  public XMLDBException(int errorCode, String message) {
    this(errorCode, 0, message, null);
  }

  /**
   * Constructs a new {@code XMLDBException} instance with specific XML:DB and vendor error codes.
   *
   * @param errorCode the XML:DB error code representing the specific error condition.
   * @param vendorErrorCode the vendor-specific error code providing additional details about the
   *        error.
   */
  public XMLDBException(int errorCode, int vendorErrorCode) {
    this(errorCode, vendorErrorCode, null, null);
  }

  /**
   * Constructs a new {@code XMLDBException} instance with specific XML:DB and vendor error codes,
   * and an error message.
   *
   * @param errorCode the XML:DB error code representing the specific error condition.
   * @param vendorErrorCode the vendor-specific error code providing additional details about the
   *        error.
   * @param message a custom message providing additional details about the error.
   */
  public XMLDBException(int errorCode, int vendorErrorCode, String message) {
    this(errorCode, vendorErrorCode, message, null);
  }

  /**
   * Constructs a new {@code XMLDBException} instance with a specific error code and a cause for the
   * exception. This constructor is used when only the XML:DB error code and the underlying cause of
   * the exception are available.
   *
   * @param errorCode the XML:DB error code representing the specific error condition.
   * @param cause the underlying {@code Throwable} that caused this exception to be thrown.
   */
  public XMLDBException(int errorCode, Throwable cause) {
    this(errorCode, 0, null, cause);
  }

  /**
   * Constructs a new {@code XMLDBException} instance with a specific error code, message, and
   * underlying cause.
   *
   * @param errorCode the XML:DB error code representing the specific error condition.
   * @param message a custom message providing additional details about the error.
   * @param cause the underlying {@code Throwable} that caused this exception to be thrown.
   */
  public XMLDBException(int errorCode, String message, Throwable cause) {
    this(errorCode, 0, message, cause);
  }

  /**
   * Constructs a new {@code XMLDBException} instance with specific XML:DB and vendor error codes,
   * and the underlying cause of the exception.
   *
   * @param errorCode the XML:DB error code representing the specific error condition.
   * @param vendorErrorCode the vendor-specific error code providing additional details about the
   *        error.
   * @param cause the underlying {@code Throwable} that caused this exception to be thrown.
   */
  public XMLDBException(int errorCode, int vendorErrorCode, Throwable cause) {
    this(errorCode, vendorErrorCode, null, cause);
  }

  /**
   * Constructs a new {@code XMLDBException} instance with specific XML:DB and vendor error codes, a
   * custom error message, and the underlying cause of the exception.
   *
   * @param errorCode the XML:DB error code representing the specific error condition.
   * @param vendorErrorCode the vendor-specific error code providing additional details about the
   *        error.
   * @param message a custom message providing additional details about the error.
   * @param cause the underlying {@code Throwable} that caused this exception to be thrown.
   */
  public XMLDBException(int errorCode, int vendorErrorCode, String message, Throwable cause) {
    super(messageFromErrorCode(message, errorCode, vendorErrorCode), cause);
    this.errorCode = errorCode;
    this.vendorErrorCode = vendorErrorCode;
  }

  static String messageFromErrorCode(final String message, final int errorCode,
      final int vendorErrorCode) {
    if (message != null) {
      return message;
    } else if (ErrorCodes.VENDOR_ERROR == errorCode) {
      return "Vendor error: " + vendorErrorCode;
    } else {
      return ErrorCodes.defaultMessage(errorCode);
    }
  }
}
