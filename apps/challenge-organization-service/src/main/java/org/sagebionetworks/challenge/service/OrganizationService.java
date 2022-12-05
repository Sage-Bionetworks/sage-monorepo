package org.sagebionetworks.challenge.service;

import java.util.ArrayList;
import java.util.List;
import org.sagebionetworks.challenge.model.dto.OrganizationDto;
import org.sagebionetworks.challenge.model.dto.OrganizationsPageDto;
import org.sagebionetworks.challenge.model.entity.OrganizationEntity;
import org.sagebionetworks.challenge.model.mapper.OrganizationMapper;
import org.sagebionetworks.challenge.model.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {

  @Autowired private OrganizationRepository organizationRepository;

  private OrganizationMapper organizationMapper = new OrganizationMapper();

  @Transactional(readOnly = true)
  public OrganizationsPageDto listOrganizations(Integer pageNumber, Integer pageSize) {
    Page<OrganizationEntity> organizationEntitiesPage =
        organizationRepository.findAll(PageRequest.of(pageNumber, pageSize));
    List<OrganizationEntity> organizationEntities = organizationEntitiesPage.getContent();
    List<OrganizationDto> organizations = new ArrayList<>();
    organizationEntities.forEach(
        organizationEntity -> {
          OrganizationDto organization = organizationMapper.convertToDto(organizationEntity);
          organizations.add(organization);
        });
    return OrganizationsPageDto.builder()
        .organizations(organizations)
        .totalResults(0)
        .paging(null)
        .build();
  }
}
