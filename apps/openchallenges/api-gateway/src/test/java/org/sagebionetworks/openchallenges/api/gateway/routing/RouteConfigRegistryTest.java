package org.sagebionetworks.openchallenges.api.gateway.routing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.api.gateway.model.dto.RouteConfig;

class RouteConfigRegistryTest {

  @Test
  void patternRouteShouldMatchConcretePath() {
    RouteKey key = RouteKey.of("GET", "/api/v1/challenges/{challengeId}");
    RouteConfig cfg = new RouteConfig(Set.of("read:challenges"), "urn:openchallenges:challenge-service", true);
    RouteConfigRegistry registry = new RouteConfigRegistry(Map.of(key, cfg));

    assertThat(registry.isAnonymousAccessAllowed("GET", "/api/v1/challenges/1"))
      .as("anonymous access should be resolved through path variable template")
      .isTrue();

    assertThat(registry.getScopesForRoute("GET", "/api/v1/challenges/123"))
      .containsExactly("read:challenges");

    assertThat(registry.getAudienceForRoute("GET", "/api/v1/challenges/abc"))
      .contains("urn:openchallenges:challenge-service");
  }

  @Test
  void nonMatchingPatternShouldNotMatch() {
    RouteKey key = RouteKey.of("GET", "/api/v1/challenges/{challengeId}");
    RouteConfig cfg = new RouteConfig(Set.of("read:challenges"), null, true);
    RouteConfigRegistry registry = new RouteConfigRegistry(Map.of(key, cfg));

    assertThat(registry.getRouteConfig("GET", "/api/v1/challenges")).isEmpty(); // missing segment
  }

  @Test
  void multiVariablePatternShouldMatch() {
    RouteKey key = RouteKey.of(
      "GET",
      "/api/v1/organizations/{org}/participations/{challengeId}/roles/{role}"
    );
    RouteConfig cfg = new RouteConfig(Set.of("update:organizations"), "urn:openchallenges:organization-service", false);
    RouteConfigRegistry registry = new RouteConfigRegistry(Map.of(key, cfg));

    String concretePath = "/api/v1/organizations/acme/participations/42/roles/admin";
    assertThat(registry.getRouteConfig("GET", concretePath)).isPresent();
    assertThat(registry.getScopesForRoute("GET", concretePath)).containsExactly("update:organizations");
    assertThat(registry.getAudienceForRoute("GET", concretePath))
      .contains("urn:openchallenges:organization-service");
  }
}
