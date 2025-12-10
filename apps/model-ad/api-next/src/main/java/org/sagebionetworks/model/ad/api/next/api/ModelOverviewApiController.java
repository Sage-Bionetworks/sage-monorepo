package org.sagebionetworks.model.ad.api.next.api;

import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.sagebionetworks.model.ad.api.next.model.dto.BasicErrorDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewsPageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Generated(
  value = "org.openapitools.codegen.languages.SpringCodegen",
  comments = "Generator version: 7.14.0"
)
@Controller
@RequestMapping("${openapi.modelADAPINext.base-path:/v1}")
public class ModelOverviewApiController implements ModelOverviewApi {

  private final ModelOverviewApiDelegate delegate;

  public ModelOverviewApiController(
    @Autowired(required = false) ModelOverviewApiDelegate delegate
  ) {
    this.delegate = Optional.ofNullable(delegate).orElse(new ModelOverviewApiDelegate() {});
  }

  @Override
  public ModelOverviewApiDelegate getDelegate() {
    return delegate;
  }
}
