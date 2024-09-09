package org.sagebionetworks.openchallenges.organization.service.exception;

public class InvalidOrganizationException extends SimpleChallengeGlobalException {

  public InvalidOrganizationException(String detail) {
    super(
      ErrorConstants.ENTITY_NOT_FOUND.getType(),
      ErrorConstants.ENTITY_NOT_FOUND.getTitle(),
      ErrorConstants.ENTITY_NOT_FOUND.getStatus(),
      detail
    );
  }
}
