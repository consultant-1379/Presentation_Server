/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.util;

import org.apache.commons.codec.digest.DigestUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Utility to extract Checksum Hash from Files.
 */
public class FileHashUtil {

    /**
     * Gets the MD5 hash for the given file contents.
     * @param file file to be read
     * @return a String hash to identify the file contents
     * @throws IOException if any exception happens reading the file
     */
    public String getHash(final File file) throws IOException {

        try (final FileInputStream stream = new FileInputStream(file)) {
            return DigestUtils.md5Hex(stream);
        }

    }
}
