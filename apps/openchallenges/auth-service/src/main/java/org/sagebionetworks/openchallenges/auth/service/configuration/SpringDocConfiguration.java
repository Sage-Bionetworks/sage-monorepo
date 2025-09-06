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
                                .description("Authentication and authorization service for OpenChallenges.  This service provides: - OAuth2 Authorization Server with Google/Synapse login - API key management for programmatic access to OpenChallenges APIs - JWT token validation for API access - User profile management  **OAuth2 Endpoints** (Spring Authorization Server - not documented here): - `GET /oauth2/authorize` - OAuth2 authorization endpoint - `POST /oauth2/token` - Token exchange endpoint - `GET /.well-known/oauth-authorization-server` - OAuth2 discovery  **Custom API Endpoints** (documented below): - `/v1/auth/api-keys` - API key management - `/v1/auth/profile` - User profile management  **Authentication Methods**: - **OAuth2**: Use standard OAuth2 flows for user login via Google/Synapse - **API Keys**: Use for programmatic access to OpenChallenges APIs. API keys inherit the scopes and permissions of the user who created them. - **JWT**: Tokens issued by OAuth2 server for API access  **Scopes**: - `openid`, `profile`, `email`: Standard OpenID Connect scopes - `read:org`: Read access to organization resources - `write:org`: Write access to organization resources - `delete:org`: Delete access to organization resources ")
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
                                .addSecuritySchemes("jwtBearer", new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                )
                )
        ;
    }
}