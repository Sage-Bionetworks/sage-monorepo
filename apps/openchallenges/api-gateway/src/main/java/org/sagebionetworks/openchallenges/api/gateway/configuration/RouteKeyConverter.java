package org.sagebionetworks.openchallenges.api.gateway.configuration;

import org.sagebionetworks.openchallenges.api.gateway.routing.RouteKey;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Spring Converter to let @ConfigurationProperties bind string keys
 * like "GET /challenges" into RouteKey instances automatically.
 */
@Component
@ConfigurationPropertiesBinding
public class RouteKeyConverter implements Converter<String, RouteKey> {

  @Override
  public RouteKey convert(String source) {
    if (source == null || source.isBlank()) {
      return null;
    }
    String[] parts = source.trim().split("\\s+", 2);
    if (parts.length != 2) {
      throw new IllegalArgumentException("Invalid route key (expected 'METHOD /path'): " + source);
    }
    return RouteKey.of(parts[0], parts[1]);
  }
}
