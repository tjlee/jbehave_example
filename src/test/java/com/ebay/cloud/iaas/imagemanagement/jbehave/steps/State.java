package com.ebay.cloud.iaas.imagemanagement.jbehave.steps;

import com.ebay.cloud.iaas.imagemanagement.jbehave.Settings;
import com.ebay.cloud.iaas.imagemanagement.service.*;
import com.ebay.soaframework.common.types.SOAHeaders;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;

import java.util.List;
import java.util.Map;

public final class State {

    public static String osProfileName;
    public static String osProfileVersion;

    public static InstallConfResult installConfigResult;

    public static int httpStatus;
    public static String httpContent;

    public static List<OSProfile> foundOSProfiles;
    public static List<OSProfileRef> foundOSProfileRef;
    public static List<OSProfileGroup> foundOSProfileGroups;

    public static String modified;

    public static HWProfiles foundHWProfiles;
    public static List<HWProfile> foundHWProfilesList;

    public static String hwProfileName;

    public static boolean isErrorOccurred;
    public static boolean isInvalid;

    public static Map<String, String> tempMap;

    public static String currentAuthToken;

    public static Task operation;

    public static ImageManagementService getConsumer() {

        final ImageManagementService consumer = JAXRSClientFactory.create(
                Settings.getServiceBaseAddress(),
                ImageManagementService.class);

        if (Settings.getAuthorizationEnabled()) {

            final String authToken = State.currentAuthToken == null ? Utils.getAuthTokenForAdmin() : State.currentAuthToken;
            WebClient.client(consumer).header(SOAHeaders.AUTH_IAFTOKEN, authToken);
        }

        return consumer;
    }

    public static ImageManagementServiceTestHelper getTestHelper() {

        return JAXRSClientFactory.create(Settings.getTestHelperBaseAddress(), ImageManagementServiceTestHelper.class);
    }
}
