package org.sagebionetworks.amp.als.dataset.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleDatasetGlobalException extends RuntimeException {

  private String type;
  private String title;
  private HttpStatus status;
  private String detail;

  public SimpleDatasetGlobalException(String details) {
    super(details);
  }
}
