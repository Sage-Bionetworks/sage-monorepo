package org.sagebionetworks.amp.als.dataset.service.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(FlywayConfiguration.class);

  // TODO: Clean the DB only in dev mode, never in production.
  // @Profile("dev")
  @Bean
  public FlywayMigrationStrategy cleanMigrationStrategy() {
    return flyway -> {
      logger.info("Executing custom Flyway migration: clean and migrate");
      flyway.clean();
      flyway.migrate();
    };
  }
}
