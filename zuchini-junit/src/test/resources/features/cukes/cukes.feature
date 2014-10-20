Feature: cukes

  Scenario: Some cukes
    Given I have 48 cukes in my belly
    Then there are 48 cukes in my belly

  Scenario Outline: More cukes
    Given I have <count> cukes in my belly
    Then there are <count> cukes in my belly
  Examples:
  | count |
  | -1    |
  | 0     |
  | 1     |
  | 2     |
