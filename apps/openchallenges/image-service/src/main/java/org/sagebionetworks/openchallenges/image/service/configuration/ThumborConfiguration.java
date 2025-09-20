package org.sagebionetworks.openchallenges.image.service.configuration;

import com.squareup.pollexor.Thumbor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ThumborConfiguration {

  private final AppProperties appProperties;

  @Bean
  public Thumbor thumbor() {
    var props = appProperties.thumbor();

    String host = normalizeHost(props.host());
    String key = props.securityKey();

    Thumbor thumbor = Thumbor.create(host, key);

    log.info("Thumbor configured in SIGNED mode for host={}", host);

    return thumbor;
  }

  /** Ensure scheme is present and remove trailing slash. */
  private static String normalizeHost(String raw) {
    String h = raw.trim();
    if (!h.startsWith("http://") && !h.startsWith("https://")) {
      h = "https://" + h; // default to https
    }
    if (h.endsWith("/")) {
      h = h.substring(0, h.length() - 1);
    }
    return h;
  }
}
