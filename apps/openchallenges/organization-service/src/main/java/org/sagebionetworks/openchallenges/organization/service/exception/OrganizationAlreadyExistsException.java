package org.sagebionetworks.openchallenges.organization.service.exception;

public class OrganizationAlreadyExistsException extends SimpleChallengeGlobalException {

  public OrganizationAlreadyExistsException(String detail) {
    super(
      ErrorConstants.ORGANIZATION_ALREADY_EXISTS.getType(),
      ErrorConstants.ORGANIZATION_ALREADY_EXISTS.getTitle(),
      ErrorConstants.ORGANIZATION_ALREADY_EXISTS.getStatus(),
      detail
    );
  }
}
