# JWT Authentication in BixArena Gradio App

This document explains how to use JWT-based authentication when making API calls from the Gradio app to the BixArena API.

## Architecture Overview

The BixArena app uses a **hybrid authentication approach**:

1. **Session cookies** (JSESSIONID) maintain user authentication state across page reloads
2. **JWTs** (JSON Web Tokens) are minted on-demand from the session for API calls
3. **Bearer tokens** are passed in the `Authorization` header to the BixArena API

### Why This Approach?

- ✅ **Session cookies survive page reloads** - User stays logged in
- ✅ **JWTs are stateless** - BixArena API can validate without session access
- ✅ **Fresh tokens** - Each API call gets a new JWT with current permissions
- ✅ **Standard OAuth2** - Uses Bearer token pattern

## Usage

### 1. Minting JWTs from Gradio Requests

Use the `get_jwt_from_request()` helper to mint a JWT from a Gradio request:

```python
import gradio as gr
from bixarena_app.auth.jwt_helper import get_jwt_from_request

def my_gradio_function(request: gr.Request):
    # Mint a fresh JWT from the session cookie in the request
    jwt_token = get_jwt_from_request(request)

    if not jwt_token:
        return "User not authenticated"

    # Use the JWT for API calls (see below)
    # ...
```

### 2. Making Authenticated API Calls

Use the `create_authenticated_api_client()` helper to create an API client with JWT:

```python
from bixarena_api_client import LeaderboardApi
from bixarena_app.api.api_client_helper import create_authenticated_api_client
from bixarena_app.auth.jwt_helper import get_jwt_from_request
import gradio as gr

def fetch_protected_data(request: gr.Request):
    # Get JWT from the request
    jwt_token = get_jwt_from_request(request)

    # Create authenticated API client
    with create_authenticated_api_client(jwt_token) as api_client:
        api = LeaderboardApi(api_client)
        data = api.get_leaderboard("open-source")

    return data
```

### 3. Optional JWT for Public Endpoints

For endpoints that are optionally authenticated (public but can be enhanced with auth):

```python
def fetch_data(request: gr.Request = None):
    # Try to get JWT, but continue if not available
    jwt_token = get_jwt_from_request(request) if request else None

    # API client works with or without JWT
    with create_authenticated_api_client(jwt_token) as api_client:
        api = LeaderboardApi(api_client)
        data = api.get_leaderboard("open-source")

    return data
```

## Flow Diagram

```
User loads page
    ↓
Browser sends JSESSIONID cookie to Gradio
    ↓
Gradio validates session (user is authenticated)
    ↓
User triggers action requiring API call
    ↓
Gradio calls get_jwt_from_request()
    ↓
JWT minting: POST /token (with JSESSIONID) → Returns JWT
    ↓
Gradio calls BixArena API with Authorization: Bearer <JWT>
    ↓
BixArena API validates JWT using JWKS from auth service
    ↓
API returns data to Gradio
    ↓
Gradio renders data to user
```

## Security Notes

- **JWTs are short-lived** (configured in auth service, typically 5-15 minutes)
- **JWTs are minted fresh** for each API call
- **Sessions persist** across page reloads via JSESSIONID cookie
- **API validates** JWTs independently without needing session access

## Environment Variables

Ensure these are set in your `.env` file:

```bash
# Auth service URL for server-side JWT minting
AUTH_BASE_URL_SSR=http://bixarena-auth-service:8111/v1

# BixArena API base URL
API_BASE_URL=http://bixarena-api:8112/v1
```

## Example: Complete Gradio Function

```python
import gradio as gr
from bixarena_api_client import ModelApi, ModelSearchQuery
from bixarena_app.api.api_client_helper import create_authenticated_api_client
from bixarena_app.auth.jwt_helper import get_jwt_from_request

def search_models(query_text: str, request: gr.Request):
    """Search for models with authentication."""

    # Get JWT from request
    jwt_token = get_jwt_from_request(request)
    if not jwt_token:
        return "Please log in to search models"

    try:
        # Create authenticated API client
        with create_authenticated_api_client(jwt_token) as api_client:
            api = ModelApi(api_client)

            # Make authenticated API call
            results = api.search_models(
                model_search_query=ModelSearchQuery(query=query_text)
            )

        return f"Found {len(results.models)} models"

    except Exception as e:
        print(f"Error searching models: {e}")
        return "Error searching models"
```

## Troubleshooting

### "No JSESSIONID cookie found"

- User is not logged in
- Session expired
- User needs to log in via /auth/oidc/start

### "Failed to mint JWT"

- Session exists but is invalid
- Auth service is down
- Network connectivity issue

### "401 Unauthorized from API"

- JWT expired (unlikely with fresh minting)
- JWT signature invalid
- User doesn't have required role
- API endpoint requires specific permissions
