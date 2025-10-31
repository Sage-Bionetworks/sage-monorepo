package org.sagebionetworks.bixarena.auth.service.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SessionConfiguration {

  private final AppProperties appProperties;

  @Bean
  public CookieSerializer cookieSerializer() {
    var config = appProperties.sessionCookie();
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setCookieName(config.name());
    serializer.setCookiePath(config.path());
    // Set domain if configured (null = host-only cookie)
    if (config.domain() != null && !config.domain().isBlank()) {
      serializer.setDomainName(config.domain());
      log.info(
        "Session cookie configured with domain: {} (cookies shared across ports)",
        config.domain()
      );
    } else {
      log.info("Session cookie configured as host-only (no domain attribute)");
    }
    serializer.setSameSite(config.sameSite());
    serializer.setUseSecureCookie(config.secure());
    serializer.setUseHttpOnlyCookie(config.httpOnly());

    log.info(
      "Session cookie configuration: name={}, path={}, sameSite={}, secure={}, httpOnly={}",
      config.name(),
      config.path(),
      config.sameSite(),
      config.secure(),
      config.httpOnly()
    );
    return serializer;
  }
}
