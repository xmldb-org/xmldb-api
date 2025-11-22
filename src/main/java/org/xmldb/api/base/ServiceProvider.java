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
package org.xmldb.api.base;

import static org.xmldb.api.base.ErrorCodes.NO_SUCH_SERVICE;

import java.util.Optional;

/**
 * The {@code ServiceProvider} interface access to the {@link Service} implementation instances and
 * is able to query for implemented ones.
 *
 * @since 2.0
 */
public interface ServiceProvider {

  /**
   * Determines if a service of the specified type is available.
   *
   * @param <S> the type of service
   * @param serviceType the class type of the service to check for
   * @return {@code true} if a service of the specified type is available, {@code false} otherwise
   *
   * @since 2.0
   */
  <S extends Service> boolean hasService(Class<S> serviceType);

  /**
   * Returns optional {@code Service} instance for the requested {@code serviceType}. If no
   * {@code Service} exists or can be provided an empty optional is returned.
   * 
   * @param <S> the type of service
   * @param serviceType the type of service to return
   * @return a optional instance of the given service type
   *
   * @since 2.0
   */
  <S extends Service> Optional<S> findService(Class<S> serviceType);

  /**
   * Returns a {@code Service} instance for the requested {@code serviceType}. If no {@code Service}
   * exists a {@link XMLDBException} with {@link ErrorCodes#NO_SUCH_SERVICE} is thrown.
   * 
   * @param <S> the type of service
   * @param serviceType the type of service to return
   * @return a instance of the given service type
   * @throws XMLDBException with expected error codes. {@link ErrorCodes#NO_SUCH_SERVICE} if the
   *         service does not exist, {@link ErrorCodes#VENDOR_ERROR} for any vendor specific errors
   *         that occur. {@link ErrorCodes#COLLECTION_CLOSED} if the {@code close} method has been
   *         called on the {@code Collection}
   *
   * @since 2.0
   */
  default <S extends Service> S getService(Class<S> serviceType) throws XMLDBException {
    return findService(serviceType)
        .orElseThrow(() -> new XMLDBException(NO_SUCH_SERVICE, "Unknown service: " + serviceType));
  }

}
