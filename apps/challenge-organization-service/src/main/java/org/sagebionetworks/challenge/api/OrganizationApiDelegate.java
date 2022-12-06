package org.sagebionetworks.challenge.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.sagebionetworks.challenge.model.dto.OrganizationDto;
import org.sagebionetworks.challenge.model.dto.OrganizationsPageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link OrganizationApiController}}. Implement this interface with
 * a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface OrganizationApiDelegate {

  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  /**
   * GET /organizations/{organizationId} : Get an organization Returns the organization specified
   *
   * @param organizationId The unique identifier of the organization (required)
   * @return An organization (status code 200) or The specified resource was not found (status code
   *     404) or The request cannot be fulfilled due to an unexpected server error (status code 500)
   * @see OrganizationApi#getOrganization
   */
  default ResponseEntity<OrganizationDto> getOrganization(Long organizationId) {
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                  String exampleString = "{ \"name\" : \"DREAM\" }";
                  ApiUtil.setExampleResponse(request, "application/json", exampleString);
                  break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                  String exampleString =
                      "Custom MIME type example not yet supported: application/problem+json";
                  ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                  break;
                }
              }
            });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * GET /organizations : Get all organizations Returns the organizations
   *
   * @param pageNumber The page number (optional, default to 0)
   * @param pageSize The number of items in a single page (optional, default to 100)
   * @return Success (status code 200) or Invalid request (status code 400) or The request cannot be
   *     fulfilled due to an unexpected server error (status code 500)
   * @see OrganizationApi#listOrganizations
   */
  default ResponseEntity<OrganizationsPageDto> listOrganizations(
      Integer pageNumber, Integer pageSize) {
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                  String exampleString = "null";
                  ApiUtil.setExampleResponse(request, "application/json", exampleString);
                  break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/problem+json"))) {
                  String exampleString =
                      "Custom MIME type example not yet supported: application/problem+json";
                  ApiUtil.setExampleResponse(request, "application/problem+json", exampleString);
                  break;
                }
              }
            });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
