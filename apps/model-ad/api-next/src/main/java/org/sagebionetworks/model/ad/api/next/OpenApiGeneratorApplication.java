package org.sagebionetworks.model.ad.api.next;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@ComponentScan(
  basePackages = {
    "org.sagebionetworks.model.ad.api.next",
    "org.sagebionetworks.model.ad.api.next.api",
    "org.sagebionetworks.model.ad.api.next.configuration",
  },
  nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@EnableCaching
public class OpenApiGeneratorApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpenApiGeneratorApplication.class, args);
  }
}
