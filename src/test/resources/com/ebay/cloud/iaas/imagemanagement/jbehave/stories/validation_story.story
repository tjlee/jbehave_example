Story: As a System Administrator, I want the system to ensure that the properties specified in the OS profile
are correct so that I cannot submit an invalid OS profile


Scenario: Blessed OS profile should not allow a PUT operation

Given I PUT predefined hw-profiles SUN_280, SUN_510
And I PUT predefined os-profile SEARCH_AGG/V2

When I change state of the os-profile SEARCH_AGG/V2 to QA
Then os-profile state should be changed to QA
When I change state of the os-profile SEARCH_AGG/V2 to BLESSED
Then os-profile state should be changed to BLESSED

When I filled os-profile with properties:
| name                 | value                          |
| boot-type            | pxe                            |
| install-type         | preseed                        |
| os                   | Ubuntu                         |
| architecture         | x64                            |
| description          | Search Agg2 OS Profile         |
| kernel-boot-location | profiles/SEARCH_AGG/V2/vmlinuz |
| initrd-boot-location | profiles/SEARCH_AGG/V2/initrd  |

And I PUT filled os-profile SEARCH_AGG/V2 with files:
| path           | type           | template |
| boot/pxeconfig | pxeconfig      | true     |
| boot/initrd    | initrd         | false    |
| boot/vmlinuz   | kernel         | false    |
| boot/preseed   | preseed-config | true     |

Then I should receive HTTP status 405

!--

Scenario: OS profile hw-profiles key set on PUT validation


When I filled os-profile with properties:
| name                 | value                          |
| boot-type            | pxe                            |
| install-type         | preseed                        |
| os                   | Ubuntu                         |
| architecture         | x64                            |
| description          | Search Agg OS Profile          |
| hw-profiles          | SUN_28                         |
| hw-profiles          | SUN_51                         |
| kernel-boot-location | profiles/SEARCH_AGG/V2/vmlinuz |
| initrd-boot-location | profiles/SEARCH_AGG/V2/initrd  |

And I PUT filled os-profile SEARCH_AGG/V2 with files:

| name           | type           | template |
| boot/pxeconfig | pxeconfig      | true     |
| boot/initrd    | initrd         | false    |
| boot/vmlinuz   | kernel         | false    |
| boot/preseed   | preseed-config | true     |
| boot/test      | test           | false    |

Then I should receive HTTP status 400

!--

Scenario: OS profile files listed in files section on PUT existence validation

When I filled os-profile with properties:
| name                 | value                          |
| boot-type            | pxe                            |
| install-type         | preseed                        |
| os                   | Ubuntu                         |
| architecture         | x64                            |
| description          | Search Agg2 OS Profile         |
| kernel-boot-location | profiles/SEARCH_AGG/V2/vmlinuz |
| initrd-boot-location | profiles/SEARCH_AGG/V2/initrd  |

And I PUT filled os-profile SEARCH_AGG/V2 with files:
| name            | type           | template |
| boot/pxeconfig1 | pxeconfig      | true     |
| boot/initrd1    | initrd         | false    |
| boot/vmlinuz1   | kernel         | false    |
| boot/preseed1   | preseed-config | true     |
!-- this file doesn't exists
Then I should receive HTTP status 400

!--

Scenario: DRAFT os-profile should allow a PUT operation

Given I PUT predefined os-profile SEARCH_AGG/V1
When I filled os-profile with properties:
| name                 | value                          |
| boot-type            | pxe                            |
| install-type         | preseed                        |
| os                   | Ubuntu                         |
| architecture         | x64                            |
| description          | Search Agg2 OS Profile         |
| kernel-boot-location | profiles/SEARCH_AGG/V1/vmlinuz |
| initrd-boot-location | profiles/SEARCH_AGG/V1/initrd  |

And I PUT filled os-profile SEARCH_AGG/V1 with files:
| path           | type           | template|
| boot/pxeconfig | pxeconfig      | true    |
| boot/initrd    | initrd         | false   |
| boot/vmlinuz   | kernel         | false   |
| boot/preseed   | preseed-config | true    |
| boot/test      | test           | false   |

!--

Scenario: Registration with virtual property 'os-profile-name' should not be allowed

When I filled os-profile with properties:
| name                 | value                          |
| os-profile-name      | temp name                      |
| boot-type            | pxe                            |
| install-type         | preseed                        |
| os                   | Ubuntu                         |
| architecture         | x64                            |
| description          | Search Agg2 OS Profile         |
| kernel-boot-location | profiles/SEARCH_AGG/V1/vmlinuz |
| initrd-boot-location | profiles/SEARCH_AGG/V1/initrd  |

And I PUT filled os-profile SEARCH_AGG/V1 with files:
| path           | type           | template|
| boot/pxeconfig | pxeconfig      | true    |
| boot/initrd    | initrd         | false   |
| boot/vmlinuz   | kernel         | false   |
| boot/preseed   | preseed-config | true    |
| boot/test      | test           | false   |

Then I should receive HTTP status 400

!--

Scenario: Registration with virtual property 'os-profile-version' should not be allowed

When I filled os-profile with properties:
| name                 | value                          |
| os-profile-version   | V1                             |
| boot-type            | pxe                            |
| install-type         | preseed                        |
| os                   | Ubuntu                         |
| architecture         | x64                            |
| description          | Search Agg2 OS Profile         |
| kernel-boot-location | profiles/SEARCH_AGG/V1/vmlinuz |
| initrd-boot-location | profiles/SEARCH_AGG/V1/initrd  |

And I PUT filled os-profile SEARCH_AGG/V1 with files:
| path           | type           | template|
| boot/pxeconfig | pxeconfig      | true    |
| boot/initrd    | initrd         | false   |
| boot/vmlinuz   | kernel         | false   |
| boot/preseed   | preseed-config | true    |
| boot/test      | test           | false   |

Then I should receive HTTP status 400

!--

Scenario: Non-DRAFT os-profile should not allow a DELETE operation
!-- SEARCH_AGG/V2 in BLESSED state

When I want to DELETE os-profile SEARCH_AGG/V2
Then I should receive HTTP status 405

!--

Scenario: os-profile with DRAFT state should allow a DELETE operation
!-- SEARCH_AGG/V1 in DRAFT state
When I want to DELETE os-profile SEARCH_AGG/V1

!--

Scenario: Folder checksum validation
Given I PUT predefined hw-profiles SUN_280
And I PUT predefined os-profile SEARCH_AGG/V3
When I change state of the os-profile SEARCH_AGG/V3 to QA
Then os-profile state should be changed to QA

When I create SEARCH_AGG/V3/testfolder/file1 in os-profile storage with contents:
; Properties passed to file1

When I want verify checksum explicitly for os-profile SEARCH_AGG/V3
Then checksum validation should failed
And invalid flag for os-profiles should be set to TRUE

When I create SEARCH_AGG/V3/testfolder/file1 in os-profile storage with contents:;nothing here 1;

When I want verify checksum explicitly for os-profile SEARCH_AGG/V3
Then checksum validation should be successful
