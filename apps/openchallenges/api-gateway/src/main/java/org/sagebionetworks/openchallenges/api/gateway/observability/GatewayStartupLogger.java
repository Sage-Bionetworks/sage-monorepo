package org.sagebionetworks.openchallenges.api.gateway.observability;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.api.gateway.routing.RouteConfigRegistry;
import org.sagebionetworks.openchallenges.api.gateway.routing.RouteKey;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayStartupLogger {

  private final RouteConfigRegistry registry;

  @EventListener(ApplicationReadyEvent.class)
  public void onReady() {
    int total = registry.size();
    log.info("API Gateway route registry loaded: {} routes", total);

    // Short method breakdown
    var countsByMethod = registry
      .getAllRouteConfigs()
      .keySet()
      .stream()
      .map(RouteKey::method)
      .collect(
        java.util.stream.Collectors.groupingBy(m -> m, java.util.stream.Collectors.counting())
      );
    countsByMethod.forEach((m, c) -> log.info("  - {}: {}", m, c));

    // Optional: detailed listing at DEBUG (avoid spamming INFO)
    if (log.isDebugEnabled()) {
      registry
        .getAllRouteConfigs()
        .forEach((key, cfg) -> {
          log.debug(
            "Route: {} {} | scopes={} | audience={} | anonymous={}",
            key.method(),
            key.path(),
            cfg.scopes(),
            cfg.audience(),
            cfg.anonymousAccess()
          );
        });
    }
  }
}
