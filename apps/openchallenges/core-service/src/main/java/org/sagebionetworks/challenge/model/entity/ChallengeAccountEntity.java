package org.sagebionetworks.challenge.model.entity;

import org.sagebionetworks.challenge.model.AccountStatus;
import org.sagebionetworks.challenge.model.AccountType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "challenge_core_account")
public class ChallengeAccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String number;

  @Enumerated(EnumType.STRING)
  private AccountType type;

  @Enumerated(EnumType.STRING)
  private AccountStatus status;

  private BigDecimal availableBalance;

  private BigDecimal actualBalance;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;
}
