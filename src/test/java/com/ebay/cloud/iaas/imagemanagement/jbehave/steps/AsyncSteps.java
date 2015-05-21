package com.ebay.cloud.iaas.imagemanagement.jbehave.steps;

import com.ebay.cloud.iaas.imagemanagement.service.OSProfileState;
import com.ebay.cloud.iaas.imagemanagement.service.TaskState;
import org.hamcrest.Matchers;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AsyncSteps {

    @When("I make asynchronous change state of the os-profile $name/$version to $newState")
    public void asyncChangeOsProfileState(String name, String version, OSProfileState newState)
            throws InterruptedException {

        State.osProfileName = name;
        State.osProfileVersion = version;

        try {

            generateFilesIntoOsProfileDirectory(name, version);

            State.operation = State.getConsumer().setOSProfileState(name, version, newState);

            Utils.awaitAsyncOperation();

        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }
    }

    @When("I make asynchronous change state with href call for os-profile $name/$version to $newState")
    public void asyncChangeOsProfileStateUsingHref(String name, String version, OSProfileState newState)
            throws InterruptedException, IOException, JAXBException {

        State.osProfileName = name;
        State.osProfileVersion = version;

        try {

            generateFilesIntoOsProfileDirectory(name, version);

            State.operation = State.getConsumer().setOSProfileState(name, version, newState);
            Utils.awaitAsyncOperationUsingHref();

        } catch (WebApplicationException e) {
            State.httpStatus = e.getResponse().getStatus();
        }
    }

    @Then("asynchronous state should be $state")
    public void validateAsyncTaskState(TaskState state) {

        if (state == TaskState.FAILED) {
            assertThat("operation.error", State.operation.error, Matchers.notNullValue());
        }
        assertThat("operation.state", State.getConsumer().getTask(State.operation.id).state, is(state));
    }

    private void generateFilesIntoOsProfileDirectory(String name, String version) {
        byte[] bytes = new byte[1024];
        Random random = new Random();
        random.nextBytes(bytes);

        // putting files into os-profile/version/boot directory
        for (int i = 0; i < 1000; i++) {
            State.getTestHelper().putOSProfileFile(name + "/" + version + "/boot/" + i,
                    new ByteArrayInputStream(bytes));
        }
    }

}
