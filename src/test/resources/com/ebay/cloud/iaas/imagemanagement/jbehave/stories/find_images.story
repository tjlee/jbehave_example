Story: OS profiles search
Scenario: Search OS profile specifying AND expression

Given I PUT predefined hw-profiles SUN_280, SUN_510
And I PUT predefined os-profile SEARCH_AGG/V1
And I PUT predefined os-profile SEARCH_AGG/V2
And I PUT predefined os-profile Test_Profile/V3

When I search os-profiles with os='Windows' AND architecture='x32'
Then I should find 1 os-profile:
| profileName  | profileOS | profileArch | profileVersion |
| Test_Profile | Windows   | x32         | V3             |

!--

Scenario: Search OS profile specifying OR expression

When I search os-profiles with os='Ubuntu' OR os='Windows'
Then I should find 3 os-profiles:
| profileName  | profileOS | profileArch | profileVersion |
| SEARCH_AGG   | Ubuntu    | x64         | V1             |
| SEARCH_AGG   | Ubuntu    | x64         | V2             |
| Test_Profile | Windows   | x32         | V3             |

!--

Scenario: Search OS profile specifying OR and AND expressions

When I search os-profiles with architecture='x32' AND (os = 'Ubuntu' OR os = 'Windows')
Then I should find 1 os-profile:
| profileName  | profileOS | profileArch | profileVersion |
| Test_Profile | Windows   | x32         | V3             |

!--

Scenario: Search OS profile specifying equals expression

When I search os-profiles with architecture='x64'
Then I should find 2 os-profiles:
| profileName | profileOS | profileArch | profileVersion |
| SEARCH_AGG  | Ubuntu    | x64         | V1             |
| SEARCH_AGG  | Ubuntu    | x64         | V2             |

!--

Scenario: Search OS profile specifying nonexistent search query

When I search os-profiles with architecture='x32' AND os='Ubuntu'
Then I should find 0 os-profiles:
| profileName | profileOS | profileArch | profileVersion |

!--

Scenario: Search OS profile specifying CONTAINS expression

When I search os-profiles with os CONTAINS 'unt'
Then I should find 2 os-profiles:
| profileName | profileOS | profileArch | profileVersion |
| SEARCH_AGG  | Ubuntu    | x64         | V1             |
| SEARCH_AGG  | Ubuntu    | x64         | V2             |

!--

Scenario: Search OS profile specifying virtual property

When I search os-profiles with os-profile-name='SEARCH_AGG'
Then I should find 2 os-profiles:
| profileName | profileOS | profileArch | profileVersion |
| SEARCH_AGG  | Ubuntu    | x64         | V1             |
| SEARCH_AGG  | Ubuntu    | x64         | V2             |

!--

Scenario: Search OS profile specifying virtual property with AND expression

When I search os-profiles with os-profile-name='SEARCH_AGG' AND os-profile-version='V1'
Then I should find 1 os-profiles:
| profileName | profileOS | profileArch | profileVersion |
| SEARCH_AGG  | Ubuntu    | x64         | V1             |

!--

Scenario: Search OS profile specifying '<', '>', '>=', '<=' expressions

When I search for list of all os-profiles
And I store modification date of the 2 os-profile in search result
And I search os-profiles with modified>='_modified_'
Then I should find 2 os-profiles:
| profileName  | profileOS | profileArch | profileVersion |
| SEARCH_AGG   | Ubuntu    | x64         | V2             |
| Test_Profile | Windows   | x32         | V3             |

When I search os-profiles with modified<'_modified_'
Then I should find 1 os-profiles:
| profileName  | profileOS | profileArch | profileVersion |
| SEARCH_AGG   | Ubuntu    | x64         | V1             |

When I search os-profiles with modified>'_modified_'
Then I should find 1 os-profiles:
| profileName  | profileOS | profileArch | profileVersion |
| Test_Profile | Windows   | x32         | V3             |

When I search os-profiles with modified<='_modified_'
Then I should find 2 os-profiles:
| profileName  | profileOS | profileArch | profileVersion |
| SEARCH_AGG   | Ubuntu    | x64         | V1             |
| SEARCH_AGG   | Ubuntu    | x64         | V2             |


