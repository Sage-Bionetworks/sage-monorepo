package org.sagebionetworks.model.ad.api.next.api;

import jakarta.annotation.Generated;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Generated(
  value = "org.openapitools.codegen.languages.SpringCodegen",
  comments = "Generator version: 7.14.0"
)
@Controller
@RequestMapping("${openapi.modelADAPINext.base-path:/v1}")
public class GeneExpressionApiController implements GeneExpressionApi {

  private final GeneExpressionApiDelegate delegate;

  public GeneExpressionApiController(
    @Autowired(required = false) GeneExpressionApiDelegate delegate
  ) {
    this.delegate = Optional.ofNullable(delegate).orElse(new GeneExpressionApiDelegate() {});
  }

  @Override
  public GeneExpressionApiDelegate getDelegate() {
    return delegate;
  }
}
