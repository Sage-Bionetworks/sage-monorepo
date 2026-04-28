package org.sagebionetworks.qtl.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@ConfigurationPropertiesScan
@EnableCaching
@SpringBootApplication
public class QtlApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(QtlApiApplication.class, args);
  }
}
