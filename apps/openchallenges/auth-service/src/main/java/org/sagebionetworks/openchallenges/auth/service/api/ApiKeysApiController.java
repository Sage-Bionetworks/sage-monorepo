package org.sagebionetworks.openchallenges.auth.service.api;

import org.sagebionetworks.openchallenges.auth.service.model.dto.ApiKeyDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.CreateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.CreateApiKeyResponseDto;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
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

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
@Controller
@RequestMapping("${openapi.openChallengesAuth.base-path:/v1}")
public class ApiKeysApiController implements ApiKeysApi {

    private final ApiKeysApiDelegate delegate;

    public ApiKeysApiController(@Autowired(required = false) ApiKeysApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ApiKeysApiDelegate() {});
    }

    @Override
    public ApiKeysApiDelegate getDelegate() {
        return delegate;
    }

}
