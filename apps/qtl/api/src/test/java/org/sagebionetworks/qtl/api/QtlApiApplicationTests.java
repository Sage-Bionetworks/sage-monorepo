package org.sagebionetworks.qtl.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class QtlApiApplicationTests {

  @Autowired
  private QtlApiApplication application;

  @Test
  void contextLoads() {
    assertThat(application).isNotNull();
  }
}
