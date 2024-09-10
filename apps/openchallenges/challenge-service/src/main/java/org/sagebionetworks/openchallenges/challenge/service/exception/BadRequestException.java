package org.sagebionetworks.openchallenges.challenge.service.exception;

public class BadRequestException extends SimpleChallengeGlobalException {

  public BadRequestException(String detail) {
    super(
      ErrorConstants.BAD_REQUEST.getType(),
      ErrorConstants.BAD_REQUEST.getTitle(),
      ErrorConstants.BAD_REQUEST.getStatus(),
      detail
    );
  }
}
