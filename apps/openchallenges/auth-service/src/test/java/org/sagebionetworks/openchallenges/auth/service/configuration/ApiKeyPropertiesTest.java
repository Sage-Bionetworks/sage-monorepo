package org.sagebionetworks.openchallenges.auth.service.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for profile-specific API key properties configuration
 */
class ApiKeyPropertiesTest {

  @SpringBootTest
  @ActiveProfiles("dev")
  @TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
  })
  static class DevProfileTest {
    
    @Autowired
    private ApiKeyProperties apiKeyProperties;

    @Test
    void shouldHaveDevPrefix() {
      assertEquals("oc_dev_", apiKeyProperties.getPrefix());
      assertEquals(40, apiKeyProperties.getLength());
    }
  }

  @SpringBootTest
  @ActiveProfiles("stage")
  @TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
  })
  static class StageProfileTest {
    
    @Autowired
    private ApiKeyProperties apiKeyProperties;

    @Test
    void shouldHaveStagePrefix() {
      assertEquals("oc_stage_", apiKeyProperties.getPrefix());
      assertEquals(40, apiKeyProperties.getLength());
    }
  }

  @SpringBootTest
  @ActiveProfiles("prod")
  @TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
  })
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
