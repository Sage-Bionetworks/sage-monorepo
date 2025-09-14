package org.sagebionetworks.openchallenges.api.gateway.configuration;

import java.util.Map;
import org.sagebionetworks.openchallenges.api.gateway.model.RouteConfig;
import org.sagebionetworks.openchallenges.api.gateway.routing.RouteKey;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.route-config")
public record RouteConfigProperties(Map<RouteKey, RouteConfig> routes) {}
