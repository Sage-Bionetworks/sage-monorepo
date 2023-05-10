package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengePlatformNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDto;
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

  private ChallengePlatformRepository challengePlatformRepository;

  private ChallengePlatformMapper challengePlatformMapper = new ChallengePlatformMapper();

  public ChallengePlatformService(ChallengePlatformRepository challengePlatformRepository) {
    this.challengePlatformRepository = challengePlatformRepository;
  }

  @Transactional(readOnly = true)
  public ChallengePlatformDto getChallengePlatform(String challengePlatformName) {
    ChallengePlatformEntity entity =
        challengePlatformRepository
            .findByName(challengePlatformName)
            .orElseThrow(
                () ->
                    new ChallengePlatformNotFoundException(
                        String.format(
                            "The challenge platform with name %s does not exist.",
                            challengePlatformName)));

    return challengePlatformMapper.convertToDto(entity);
  }

  @Transactional(readOnly = true)
  public ChallengePlatformsPageDto listChallengePlatforms(Integer pageNumber, Integer pageSize) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    Page<ChallengePlatformEntity> entitiesPage = challengePlatformRepository.findAll(pageable);

    List<ChallengePlatformDto> challengePlatforms =
        challengePlatformMapper.convertToDtoList(entitiesPage.getContent());

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
