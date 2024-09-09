package org.sagebionetworks.openchallenges.organization.service.service;

import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.organization.service.exception.OrganizationNotFoundException;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationSearchQueryDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationsPageDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;
import org.sagebionetworks.openchallenges.organization.service.model.mapper.OrganizationMapper;
import org.sagebionetworks.openchallenges.organization.service.model.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {

  private static final Logger LOG = LoggerFactory.getLogger(OrganizationService.class);

  private final OrganizationRepository organizationRepository;

  public OrganizationService(OrganizationRepository organizationRepository) {
    this.organizationRepository = organizationRepository;
  }

  private OrganizationMapper organizationMapper = new OrganizationMapper();

  private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("name", "description");

  @Transactional(readOnly = true)
  public OrganizationsPageDto listOrganizations(OrganizationSearchQueryDto query) {
    LOG.info("query {}", query);

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
    LOG.debug("Organizations {}", organizations);

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
    String orgLogin = String.valueOf(identifier);
    Long orgId = null;
    try {
      orgId = Long.valueOf(orgLogin);
    } catch (Exception ignore) {
      // Ignore
    }

    LOG.info("login: {}", orgLogin);
    LOG.info("id: {}", orgId);

    OrganizationEntity orgEntity = organizationRepository
      .findByIdOrLogin(orgId, orgLogin)
      .orElseThrow(() ->
        new OrganizationNotFoundException(
          String.format("The organization with the ID or login %s does not exist.", identifier)
        )
      );

    return organizationMapper.convertToDto(orgEntity);
  }
}
