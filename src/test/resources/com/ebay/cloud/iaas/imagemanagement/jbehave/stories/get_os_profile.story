Story: Get OS Profile by name and version
Scenario: Get OS Profile

Given I PUT predefined hw-profiles SUN_280, SUN_510
And I PUT predefined os-profile SEARCH_AGG/V1
And I PUT predefined os-profile SEARCH_AGG/V2
And I PUT predefined os-profile Test_Profile/V3

When I GET an os-profile named SEARCH_AGG version V1
Then I should receive os-profile with properties:
| name                 | value                                  |
| boot-type            | pxe                                    |
| os                   | Ubuntu                                 |
| architecture         | x64                                    |
| description          | Search Agg1 OS Profile                 |
| kernel-boot-location | os-profiles/SEARCH_AGG/V1/boot/vmlinuz |
| initrd-boot-location | os-profiles/SEARCH_AGG/V1/boot/initrd  |
| region               | new_region                             |
| install-type         | preseed                                |

Then received os-profile should have file types:
pxeconfig,
initrd,
kernel,
preseed-config,
test