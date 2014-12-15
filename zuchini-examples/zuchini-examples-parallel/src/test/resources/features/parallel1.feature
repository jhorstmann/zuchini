Feature: Scenarios are run in parallel

  Scenario: Counter 1
    Given the counter is initialized to 1
    When the counter is incremented
    Then the counter should be 2

  Scenario: Counter 2
    Given the counter is initialized to 2
    When the counter is incremented
    Then the counter should be 3

  Scenario: Counter 3
    Given the counter is initialized to 3
    When the counter is incremented
    Then the counter should be 4

  Scenario: Counter 4
    Given the counter is initialized to 4
    When the counter is incremented
    Then the counter should be 5

  Scenario Outline: Lots of counters
    Given the counter is initialized to <initial>
    When the counter is incremented
    Then the counter should be <expected>
    Examples:
      | initial | expected |
      |       5 |        6 |
      |       6 |        7 |
      |       7 |        8 |
      |       8 |        9 |
      |       9 |       10 |
      |      10 |       11 |
      |      11 |       12 |
      |      12 |       13 |
      |      13 |       14 |
      |      14 |       15 |
      |      15 |       16 |
      |      16 |       17 |
      |      17 |       18 |
      |      18 |       19 |
      |      19 |       20 |
      |      20 |       21 |

