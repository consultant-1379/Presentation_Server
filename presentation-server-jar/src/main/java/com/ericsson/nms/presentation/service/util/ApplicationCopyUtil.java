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

import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.CitrixApplication;
import com.ericsson.nms.presentation.service.api.dto.WebApplication;

import javax.inject.Inject;

/**
 * Created by ejgemro on 11/23/15.
 */
public class ApplicationCopyUtil {

    @Inject
    PresentationServiceConfig config;

    private WebApplication copy(final boolean favorite, final WebApplication app) {

        final WebApplication webApp = new WebApplication(app.getId(), app.getName());
        webApp.setAcronym(app.getAcronym());
        webApp.setShortInfo(app.getShortInfo());
        webApp.setFavorite(Boolean.toString(favorite));
        webApp.setExternal(app.getExternal());
        webApp.setVersion(app.getVersion());
        webApp.setConsumes(app.getConsumes());
        webApp.setPath(app.getPath());
        webApp.setExternalHost(app.getExternalHost());
        webApp.setHost(config != null ? config.getWebHost(app.getHost()) : "localhost");
        webApp.setProtocol(config != null ? config.getWebProtocol(app.getProtocol()) : "http");
        webApp.setOpenInNewWindow(app.getOpenInNewWindow());
        final int portNumber = app.getPort() != null ? Integer.parseInt(app.getPort()) : 80;
        final boolean defaultPort = (portNumber == 80 || portNumber == 443);
        webApp.setPort(((("http".equals(webApp.getProtocol()) && defaultPort) ||
                ("https".equals(webApp.getProtocol()) && defaultPort)) ? "" : ":" + portNumber));
        webApp.setHidden(app.isHidden());
        return webApp;
    }

    private CitrixApplication copy(final boolean favorite, final CitrixApplication app) {

        final CitrixApplication citrixApp = new CitrixApplication(app.getId(), app.getName());
        citrixApp.setAcronym(app.getAcronym());
        citrixApp.setShortInfo(app.getShortInfo());
        citrixApp.setFavorite(Boolean.toString(favorite));
        citrixApp.setHost(config != null ? config.getWebHost(app.getHost()) : "localhost");
        citrixApp.setHidden(app.isHidden());
        return citrixApp;
    }

    /**
     * @param <T> The expected class of the value
     * @param favorite {boolean}
     * @param app <T>
     * @return copy or exception
     */
    public <T extends AbstractApplication> T copy(final boolean favorite, final T app) {

        if (app instanceof WebApplication) {
            return (T)this.copy(favorite, (WebApplication) app);
        }
        if (app instanceof CitrixApplication) {
            return (T)this.copy(favorite, (CitrixApplication) app);
        }

        throw new IllegalArgumentException("Only CitrixApplication and WebApplication instances can be copied.");
    }

}
