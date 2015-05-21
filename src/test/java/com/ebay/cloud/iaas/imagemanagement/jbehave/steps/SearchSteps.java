package com.ebay.cloud.iaas.imagemanagement.jbehave.steps;

import com.ebay.cloud.iaas.imagemanagement.service.*;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ebay.cloud.iaas.imagemanagement.TestUtils.hasField;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class SearchSteps {

    // When steps

    @When("I search os-profiles with $query")
    public void searchOSProfile(String query) {
        State.foundOSProfiles.clear();
        State.foundOSProfileRef.clear();

        if (State.modified != null) {
            query = query.replace("_modified_", State.modified);
        }

        OSProfiles osProfiles = State.getConsumer().findOSProfiles(query);

        for (OSProfileRef osProfileRef : osProfiles.osProfileRefs) {
            State.foundOSProfileRef.add(osProfileRef);
            State.foundOSProfiles.add(
                    State.getConsumer().getOSProfile(
                            osProfileRef.name,
                            osProfileRef.version));
        }

    }

    @When("I search for list of all os-profiles")
    public void searchForListOfOSProfiles() {
        findOSProfiles(null);
    }

    @When("I search for list of all os-profiles named $name")
    public void searchForListOfOSProfiles(String name) {
        findOSProfiles(name);
    }

    @When("I store modification date of the $number os-profile in search result")
    public void storeModificationDateInSearchResult(int number) {
        State.modified = Utils.ISO_8601.format(
                State.foundOSProfileRef.get(number - 1).modified.getTime());
    }

    @When("I search hw-profiles with $query")
    public void searchHWProfile(String query) {
        State.foundHWProfiles = State.getConsumer().findHWProfiles(query);
    }

    @When("I search for list of all hw-profiles")
    public void searchForListOfHWProfiles() {
        State.foundHWProfiles = State.getConsumer().listHWProfiles();
    }

    @When("I search for list of os-profiles grouped by name")
    public void searchForOSProfilesGrouped() {
        OSProfileGroups osProfilesGroups = State.getConsumer().getOSProfileGroups();
        for (OSProfileGroup osProfileGroup : osProfilesGroups.osProfileGroups) {
            State.foundOSProfileGroups.add(osProfileGroup);
        }
    }

    // Then steps

    @Then("I should find $amount os-profiles:$profileTable")
    @Alias("I should find $amount os-profile:$profileTable")
    public void foundOsProfiles(int amount, ExamplesTable profileTable) {
        assertThat("foundOSProfileRef.size", State.foundOSProfileRef.size(), is(amount));

        Set<OSProfile> expectedOSProfiles = Sets.newHashSet(Utils.convertToOSProfiles(profileTable));

        Set<OSProfile> actualOSProfiles = Sets.newHashSet(
                Utils.transformFoundOSProfiles(Utils.retrieveOSProfilesByRefs(State.foundOSProfileRef)));

        assertThat("OS profiles", actualOSProfiles, is(expectedOSProfiles));
    }

    @Then("I should find $amount hw-profiles:$hwProfileTable")
    @Alias("I should find $amount hw-profile:$hwProfileTable")
    public void foundHWProfiles(int amount, ExamplesTable profileTable) {
        assertThat("foundHWProfiles.amount", State.foundHWProfiles.hwProfileRefs.size(), is(amount));

        Map<String, HWProfileRef> refMap = Maps.uniqueIndex(
                State.foundHWProfiles.hwProfileRefs,
                new Function<HWProfileRef, String>() {
                    @Override
                    public String apply(HWProfileRef input) {
                        return input.name;
                    }
                });

        Map<String, HWProfile> profileMap = Maps.transformValues(
                refMap,
                new Function<HWProfileRef, HWProfile>() {
                    @Override
                    public HWProfile apply(HWProfileRef input) {
                        return State.getConsumer().getHWProfile(input.name);
                    }
                });

        for (Map<String, String> row : profileTable.getRows()) {
            String name = row.get("profileName");
            HWProfile profile = profileMap.get(name);
            assertThat("foundHWProfiles[" + name + "]", profile, notNullValue());

            for (String key : row.keySet()) {
                if ("profileName".equals(key)) {
                    continue;
                }

                String value = profile.properties.get(key);
                assertThat("foundHWProfiles[" + name + "]." + key, value, is(row.get(key)));
            }
        }
    }

    @Then("I should find $amount os-profiles that have the properties:$propTable")
    @Alias("I should find $amount os-profile that have the properties:$propTable")
    public void foundOsProfilesWithProperty(int amount, ExamplesTable properties) {
        assertThat("foundOSProfiles.size", State.foundOSProfiles.size(), is(amount));
        for (Map<String, String> row : properties.getRows()) {
            for (OSProfile foundOSProfile : State.foundOSProfiles) {
                assertThat("foundOSProfile.properties.size",
                        properties.getRows().size() <= foundOSProfile.properties.size(), is(true));
                for (Map.Entry<String, String> entry : foundOSProfile.properties.entrySet()) {
                    if (entry.getKey().equals(row.get("name"))) {
                        assertThat("entry.getValue", entry.getValue(), is(row.get("value")));
                    }
                }
            }
        }
    }

    @Then("I should find os-profiles grouped by name:$profiles")
    public void foundOSProfilesGrouped(ExamplesTable profilesTable) {
        assertThat("foundOSProfileGroups.size", State.foundOSProfileGroups.size(), is(profilesTable.getRows().size()));
        int k = 0;
        for (Map<String, String> row : profilesTable.getRows()) {
            for (OSProfileGroup osProfileGroup : State.foundOSProfileGroups) {
                if (osProfileGroup.name.equals(row.get("profileName"))) {
                    k = k + 1;
                }
            }
        }
        assertThat("foundOSProfileGroups.size", State.foundOSProfileGroups.size(), is(k));
    }

    private void findOSProfiles(String name) {
        State.foundOSProfileRef.clear();
        OSProfiles osProfiles;
        if (name == null) {
            osProfiles = State.getConsumer().findOSProfiles(null);
        } else {
            osProfiles = State.getConsumer().findOSProfiles(name, null);
        }

        for (OSProfileRef osProfileRef : osProfiles.osProfileRefs) {
            State.foundOSProfileRef.add(osProfileRef);
        }
    }

    @Then("I should find the following os-profiles in the following order:$profilesTable")
    public void validateFoundOSProfileOrder(ExamplesTable profilesTable) {
        List<Map<String, String>> rows = profilesTable.getRows();
        assertThat("foundOSProfileRef.size", State.foundOSProfileRef.size(), is(rows.size()));
        for (int i = 0; i < rows.size(); i++) {
            assertThat(
                    "foundOSProfilesRef[" + i + "]",
                    State.foundOSProfileRef.get(i),
                    allOf(hasField("name", is(rows.get(i).get("profileName"))),
                            hasField("version", is(rows.get(i).get("profileVersion")))));
        }
    }

}
