package org.sagebionetworks.amp.als.dataset.service.configuration;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

  // TODO: Only clean the DB in development mode, never in production.
  // @Profile("dev")
  @Bean
  public FlywayMigrationStrategy cleanMigrationStrategy() {
    return flyway -> {
      flyway.clean();
      flyway.migrate();
    };
  }
  // @Autowired
  // public FlywayConfiguration(DataSource dataSource) {
  //   Flyway flyway =
  //       Flyway.configure()
  //           // .baselineOnMigrate(true)
  //           .cleanDisabled(false)
  //           .dataSource(dataSource)
  //           .load();
  //   // flyway.clean();
  //   // flyway.migrate();
  // }
}
