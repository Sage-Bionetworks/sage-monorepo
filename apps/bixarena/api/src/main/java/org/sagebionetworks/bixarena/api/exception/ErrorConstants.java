package org.sagebionetworks.bixarena.api.exception;

import org.springframework.http.HttpStatus;

public enum ErrorConstants {
  ENTITY_NOT_FOUND("about:blank", "Entity not found", HttpStatus.NOT_FOUND),
  BAD_REQUEST("about:blank", "Bad request", HttpStatus.BAD_REQUEST),
  INTERNAL_SERVER_ERROR("about:blank", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

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
