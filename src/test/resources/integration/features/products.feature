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
    And match response.id =~ uuidRegex
    And match response.name == 'Test Product'
    And match response.description == 'Integration test product'
    And match response.price == 99.99
    And match response.stock == 10
    And match response.status == 'AVAILABLE'
    And match header Location =~ '.*\\/api\\/v1\\/products\\/.*'

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
    And match response.name == 'Minimal Product'
    And match response.description == null
    And match response.price == 0

  Scenario: Get product by ID after creation
    # First create a product
    Given path 'api/v1/products'
    And request
        """
        {
          "name": "Product for Get Test",
          "description": "Testing GET endpoint",
          "price": 149.99,
          "stock": 5,
          "status": "AVAILABLE"
        }
        """
    When method post
    # Store the ID for subsequent calls
    * def productId = response.id
    # Now get the product by ID
    Given path 'api/v1/products', productId
    When method get
    Then status 200
    And match response.id == productId
    And match response.name == 'Product for Get Test'

  Scenario: Get product by ID returns 404 when not found
    Given path 'api/v1/products/00000000-0000-0000-0000-000000000000'
    When method get
    Then status 404

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
    And param name = 'Widget'
    When method get
    Then status 200
    And match response contains any { name == 'Widget Pro' }

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