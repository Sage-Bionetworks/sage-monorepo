package org.sagebionetworks.bixarena.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.auth.service.configuration.AppProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@ConfigurationPropertiesScan
@EnableRedisHttpSession(
  maxInactiveIntervalInSeconds = 604800,
  redisNamespace = "${spring.session.redis.namespace}"
)
@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
public class BixArenaAuthServiceApplication implements CommandLineRunner {

  private final AppProperties appProperties;

  public static void main(String[] args) {
    SpringApplication.run(BixArenaAuthServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info(appProperties.welcomeMessage());
  }
}
