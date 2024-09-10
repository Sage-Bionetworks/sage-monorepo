package org.sagebionetworks.modelad.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
  basePackages = {
    "org.sagebionetworks.modelad.api",
    "org.sagebionetworks.modelad.api.api",
    "org.sagebionetworks.modelad.api.configuration",
  }
)
public class OpenApiGeneratorApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpenApiGeneratorApplication.class, args);
  }
}
