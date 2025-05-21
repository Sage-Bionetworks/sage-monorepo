package org.sagebionetworks.agora.gene.api.api;

import org.sagebionetworks.agora.gene.api.model.dto.BasicErrorDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileProteinSearchQueryDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaSearchQueryDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfilesProteinPageDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfilesRnaPageDto;


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
@RequestMapping("${openapi.agoraGene.base-path:/v1}")
public class DifferentialExpressionProfileApiController implements DifferentialExpressionProfileApi {

    private final DifferentialExpressionProfileApiDelegate delegate;

    public DifferentialExpressionProfileApiController(@Autowired(required = false) DifferentialExpressionProfileApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new DifferentialExpressionProfileApiDelegate() {});
    }

    @Override
    public DifferentialExpressionProfileApiDelegate getDelegate() {
        return delegate;
    }

}
