package org.sagebionetworks.openchallenges.config.server;

import org.sagebionetworks.openchallenges.app.config.data.ConfigServerConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.ComponentScan;

@EnableConfigServer
@SpringBootApplication
@ComponentScan(basePackages = "org.sagebionetworks.openchallenges")
public class ConfigServerApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(ConfigServerApplication.class);

  private final ConfigServerConfigData configServerConfigData;

  public ConfigServerApplication(ConfigServerConfigData configServerConfigData) {
    this.configServerConfigData = configServerConfigData;
  }

  public static void main(String[] args) {
    SpringApplication.run(ConfigServerApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info(configServerConfigData.getWelcomeMessage());
  }
}
