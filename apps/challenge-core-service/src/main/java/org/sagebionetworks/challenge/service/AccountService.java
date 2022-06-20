package org.sagebionetworks.challenge.service;

import org.sagebionetworks.challenge.model.dto.ChallengeAccount;
import org.sagebionetworks.challenge.model.entity.ChallengeAccountEntity;
import org.sagebionetworks.challenge.model.mapper.ChallengeAccountMapper;
import org.sagebionetworks.challenge.repository.ChallengeAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private ChallengeAccountMapper bankAccountMapper = new ChallengeAccountMapper();

    private final ChallengeAccountRepository bankAccountRepository;

    public ChallengeAccount readChallengeAccount(String accountNumber) {
        ChallengeAccountEntity entity = bankAccountRepository.findByNumber(accountNumber).get();
        return bankAccountMapper.convertToDto(entity);
    }
}