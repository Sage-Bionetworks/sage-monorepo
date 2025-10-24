package org.sagebionetworks.bixarena.api.gateway.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFilter that allows anonymous access for configured public endpoints.
 * Reads the list of anonymous endpoints from the route config file.
 * If the request matches, sets an AnonymousAuthenticationToken in the context.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@RequiredArgsConstructor
public class AnonymousAccessGatewayFilter implements WebFilter {

  private final Set<String> anonymousEndpoints;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().value();
    String method = request.getMethodValue();

    if (isAnonymousAllowed(method, path)) {
      log.debug("Allowing anonymous access for {} {}", method, path);
      Authentication anonymousAuth = new AnonymousAuthenticationToken(
        "anonymousUser",
        "anonymousUser",
        List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
      );
      return chain
        .filter(exchange)
        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(anonymousAuth));
    }
    return chain.filter(exchange);
  }

  private boolean isAnonymousAllowed(String method, String path) {
    // Simple path match; can be extended for method/path matching
    return anonymousEndpoints.contains(path);
  }
}
