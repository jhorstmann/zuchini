Feature: Scenarios can be filtered by surefire plugin

  Scenario: Datatable with identity naming convention
    Given a datatable with identity naming convention
      | productName | productDescription |
      | Test        | This is a test     |
    Then the column "productName" in row "1" is "Test"
    And the column "productDescription" in row "1" is "This is a test"

  Scenario: Datatable with lowercase naming convention
    Given a datatable with lowercase naming convention
      | product name | product description    |
      | Test         | This is a test         |
    Then the column "productName" in row "1" is "Test"
    And the column "productDescription" in row "1" is "This is a test"

  Scenario: Datatable with uppercase naming convention
    Given a datatable with uppercase naming convention
      | Product Name | Product Description    |
      | Test         | This is a test         |
    Then the column "productName" in row "1" is "Test"
    And the column "productDescription" in row "1" is "This is a test"
