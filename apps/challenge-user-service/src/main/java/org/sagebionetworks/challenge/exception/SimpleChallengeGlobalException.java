package org.sagebionetworks.challenge.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleChallengeGlobalException extends RuntimeException {

  private String code;
  private String message;

  public SimpleChallengeGlobalException(String message) {
    super(message);
  }
}
