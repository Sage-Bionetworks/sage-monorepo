package org.sagebionetworks.challenge.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  @Bean
  public OpenAPI openApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("User API")
                .description("This is the User Service of the Challenge Registry.")
                .version("v1.0")
                .contact(
                    new Contact().name("This is the User ").url("https://challenge-registry.org"))
                .termsOfService("TOC")
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url(
                            "https://github.com/Sage-Bionetworks/challenge-registry/blob/main/LICENSE.txt")));
  }
}
