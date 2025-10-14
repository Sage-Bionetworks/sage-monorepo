package org.sagebionetworks.bixarena.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BixArenaApiApplicationTests {

  @Autowired
  private BixArenaApiApplication application;

  @Test
  void contextLoads() {
    assertThat(application).isNotNull();
  }
}
