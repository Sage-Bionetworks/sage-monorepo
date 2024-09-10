package org.sagebionetworks.openchallenges.image.service.exception;

public class ImageHeightNotSpecifiedException extends SimpleChallengeGlobalException {

  public ImageHeightNotSpecifiedException(String detail) {
    super(
      ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getType(),
      ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getTitle(),
      ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getStatus(),
      detail
    );
  }
}
