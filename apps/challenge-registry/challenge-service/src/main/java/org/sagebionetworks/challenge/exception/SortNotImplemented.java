package org.sagebionetworks.challenge.exception;

public class SortNotImplemented extends SimpleChallengeGlobalException {
  public SortNotImplemented(String detail) {
    super(
        ErrorConstants.SORT_NOT_IMPLEMENTED.getType(),
        ErrorConstants.SORT_NOT_IMPLEMENTED.getTitle(),
        ErrorConstants.SORT_NOT_IMPLEMENTED.getStatus(),
        detail);
  }
}
