Story: Asynchronous operations

Scenario: Asynchronous set-state operation. Workflow

Given I PUT predefined hw-profiles SUN_510
And I PUT predefined os-profile SEARCH_AGG/V2

When I make asynchronous change state of the os-profile SEARCH_AGG/V2 to QA
Then os-profile state should be changed to QA
And asynchronous state should be COMPLETED


Scenario: Asynchronous set-state operation task. Failed task state

When I changing elements values in XML file for os-profile SEARCH_AGG/V2
And I make asynchronous change state of the os-profile SEARCH_AGG/V2 to BLESSED
Then asynchronous state should be FAILED

Scenario: Asynchronous set-state operation task. href availability

Given I PUT predefined hw-profiles SUN_280
And I PUT predefined os-profile SEARCH_AGG/V1

When I make asynchronous change state with href call for os-profile SEARCH_AGG/V1 to QA
Then os-profile state should be changed to QA
And asynchronous state should be COMPLETED



