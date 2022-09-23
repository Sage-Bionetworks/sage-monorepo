package org.sagebionetworks.challenge.api;

import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;
import org.sagebionetworks.challenge.model.dto.PageableDto;
import org.sagebionetworks.challenge.model.dto.UserDto;
import org.sagebionetworks.challenge.model.dto.UserUpdateRequestDto;
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
   * POST /api/v1/users/register
   *
   * @param userDto (required)
   * @return OK (status code 200)
   * @see UserApi#createUser
   */
  default ResponseEntity<UserDto> createUser(UserDto userDto) {
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                  String exampleString =
                      "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
                  ApiUtil.setExampleResponse(request, "*/*", exampleString);
                  break;
                }
              }
            });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * GET /api/v1/users/{id}
   *
   * @param id (required)
   * @return OK (status code 200)
   * @see UserApi#getUser
   */
  default ResponseEntity<UserDto> getUser(Long id) {
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                  String exampleString =
                      "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
                  ApiUtil.setExampleResponse(request, "*/*", exampleString);
                  break;
                }
              }
            });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * GET /api/v1/users/
   *
   * @param pageable (optional)
   * @return OK (status code 200)
   * @see UserApi#listUsers
   */
  default ResponseEntity<List<UserDto>> listUsers(PageableDto pageable) {
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                  String exampleString =
                      "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
                  ApiUtil.setExampleResponse(request, "*/*", exampleString);
                  break;
                }
              }
            });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * PATCH /api/v1/users/{id}
   *
   * @param id (required)
   * @param userUpdateRequestDto (required)
   * @return OK (status code 200)
   * @see UserApi#updateUser
   */
  default ResponseEntity<UserDto> updateUser(Long id, UserUpdateRequestDto userUpdateRequestDto) {
    getRequest()
        .ifPresent(
            request -> {
              for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                  String exampleString =
                      "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
                  ApiUtil.setExampleResponse(request, "*/*", exampleString);
                  break;
                }
              }
            });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
