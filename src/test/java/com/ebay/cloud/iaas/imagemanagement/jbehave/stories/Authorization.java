package com.ebay.cloud.iaas.imagemanagement.jbehave.stories;

import com.ebay.cloud.iaas.imagemanagement.jbehave.ImageManagementStory;
import com.ebay.cloud.iaas.imagemanagement.jbehave.Settings;

public class Authorization extends ImageManagementStory {
    @Override
    public void run() throws Throwable {
        if (Settings.getAuthorizationEnabled()) {
            super.run();
        }
    }
}
