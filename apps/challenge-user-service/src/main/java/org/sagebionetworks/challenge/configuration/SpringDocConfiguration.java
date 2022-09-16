package org.sagebionetworks.challenge.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfiguration {

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("User API")
                                .description("This is the User Service of the Challenge Registry.")
                                .termsOfService("TOC")
                                .contact(
                                        new Contact()
                                                .name("This is the User ")
                                                .url("https://challenge-registry.org")
                                )
                                .license(
                                        new License()
                                                .name("Apache 2.0")
                                                .url("https://github.com/Sage-Bionetworks/challenge-registry/blob/main/LICENSE.txt")
                                )
                                .version("v1.0")
                )
        ;
    }
}