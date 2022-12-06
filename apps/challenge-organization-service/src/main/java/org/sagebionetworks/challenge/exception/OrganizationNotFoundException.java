package org.sagebionetworks.challenge.exception;

public class OrganizationNotFoundException extends SimpleChallengeGlobalException {
  public OrganizationNotFoundException(String detail) {
    super(
        ErrorConstants.ENTITY_NOT_FOUND.getType(),
        ErrorConstants.ENTITY_NOT_FOUND.getTitle(),
        ErrorConstants.ENTITY_NOT_FOUND.getStatus(),
        detail);
  }
}
