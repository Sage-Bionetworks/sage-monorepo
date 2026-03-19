package org.sagebionetworks.bixarena.auth.service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
public class FlywayConfiguration {
  // Disabled: clean+migrate on every restart destroys auth.user rows, which invalidates
  // active sessions (the session in Valkey still references a user UUID that no longer
  // exists in the DB). To manually clean and re-apply migrations when needed, run:
  //   ./gradlew :bixarena-auth-service:flywayClean :bixarena-auth-service:flywayMigrate
  //
  // @Profile("dev")
  // @Bean
  // public FlywayMigrationStrategy cleanMigrationStrategy() {
  //   return flyway -> {
  //     log.info("Executing custom Flyway migration: clean and migrate (auth schema only)");
  //     flyway.clean();
  //     flyway.migrate();
  //   };
  // }
}
