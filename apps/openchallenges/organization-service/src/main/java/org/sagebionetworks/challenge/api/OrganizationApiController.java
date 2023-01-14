package org.sagebionetworks.challenge.api;

import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
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
