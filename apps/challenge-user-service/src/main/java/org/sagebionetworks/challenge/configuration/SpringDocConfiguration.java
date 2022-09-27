package org.sagebionetworks.challenge.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

  @Bean
  OpenAPI apiInfo() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Challenge User API")
                .description("This is the User Service of the Challenge Registry.")
                .termsOfService("TOC")
                .contact(
                    new Contact()
                        .name("The Challenge Registry Team")
                        .url("https://github.com/Sage-Bionetworks/challenge-registry"))
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url(
                            "https://github.com/Sage-Bionetworks/challenge-registry/blob/main/LICENSE.txt"))
                .version("1.0.0"));
  }
}
