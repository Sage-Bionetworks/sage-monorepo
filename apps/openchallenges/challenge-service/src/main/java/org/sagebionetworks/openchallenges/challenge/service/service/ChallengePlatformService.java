package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengePlatformNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengePlatformEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.ChallengePlatformMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengePlatformRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ChallengePlatformService {

  private final ChallengePlatformRepository challengePlatformRepository;

  private ChallengePlatformMapper challengePlatformMapper = new ChallengePlatformMapper();

  private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("name");

  public ChallengePlatformService(ChallengePlatformRepository challengePlatformRepository) {
    this.challengePlatformRepository = challengePlatformRepository;
  }

  @Transactional(readOnly = true)
  public ChallengePlatformDto getChallengePlatform(Long challengePlatformId) {
    ChallengePlatformEntity entity = challengePlatformRepository
      .findById(challengePlatformId)
      .orElseThrow(() ->
        new ChallengePlatformNotFoundException(
          String.format("The challenge platform with ID %d does not exist.", challengePlatformId)
        )
      );

    return challengePlatformMapper.convertToDto(entity);
  }

  @Transactional(readOnly = true)
  public ChallengePlatformsPageDto listChallengePlatforms(ChallengePlatformSearchQueryDto query) {
    log.info("query {}", query);

    Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());

    List<String> fieldsToSearchBy = SEARCHABLE_FIELDS;
    Page<ChallengePlatformEntity> entitiesPage = challengePlatformRepository.findAll(
      pageable,
      query,
      fieldsToSearchBy.toArray(new String[0])
    );
    log.info("entitiesPage {}", entitiesPage);

    List<ChallengePlatformDto> challengePlatforms = challengePlatformMapper.convertToDtoList(
      entitiesPage.getContent()
    );

    return ChallengePlatformsPageDto.builder()
      .challengePlatforms(challengePlatforms)
      .number(entitiesPage.getNumber())
      .size(entitiesPage.getSize())
      .totalElements(entitiesPage.getTotalElements())
      .totalPages(entitiesPage.getTotalPages())
      .hasNext(entitiesPage.hasNext())
      .hasPrevious(entitiesPage.hasPrevious())
      .build();
  }
}
