package org.sagebionetworks.openchallenges.api.gateway.configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import org.sagebionetworks.openchallenges.api.gateway.model.dto.RouteConfig;
import org.sagebionetworks.openchallenges.api.gateway.model.dto.RouteSpec;
import org.sagebionetworks.openchallenges.api.gateway.routing.RouteConfigRegistry;
import org.sagebionetworks.openchallenges.api.gateway.routing.RouteKey;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RouteConfigProperties.class)
public class RouteConfigConfiguration {

  @Bean
  RouteConfigRegistry routeConfigRegistry(RouteConfigProperties props) {
    Map<RouteKey, RouteConfig> typed = new LinkedHashMap<>();

    for (RouteSpec r : props.routes()) {
      RouteKey key = RouteKey.of(r.method(), r.path());
      RouteConfig cfg = new RouteConfig(r.scopes(), r.audience(), r.anonymousAccess());

      if (typed.putIfAbsent(key, cfg) != null) {
        throw new IllegalArgumentException(
          "Duplicate route: %s %s".formatted(key.method(), key.path())
        );
      }
    }

    return new RouteConfigRegistry(typed);
  }
}
