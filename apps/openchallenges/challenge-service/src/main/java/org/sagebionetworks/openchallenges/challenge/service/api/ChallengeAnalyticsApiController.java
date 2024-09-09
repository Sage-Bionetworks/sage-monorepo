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
public class ChallengeAnalyticsApiController implements ChallengeAnalyticsApi {

  private final ChallengeAnalyticsApiDelegate delegate;

  public ChallengeAnalyticsApiController(
    @Autowired(required = false) ChallengeAnalyticsApiDelegate delegate
  ) {
    this.delegate = Optional.ofNullable(delegate).orElse(new ChallengeAnalyticsApiDelegate() {});
  }

  @Override
  public ChallengeAnalyticsApiDelegate getDelegate() {
    return delegate;
  }
}
