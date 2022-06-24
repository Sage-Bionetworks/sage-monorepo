package org.sagebionetworks.challenge.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String identificationNumber;
    private List<ChallengeAccount> challengeAccounts;

}