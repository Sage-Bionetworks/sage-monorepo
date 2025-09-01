package org.sagebionetworks.openchallenges.auth.service.api;

import org.sagebionetworks.openchallenges.auth.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.GetCurrentUser200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2ErrorDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2TokenResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2AuthorizationServerMetadata200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2Introspect200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2JwksJson200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2JwksJson404ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2RevokeToken200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2WellKnownOpenidConfiguration200ResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.Oauth2WellKnownOpenidConfiguration404ResponseDto;
import java.net.URI;


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
public class OAuth2ApiController implements OAuth2Api {

    private final OAuth2ApiDelegate delegate;

    public OAuth2ApiController(@Autowired(required = false) OAuth2ApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new OAuth2ApiDelegate() {});
    }

    @Override
    public OAuth2ApiDelegate getDelegate() {
        return delegate;
    }

}
