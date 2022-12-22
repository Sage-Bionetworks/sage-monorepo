package org.sagebionetworks.challenge.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.challenge.exception.ChallengePlatformNotFoundException;
import org.sagebionetworks.challenge.model.dto.ChallengePlatformDto;
import org.sagebionetworks.challenge.model.dto.ChallengePlatformsPageDto;
import org.sagebionetworks.challenge.model.entity.ChallengePlatformEntity;
import org.sagebionetworks.challenge.model.mapper.ChallengePlatformMapper;
import org.sagebionetworks.challenge.model.repository.ChallengePlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ChallengePlatformService {

  @Autowired private ChallengePlatformRepository challengePlatformRepository;

  private ChallengePlatformMapper challengePlatformMapper = new ChallengePlatformMapper();

  @Transactional(readOnly = true)
  public ChallengePlatformDto getChallengePlatform(Long challengePlatformId) {
    ChallengePlatformEntity entity =
        challengePlatformRepository
            .findById(challengePlatformId)
            .orElseThrow(
                () ->
                    new ChallengePlatformNotFoundException(
                        String.format(
                            "The challenge platform with ID %d does not exist.",
                            challengePlatformId)));

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
