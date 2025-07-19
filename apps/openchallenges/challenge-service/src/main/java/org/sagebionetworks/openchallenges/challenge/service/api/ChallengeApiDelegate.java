package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeJsonLdDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPageDto;
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
 * A delegate to be called by the {@link ChallengeApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public interface ChallengeApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * DELETE /challenges/{challengeId} : Delete a challenge
     * Deletes a challenge by its unique ID. This action is irreversible. 
     *
     * @param challengeId The unique identifier of the challenge. (required)
     * @return Deletion successful (status code 204)
     *         or Unauthorized (status code 401)
     *         or The user does not have the permission to perform this action (status code 403)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see ChallengeApi#deleteChallengeById
     */
    default ResponseEntity<Void> deleteChallengeById(Long challengeId) {
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
     * GET /challenges/{challengeId} : Get a challenge
     * Returns the challenge specified
     *
     * @param challengeId The unique identifier of the challenge. (required)
     * @return A challenge (status code 200)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see ChallengeApi#getChallenge
     */
    default ResponseEntity<ChallengeDto> getChallenge(Long challengeId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"avatarUrl\" : \"https://openchallenges.io\", \"endDate\" : \"2017-07-21\", \"description\" : \"This is an example description of the challenge.\", \"platform\" : { \"name\" : \"Example Challenge Platform\", \"id\" : 1, \"slug\" : \"example-challenge-platform\" }, \"starredCount\" : 100, \"createdAt\" : \"2022-07-04T22:19:11Z\", \"incentives\" : [ \"publication\", \"publication\" ], \"submissionTypes\" : [ \"container_image\", \"container_image\" ], \"websiteUrl\" : \"https://openchallenges.io\", \"name\" : \"name\", \"id\" : 1, \"categories\" : [ \"featured\", \"featured\" ], \"headline\" : \"Example challenge headline\", \"operation\" : { \"classId\" : \"http://edamontology.org/data_0850\", \"preferredLabel\" : \"Sequence set\", \"id\" : 1 }, \"slug\" : \"awesome-challenge\", \"startDate\" : \"2017-07-21\", \"doi\" : \"https://doi.org/123/abc\", \"status\" : \"active\", \"inputDataTypes\" : [ { \"classId\" : \"http://edamontology.org/data_0850\", \"preferredLabel\" : \"Sequence set\", \"id\" : 1 }, { \"classId\" : \"http://edamontology.org/data_0850\", \"preferredLabel\" : \"Sequence set\", \"id\" : 1 } ], \"updatedAt\" : \"2022-07-04T22:19:11Z\" }";
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
     * GET /challenges/{challengeId}/json-ld : Get a challenge in JSON-LD format
     * Returns the challenge specified in JSON-LD format
     *
     * @param challengeId The unique identifier of the challenge. (required)
     * @return A challenge (status code 200)
     *         or The specified resource was not found (status code 404)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see ChallengeApi#getChallengeJsonLd
     */
    default ResponseEntity<ChallengeJsonLdDto> getChallengeJsonLd(Long challengeId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/ld+json"))) {
                    String exampleString = "Custom MIME type example not yet supported: application/ld+json";
                    ApiUtil.setExampleResponse(request, "application/ld+json", exampleString);
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
     * GET /challenges : List challenges
     * List challenges
     *
     * @param challengeSearchQuery The search query used to find challenges. (optional)
     * @return Success (status code 200)
     *         or Invalid request (status code 400)
     *         or The request cannot be fulfilled due to an unexpected server error (status code 500)
     * @see ChallengeApi#listChallenges
     */
    default ResponseEntity<ChallengesPageDto> listChallenges(ChallengeSearchQueryDto challengeSearchQuery) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"number\" : 99, \"size\" : 99, \"challenges\" : [ { \"avatarUrl\" : \"https://openchallenges.io\", \"endDate\" : \"2017-07-21\", \"description\" : \"This is an example description of the challenge.\", \"platform\" : { \"name\" : \"Example Challenge Platform\", \"id\" : 1, \"slug\" : \"example-challenge-platform\" }, \"starredCount\" : 100, \"createdAt\" : \"2022-07-04T22:19:11Z\", \"incentives\" : [ \"publication\", \"publication\" ], \"submissionTypes\" : [ \"container_image\", \"container_image\" ], \"websiteUrl\" : \"https://openchallenges.io\", \"name\" : \"name\", \"id\" : 1, \"categories\" : [ \"featured\", \"featured\" ], \"headline\" : \"Example challenge headline\", \"operation\" : { \"classId\" : \"http://edamontology.org/data_0850\", \"preferredLabel\" : \"Sequence set\", \"id\" : 1 }, \"slug\" : \"awesome-challenge\", \"startDate\" : \"2017-07-21\", \"doi\" : \"https://doi.org/123/abc\", \"status\" : \"active\", \"inputDataTypes\" : [ { \"classId\" : \"http://edamontology.org/data_0850\", \"preferredLabel\" : \"Sequence set\", \"id\" : 1 }, { \"classId\" : \"http://edamontology.org/data_0850\", \"preferredLabel\" : \"Sequence set\", \"id\" : 1 } ], \"updatedAt\" : \"2022-07-04T22:19:11Z\" }, { \"avatarUrl\" : \"https://openchallenges.io\", \"endDate\" : \"2017-07-21\", \"description\" : \"This is an example description of the challenge.\", \"platform\" : { \"name\" : \"Example Challenge Platform\", \"id\" : 1, \"slug\" : \"example-challenge-platform\" }, \"starredCount\" : 100, \"createdAt\" : \"2022-07-04T22:19:11Z\", \"incentives\" : [ \"publication\", \"publication\" ], \"submissionTypes\" : [ \"container_image\", \"container_image\" ], \"websiteUrl\" : \"https://openchallenges.io\", \"name\" : \"name\", \"id\" : 1, \"categories\" : [ \"featured\", \"featured\" ], \"headline\" : \"Example challenge headline\", \"operation\" : { \"classId\" : \"http://edamontology.org/data_0850\", \"preferredLabel\" : \"Sequence set\", \"id\" : 1 }, \"slug\" : \"awesome-challenge\", \"startDate\" : \"2017-07-21\", \"doi\" : \"https://doi.org/123/abc\", \"status\" : \"active\", \"inputDataTypes\" : [ { \"classId\" : \"http://edamontology.org/data_0850\", \"preferredLabel\" : \"Sequence set\", \"id\" : 1 }, { \"classId\" : \"http://edamontology.org/data_0850\", \"preferredLabel\" : \"Sequence set\", \"id\" : 1 } ], \"updatedAt\" : \"2022-07-04T22:19:11Z\" } ], \"totalPages\" : 99, \"hasPrevious\" : true, \"hasNext\" : true, \"totalElements\" : 99 }";
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
