package org.sagebionetworks.openchallenges.auth.service.api;

import org.sagebionetworks.openchallenges.auth.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LogoutRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LogoutResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2AuthorizeRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2AuthorizeResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2CallbackRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2RevokeResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.RefreshTokenRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.RefreshTokenResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateJwtRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateJwtResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

/**
 * A delegate to be called by the {@link AuthenticationApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface AuthenticationApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /v1/auth/oauth2/callback : Complete OAuth2 authentication
     * Handle OAuth2 callback and exchange code for JWT tokens
     *
     * @param oauth2CallbackRequestDto  (required)
     * @return OAuth2 authentication successful (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see AuthenticationApi#completeOAuth2
     */
    default ResponseEntity<LoginResponseDto> completeOAuth2(OAuth2CallbackRequestDto oauth2CallbackRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"expiresIn\" : 3600, \"role\" : \"admin\", \"accessToken\" : \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"tokenType\" : \"Bearer\", \"userId\" : \"123e4567-e89b-12d3-a456-426614174000\", \"refreshToken\" : \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"username\" : \"admin\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /v1/auth/oauth2/authorize : Initiate OAuth2 authentication
     * Start OAuth2 flow with external provider (Google, Synapse)
     *
     * @param oauth2AuthorizeRequestDto  (required)
     * @return OAuth2 authorization URL generated (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see AuthenticationApi#initiateOAuth2
     */
    default ResponseEntity<OAuth2AuthorizeResponseDto> initiateOAuth2(OAuth2AuthorizeRequestDto oauth2AuthorizeRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"authorizationUrl\" : \"https://accounts.google.com/oauth2/authorize?client_id=...\", \"state\" : \"random_state_string\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /v1/auth/login : User login
     * Authenticate user and return JWT token
     *
     * @param loginRequestDto  (required)
     * @return Login successful (status code 200)
     *         or Unauthorized (status code 401)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see AuthenticationApi#login
     */
    default ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"expiresIn\" : 3600, \"role\" : \"admin\", \"accessToken\" : \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"tokenType\" : \"Bearer\", \"userId\" : \"123e4567-e89b-12d3-a456-426614174000\", \"refreshToken\" : \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"username\" : \"admin\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /v1/auth/logout : User logout
     * Logout user and revoke refresh tokens for security
     *
     * @param logoutRequestDto  (required)
     * @return Logout successful (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see AuthenticationApi#logout
     */
    default ResponseEntity<LogoutResponseDto> logout(LogoutRequestDto logoutRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"Successfully logged out\", \"revokedTokens\" : 1 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /v1/auth/jwt/refresh : Refresh JWT access token
     * Exchange refresh token for new access token
     *
     * @param refreshTokenRequestDto  (required)
     * @return New access token generated (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see AuthenticationApi#refreshJwt
     */
    default ResponseEntity<RefreshTokenResponseDto> refreshJwt(RefreshTokenRequestDto refreshTokenRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"expiresIn\" : 3600, \"accessToken\" : \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"tokenType\" : \"Bearer\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /v1/auth/oauth2/revoke : Revoke OAuth2 token
     * Revoke access or refresh tokens according to RFC 7009
     *
     * @param token The token to revoke (access token or refresh token) (required)
     * @param tokenTypeHint Hint about the type of token being revoked (optional)
     * @return Token revocation successful (or token was already invalid) (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see AuthenticationApi#revokeOAuth2Token
     */
    default ResponseEntity<OAuth2RevokeResponseDto> revokeOAuth2Token(String token,
        String tokenTypeHint) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"Token successfully revoked\", \"revokedTokens\" : 1 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /v1/auth/validate : Validate API key
     * Internal endpoint to validate API keys (used by other services)
     *
     * @param validateApiKeyRequestDto  (required)
     * @return API key is valid (status code 200)
     *         or Unauthorized (status code 401)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see AuthenticationApi#validateApiKey
     */
    default ResponseEntity<ValidateApiKeyResponseDto> validateApiKey(ValidateApiKeyRequestDto validateApiKeyRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"valid\" : true, \"role\" : \"admin\", \"scopes\" : [ \"organizations:read\", \"organizations:write\" ], \"userId\" : \"123e4567-e89b-12d3-a456-426614174000\", \"username\" : \"admin\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /v1/auth/jwt/validate : Validate JWT token
     * Internal endpoint to validate JWT tokens (used by other services)
     *
     * @param validateJwtRequestDto  (required)
     * @return JWT validation result (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see AuthenticationApi#validateJwt
     */
    default ResponseEntity<ValidateJwtResponseDto> validateJwt(ValidateJwtRequestDto validateJwtRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"valid\" : true, \"role\" : \"admin\", \"userId\" : \"123e4567-e89b-12d3-a456-426614174000\", \"expiresAt\" : \"2025-08-30T15:30:00Z\", \"username\" : \"admin\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
