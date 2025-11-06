package org.sagebionetworks.model.ad.api.next.exception;

public class DiseaseCorrelationNotFoundException extends RuntimeException {

  public DiseaseCorrelationNotFoundException(String id) {
    super("Disease correlation not found with id: " + id);
  }

  public DiseaseCorrelationNotFoundException(String cluster, String id) {
    super("Disease correlation not found with cluster: " + cluster + ", id: " + id);
  }
}
