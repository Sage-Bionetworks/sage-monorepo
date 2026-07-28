package org.sagebionetworks.model.ad.api.next.exception;

import lombok.experimental.StandardException;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;

@StandardException
public class ModelNotFoundException extends RuntimeException {

  public ModelNotFoundException(ModelOrganismDto modelOrganism, String name) {
    super("Model not found for model organism " + modelOrganism + ": " + name);
  }
}
