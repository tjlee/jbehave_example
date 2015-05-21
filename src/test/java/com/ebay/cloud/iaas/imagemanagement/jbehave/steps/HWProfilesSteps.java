package com.ebay.cloud.iaas.imagemanagement.jbehave.steps;

import com.ebay.cloud.iaas.imagemanagement.service.HWProfile;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import javax.ws.rs.WebApplicationException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;


public class HWProfilesSteps {

    // Given steps

    @Given("I PUT predefined hw-profiles $hwProfileNames")
    public void putPredefinedHWProfiles(List<String> hwProfileNames) throws IOException {

        for (String hwProfileName : hwProfileNames) {
            putPredefinedHWProfile(hwProfileName);
        }
    }

    @Given("I PUT predefined hw-profile $hwProfileName")
    public void putPredefinedHWProfile(String hwProfileName) throws IOException {

        Map<String, String> properties = Utils.readHWProfilePropertiesFromResource(hwProfileName);

        HWProfile profile = HWProfile.make(null, properties);

        State.getConsumer().putHWProfile(hwProfileName, profile);
    }

    // When steps

    @SuppressWarnings({"unchecked"})
    @When("I PUT hw-profile $hwProfileName with properties: $properties")
    public void putHWProfileWithProperties(String hwProfileName, Map properties) {
        State.hwProfileName = hwProfileName;

        HWProfile profile = HWProfile.make(null, properties);

        // updating hw-profile
        try {
            State.getConsumer().putHWProfile(hwProfileName, profile);

            // for further comparison
            State.foundHWProfilesList.add(
                    State.getConsumer().getHWProfile(hwProfileName));
        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }


    }

    @When("I GET an hw-profile named $hwProfileName")
    public void getHWProfile(String hwProfileName) {
        try {
            State.foundHWProfilesList.add(
                    State.getConsumer().getHWProfile(hwProfileName));
        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }
    }

    @When("I want to DELETE hw-profile named $hwProfileName")
    public void deleteHWProfile(String hwProfileName) {

        State.hwProfileName = hwProfileName;

        try {
            State.getConsumer().deleteHWProfile(hwProfileName);
        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }

    }

    // Then steps

    @Then("I should receive hw-profile with properties:$hwProfileProperties")
    public void validateHWProfileProperties(Map hwProfileProperties) {

        Utils.compareProperties(
                hwProfileProperties,
                State.foundHWProfilesList.get(0).properties);

    }

    @Then("I should ensure that hw-profile was deleted")
    public void validateHWProfileDeleted() {
        try {
            HWProfile hwProfile = State.getConsumer().getHWProfile(State.hwProfileName);
        } catch (WebApplicationException e) {
            assertThat("httpStatus", e.getResponse().getStatus(), is(NOT_FOUND.getStatusCode()));
        }
    }

    @Then("I should ensure that error occurred")
    public void validateDeletionErrorOccurred() {

        assertThat("isErrorOccurred", State.isErrorOccurred, is(true));
    }
}
