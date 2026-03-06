package org.sagebionetworks.bixarena.api.exception;

import java.util.UUID;

public class DuplicateBattleValidationException extends RuntimeException {

  public DuplicateBattleValidationException(UUID battleId) {
    super(String.format(
      "A validation with this method already exists for battle ID: %s",
      battleId
    ));
  }
}
