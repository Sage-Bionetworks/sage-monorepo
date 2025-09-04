# Organization Service API Gateway Integration

## Overview

Updated the OpenChallenges Organization Service OpenAPI specifications to support authentication via the API Gateway using trusted headers. This enables the organization service to work in an API Gateway deployment where authentication is centralized at the gateway level.

## Changes Made

### 1. New Security Scheme Component

**File:** `/libs/openchallenges/api-description/src/components/securitySchemes/TrustedHeaderAuth.yaml`

Created a new security scheme that describes authentication via trusted headers set by the API Gateway:

```yaml
type: apiKey
in: header
name: X-Authenticated-User-Id
description: >
  Authentication via trusted headers set by the API Gateway.
  The API Gateway validates JWT tokens or API keys and forwards authenticated
  user information to downstream services via trusted headers:
  - X-Authenticated-User-Id: The authenticated user's ID
  - X-Authenticated-User: The authenticated user's username  
  - X-Authenticated-Roles: The authenticated user's roles
  - X-Scopes: OAuth2 scopes (if applicable)
```

### 2. Updated Organization Service Security Schemes

**File:** `/libs/openchallenges/api-description/src/organization.openapi.yaml`

Uncommented and updated the security schemes section to include the new trusted header auth:

```yaml
components:
  securitySchemes:
    jwtBearerAuth:
      $ref: components/securitySchemes/JwtBearerAuth.yaml
    apiKeyAuth:
      $ref: components/securitySchemes/ApiKeyAuth.yaml
    trustedHeaderAuth:
      $ref: components/securitySchemes/TrustedHeaderAuth.yaml
```

### 3. Updated All Authenticated Endpoints

Added `trustedHeaderAuth` as a security option to all organization service operations that require authentication:

**Files Updated:**

- `/paths/organizations.yaml` - POST operation (create organization)
- `/paths/organizations/{org}.yaml` - PUT and DELETE operations
- `/paths/organizations/{org}/participations.yaml` - POST operation
- `/paths/organizations/{org}/participations/{challengeId}/roles/{role}.yaml` - DELETE operation

All secured endpoints now support three authentication methods:

```yaml
security:
  - jwtBearerAuth: []
  - apiKeyAuth: []
  - trustedHeaderAuth: []
```

## API Gateway Authentication Flow

### Current Direct Authentication (Legacy)

1. Client sends JWT/API key directly to organization service
2. Organization service validates token via AuthServiceClient
3. Service creates authentication context from validation response

### New Trusted Header Authentication (API Gateway)

1. Client sends JWT/API key to API Gateway
2. API Gateway validates token via auth service
3. API Gateway sets trusted headers:
   - `X-Authenticated-User-Id`: User's unique identifier
   - `X-Authenticated-User`: Username
   - `X-Authenticated-Roles`: User's roles
   - `X-Scopes`: OAuth2 scopes (if applicable)
4. Organization service trusts these headers for authentication

## Implementation Requirements

To fully support API Gateway integration, the organization service code needs these updates:

### 1. Create Trusted Header Authentication Filter

Create a new filter similar to `ApiKeyAuthenticationFilter` but reads trusted headers:

```java
@Component
public class TrustedHeaderAuthenticationFilter implements Filter {

  private static final String X_AUTHENTICATED_USER_ID_HEADER = "X-Authenticated-User-Id";
  private static final String X_AUTHENTICATED_USER_HEADER = "X-Authenticated-User";
  private static final String X_AUTHENTICATED_ROLES_HEADER = "X-Authenticated-Roles";
  private static final String X_SCOPES_HEADER = "X-Scopes";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    HttpServletRequest httpRequest = (HttpServletRequest) request;

    String userId = httpRequest.getHeader(X_AUTHENTICATED_USER_ID_HEADER);
    String username = httpRequest.getHeader(X_AUTHENTICATED_USER_HEADER);
    String roles = httpRequest.getHeader(X_AUTHENTICATED_ROLES_HEADER);

    if (userId != null && username != null) {
      // Create authentication from trusted headers
      var authorities = parseRoles(roles);
      var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    chain.doFilter(request, response);
  }
}

```

### 2. Update Security Configuration

Modify the organization service's security configuration to use the trusted header filter when deployed behind API Gateway:

```java
@Configuration
@EnableWebSecurity
public class OrganizationServiceSecurityConfig {

  @Value("${openchallenges.security.mode:direct}")
  private String securityMode;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    if ("trusted-headers".equals(securityMode)) {
      // API Gateway mode - trust headers
      http.addFilterBefore(
        trustedHeaderAuthenticationFilter,
        UsernamePasswordAuthenticationFilter.class
      );
    } else {
      // Direct mode - validate tokens directly
      http.addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    return http.build();
  }
}

```

### 3. Configuration Property

Add configuration to control authentication mode:

```yaml
# application.yml
openchallenges:
  security:
    mode: trusted-headers # or "direct" for legacy mode
```

## Benefits

1. **Centralized Authentication**: All authentication logic in API Gateway
2. **Simplified Services**: Organization service no longer needs to call auth service
3. **Better Performance**: Eliminates auth service calls from organization service
4. **Consistent Security**: All services use same authentication pattern
5. **Backward Compatibility**: Still supports direct authentication for development

## Security Considerations

1. **Network Security**: Trusted headers approach requires secure network between gateway and services
2. **Header Validation**: Services should validate that requests come through the API Gateway
3. **Configuration Management**: Clear separation between gateway and direct authentication modes
4. **Testing**: Both authentication modes should be testable independently

## Deployment Notes

- **Development**: Can use direct authentication mode for local development
- **Production**: Should use trusted headers mode with API Gateway
- **Staging**: Can test both modes to ensure compatibility
- **Migration**: Gradual rollout possible with configuration switches
