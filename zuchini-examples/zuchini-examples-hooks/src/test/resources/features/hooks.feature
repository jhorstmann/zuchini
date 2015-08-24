Feature: Scenarios can be filtered by surefire plugin

  Scenario: Without tags on scenario
    Given a step
    Then the executed hooks are:
      | untagged1 |
      | untagged2 |

  @tag1
  Scenario: With tag on scenario
    Given a step
    Then the executed hooks are:
      | tag1      |
      | untagged1 |
      | untagged2 |
