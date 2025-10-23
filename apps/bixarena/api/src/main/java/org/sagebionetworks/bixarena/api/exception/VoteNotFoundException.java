package org.sagebionetworks.bixarena.api.exception;

import java.util.UUID;

public class VoteNotFoundException extends RuntimeException {

  public VoteNotFoundException(UUID voteId) {
    super(String.format("Vote not found with ID: %s", voteId));
  }
}
