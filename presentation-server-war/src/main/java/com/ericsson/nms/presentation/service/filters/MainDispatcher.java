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
package com.ericsson.nms.presentation.service.filters;

import org.jboss.resteasy.plugins.server.servlet.FilterDispatcher;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 * A class to initialize the Rest WebService with
 * Servlet 3.0 annotations.
 *
 * @author ejonbli
 */
@WebFilter(
        filterName = "restfilter",
        urlPatterns = {"/*"},
        initParams = {
                @WebInitParam(name = "resteasy.scan", value = "true")
        }
    )
public class MainDispatcher extends FilterDispatcher {

    @Inject
    Logger logger;

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {

        final String userId = ((HttpServletRequest) servletRequest).getHeader("X-Tor-UserID");
        if (logger.isDebugEnabled()) {
            logger.debug("Called REST URI: {}", ((HttpServletRequest) servletRequest).getServletPath());
            logger.debug("Method: {}", ((HttpServletRequest) servletRequest).getMethod());
            logger.debug("X-Tor-UserID: {}", userId);
            logger.debug("Request Headers: ");

            final Enumeration headerNames = ((HttpServletRequest) servletRequest).getHeaderNames();
            while (headerNames.hasMoreElements()) {
                final String name = (String) headerNames.nextElement();
                logger.debug("   {} -> {}", name, ((HttpServletRequest) servletRequest).getHeader(name));
            }
        }

        super.doFilter(servletRequest, servletResponse, filterChain);
    }
}
