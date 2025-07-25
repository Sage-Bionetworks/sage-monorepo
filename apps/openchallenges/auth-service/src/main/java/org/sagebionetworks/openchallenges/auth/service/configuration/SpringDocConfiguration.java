package org.sagebionetworks.openchallenges.auth.service.configuration;

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

    @Bean(name = "org.sagebionetworks.openchallenges.auth.service.configuration.SpringDocConfiguration.apiInfo")
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("OpenChallenges Auth API")
                                .description("Discover, explore, and contribute to open biomedical challenges.")
                                .contact(
                                        new Contact()
                                                .name("Support")
                                                .url("https://github.com/Sage-Bionetworks/sage-monorepo")
                                )
                                .license(
                                        new License()
                                                .name("Apache 2.0")
                                                .url("https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/LICENSE.txt")
                                )
                                .version("1.0.0")
                )
                .components(
                        new Components()
                                .addSecuritySchemes("apiBearerAuth", new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("api_key")
                                )
                )
        ;
    }
}