package org.sagebionetworks.challenge.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.sagebionetworks.challenge.model.dto.UserCreateRequestDto;
import org.sagebionetworks.challenge.model.dto.UserCreateResponseDto;
import org.sagebionetworks.challenge.model.dto.UserDto;
import org.sagebionetworks.challenge.model.dto.UsersPageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link UserApiController}}. Implement this interface with a {@link
 * org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface UserApiDelegate {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  /**
   * POST /users/register : Create a user Create a user with the specified account name
   *
   * @param userCreateRequestDto (required)
   * @return Account created (status code 201) or Invalid request (status code 400) or The request
   *     conflicts with current state of the target resource (status code 409) or The request cannot
   *     be fulfilled due to an unexpected server error (status code 500)
   * @see UserApi#createUser
   */
  default ResponseEntity<UserCreateResponseDto> createUser(
    UserCreateRequestDto userCreateRequestDto
  ) {
    getRequest()
      .ifPresent(request -> {
        for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
            String exampleString = "{ \"id\" : \"507f1f77bcf86cd799439011\" }";
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
   * DELETE /users/{userId} : Delete a user Deletes the user specified
   *
   * @param userId The unique identifier of the user, either the user account ID or login (required)
   * @return Deleted (status code 200) or The specified resource was not found (status code 400) or
   *     The request cannot be fulfilled due to an unexpected server error (status code 500)
   * @see UserApi#deleteUser
   */
  default ResponseEntity<Object> deleteUser(Long userId) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * GET /users/{userId} : Get a user Returns the user specified
   *
   * @param userId The unique identifier of the user, either the user account ID or login (required)
   * @return A user (status code 200) or The specified resource was not found (status code 404) or
   *     The request cannot be fulfilled due to an unexpected server error (status code 500)
   * @see UserApi#getUser
   */
  default ResponseEntity<UserDto> getUser(Long userId) {
    getRequest()
      .ifPresent(request -> {
        for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
          if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
            String exampleString =
              "{ \"login\" : \"awesome-user\", \"email\" : \"awesome-user@example.org\", \"name\" : \"Awesome User\", \"status\" : \"approved\", \"avatarUrl\" : \"https://example.com/awesome-avatar.png\", \"bio\" : \"A great bio\", \"createdAt\" : \"2017-07-08T16:18:44-04:00\", \"updatedAt\" : \"2017-07-08T16:18:44-04:00\", \"type\" : \"User\" }";
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
   * GET /users : Get all users Returns the users
   *
   * @param pageNumber The page number (optional, default to 0)
   * @param pageSize The number of items in a single page (optional, default to 100)
   * @return Success (status code 200) or Invalid request (status code 400) or The request cannot be
   *     fulfilled due to an unexpected server error (status code 500)
   * @see UserApi#listUsers
   */
  default ResponseEntity<UsersPageDto> listUsers(Integer pageNumber, Integer pageSize) {
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
