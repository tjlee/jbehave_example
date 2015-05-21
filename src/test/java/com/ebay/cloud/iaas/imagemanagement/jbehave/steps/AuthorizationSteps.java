package com.ebay.cloud.iaas.imagemanagement.jbehave.steps;

import org.jbehave.core.annotations.*;

public class AuthorizationSteps {

    @Given("I authenticate as $login")
    public void authAsAdmin(String login) throws Exception {

        logInAs(login);
    }

    @When("I log out")
    public void logOut() throws Exception {

        State.currentAuthToken = null;
    }

    @When("I log in as $login")
    public void logInAs(String login) throws Exception {

        State.currentAuthToken = Utils.getAuthTokenForUser(login);
    }
}
