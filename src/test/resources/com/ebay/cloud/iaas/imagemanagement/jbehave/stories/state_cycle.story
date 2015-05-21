Story: State support for os-profile
Scenario: OS profile state cycle

Given I PUT predefined hw-profiles SUN_510
And I PUT predefined os-profile SEARCH_AGG/V2

!-- DRAFT -> DRAFT
When I change state of the os-profile SEARCH_AGG/V2 to DRAFT
Then os-profile state should be changed to DRAFT

!-- DRAFT -> BLESSED
When I change state of the os-profile SEARCH_AGG/V2 to BLESSED
Then os-profile state should not be changed and remain DRAFT

!-- DRAFT -> ARCHIVED
When I change state of the os-profile SEARCH_AGG/V2 to ARCHIVED
Then os-profile state should not be changed and remain DRAFT

!-- DRAFT -> QA
When I change state of the os-profile SEARCH_AGG/V2 to QA
Then os-profile state should be changed to QA

!-- QA -> DRAFT
When I change state of the os-profile SEARCH_AGG/V2 to DRAFT
Then os-profile state should be changed to DRAFT

!-- DRAFT -> QA
When I change state of the os-profile SEARCH_AGG/V2 to QA
Then os-profile state should be changed to QA

!-- QA -> QA
When I change state of the os-profile SEARCH_AGG/V2 to QA
Then os-profile state should be changed to QA

!-- QA -> ARCHIVED
When I change state of the os-profile SEARCH_AGG/V2 to ARCHIVED
Then os-profile state should not be changed and remain QA

!-- QA -> BLESSED
When I change state of the os-profile SEARCH_AGG/V2 to BLESSED
Then os-profile state should be changed to BLESSED

!-- BLESSED -> DRAFT
When I change state of the os-profile SEARCH_AGG/V2 to DRAFT
Then os-profile state should not be changed and remain BLESSED

!-- BLESSED -> QA
When I change state of the os-profile SEARCH_AGG/V2 to QA
Then os-profile state should not be changed and remain BLESSED

!-- BLESSED -> BLESSED
When I change state of the os-profile SEARCH_AGG/V2 to BLESSED
Then os-profile state should be changed to BLESSED

!-- BLESSED -> ARCHIVED
When I change state of the os-profile SEARCH_AGG/V2 to ARCHIVED
Then os-profile state should be changed to ARCHIVED

!-- ARCHIVED -> DRAFT
When I change state of the os-profile SEARCH_AGG/V2 to DRAFT
Then os-profile state should not be changed and remain ARCHIVED

!-- ARCHIVED -> QA
When I change state of the os-profile SEARCH_AGG/V2 to QA
Then os-profile state should not be changed and remain ARCHIVED

!-- ARCHIVED -> ARCHIVED
When I change state of the os-profile SEARCH_AGG/V2 to ARCHIVED
Then os-profile state should be changed to ARCHIVED

!-- ARCHIVED -> BLESSED
When I change state of the os-profile SEARCH_AGG/V2 to BLESSED
Then os-profile state should be changed to BLESSED
