Feature: MockMvc

  Background: Common request headers
    Given the following request headers:
      | Accept | application/json |

  Scenario: Simple request
    When a GET request to "/api/hello" is executed with the following parameters:
      | name | World |
    Then the response status is 200
    And the response body matches the following json paths:
      | .name     | World       |
      | .greeting | Hello World |
    And the response contains the following headers:
      | Content-Type | application/json;charset=UTF-8 |

