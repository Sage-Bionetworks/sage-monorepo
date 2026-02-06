package org.sagebionetworks.openchallenges.organization.service.configuration;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FlywayConfiguration {

  // Only clean the DB in development mode, never in production.
  @Profile("dev")
  @Bean
  public FlywayMigrationStrategy cleanMigrationStrategy() {
    return flyway -> {
      flyway.clean();
      flyway.migrate();
    };
  }
}
