package org.sagebionetworks.openchallenges.api.gateway.observability;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.api.gateway.routing.RouteConfigRegistry;
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
    // Single-line JSON summary for Grafana/Loki (INFO)
    log.info(RouteConfigPrinter.jsonSummary(registry));

    // Optional: per-route JSON events (each a single line at INFO)
    RouteConfigPrinter.jsonPerRoute(registry).forEach(log::info);
    // Developer-friendly pretty dump at DEBUG
    // if (log.isDebugEnabled()) {
    //   log.debug(RouteConfigPrinter.detailed(registry));
    // }
  }
}
