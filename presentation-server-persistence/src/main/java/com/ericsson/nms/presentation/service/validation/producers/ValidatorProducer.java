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
package com.ericsson.nms.presentation.service.validation.producers;

import com.ericsson.nms.presentation.service.validation.producers.qualifiers.CustomValidator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Produces an injectable Validator instance
 */
@ApplicationScoped
public class ValidatorProducer {

    @Produces
    @CustomValidator
    public Validator produce() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

}
