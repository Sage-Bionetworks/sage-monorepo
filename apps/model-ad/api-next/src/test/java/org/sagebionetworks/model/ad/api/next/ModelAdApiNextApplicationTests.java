package org.sagebionetworks.model.ad.api.next;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ModelAdApiNextApplicationTests {

  @Autowired
  private ModelAdApiNextApplication application;

  @Test
  void contextLoads() {
    assertThat(application).isNotNull();
  }
}
