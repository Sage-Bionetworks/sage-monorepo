package org.sagebionetworks.openchallenges.auth.service.api;

import org.sagebionetworks.openchallenges.auth.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.GetCurrentUser200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2ErrorDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2TokenResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2AuthorizationServerMetadata200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2Introspect200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2JwksJson200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2JwksJson404ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2RevokeToken200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2WellKnownOpenidConfiguration200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2WellKnownOpenidConfiguration404ResponseDto;
import java.net.URI;
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
 * A delegate to be called by the {@link OAuth2ApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface OAuth2ApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /.well-known/oauth-authorization-server : OAuth2 Authorization Server Discovery
     * RFC 8414 OAuth2 Authorization Server Metadata endpoint. Returns the OAuth2 authorization server discovery document. 
     *
     * @return OAuth2 Authorization Server Metadata (status code 200)
     *         or Discovery document not available (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see OAuth2Api#oauth2AuthorizationServerMetadata
     */
    default ResponseEntity<Oauth2AuthorizationServerMetadata200ResponseDto> oauth2AuthorizationServerMetadata() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"response_types_supported\" : [ \"code\", \"token\" ], \"code_challenge_methods_supported\" : [ \"S256\", \"plain\" ], \"introspection_endpoint\" : \"https://api.openchallenges.io/oauth2/introspect\", \"grant_types_supported\" : [ \"authorization_code\", \"refresh_token\", \"client_credentials\" ], \"revocation_endpoint\" : \"https://api.openchallenges.io/oauth2/revoke\", \"token_endpoint_auth_methods_supported\" : [ \"client_secret_basic\", \"client_secret_post\" ], \"scopes_supported\" : [ \"openid\", \"profile\", \"email\", \"user:profile\", \"read:org\", \"write:org\" ], \"issuer\" : \"https://api.openchallenges.io\", \"authorization_endpoint\" : \"https://api.openchallenges.io/oauth2/authorize\", \"token_endpoint\" : \"https://api.openchallenges.io/oauth2/token\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"The request is missing a required parameter\", \"error\" : \"invalid_request\", \"error_uri\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
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
     * GET /oauth2/authorize : OAuth2 Authorization Endpoint
     * Standard OAuth2 authorization endpoint for third-party applications. Redirects users to consent page and returns authorization code. 
     *
     * @param responseType Response type, must be &#39;code&#39; for authorization code flow (required)
     * @param clientId OAuth2 client identifier (required)
     * @param redirectUri Redirect URI for the OAuth2 client (required)
     * @param scope Space-separated list of requested scopes (optional)
     * @param state Opaque state value for CSRF protection (optional)
     * @return Authorization consent page (when user needs to grant consent) (status code 200)
     *         or Redirect to client application with authorization code (status code 302)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see OAuth2Api#oauth2Authorize
     */
    default ResponseEntity<String> oauth2Authorize(String responseType,
        String clientId,
        URI redirectUri,
        String scope,
        String state) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
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
     * POST /oauth2/introspect : Token introspection endpoint
     * RFC 7662 token introspection endpoint to determine the active state of a token and to determine meta-information about this token. 
     *
     * @param token The token to introspect (required)
     * @param tokenTypeHint Hint about the type of token being introspected (optional)
     * @return Token introspection response (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     * @see OAuth2Api#oauth2Introspect
     */
    default ResponseEntity<Oauth2Introspect200ResponseDto> oauth2Introspect(String token,
        String tokenTypeHint) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"sub\" : \"Z5O3upPC88QrAjx00dis\", \"aud\" : \"https://protected.example.net/resource\", \"scope\" : \"read:org user:profile\", \"iss\" : \"https://server.example.com/\", \"active\" : true, \"token_type\" : \"Bearer\", \"exp\" : 1419356238, \"iat\" : 1419350238, \"client_id\" : \"l238j323ds-23ij4\", \"username\" : \"jdoe\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"The request is missing a required parameter\", \"error\" : \"invalid_request\", \"error_uri\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"The request is missing a required parameter\", \"error\" : \"invalid_request\", \"error_uri\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /.well-known/jwks.json : JSON Web Key Set
     * RFC 7517 JSON Web Key Set document containing the public keys used to verify JWT tokens. 
     *
     * @return JSON Web Key Set (status code 200)
     *         or JWKS not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see OAuth2Api#oauth2JwksJson
     */
    default ResponseEntity<Oauth2JwksJson200ResponseDto> oauth2JwksJson() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"keys\" : [ { \"kty\" : \"RSA\", \"e\" : \"AQAB\", \"use\" : \"sig\", \"kid\" : \"2011-04-29\", \"alg\" : \"RS256\", \"n\" : \"0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx4cbbfAAtVT86zwu1RK7aPFFxuhDR1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMstn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6Cf0h4QyQ5v-65YGjQR0_FDW2QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1n91CbOpbIS\" }, { \"kty\" : \"RSA\", \"e\" : \"AQAB\", \"use\" : \"sig\", \"kid\" : \"2011-04-29\", \"alg\" : \"RS256\", \"n\" : \"0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx4cbbfAAtVT86zwu1RK7aPFFxuhDR1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMstn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6Cf0h4QyQ5v-65YGjQR0_FDW2QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1n91CbOpbIS\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"JSON Web Key Set is not available\", \"error\" : \"jwks_not_found\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
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
     * POST /oauth2/revoke : Revoke access or refresh token
     * Revoke an access token or refresh token according to RFC 7009. This endpoint invalidates the specified token and any associated tokens. 
     *
     * @param token The token to revoke (access token or refresh token) (required)
     * @param tokenTypeHint Hint about the type of token being revoked (optional)
     * @param clientId Client identifier (optional)
     * @param clientSecret Client secret (if required) (optional)
     * @return Token successfully revoked (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     * @see OAuth2Api#oauth2RevokeToken
     */
    default ResponseEntity<Oauth2RevokeToken200ResponseDto> oauth2RevokeToken(String token,
        String tokenTypeHint,
        String clientId,
        String clientSecret) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"Token revoked successfully\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"The request is missing a required parameter\", \"error\" : \"invalid_request\", \"error_uri\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"The request is missing a required parameter\", \"error\" : \"invalid_request\", \"error_uri\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /oauth2/token : OAuth2 Token Endpoint
     * Standard OAuth2 token endpoint for exchanging authorization codes for access tokens. Also supports refresh token grant and client credentials grant. 
     *
     * @param grantType OAuth2 grant type (required)
     * @param clientId OAuth2 client identifier (required)
     * @param code Authorization code (required for authorization_code grant) (optional)
     * @param redirectUri Redirect URI (required for authorization_code grant) (optional)
     * @param clientSecret OAuth2 client secret (optional)
     * @param refreshToken Refresh token (required for refresh_token grant) (optional)
     * @param scope Requested scope (optional, space-separated) (optional)
     * @return Access token response (status code 200)
     *         or Invalid request (status code 400)
     *         or Invalid client credentials (status code 401)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see OAuth2Api#oauth2Token
     */
    default ResponseEntity<OAuth2TokenResponseDto> oauth2Token(String grantType,
        String clientId,
        String code,
        URI redirectUri,
        String clientSecret,
        String refreshToken,
        String scope) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"access_token\" : \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"refresh_token\" : \"def456...\", \"scope\" : \"read:org write:org user:profile\", \"token_type\" : \"Bearer\", \"expires_in\" : 3600 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"The request is missing a required parameter\", \"error\" : \"invalid_request\", \"error_uri\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"The request is missing a required parameter\", \"error\" : \"invalid_request\", \"error_uri\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
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
     * GET /oauth2/userinfo : OAuth2 user info endpoint
     * Standard OAuth2/OIDC user info endpoint that returns information about the authenticated user. Requires a valid access token with appropriate scopes. 
     *
     * @return User information (status code 200)
     *         or Invalid or expired access token (status code 401)
     *         or Insufficient scope (status code 403)
     * @see OAuth2Api#oauth2UserInfo
     */
    default ResponseEntity<GetCurrentUser200ResponseDto> oauth2UserInfo() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"sub\" : \"248289761001\", \"website\" : \"https://janedoe.example.com\", \"email_verified\" : true, \"updated_at\" : 1311280970, \"name\" : \"Jane Doe\", \"preferred_username\" : \"j.doe\", \"given_name\" : \"Jane\", \"locale\" : \"en-US\", \"family_name\" : \"Doe\", \"email\" : \"janedoe@example.com\", \"picture\" : \"https://example.com/profile.jpg\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"The request is missing a required parameter\", \"error\" : \"invalid_request\", \"error_uri\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"The request is missing a required parameter\", \"error\" : \"invalid_request\", \"error_uri\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /.well-known/openid-configuration : OAuth2 Authorization Server Metadata
     * RFC 8414 OAuth2 Authorization Server Metadata endpoint. Returns the OAuth2/OIDC discovery document with server configuration. 
     *
     * @return OAuth2 Authorization Server Metadata (status code 200)
     *         or Configuration not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see OAuth2Api#oauth2WellKnownOpenidConfiguration
     */
    default ResponseEntity<Oauth2WellKnownOpenidConfiguration200ResponseDto> oauth2WellKnownOpenidConfiguration() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"response_types_supported\" : [ \"code\", \"token\", \"id_token\" ], \"introspection_endpoint\" : \"https://api.openchallenges.io/oauth2/introspect\", \"grant_types_supported\" : [ \"authorization_code\", \"refresh_token\", \"client_credentials\" ], \"revocation_endpoint\" : \"https://api.openchallenges.io/oauth2/revoke\", \"scopes_supported\" : [ \"openid\", \"profile\", \"email\", \"user:profile\", \"read:org\", \"write:org\" ], \"issuer\" : \"https://api.openchallenges.io\", \"authorization_endpoint\" : \"https://api.openchallenges.io/oauth2/authorize\", \"userinfo_endpoint\" : \"https://api.openchallenges.io/oauth2/userinfo\", \"code_challenge_methods_supported\" : [ \"S256\", \"plain\" ], \"jwks_uri\" : \"https://api.openchallenges.io/.well-known/jwks.json\", \"subject_types_supported\" : [ \"public\" ], \"id_token_signing_alg_values_supported\" : [ \"RS256\", \"HS256\" ], \"token_endpoint_auth_methods_supported\" : [ \"client_secret_basic\", \"client_secret_post\" ], \"token_endpoint\" : \"https://api.openchallenges.io/oauth2/token\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"OAuth2 configuration is not available\", \"error\" : \"configuration_not_found\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
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
