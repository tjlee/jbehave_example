Story: As a System Administrator, I want the system to ensure that the properties specified in hw-profile
are correct so that I cannot submit an invalid hw-profile

Scenario: hw-profile should allow DELETE operation

Given I PUT predefined hw-profiles SUN_210
When I want to DELETE hw-profile named SUN_210
Then I should ensure that hw-profile was deleted

!--

Scenario: DELETE operation on nonexistent hw-profile

When I want to DELETE hw-profile named SUN_210
Then I should receive HTTP status 404

!--

Scenario: hw-profile should allow a PUT operation

Given I PUT predefined hw-profiles SUN_210

When I PUT hw-profile SUN_210 with properties:
| name         | value   |
| os           | Windows |
| install-type | preseed |

Then I should receive hw-profile with properties:
| name         | value   |
| os           | Windows |
| install-type | preseed |

!--


