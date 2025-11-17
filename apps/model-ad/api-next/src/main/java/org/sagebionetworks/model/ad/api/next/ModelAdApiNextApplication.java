package org.sagebionetworks.model.ad.api.next;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@ConfigurationPropertiesScan
@EnableCaching
@SpringBootApplication
public class ModelAdApiNextApplication {

  public static void main(String[] args) {
    SpringApplication.run(ModelAdApiNextApplication.class, args);
  }
}
