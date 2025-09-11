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
 * These tests verify that Spring Boot profiles correctly configure AuthServiceProperties.
 */
class AuthServicePropertiesIntegrationTest {

  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
    AuthServiceProperties.class
  })
  @EnableConfigurationProperties(AuthServiceProperties.class)
  @TestPropertySource(
    properties = {
      "app.oauth2.authorization-server.enabled=false",
      "openchallenges.auth-service.api-key.prefix=oc_dev_",
      "openchallenges.auth-service.api-key.length=40"
    }
  )
  @ActiveProfiles("dev")
  @Tag("integration")
  static class DevProfileTest {

        @Autowired
    private AuthServiceProperties authServiceProperties;

    @Test
    void testDevProfileApiKeyConfiguration() {
      assertEquals("oc_dev_", authServiceProperties.getApiKey().getPrefix());
      assertEquals(40, authServiceProperties.getApiKey().getLength());
    }
  }

  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
    AuthServiceProperties.class
  })
  @EnableConfigurationProperties(AuthServiceProperties.class)
  @TestPropertySource(
    properties = {
      "app.oauth2.authorization-server.enabled=false",
      "openchallenges.auth-service.api-key.prefix=oc_stage_",
      "openchallenges.auth-service.api-key.length=40"
    }
  )
  @ActiveProfiles("stage")
  @Tag("integration")
  static class StageProfileTest {

    @Autowired
    private AuthServiceProperties authServiceProperties;

    @Test
    void shouldHaveStagePrefix() {
      assertEquals("oc_stage_", authServiceProperties.getApiKey().getPrefix());
      assertEquals(40, authServiceProperties.getApiKey().getLength());
    }
  }

    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
    AuthServiceProperties.class
  })
  @EnableConfigurationProperties(AuthServiceProperties.class)
  @TestPropertySource(
    properties = {
      "app.oauth2.authorization-server.enabled=false",
      "openchallenges.auth-service.api-key.prefix=oc_prod_",
      "openchallenges.auth-service.api-key.length=40"
    }
  )
  @ActiveProfiles("prod")
  @Tag("integration")
  static class ProdProfileTest {

    @Autowired
    private AuthServiceProperties authServiceProperties;

    @Test
    void shouldHaveProdPrefix() {
      assertEquals("oc_prod_", authServiceProperties.getApiKey().getPrefix());
      assertEquals(40, authServiceProperties.getApiKey().getLength());
    }
  }
}
