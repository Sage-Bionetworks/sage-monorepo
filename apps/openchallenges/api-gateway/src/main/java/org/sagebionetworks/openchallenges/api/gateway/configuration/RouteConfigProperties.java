package org.sagebionetworks.openchallenges.api.gateway.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.sagebionetworks.openchallenges.api.gateway.model.dto.RouteSpec;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Binds app2.routes from route-config.yml (imported via spring.config.import).
 */
@Validated
@ConfigurationProperties(prefix = "app")
public record RouteConfigProperties(@NotNull @Valid List<RouteSpec> routes) {}
