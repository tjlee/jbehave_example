Story: hw-profiles list search

Scenario: Search for all hw-profiles

Given I PUT predefined hw-profiles SUN_510, SUN_610, SUN_210

When I search for list of all hw-profiles

Then I should find 3 hw-profiles:
| profileName | os      | architecture |
| SUN_510     | Windows | x32          |
| SUN_610     | Windows | x64          |
| SUN_210     | Ubuntu  | x64          |
