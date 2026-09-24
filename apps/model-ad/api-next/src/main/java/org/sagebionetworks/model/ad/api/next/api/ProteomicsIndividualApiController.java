package org.sagebionetworks.model.ad.api.next.api;

import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualFilterQueryDto;


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
@RequestMapping("${openapi.modelADAPINext.base-path:/v1}")
public class ProteomicsIndividualApiController implements ProteomicsIndividualApi {

    private final ProteomicsIndividualApiDelegate delegate;

    public ProteomicsIndividualApiController(@Autowired(required = false) ProteomicsIndividualApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ProteomicsIndividualApiDelegate() {});
    }

    @Override
    public ProteomicsIndividualApiDelegate getDelegate() {
        return delegate;
    }

}
