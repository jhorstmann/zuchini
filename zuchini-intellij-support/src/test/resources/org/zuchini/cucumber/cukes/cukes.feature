Feature: cukes

  Scenario: Some cukes
    Given I have 48 cukes in my belly
    Then there are 48 cukes in my belly


  Scenario: Failure
    Given I have -1 cukes in my belly
    Then there are -1 cukes in my belly

  Scenario: Cukes are scenario scoped
    Then there are 0 cukes in my belly

  Scenario Outline: More cukes
    Given I have <count> cukes in my belly
    Then there are <count> cukes in my belly
  Examples:
    | count |
    | 1     |
    | 2     |
    | 0     |

  Scenario: No cukes
    Given I have 0 cukes in my belly
    Then there are 0 cukes in my belly

  Scenario: Singular cukes
    Given I have 1 cukes in my belly
    Then there are 1 cukes in my belly

