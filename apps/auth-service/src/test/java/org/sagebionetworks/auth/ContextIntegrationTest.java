package org.sagebionetworks.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AuthorizationServiceApp.class})
public class ContextIntegrationTest {

  @Test
  public void whenLoadApplication_thenSuccess() {}
}
