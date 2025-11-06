package org.sagebionetworks.model.ad.api.next.exception;

public class ModelOverviewNotFoundException extends RuntimeException {

  public ModelOverviewNotFoundException(String id) {
    super("Model overview not found with id: " + id);
  }
}
