Feature: Scenarios can be filtered by surefire plugin

  Scenario: Successful scenario
    Given a datatable
      | Name | Description    |
      | Test | This is a test |
    Then the column "Name" in row "1" is "Test"
    And the column "Description" in row "1" is "This is a test"
