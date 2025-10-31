package org.sagebionetworks.bixarena.auth.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionConfiguration {

  @Bean
  public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setCookieName("JSESSIONID"); // Keep standard name for compatibility
    serializer.setCookiePath("/");
    // Set explicit domain to share cookies across ports (127.0.0.1:7860, 127.0.0.1:8113, etc.)
    serializer.setDomainName("127.0.0.1");
    serializer.setSameSite("Lax"); // Use Lax for HTTP; allows top-level navigation
    serializer.setUseSecureCookie(false); // False for local HTTP development
    serializer.setUseHttpOnlyCookie(true); // Security: prevent JavaScript access
    return serializer;
  }
}
