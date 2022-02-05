/*
 * The XML:DB Initiative Software License, Version 1.0
 *
 * Copyright (c) 2000-2022 The XML:DB Initiative. All rights reserved.
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
 * ====================================================================
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * XML:DB Initiative. For more information on the XML:DB Initiative, please see
 * <https://github.com/xmldb-org/>.
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
import org.xmldb.api.security.UserPrincipalLookupService;

@MockitoSettings
class ServiceProviderCacheTest {
    @Mock
    CollectionManagementService collectionManagementService;
    @Mock
    Supplier<CollectionManagementService> collectionManagementServiceSupplier;

    ServiceProviderCache cache;

    @BeforeEach
    void prepare() {
        cache = ServiceProviderCache
                .initialize(init -> init.add(CollectionManagementService.class,
                        collectionManagementServiceSupplier));
    }

    @Test
    void testInitialize() throws XMLDBException {
        verifyNoInteractions(collectionManagementServiceSupplier);
        assertThat(cache.hasService(CollectionManagementService.class)).isTrue();
        assertThat(cache.hasService(UserPrincipalLookupService.class)).isFalse();
    }

    @Test
    void testHasService() throws XMLDBException {
        verifyNoInteractions(collectionManagementServiceSupplier);
        assertThat(cache.hasService(CollectionManagementService.class)).isTrue();
        assertThat(cache.hasService(UserPrincipalLookupService.class)).isFalse();
    }

    @Test
    void testFindService() throws XMLDBException {
        when(collectionManagementServiceSupplier.get())
                .thenReturn(collectionManagementService);
        assertThat(cache.findService(CollectionManagementService.class))
                .satisfies(s -> assertThat(s.get())
                        .isEqualTo(collectionManagementService));
        assertThat(cache.findService(UserPrincipalLookupService.class))
                .satisfies(s -> assertThat(s).isEmpty());
    }

    @Test
    void testGetService() throws XMLDBException {
        when(collectionManagementServiceSupplier.get())
                .thenReturn(collectionManagementService);
        assertThat(cache.getService(CollectionManagementService.class))
                .isEqualTo(collectionManagementService);
        assertThatExceptionOfType(XMLDBException.class).isThrownBy(
                () -> cache.getService(UserPrincipalLookupService.class))
                .satisfies(e -> {
                    assertThat(e.errorCode).isEqualTo(NO_SUCH_SERVICE);
                    assertThat(e.vendorErrorCode).isEqualTo(0);
                });
    }
}
