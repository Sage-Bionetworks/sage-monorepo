package org.sagebionetworks.bixarena.api.exception;

import java.util.UUID;

public class DuplicateVoteException extends RuntimeException {

  public DuplicateVoteException(UUID battleId) {
    super(String.format("A vote already exists for battle ID: %s", battleId));
  }
}
