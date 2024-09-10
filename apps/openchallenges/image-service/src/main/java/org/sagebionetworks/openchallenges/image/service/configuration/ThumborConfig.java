package org.sagebionetworks.openchallenges.image.service.configuration;

import com.squareup.pollexor.Thumbor;
import org.sagebionetworks.openchallenges.app.config.data.ImageServiceConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThumborConfig {

  private final ImageServiceConfigData imageServiceConfigData;

  public ThumborConfig(ImageServiceConfigData imageServiceConfigData) {
    this.imageServiceConfigData = imageServiceConfigData;
  }

  @Bean
  public Thumbor thumbor() {
    return Thumbor.create(
      this.imageServiceConfigData.getThumborHost(),
      this.imageServiceConfigData.getThumborSecurityKey()
    );
  }
}
