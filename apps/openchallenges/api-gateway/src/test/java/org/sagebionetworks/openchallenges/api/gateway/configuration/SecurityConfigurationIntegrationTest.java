package org.sagebionetworks.openchallenges.api.gateway.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("integration")
class SecurityConfigurationIntegrationTest {

  @LocalServerPort
  private int port;

  @Test
  @DisplayName("should load security configuration in application context")
  void shouldLoadSecurityConfigurationInApplicationContext(ApplicationContext context) {
    // when
    SecurityWebFilterChain securityWebFilterChain = context.getBean(SecurityWebFilterChain.class);

    // then
    assertNotNull(securityWebFilterChain);
  }

  @Test
  @DisplayName("should have security configuration bean in context")
  void shouldHaveSecurityConfigurationBeanInContext(ApplicationContext context) {
    // when
    SecurityConfiguration securityConfiguration = context.getBean(SecurityConfiguration.class);

    // then
    assertNotNull(securityConfiguration);
  }
}
