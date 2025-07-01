package org.sagebionetworks.amp.als.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@ComponentScan(
  basePackages = {
    "org.sagebionetworks.amp.als.user.service",
    "org.sagebionetworks.amp.als.user.service.api",
    "org.sagebionetworks.amp.als.user.service.configuration",
  },
  nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class UserServiceApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(UserServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info("Hello, welcome to the User Service!");
  }
}
