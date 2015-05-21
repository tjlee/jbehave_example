Story: OS Profile Checksum

Scenario: OS Profile Checksum validation with corrupted files on QA to BLESSED state change step

Given I PUT predefined hw-profiles SUN_280, SUN_510
And I PUT predefined os-profile SEARCH_AGG/V1

When I change state of the os-profile SEARCH_AGG/V1 to QA
Then os-profile state should be changed to QA

When I create SEARCH_AGG/V1/boot/initrd in os-profile storage with contents:
;some changed content for checksum verification

When I change state of the os-profile SEARCH_AGG/V1 to BLESSED
Then asynchronous state should be FAILED

!--

Scenario: OS profile xml file checksum validation. Adding comments and elements order changing into xml.

!--Given I authenticate as Administrator
Given I PUT predefined os-profile SEARCH_AGG/V2

When I change state of the os-profile SEARCH_AGG/V2 to QA
Then os-profile state should be changed to QA

When I adding comments and changing elements order in XML file for os-profile SEARCH_AGG/V2

When I change state of the os-profile SEARCH_AGG/V2 to BLESSED
Then os-profile state should be changed to BLESSED

!--

Scenario: OS profile xml file checksum validation. Changing elements values in xml.

Given I PUT predefined os-profile Test_Profile/V3

When I change state of the os-profile Test_Profile/V3 to QA
Then os-profile state should be changed to QA

When I changing elements values in XML file for os-profile Test_Profile/V3

When I change state of the os-profile Test_Profile/V3 to BLESSED
Then asynchronous state should be FAILED

!--

Scenario: Explicit checksum validation

Given I PUT predefined os-profile SEARCH_AGG/V3

When I change state of the os-profile SEARCH_AGG/V3 to QA
Then os-profile state should be changed to QA

When I want verify checksum explicitly for os-profile SEARCH_AGG/V3
Then checksum validation should be successful

When I changing elements values in XML file for os-profile SEARCH_AGG/V3

When I want verify checksum explicitly for os-profile SEARCH_AGG/V3
Then checksum validation should failed
