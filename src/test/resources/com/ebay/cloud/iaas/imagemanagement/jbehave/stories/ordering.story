Story: OS profiles search ordering
Scenario: Searching OS profiles

Given I PUT predefined hw-profiles SUN_280, SUN_510
And I PUT predefined os-profile SEARCH_AGG/V1
And I PUT predefined os-profile SEARCH_AGG/V2
And I PUT predefined os-profile Test_Profile/V3

When I search for list of all os-profiles
Then I should find the following os-profiles in the following order:
| profileName  | profileVersion |
| Test_Profile | V3             |
| SEARCH_AGG   | V2             |
| SEARCH_AGG   | V1             |

When I change state of the os-profile SEARCH_AGG/V2 to QA
And I search for list of all os-profiles
Then I should find the following os-profiles in the following order:
| profileName  | profileVersion |
| SEARCH_AGG   | V2             |
| Test_Profile | V3             |
| SEARCH_AGG   | V1             |

When I search os-profiles with os='Ubuntu' OR os='Windows'
Then I should find the following os-profiles in the following order:
| profileName  | profileVersion |
| SEARCH_AGG   | V2             |
| Test_Profile | V3             |
| SEARCH_AGG   | V1             |

When I change state of the os-profile SEARCH_AGG/V1 to QA
And I search os-profiles with os='Ubuntu' OR os='Windows'
Then I should find the following os-profiles in the following order:
| profileName  | profileVersion |
| SEARCH_AGG   | V1             |
| SEARCH_AGG   | V2             |
| Test_Profile | V3             |
