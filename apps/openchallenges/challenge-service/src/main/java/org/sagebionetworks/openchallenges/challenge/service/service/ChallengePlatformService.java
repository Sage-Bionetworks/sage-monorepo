package org.sagebionetworks.openchallenges.challenge.service.service;

import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengePlatformNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.exception.DuplicateContributionException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengePlatformsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengePlatformEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.ChallengePlatformMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengePlatformRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengePlatformService {

  private static final Logger logger = LoggerFactory.getLogger(ChallengePlatformService.class);

  private final ChallengePlatformRepository challengePlatformRepository;

  private ChallengePlatformMapper challengePlatformMapper = new ChallengePlatformMapper();

  private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("name");

  public ChallengePlatformService(ChallengePlatformRepository challengePlatformRepository) {
    this.challengePlatformRepository = challengePlatformRepository;
  }

  @Transactional(readOnly = false)
  public void deleteChallengePlatform(Long id) {}

  @Transactional(readOnly = true)
  public ChallengePlatformDto getChallengePlatform(Long id) {
    ChallengePlatformEntity entity = challengePlatformRepository
      .findById(id)
      .orElseThrow(() ->
        new ChallengePlatformNotFoundException(
          String.format("The challenge platform with ID %d does not exist.", id)
        )
      );

    return challengePlatformMapper.convertToDto(entity);
  }

  @Transactional(readOnly = true)
  public ChallengePlatformsPageDto listChallengePlatforms(ChallengePlatformSearchQueryDto query) {
    logger.info("query {}", query);

    Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());

    List<String> fieldsToSearchBy = SEARCHABLE_FIELDS;
    Page<ChallengePlatformEntity> entitiesPage = challengePlatformRepository.findAll(
      pageable,
      query,
      fieldsToSearchBy.toArray(new String[0])
    );
    logger.info("entitiesPage {}", entitiesPage);

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

  @Transactional
  public ChallengePlatformDto createChallengePlatform(ChallengePlatformCreateRequestDto request) {
    // Create the challenge platform entity
    ChallengePlatformEntity entity = ChallengePlatformEntity.builder()
      .slug(request.getSlug())
      .name(request.getName())
      .avatarKey(request.getAvatarKey())
      .websiteUrl(request.getWebsiteUrl())
      .build();

    try {
      // Save the entity
      ChallengePlatformEntity savedEntity = challengePlatformRepository.save(entity);

      // Return the full challenge platform DTO
      return challengePlatformMapper.convertToDto(savedEntity);
    } catch (DataIntegrityViolationException e) {
      String message = e.getMessage();
      if (message != null) {
        if (message.contains("challenge_platform_name_key")) {
          throw new DuplicateContributionException(
            String.format("A challenge platform with name '%s' already exists.", request.getName()),
            e
          );
        } else if (message.contains("challenge_platform_slug_key")) {
          throw new DuplicateContributionException(
            String.format("A challenge platform with slug '%s' already exists.", request.getSlug()),
            e
          );
        }
      }
      // Re-throw the original exception if it's not the constraint we're looking for
      throw e;
    }
  }
}
