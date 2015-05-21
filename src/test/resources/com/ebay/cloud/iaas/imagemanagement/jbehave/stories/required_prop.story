Story: As a System Administrator, I want the system to ensure that the required properties specified in the OS profile
so that I cannot submit an invalid OS profile

Scenario: Validate boot-type property

Given I PUT predefined hw-profiles SUN_510

When I filled os-profile with properties:
| name                 | value                          |
| install-type         | preseed                        |
| os                   | Ubuntu                         |
| architecture         | x64                            |
| description          | Search Agg2 OS Profile         |

And I PUT filled os-profile SEARCH_AGG/V2 with files:
| name           | type           | template |
| boot/pxeconfig | pxeconfig      | true     |
| boot/initrd    | initrd         | false    |
| boot/vmlinuz   | kernel         | false    |
| boot/preseed   | preseed-config | true     |

Then I should receive HTTP status 400


Scenario: Validate install-type property

When I filled os-profile with properties:
| name                 | value                          |
| boot-type            | pxe                            |
| os                   | Ubuntu                         |
| architecture         | x64                            |
| description          | Search Agg2 OS Profile         |

And I PUT filled os-profile SEARCH_AGG/V2 with files:
| name           | type           | template |
| boot/pxeconfig | pxeconfig      | true     |
| boot/initrd    | initrd         | false    |
| boot/vmlinuz   | kernel         | false    |
| boot/preseed   | preseed-config | true     |

Then I should receive HTTP status 400

Scenario: Validate hw-profiles property

When I filled os-profile with properties:
| name                 | value                          |
| boot-type            | pxe                            |
| install-type         | preseed                        |
| os                   | Ubuntu                         |
| architecture         | x64                            |
| description          | Search Agg2 OS Profile         |

And I PUT filled os-profile SEARCH_AGG/V2 with files:
| name           | type           | template |
| boot/pxeconfig | pxeconfig      | true     |
| boot/initrd    | initrd         | false    |
| boot/vmlinuz   | kernel         | false    |
| boot/preseed   | preseed-config | true     |

Then I should receive HTTP status 400

