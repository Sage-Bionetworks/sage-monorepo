package org.sagebionetworks.challenge.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfiguration {

  @Bean
  public OpenAPI openApi() {
    return new OpenAPI()
        .info(new Info()
            .title("Title")
            .description("my little API")
            .version("v1.0")
            .contact(new Contact()
                .name("Challenge Registry Team")
                .url("https://challenge-registry.org")
                .email("thomas.schaffter@sagebionetworks.org"))
            .termsOfService("TOC")
            .license(new License().name("License").url("#")));
  }
}
