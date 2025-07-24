package org.sagebionetworks.openchallenges.challenge.service.exception;

public class OrganizationNotFoundException extends SimpleChallengeGlobalException {

  public OrganizationNotFoundException(String detail) {
    super(detail); // This calls the constructor that sets the message
    // Also set the structured exception fields
    this.setType(ErrorConstants.ENTITY_NOT_FOUND.getType());
    this.setTitle(ErrorConstants.ENTITY_NOT_FOUND.getTitle());
    this.setStatus(ErrorConstants.ENTITY_NOT_FOUND.getStatus());
    this.setDetail(detail);
  }
}
