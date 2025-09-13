package org.sagebionetworks.openchallenges.api.gateway.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Application configuration properties.
 *
 * All properties are prefixed with 'app.'
 * Properties are validated on application startup.
 */
@Component
@ConfigurationProperties(prefix = "app")
@Data
@Validated
public class AppProperties {

  @NotBlank(message = "Welcome message must not be blank")
  private String welcomeMessage = "Welcome to OpenChallenges API Gateway!";

  @Valid
  private AuthConfiguration auth = new AuthConfiguration();

  @Data
  public static class AuthConfiguration {

    @NotBlank(message = "Realm must not be blank")
    private String realm = "OpenChallenges";

    @NotBlank(message = "Service URL must not be blank")
    private String serviceUrl = "http://openchallenges-auth-service:8087/v1";
  }
}
