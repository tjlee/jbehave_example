Story: Get hw-profile

Scenario: Get hw-profile by name

Given I PUT predefined hw-profiles SUN_210
When I GET an hw-profile named SUN_210

Then I should receive hw-profile with properties:
| name         | value                       |
| os           | Ubuntu                      |
| architecture | x64                         |
| description  | SUN_210 profile description |