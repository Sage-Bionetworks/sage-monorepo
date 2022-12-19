package org.sagebionetworks.challenge.service;

import java.util.List;
import org.sagebionetworks.challenge.model.dto.ChallengeDto;
import org.sagebionetworks.challenge.model.dto.ChallengesPageDto;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.sagebionetworks.challenge.model.mapper.ChallengeMapper;
import org.sagebionetworks.challenge.model.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeService {

  @Autowired private ChallengeRepository challengeRepository;

  private ChallengeMapper challengeMapper = new ChallengeMapper();

  @Transactional(readOnly = true)
  public ChallengesPageDto listChallenges(Integer pageNumber, Integer pageSize) {
    Page<ChallengeEntity> challengeEntitiesPage =
        challengeRepository.findAll(PageRequest.of(pageNumber, pageSize));
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
