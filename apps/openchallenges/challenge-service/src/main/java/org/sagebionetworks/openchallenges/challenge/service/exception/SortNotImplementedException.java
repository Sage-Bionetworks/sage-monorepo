package org.sagebionetworks.openchallenges.challenge.service.exception;

public class SortNotImplementedException extends SimpleChallengeGlobalException {

  public SortNotImplementedException(String detail) {
    super(
      ErrorConstants.SORT_NOT_IMPLEMENTED.getType(),
      ErrorConstants.SORT_NOT_IMPLEMENTED.getTitle(),
      ErrorConstants.SORT_NOT_IMPLEMENTED.getStatus(),
      detail
    );
  }
}
