/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.validation.interceptors.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marker annotation used to indicate is a parameter on a method intercepted by
 * {@link com.ericsson.nms.presentation.service.validation.interceptors.ValidationInterceptor}
 * should be validated.
 *
 * E.g: In the following case only the object parameter will be validated
 * <pre>
 *     void update(@IgnoreValidation String id, MyObject object);
 * </pre>
 *
 */
@Target({PARAMETER})
@Retention(RUNTIME)
public @interface IgnoreValidation {

}
