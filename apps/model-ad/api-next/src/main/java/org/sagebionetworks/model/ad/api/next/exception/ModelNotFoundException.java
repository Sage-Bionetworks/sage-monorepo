package org.sagebionetworks.model.ad.api.next.exception;

import lombok.experimental.StandardException;
import org.sagebionetworks.model.ad.api.next.model.dto.OrganismDto;

@StandardException
public class ModelNotFoundException extends RuntimeException {

  public ModelNotFoundException(OrganismDto organism, String name) {
    super("Model not found for organism " + organism + ": " + name);
  }
}
