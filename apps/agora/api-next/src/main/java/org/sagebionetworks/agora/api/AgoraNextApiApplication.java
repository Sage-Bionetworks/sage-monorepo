package org.sagebionetworks.agora.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgoraNextApiApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(AgoraNextApiApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(AgoraNextApiApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info("Welcome to BixArena API!");
  }
}
