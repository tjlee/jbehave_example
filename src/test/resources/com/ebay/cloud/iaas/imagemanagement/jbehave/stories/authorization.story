Story: As a user, I need the ability to authenticate and authorize users of the Image Management service so that
we can prevent unauthorized access to the system.

Scenario: Authorization story

Given I authenticate as Administrator
Given I PUT predefined hw-profiles SUN_280, SUN_510
And I PUT predefined os-profile SEARCH_AGG/V1

When I log out
And I log in as User

When I PUT hw-profile SUN_610 with properties:
| name         | value                       |
| os           | Ubuntu                      |
| architecture | x64                         |
| description  | SUN_610 profile description |

Then I should receive HTTP status 403

When I filled os-profile with properties:
| name                 | value                                  |
| boot-type            | pxe                                    |
| install-type         | preseed                                |
| os                   | Ubuntu                                 |
| architecture         | x64                                    |
| description          | Search Agg1 OS Profile                 |
| kernel-boot-location | os-profiles/SEARCH_AGG/V1/boot/vmlinuz |
| initrd-boot-location | os-profiles/SEARCH_AGG/V1/boot/initrd  |

And I PUT filled os-profile SEARCH_AGG/V1 with files:
| path           | type           | template |
| boot/pxeconfig | pxeconfig      | true     |
| boot/initrd    | initrd         | false    |
| boot/vmlinuz   | kernel         | false    |
| boot/preseed   | preseed-config | true     |
| boot/test      | test           | false    |

Then I should receive HTTP status 403

When I change state of the os-profile SEARCH_AGG/V1 to QA
Then I should receive HTTP status 403


When I POST install config properties to the OS profile:
| name       | value   |
| region     | lvs     |
| hw-profile | SUN_280 |
| TZ         | PST     |
Then I should receive HTTP status 403


When I search for list of all os-profiles
Then I should find 0 os-profiles:
| profileName  | profileOS | profileArch | profileVersion |

When I GET an hw-profile named SUN_510
Then I should receive HTTP status 403

When I GET an os-profile named SEARCH_AGG version V1
Then I should receive HTTP status 403

When I want to DELETE os-profile SEARCH_AGG/V1
Then I should receive HTTP status 403

When I want to DELETE os-profile SEARCH_AGG/V2
Then I should receive HTTP status 403

When I log out
And I log in as Administrator

When I want to DELETE os-profile SEARCH_AGG/V1
Then I should ensure that os-profile was deleted

When I want to DELETE hw-profile named SUN_510
Then I should ensure that hw-profile was deleted

When I want to DELETE hw-profile named SUN_280
Then I should ensure that hw-profile was deleted