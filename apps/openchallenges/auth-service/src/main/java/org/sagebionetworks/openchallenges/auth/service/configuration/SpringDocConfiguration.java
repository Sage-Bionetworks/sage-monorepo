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
                                .description("Authentication and authorization service for OpenChallenges.  This service provides: - JWT-based user authentication via username/password - API key management for service-to-service communication - Role-based access control with defined scopes - Token validation for accessing protected resources  **Authentication Methods**: - `/v1/auth/login` - Username/password authentication returning JWT tokens - `/v1/auth/api-keys` - API key management for programmatic access - `/v1/auth/validate` - Token validation for protected resources  **Authorization Scopes**: API access is controlled through role-based permissions and scopes defined below. ")
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
                                .addSecuritySchemes("jwtBearerAuth", new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                )
                                .addSecuritySchemes("apiBearerAuth", new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                )
                )
        ;
    }
}