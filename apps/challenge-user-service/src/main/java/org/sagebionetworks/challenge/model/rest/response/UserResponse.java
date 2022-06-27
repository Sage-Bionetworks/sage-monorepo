package org.sagebionetworks.challenge.model.rest.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserResponse {
  private String firstName;
  private String lastName;
  private List<AccountResponse> challengeAccounts;
  private String identificationNumber;
  private Integer id;
  private String email;
}
