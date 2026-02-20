package org.sagebionetworks.agora.api.next.exception;

import lombok.experimental.StandardException;

@StandardException
public class NominatedDrugNotFoundException extends RuntimeException {

  public NominatedDrugNotFoundException() {
    super("No nominated drugs found");
  }
}
