package org.sagebionetworks.amp.als.dataset.service.exception;

public class BadRequestException extends SimpleDatasetGlobalException {

  public BadRequestException(String detail) {
    super(
      ErrorConstants.BAD_REQUEST.getType(),
      ErrorConstants.BAD_REQUEST.getTitle(),
      ErrorConstants.BAD_REQUEST.getStatus(),
      detail
    );
  }
}
