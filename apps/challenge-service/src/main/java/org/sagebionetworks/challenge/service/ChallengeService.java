package org.sagebionetworks.challenge.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.challenge.model.dto.ChallengeDto;
import org.sagebionetworks.challenge.model.dto.ChallengeStatusDto;
import org.sagebionetworks.challenge.model.dto.ChallengesPageDto;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.sagebionetworks.challenge.model.entity.QChallengeEntity;
import org.sagebionetworks.challenge.model.mapper.ChallengeMapper;
import org.sagebionetworks.challenge.model.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ChallengeService {

  @Autowired private ChallengeRepository challengeRepository;

  private ChallengeMapper challengeMapper = new ChallengeMapper();

  @Transactional(readOnly = true)
  public ChallengesPageDto listChallenges(
      Integer pageNumber, Integer pageSize, List<ChallengeStatusDto> status) {

    log.info("status {}", status);
    QChallengeEntity qChallenge = QChallengeEntity.challengeEntity;
    BooleanExpression q = qChallenge.status.in(status.stream().map(s -> s.toString()).toList());

    Page<ChallengeEntity> challengeEntitiesPage =
        challengeRepository.findAll(q, PageRequest.of(pageNumber, pageSize));

    List<ChallengeDto> challenges =
        challengeMapper.convertToDtoList(challengeEntitiesPage.getContent());

    return ChallengesPageDto.builder()
        .challenges(challenges)
        .number(challengeEntitiesPage.getNumber())
        .size(challengeEntitiesPage.getSize())
        .totalElements(challengeEntitiesPage.getTotalElements())
        .totalPages(challengeEntitiesPage.getTotalPages())
        .hasNext(challengeEntitiesPage.hasNext())
        .hasPrevious(challengeEntitiesPage.hasPrevious())
        .build();
  }

  // @Transactional(readOnly = true)
  // public ChallengeDto getChallenge(String challengeLogin) {
  //   ChallengeEntity challengeEntity =
  //       challengeRepository
  //           .findByLogin(challengeLogin)
  //           .orElseThrow(
  //               () ->
  //                   new ChallengeNotFoundException(
  //                       String.format(
  //                           "The challenge with ID %s does not exist.", challengeLogin)));
  //   ChallengeDto challenge = challengeMapper.convertToDto(challengeEntity);
  //   return challenge;
  // }
}
