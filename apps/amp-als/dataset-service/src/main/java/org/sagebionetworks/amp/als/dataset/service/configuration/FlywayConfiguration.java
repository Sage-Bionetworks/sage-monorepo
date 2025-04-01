package org.sagebionetworks.amp.als.dataset.service.configuration;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

  // TODO: Clean the DB only in dev mode, never in production.
  // @Profile("dev")
  @Bean
  public FlywayMigrationStrategy cleanMigrationStrategy() {
    return flyway -> {
      flyway.clean();
      flyway.migrate();
    };
  }
}
