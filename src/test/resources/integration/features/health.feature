Feature: Health Check Integration Tests

  Background:
    * url baseUrl
    * header Accept = 'application/json'

  Scenario: Health endpoint returns UP status
    Given path 'actuator/health'
    When method GET
    Then status 200
    And match response.status == 'UP'

  Scenario: Health/liveness endpoint returns UP status
    Given path 'actuator/health/liveness'
    When method GET
    Then status 200
    And match response.status == 'UP'

  Scenario: Health/liveness endpoint returns UP status
    Given path 'actuator/health/readiness'
    When method GET
    Then status 200
    And match response.status == 'UP'
