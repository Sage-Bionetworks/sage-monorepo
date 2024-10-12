package org.sagebionetworks.agora.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication
@ComponentScan(basePackages = { "org.sagebionetworks.agora" })
public class AgoraApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(AgoraApiApplication.class, args);
  }
}
