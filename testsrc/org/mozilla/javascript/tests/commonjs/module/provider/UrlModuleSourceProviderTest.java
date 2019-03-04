/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.javascript.tests.commonjs.module.provider;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlConnectionExpiryCalculator;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

public class UrlModuleSourceProviderTest {

    private static final UrlConnectionExpiryCalculator ALWAYS_CHECK_EXPIRED = urlConnection -> 0;

    @Test
    public void testModuleNotModified() throws Exception {
        // given
        final File file = File.createTempFile("test", ".js");
        final ModuleSource result;
        try {
            final URI moduleURI = getModuleURI(file);
            final UrlModuleSourceProvider sourceProvider =
                    new UrlModuleSourceProvider(null, null, ALWAYS_CHECK_EXPIRED, null);
            final ModuleSource moduleSource = sourceProvider.loadSource(moduleURI, null, null);
            moduleSource.getReader().close();

            // when
            result = sourceProvider.loadSource(moduleURI, null, moduleSource.getValidator());
        } finally {
            file.delete();
        }

        // then
        Assert.assertEquals("Not modified", ModuleSourceProvider.NOT_MODIFIED, result);
    }

    @Test
    public void testModuleModified() throws Exception {
        // given
        final File file = File.createTempFile("test", ".js");
        final ModuleSource result;
        try {
            final URI moduleURI = getModuleURI(file);
            final UrlModuleSourceProvider sourceProvider =
                    new UrlModuleSourceProvider(null, null, ALWAYS_CHECK_EXPIRED, null);
            final ModuleSource moduleSource = sourceProvider.loadSource(moduleURI, null, null);
            moduleSource.getReader().close();

            // when
            file.setLastModified(Long.MAX_VALUE);
            result = sourceProvider.loadSource(moduleURI, null, moduleSource.getValidator());
            result.getReader().close();
        } finally {
            file.delete();
        }

        // then
        Assert.assertNotNull(result);
        Assert.assertNotEquals("Modified", ModuleSourceProvider.NOT_MODIFIED, result);
    }

    private static URI getModuleURI(final File file) throws URISyntaxException {
        final String uriString = file.toURI().toASCIIString();
        return new URI(uriString.substring(0, uriString.lastIndexOf('.')));
    }

}