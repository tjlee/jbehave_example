Story: Invalid flag story

Scenario: Invalid flag cycle

Given I PUT predefined hw-profiles SUN_280, SUN_510
And I PUT predefined os-profile SEARCH_AGG/V1

!-- changing state
When I change state of the os-profile SEARCH_AGG/V1 to QA

!-- modifying file for checksum change
When I create SEARCH_AGG/V1/boot/initrd in os-profile storage with contents:
;some changed content for checksum verification

When I change state of the os-profile SEARCH_AGG/V1 to BLESSED
Then invalid flag for os-profiles should be set to TRUE

When I POST install config properties to the OS profile:
| name       | value   |
| region     | lvs     |
| hw-profile | SUN_280 |
| TZ         | PST     |

Then asynchronous state should be FAILED

!-- copying files

When I create SEARCH_AGG/V2/boot/pxeconfig in os-profile storage with contents:
DISPLAY boot.txt

PROMPT 1
TIMEOUT 0

DEFAULT install

LABEL install
    kernel {kernel-boot-location}
    append vga=normal initrd={initrd-boot-location} url={preseed-config-url} --
And I create SEARCH_AGG/V2/hw-profiles/SUN_510 in os-profile storage with contents:
; SUN_510 file content
And I create SEARCH_AGG/V2/boot/vmlinuz in os-profile storage with contents:
;some vmlinuz content
And I create SEARCH_AGG/V2/boot/initrd in os-profile storage with contents:
;some initrd content
And I create SEARCH_AGG/V2/boot/preseed in os-profile storage with contents:
;some preseed content
tz={TZ}
install service url={install-service-url}
install service parameterized url={install-service-parameterized-url}
;some more content

When I want to clone corrupted os-profile SEARCH_AGG/V1 to os-profile with version V2
Then os-profile state should be changed to DRAFT
And invalid flag for os-profiles should be set to FALSE


Scenario: Verifying invalid flag after explicit checksum validation
Given I PUT predefined os-profile SEARCH_AGG/V2
!-- changing os profile state to invoke checksum calculation
When I change state of the os-profile SEARCH_AGG/V2 to QA

When I create SEARCH_AGG/V2/boot/initrd in os-profile storage with contents:
;some changed content for checksum verification

When I want verify checksum explicitly for os-profile SEARCH_AGG/V2
Then checksum validation should failed
And invalid flag for os-profiles should be set to TRUE

!-- restoring file
When I create SEARCH_AGG/V2/boot/initrd in os-profile storage with contents:;some initrd content

When I want verify checksum explicitly for os-profile SEARCH_AGG/V2
Then checksum validation should be successful

When I change state of the os-profile SEARCH_AGG/V2 to BLESSED
Then os-profile state should be changed to BLESSED