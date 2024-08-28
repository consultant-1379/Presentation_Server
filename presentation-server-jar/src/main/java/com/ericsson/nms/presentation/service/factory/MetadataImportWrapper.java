/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.factory;

import com.ericsson.nms.presentation.service.api.dto.Metadata;

/**
 * Preserve any error information that occurs when reading Metadata.
 * @see Metadata
 */
public class MetadataImportWrapper {

    private Metadata metadata;

    private long readFailures;

    /**
     * Constructor.
     * @param metadata Metadata object
     */
    public MetadataImportWrapper(final Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Constructor.
     * @param metadata Metadata object
     * @param readFailures number of failures to report
     */
    public MetadataImportWrapper(final Metadata metadata, final long readFailures) {
        this.metadata = metadata;
        this.readFailures = readFailures;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(final Metadata metadata) {
        this.metadata = metadata;
    }

    public long getReadFailures() {
        return readFailures;
    }

    public void setReadFailures(final long readFailures) {
        this.readFailures = readFailures;
    }
}
