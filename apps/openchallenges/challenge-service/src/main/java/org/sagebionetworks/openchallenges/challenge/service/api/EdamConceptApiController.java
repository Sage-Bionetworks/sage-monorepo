package org.sagebionetworks.openchallenges.challenge.service.api;

import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.openChallengesChallengeREST.base-path:/v1}")
public class EdamConceptApiController implements EdamConceptApi {

  private final EdamConceptApiDelegate delegate;

  public EdamConceptApiController(@Autowired(required = false) EdamConceptApiDelegate delegate) {
    this.delegate = Optional.ofNullable(delegate).orElse(new EdamConceptApiDelegate() {});
  }

  @Override
  public EdamConceptApiDelegate getDelegate() {
    return delegate;
  }
}
