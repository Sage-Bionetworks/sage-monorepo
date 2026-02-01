package org.sagebionetworks.bixarena.api.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
public class FlywayConfiguration {

  /**
   * Clean the DB only in dev mode, never in production.
   * With schema separation, this is now safe as it only affects the api schema.
   * This strategy ensures that all migrations are reapplied on every startup in dev,
   * which is useful for testing new migrations and keeping dev database in sync.
   */
  @Profile("dev")
  @Bean
  public FlywayMigrationStrategy cleanMigrationStrategy() {
    return flyway -> {
      log.info("Executing custom Flyway migration: clean and migrate (api schema only)");
      flyway.clean();
      flyway.migrate();
    };
  }
}
