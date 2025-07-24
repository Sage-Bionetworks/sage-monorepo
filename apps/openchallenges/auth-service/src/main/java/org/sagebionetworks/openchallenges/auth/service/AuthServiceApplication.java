package org.sagebionetworks.openchallenges.auth.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@EnableConfigurationProperties
@ComponentScan(
  basePackages = {
    "org.sagebionetworks.openchallenges.auth.service",
    "org.sagebionetworks.openchallenges.auth.service.api",
    "org.sagebionetworks.openchallenges.auth.service.configuration",
  },
  nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class AuthServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthServiceApplication.class, args);
  }
}
