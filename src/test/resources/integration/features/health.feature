Feature: Health Check Integration Tests

  Background:
    * url baseUrl
    * header Accept = 'application/json'

  Scenario: Health endpoint returns UP status
    Given path 'actuator/health'
    When method get
    Then status 200
    And match response.status == 'UP'

  Scenario: Info endpoint returns application information
    Given path 'actuator/info'
    When method get
    Then status 200
    And match response.app.name == 'seed'

  Scenario: Metrics endpoint is accessible
    Given path 'actuator/metrics'
    When method get
    Then status 200
    And match response.names == '#array'
    And assert response.names.length > 0

  Scenario: Prometheus metrics endpoint is accessible
    Given path 'actuator/prometheus'
    When method get
    Then status 200
    And match response =~ '.*jvm.*'
    And match response =~ '.*process.*'