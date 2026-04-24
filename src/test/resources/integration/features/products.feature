Feature: Product API Integration Tests

  Background:
    * url baseUrl
    * header Content-Type = 'application/json'
    * def uuidRegex = '[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}'

  Scenario: Get all products returns empty list when no products exist
    Given path 'api/v1/products'
    When method get
    Then status 200
    And match response == []

  Scenario: Create a new product
    Given path 'api/v1/products'
    And request
        """
        {
          "name": "Test Product",
          "description": "Integration test product",
          "price": 99.99,
          "stock": 10,
          "status": "AVAILABLE"
        }
        """
    When method post
    Then status 201
    And match response contains { name: 'Test Product', description: 'Integration test product', price: 99.99, stock: 10, status: 'AVAILABLE' }
    And match response.id != null
    And match header Location contains '/api/v1/products/'

  Scenario: Create product with minimal required fields
    Given path 'api/v1/products'
    And request
        """
        {
          "name": "Minimal Product",
          "price": 0,
          "stock": 0,
          "status": "AVAILABLE"
        }
        """
    When method post
    Then status 201
    And match response contains { name: 'Minimal Product', description: '#null', price: 0, stock: 0, status: 'AVAILABLE' }

  Scenario: Search products by name
    # Create products with similar names
    Given path 'api/v1/products'
    And request
        """
        {
          "name": "Widget Pro",
          "description": "Professional widget",
          "price": 49.99,
          "stock": 100,
          "status": "AVAILABLE"
        }
        """
    When method post
    # Search for products matching the name
    Given path 'api/v1/products/search'
    And param term = 'name'
    And param value = 'Widget'
    When method get
    Then status 200
    And match response contains any { name: 'Widget Pro' }

  Scenario: Create product fails validation with negative price
    Given path 'api/v1/products'
    And request
        """
        {
          "name": "Invalid Product",
          "price": -10.99,
          "stock": 5,
          "status": "AVAILABLE"
        }
        """
    When method post
    Then status 400