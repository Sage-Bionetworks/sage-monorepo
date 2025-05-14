package org.sagebionetworks.agora.gene.api;

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
    "org.sagebionetworks.agora.gene.api",
    "org.sagebionetworks.agora.gene.api.api",
    "org.sagebionetworks.agora.gene.api.configuration",
  },
  nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class GeneApiApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(GeneApiApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(GeneApiApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info("Hello, welcome to the Gene API!");
  }
}
