package org.sagebionetworks.challenge.model.dto;

import org.sagebionetworks.challenge.model.AccountStatus;
import org.sagebionetworks.challenge.model.AccountType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChallengeAccount {

    private Long id;
    private String number;
    private AccountType type;
    private AccountStatus status;
    private BigDecimal availableBalance;
    private BigDecimal actualBalance;
    private User user;

}