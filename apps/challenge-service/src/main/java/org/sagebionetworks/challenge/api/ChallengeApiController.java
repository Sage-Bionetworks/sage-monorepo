package org.sagebionetworks.challenge.api;

import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.challenge.base-path:/v1}")
public class ChallengeApiController implements ChallengeApi {

  private final ChallengeApiDelegate delegate;

  public ChallengeApiController(@Autowired(required = false) ChallengeApiDelegate delegate) {
    this.delegate = Optional.ofNullable(delegate).orElse(new ChallengeApiDelegate() {});
  }

  @Override
  public ChallengeApiDelegate getDelegate() {
    return delegate;
  }
}
