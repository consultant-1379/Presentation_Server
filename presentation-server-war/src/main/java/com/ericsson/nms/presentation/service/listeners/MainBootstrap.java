/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.listeners;

import com.ericsson.nms.presentation.service.util.PresentationServiceConfig;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

/**
 * Main bootstrap class
 */
@WebListener
public class MainBootstrap extends ResteasyBootstrap {

    @Inject
    private PresentationServiceConfig config;

    /**
     * @see org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(final ServletContextEvent event) {
        super.contextInitialized(event);

        config.init();
    }

}
