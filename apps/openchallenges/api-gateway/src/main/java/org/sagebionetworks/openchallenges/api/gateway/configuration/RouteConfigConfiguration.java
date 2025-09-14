package org.sagebionetworks.openchallenges.api.gateway.configuration;

import java.util.Map;
import org.sagebionetworks.openchallenges.api.gateway.model.RouteConfig;
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
    Map<RouteKey, RouteConfig> map = props.routes() == null ? Map.of() : props.routes();
    return new RouteConfigRegistry(map);
  }
}
