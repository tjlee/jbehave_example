package com.ebay.cloud.iaas.imagemanagement.jbehave.steps;

import com.ebay.cloud.iaas.imagemanagement.service.InstallFile;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.io.IOUtils;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ebay.cloud.iaas.imagemanagement.service.ImageManagementConstants.ALL_PROPERTIES_TXT_URL_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class InstallServiceSteps {

    // When steps

    @When("I make GET request using URL returned in install config for file type \"$fileType\"")
    public void getFileFromInstallService(final String fileType) throws IOException {

        Utils.get(getFileUrlByType(fileType));
    }

    @When("I make GET request using URL returned in install config for all-properties")
    public void getAllPropFromInstallConf() throws IOException {

        Utils.get(URI.create(State.installConfigResult.properties.get(ALL_PROPERTIES_TXT_URL_PROPERTY)));
    }

    @When("I create $filePath in os-profile storage with contents:$content")
    public void createFileWithContents(String filePath, String content) {
        State.getTestHelper().putOSProfileFile(filePath, IOUtils.toInputStream(content));
    }

    // Then steps

    @Then("I should receive HTTP status $status with content: $contents")
    @Alias("I should receive http status $status with content: $contents")
    public void validateHttpStatusAndContent(int status, String contents) {

        assertThat("HTTP status", State.httpStatus, is(status));
        assertThat("HTTP content", State.httpContent, is(Utils.substituteSettings(contents)));
    }

    @Then("I should receive HTTP status $status")
    public void validateHttpStatus(int status) {

        assertThat("HTTP status", State.httpStatus, is(status));
    }

    @Then("HTTP response should contain links for files:$files")
    public void validateResponseForFiles(List<String> fileTypes) {
        for (String file : fileTypes) {
            Pattern pattern = Pattern.compile("<a href\\=\\\"\\w+\\\">" + file + "</a>");
            Matcher matcher = pattern.matcher(State.httpContent);
            while (matcher.find()) {
                assertThat(
                        "file \"" + file + "\"",
                        matcher.group(), is("<a href=\"" + file + "\">" + file + "</a>"));
            }
        }
    }

    @Then("I should receive HTTP status $status with content where \"$placeholder\" is equal to the URL of the \"$fileType\" file: $contents")
    public void validateHttpStatusAndContent(int status, String placeholder, String fileType, String contents) {

        contents = contents.replace(placeholder, getFileUrlByType(fileType).toString());

        validateHttpStatusAndContent(status, contents);
    }

    private URI getFileUrlByType(final String fileType) {
        return Iterables.find(
                State.installConfigResult.files,

                new Predicate<InstallFile>() {
                    @Override
                    public boolean apply(InstallFile input) {
                        return Objects.equal(input.type, fileType);
                    }
                }).url;
    }
}
