package com.ebay.cloud.iaas.imagemanagement.jbehave;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Test settings.
 */
public final class Settings {

    private static final String PROPERTIES_FILE = "/jbehave.properties";

    private static final String ROOT = Settings.class.getPackage().getName();
    private static final String INSTALL_SERVICE_HOST = ROOT + ".installServiceHost";
    private static final String SERVICE_BASE_ADDRESS = ROOT + ".serviceBaseAddress";
    private static final String TEST_HELPER_BASE_ADDRESS = ROOT + ".testHelperBaseAddress";
    private static final String PLAIN_CONF_BASE_ADDR = ROOT + ".plainConfBaseAddr";
    private static final String PARAM_CONF_BASE_ADDR = ROOT + ".paramConfBaseAddr";
    private static final String AUTHORIZATION_ENABLED = ROOT + ".authorizationEnabled";

    private static Settings instance;

    private final String installServiceHost;
    private final String installServiceURL;
    private final URI serviceBaseAddress;
    private final URI testHelperBaseAddress;
    private final String plainConfBaseAddr;
    private final String paramConfBaseAddr;
    private final boolean authorizationEnabled;

    private Settings(Properties properties) {
        installServiceHost = getProperty(properties, INSTALL_SERVICE_HOST);
        installServiceURL = "http://" + installServiceHost;
        plainConfBaseAddr = getProperty(properties, PLAIN_CONF_BASE_ADDR);
        paramConfBaseAddr = getProperty(properties, PARAM_CONF_BASE_ADDR);
        authorizationEnabled = Boolean.parseBoolean(getProperty(properties, AUTHORIZATION_ENABLED));

        try {
            serviceBaseAddress = new URI(getProperty(properties, SERVICE_BASE_ADDRESS));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid service base address URL", e);
        }
        try {
            testHelperBaseAddress = new URI(getProperty(properties, TEST_HELPER_BASE_ADDRESS));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid test helper base address URL", e);
        }
    }

    public static Boolean getAuthorizationEnabled() {
        return getInstance().authorizationEnabled;
    }

    public static URI getServiceBaseAddress() {
        return getInstance().serviceBaseAddress;
    }

    public static URI getTestHelperBaseAddress() {
        return getInstance().testHelperBaseAddress;
    }

    public static String getInstallServiceHost() {
        return getInstance().installServiceHost;
    }

    public static String getInstallServiceURL() {
        return getInstance().installServiceURL;
    }

    public static String getPlainConfBaseAddr() {
        return getInstance().plainConfBaseAddr;
    }

    public static String getParamConfBaseAddr() {
        return getInstance().paramConfBaseAddr;
    }

    private static Settings getInstance() {

        if (instance == null) {
            instance = loadInstance();
        }
        return instance;
    }

    private static String getProperty(Properties properties, String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            throw new RuntimeException("The property '" + key + "' is not specified.");
        }

        return value;
    }

    public static String getInstallServicePort() {
        URI url = URI.create(getPlainConfBaseAddr());

        return url.getPort() == -1 ? "80" : Integer.toString(url.getPort());
    }

    private static Settings loadInstance() {
        Properties properties = new Properties();
        InputStream stream = Settings.class.getResourceAsStream(PROPERTIES_FILE);

        try {
            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Settings(properties);
    }
}
