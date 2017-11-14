Feature: Hello World Feature

  Scenario: Hello World Scenario
    Given the user name is 'World'
    When the user clicks the hello button
    Then the output is 'Hello World'

  Scenario: Hello JUnit5 Scenario
    Given the user name is 'JUnit5'
    When the user clicks the hello button
    Then the output is 'Hello JUnit5'

  Scenario Outline: Parameterized Hello
    Given the user name is '<name>'
    When the user clicks the hello button
    Then the output is 'Hello <name>'
    Examples:
      | name   |
      | World  |
      | JUnit5 |
