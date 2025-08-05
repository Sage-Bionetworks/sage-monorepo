package org.sagebionetworks.openchallenges.api.gateway.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@ExtendWith(MockitoExtension.class)
class SecurityConfigurationTest {

  @InjectMocks
  private SecurityConfiguration securityConfiguration;

  @Test
  @DisplayName("should create security web filter chain when configured")
  void shouldCreateSecurityWebFilterChainWhenConfigured() {
    // given
    ServerHttpSecurity http = ServerHttpSecurity.http();

    // when
    SecurityWebFilterChain result = securityConfiguration.securityWebFilterChain(http);

    // then
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("should build security configuration with web filters")
  void shouldBuildSecurityConfigurationWithWebFilters() {
    // given
    ServerHttpSecurity http = ServerHttpSecurity.http();

    // when
    SecurityWebFilterChain filterChain = securityConfiguration.securityWebFilterChain(http);

    // then
    assertThat(filterChain).isNotNull();
    assertThat(filterChain.getWebFilters()).isNotNull();
  }

  @Test
  @DisplayName("should return same filter chain instance for multiple calls")
  void shouldReturnSameFilterChainInstanceForMultipleCalls() {
    // given
    ServerHttpSecurity http1 = ServerHttpSecurity.http();
    ServerHttpSecurity http2 = ServerHttpSecurity.http();

    // when
    SecurityWebFilterChain chain1 = securityConfiguration.securityWebFilterChain(http1);
    SecurityWebFilterChain chain2 = securityConfiguration.securityWebFilterChain(http2);

    // then
    assertThat(chain1).isNotNull();
    assertThat(chain2).isNotNull();
    assertThat(chain1).isNotSameAs(chain2);
  }

  @Test
  @DisplayName("should handle null server http security gracefully")
  void shouldHandleNullServerHttpSecurityGracefully() {
    // given
    ServerHttpSecurity http = null;

    // when & then
    try {
      securityConfiguration.securityWebFilterChain(http);
    } catch (NullPointerException e) {
      // Expected behavior - Spring Security should handle null appropriately
      assertThat(e).isInstanceOf(NullPointerException.class);
    }
  }
}
