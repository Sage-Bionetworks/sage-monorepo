package org.sagebionetworks.openchallenges.organization.service.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.organization.service.exception.OrganizationAlreadyExistsException;
import org.sagebionetworks.openchallenges.organization.service.exception.OrganizationNotFoundException;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationCreateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationUpdateRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationsPageDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.sagebionetworks.openchallenges.organization.service.model.mapper.OrganizationMapper;
import org.sagebionetworks.openchallenges.organization.service.model.repository.ChallengeParticipationRepository;
import org.sagebionetworks.openchallenges.organization.service.model.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {

  private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

  private final OrganizationRepository organizationRepository;
  private final ChallengeParticipationRepository challengeParticipationRepository;

  @PersistenceContext
  private EntityManager entityManager;

  public OrganizationService(
    OrganizationRepository organizationRepository,
    ChallengeParticipationRepository challengeParticipationRepository
  ) {
    this.organizationRepository = organizationRepository;
    this.challengeParticipationRepository = challengeParticipationRepository;
  }

  private OrganizationMapper organizationMapper = new OrganizationMapper();

  private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("name", "description");

  @Transactional(readOnly = false)
  public void deleteOrganization(String identifier) {
    String orgLogin = String.valueOf(identifier);
    Long orgId = null;
    try {
      orgId = Long.valueOf(orgLogin);
    } catch (NumberFormatException ignore) {
      // Ignore - this means the identifier is not a numeric ID
    }

    logger.info("Attempting to delete organization with login: {} or id: {}", orgLogin, orgId);

    OrganizationEntity orgEntity = organizationRepository
      .findByIdOrLogin(orgId, orgLogin)
      .orElseThrow(() ->
        new OrganizationNotFoundException(
          String.format("The organization with the ID or login %s does not exist.", identifier)
        )
      );

    // First delete all challenge participations that reference this organization
    logger.info("Deleting challenge participations for organization: {}", orgEntity.getId());
    challengeParticipationRepository.deleteByOrganization(orgEntity);

    // Now delete the organization
    organizationRepository.delete(orgEntity);
    logger.info(
      "Successfully deleted organization with ID: {} and login: {}",
      orgEntity.getId(),
      orgEntity.getLogin()
    );
  }

  @Transactional(readOnly = true)
  public OrganizationsPageDto listOrganizations(OrganizationSearchQueryDto query) {
    logger.info("query {}", query);

    Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());

    List<String> fieldsToSearchBy = SEARCHABLE_FIELDS;
    Page<OrganizationEntity> organizationEntitiesPage = organizationRepository.findAll(
      pageable,
      query,
      fieldsToSearchBy.toArray(new String[0])
    );

    List<OrganizationDto> organizations = organizationMapper.convertToDtoList(
      organizationEntitiesPage.getContent()
    );
    logger.debug("Organizations {}", organizations);

    return OrganizationsPageDto.builder()
      .organizations(organizations)
      .number(organizationEntitiesPage.getNumber())
      .size(organizationEntitiesPage.getSize())
      .totalElements(organizationEntitiesPage.getTotalElements())
      .totalPages(organizationEntitiesPage.getTotalPages())
      .hasNext(organizationEntitiesPage.hasNext())
      .hasPrevious(organizationEntitiesPage.hasPrevious())
      .build();
  }

  @Transactional(readOnly = true)
  public OrganizationDto getOrganization(String identifier) {
    // Find the organization
    String orgLogin = String.valueOf(identifier);
    Long orgId = null;
    try {
      orgId = Long.valueOf(orgLogin);
    } catch (Exception ignore) {
      // Ignore
    }
    OrganizationEntity orgEntity = organizationRepository
      .findByIdOrLogin(orgId, orgLogin)
      .orElseThrow(() ->
        new OrganizationNotFoundException(
          String.format("The organization with the ID or login %s does not exist.", identifier)
        )
      );

    return organizationMapper.convertToDto(orgEntity);
  }

  @Transactional
  public OrganizationDto createOrganization(OrganizationCreateRequestDto request) {
    // Create the organization entity
    OrganizationEntity entity = OrganizationEntity.builder()
      .login(request.getLogin())
      .name(request.getName())
      .description(request.getDescription())
      .avatarKey(request.getAvatarKey())
      .websiteUrl(request.getWebsiteUrl())
      .challengeCount(0)
      .acronym(request.getAcronym())
      .build();

    try {
      // Save the entity
      OrganizationEntity savedEntity = organizationRepository.save(entity);
      entityManager.refresh(savedEntity);

      // Return the full organization DTO
      return organizationMapper.convertToDto(savedEntity);
    } catch (DataIntegrityViolationException e) {
      // Check if this is the unique constraint violation
      String message = e.getMessage();
      if (message != null) {
        if (message.contains("organization_login_key")) {
          throw new OrganizationAlreadyExistsException(
            String.format("A organization with login '%s' already exists.", request.getLogin())
          );
        }
      }
      // Re-throw the original exception if it's not the constraint we're looking for
      throw e;
    }
  }

  @Transactional
  public OrganizationDto updateOrganization(
    String identifier,
    OrganizationUpdateRequestDto request
  ) {
    // Find the existing organization
    String orgLogin = String.valueOf(identifier);
    Long orgId = null;
    try {
      orgId = Long.valueOf(orgLogin);
    } catch (Exception ignore) {
      // Ignore
    }
    OrganizationEntity existingOrg = organizationRepository
      .findByIdOrLogin(orgId, orgLogin)
      .orElseThrow(() ->
        new OrganizationNotFoundException(
          String.format("The organization with the ID or login %s does not exist.", identifier)
        )
      );

    // Update the organization
    existingOrg.setName(request.getName());
    existingOrg.setDescription(request.getDescription());
    existingOrg.setAvatarKey(request.getAvatarKey());
    existingOrg.setWebsiteUrl(request.getWebsiteUrl());
    existingOrg.setAcronym(request.getAcronym());

    try {
      // Save the updated entity
      OrganizationEntity updatedEntity = organizationRepository.save(existingOrg);

      // Return the updated organization as DTO
      return organizationMapper.convertToDto(updatedEntity);
    } catch (DataIntegrityViolationException e) {
      // Re-throw the original exception if it's not the constraint we're looking for
      throw e;
    }
  }
}
