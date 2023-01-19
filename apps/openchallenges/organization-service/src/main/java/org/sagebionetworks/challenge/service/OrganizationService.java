package org.sagebionetworks.challenge.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sagebionetworks.challenge.exception.OrganizationNotFoundException;
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

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Transactional(readOnly = true)
  public OrganizationsPageDto listOrganizations(Integer pageNumber, Integer pageSize) {
    Page<OrganizationEntity> organizationEntitiesPage =
        organizationRepository.findAll(PageRequest.of(pageNumber, pageSize));
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
    EntityManager em = entityManagerFactory.createEntityManager();
    em.getTransaction().begin();
    Session session = em.unwrap(Session.class);
    OrganizationEntity orgEntity =
        session.bySimpleNaturalId(OrganizationEntity.class).load(organizationLogin);
    if (orgEntity == null) {
      throw new OrganizationNotFoundException(
          String.format("The organization with ID %s does not exist.", organizationLogin));
    }
    return organizationMapper.convertToDto(orgEntity);
  }
}
