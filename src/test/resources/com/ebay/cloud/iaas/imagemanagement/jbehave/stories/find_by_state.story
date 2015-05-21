Story: OS profile search by state

Scenario: OS profile search by state

Given I PUT predefined hw-profiles SUN_280, SUN_510
And I PUT predefined os-profile SEARCH_AGG/V1
And I PUT predefined os-profile SEARCH_AGG/V2
And I PUT predefined os-profile Test_Profile/V3

When I change state of the os-profile SEARCH_AGG/V2 to QA
Then os-profile state should be changed to QA

When I search os-profiles with state='DRAFT'
Then I should find 2 os-profiles that have the properties:
| name    | value  |
| state   | DRAFT  |

When I search os-profiles with state<>'DRAFT'
Then I should find 1 os-profiles that have the properties:
| name    | value  |
| state   | QA     |

When I change state of the os-profile SEARCH_AGG/V1 to QA
Then os-profile state should be changed to QA

When I change state of the os-profile SEARCH_AGG/V2 to BLESSED
Then os-profile state should be changed to BLESSED

When I search os-profiles with state='QA'
Then I should find 1 os-profiles that have the properties:
| name    | value |
| state   | QA    |

When I change state of the os-profile SEARCH_AGG/V1 to BLESSED
Then os-profile state should be changed to BLESSED

When I change state of the os-profile Test_Profile/V3 to QA
Then os-profile state should be changed to QA

When I search os-profiles with state='DRAFT'
Then I should find 0 os-profiles that have the properties:
| name    | value    |

