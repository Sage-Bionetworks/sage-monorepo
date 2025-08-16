package org.sagebionetworks.bixarena.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@ComponentScan(
  basePackages = { "org.sagebionetworks.bixarena.api" },
  nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class BixArenaApiApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(BixArenaApiApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(BixArenaApiApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info("Welcome to BixArena API");
  }
}
