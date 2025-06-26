package org.sagebionetworks.openchallenges.challenge.service.api;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.BasicErrorDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPerYearDto;


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

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
@Controller
@RequestMapping("${openapi.openChallengesChallengeREST.base-path:/v1}")
public class ChallengeAnalyticsApiController implements ChallengeAnalyticsApi {

    private final ChallengeAnalyticsApiDelegate delegate;

    public ChallengeAnalyticsApiController(@Autowired(required = false) ChallengeAnalyticsApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ChallengeAnalyticsApiDelegate() {});
    }

    @Override
    public ChallengeAnalyticsApiDelegate getDelegate() {
        return delegate;
    }

}
