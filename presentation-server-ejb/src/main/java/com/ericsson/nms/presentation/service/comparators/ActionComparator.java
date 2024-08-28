/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.comparators;

import com.ericsson.nms.presentation.service.api.dto.Action;
import com.ericsson.nms.presentation.service.util.ActionCategoryUtil;

import javax.inject.Inject;
import java.util.Comparator;

/**
 * <p>This comparator is used to sort the actions in the following precedence:</p>
 * <p>
 *     <ul>
 *         <ol>Local or Remote Action</ol>
 *         <ol>Action Category (see bellow)</ol>
 *         <ol>Primary Action</ol>
 *         <ol>Order attribute</ol>
 *     </ul>
 * </p>
 * <p>The following order will be applied to categories:
 *      <ul>
 *          <ol>Actions in the "Fault Management Actions" category</ol>
 *          <ol>Actions in the "Monitoring & Troubleshooting Actions" category</ol>
 *          <ol>Actions in the "Configuration Management" category</ol>
 *          <ol>Actions in the "Legacy Actions"</ol>
 *          <ol>Actions in the "Performance Management" category</ol>
 *          <ol>Actions in the "Security Management" category</ol>
 *      </ul>
 * </p>
 */
public class ActionComparator implements Comparator<Action> {

    @Inject
    ActionCategoryUtil categoryUtil;

    @Override
    public int compare(final Action left, final Action right) {

        final String leftCategory = left.getCategory();
        final String rightCategory = right.getCategory();

        // Left action is local, so has higher precedence
        if (left.isLocal() && !right.isLocal()) {
            return -1;

        // Right action is local, so has higher precedence
        } else if (!left.isLocal() && right.isLocal()) {
            return 1;

        } else if (left.isLocal() && right.isLocal()) {
            return compareActionOrderAttribute(left, right);
        }

        // If they are in the same category the precedence is also the same
        if (leftCategory.equals(rightCategory)) {
            return compareActionsInTheSameCategory(left, right);
        // Both actions are on remote categories
        } else {
            return compareRemoteCategory(leftCategory, rightCategory);
        }

    }

    private int compareActionsInTheSameCategory(final Action left, final Action right) {

        if (left.isPrimary() && !right.isPrimary()) {
            return -1;

        } else if (!left.isPrimary() && right.isPrimary()) {
            return 1;
        }

        return compareActionOrderAttribute(left, right);
    }

    /**
     * Compares two remote categories based on their precedence
     * @param left
     * @param right
     * @return
     */
    private int compareRemoteCategory(final String left, final String right) {
        return categoryUtil.getPrecedence(left)
                .compareTo(categoryUtil.getPrecedence(right));
    }

    /**
     * Compares two actions based on their order attribute
     * @param left
     * @param right
     * @return
     */
    private int compareActionOrderAttribute(final Action left, final Action right) {
        final Comparator<Integer> nullSafeComparator = Comparator.nullsLast(Integer::compareTo);
        return Comparator.comparing(Action::getOrder, nullSafeComparator).compare(left, right);
    }
}
