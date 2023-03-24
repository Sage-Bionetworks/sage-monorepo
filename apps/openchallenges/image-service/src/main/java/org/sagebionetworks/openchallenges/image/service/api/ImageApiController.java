package org.sagebionetworks.openchallenges.image.service.api;

import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.openChallengesImageREST.base-path:/v1}")
public class ImageApiController implements ImageApi {

  private final ImageApiDelegate delegate;

  public ImageApiController(@Autowired(required = false) ImageApiDelegate delegate) {
    this.delegate = Optional.ofNullable(delegate).orElse(new ImageApiDelegate() {});
  }

  @Override
  public ImageApiDelegate getDelegate() {
    return delegate;
  }
}
