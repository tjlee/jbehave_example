Story: Serving all properties
Scenario: Serve all properties link


Given I PUT predefined hw-profiles SUN_510
And I PUT predefined os-profile Test_Profile/V3

When I POST install config properties to the OS profile:
| name       | value   |
| region     | lvs     |
| hw-profile | SUN_510 |
| TZ         | PST     |

When I make GET request using URL returned in install config for all-properties

Then received property list has to contain:
| name                   | value                                                         |
| os-profile-name        | Test_Profile                                                  |
| boot-type              | pxe                                                           |
| install-type           | preseed                                                       |
| os                     | Windows                                                       |
| architecture           | x32                                                           |
| kernel-boot-location   | os-profiles/Test_Profile/V3/boot/vmlinuz                      |
| initrd-boot-location   | os-profiles/Test_Profile/V3/boot/initrd                       |
| hw-profiles            | SUN_510                                                       |
| install-service-host   | _serviceHost_                                                 |
| preseed-config-file    | boot/preseed                                                  |
| preseed-config-url     | _param_base_address_/.*                                       |
| pxeconfig-url          | _param_base_address_/.*                                       |
| all-properties-txt-url | _param_base_address_/.*                                       |
| initrd-url             | _plain_base_address_/os-profiles/Test_Profile/V3/boot/initrd  |
| kernel-url             | _plain_base_address_/os-profiles/Test_Profile/V3/boot/vmlinuz |
| install-service-url    | _plain_base_address_                                          |
| pxeconfig-file         | boot/pxeconfig                                                |
| os-profile-version     | V3                                                            |
| hw-profile             | SUN_510                                                       |
| install-service-port   | 80                                                            |
| kernel-file            | boot/vmlinuz                                                  |
| region                 | lvs                                                           |
| initrd-file            | boot/initrd                                                   |
| TZ                     | PST                                                           |
| description            | SUN_510 \n SUN_510 \!\@\#\$\%\^\&\*\(\)\_\+ \n SUN_510        |

!-- "\" in description parameter to escape special symbols in RegExp