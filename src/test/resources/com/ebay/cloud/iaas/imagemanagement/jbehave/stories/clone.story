Story: Clone operation

Scenario: Clone operation

Given I PUT predefined hw-profiles SUN_280, SUN_510
And I PUT predefined os-profile SEARCH_AGG/V1

When I change state of the os-profile SEARCH_AGG/V1 to QA

!-- coping files
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
And I create SEARCH_AGG/V2/boot/test in os-profile storage with contents:
;some test content
And I create SEARCH_AGG/V2/boot/preseed in os-profile storage with contents:
;some preseed content
tz={TZ}
install service url={install-service-url}
install service parameterized url={install-service-parameterized-url}
;some more content


When I want to clone os-profile SEARCH_AGG/V1 to os-profile with version V2
Then os-profile state should be changed to DRAFT

!--

Scenario: Clone operation without files

When I change state of the os-profile SEARCH_AGG/V2 to QA
When I want to clone os-profile SEARCH_AGG/V2 to os-profile with version V3
When I change state of the os-profile SEARCH_AGG/V3 to QA
Then asynchronous state should be FAILED

!--

Scenario: Clone operation to existent non-DRAFT OS profile version

Given I PUT predefined os-profile SEARCH_AGG/V3
When I change state of the os-profile SEARCH_AGG/V3 to QA

When I want to clone os-profile SEARCH_AGG/V2 to os-profile with version V3
Then I should receive HTTP status 409

!--

Scenario: Self-clone operation

Given I PUT predefined os-profile Test_Profile/V3

When I want to clone os-profile Test_Profile/V3 to os-profile with version V3
Then I should receive HTTP status 409

!--

Scenario: Clone invalid os-profile

When I create Test_Profile/V3/boot/initrd in os-profile storage with contents:
;some changed content for checksum verification

!-- set invalid flag to true by explicit check sum invoke
When I want verify checksum explicitly for os-profile Test_Profile/V3

!-- copy files
When I create Test_Profile/V3/boot/pxeconfig in os-profile storage with contents:
DISPLAY boot.txt

PROMPT 1
TIMEOUT 0

DEFAULT install

LABEL install
    kernel {kernel-boot-location}
    append vga=normal initrd={initrd-boot-location} url={preseed-config-url} --
And I create Test_Profile/V4/hw-profiles/SUN_510 in os-profile storage with contents:
; SUN_510 file content
And I create Test_Profile/V4/boot/vmlinuz in os-profile storage with contents:
;some vmlinuz content
And I create Test_Profile/V4/boot/initrd in os-profile storage with contents:
;some initrd content
And I create Test_Profile/V4/boot/preseed in os-profile storage with contents:
;some preseed content
tz={TZ}
install service url={install-service-url}
install service parameterized url={install-service-parameterized-url}
;some more content

!-- cloning os-profile with allow Invalid flag set to true

When I want to clone corrupted os-profile Test_Profile/V3 to os-profile with version V4
Then os-profile state should be changed to DRAFT
And invalid flag for os-profiles should be set to FALSE
