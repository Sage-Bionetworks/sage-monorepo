package org.sagebionetworks.openchallenges.challenge.service.service;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengeNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.exception.ChallengePlatformNotFoundException;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeCategoryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeCreateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeJsonLdDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeUpdateRequestDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengesPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeCategoryEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeIncentiveEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeInputDataTypeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeSubmissionTypeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.EdamConceptEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.SimpleChallengePlatformEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.ChallengeIncentiveMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.ChallengeJsonLdMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.ChallengeMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeCategoryRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeContributionRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeIncentiveRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeInputDataTypeRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengePlatformRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeStarRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeSubmissionTypeRepository;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.EdamConceptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeService {

  private static final Logger logger = LoggerFactory.getLogger(ChallengeService.class);

  private final ChallengeRepository challengeRepository;
  private final ChallengePlatformRepository challengePlatformRepository;
  private final ChallengeContributionRepository challengeContributionRepository;
  private final ChallengeIncentiveRepository challengeIncentiveRepository;
  private final ChallengeSubmissionTypeRepository challengeSubmissionTypeRepository;
  private final ChallengeCategoryRepository challengeCategoryRepository;
  private final ChallengeStarRepository challengeStarRepository;
  private final ChallengeInputDataTypeRepository challengeInputDataTypeRepository;
  private final EdamConceptRepository edamConceptRepository;
  private final EntityManager entityManager;

  public ChallengeService(
    ChallengeRepository challengeRepository,
    ChallengePlatformRepository challengePlatformRepository,
    ChallengeContributionRepository challengeContributionRepository,
    ChallengeIncentiveRepository challengeIncentiveRepository,
    ChallengeSubmissionTypeRepository challengeSubmissionTypeRepository,
    ChallengeCategoryRepository challengeCategoryRepository,
    ChallengeStarRepository challengeStarRepository,
    ChallengeInputDataTypeRepository challengeInputDataTypeRepository,
    EdamConceptRepository edamConceptRepository,
    EntityManager entityManager
  ) {
    this.challengeRepository = challengeRepository;
    this.challengePlatformRepository = challengePlatformRepository;
    this.challengeContributionRepository = challengeContributionRepository;
    this.challengeIncentiveRepository = challengeIncentiveRepository;
    this.challengeSubmissionTypeRepository = challengeSubmissionTypeRepository;
    this.challengeCategoryRepository = challengeCategoryRepository;
    this.challengeStarRepository = challengeStarRepository;
    this.challengeInputDataTypeRepository = challengeInputDataTypeRepository;
    this.edamConceptRepository = edamConceptRepository;
    this.entityManager = entityManager;
  }

  private ChallengeMapper challengeMapper = new ChallengeMapper();
  private ChallengeJsonLdMapper challengeJsonLdMapper = new ChallengeJsonLdMapper();
  private ChallengeIncentiveMapper challengeIncentiveMapper = new ChallengeIncentiveMapper();

  private static final List<String> SEARCHABLE_FIELDS = Arrays.asList(
    "description",
    "headline",
    "input_data_types.class_id",
    "input_data_types.preferred_label",
    "name",
    "operation.class_id",
    "operation.preferred_label"
  );

  @Transactional(readOnly = true)
  public ChallengesPageDto listChallenges(ChallengeSearchQueryDto query) {
    logger.debug("query {}", query);

    Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());

    List<String> fieldsToSearchBy = SEARCHABLE_FIELDS;
    Page<ChallengeEntity> challengeEntitiesPage = challengeRepository.findAll(
      pageable,
      query,
      fieldsToSearchBy.toArray(new String[0])
    );
    logger.debug("challengeEntitiesPage {}", challengeEntitiesPage);

    List<ChallengeDto> challenges = challengeMapper.convertToDtoList(
      challengeEntitiesPage.getContent()
    );

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

  @Transactional(readOnly = true)
  public ChallengeDto getChallenge(Long challengeId) {
    ChallengeEntity challengeEntity = getChallengeEntity(challengeId);
    return challengeMapper.convertToDto(challengeEntity);
  }

  @Transactional(readOnly = true)
  public ChallengeJsonLdDto getChallengeJsonLd(Long challengeId) {
    ChallengeEntity challengeEntity = getChallengeEntity(challengeId);
    return challengeJsonLdMapper.convertToDto(challengeEntity);
  }

  private ChallengeEntity getChallengeEntity(Long challengeId) throws ChallengeNotFoundException {
    return challengeRepository
      .findById(challengeId)
      .orElseThrow(() ->
        new ChallengeNotFoundException(
          String.format("The challenge with ID %d does not exist.", challengeId)
        )
      );
  }

  private SimpleChallengePlatformEntity getChallengePlatformEntity(Long platformId)
    throws ChallengePlatformNotFoundException {
    return challengePlatformRepository
      .findById(platformId)
      .map(platform ->
        SimpleChallengePlatformEntity.builder()
          .id(platform.getId())
          .slug(platform.getSlug())
          .name(platform.getName())
          .avatarKey(platform.getAvatarKey())
          .websiteUrl(platform.getWebsiteUrl())
          .build()
      )
      .orElseThrow(() ->
        new ChallengePlatformNotFoundException(
          String.format("The challenge platform with ID %d does not exist.", platformId)
        )
      );
  }

  @Transactional
  public void deleteChallenge(Long challengeId) {
    // Verify challenge exists before deletion
    getChallengeEntity(challengeId);

    logger.info("Deleting challenge with ID: {}", challengeId);

    // Delete child entities first (order doesn't matter as they don't depend on each other)
    logger.debug("Deleting challenge contributions for challenge ID: {}", challengeId);
    challengeContributionRepository.deleteByChallengeId(challengeId);

    logger.debug("Deleting challenge incentives for challenge ID: {}", challengeId);
    challengeIncentiveRepository.deleteByChallengeId(challengeId);

    logger.debug("Deleting challenge submission types for challenge ID: {}", challengeId);
    challengeSubmissionTypeRepository.deleteByChallengeId(challengeId);

    logger.debug("Deleting challenge categories for challenge ID: {}", challengeId);
    challengeCategoryRepository.deleteByChallengeId(challengeId);

    logger.debug("Deleting challenge stars for challenge ID: {}", challengeId);
    challengeStarRepository.deleteByChallengeId(challengeId);

    logger.debug("Deleting challenge input data types for challenge ID: {}", challengeId);
    challengeInputDataTypeRepository.deleteAllByChallengeId(challengeId);

    // Delete the challenge entity last
    logger.debug("Deleting challenge entity with ID: {}", challengeId);
    challengeRepository.deleteById(challengeId);

    logger.info("Successfully deleted challenge with ID: {}", challengeId);
  }

  @Transactional
  public ChallengeDto createChallenge(ChallengeCreateRequestDto request) {
    // Create a new challenge entity
    ChallengeEntity newChallenge = new ChallengeEntity();
    newChallenge.setSlug(request.getSlug());
    newChallenge.setName(request.getName());
    newChallenge.setHeadline(request.getHeadline());
    newChallenge.setDescription(request.getDescription());
    newChallenge.setDoi(request.getDoi());
    newChallenge.setStatus(request.getStatus().toString());
    newChallenge.setWebsiteUrl(request.getWebsiteUrl());
    newChallenge.setAvatarUrl(request.getAvatarUrl());

    // Initialize empty collections to prevent NullPointerException in mapper
    newChallenge.setSubmissionTypes(new ArrayList<>());
    newChallenge.setIncentives(new ArrayList<>());
    newChallenge.setCategories(new ArrayList<>());
    newChallenge.setInputDataTypes(new ArrayList<>());
    newChallenge.setStars(new ArrayList<>());
    newChallenge.setContributions(new ArrayList<>());

    // Set platform
    SimpleChallengePlatformEntity platform = getChallengePlatformEntity(request.getPlatformId());
    newChallenge.setPlatform(platform);

    try {
      // Save the challenge entity first to get the ID
      ChallengeEntity savedChallenge = challengeRepository.save(newChallenge);
      challengeRepository.flush();

      logger.info("Successfully created challenge with ID: {}", savedChallenge.getId());

      // Return the created challenge as DTO
      return challengeMapper.convertToDto(savedChallenge);
    } catch (DataIntegrityViolationException e) {
      // Handle potential unique constraint violations (e.g., slug uniqueness)
      throw new RuntimeException(
        "Challenge creation failed due to data constraint violation. " +
        "This may be due to a duplicate slug or other unique constraint violation.",
        e
      );
    }
  }

  @Transactional
  public ChallengeDto updateChallenge(Long challengeId, ChallengeUpdateRequestDto request) {
    // Find the existing challenge
    ChallengeEntity existingChallenge = getChallengeEntity(challengeId);

    // Update the challenge components
    updateBasicFields(existingChallenge, request);
    updatePlatform(existingChallenge, request.getPlatformId());
    updateOperation(existingChallenge, request.getOperation());
    updateIncentives(existingChallenge, request.getIncentives());
    updateSubmissionTypes(existingChallenge, request.getSubmissionTypes());
    updateCategories(existingChallenge, request.getCategories());
    updateInputDataTypes(existingChallenge, request.getInputDataTypes());

    // Save the updated entity
    challengeRepository.save(existingChallenge);
    challengeRepository.flush();

    // Refresh the entity to get the updated state
    ChallengeEntity refreshedEntity = refreshChallengeEntity(challengeId);

    logger.info("Successfully updated challenge with ID: {}", challengeId);

    // Return the updated challenge as DTO
    return challengeMapper.convertToDto(refreshedEntity);
  }

  private void updateBasicFields(ChallengeEntity challenge, ChallengeUpdateRequestDto request) {
    challenge.setSlug(request.getSlug());
    challenge.setName(request.getName());
    challenge.setHeadline(request.getHeadline());
    challenge.setDescription(request.getDescription());
    challenge.setDoi(request.getDoi());
    challenge.setStatus(request.getStatus().toString());
    challenge.setWebsiteUrl(request.getWebsiteUrl());
    challenge.setAvatarUrl(request.getAvatarUrl());
    challenge.setStartDate(request.getStartDate());
    challenge.setEndDate(request.getEndDate());
  }

  private void updatePlatform(ChallengeEntity challenge, Long platformId) {
    if (platformId != null) {
      SimpleChallengePlatformEntity platform = getChallengePlatformEntity(platformId);
      challenge.setPlatform(platform);
    } else {
      // If platformId is null, remove the platform association
      challenge.setPlatform(null);
    }
  }

  private void updateOperation(ChallengeEntity challenge, Long operationId) {
    if (operationId != null) {
      // Check if the operation is already set to the same value
      if (
        challenge.getOperation() != null && challenge.getOperation().getId().equals(operationId)
      ) {
        // Operation is already set to the requested value, no changes needed
        return;
      }

      // Find the EDAM concept for the operation
      EdamConceptEntity operation = edamConceptRepository
        .findById(operationId)
        .orElseThrow(() ->
          new RuntimeException(String.format("EDAM concept with ID %d not found", operationId))
        );

      challenge.setOperation(operation);
    } else {
      // If operationId is null, remove the operation association
      challenge.setOperation(null);
    }
  }

  private void updateIncentives(ChallengeEntity challenge, List<ChallengeIncentiveDto> incentives) {
    List<ChallengeIncentiveEntity> existingIncentives = challenge.getIncentives();
    List<String> newIncentiveNames = incentives != null
      ? incentives.stream().map(dto -> dto.getValue()).toList()
      : new ArrayList<>();

    // Find incentives to delete (present in DB but not in request)
    for (ChallengeIncentiveEntity existing : new ArrayList<>(existingIncentives)) {
      boolean stillPresent = newIncentiveNames
        .stream()
        .anyMatch(name -> name.equals(existing.getName()));
      if (!stillPresent) {
        challengeIncentiveRepository.delete(existing);
        existingIncentives.remove(existing);
      }
    }

    // Find incentives to add (present in request but not in DB)
    for (String newIncentiveName : newIncentiveNames) {
      boolean alreadyExists = existingIncentives
        .stream()
        .anyMatch(existing -> existing.getName().equals(newIncentiveName));
      if (!alreadyExists) {
        ChallengeIncentiveEntity newIncentive = ChallengeIncentiveEntity.builder()
          .name(newIncentiveName)
          .challenge(challenge)
          .createdAt(java.time.OffsetDateTime.now())
          .build();
        challengeIncentiveRepository.save(newIncentive);
        existingIncentives.add(newIncentive);
      }
    }
  }

  private void updateSubmissionTypes(
    ChallengeEntity challenge,
    List<ChallengeSubmissionTypeDto> submissionTypes
  ) {
    List<ChallengeSubmissionTypeEntity> existingSubmissionTypes = challenge.getSubmissionTypes();
    List<String> newSubmissionTypeNames = submissionTypes != null
      ? submissionTypes.stream().map(dto -> dto.getValue()).toList()
      : new ArrayList<>();

    // Find submission types to delete (present in DB but not in request)
    for (ChallengeSubmissionTypeEntity existing : new ArrayList<>(existingSubmissionTypes)) {
      boolean stillPresent = newSubmissionTypeNames
        .stream()
        .anyMatch(name -> name.equals(existing.getName()));
      if (!stillPresent) {
        challengeSubmissionTypeRepository.delete(existing);
        existingSubmissionTypes.remove(existing);
      }
    }

    // Find submission types to add (present in request but not in DB)
    for (String newTypeName : newSubmissionTypeNames) {
      boolean alreadyExists = existingSubmissionTypes
        .stream()
        .anyMatch(existing -> existing.getName().equals(newTypeName));
      if (!alreadyExists) {
        ChallengeSubmissionTypeEntity newSubmissionType = ChallengeSubmissionTypeEntity.builder()
          .name(newTypeName)
          .challenge(challenge)
          .createdAt(java.time.OffsetDateTime.now())
          .build();
        challengeSubmissionTypeRepository.save(newSubmissionType);
        existingSubmissionTypes.add(newSubmissionType);
      }
    }
  }

  private void updateCategories(ChallengeEntity challenge, List<ChallengeCategoryDto> categories) {
    List<ChallengeCategoryEntity> existingCategories = challenge.getCategories();
    List<String> newCategoryNames = categories != null
      ? categories.stream().map(dto -> dto.getValue()).toList()
      : new ArrayList<>();

    // Find categories to delete (present in DB but not in request)
    for (ChallengeCategoryEntity existing : new ArrayList<>(existingCategories)) {
      boolean stillPresent = newCategoryNames
        .stream()
        .anyMatch(name -> name.equals(existing.getName()));
      if (!stillPresent) {
        challengeCategoryRepository.delete(existing);
        existingCategories.remove(existing);
      }
    }

    // Find categories to add (present in request but not in DB)
    for (String newCategoryName : newCategoryNames) {
      boolean alreadyExists = existingCategories
        .stream()
        .anyMatch(existing -> existing.getName().equals(newCategoryName));
      if (!alreadyExists) {
        ChallengeCategoryEntity newCategory = ChallengeCategoryEntity.builder()
          .name(newCategoryName)
          .challenge(challenge)
          .build();
        challengeCategoryRepository.save(newCategory);
        existingCategories.add(newCategory);
      }
    }
  }

  private void updateInputDataTypes(ChallengeEntity challenge, List<Long> inputDataTypeIds) {
    List<Long> newInputDataTypeIds = inputDataTypeIds != null
      ? inputDataTypeIds
      : new ArrayList<>();

    // Clear existing input data types for this challenge
    challengeInputDataTypeRepository.deleteAllByChallengeId(challenge.getId());

    // Add new input data types
    for (Long newInputDataTypeId : newInputDataTypeIds) {
      EdamConceptEntity edamConcept = edamConceptRepository
        .findById(newInputDataTypeId)
        .orElseThrow(() ->
          new RuntimeException(
            String.format("EDAM concept with ID %d not found", newInputDataTypeId)
          )
        );

      ChallengeInputDataTypeEntity newInputDataType = ChallengeInputDataTypeEntity.builder()
        .challenge(challenge)
        .edamConcept(edamConcept)
        .createdAt(java.time.OffsetDateTime.now())
        .build();
      challengeInputDataTypeRepository.save(newInputDataType);
    }
  }

  private ChallengeEntity refreshChallengeEntity(Long challengeId) {
    // Clear the session cache to force fresh fetch of incentives
    entityManager.clear();
    // Refresh the entity to get the updated incentives
    return getChallengeEntity(challengeId);
  }
}
