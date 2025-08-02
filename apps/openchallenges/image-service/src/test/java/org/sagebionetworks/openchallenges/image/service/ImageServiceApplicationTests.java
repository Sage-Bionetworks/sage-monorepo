package org.sagebionetworks.openchallenges.image.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@ActiveProfiles("test")
class ImageServiceApplicationTests {

  @Test
  @SuppressWarnings("squid:S2699")
  void contextLoads() {}
}
