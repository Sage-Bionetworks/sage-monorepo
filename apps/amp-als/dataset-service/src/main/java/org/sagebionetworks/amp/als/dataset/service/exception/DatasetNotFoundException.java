package org.sagebionetworks.amp.als.dataset.service.exception;

public class DatasetNotFoundException extends SimpleDatasetGlobalException {

  public DatasetNotFoundException(String detail) {
    super(
      ErrorConstants.ENTITY_NOT_FOUND.getType(),
      ErrorConstants.ENTITY_NOT_FOUND.getTitle(),
      ErrorConstants.ENTITY_NOT_FOUND.getStatus(),
      detail
    );
  }
}
