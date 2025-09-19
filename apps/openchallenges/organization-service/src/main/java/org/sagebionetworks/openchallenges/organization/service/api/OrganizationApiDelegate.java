package org.sagebionetworks.openchallenges.organization.service.api;

import org.sagebionetworks.openchallenges.organization.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationCreateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationUpdateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationsPageDto;
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
 * A delegate to be called by the {@link OrganizationApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public interface OrganizationApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /organizations : Create an organization
     * Create an organization with the specified account name
     *
     * @param organizationCreateRequestDto  (required)
     * @return Organization created successfully (status code 201)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The request conflicts with current state of the target resource (status code 409)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see OrganizationApi#createOrganization
     */
    default ResponseEntity<OrganizationDto> createOrganization(OrganizationCreateRequestDto organizationCreateRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2022-07-04T22:19:11Z\", \"avatarKey\" : \"logo/dream.png\", \"websiteUrl\" : \"https://openchallenges.io\", \"name\" : \"Example organization\", \"description\" : \"A description of the organization.\", \"id\" : 1, \"challengeCount\" : 10, \"login\" : \"example-org\", \"shortName\" : \"OC\", \"updatedAt\" : \"2022-07-04T22:19:11Z\" }";
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
     * DELETE /organizations/{org} : Delete an organization
     * Deletes the organization specified by its login or ID.
     *
     * @param org The id or login of the organization. (required)
     * @return Organization successfully deleted (status code 204)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see OrganizationApi#deleteOrganization
     */
    default ResponseEntity<Void> deleteOrganization(String org) {
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
     * GET /organizations/{org} : Get an organization
     * Returns the organization identified by its login or ID.
     *
     * @param org The id or login of the organization. (required)
     * @return Organization retrieved successfully (status code 200)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see OrganizationApi#getOrganization
     */
    default ResponseEntity<OrganizationDto> getOrganization(String org) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2022-07-04T22:19:11Z\", \"avatarKey\" : \"logo/dream.png\", \"websiteUrl\" : \"https://openchallenges.io\", \"name\" : \"Example organization\", \"description\" : \"A description of the organization.\", \"id\" : 1, \"challengeCount\" : 10, \"login\" : \"example-org\", \"shortName\" : \"OC\", \"updatedAt\" : \"2022-07-04T22:19:11Z\" }";
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
     * GET /organizations : List organizations
     * List organizations
     *
     * @param organizationSearchQuery The search query used to find organizations. (optional)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see OrganizationApi#listOrganizations
     */
    default ResponseEntity<OrganizationsPageDto> listOrganizations(OrganizationSearchQueryDto organizationSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"number\" : 99, \"size\" : 99, \"totalPages\" : 99, \"organizations\" : [ { \"createdAt\" : \"2022-07-04T22:19:11Z\", \"avatarKey\" : \"logo/dream.png\", \"websiteUrl\" : \"https://openchallenges.io\", \"name\" : \"Example organization\", \"description\" : \"A description of the organization.\", \"id\" : 1, \"challengeCount\" : 10, \"login\" : \"example-org\", \"shortName\" : \"OC\", \"updatedAt\" : \"2022-07-04T22:19:11Z\" }, { \"createdAt\" : \"2022-07-04T22:19:11Z\", \"avatarKey\" : \"logo/dream.png\", \"websiteUrl\" : \"https://openchallenges.io\", \"name\" : \"Example organization\", \"description\" : \"A description of the organization.\", \"id\" : 1, \"challengeCount\" : 10, \"login\" : \"example-org\", \"shortName\" : \"OC\", \"updatedAt\" : \"2022-07-04T22:19:11Z\" } ], \"hasPrevious\" : true, \"hasNext\" : true, \"totalElements\" : 99 }";
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
     * PUT /organizations/{org} : Update an existing organization
     * Updates an existing organization.
     *
     * @param org The id or login of the organization. (required)
     * @param organizationUpdateRequestDto  (required)
     * @return Organization successfully updated (status code 200)
     *         or Invalid request (status code 400)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request conflicts with current state of the target resource (status code 409)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see OrganizationApi#updateOrganization
     */
    default ResponseEntity<OrganizationDto> updateOrganization(String org,
        OrganizationUpdateRequestDto organizationUpdateRequestDto) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2022-07-04T22:19:11Z\", \"avatarKey\" : \"logo/dream.png\", \"websiteUrl\" : \"https://openchallenges.io\", \"name\" : \"Example organization\", \"description\" : \"A description of the organization.\", \"id\" : 1, \"challengeCount\" : 10, \"login\" : \"example-org\", \"shortName\" : \"OC\", \"updatedAt\" : \"2022-07-04T22:19:11Z\" }";
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

}
