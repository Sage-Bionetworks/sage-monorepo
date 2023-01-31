package org.sagebionetworks.openchallenges.challenge.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleChallengeGlobalException extends RuntimeException {

  private String type;
  private String title;
  private HttpStatus status;
  private String detail;

  public SimpleChallengeGlobalException(String details) {
    super(details);
  }
}
