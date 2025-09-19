package org.sagebionetworks.openchallenges.image.service.configuration;

import com.squareup.pollexor.Thumbor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ThumborConfiguration {

  private final AppProperties appProperties;

  @Bean
  public Thumbor thumbor() {
    return Thumbor.create(
      this.appProperties.thumbor().host(),
      this.appProperties.thumbor().securityKey()
    );
  }
}
