package org.sagebionetworks.challenge.api;

import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
public class UserControllerApiController implements UserControllerApi {

  private final UserControllerApiDelegate delegate;

  public UserControllerApiController(
      @Autowired(required = false) UserControllerApiDelegate delegate) {
    this.delegate = Optional.ofNullable(delegate).orElse(new UserControllerApiDelegate() {});
  }

  @Override
  public UserControllerApiDelegate getDelegate() {
    return delegate;
  }
}
