package org.sagebionetworks.openchallenges.organization.service.api;

import org.sagebionetworks.openchallenges.organization.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.LoginRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.LoginResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public interface AuthenticationApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /auth/login : User login
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
                    String exampleString = "{ \"expiresIn\" : 86400, \"tokenType\" : \"Bearer\", \"token\" : \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\" }";
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
