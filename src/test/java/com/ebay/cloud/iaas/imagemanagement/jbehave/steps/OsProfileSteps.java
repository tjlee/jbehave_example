package com.ebay.cloud.iaas.imagemanagement.jbehave.steps;

import com.ebay.cloud.iaas.imagemanagement.service.*;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.apache.cxf.jaxrs.client.WebClient;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class OsProfileSteps {

    // Given steps

    @Given("I PUT predefined os-profile $osProfileName/$osProfileVersion")
    public void putPredefinedOSProfile(String osProfileName, String osProfileVersion) throws IOException {

        Utils.createOSProfileFilesFromResources(osProfileName, osProfileVersion);

        Iterable<String> hwProfiles = Utils.readOSProfileHWProfilesFromResources(osProfileName, osProfileVersion);
        Map<String, String> properties = Utils.readOSProfilePropertiesFromResources(osProfileName, osProfileVersion);
        Iterable<OSProfileFile> files = Utils.readOSProfileFilesFromResources(osProfileName, osProfileVersion);

        OSProfile profile = OSProfile.make(null, null, null, null, null, hwProfiles, properties, files);

        State.getConsumer().putOSProfile(osProfileName, osProfileVersion, profile);

        State.osProfileName = osProfileName;
        State.osProfileVersion = osProfileVersion;
    }

    // When steps

    @When("I GET an os-profile named $osProfile version $version")
    public void getOSProfile(String name, String version) {
        try {
            State.foundOSProfiles.add(
                    State.getConsumer().getOSProfile(name, version));
        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }
    }

    @When("I change state of the os-profile $name/$version to $newState")
    public void changeOSProfileState(String name, String version, OSProfileState newState) throws InterruptedException {
        State.osProfileName = name;
        State.osProfileVersion = version;

        try {

            State.operation = State.getConsumer().setOSProfileState(name, version, newState);

            Utils.awaitAsyncOperation();

        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }
    }

    @SuppressWarnings({"unchecked"})
    @When("I POST install config properties to the OS profile: $properties")
    public void postInstallConfigPropertiesToOSProfile(MultivaluedMap properties) throws InterruptedException {

        try {
            State.operation = State.getConsumer().makeInstallConf(
                    State.osProfileName,
                    State.osProfileVersion,
                    properties);

            Utils.awaitAsyncOperation();

            State.installConfigResult = ((InstallConfTask) State.operation).getResult();
        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }
    }

    @SuppressWarnings({"unchecked"})
    @When("I filled os-profile with properties: $properties")
    public void fillOSProfileWithProperties(Map osProfileProperties) {

        State.tempMap = osProfileProperties;
    }

    @When("I PUT filled os-profile $osProfileName/$osProfileVersion with files:$files")
    public void putFilledOSProfile(String osProfileName, String osProfileVersion, ExamplesTable filesTable) throws
            IOException {
        Iterable<OSProfileFile> files = Utils.readOSProfileFilesFromTable(filesTable);

        Iterable<String> hwProfiles = Utils.readOSProfileHWProfilesFromResources(osProfileName, osProfileVersion);

        OSProfile profile = OSProfile.make(
                osProfileName, osProfileVersion, null, null, null, hwProfiles, State.tempMap, files);

        try {
            State.getConsumer().putOSProfile(osProfileName, osProfileVersion, profile);
        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }

        State.osProfileName = osProfileName;
        State.osProfileVersion = osProfileVersion;
    }

    @When("I want to DELETE os-profile $osProfileName/$osProfileVersion")
    public void deleteOSProfile(String osProfileName, String osProfileVersion) {

        try {
            State.getConsumer().deleteOSProfile(osProfileName, osProfileVersion);
        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }

        State.osProfileName = osProfileName;
        State.osProfileVersion = osProfileVersion;
    }

    @When("I adding comments and changing elements order in XML file for os-profile $osProfileName/$osProfileVersion")
    public void changeXmlCommentsOrder(String name, String version) throws Exception {
        InputStream osProfileXmlStream = getOSProfileAsStream(name, version);
        InputStream changedXmlStream = Utils.getChangedCommentAndOrderInXmlSteam(osProfileXmlStream);

        State.getTestHelper().putOSProfileMetadata(name + "/" + version, changedXmlStream);
    }

    @When("I changing elements values in XML file for os-profile $osProfileName/$osProfileVersion")
    public void changeXmlElementsValue(String name, String version) throws Exception {
        InputStream osProfileXmlStream = getOSProfileAsStream(name, version);
        InputStream changedXmlStream = Utils.getChangedElementValueInXmlSteam(osProfileXmlStream);

        State.getTestHelper().putOSProfileMetadata(name + "/" + version, changedXmlStream);
    }

    @When("I want verify checksum explicitly for os-profile $osProfileName/$osProfileVersion")
    public void explicitChecksumVerification(String name, String version) throws InterruptedException {

        State.operation = State.getConsumer().verifyOSProfile(name, version);

        Utils.awaitAsyncOperation();

        State.isInvalid = ((VerifyTask) State.operation).getResult().invalid;
    }

    @When("I want to clone os-profile $osProfileName/$osProfileVersion to os-profile with version $osProfileNewVersion")
    public void cloneOSProfile(String osProfileName, String osProfileVersion, String newOSProfileVersion) {

        try {
            State.osProfileName = osProfileName;
            State.osProfileVersion = newOSProfileVersion;

            State.getConsumer().cloneOSProfile(osProfileName, osProfileVersion, newOSProfileVersion, false);
        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }
    }

    @When("I want to clone corrupted os-profile $osProfileName/$osProfileVersion to os-profile with version $osProfileNewVersion")
    public void cloneCorruptedOSProfile(String osProfileName, String osProfileVersion, String newOSProfileVersion) {

        try {
            State.osProfileName = osProfileName;
            State.osProfileVersion = newOSProfileVersion;

            State.getConsumer().cloneOSProfile(osProfileName, osProfileVersion, newOSProfileVersion, true);
        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }
    }

    // Then steps

    @Then("os-profile should be registered successfully")
    public void validateNoError() {
        // Not 4XX http status code
        assertThat("httpStatus", State.httpStatus, is(0));
    }

    @Then("os-profile state should be changed to $state")
    public void validateOSProfileState(OSProfileState state) {

        OSProfile profile = State.getConsumer().getOSProfile(State.osProfileName, State.osProfileVersion);

        assertThat("profile.state", profile.state, is(state));
    }

    @Then("os-profile state should not be changed and remain $state")
    public void validateOSProfileStateDoesNotChanged(OSProfileState remainState) {
        // if setStateTask if FAILED instantly (not allowed operation) then id = null
        if (State.operation.id != null) {
            assertThat("task.state", State.getConsumer().getTask(State.operation.id).state, is(TaskState.FAILED));
        } else {
            assertThat("task.state", State.operation.state, is(TaskState.FAILED));
        }

        OSProfile profile = State.getConsumer().getOSProfile(State.osProfileName, State.osProfileVersion);
        assertThat("profile.state", profile.state, is(remainState));
    }

    @Then("received os-profile should have file types: $fileTypes")
    public void validateOSProfileFileTypes(List<String> fileTypes) {

        Collection<String> actual = Collections2.transform(
                State.foundOSProfiles.get(0).files,

                new Function<OSProfileFile, String>() {
                    @Override
                    public String apply(OSProfileFile input) {
                        return input.type;
                    }
                }
        );

        assertThat(
                "foundOSProfiles[0].files.types",
                Sets.newTreeSet(actual),
                is(Sets.newTreeSet(fileTypes)));
    }

    @SuppressWarnings({"unchecked"})
    @Then("received install config should have properties: $properties")
    @Alias("received property list has to contain: $properties")
    public void validateInstallConfigProperties(Map properties) {

        Utils.compareProperties(
                properties,
                State.installConfigResult.properties);
    }

    @Then("received install config should have file types: $fileTypes")
    public void validateInstallConfigFileTypes(List<String> fileTypes) {

        Collection<String> actual = Collections2.transform(
                State.installConfigResult.files,

                new Function<InstallFile, String>() {
                    @Override
                    public String apply(InstallFile input) {
                        return input.type;
                    }
                }
        );

        assertThat(
                "installConfig.files.types",
                Sets.newTreeSet(actual),
                is(Sets.newTreeSet(fileTypes)));
    }

    @Then("I should receive os-profile with properties: $properties")
    public void validateOSProfileProperties(Map properties) {

        Utils.compareProperties(
                properties,
                State.foundOSProfiles.get(0).properties);
    }

    @Then("I should ensure that os-profile was deleted")
    public void validateOSProfileDeleted() {
        try {
            OSProfile profile = State.getConsumer().getOSProfile(State.osProfileName, State.osProfileVersion);
        } catch (WebApplicationException e) {
            assertThat("httpStatus", e.getResponse().getStatus(), is(NOT_FOUND.getStatusCode()));
        }
    }

    @Then("checksum validation should be successful")
    public void validateChecksumIsSuccessful() {
        assertThat("invalid", State.isInvalid, is(false));
    }

    @Then("checksum validation should failed")
    public void validateChecksumIsFailed() {
        assertThat("invalid", State.isInvalid, is(true));
    }

    @Then("invalid flag for os-profiles should be set to $isInvalid")
    public void validateInvalidFlag(String isInvalid) {
        OSProfile profile = State.getConsumer().getOSProfile(State.osProfileName, State.osProfileVersion);
        assertThat("isInvalid", profile.invalid, is(Boolean.parseBoolean(isInvalid)));
    }

    private InputStream getOSProfileAsStream(String name, String version) {

        WebClient client = WebClient.fromClient(WebClient.client(State.getConsumer()), true).
                path(ImageManagementService.OS_PROFILE_PATH, name, version);

        javax.ws.rs.core.Response response = client.get();
        if (response.getStatus() != OK.getStatusCode()) {
            throw new WebApplicationException(response);
        }

        return (InputStream) response.getEntity();
    }
}


