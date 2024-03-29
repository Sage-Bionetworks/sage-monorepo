package org.sagebionetworks.openchallenges.organization.service.api;

import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.openChallengesOrganizationREST.base-path:/v1}")
public class OrganizationApiController implements OrganizationApi {

  private final OrganizationApiDelegate delegate;

  public OrganizationApiController(@Autowired(required = false) OrganizationApiDelegate delegate) {
    this.delegate = Optional.ofNullable(delegate).orElse(new OrganizationApiDelegate() {});
  }

  @Override
  public OrganizationApiDelegate getDelegate() {
    return delegate;
  }
}
