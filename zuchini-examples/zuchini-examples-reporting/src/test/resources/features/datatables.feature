Feature: Json report of features and scenarios

  Scenario: Simple scenario
    Given a simple scenario
    When the scenario is executed
    Then a report is generated

  Scenario: Scenario including a datatable
    Given a datatable
     | Key  | Value |
     | ABC  | 123   |
     | DEF  | 456   |
    When the scenario is executed
    Then a report is generated

  Scenario: Scenario with failing assumption
    Given a failing assumption
