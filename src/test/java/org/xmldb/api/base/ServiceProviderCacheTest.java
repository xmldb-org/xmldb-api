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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.xmldb.api.base.ErrorCodes.NO_SUCH_SERVICE;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.modules.XQueryService;
import org.xmldb.api.security.UserPrincipalLookupService;

@MockitoSettings
class ServiceProviderCacheTest {
  @Mock
  CollectionManagementService collectionManagementService;
  @Mock
  Supplier<CollectionManagementService> collectionManagementServiceSupplier;
  @Mock
  Supplier<CombinedQueryService> combinedQueryServiceProvider;
  @Mock
  CombinedQueryService combinedQueryService;

  ServiceProviderCache cache;

  @BeforeEach
  void prepare() {
    cache = ServiceProviderCache.withRegistered(registry -> {
      registry.add(CollectionManagementService.class, collectionManagementServiceSupplier);
      registry.add(XPathQueryService.class, combinedQueryServiceProvider);
      registry.add(XQueryService.class, combinedQueryServiceProvider);
    });
  }

  @Test
  void testWithRegistered() throws XMLDBException {
    verifyNoInteractions(collectionManagementServiceSupplier);
    assertThat(cache.hasService(CollectionManagementService.class)).isTrue();
    assertThat(cache.hasService(XPathQueryService.class)).isTrue();
    assertThat(cache.hasService(XQueryService.class)).isTrue();
    assertThat(cache.hasService(UserPrincipalLookupService.class)).isFalse();
  }

  @Test
  void testHasService() throws XMLDBException {
    verifyNoInteractions(collectionManagementServiceSupplier);
    assertThat(cache.hasService(CollectionManagementService.class)).isTrue();
    assertThat(cache.hasService(XPathQueryService.class)).isTrue();
    assertThat(cache.hasService(XQueryService.class)).isTrue();
    assertThat(cache.hasService(UserPrincipalLookupService.class)).isFalse();
  }

  @Test
  void testFindService() throws XMLDBException {
    when(collectionManagementServiceSupplier.get()).thenReturn(collectionManagementService);
    when(combinedQueryServiceProvider.get()).thenReturn(combinedQueryService);
    assertThat(cache.findService(CollectionManagementService.class))
        .satisfies(s -> assertThat(s).contains(collectionManagementService));
    assertThat(cache.findService(XPathQueryService.class))
        .satisfies(s -> assertThat(s).contains(combinedQueryService));
    assertThat(cache.findService(XQueryService.class))
        .satisfies(s -> assertThat(s).contains(combinedQueryService));
    assertThat(cache.findService(UserPrincipalLookupService.class))
        .satisfies(s -> assertThat(s).isEmpty());
  }

  @Test
  void testGetService() throws XMLDBException {
    when(collectionManagementServiceSupplier.get()).thenReturn(collectionManagementService);
    when(combinedQueryServiceProvider.get()).thenReturn(combinedQueryService);
    assertThat(cache.getService(CollectionManagementService.class))
        .isEqualTo(collectionManagementService);
    assertThat(cache.getService(XPathQueryService.class)).isEqualTo(combinedQueryService);
    assertThat(cache.getService(XQueryService.class)).isEqualTo(combinedQueryService);
    assertThatExceptionOfType(XMLDBException.class)
        .isThrownBy(() -> cache.getService(UserPrincipalLookupService.class)).satisfies(e -> {
          assertThat(e.errorCode).isEqualTo(NO_SUCH_SERVICE);
          assertThat(e.vendorErrorCode).isZero();
        });
  }

  interface CombinedQueryService extends XPathQueryService, XQueryService {
  }
}
