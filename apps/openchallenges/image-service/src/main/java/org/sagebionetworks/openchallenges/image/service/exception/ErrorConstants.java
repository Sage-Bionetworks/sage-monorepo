package org.sagebionetworks.openchallenges.image.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorConstants {
  IMAGE_HEIGHT_NOT_SPECIFIED(
    "IMAGE-SERVICE-1000",
    "Image height not found",
    HttpStatus.BAD_REQUEST
  ),
  BAD_REQUEST("IMAGE-SERVICE-1001", "Bad request", HttpStatus.BAD_REQUEST);

  private String type;
  private String title;
  private HttpStatus status;
}
