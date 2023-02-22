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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrganizationService {

  private static final Logger LOG = LoggerFactory.getLogger(OrganizationService.class);

  @Autowired private OrganizationRepository organizationRepository;

  private OrganizationMapper organizationMapper = new OrganizationMapper();

  private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("name", "description");

  @Transactional(readOnly = true)
  public OrganizationsPageDto listOrganizations(OrganizationSearchQueryDto query) {
    LOG.info("query {}", query);
    log.info("query 2 {}", query);

    Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());

    List<String> fieldsToSearchBy = SEARCHABLE_FIELDS;
    Page<OrganizationEntity> organizationEntitiesPage =
        organizationRepository.findAll(pageable, query, fieldsToSearchBy.toArray(new String[0]));
    LOG.info("organizationEntitiesPage {}", organizationEntitiesPage);

    List<OrganizationDto> organizations =
        organizationMapper.convertToDtoList(organizationEntitiesPage.getContent());

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
  public OrganizationDto getOrganization(String organizationLogin) {
    OrganizationEntity orgEntity =
        organizationRepository
            .findBySimpleNaturalId(organizationLogin)
            .orElseThrow(
                () ->
                    new OrganizationNotFoundException(
                        String.format(
                            "The organization with ID %s does not exist.", organizationLogin)));
    return organizationMapper.convertToDto(orgEntity);
  }
}
