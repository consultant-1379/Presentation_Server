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
package com.ericsson.nms.presentation.service.validation.interceptors;


import com.ericsson.nms.presentation.exceptions.service.ValidationException;
import com.ericsson.nms.presentation.service.validation.interceptors.annotations.IgnoreValidation;
import com.ericsson.nms.presentation.service.validation.interceptors.bindings.Validate;
import com.ericsson.nms.presentation.service.validation.producers.qualifiers.CustomValidator;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Interceptor used to trigger bean validation framework
 */
@Validate
@Priority(Interceptor.Priority.APPLICATION)
@Interceptor
public class ValidationInterceptor {

    @Inject
    @CustomValidator
    private Validator validator;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {

        Stream.of(context.getParameters())
            .filter(this::shouldBeValidated)
            .forEach(this::validate);

        return context.proceed();
    }

    private boolean shouldBeValidated(Object parameter) {
        return Stream.of(parameter.getClass().getAnnotations())
            .noneMatch(a -> a.getClass().equals(IgnoreValidation.class));
    }

    private <T> void validate(T parameter) {

        Set<ConstraintViolation<T>> violations = validator.validate(parameter);
        if (!violations.isEmpty()) {
            ConstraintViolation<T> firstViolation = violations.stream().findFirst().get();
            throw new ValidationException(firstViolation.getMessage());
        }

    }

}
