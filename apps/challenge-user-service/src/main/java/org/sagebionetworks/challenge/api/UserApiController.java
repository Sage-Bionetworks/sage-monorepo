package org.sagebionetworks.challenge.api;

import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
public class UserApiController implements UserApi {

  private final UserApiDelegate delegate;

  public UserApiController(@Autowired(required = false) UserApiDelegate delegate) {
    this.delegate = Optional.ofNullable(delegate).orElse(new UserApiDelegate() {});
  }

  @Override
  public UserApiDelegate getDelegate() {
    return delegate;
  }
}
