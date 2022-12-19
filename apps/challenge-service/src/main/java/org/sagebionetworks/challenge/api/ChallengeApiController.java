package org.sagebionetworks.challenge.api;

import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
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
