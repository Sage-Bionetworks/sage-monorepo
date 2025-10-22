package org.sagebionetworks.bixarena.auth.service.api;

import org.sagebionetworks.bixarena.auth.service.model.dto.BasicErrorDto;
import org.sagebionetworks.bixarena.auth.service.model.dto.GetJwks200ResponseDto;
import org.sagebionetworks.bixarena.auth.service.model.dto.MintInternalToken200ResponseDto;
import org.sagebionetworks.bixarena.auth.service.model.dto.OidcCallback200ResponseDto;
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
 * A delegate to be called by the {@link AuthApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface AuthApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /.well-known/jwks.json : JSON Web Key Set
     * Returns the public keys used to verify internally issued JWTs.
     *
     * @return JWKS document (status code 200)
     *         or Invalid request parameters (status code 400)
     * @see AuthApi#getJwks
     */
    default ResponseEntity<GetJwks200ResponseDto> getJwks() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"keys\" : [ \"{}\", \"{}\" ] }";
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
     * POST /auth/logout : Logout current session
     * Invalidate the current authenticated session.
     *
     * @return Logged out (idempotent) (status code 204)
     * @see AuthApi#logout
     */
    default ResponseEntity<Void> logout() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /token : Mint short-lived internal JWT
     * Exchanges an authenticated session (cookie) for an internal JWT.
     *
     * @return Access token response (status code 200)
     *         or Unauthorized (status code 401)
     * @see AuthApi#mintInternalToken
     */
    default ResponseEntity<MintInternalToken200ResponseDto> mintInternalToken() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"access_token\" : \"access_token\", \"token_type\" : \"Bearer\", \"expires_in\" : 600 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"instance\" : \"instance\", \"detail\" : \"detail\", \"title\" : \"title\", \"type\" : \"type\", \"status\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /auth/oidc/callback : OIDC redirect callback
     * Handles redirect from Synapse, validates state and nonce, establishes authenticated session.
     *
     * @param code  (required)
     * @param state  (required)
     * @return Authentication successful (status code 200)
     *         or Invalid request parameters (status code 400)
     *         or Unauthorized (status code 401)
     * @see AuthApi#oidcCallback
     */
    default ResponseEntity<OidcCallback200ResponseDto> oidcCallback(String code,
        String state) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/problem+json";
                    ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"instance\" : \"instance\", \"detail\" : \"detail\", \"title\" : \"title\", \"type\" : \"type\", \"status\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /auth/oidc/start : Start Synapse OIDC authorization code flow
     * Initiates the OIDC login by redirecting the user to Synapse with state and nonce.
     *
     * @return Flow started (no content; clients should follow redirect) (status code 204)
     *         or Redirect to Synapse login (status code 302)
     *         or Invalid request parameters (status code 400)
     * @see AuthApi#startOidc
     */
    default ResponseEntity<Void> startOidc() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
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
