package org.sagebionetworks.bixarena.auth.service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FlywayConfiguration {

  // TODO: Clean the DB only in dev mode, never in production.
  // @Profile("dev")
  @Bean
  public FlywayMigrationStrategy cleanMigrationStrategy() {
    return flyway -> {
      log.info("Executing custom Flyway migration: clean and migrate");
      flyway.clean();
      flyway.migrate();
    };
  }
}
