Scenario: Register OS profile with files folder

Given I PUT predefined hw-profiles SUN_280

And I PUT predefined os-profile SEARCH_AGG/V3

When I POST install config properties to the OS profile:
| name       | value   |
| region     | lvs     |
| hw-profile | SUN_280 |
| TZ         | PST     |

Then received install config should have file types:

pxeconfig,
initrd,
kernel,
preseed-config,
testfolder

When I make GET request using URL returned in install config for file type "testfolder"
Then I should receive HTTP status 200
And HTTP response should contain links for files:
file1,
file2

