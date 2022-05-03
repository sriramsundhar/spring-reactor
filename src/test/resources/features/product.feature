Feature: Products

  Scenario: Get all products
    Given API Service is started
    When when client requests all products
    Then total of 3 products are returned