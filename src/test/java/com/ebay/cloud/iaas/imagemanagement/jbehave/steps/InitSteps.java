package com.ebay.cloud.iaas.imagemanagement.jbehave.steps;

import com.ebay.cloud.iaas.imagemanagement.service.HWProfile;
import com.ebay.cloud.iaas.imagemanagement.service.OSProfile;
import com.ebay.cloud.iaas.imagemanagement.service.OSProfileGroup;
import com.ebay.cloud.iaas.imagemanagement.service.OSProfileRef;
import com.google.common.collect.Maps;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.BeforeStories;

import java.util.ArrayList;

public class InitSteps {

    @BeforeStories
    public void initialize() {

        State.getTestHelper().resetService();
    }

    @BeforeScenario
    public void beforeScenario() {

        State.foundOSProfiles = new ArrayList<OSProfile>();

        State.foundOSProfileRef = new ArrayList<OSProfileRef>();

        State.foundOSProfileGroups = new ArrayList<OSProfileGroup>();

        State.foundHWProfilesList = new ArrayList<HWProfile>();

        State.isErrorOccurred = false;

        State.isInvalid = false;

        State.tempMap = Maps.newHashMap();

        State.httpStatus = 0;
    }
}
