# OpenChallenges Authentication Implementation Plan

## Overview

This document outlines the implementation plan for upgrading the OpenChallenges authentication system to support:

1. **JWT-Centric Authentication** with API Key Generation for the auth service
2. **OAuth2/OIDC Integration** (Google and Synapse)
3. **User-Delegated MCP Authentication** for the MCP server

## OpenAPI-First Development Approach

OpenChallenges follows an **API-spec first approach** where:

- **OpenAPI source files** are in `libs/openchallenges/api-description/src/`
- **Generated OpenAPI specs** are in `libs/openchallenges/api-description/openapi/` (auto-generated, never edit directly)
- **Code generation** uses the generated specs to create server stubs and API clients
- **All changes** to APIs must start with source file updates in `src/`

### OpenAPI Development Workflow

```
1. Edit source files in: libs/openchallenges/api-description/src/
2. Build API specs: nx build openchallenges-api-description
3. Generate code: nx run <project-name>:generate
4. Implement business logic in generated delegate classes
```

### Current OpenAPI Structure

```
libs/openchallenges/api-description/
├── src/                             # ⭐ Edit these source files
│   ├── auth/                        # Auth service source specs
│   ├── challenge/                   # Challenge service source specs
│   └── organization/                # Organization service source specs
└── openapi/                         # 🚫 Auto-generated (never edit directly)
    ├── auth-service.openapi.yaml    # Generated auth service spec
    ├── challenge-service.openapi.yaml # Generated challenge service spec
    └── openapi.yaml                 # Combined public API spec
```

### Code Generation Commands

```bash
# 1. Build API specifications from source
nx build openchallenges-api-description

# 2. Generate server stubs and clients
nx run openchallenges-auth-service:generate
nx run openchallenges-api-client-java:generate
nx run openchallenges-api-client-angular:generate
nx run openchallenges-api-client-python:generate
```

## Architecture Overview

### Current State

```
User Login (username/password) → API Key Generation → API Key Validation (Stateless)
```

### Target State

```
┌─────────────────────────────────────────────────────────────────┐
│                    Authentication Methods                        │
├─────────────────────────────────────────────────────────────────┤
│  Local Login    │  Google OAuth2   │  Synapse OIDC              │
│  (existing)     │  (new)          │  (new)                     │
└─────────────────┴─────────────────┴────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    JWT-Centric Auth Service                     │
├─────────────────────────────────────────────────────────────────┤
│  • Primary: JWT tokens (15-60 min expiry)                      │
│  • Secondary: API keys (user-generated, long-lived)            │
│  • Unified user identity store                                 │
│  • OAuth2/OIDC provider integration                            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    MCP Server Integration                       │
├─────────────────────────────────────────────────────────────────┤
│  • User authentication via MCP tools                           │
│  • JWT token management                                        │
│  • Permission-based operations                                 │
│  • OAuth2 support for external providers                       │
└─────────────────────────────────────────────────────────────────┘
```

## Phase 1: JWT-Centric Auth Service Implementation

### 1.0 OpenAPI Specification Updates (API-First)

#### Tasks:

- [x] **1.0.1** Update OpenAPI source files in `libs/openchallenges/api-description/src/`
- [x] **1.0.2** Add OAuth2 authentication endpoints to auth service spec
- [x] **1.0.3** Update existing login endpoint response schema for JWT
- [x] **1.0.4** Add JWT validation and refresh endpoints
- [x] **1.0.5** Run `nx build openchallenges-api-description` to generate specs
- [x] **1.0.6** Regenerate API clients with `nx run <project-name>:generate`

#### ✅ **COMPLETED** - OpenAPI specifications updated with:

- JWT-based LoginResponse with accessToken, refreshToken, tokenType, expiresIn
- OAuth2 endpoints: `/auth/oauth2/authorize` and `/auth/oauth2/callback`
- JWT management: `/auth/jwt/validate` and `/auth/jwt/refresh`
- New DTOs: OAuth2AuthorizeRequest/Response, OAuth2CallbackRequest, ValidateJwtRequest/Response, RefreshTokenRequest/Response
- Updated security schemes with JwtBearerAuth
- Generated Java DTOs and API interfaces successfully

#### OpenAPI Changes Required:

**auth-service.openapi.yaml additions:**

```yaml
paths:
  /auth/login:
    post:
      # Update existing endpoint to return JWT + API key
      responses:
        '200':
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/JwtLoginResponse'

  /auth/oauth2/{provider}/authorize:
    post:
      summary: Initiate OAuth2 authentication
      parameters:
        - name: provider
          in: path
          required: true
          schema:
            type: string
            enum: [google, synapse]
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/OAuth2AuthorizeRequest'
      responses:
        '200':
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OAuth2AuthorizeResponse'

  /auth/oauth2/{provider}/callback:
    post:
      summary: Complete OAuth2 authentication
      parameters:
        - name: provider
          in: path
          required: true
          schema:
            type: string
            enum: [google, synapse]
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/OAuth2CallbackRequest'
      responses:
        '200':
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/JwtLoginResponse'

  /auth/jwt/validate:
    post:
      summary: Validate JWT token
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ValidateJwtRequest'
      responses:
        '200':
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ValidateJwtResponse'

  /auth/jwt/refresh:
    post:
      summary: Refresh JWT token
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RefreshTokenRequest'
      responses:
        '200':
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/JwtLoginResponse'

components:
  schemas:
    JwtLoginResponse:
      type: object
      properties:
        accessToken:
          type: string
          description: JWT access token
        refreshToken:
          type: string
          description: JWT refresh token
        tokenType:
          type: string
          enum: [Bearer]
        expiresIn:
          type: integer
          description: Token expiry in seconds
        user:
          $ref: '#/components/schemas/User'
        apiKey:
          type: string
          description: Optional API key for backward compatibility
      required:
        - accessToken
        - refreshToken
        - tokenType
        - expiresIn
        - user

    OAuth2AuthorizeRequest:
      type: object
      properties:
        redirectUri:
          type: string
          format: uri
        state:
          type: string
      required:
        - redirectUri

    OAuth2AuthorizeResponse:
      type: object
      properties:
        authorizationUrl:
          type: string
          format: uri
        state:
          type: string
      required:
        - authorizationUrl

    OAuth2CallbackRequest:
      type: object
      properties:
        authorizationCode:
          type: string
        redirectUri:
          type: string
          format: uri
        state:
          type: string
      required:
        - authorizationCode
        - redirectUri

    ValidateJwtRequest:
      type: object
      properties:
        token:
          type: string
      required:
        - token

    ValidateJwtResponse:
      type: object
      properties:
        valid:
          type: boolean
        userId:
          type: string
          format: uuid
        username:
          type: string
        role:
          type: string
        scopes:
          type: array
          items:
            type: string
        expiresAt:
          type: string
          format: date-time
      required:
        - valid

    RefreshTokenRequest:
      type: object
      properties:
        refreshToken:
          type: string
      required:
        - refreshToken

    User:
      type: object
      properties:
        id:
          type: string
          format: uuid
        username:
          type: string
        email:
          type: string
          format: email
        displayName:
          type: string
        role:
          type: string
          enum: [admin, user, readonly, service]
        authProvider:
          type: string
          enum: [local, google, synapse]
        enabled:
          type: boolean
      required:
        - id
        - username
        - role
        - authProvider
        - enabled
```

#### Code Generation Commands:

```bash
# Generate auth service server stubs
cd apps/openchallenges/auth-service
npx @openapitools/openapi-generator-cli generate

# Generate Java API client
cd libs/openchallenges/api-client-java
npx @openapitools/openapi-generator-cli generate

# Generate Angular API client
cd libs/openchallenges/api-client-angular
npx @openapitools/openapi-generator-cli generate

# Generate Python API client
cd libs/openchallenges/api-client-python
npx @openapitools/openapi-generator-cli generate
```

### 1.1 Database Schema Extensions

#### Tasks:

- [x] **1.1.1** Create database migration V1.1.0
- [x] **1.1.2** Extend `app_user` table with OAuth2 fields
- [x] **1.1.3** Create `user_external_account` table (implemented as `external_account`)
- [x] **1.1.4** Add indexes for performance
- [ ] **1.1.5** Test migration with existing data

#### Database Changes:

```sql
-- V1.1.0__add_oauth_jwt_support.sql
ALTER TABLE app_user
    ADD COLUMN auth_provider VARCHAR(50) DEFAULT 'local',
    ADD COLUMN external_id VARCHAR(255),
    ADD COLUMN email VARCHAR(255),
    ADD COLUMN display_name VARCHAR(255),
    ALTER COLUMN password_hash DROP NOT NULL;

CREATE TABLE user_external_account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_user(id),
    provider VARCHAR(50) NOT NULL,
    external_id VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    display_name VARCHAR(255),
    provider_metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE(provider, external_id)
);

CREATE INDEX idx_user_external_account_provider_external_id
    ON user_external_account(provider, external_id);
CREATE INDEX idx_user_auth_provider ON app_user(auth_provider);
CREATE INDEX idx_user_external_id ON app_user(external_id);
CREATE INDEX idx_user_email ON app_user(email);
```

### 1.2 Add OAuth2/JWT Dependencies

#### Tasks:

- [x] **1.2.1** Update `gradle/libs.versions.toml` with OAuth2 dependencies
- [x] **1.2.2** Update auth service `build.gradle.kts`
- [ ] **1.2.3** Configure Spring Security OAuth2 properties

#### Dependencies to Add:

```gradle
// In gradle/libs.versions.toml
spring-boot-starter-oauth2-client = { module = "org.springframework.boot:spring-boot-starter-oauth2-client", version.ref = "spring-boot" }
spring-security-oauth2-jose = { module = "org.springframework.security:spring-security-oauth2-jose", version.ref = "spring-security" }
spring-boot-starter-webflux = { module = "org.springframework.boot:spring-boot-starter-webflux", version.ref = "spring-boot" }

// In auth-service/build.gradle.kts
implementation(libs.spring.boot.starter.oauth2.client)
implementation(libs.spring.security.oauth2.jose)
implementation(libs.spring.boot.starter.webflux)
```

### 1.3 Update Entity Models

#### Tasks:

- [x] **1.3.1** Update `User` entity with OAuth2 fields
- [x] **1.3.2** Create `UserExternalAccount` entity (implemented as `ExternalAccount`)
- [x] **1.3.3** Add `AuthProvider` enum (implemented as `ExternalAccount.Provider`)
- [x] **1.3.4** Update user repository methods
- [x] **1.3.5** Add validation and constraints

#### Key Files:

- `User.java` - Add OAuth2 fields and relationships
- `UserExternalAccount.java` - New entity for external account linking
- `UserRepository.java` - Add OAuth2 query methods

### 1.4 JWT Service Implementation

#### Tasks:

- [x] **1.4.1** Create `JwtService` for token generation/validation
- [x] **1.4.2** Configure JWT properties (secret, expiration times)
- [x] **1.4.3** Implement token refresh mechanism
- [ ] **1.4.4** Add JWT security filter
- [ ] **1.4.5** Update security configuration

#### Key Components:

```java
@Service
public class JwtService {
  // JWT token generation
  // Token validation
  // Token refresh
  // Claims extraction
}

@Component
public class JwtAuthenticationFilter {
  // JWT token extraction from requests
  // Token validation
  // Security context setup
}

```

### 1.5 OAuth2 Integration

#### Tasks:

- [x] **1.5.1** Configure OAuth2 client registrations (OAuth2ConfigurationService)
- [x] **1.5.2** Create OAuth2 service layer
- [ ] **1.5.3** Implement Google OAuth2 integration (placeholder implemented)
- [ ] **1.5.4** Implement Synapse OIDC integration (placeholder implemented)
- [x] **1.5.5** Add account linking functionality (basic structure)

#### Key Components:

```java
@Service
public class OAuth2AuthenticationService {
  // OAuth2 authorization URL generation
  // Authorization code exchange
  // User info retrieval
  // Account creation/linking
}

```

### 1.6 Update API Endpoints

#### Tasks:

- [x] **1.6.1** ✅ Update OpenAPI specification (completed in 1.0)
- [x] **1.6.2** ✅ Regenerate server stubs (completed in 1.0)
- [x] **1.6.3** Implement `AuthenticationApiDelegateImpl` methods (service layer complete)
- [ ] **1.6.4** Implement OAuth2 endpoint delegates (need API delegate layer)
- [x] **1.6.5** Update existing login implementation (AuthenticationService complete)
- [ ] **1.6.6** Test all endpoint implementations

#### Implementation Notes:

- Server stubs and DTOs are auto-generated from OpenAPI spec
- Only implement business logic in `*ApiDelegateImpl` classes
- Do not modify generated `*Api`, `*ApiController`, or DTO classes
- Use generated DTOs for request/response mapping

#### Key Implementation Files:

```java
// Generated (do not modify)
AuthenticationApi.java
AuthenticationApiController.java
OAuth2AuthorizeRequestDto.java
JwtLoginResponseDto.java

// Implement business logic here
AuthenticationApiDelegateImpl.java
OAuth2AuthenticationApiDelegateImpl.java  // New
```

### 1.7 Update Authentication Flow

#### Tasks:

- [x] **1.7.1** Modify login response to include JWT (LoginResponseDto structure ready)
- [x] **1.7.2** Maintain backward compatibility for API keys (AuthenticationService supports both)
- [ ] **1.7.3** Update API key generation to work with JWT auth
- [x] **1.7.4** Add refresh token support (RefreshToken entity and service complete)
- [ ] **1.7.5** Update security filter chain

#### Response Format:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "def50200e3b4c...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": "uuid",
    "username": "john.doe",
    "email": "john.doe@example.com",
    "role": "user"
  },
  "apiKey": "oc_dev_abc123..." // Optional, for backward compatibility
}
```

### 1.8 Testing and Validation

#### Tasks:

- [ ] **1.8.1** Unit tests for JWT service
- [ ] **1.8.2** Integration tests for OAuth2 flows
- [ ] **1.8.3** Security tests for token validation
- [ ] **1.8.4** Backward compatibility tests
- [ ] **1.8.5** Load testing for token performance

## Phase 2: MCP Server User-Delegated Authentication

### 2.1 MCP Server Dependencies

#### Tasks:

- [ ] **2.1.1** ✅ Update OpenChallenges API client dependency (generated from 1.0)
- [ ] **2.1.2** Add authentication dependencies to MCP server
- [ ] **2.1.3** Add OpenChallenges auth client dependency
- [ ] **2.1.4** Configure security properties

#### Dependencies:

```gradle
// In mcp-server/build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-security")
implementation("org.springframework.security:spring-security-oauth2-jose")
implementation(project(":openchallenges-api-client-java")) // Updated with JWT support
```

#### API Client Benefits:

- Auto-generated `AuthenticationApi` client with JWT endpoints
- Type-safe DTOs for all authentication requests/responses
- Built-in OAuth2 support via generated client methods

### 2.2 Authentication Manager

#### Tasks:

- [ ] **2.2.1** Create `UserAuthenticationManager`
- [ ] **2.2.2** Implement session management
- [ ] **2.2.3** Add token refresh logic
- [ ] **2.2.4** Create authentication tools

#### Key Components:

```java
@Component
public class UserAuthenticationManager {

  @Tool("authenticate")
  public AuthenticationResult authenticate(String username, String password);

  @Tool("getAuthenticationStatus")
  public AuthenticationStatus getAuthenticationStatus();

  @Tool("logout")
  public LogoutResult logout();
}

```

### 2.3 OAuth2 Support for MCP

#### Tasks:

- [ ] **2.3.1** Create OAuth2 authentication tools
- [ ] **2.3.2** Implement authorization URL generation
- [ ] **2.3.3** Add callback handling
- [ ] **2.3.4** Support Google and Synapse providers

#### OAuth2 Tools:

```java
@Component
public class OAuth2AuthenticationManager {

  @Tool("getGoogleLoginUrl")
  public OAuth2LoginUrl getGoogleLoginUrl();

  @Tool("getSynapseLoginUrl")
  public OAuth2LoginUrl getSynapseLoginUrl();

  @Tool("completeOAuth2Login")
  public AuthenticationResult completeOAuth2Login(String provider, String authCode);
}

```

### 2.4 Authenticated API Client

#### Tasks:

- [ ] **2.4.1** Create authenticated API client wrapper
- [ ] **2.4.2** Implement automatic token refresh
- [ ] **2.4.3** Add permission validation
- [ ] **2.4.4** Update existing service classes

#### Implementation:

```java
@Component
public class AuthenticatedApiClientFactory {

  public ApiClient createAuthenticatedClient();

  public ApiClient createPublicClient();

  private void handleAuthenticationError();
}

```

### 2.5 Update MCP Tools

#### Tasks:

- [ ] **2.5.1** Update `ChallengeService` with auth support
- [ ] **2.5.2** Update `OrganizationService` with auth support
- [ ] **2.5.3** Add permission-based operation validation
- [ ] **2.5.4** Implement read/write operation separation
- [ ] **2.5.5** Add comprehensive error handling

#### Tool Categories:

```java
// Read-only tools (no auth required)
@Tool("searchChallenges")
@Tool("getChallengeDetails")
@Tool("listOrganizations")

// Write tools (authentication required)
@Tool("createChallenge")
@Tool("updateChallenge")
@Tool("deleteChallenge")
@Tool("createOrganization")

// Admin tools (admin role required)
@Tool("deleteOrganization")
@Tool("manageUsers")
```

### 2.6 Configuration and Properties

#### Tasks:

- [ ] **2.6.1** Add authentication configuration properties
- [ ] **2.6.2** Configure OAuth2 client settings
- [ ] **2.6.3** Add environment variable support
- [ ] **2.6.4** Update Docker configuration

#### Configuration:

```yaml
openchallenges-mcp-server:
  auth-service-url: ${AUTH_SERVICE_URL:http://openchallenges-auth-service:8080/v1}
  oauth2:
    google:
      client-id: ${GOOGLE_CLIENT_ID}
      client-secret: ${GOOGLE_CLIENT_SECRET}
    synapse:
      client-id: ${SYNAPSE_CLIENT_ID}
      client-secret: ${SYNAPSE_CLIENT_SECRET}
  security:
    token-refresh-threshold: 300
    session-timeout: 3600
```

## Phase 3: Integration and Testing

### 3.1 End-to-End Integration

#### Tasks:

- [ ] **3.1.1** Validate all API client generations (Java, Angular, Python)
- [ ] **3.1.2** Test complete authentication flow
- [ ] **3.1.3** Validate JWT token exchange
- [ ] **3.1.4** Test OAuth2 integrations
- [ ] **3.1.5** Verify permission enforcement
- [ ] **3.1.6** Test MCP tool operations

#### API Client Validation:

```bash
# Verify all clients generate successfully
cd libs/openchallenges/api-client-java && npm run generate
cd libs/openchallenges/api-client-angular && npm run generate
cd libs/openchallenges/api-client-python && npm run generate

# Test client compilation
cd libs/openchallenges/api-client-java && ./gradlew build
cd libs/openchallenges/api-client-angular && npm run build
cd libs/openchallenges/api-client-python && python -m build
```

- [ ] **3.1.2** Validate JWT token exchange
- [ ] **3.1.3** Test OAuth2 integrations
- [ ] **3.1.4** Verify permission enforcement
- [ ] **3.1.5** Test MCP tool operations

### 3.2 Security Validation

#### Tasks:

- [ ] **3.2.1** Security audit of JWT implementation
- [ ] **3.2.2** OAuth2 security review
- [ ] **3.2.3** Token expiration testing
- [ ] **3.2.4** Permission boundary testing
- [ ] **3.2.5** OWASP security checklist

### 3.3 Performance Testing

#### Tasks:

- [ ] **3.3.1** JWT token performance benchmarks
- [ ] **3.3.2** Database migration performance
- [ ] **3.3.3** OAuth2 flow performance
- [ ] **3.3.4** MCP server response times
- [ ] **3.3.5** Load testing under authentication load

### 3.4 Documentation

#### Tasks:

- [ ] **3.4.1** Update API documentation
- [ ] **3.4.2** Create OAuth2 setup guides
- [ ] **3.4.3** MCP server authentication guide
- [ ] **3.4.4** Migration guide for existing users
- [ ] **3.4.5** Troubleshooting documentation

## Phase 4: Deployment and Migration

### 4.1 Environment Setup

#### Tasks:

- [ ] **4.1.1** Configure OAuth2 applications (Google, Synapse)
- [ ] **4.1.2** Set up environment variables
- [ ] **4.1.3** Update Docker configurations
- [ ] **4.1.4** Configure load balancer settings
- [ ] **4.1.5** Set up monitoring and alerting

### 4.2 Data Migration

#### Tasks:

- [ ] **4.2.1** Backup existing authentication data
- [ ] **4.2.2** Run database migrations
- [ ] **4.2.3** Migrate existing API keys
- [ ] **4.2.4** Validate data integrity
- [ ] **4.2.5** Test rollback procedures

### 4.3 Gradual Rollout

#### Tasks:

- [ ] **4.3.1** Deploy to development environment
- [ ] **4.3.2** Deploy to staging environment
- [ ] **4.3.3** Limited production rollout
- [ ] **4.3.4** Monitor authentication metrics
- [ ] **4.3.5** Full production deployment

## Implementation Checklist Summary

### Phase 1: JWT-Centric Auth Service

- [ ] **OpenAPI specification updates (1.0)** ⭐ **Start Here - API-First**
- [ ] Database schema extensions (1.1)
- [ ] Add OAuth2/JWT dependencies (1.2)
- [ ] Update entity models (1.3)
- [ ] JWT service implementation (1.4)
- [ ] OAuth2 integration (1.5)
- [ ] Update API endpoints (1.6)
- [ ] Update authentication flow (1.7)
- [ ] Testing and validation (1.8)

### Phase 2: MCP Server Authentication

- [ ] MCP server dependencies (2.1)
- [ ] Authentication manager (2.2)
- [ ] OAuth2 support for MCP (2.3)
- [ ] Authenticated API client (2.4)
- [ ] Update MCP tools (2.5)
- [ ] Configuration and properties (2.6)

### Phase 3: Integration and Testing

- [ ] End-to-end integration (3.1)
- [ ] Security validation (3.2)
- [ ] Performance testing (3.3)
- [ ] Documentation (3.4)

### Phase 4: Deployment and Migration

- [ ] Environment setup (4.1)
- [ ] Data migration (4.2)
- [ ] Gradual rollout (4.3)

## Key Design Decisions

### Authentication Strategy

- **Primary**: JWT tokens for interactive sessions (15-60 minutes)
- **Secondary**: API keys for automation and long-lived access
- **Backward Compatibility**: Existing API key workflows remain unchanged

### OAuth2 Providers

- **Google**: Standard OAuth2 implementation
- **Synapse**: OIDC implementation with Sage Bionetworks integration

### MCP Authentication

- **User-Delegated**: Each user authenticates with their own credentials
- **Permission-Based**: Operations validated against user roles and scopes
- **Session Management**: In-memory session storage with automatic cleanup

### Security Considerations

- JWT signing with HS256 (configurable)
- Automatic token refresh
- Secure credential handling
- Audit logging for all authentication events
- CSRF protection for OAuth2 flows

## Environment Variables

```bash
# Auth Service
JWT_SECRET=your-jwt-secret-key
JWT_ACCESS_TOKEN_EXPIRY=3600
JWT_REFRESH_TOKEN_EXPIRY=604800

# OAuth2 Providers
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
SYNAPSE_CLIENT_ID=your-synapse-client-id
SYNAPSE_CLIENT_SECRET=your-synapse-client-secret

# MCP Server
AUTH_SERVICE_URL=http://openchallenges-auth-service:8080/v1
MCP_OAUTH_REDIRECT_URI=http://localhost:3000/auth/callback
```

## Success Criteria

1. **Functional**: Users can authenticate via local credentials, Google, or Synapse
2. **Security**: All authentication methods use secure JWT tokens
3. **Compatibility**: Existing API key workflows continue to work
4. **Performance**: Authentication adds <100ms latency to requests
5. **Usability**: MCP server provides clear authentication instructions
6. **Reliability**: System handles authentication failures gracefully
7. **Scalability**: Architecture supports horizontal scaling

## Rollback Plan

1. **Database**: Flyway migrations are reversible
2. **API**: Backward-compatible endpoints maintain existing functionality
3. **Configuration**: Feature flags allow disabling new authentication methods
4. **MCP**: Can operate in read-only mode without authentication

---

**Document Version**: 2.0
**Last Updated**: August 30, 2025 (Phase 1.5 Complete)
**Next Review**: Before Phase 2.0 implementation

## 📍 **CURRENT IMPLEMENTATION STATUS**

### ✅ **COMPLETED: Phase 1.5 - Service Layer Implementation**

**Branch**: `openchallenges/auth-service-oauth`
**Database**: PostgreSQL started and ready for testing
**Compilation**: All components compile successfully ✅

#### **Completed Components:**

1. **🔐 JWT Service** (`JwtService.java`)

   - Token generation, validation, and refresh logic
   - Configurable expiration times (access: 1h, refresh: 7d)
   - Secure token signing with HS256

2. **🛡️ Authentication Service** (`AuthenticationService.java`)

   - Username/password authentication with JWT token generation
   - OAuth2 authorization URL generation (placeholder implementation)
   - JWT token validation and refresh workflows
   - Uses OpenAPI-generated DTOs with proper type handling

3. **⚙️ OAuth2 Configuration Service** (`OAuth2ConfigurationService.java`)

   - Provider management for Google and Synapse
   - Authorization URL generation
   - Configuration validation

4. **🗄️ Database Schema** (V1.1.0 migration)

   - Extended `app_user` table with email field
   - `external_account` table for OAuth2 provider linkage
   - `refresh_token` table with expiration/revocation support

5. **📊 Repository Layer**

   - Extended `UserRepository` with OAuth2 methods
   - `ExternalAccountRepository` with provider-specific queries
   - `RefreshTokenRepository` with token management methods

6. **📋 OpenAPI Integration**
   - Generated DTOs available with `Dto` suffix
   - API interfaces ready for delegate implementation
   - Proper type handling for enums, URIs, and UUIDs

#### **Configuration Ready:**

```properties
# JWT Configuration
app.security.jwt.secret=${JWT_SECRET:openchallenges-default-jwt-secret-key-change-in-production}
app.security.jwt.access-token-expiration-ms=${JWT_ACCESS_EXPIRATION:3600000}  # 1 hour
app.security.jwt.refresh-token-expiration-ms=${JWT_REFRESH_EXPIRATION:604800000}  # 7 days
app.security.jwt.issuer=${JWT_ISSUER:openchallenges-auth-service}

# OAuth2 Configuration
app.security.oauth2.google.client-id=${GOOGLE_CLIENT_ID:}
app.security.oauth2.google.client-secret=${GOOGLE_CLIENT_SECRET:}
app.security.oauth2.synapse.client-id=${SYNAPSE_CLIENT_ID:}
app.security.oauth2.synapse.client-secret=${SYNAPSE_CLIENT_SECRET:}
app.base-url=${BASE_URL:http://localhost:8085}
```

### 🚀 **NEXT: Phase 2.0 - API Layer Implementation**

**Priority**: Implement API controllers/delegates to connect OpenAPI interfaces with service layer

#### **Immediate Tasks:**

1. **AuthenticationApiDelegate Implementation**

   - Wire `login()` → `AuthenticationService.authenticateUser()`
   - Wire `initiateOAuth2()` → `AuthenticationService.authorizeOAuth2()`
   - Wire `completeOAuth2()` → `AuthenticationService.handleOAuth2Callback()`
   - Wire `validateJwt()` → `AuthenticationService.validateJwt()`
   - Wire `refreshJwt()` → `AuthenticationService.refreshToken()`

2. **OAuth2 Provider Integration**

   - Complete `AuthenticationService.handleOAuth2Callback()` with real token exchange
   - Implement user account creation/linking from OAuth2 providers
   - Add external account management

3. **Spring Security Configuration**

   - Configure JWT authentication filters
   - Add password encoder bean
   - Set up CORS and security policies

4. **Integration Testing**
   - Test username/password authentication with database
   - Test OAuth2 flows (when provider credentials available)
   - Validate JWT token workflows

#### **Technical Notes for Next Session:**

- **DTO Usage**: All DTOs have `Dto` suffix (e.g., `LoginResponseDto`)
- **Enum Handling**: Use `.getValue()` for enum values: `request.getProvider().getValue()`
- **Type Casting**: Cast `long` to `int` for expiration times: `(int) jwtService.getAccessTokenExpirationSeconds()`
- **URI Handling**: Use `new java.net.URI(string)` for authorization URLs
- **Repository Methods**: Added OAuth2-specific methods to existing repositories

#### **Key Files Created/Modified:**

```
✅ Created:
- JwtService.java - JWT token operations
- OAuth2ConfigurationService.java - Provider management
- AuthenticationService.java - Core auth business logic
- ExternalAccountRepository.java - OAuth2 data access
- RefreshTokenRepository.java - Token management data access

✅ Modified:
- User.java - Added email field
- UserRepository.java - Added findByUsernameIgnoreCase()
- build.gradle.kts - Added JWT/OAuth2 dependencies
- V1.1.0__add_oauth2_support.sql - Database schema
```

### 🎯 **Resume Point:**

Create `AuthenticationApiDelegate.java` to implement OpenAPI delegate pattern and connect service layer to API endpoints.

---
