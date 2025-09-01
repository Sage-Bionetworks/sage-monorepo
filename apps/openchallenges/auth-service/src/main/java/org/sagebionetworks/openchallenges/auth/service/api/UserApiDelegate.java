package org.sagebionetworks.openchallenges.auth.service.api;

import org.sagebionetworks.openchallenges.auth.service.model.dto.GetCurrentUser200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2ErrorDto;
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
 * A delegate to be called by the {@link UserApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface UserApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /user : Get current user information
     * Get information about the currently authenticated user. This is an alias for the OAuth2 userinfo endpoint for backward compatibility. 
     *
     * @return User information (status code 200)
     *         or Invalid or expired access token (status code 401)
     *         or Insufficient scope (status code 403)
     * @see UserApi#getCurrentUser
     */
    default ResponseEntity<GetCurrentUser200ResponseDto> getCurrentUser() {
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

}
