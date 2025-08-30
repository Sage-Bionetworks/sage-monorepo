package org.sagebionetworks.openchallenges.auth.service.api;

import org.sagebionetworks.openchallenges.auth.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2AuthorizeRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2AuthorizeResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2CallbackRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.RefreshTokenRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.RefreshTokenResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateJwtRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateJwtResponseDto;


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
public class AuthenticationApiController implements AuthenticationApi {

    private final AuthenticationApiDelegate delegate;

    public AuthenticationApiController(@Autowired(required = false) AuthenticationApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new AuthenticationApiDelegate() {});
    }

    @Override
    public AuthenticationApiDelegate getDelegate() {
        return delegate;
    }

}
