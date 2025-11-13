package org.sagebionetworks.bixarena.api.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter to log response headers, particularly Set-Cookie headers.
 *
 * <p>This filter runs AFTER the response is received from downstream services but BEFORE it's sent
 * to the client. It helps debug issues with cookie handling and header propagation.
 *
 * <p>Order: LOWEST_PRECEDENCE - 1 (runs after most filters but before final response)
 */
@Component
public class ResponseHeaderLoggingFilter implements GlobalFilter, Ordered {

  private static final Logger log = LoggerFactory.getLogger(ResponseHeaderLoggingFilter.class);

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String path = exchange.getRequest().getPath().value();
    String method = exchange.getRequest().getMethod().name();

    // Process the request and log response headers
    return chain
        .filter(exchange)
        .then(
            Mono.fromRunnable(
                () -> {
                  HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
                  int statusCode = exchange.getResponse().getStatusCode().value();

                  // Log Set-Cookie headers for debugging
                  if (responseHeaders.containsKey(HttpHeaders.SET_COOKIE)) {
                    var setCookies = responseHeaders.get(HttpHeaders.SET_COOKIE);
                    log.debug(
                        "Response headers for {} {}: status={} Set-Cookie count={}",
                        method,
                        path,
                        statusCode,
                        setCookies != null ? setCookies.size() : 0);
                    if (setCookies != null) {
                      for (int i = 0; i < setCookies.size(); i++) {
                        String cookieValue = setCookies.get(i);
                        // Log first 100 chars of cookie for debugging (don't log full value for
                        // security)
                        String preview =
                            cookieValue.length() > 100
                                ? cookieValue.substring(0, 100) + "..."
                                : cookieValue;
                        log.debug("  Set-Cookie[{}]: {}", i, preview);
                      }
                    }
                  } else {
                    log.debug(
                        "Response headers for {} {}: status={} (no Set-Cookie headers)",
                        method,
                        path,
                        statusCode);
                  }
                }));
  }

  @Override
  public int getOrder() {
    // Run after most filters but before final response
    // LOWEST_PRECEDENCE - 1 ensures this runs late in the chain
    return Ordered.LOWEST_PRECEDENCE - 1;
  }
}
