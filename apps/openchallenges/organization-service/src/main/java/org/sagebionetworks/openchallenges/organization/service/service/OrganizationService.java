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
import org.sagebionetworks.openchallenges.organization.service.model.rest.response.ImageResponse;
import org.sagebionetworks.openchallenges.organization.service.service.rest.ImageServiceRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {

  private static final Logger LOG = LoggerFactory.getLogger(OrganizationService.class);

  private final ImageServiceRestClient imageServiceRestClient;

  @Autowired private OrganizationRepository organizationRepository;

  private OrganizationMapper organizationMapper = new OrganizationMapper();

  private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("name", "description");

  public OrganizationService(ImageServiceRestClient imageServiceRestClient) {
    this.imageServiceRestClient = imageServiceRestClient;
  }

  @Transactional(readOnly = true)
  public OrganizationsPageDto listOrganizations(OrganizationSearchQueryDto query) {
    LOG.info("query {}", query);

    Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());

    List<String> fieldsToSearchBy = SEARCHABLE_FIELDS;
    Page<OrganizationEntity> organizationEntitiesPage =
        organizationRepository.findAll(pageable, query, fieldsToSearchBy.toArray(new String[0]));
    LOG.info("organizationEntitiesPage {}", organizationEntitiesPage);

    List<OrganizationDto> organizations =
        organizationMapper.convertToDtoList(organizationEntitiesPage.getContent());

    // Convert the image object keys to URLs
    organizations.stream()
        .parallel()
        .forEach(
            org -> {
              // The avatar url in the org data model is actually an object key.
              // TODO Handle errors
              ImageResponse image = imageServiceRestClient.getImage(org.getAvatarUrl());
              org.setAvatarUrl(image.getUrl());
            });

    LOG.info("Final orgs {}", organizations);

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

    OrganizationDto org = organizationMapper.convertToDto(orgEntity);

    // Convert the image object key to URL
    ImageResponse image = imageServiceRestClient.getImage(org.getAvatarUrl());
    org.setAvatarUrl(image.getUrl());

    return org;
  }
}
