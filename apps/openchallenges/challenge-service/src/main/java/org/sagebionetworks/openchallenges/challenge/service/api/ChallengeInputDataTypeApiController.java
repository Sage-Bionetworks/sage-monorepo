package org.sagebionetworks.openchallenges.challenge.service.api;

import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.challenge.base-path:/v1}")
public class ChallengeInputDataTypeApiController implements ChallengeInputDataTypeApi {

  private final ChallengeInputDataTypeApiDelegate delegate;

  public ChallengeInputDataTypeApiController(
      @Autowired(required = false) ChallengeInputDataTypeApiDelegate delegate) {
    this.delegate =
        Optional.ofNullable(delegate).orElse(new ChallengeInputDataTypeApiDelegate() {});
  }

  @Override
  public ChallengeInputDataTypeApiDelegate getDelegate() {
    return delegate;
  }
}
