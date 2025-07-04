package org.sagebionetworks.openchallenges.organization.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.NONE,
  properties = { "spring.cloud.openfeign.enabled=false" }
)
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@ActiveProfiles("test")
class OrganizationServiceApplicationTests {

  @Test
  void contextLoads() {}
}
