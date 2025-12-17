package org.sagebionetworks.model.ad.api.next.configuration;

import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

/**
 * Web MVC configuration for the Model-AD API.
 * Configures path matching to handle encoded slashes in URL paths.
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    UrlPathHelper urlPathHelper = new UrlPathHelper();
    // Disable URL decoding so that %2F remains encoded during path matching
    // This allows model names with forward slashes like "5xFAD (IU/Jax/Pitt)"
    urlPathHelper.setUrlDecode(false);
    urlPathHelper.setRemoveSemicolonContent(false);
    configurer.setUrlPathHelper(urlPathHelper);
  }

  /**
   * Configure Tomcat to allow encoded slashes in URLs.
   * This is required for model names containing forward slashes like "5xFAD (IU/Jax/Pitt)".
   */
  @Bean
  public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
    return factory -> factory.addConnectorCustomizers(
      (TomcatConnectorCustomizer) connector -> {
        // Use passthrough to keep encoded slashes as %2F during Spring path matching
        // We'll decode manually in the controller layer
        connector.setEncodedSolidusHandling("passthrough");
      }
    );
  }
}
