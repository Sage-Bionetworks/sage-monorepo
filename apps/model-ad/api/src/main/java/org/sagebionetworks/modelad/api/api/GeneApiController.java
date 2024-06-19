package org.sagebionetworks.modelad.api.api;

import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.mODELADREST.base-path:/v1}")
public class GeneApiController implements GeneApi {

  private final GeneApiDelegate delegate;

  public GeneApiController(@Autowired(required = false) GeneApiDelegate delegate) {
    this.delegate = Optional.ofNullable(delegate).orElse(new GeneApiDelegate() {});
  }

  @Override
  public GeneApiDelegate getDelegate() {
    return delegate;
  }
}
