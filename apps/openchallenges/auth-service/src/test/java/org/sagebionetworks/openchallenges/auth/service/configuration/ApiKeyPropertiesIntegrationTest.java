package org.sagebionetworks.openchallenges.auth.service.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration tests for profile-specific API key properties configuration.
 * These tests verify that Spring Boot profiles correctly configure ApiKeyProperties.
 */
class ApiKeyPropertiesIntegrationTest {

  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
  @TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.datasource.driverClassName=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
      "spring.flyway.enabled=false",
      "app.oauth2.authorization-server.enabled=false",
    }
  )
  @ActiveProfiles("dev")
  @Tag("integration")
  static class DevProfileTest {

    @Autowired
    private ApiKeyProperties apiKeyProperties;

    @Test
    void shouldHaveDevPrefix() {
      assertEquals("oc_dev_", apiKeyProperties.getPrefix());
      assertEquals(40, apiKeyProperties.getLength());
    }
  }

  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
  @TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.datasource.driverClassName=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
      "spring.flyway.enabled=false",
      "app.oauth2.authorization-server.enabled=false",
    }
  )
  @ActiveProfiles("stage")
  @Tag("integration")
  static class StageProfileTest {

    @Autowired
    private ApiKeyProperties apiKeyProperties;

    @Test
    void shouldHaveStagePrefix() {
      assertEquals("oc_stage_", apiKeyProperties.getPrefix());
      assertEquals(40, apiKeyProperties.getLength());
    }
  }

  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
  @TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.datasource.driverClassName=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
      "spring.flyway.enabled=false",
      "app.oauth2.authorization-server.enabled=false",
    }
  )
  @ActiveProfiles("prod")
  @Tag("integration")
  static class ProdProfileTest {

    @Autowired
    private ApiKeyProperties apiKeyProperties;

    @Test
    void shouldHaveProdPrefix() {
      assertEquals("oc_prod_", apiKeyProperties.getPrefix());
      assertEquals(40, apiKeyProperties.getLength());
    }
  }
}
