package org.sagebionetworks.agora.api.next;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@ConfigurationPropertiesScan
@EnableCaching
@SpringBootApplication
public class AgoraApiNextApplication {

  public static void main(String[] args) {
    SpringApplication.run(AgoraApiNextApplication.class, args);
  }
}
