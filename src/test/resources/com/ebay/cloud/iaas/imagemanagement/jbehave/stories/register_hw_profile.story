Story: As an API user I want to register hw-profile

Scenario: Register hw-profile

When I PUT hw-profile SUN_210 with properties:
| name         | value                       |
| os           | Ubuntu                      |
| architecture | x64                         |
| description  | SUN_210 profile description |


Scenario: Register hw-profile with without properties


When I PUT hw-profile SUN_220 with properties:
| name | value |

