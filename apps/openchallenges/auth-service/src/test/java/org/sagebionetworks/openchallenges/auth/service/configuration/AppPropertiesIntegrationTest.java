package org.sagebionetworks.openchallenges.auth.service.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration tests for profile-specific API key properties configuration.
 * These tests verify that Spring Boot profiles correctly configure AppProperties.
 */
class AppPropertiesIntegrationTest {

  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
    AppProperties.class
  })
  @EnableConfigurationProperties(AppProperties.class)
  @ActiveProfiles("dev")
  @TestPropertySource(
    properties = {
      "app.oauth2.authorization-server.enabled=false",
      "app.oauth2.resource-server.enabled=false",
      "server.port=0"
    }
  )
  static class DevProfileTest {

    @Autowired
    private AppProperties appProperties;

    @Test
    @Tag("unit")
    void shouldConfigureDevApiKeyProperties() {
      assertEquals("oc_dev_", appProperties.getApiKey().getPrefix());
      assertEquals(40, appProperties.getApiKey().getLength());
    }
  }

  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
    AppProperties.class
  })
  @EnableConfigurationProperties(AppProperties.class)
  @ActiveProfiles("stage")
  @TestPropertySource(
    properties = {
      "app.oauth2.authorization-server.enabled=false",
      "app.oauth2.resource-server.enabled=false",
      "server.port=0"
    }
  )
  static class StageProfileTest {

    @Autowired
    private AppProperties appProperties;

    @Test
    @Tag("unit")
    void shouldConfigureStageApiKeyProperties() {
      assertEquals("oc_stage_", appProperties.getApiKey().getPrefix());
      assertEquals(40, appProperties.getApiKey().getLength());
    }
  }

  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
    AppProperties.class
  })
  @EnableConfigurationProperties(AppProperties.class)
  @ActiveProfiles("prod")
  @TestPropertySource(
    properties = {
      "app.oauth2.authorization-server.enabled=false",
      "app.oauth2.resource-server.enabled=false",
      "server.port=0"
    }
  )
  static class ProdProfileTest {

    @Autowired
    private AppProperties appProperties;

    @Test
    @Tag("unit")
    void shouldConfigureProdApiKeyProperties() {
      assertEquals("oc_prod_", appProperties.getApiKey().getPrefix());
      assertEquals(40, appProperties.getApiKey().getLength());
    }
  }
}