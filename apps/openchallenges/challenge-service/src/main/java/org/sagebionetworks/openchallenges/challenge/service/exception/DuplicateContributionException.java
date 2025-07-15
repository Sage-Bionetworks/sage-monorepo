package org.sagebionetworks.openchallenges.challenge.service.exception;

/**
 * Exception thrown when attempting to create a duplicate challenge contribution.
 * This occurs when an organization already has a specific role for a challenge.
 */
public class DuplicateContributionException extends RuntimeException {

  public DuplicateContributionException(String message) {
    super(message);
  }

  public DuplicateContributionException(String message, Throwable cause) {
    super(message, cause);
  }
}
