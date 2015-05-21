Story: As a System Administrator, I need a script to register,
so that it is more user-friendly(outside of the REST API).

Scenario: Register OS profiles using script
GivenStories: com/ebay/cloud/iaas/imagemanagement/jbehave/stories/file_structure_creation.story


Given I authenticate as Administrator

When I want to register hw-profile named SUN_510 using register script with following XML:
<hw-profile>
    <properties>
          <property name="architecture">x32</property>
          <property name="os">Windows</property>
          <property name="description">SUN_510
   SUN_510 !@#$%^&amp;*()_+
   SUN_510</property>
    </properties>
</hw-profile>

And I want to register hw-profile named SUN_280 using register script with following XML:
<hw-profile>
    <properties>
        <property name="TZ">AST</property>
        <property name="os">Ubuntu</property>
        <property name="architecture">x64</property>
    </properties>
</hw-profile>

And I want to register os-profile SEARCH_AGG/V2 using register script with following XML:
<os-profile>
  <hw-profiles>
    <hw-profile>SUN_280</hw-profile>
    <hw-profile>SUN_510</hw-profile>
  </hw-profiles>
  <properties>
    <property name="os">Ubuntu</property>
    <property name="description">Search Agg OS Profile</property>
    <property name="architecture">x64</property>
    <property name="boot-type">pxe</property>
    <property name="install-type">preseed</property>
    <property name="kernel-boot-location">profiles/SEARCH_AGG/V2/boot/vmlinuz</property>
    <property name="initrd-boot-location">profiles/SEARCH_AGG/V2/boot/initrd</property>
  </properties>

  <files>
    <file>
      <path>boot/pxeconfig</path>
      <template>true</template>
      <type>pxeconfig</type>
    </file>
    <file>
      <path>boot/preseed</path>
      <template>true</template>
      <type>preseed-config</type>
    </file>
    <file>
      <path>boot/vmlinuz</path>
      <type>kernel</type>
    </file>
    <file>
      <path>boot/initrd</path>
      <type>initrd</type>
    </file>
  </files>
</os-profile>

When I GET an os-profile named SEARCH_AGG version V2

Then I should receive os-profile with properties:
| name                 | value                               |
| os                   | Ubuntu                              |
| architecture         | x64                                 |
| description          | Search Agg OS Profile               |
| kernel-boot-location | profiles/SEARCH_AGG/V2/boot/vmlinuz |
| initrd-boot-location | profiles/SEARCH_AGG/V2/boot/initrd  |
| boot-type            | pxe                                 |
| install-type         | preseed                             |

Then received os-profile should have file types:
pxeconfig,
initrd,
kernel,
preseed-config
