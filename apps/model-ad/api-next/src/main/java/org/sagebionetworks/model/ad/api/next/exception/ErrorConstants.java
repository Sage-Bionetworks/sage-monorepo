package org.sagebionetworks.model.ad.api.next.exception;

import org.springframework.http.HttpStatus;

public enum ErrorConstants {
  ENTITY_NOT_FOUND("about:blank", "Entity not found", HttpStatus.NOT_FOUND),
  INVALID_CATEGORY("about:blank", "Invalid Category", HttpStatus.BAD_REQUEST),
  INVALID_OBJECT_ID("about:blank", "Invalid Request Parameter", HttpStatus.BAD_REQUEST),
  INVALID_FILTER("about:blank", "Invalid Filter Configuration", HttpStatus.BAD_REQUEST),
  DATA_INTEGRITY_ERROR("about:blank", "Data Integrity Error", HttpStatus.INTERNAL_SERVER_ERROR),
  BAD_REQUEST("about:blank", "Bad Request", HttpStatus.BAD_REQUEST),
  INTERNAL_SERVER_ERROR("about:blank", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);

  public static final String CATEGORY_REQUIREMENT_MESSAGE =
    "Query parameter category must repeat twice (e.g. ?category=CONSENSUS NETWORK MODULES" +
    "&category=subcategory) and each value must be a string";

  public static final String SUPPORTED_CATEGORY = "CONSENSUS NETWORK MODULES";

  private final String type;
  private final String title;
  private final HttpStatus status;

  ErrorConstants(String type, String title, HttpStatus status) {
    this.type = type;
    this.title = title;
    this.status = status;
  }

  public String getType() {
    return type;
  }

  public String getTitle() {
    return title;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
