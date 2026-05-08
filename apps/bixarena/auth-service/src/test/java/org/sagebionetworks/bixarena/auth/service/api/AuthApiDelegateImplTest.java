package org.sagebionetworks.bixarena.auth.service.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.bixarena.auth.service.configuration.AppProperties;
import org.sagebionetworks.bixarena.auth.service.security.key.JwkKeyStore;
import org.sagebionetworks.bixarena.auth.service.service.InternalJwtService;
import org.sagebionetworks.bixarena.auth.service.service.UserService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class AuthApiDelegateImplTest {

  @Mock
  private JwkKeyStore keyStore;

  @Mock
  private InternalJwtService jwtService;

  @Mock
  private AppProperties appProperties;

  @Mock
  private UserService userService;

  @InjectMocks
  private AuthApiDelegateImpl delegate;

  private MockHttpServletRequest request;
  private MockHttpSession session;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    session = new MockHttpSession();
    request.setSession(session);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    AppProperties.Auth auth = new AppProperties.Auth(
      "https://signin.synapse.org",
      "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/token",
      "https://repo-prod.prod.sagebase.org/auth/v1/openid/jwks",
      "test-client-id",
      "test-client-secret",
      URI.create("http://localhost:8000/auth/callback"),
      "urn:bixarena:auth",
      "urn:bixarena:auth",
      600L,
      "service-client",
      "service-secret",
      300L
    );
    when(appProperties.auth()).thenReturn(auth);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  // --- login() return_to session storage ---

  @Test
  @DisplayName("should store return_to in session when path is a valid relative path")
  void shouldStoreReturnToInSessionWhenPathIsValidRelativePath() {
    delegate.login("/battle");

    assertThat(session.getAttribute("OIDC_RETURN_TO")).isEqualTo("/battle");
  }

  @Test
  @DisplayName("should store return_to for nested relative path")
  void shouldStoreReturnToForNestedRelativePath() {
    delegate.login("/leaderboard/cancer-biology");

    assertThat(session.getAttribute("OIDC_RETURN_TO")).isEqualTo("/leaderboard/cancer-biology");
  }

  @Test
  @DisplayName("should not store return_to when return_to is null")
  void shouldNotStoreReturnToWhenReturnToIsNull() {
    delegate.login(null);

    assertThat(session.getAttribute("OIDC_RETURN_TO")).isNull();
  }

  @Test
  @DisplayName("should not store return_to when path is an absolute URL")
  void shouldNotStoreReturnToWhenPathIsAbsoluteUrl() {
    delegate.login("http://evil.com/steal");

    assertThat(session.getAttribute("OIDC_RETURN_TO")).isNull();
  }

  @Test
  @DisplayName("should not store return_to when path is a protocol-relative URL")
  void shouldNotStoreReturnToWhenPathIsProtocolRelativeUrl() {
    delegate.login("//evil.com/steal");

    assertThat(session.getAttribute("OIDC_RETURN_TO")).isNull();
  }

  @Test
  @DisplayName("should not store return_to when path uses javascript scheme")
  void shouldNotStoreReturnToWhenPathUsesJavascriptScheme() {
    delegate.login("javascript:alert(1)");

    assertThat(session.getAttribute("OIDC_RETURN_TO")).isNull();
  }

  @Test
  @DisplayName("should not store return_to when path has no leading slash")
  void shouldNotStoreReturnToWhenPathHasNoLeadingSlash() {
    delegate.login("evil.com");

    assertThat(session.getAttribute("OIDC_RETURN_TO")).isNull();
  }

  @Test
  @DisplayName("should always store OIDC_STATE and OIDC_NONCE regardless of return_to")
  void shouldAlwaysStoreOidcStateAndNonce() {
    delegate.login("/battle");

    assertThat(session.getAttribute("OIDC_STATE")).isNotNull();
    assertThat(session.getAttribute("OIDC_NONCE")).isNotNull();
  }
}
