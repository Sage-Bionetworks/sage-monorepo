package org.sagebionetworks.bixarena.api.api;

import org.sagebionetworks.bixarena.api.model.dto.BasicErrorDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardEntryPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardListInnerDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardModelHistoryPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardModelHistoryQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotPageDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.RateLimitErrorDto;


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
@RequestMapping("${openapi.bixArenaAPIService.base-path:/v1}")
public class LeaderboardApiController implements LeaderboardApi {

    private final LeaderboardApiDelegate delegate;

    public LeaderboardApiController(@Autowired(required = false) LeaderboardApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new LeaderboardApiDelegate() {});
    }

    @Override
    public LeaderboardApiDelegate getDelegate() {
        return delegate;
    }

}
