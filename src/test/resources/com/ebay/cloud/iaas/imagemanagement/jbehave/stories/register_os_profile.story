Story: As an API user(on behalf of na SA) I want to register an OS Profile
so that I can boot it later using the OS Provisioning service

Scenario: Register OS profile SEARCH_AGG/V1

Given I PUT predefined hw-profiles SUN_280, SUN_510

!-- file structure creation
When I create SEARCH_AGG/V1/boot/pxeconfig in os-profile storage with contents:
DISPLAY boot.txt

PROMPT 1
TIMEOUT 0

DEFAULT install

LABEL install
    kernel {kernel-boot-location}
    append vga=normal initrd={initrd-boot-location} url={preseed-config-url} --
And I create SEARCH_AGG/V1/hw-profiles/SUN_280 in os-profile storage with contents:
; SUN_280 file content
And I create SEARCH_AGG/V1/hw-profiles/SUN_510 in os-profile storage with contents:
; SUN_510 file content
And I create SEARCH_AGG/V1/boot/vmlinuz in os-profile storage with contents:
;some vmlinuz content
And I create SEARCH_AGG/V1/boot/initrd in os-profile storage with contents:
;some initrd content
And I create SEARCH_AGG/V1/boot/preseed in os-profile storage with contents:
; Properties passed to install-conf
region={region}
tz={TZ}
; auto-generated properties:
install-service-url={install-service-url}
install-service-host={install-service-host}
install-service-port={install-service-port}
; OS Profile properties:
name={name}
state={state}
os={os}
architecture={architecture}
description={description}
kernel-boot-location={kernel-boot-location}
initrd-boot-location={initrd-boot-location}
; hw-profile:
{hw-profile}
;some more content
And I create SEARCH_AGG/V1/boot/test in os-profile storage with contents:
;some test content here
{ignored property}
;additional content

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

Then os-profile should be registered successfully

