# OAuth/OIDC Authentication Implementation Plan - Updated Context

## 🎯 **Project Overview**

**User Request**: "I would like to extend the authentication service to enable users to login with a Google account, and later on with a Synapse account"

**Chosen Architecture**: Option 1: JWT-Centric with API Key Generation

- Primary authentication via JWT tokens
- OAuth2 providers (Google, Synapse) link to existing users
- API keys remain for service-to-service authentication

## 📍 **Current Status: Phase 1.5 COMPLETE**

### ✅ **Completed Phases:**

#### **Phase 1.0: OpenAPI Specifications** ✅

- **Location**: `libs/openchallenges/api-description/src/auth-service.yaml`
- **JWT Endpoints**: `/auth/login`, `/auth/jwt/validate`, `/auth/jwt/refresh`
- **OAuth2 Endpoints**: `/auth/oauth2/{provider}/authorize`, `/auth/oauth2/{provider}/callback`
- **Generation**: `nx run openchallenges-auth-service:generate`
- **Generated DTOs**: Available in `src/main/java/.../model/dto/` with `Dto` suffix

#### **Phase 1.1: Database Migration** ✅

- **File**: `apps/openchallenges/auth-service/src/main/resources/db/migration/V1.1.0__add_oauth2_support.sql`
- **Tables**: `external_account`, `refresh_token`
- **Schema**: OAuth2 provider linking, secure token storage with expiration/revocation

#### **Phase 1.2: Dependencies** ✅

- **File**: `gradle/libs.versions.toml`
- **Added**: `jjwt-api`, `jjwt-impl`, `jjwt-jackson`, Spring OAuth2 libraries
- **Build**: `apps/openchallenges/auth-service/build.gradle.kts` updated

#### **Phase 1.3: Entity Models** ✅

- **User.java**: Extended with `email` field (unique, nullable)
- **ExternalAccount.java**: OAuth2 provider linkage with token management
- **RefreshToken.java**: Secure token storage with expiration/revocation logic
- **Location**: `apps/openchallenges/auth-service/src/main/java/.../model/entity/`

#### **Phase 1.4: Repository Interfaces** ✅

- **UserRepository.java**: Added `findByUsernameIgnoreCase()` method
- **ExternalAccountRepository.java**: OAuth2-specific queries
- **RefreshTokenRepository.java**: Token management with `findByTokenHashAndUserId()`, `deleteByTokenHashAndUserId()`
- **Location**: `apps/openchallenges/auth-service/src/main/java/.../repository/`

#### **Phase 1.5: Service Layer** ✅

- **JwtService.java**: Token generation, validation, refresh logic with configurable expiration
- **OAuth2ConfigurationService.java**: Provider management (Google, Synapse) with authorization URL generation
- **AuthenticationService.java**: Core authentication flows using OpenAPI DTOs
- **Location**: `apps/openchallenges/auth-service/src/main/java/.../service/`

### 🔧 **Technical Configuration:**

#### **JWT Configuration** (JwtService.java)

```properties
app.security.jwt.secret=${JWT_SECRET:openchallenges-default-jwt-secret-key-change-in-production}
app.security.jwt.access-token-expiration-ms=${JWT_ACCESS_EXPIRATION:3600000}  # 1 hour
app.security.jwt.refresh-token-expiration-ms=${JWT_REFRESH_EXPIRATION:604800000}  # 7 days
app.security.jwt.issuer=${JWT_ISSUER:openchallenges-auth-service}
```

#### **OAuth2 Configuration** (OAuth2ConfigurationService.java)

```properties
app.security.oauth2.google.client-id=${GOOGLE_CLIENT_ID:}
app.security.oauth2.google.client-secret=${GOOGLE_CLIENT_SECRET:}
app.security.oauth2.synapse.client-id=${SYNAPSE_CLIENT_ID:}
app.security.oauth2.synapse.client-secret=${SYNAPSE_CLIENT_SECRET:}
app.base-url=${BASE_URL:http://localhost:8085}
```

#### **Provider Endpoints**

- **Google**: `https://accounts.google.com/o/oauth2/v2/auth`, `https://oauth2.googleapis.com/token`
- **Synapse**: `https://signin.synapse.org/oauth2/authorize`, `https://signin.synapse.org/oauth2/token`

### 🗄️ **Database Schema:**

```sql
-- User table extended
ALTER TABLE app_user ADD COLUMN email VARCHAR(255) UNIQUE;

-- External account linking
CREATE TABLE external_account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    provider VARCHAR(50) NOT NULL,
    external_id VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    access_token_hash VARCHAR(255),
    refresh_token_hash VARCHAR(255),
    expires_at TIMESTAMPTZ,
    raw_attributes JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, provider),
    UNIQUE(provider, external_id)
);

-- Refresh token management
CREATE TABLE refresh_token (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);
```

## 🚀 **Next Phases (Ready to Implement):**

### **Phase 2.0: API Controllers/Delegates** (PRIORITY)

**Goal**: Wire OpenAPI interfaces to service layer

- **Files to Create**:
  - `AuthenticationApiDelegate.java` - Implement delegate pattern
  - Wire `AuthenticationService` methods to OpenAPI endpoints
- **Methods to Implement**:
  - `login()` → `authenticateUser()`
  - `initiateOAuth2()` → `authorizeOAuth2()`
  - `completeOAuth2()` → `handleOAuth2Callback()`
  - `validateJwt()` → `validateJwt()`
  - `refreshJwt()` → `refreshToken()`

### **Phase 2.1: OAuth2 Provider Integration**

**Goal**: Complete OAuth2 flows with real provider APIs

- **AuthenticationService.handleOAuth2Callback()**: Currently placeholder
- **OAuth2 Token Exchange**: Code → Access Token → User Info
- **User Account Linking**: Create/link users from OAuth2 providers
- **External Account Management**: Store and manage provider tokens

### **Phase 2.2: Security Configuration**

**Goal**: Spring Security integration

- **JWT Authentication Filter**: Validate JWT tokens in requests
- **Security Configuration**: Configure endpoints, CORS, etc.
- **Password Encoder**: Configure BCrypt for password hashing

### **Phase 2.3: Integration Testing**

**Goal**: Test with real database and OAuth2 providers

- **Database**: PostgreSQL (STARTED by user)
- **Test Users**: Create test accounts
- **OAuth2 Flows**: Test Google/Synapse authentication
- **JWT Flows**: Test token generation/validation/refresh

## 🔍 **Key Implementation Notes:**

### **Generated DTOs Usage:**

- All DTOs have `Dto` suffix: `LoginResponseDto`, `OAuth2AuthorizeRequestDto`, etc.
- Use `.getValue()` for enum values: `request.getProvider().getValue()`
- Use proper types: `URI` for URLs, `UUID` for user IDs, `Integer` for expiration

### **Authentication Flow:**

1. **Username/Password**: `POST /auth/login` → JWT tokens
2. **OAuth2 Initiate**: `POST /auth/oauth2/{provider}/authorize` → authorization URL
3. **OAuth2 Callback**: `GET /auth/oauth2/{provider}/callback` → JWT tokens (after user linking)
4. **JWT Validation**: `POST /auth/jwt/validate` → user info
5. **Token Refresh**: `POST /auth/jwt/refresh` → new JWT tokens

### **Database Commands:**

- **Start**: Database already started by user
- **Migration**: Flyway will auto-run migrations on startup
- **Testing**: Can create test users and test authentication flows

## 🎯 **Immediate Next Steps (Phase 2.0):**

1. **Create AuthenticationApiDelegate.java**
2. **Wire service methods to API endpoints**
3. **Test basic JWT authentication flow**
4. **Add OAuth2 provider integration**
5. **Configure Spring Security**

## 📝 **Important Files Modified:**

- `User.java` - Added email field
- `UserRepository.java` - Added findByUsernameIgnoreCase()
- `RefreshTokenRepository.java` - Added OAuth2 token methods
- Created: `JwtService.java`, `OAuth2ConfigurationService.java`, `AuthenticationService.java`
- Created: `ExternalAccountRepository.java`, `RefreshTokenRepository.java`

**Branch**: `openchallenges/auth-service-oauth`
**Last Commit**: JWT and OAuth2 service layer implementation
**Compilation**: All components compile successfully ✅
**Database**: PostgreSQL started and ready for testing ✅

---

**Resume Point**: Implement Phase 2.0 (API Controllers) to connect OpenAPI interfaces with service layer.
