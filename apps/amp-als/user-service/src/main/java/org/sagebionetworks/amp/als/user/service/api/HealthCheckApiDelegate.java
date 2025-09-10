package org.sagebionetworks.amp.als.user.service.api;

import org.sagebionetworks.amp.als.user.service.model.dto.BasicErrorDto;
import org.sagebionetworks.amp.als.user.service.model.dto.HealthCheckDto;
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
 * A delegate to be called by the {@link HealthCheckApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public interface HealthCheckApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /health-check : Get health check information
     * Get information about the health of the service
     *
     * @return Success (status code 200)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see HealthCheckApi#getHealthCheck
     */
    default ResponseEntity<HealthCheckDto> getHealthCheck() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"pass\" }";
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
