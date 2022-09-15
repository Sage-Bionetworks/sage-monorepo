package org.sagebionetworks.challenge.api;

import org.sagebionetworks.challenge.model.dto.User;
import org.sagebionetworks.challenge.model.dto.UserUpdateRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-09-15T22:55:24.066910493Z[Etc/UTC]")
@Controller
public class ApiApiController implements ApiApi {

    private final ApiApiDelegate delegate;

    public ApiApiController(@Autowired(required = false) ApiApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ApiApiDelegate() {});
    }

    @Override
    public ApiApiDelegate getDelegate() {
        return delegate;
    }

}
