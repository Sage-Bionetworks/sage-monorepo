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
                                .description("Modern OAuth2 and OpenID Connect authentication service for OpenChallenges.  This service provides: - Standard OAuth2 Authorization Server with PKCE support - OpenID Connect for user authentication - API key management for service-to-service communication - Legacy username/password authentication  **OAuth2 Endpoints** (unversioned, standards-compliant): - Use `/oauth2/_*` endpoints for standard OAuth2 flows - Use `/.well-known/_*` endpoints for discovery - Replaces legacy `/v1/auth/jwt/_*` and `/v1/auth/oauth2/_*` endpoints  **Custom API Endpoints** (versioned): - Use `/v1/auth/_*` for domain-specific functionality like API keys - Use `/v1/auth/login` for legacy username/password authentication ")
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
                                .addSecuritySchemes("OAuth2", new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                )
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