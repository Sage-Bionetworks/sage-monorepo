package org.sagebionetworks.challenge.service;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.challenge.model.domain.ChallengeDomain;
import org.sagebionetworks.challenge.model.dto.ChallengeDto;
import org.sagebionetworks.challenge.model.dto.ChallengeFilterDto;
import org.sagebionetworks.challenge.model.dto.ChallengesPageDto;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.sagebionetworks.challenge.model.mapper.ChallengeMapper;
import org.sagebionetworks.challenge.model.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ChallengeService {

  @Autowired private ChallengeRepository challengeRepository;

  @Autowired private ProducerService producerService;

  private ChallengeMapper challengeMapper = new ChallengeMapper();

  private static final List<String> SEARCHABLE_FIELDS =
      Arrays.asList("name", "headline", "description");

  @Transactional(readOnly = true)
  public ChallengesPageDto listChallenges(
      Integer pageNumber, Integer pageSize, ChallengeFilterDto challengeFilter) {

    log.info("challengeFilter {}", challengeFilter);

    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    // ChallengeFilterBuilder builder = ChallengeFilter.builder();
    // if (status != null) {
    //   builder.status(status.stream().map(o -> o.toString()).toList());
    // }
    // if (platforms != null) {
    //   builder.platforms(platforms);
    // }
    // if (difficulties != null) {
    //   builder.difficulties(difficulties.stream().map(o -> o.toString()).toList());
    // }
    // if (submissionTypes != null) {
    //   builder.submissionTypes(submissionTypes.stream().map(o -> o.toString()).toList());
    // }
    // if (incentives != null) {
    //   builder.incentives(incentives.stream().map(o -> o.toString()).toList());
    // }
    // builder.searchTerms(searchTerms);
    // ChallengeFilter filter = builder.build();

    ChallengeDomain challengeDomain = new ChallengeDomain("plop");
    log.info("challenge sent: {}", challengeDomain);
    producerService.sendMessage(challengeDomain);

    List<String> fieldsToSearchBy = SEARCHABLE_FIELDS;
    Page<ChallengeEntity> challengeEntitiesPage =
        challengeRepository.findAll(
            pageable, challengeFilter, fieldsToSearchBy.toArray(new String[0]));
    log.info("challengeEntitiesPage {}", challengeEntitiesPage);

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
