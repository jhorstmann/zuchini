Feature: MockMvc

  Background: Common request headers
    Given the following request headers:
      | Accept | application/json |

  Scenario Outline: Simple requests
    When a GET request to "/api/hello" is executed with the following parameters:
      | name | <name> |
    Then the response status is 200
    And the response body matches the following json paths:
      | .name     | <name>       |
      | .greeting | Hello <name> |
    And the response contains the following headers:
      | Content-Type | application/json;charset=UTF-8 |
    Examples:
      | name    |
      | World   |
      | Spring  |
      | Zuchini |

