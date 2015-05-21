Story: As the machine booting, I want to serve pxeconfig, preseed files.

Scenario: Serving install files
Given I PUT predefined hw-profiles SUN_280, SUN_510
And I PUT predefined os-profile SEARCH_AGG/V1

When I POST install config properties to the OS profile:
| name       | value   |
| region     | lvs     |
| hw-profile | SUN_280 |
| TZ         | PST     |


Then received install config should have properties:
| name                     | value                                                    |
| os-profile-name          | SEARCH_AGG                                               |
| boot-type                | pxe                                                      |
| install-type             | preseed                                                  |
| os                       | Ubuntu                                                   |
| architecture             | x64                                                      |
| description              | Search Agg1 OS Profile                                   |
| kernel-boot-location     | os-profiles/SEARCH_AGG/V1/boot/vmlinuz                   |
| initrd-boot-location     | os-profiles/SEARCH_AGG/V1/boot/initrd                    |
| hw-profiles              | SUN_510,SUN_280                                          |
| install-service-host     | $installServiceHost                                      |
| preseed-config-file      | boot/preseed                                             |
| preseed-config-url       | $paramBaseAddress/.*                                     |
| pxeconfig-url            | $paramBaseAddress/.*                                     |
| all-properties-txt-url   | $paramBaseAddress/.*                                     |
| initrd-url               | $plainBaseAddress/os-profiles/SEARCH_AGG/V1/boot/initrd  |
| kernel-url               | $plainBaseAddress/os-profiles/SEARCH_AGG/V1/boot/vmlinuz |
| test-url                 | $plainBaseAddress/os-profiles/SEARCH_AGG/V1/boot/test    |
| install-service-url      | $plainBaseAddress                                        |
| test-file                | boot/test                                                |
| pxeconfig-file           | boot/pxeconfig                                           |
| os-profile-version       | V1                                                       |
| hw-profile               | SUN_280                                                  |
| install-service-port     | 80                                                       |
| kernel-file              | boot/vmlinuz                                             |
| region                   | lvs                                                      |
| initrd-file              | boot/initrd                                              |
| TZ                       | PST                                                      |

Then received install config should have file types:
pxeconfig,
initrd,
kernel,
preseed-config,
test

When I make GET request using URL returned in install config for file type "test"

Then I should receive HTTP status 200 with content:
;some test content here
{ignored property}
;additional content

When I make GET request using URL returned in install config for file type "pxeconfig"

Then I should receive HTTP status 200 with content where "_url_" is equal to the URL of the "preseed-config" file:
DISPLAY boot.txt

PROMPT 1
TIMEOUT 0

DEFAULT install

LABEL install
    kernel os-profiles/SEARCH_AGG/V1/boot/vmlinuz
    append vga=normal initrd=os-profiles/SEARCH_AGG/V1/boot/initrd url=_url_ --


When I make GET request using URL returned in install config for file type "preseed-config"

Then I should receive HTTP status 200 with content:
; Properties passed to install-conf
region=lvs
tz=PST
; auto-generated properties:
install-service-url=$installServiceUrl
install-service-host=$installServiceHost
install-service-port=$installServicePort
; OS Profile properties:
os-profile-name=SEARCH_AGG
os=Ubuntu
architecture=x64
description=Search Agg1 OS Profile
kernel-boot-location=os-profiles/SEARCH_AGG/V1/boot/vmlinuz
initrd-boot-location=os-profiles/SEARCH_AGG/V1/boot/initrd
; hw-profile:
SUN_280
;some more content