/*******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.comparators;

import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;

import java.util.Comparator;

/**
 * This comparator is used to sort applications by name and version
 *
 * This will sort applications alphabetically by their name.
 * If two applications contain the same name, they will be sorted by version, with the higher version going first.
 */
public class ApplicationComparator implements Comparator<AbstractApplication> {

    @Override
    public int compare(final AbstractApplication application1, final AbstractApplication application2) {
        if (application1.getName() != null && application2.getName() != null){
            if(application1.getName().equals(application2.getName())){
                if(application1.getVersion() == null){
                    return 1;
                }else if(application2.getVersion() == null){
                    return -1;
                }
                return application2.getVersion().compareTo(application1.getVersion());
            }
            return application1.getName().compareTo(application2.getName());
        }
        return 0;
    }
}
