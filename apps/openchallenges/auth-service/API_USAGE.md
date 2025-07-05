# OpenChallenges Auth Service - API Key Management

This document describes how to use the API key management endpoints in the OpenChallenges Auth Service.

## Overview

The auth service provides secure API key management functionality with the following features:

- **Profile-based API key prefixes** (dev: `oc_dev_`, stage: `oc_stage_`, prod: `oc_prod_`)
- **Secure authentication** via API keys in Authorization header
- **Full CRUD operations** for API key management
- **Automatic expiration** and cleanup of API keys
- **User isolation** - users can only manage their own API keys

## Available Endpoints

### Authentication Endpoints

#### 1. User Login

```http
POST /v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "changeme"
}
```

**Response:**

```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "username": "admin",
  "role": "ADMIN",
  "apiKey": "oc_dev_abc123def456ghi789jkl012mno345pqr678stu"
}
```

#### 2. Validate API Key (Internal)

```http
POST /v1/auth/validate
Content-Type: application/json

{
  "apiKey": "oc_dev_abc123def456ghi789jkl012mno345pqr678stu"
}
```

**Response:**

```json
{
  "valid": true,
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "username": "admin",
  "role": "admin",
  "scopes": ["organizations:read", "organizations:write", "challenges:read", "challenges:write"]
}
```

### API Key Management Endpoints

All API key management endpoints require authentication via the `Authorization` header:

```
Authorization: Bearer oc_dev_your_api_key_here
```

#### 3. Create API Key

```http
POST /v1/auth/api-keys
Authorization: Bearer oc_dev_your_api_key_here
Content-Type: application/json

{
  "name": "My Project API Key",
  "expiresIn": 30
}
```

**Response:**

```json
{
  "id": "456e7890-e12b-34c5-d678-901234567890",
  "key": "oc_dev_xyz789abc012def345ghi678jkl901mno234pqr",
  "name": "My Project API Key",
  "prefix": "oc_dev_",
  "createdAt": "2025-07-03T10:30:00Z",
  "expiresAt": "2025-08-02T10:30:00Z"
}
```

#### 4. List API Keys

```http
GET /v1/auth/api-keys
Authorization: Bearer oc_dev_your_api_key_here
```

**Response:**

```json
[
  {
    "id": "456e7890-e12b-34c5-d678-901234567890",
    "name": "My Project API Key",
    "prefix": "oc_dev_",
    "createdAt": "2025-07-03T10:30:00Z",
    "expiresAt": "2025-08-02T10:30:00Z",
    "lastUsedAt": "2025-07-03T15:45:00Z"
  },
  {
    "id": "789e0123-e45f-67g8-h901-234567890123",
    "name": "Login Session",
    "prefix": "oc_dev_",
    "createdAt": "2025-07-03T09:15:00Z",
    "expiresAt": null,
    "lastUsedAt": "2025-07-03T15:50:00Z"
  }
]
```

#### 5. Delete API Key

```http
DELETE /v1/auth/api-keys/{keyId}
Authorization: Bearer oc_dev_your_api_key_here
```

**Response:** `204 No Content`

## Usage Examples

### Example 1: Basic Workflow with cURL

```bash
# 1. Login to get an API key
LOGIN_RESPONSE=$(curl -X POST http://localhost:8087/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "changeme"}')

API_KEY=$(echo $LOGIN_RESPONSE | jq -r '.apiKey')

# 2. Create a new API key for a project
curl -X POST http://localhost:8087/v1/auth/api-keys \
  -H "Authorization: Bearer $API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"name": "My Project Key", "expiresIn": 90}'

# 3. List all API keys
curl -X GET http://localhost:8087/v1/auth/api-keys \
  -H "Authorization: Bearer $API_KEY"

# 4. Delete a specific API key
curl -X DELETE http://localhost:8087/v1/auth/api-keys/{keyId} \
  -H "Authorization: Bearer $API_KEY"
```

### Example 2: JavaScript/Node.js

```javascript
const API_BASE = 'http://localhost:8087';

// Login and get API key
async function login(username, password) {
  const response = await fetch(`${API_BASE}/v1/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });
  return await response.json();
}

// Create API key
async function createApiKey(apiKey, name, expiresIn) {
  const response = await fetch(`${API_BASE}/v1/auth/api-keys`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${apiKey}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ name, expiresIn }),
  });
  return await response.json();
}

// List API keys
async function listApiKeys(apiKey) {
  const response = await fetch(`${API_BASE}/v1/auth/api-keys`, {
    headers: { Authorization: `Bearer ${apiKey}` },
  });
  return await response.json();
}

// Usage
async function example() {
  // Login
  const loginResponse = await login('admin', 'changeme');
  const apiKey = loginResponse.apiKey;

  // Create a new API key
  const newKey = await createApiKey(apiKey, 'My App Key', 30);
  console.log('Created API key:', newKey.key);

  // List all keys
  const keys = await listApiKeys(apiKey);
  console.log('All API keys:', keys);
}
```

### Example 3: Python

```python
import requests
import json

API_BASE = 'http://localhost:8087'

def login(username, password):
    response = requests.post(f'{API_BASE}/v1/auth/login',
                           json={'username': username, 'password': password})
    return response.json()

def create_api_key(api_key, name, expires_in=None):
    headers = {'Authorization': f'Bearer {api_key}'}
    data = {'name': name}
    if expires_in:
        data['expiresIn'] = expires_in

    response = requests.post(f'{API_BASE}/v1/auth/api-keys',
                           headers=headers, json=data)
    return response.json()

def list_api_keys(api_key):
    headers = {'Authorization': f'Bearer {api_key}'}
    response = requests.get(f'{API_BASE}/v1/auth/api-keys', headers=headers)
    return response.json()

def delete_api_key(api_key, key_id):
    headers = {'Authorization': f'Bearer {api_key}'}
    response = requests.delete(f'{API_BASE}/v1/auth/api-keys/{key_id}',
                             headers=headers)
    return response.status_code == 204

# Usage
if __name__ == '__main__':
    # Login
    login_response = login('admin', 'changeme')
    api_key = login_response['apiKey']

    # Create a new API key
    new_key = create_api_key(api_key, 'Python Script Key', 60)
    print(f"Created API key: {new_key['key']}")

    # List all keys
    keys = list_api_keys(api_key)
    print(f"Total API keys: {len(keys)}")

    # Use the new API key for subsequent requests
    project_api_key = new_key['key']
    # ... use project_api_key for your application
```

## Security Considerations

1. **API Key Storage**: Never store API keys in plain text. Use environment variables or secure key management systems.

2. **HTTPS**: Always use HTTPS in production to protect API keys in transit.

3. **Key Rotation**: Regularly rotate API keys, especially for long-running applications.

4. **Least Privilege**: Create API keys with minimal required permissions and appropriate expiration times.

5. **Monitoring**: Monitor API key usage and revoke any suspicious or unused keys.

## Error Handling

### Common Error Responses

#### 401 Unauthorized

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or missing API key"
}
```

#### 404 Not Found

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "API key not found or access denied"
}
```

#### 400 Bad Request

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request parameters"
}
```

## Profile-Specific Behavior

The auth service behavior changes based on the active Spring profile:

- **Development (`dev`)**:

  - API keys prefixed with `oc_dev_`
  - Database cleaning on startup
  - Verbose logging

- **Staging (`stage`)**:

  - API keys prefixed with `oc_stage_`
  - No database cleaning
  - Moderate logging

- **Production (`prod`)**:
  - API keys prefixed with `oc_prod_`
  - No database cleaning
  - Minimal logging

Set the profile using:

```bash
export SPRING_PROFILES_ACTIVE=prod
# or
java -jar auth-service.jar --spring.profiles.active=prod
```

## Swagger/OpenAPI Documentation

Interactive API documentation is available at:

- **Swagger UI**: http://localhost:8087/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8087/v3/api-docs
