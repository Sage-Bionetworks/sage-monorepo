package org.sagebionetworks.challenge.model.rest.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponse {
  private String number;
  private Integer id;
  private String type;
  private String status;
}
