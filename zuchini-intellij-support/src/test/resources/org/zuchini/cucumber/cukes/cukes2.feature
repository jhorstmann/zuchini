Feature: cukes 2

  Scenario: No cukes
    Given I have 0 cukes in my belly
    Then there are 0 cukes in my belly

  Scenario: Singular cukes
    Given I have 1 cukes in my belly
    Then there are 1 cukes in my belly

