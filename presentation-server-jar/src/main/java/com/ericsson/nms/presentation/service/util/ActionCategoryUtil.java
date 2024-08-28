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

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.ericsson.nms.presentation.service.PresentationServerConstants.*;

/**
 * Utility class for Action categories.
 */
public class ActionCategoryUtil {

    /**
     * Holds all remote actions categories and their precedence when ordering
     */
    private final Map<String, Integer> precedences = new HashMap<>();

    /**
     * Initialize the precedence map.
     */
    @PostConstruct
    public void initialize() {

        // Initializes the precedence map
        precedences.put(FAULT_MANAGEMENT_ACTION_CATEGORY, 1);
        precedences.put(MONITORING_ACTION_CATEGORY, 2);
        precedences.put(CONFIGURATION_ACTION_CATEGORY, 3);
        precedences.put(PERFORMANCE_ACTION_CATEGORY, 4);
        precedences.put(SECURITY_ACTION_CATEGORY, 5);
        precedences.put(COLLECTION_ACTION_CATEGORY, 6);
        precedences.put(COLLECTION_MODIFICATION_ACTION_CATEGORY, 7);
        precedences.put(LEGACY_ACTION_CATEGORY, 8);
    }

    /**
     * Retrieves all supported action categories
     * @return supported action categories
     */
    public Set<String> getSupportedCategories() {
        return precedences.keySet();
    }

    /**
     * Verifies if the given category is supported
     * @param category category to be checked
     * @return true if the category is valid, false otherwise
     */
    public boolean isCategorySupported(final String category) {
        return getSupportedCategories().contains(category);
    }

    /**
     * Returns the precedence for the given category
     * @param category category to be checked
     * @return integer representing the precedence level
     */
    public Integer getPrecedence(final String category) {
        return precedences.get(category);
    }

}
