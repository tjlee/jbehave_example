Story: hw-profiles search

Scenario: Search hw-profile specifying AND expression

Given I PUT predefined hw-profiles SUN_510, SUN_220, SUN_610, SUN_210

When I search hw-profiles with os='Windows' AND architecture='x32'
Then I should find 1 hw-profile:
| profileName | os      | architecture |
| SUN_510     | Windows | x32          |

!--

Scenario: Search hw-profile specifying OR expression

When I search hw-profiles with os='Ubuntu' OR os='Windows'

Then I should find 3 hw-profiles:
| profileName | os      | architecture |
| SUN_510     | Windows | x32          |
| SUN_610     | Windows | x64          |
| SUN_210     | Ubuntu  | x64          |

!--

Scenario: Search hw-profile specifying AND and OR expression

When I search hw-profiles with os='Ubuntu' AND (architecture='x32' OR architecture='x64')

Then I should find 1 hw-profile:
| profileName | os      | architecture |
| SUN_210     | Ubuntu  | x64          |

!--

Scenario: Search hw-profile specifying equals expression

When I search hw-profiles with architecture='x64'

Then I should find 2 hw-profiles:
| profileName | os      | architecture |
| SUN_210     | Ubuntu  | x64          |
| SUN_610     | Windows | x64          |

!--

Scenario: Search hw-profile specifying nonexistent expression

When I search hw-profiles with architecture='x32' AND os='Ubuntu'

Then I should find 0 hw-profiles:
| profileName | os      | architecture |

!--

Scenario: Search hw-profile specifying CONTAINS expression

When I search hw-profiles with os CONTAINS 'ind'

Then I should find 2 hw-profiles:
| profileName | os      | architecture |
| SUN_510     | Windows | x32          |
| SUN_610     | Windows | x64          |
