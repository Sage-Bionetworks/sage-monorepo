# OpenChallenges Auth Service - Profile Configuration

## Overview

The auth service supports three different profiles for different environments:

- **dev**: Development environment
- **stage**: Staging environment
- **prod**: Production environment

Each profile has its own API key prefix and configuration settings.

## Profile Configuration

### Development Profile (`dev`)

- **API Key Prefix**: `oc_dev_`
- **Database**: Default PostgreSQL connection
- **JPA**: Show SQL queries enabled
- **Logging**: DEBUG level for OpenChallenges packages

### Staging Profile (`stage`)

- **API Key Prefix**: `oc_stage_`
- **Database**: PostgreSQL connection to staging database
- **JPA**: Show SQL queries disabled
- **Logging**: INFO level for OpenChallenges packages

### Production Profile (`prod`)

- **API Key Prefix**: `oc_prod_`
- **Database**: PostgreSQL connection to production database
- **JPA**: Show SQL queries disabled
- **Logging**: WARN level for OpenChallenges packages

## How to Use

### 1. Setting the Active Profile

#### Method 1: In application.yml (default)

```yaml
spring:
  profiles:
    active: dev # Change to dev, stage, or prod
```

#### Method 2: Environment Variable

```bash
export SPRING_PROFILES_ACTIVE=prod
```

#### Method 3: Command Line Argument

```bash
java -jar auth-service.jar --spring.profiles.active=prod
```

#### Method 4: Gradle Run

```bash
./gradlew bootRun --args="--spring.profiles.active=stage"
```

### 2. API Key Generation

When you create API keys using the auth service, they will automatically use the prefix based on the active profile:

- **Dev environment**: Generated keys start with `oc_dev_`
- **Stage environment**: Generated keys start with `oc_stage_`
- **Production environment**: Generated keys start with `oc_prod_`

### 3. Configuration Files

Each profile has its own configuration file:

- `application.yml` - Common configuration and default profile
- `application-dev.yml` - Development-specific configuration
- `application-stage.yml` - Staging-specific configuration
- `application-prod.yml` - Production-specific configuration

### 4. Database Configuration

Each profile can have different database configurations:

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://openchallenges-postgres-prod:8091/auth_service
    username: auth_service
    password: ${DB_PASSWORD}
```

## Example Usage

### Running in Development Mode

```bash
cd /workspaces/sage-monorepo/apps/openchallenges/auth-service
./gradlew bootRun --args="--spring.profiles.active=dev"
```

### Running in Production Mode

```bash
cd /workspaces/sage-monorepo/apps/openchallenges/auth-service
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun
```

### Testing Different Profiles

```bash
# Test with dev profile
./gradlew test -Dspring.profiles.active=dev

# Test with stage profile
./gradlew test -Dspring.profiles.active=stage

# Test with prod profile
./gradlew test -Dspring.profiles.active=prod
```

## Configuration Properties

The API key configuration is centralized in the `openchallenges.auth.api-key` properties:

```yaml
openchallenges:
  auth:
    api-key:
      prefix: oc_dev_ # Changes based on active profile
      length: 40 # Common across all profiles
```

## Security Notes

1. **Environment Variables**: Use environment variables for sensitive configuration like database passwords
2. **Profile-Specific Secrets**: Store different secrets for each environment
3. **API Key Prefixes**: The prefix helps identify which environment an API key belongs to
4. **Database Isolation**: Each profile should use a separate database instance

## Testing

The auth service includes tests that verify the profile-specific configuration:

```bash
# Run all tests
./gradlew test

# Test specific profile configuration
./gradlew test --tests "*ApiKeyPropertiesTest*"
```
