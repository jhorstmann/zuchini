Feature: Scenarios are run in parallel

  Scenario: Counter 1
    Given the counter is initialized to 1
    When the counter is incremented
    Then the counter should be 2

  Scenario: Counter 2
    Given the counter is initialized to 2
    When the counter is incremented
    Then the counter should be 3

  Scenario Outline: Lots of counters
    Given the counter is initialized to <initial>
    When the counter is incremented
    Then the counter should be <expected>
    Examples:
      | initial | expected |
      |       3 |        4 |
      |       4 |        5 |
      |       5 |        6 |
      |       6 |        7 |
      |       7 |        8 |
      |       8 |        9 |
      |       9 |       10 |
      |      10 |       11 |

