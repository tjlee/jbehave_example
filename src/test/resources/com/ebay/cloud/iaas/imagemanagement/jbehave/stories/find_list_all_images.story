Story: OS profiles list search

Scenario: Find all OS profiles (names and versions list)

Given I PUT predefined hw-profiles SUN_280, SUN_510
And I PUT predefined os-profile SEARCH_AGG/V1
And I PUT predefined os-profile SEARCH_AGG/V2
And I PUT predefined os-profile Test_Profile/V3

When I search for list of all os-profiles
Then I should find 3 os-profiles:
| profileName  | profileOS | profileArch | profileVersion |
| SEARCH_AGG   | Ubuntu    | x64         | V1             |
| SEARCH_AGG   | Ubuntu    | x64         | V2             |
| Test_Profile | Windows   | x32         | V3             |


Scenario: Find named OS profiles list

When I search for list of all os-profiles named SEARCH_AGG
Then I should find 2 os-profiles:
| profileName  | profileOS | profileArch | profileVersion |
| SEARCH_AGG   | Ubuntu    | x64         | V1             |
| SEARCH_AGG   | Ubuntu    | x64         | V2             |



Scenario: Find all OS profiles (names only)

When I search for list of os-profiles grouped by name
Then I should find os-profiles grouped by name:
| profileName  |
| SEARCH_AGG   |
| Test_Profile |
