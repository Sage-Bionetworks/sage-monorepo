package org.sagebionetworks.challenge.api;

import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.model.dto.UserUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

/**
 * A delegate to be called by the {@link UserControllerApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface UserControllerApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/v1/users/register
     *
     * @param user  (required)
     * @return OK (status code 200)
     * @see UserControllerApi#createUser
     */
    default ResponseEntity<User> createUser(User user) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                    String exampleString = "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
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
     * @param id  (required)
     * @return OK (status code 200)
     * @see UserControllerApi#getUser
     */
    default ResponseEntity<User> getUser(Long id) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                    String exampleString = "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
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
     * @param page Zero-based page index (0..N) (optional, default to 0)
     * @param size The size of the page to be returned (optional, default to 20)
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported. (optional)
     * @return OK (status code 200)
     * @see UserControllerApi#listUsers
     */
    default ResponseEntity<List<User>> listUsers(Integer page,
        Integer size,
        List<String> sort) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                    String exampleString = "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
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
     * @param id  (required)
     * @param userUpdateRequest  (required)
     * @return OK (status code 200)
     * @see UserControllerApi#updateUser
     */
    default ResponseEntity<User> updateUser(Long id,
        UserUpdateRequest userUpdateRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
                    String exampleString = "{ \"password\" : \"password\", \"id\" : 0, \"email\" : \"email\", \"authId\" : \"authId\", \"username\" : \"username\", \"status\" : \"PENDING\" }";
                    ApiUtil.setExampleResponse(request, "*/*", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
