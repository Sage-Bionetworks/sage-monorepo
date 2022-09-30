package org.sagebionetworks.challenge.model.rest.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
  private String firstName;
  private String lastName;
  private List<AccountResponse> challengeAccounts;
  private Integer id;
  private String email;
}
