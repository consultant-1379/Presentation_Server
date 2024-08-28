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

import com.ericsson.nms.presentation.exceptions.MissingRequiredPropertyException;
import com.ericsson.oss.itpf.sdk.config.annotation.ConfigurationChangeNotification;
import com.ericsson.oss.itpf.sdk.config.annotation.Configured;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Presentation service config
 */
@ApplicationScoped
public class PresentationServiceConfig {

    @Inject
    Logger logger;

    @Inject
    @Configured(propertyName = "PresentationService_icaAddr")
    String[] icaAddr;

    @Inject
    @Configured(propertyName = "PresentationService_webHost")
    String[] webHost;

    @Inject
    @Configured(propertyName = "PresentationService_webProtocol")
    String[] webProtocol;

    /**
     * Root HashMap storing all properties in the configuration
     */
    private final Map<String, Map<String, String>> configMap = new HashMap<>();

    private static final String HOST_KEY = "WebHost";
    private static final String PROTOCOL_KEY = "WebProtocol";

    /**
     * @see PresentationServiceConfig#init()
     */
    @PostConstruct
    public void init() {
        parse(HOST_KEY, webHost);
        parse(PROTOCOL_KEY, webProtocol);
    }

    /**
     * Takes a property key and parses the line of nested key/value pairs on the line into its own HashMap and stores the HashMap into the master
     * ConfigMap.
     *
     * @param key
     *            Property Key
     * @param ps
     */
    private void parse(final String key, final String[] ps) {
        final Map<String, String> emap = new HashMap<>();
        emap.put("default", "");
        for (final String e : ps) {
            final String[] p = e.trim().split(":");
            if (p.length < 2) {
                continue;
            }
            emap.put(p[0], p[1]);
        }
        configMap.put(key, emap);
    }

    /**
     * Return the value of a property given its root/type and key
     *
     * @param root
     *            The type of property to get
     * @param key
     *            The key of the property type to get
     * @return The value for the passed root/key. If key is null, the default value is returned.
     */
    private String valueOf(final String root, final String key) {
        final String value = (key != null ? configMap.get(root).get(key) : configMap.get(root).get("default"));
        if (value == null) {
            throw new MissingRequiredPropertyException(key, root);
        }
        return value;
    }

    /**
     * @see PresentationServiceConfig#getWebHost(String)
     * @param key {String}
     * @return WebHost
     */
    public String getWebHost(final String key) {
        return valueOf(HOST_KEY, key);
    }

    /**
     * Triggered by the ServiceFramework-Config API when the WebHost property is updated in the properties file. Creates a new webHost HashMap with
     * the updated properties and replaces it with the Map for the property key already in the root HashMap.
     *
     * @param changedWebHost {String[]}
     */
    void changedWebHost(@Observes @ConfigurationChangeNotification(propertyName = "PresentationService_webHost") final String[] changedWebHost) {
        if (logger.isInfoEnabled()) {
            logger.info("Configuration change notification received for property 'PresentationService_webHost'.  New value = {}",
                    Arrays.toString(changedWebHost));
        }
        this.webHost = changedWebHost;
        parse(HOST_KEY, this.webHost);
    }

    /**
     * @see PresentationServiceConfig#getWebProtocol(String)
     * @param key {String}
     * @return WebProtocol
     */
    public String getWebProtocol(final String key) {
        return valueOf(PROTOCOL_KEY, key);
    }

    /**
     * Triggered by the ServiceFramework-Config API when the WebProtocol property is updated in the properties file. Creates a new webProtocol HashMap
     * with the updated properties and replaces it with the Map for the property key already in the root HashMap.
     *
     * @param changedWebProtocol {String[]}
     */
    void changedWebProtocol(
            @Observes @ConfigurationChangeNotification(propertyName = "PresentationService_webProtocol") final String[] changedWebProtocol) {
        if (logger.isInfoEnabled()) {
            logger.info("Configuration change notification received for property 'PresentationService_webProtocol'.  New value = {}",
                    Arrays.toString(changedWebProtocol));
        }
        this.webProtocol = changedWebProtocol;
        parse(PROTOCOL_KEY, this.webProtocol);
    }

}
