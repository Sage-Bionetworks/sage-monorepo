package org.sagebionetworks.agora.api.next.api;

import org.sagebionetworks.agora.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetsPageDto;


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
@RequestMapping("${openapi.agoraAPINext.base-path:/v1}")
public class NominatedTargetApiController implements NominatedTargetApi {

    private final NominatedTargetApiDelegate delegate;

    public NominatedTargetApiController(@Autowired(required = false) NominatedTargetApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new NominatedTargetApiDelegate() {});
    }

    @Override
    public NominatedTargetApiDelegate getDelegate() {
        return delegate;
    }

}
