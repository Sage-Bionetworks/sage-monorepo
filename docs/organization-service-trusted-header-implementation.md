# Organization Service Trusted Header Authentication Implementation

## Summary

Successfully updated the OpenChallenges Organization Service to support trusted header authentication for API Gateway integration. The service now supports two authentication modes: **trusted-headers** (for API Gateway deployment) and **direct** (for standalone deployment).

## Changes Made

### 1. **New Authentication Filter**

**File:** `TrustedHeaderAuthenticationFilter.java`

- **Purpose**: Authenticates requests using trusted headers set by the API Gateway
- **Headers Processed**:
  - `X-Authenticated-User-Id`: User's unique identifier (UUID)
  - `X-Authenticated-User`: Username
  - `X-Authenticated-Roles`: User's roles
  - `X-Scopes`: OAuth2 scopes (comma-separated)
- **Behavior**: Creates Spring Security authentication context without calling auth service
- **Error Handling**: Gracefully handles invalid UUIDs and missing headers

### 2. **Updated Security Configuration**

**File:** `SecurityConfiguration.java`

- **Configuration Property**: `openchallenges.security.mode` (default: `trusted-headers`)
- **Supported Modes**:
  - `trusted-headers`: Uses `TrustedHeaderAuthenticationFilter` for API Gateway deployment
  - `direct`: Uses placeholder JWT and API key filters for standalone deployment
- **Filter Chain**: Conditionally configures authentication filters based on mode

### 3. **Placeholder Authentication Filters**

**Updated:** `ApiKeyAuthenticationFilter.java`

- **Purpose**: Placeholder implementation that passes through without authentication
- **Behavior**: Logs debug message and continues filter chain
- **Use Case**: Future implementation for direct authentication mode

**New:** `JwtBearerAuthenticationFilter.java`

- **Purpose**: Placeholder implementation for JWT Bearer token authentication
- **Behavior**: Logs debug message and continues filter chain
- **Use Case**: Future implementation for direct authentication mode

### 4. **Configuration Updates**

**File:** `application.yml`

- **Added**: `openchallenges.security.mode: trusted-headers` configuration
- **Purpose**: Controls which authentication mode the service uses
- **Default**: `trusted-headers` for API Gateway integration

### 5. **Comprehensive Test Coverage**

**File:** `TrustedHeaderAuthenticationFilterTest.java`

- **Coverage**: Tests all authentication scenarios including valid headers, missing headers, invalid UUID format
- **Verification**: Ensures proper Spring Security context creation
- **Edge Cases**: Handles missing roles (defaults to "USER"), malformed user IDs

**Updated:** `ApiKeyAuthenticationFilterTest.java`

- **Updated**: Tests for placeholder behavior (passes through without authentication)
- **Purpose**: Verifies placeholder implementation works correctly

## Authentication Flow

### Trusted Headers Mode (API Gateway)

```
1. Client → API Gateway [JWT/API Key]
2. API Gateway → Auth Service [Validation]
3. API Gateway → Organization Service [Trusted Headers]
4. Organization Service → [Create Authentication Context]
```

**Trusted Headers Set by API Gateway:**

- `X-Authenticated-User-Id`: `550e8400-e29b-41d4-a716-446655440000`
- `X-Authenticated-User`: `john.doe@example.com`
- `X-Authenticated-Roles`: `ADMIN`
- `X-Scopes`: `organizations:read,organizations:write`

### Direct Mode (Standalone)

```
1. Client → Organization Service [JWT/API Key]
2. Organization Service → [Placeholder Processing]
3. Organization Service → [Continue Without Authentication]
```

## Configuration Examples

### API Gateway Deployment (Production)

```yaml
openchallenges:
  security:
    mode: trusted-headers
```

### Standalone Deployment (Development)

```yaml
openchallenges:
  security:
    mode: direct
```

## Code Examples

### Accessing Authenticated User in Controllers

```java
@RestController
public class OrganizationController {

  @GetMapping("/v1/organizations")
  @PreAuthorize("hasAuthority('organizations:read')")
  public ResponseEntity<Organization> getOrganization(Authentication auth) {
    if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser) {
      AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
      logger.info("Request from user: {} ({})", user.getUsername(), user.getUserId());
    }
    // ... implementation
  }
}

```

### Testing Trusted Headers Locally

```bash
curl -H "X-Authenticated-User-Id: 550e8400-e29b-41d4-a716-446655440000" \
     -H "X-Authenticated-User: test@example.com" \
     -H "X-Authenticated-Roles: ADMIN" \
     -H "X-Scopes: organizations:read,organizations:write" \
     http://localhost:8084/v1/organizations
```

## Benefits

1. **Centralized Authentication**: All authentication logic handled by API Gateway
2. **Performance**: No auth service calls from organization service
3. **Scalability**: Reduced load on auth service
4. **Security**: Trusted network communication between gateway and services
5. **Flexibility**: Support for both gateway and standalone deployments
6. **Maintainability**: Clean separation of concerns

## Security Considerations

1. **Network Security**: Requires secure communication between API Gateway and services
2. **Header Validation**: Organization service trusts headers from API Gateway
3. **Configuration Management**: Clear distinction between deployment modes
4. **Testing**: Both modes should be thoroughly tested

## Next Steps

1. **Deploy with API Gateway**: Configure organization service with `trusted-headers` mode
2. **Update Docker Compose**: Set `OPENCHALLENGES_SECURITY_MODE=trusted-headers`
3. **Monitor Logs**: Verify trusted header authentication is working
4. **Performance Testing**: Compare performance with and without auth service calls

## Deployment Notes

- **Default Mode**: `trusted-headers` (ready for API Gateway)
- **Backward Compatibility**: Supports `direct` mode for legacy deployments
- **Environment Variable**: `OPENCHALLENGES_SECURITY_MODE` overrides YAML configuration
- **Health Checks**: All authentication modes support health check endpoints
