Scenario: Create files structure in Image Management storage

!-- File structure creation V2
When I create SEARCH_AGG/V2/boot/pxeconfig in os-profile storage with contents:
DISPLAY boot.txt

PROMPT 1
TIMEOUT 0

DEFAULT install

LABEL install
    kernel {kernel-boot-location}
    append vga=normal initrd={initrd-boot-location} url={preseed-config-url} --
And I create SEARCH_AGG/V2/hw-profiles/SUN_280 in os-profile storage with contents:
; SUN_280 file content
And I create SEARCH_AGG/V2/hw-profiles/SUN_510 in os-profile storage with contents:
; SUN_510 file content
And I create SEARCH_AGG/V2/boot/vmlinuz in os-profile storage with contents:
;some vmlinuz content
And I create SEARCH_AGG/V2/boot/initrd in os-profile storage with contents:
;some initrd content
And I create SEARCH_AGG/V2/boot/preseed in os-profile storage with contents:
;some preseed content
tz={TZ}
;some more content
