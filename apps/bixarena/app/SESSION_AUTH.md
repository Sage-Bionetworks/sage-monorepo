# BixArena Session-Based Authentication

## Overview

The BixArena application now imp```javascript
// Secure cookie settings
document.cookie = "bixarena_session=SESSION_ID; path=/; max-age=86400; samesite=strict; secure";

````

- **Name**: `bixarena_session`
- **Max-Age**: 86,400 seconds (24 hours)
- **SameSite**: Strict (CSRF protection)
- **Secure**: Only for HTTPS (omitted in development)
- **Path**: `/` (application-wide)rsistent login state using a session-based authentication system with secure cookies. Users no longer need to re-authenticate when refreshing the page, addressing the previous limitation of token-only authentication.

## Key Features

### 1. Persistent Login State
- **Server-side sessions**: Session data is stored server-side with unique session IDs
- **Secure cookies**: Session IDs are stored in HttpOnly, SameSite=Strict cookies
- **Session-based expiry**: Sessions expire when cookies expire (24 hours)

### 2. Enhanced Security
- **Cookie-based expiry**: 24-hour session lifetime tied to cookie expiry
- **Session validation**: Automatic cleanup of expired sessions
- **Cookie security**: Secure flag for HTTPS, SameSite=Strict for CSRF protection

### 3. Scalable Session Storage

- **Memory store**: For development and small deployments
- **Redis support**: For production with high availability
- **Automatic cleanup**: Background thread removes expired sessions

## Implementation Details

### Architecture Components

1. **SessionManager** (`src/auth/session_manager.py`)

   - Manages user sessions with 24-hour expiry
   - Provides session validation and cleanup
   - Simple cookie-based session management

2. **SessionStore** (`src/auth/session_store.py`)

   - Abstract interface for session storage
   - MemorySessionStore for development
   - RedisSessionStore for production

3. **SynapseOAuthClient** (`src/auth/oauth_client.py`)

   - Simplified OAuth client for Synapse integration
   - Handles authorization code exchange for access tokens
   - No refresh token complexity

4. **AuthService** (`src/auth/auth_service.py`)
   - Updated for session-based authentication
   - Manages login/logout workflows
   - Handles cookie-based session loading

### Session Flow

#### Initial Login

1. User clicks login button
2. Redirected to Synapse OAuth with `offline_access` scope
3. OAuth callback processes authorization code
4. Access and refresh tokens obtained from Synapse
5. Server-side session created with unique session ID
6. Session ID stored in secure HttpOnly cookie
7. User profile loaded and authenticated

#### Subsequent Page Loads

1. Browser sends session cookie with request
2. Session ID extracted from cookie
3. Session data loaded from server-side store
4. Token expiry checked (refreshes if needed)
5. User authenticated without login prompt

#### Session Expiry

1. Sessions expire after 24 hours (tied to cookie expiry)
2. Users need to re-authenticate after session expires
3. No automatic token refresh - keeps implementation simple

#### Logout

1. User clicks logout button
2. Server-side session deleted
3. Cookie cleared via JavaScript
4. User redirected to home page

### Cookie Configuration

```javascript
// Secure cookie settings
document.cookie = 'bixarena_session=SESSION_ID; path=/; max-age=15552000; samesite=strict; secure';
````

- **Name**: `bixarena_session`
- **Max-Age**: 15,552,000 seconds (180 days)
- **SameSite**: Strict (CSRF protection)
- **Secure**: Only for HTTPS (omitted in development)
- **Path**: `/` (application-wide)

### Environment Configuration

#### Development

```bash
ENVIRONMENT=development
SYNAPSE_CLIENT_ID=your_client_id
SYNAPSE_CLIENT_SECRET=your_client_secret
APP_REDIRECT_URI=http://127.0.0.1:8100
```

#### Production

```bash
ENVIRONMENT=production
REDIS_URL=redis://your-redis-server:6379
SYNAPSE_CLIENT_ID=your_client_id
SYNAPSE_CLIENT_SECRET=your_client_secret
APP_REDIRECT_URI=https://your-domain.com
```

## Synapse OAuth Configuration

### Required Scopes

- `openid`: For user identity information
- `view`: For accessing user profile data

### Client Registration

The OAuth client must be registered with Synapse with the following configuration:

```python
client_meta_data = {
    'client_name': 'BixArena',
    'redirect_uris': ['https://your-domain.com'],
    'client_uri': 'https://your-domain.com',
    'userinfo_signed_response_alg': 'RS256'
}
```

### Token Endpoints

- **Authorization**: `https://signin.synapse.org`
- **Token Exchange**: `https://repo-prod.prod.sagebase.org/auth/v1/oauth2/token`
- **User Profile**: `https://repo-prod.prod.sagebase.org/repo/v1/userProfile`

## Security Considerations

### Implemented Protections

1. **Session Hijacking**: Sessions expire after 180 days of inactivity
2. **CSRF**: SameSite=Strict cookie prevents cross-site requests
3. **XSS**: HttpOnly cookies prevent JavaScript access (when supported)
4. **Token Exposure**: Access tokens stored server-side only
5. **Session Fixation**: New session ID generated on login

### Production Recommendations

1. **HTTPS Only**: Ensure all traffic uses HTTPS
2. **Redis Security**: Secure Redis instance with authentication
3. **Session Monitoring**: Log session creation/destruction
4. **Rate Limiting**: Implement OAuth callback rate limiting
5. **Security Headers**: Add HSTS, CSP, and other security headers

## Testing

### Manual Testing

1. Login with Synapse credentials
2. Refresh the page - should remain logged in
3. Close browser and reopen - should remain logged in
4. Wait for token expiry - should auto-refresh
5. Logout - should clear session and redirect

### Automated Testing

Run the test suite to verify components:

```bash
cd apps/bixarena/app
python test_auth.py
```

## Troubleshooting

### Common Issues

1. **Cookie Not Set**

   - Check secure flag for HTTPS vs HTTP
   - Verify SameSite compatibility
   - Check browser developer tools

2. **Session Not Loading**

   - Verify session store is accessible
   - Check session expiry settings
   - Confirm session ID format

3. **Token Refresh Fails**
   - Verify refresh token scope in OAuth
   - Check Synapse client configuration
   - Confirm token endpoint accessibility

### Debug Mode

Enable debug logging by setting:

```bash
export DEBUG=true
```

This will provide detailed session and authentication logs for troubleshooting.

## Migration Notes

### Breaking Changes

- Session management now requires server-side storage
- Cookie-based authentication replaces token-only approach
- OAuth scope includes `offline_access` for refresh tokens

### Backward Compatibility

- Existing users will need to re-authenticate once
- Development bypass mode still supported
- Environment variables remain the same

## Future Enhancements

1. **Session Analytics**: Track session usage patterns
2. **Multi-Device Support**: Allow multiple active sessions
3. **Session Management UI**: Allow users to view/revoke sessions
4. **Advanced Security**: Implement session fingerprinting
5. **Database Sessions**: Support PostgreSQL/MySQL for session storage

## Conclusion

The new session-based authentication system provides a seamless user experience while maintaining strong security practices. Users can now refresh pages, close browsers, and return to the application without needing to re-authenticate, significantly improving the usability of BixArena.
