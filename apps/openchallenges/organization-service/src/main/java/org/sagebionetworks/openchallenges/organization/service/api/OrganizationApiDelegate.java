package org.sagebionetworks.openchallenges.organization.service.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationsPageDto;
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
   * GET /organizations/{org} : Get an organization Returns the organization specified
   *
   * @param org The id or login of the organization. (required)
   * @return An organization (status code 200) or The specified resource was not found (status code
   *     404) or The request cannot be fulfilled due to an unexpected server error (status code 500)
   * @see OrganizationApi#getOrganization
   */
  default ResponseEntity<OrganizationDto> getOrganization(String org) {
    getRequest()
      .ifPresent(request -> {
        for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
            String exampleString =
              "{ \"createdAt\" : \"2022-07-04T22:19:11Z\", \"avatarKey\" : \"logo/dream.png\", \"websiteUrl\" : \"https://openchallenges.io\", \"acronym\" : \"OC\", \"name\" : \"Example organization\", \"description\" : \"A description of the organization.\", \"id\" : 1, \"challengeCount\" : 10, \"login\" : \"example-org\", \"updatedAt\" : \"2022-07-04T22:19:11Z\" }";
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
   * GET /organizations : List organizations List organizations
   *
   * @param organizationSearchQuery The search query used to find organizations. (optional)
   * @return Success (status code 200) or Invalid request (status code 400) or The request cannot be
   *     fulfilled due to an unexpected server error (status code 500)
   * @see OrganizationApi#listOrganizations
   */
  default ResponseEntity<OrganizationsPageDto> listOrganizations(
    OrganizationSearchQueryDto organizationSearchQuery
  ) {
    getRequest()
      .ifPresent(request -> {
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
