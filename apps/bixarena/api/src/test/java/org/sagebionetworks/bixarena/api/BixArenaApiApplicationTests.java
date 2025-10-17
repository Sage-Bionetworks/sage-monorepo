package org.sagebionetworks.bixarena.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
  "app.welcome-message=Welcome to BixArena API (test)",
  "app.ui-base-url=http://localhost:8100",
  "app.auth.authorize-url=https://example.org/authorize",
  "app.auth.token-url=https://example.org/token",
  "app.auth.jwks-url=https://example.org/jwks",
  "app.auth.client-id=test-client",
  "app.auth.client-secret=test-secret",
  "app.auth.redirect-uri=http://localhost:8112/v1/auth/oidc/callback",
  "app.auth.internal-issuer=http://localhost",
  "app.auth.audience=test-audience",
  "app.auth.token-ttl-seconds=600"
})
@ActiveProfiles("test")
class BixArenaApiApplicationTests {

  @Autowired
  private BixArenaApiApplication application;

  @Test
  void contextLoads() {
    assertThat(application).isNotNull();
  }
}
