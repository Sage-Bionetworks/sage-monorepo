package org.sagebionetworks.challenge.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.challenge.model.dto.ChallengeAccount;
import org.sagebionetworks.challenge.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @GetMapping("/challenge-account/{account_number}")
  public ResponseEntity<ChallengeAccount> getChallengeAccount(
      @PathVariable("account_number") String accountNumber) {
    log.info("Reading account by ID {}", accountNumber);
    return ResponseEntity.ok(accountService.readChallengeAccount(accountNumber));
  }
}
