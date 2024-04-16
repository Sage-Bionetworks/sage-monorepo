package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamConceptsPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.EdamConceptEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.EdamConceptMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.EdamConceptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EdamConceptService {

  private static final Logger LOG = LoggerFactory.getLogger(EdamConceptService.class);

  private final EdamConceptRepository edamConceptRepository;

  private EdamConceptMapper edamConceptMapper = new EdamConceptMapper();

  private static final List<String> SEARCHABLE_FIELDS =
      Arrays.asList("class_id", "preferred_label");

  public EdamConceptService(EdamConceptRepository edamConceptRepository) {
    this.edamConceptRepository = edamConceptRepository;
  }

  @Transactional(readOnly = true)
  public EdamConceptsPageDto listEdamConcepts(EdamConceptSearchQueryDto query) {
    Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());

    LOG.info("Sections: {}", query.getSections());

    List<String> fieldsToSearchBy = SEARCHABLE_FIELDS;
    Page<EdamConceptEntity> entitiesPage =
        edamConceptRepository.findAll(pageable, query, fieldsToSearchBy.toArray(new String[0]));

    List<EdamConceptDto> edamConcepts =
        edamConceptMapper.convertToDtoList(entitiesPage.getContent());

    return EdamConceptsPageDto.builder()
        .edamConcepts(edamConcepts)
        .number(entitiesPage.getNumber())
        .size(entitiesPage.getSize())
        .totalElements(entitiesPage.getTotalElements())
        .totalPages(entitiesPage.getTotalPages())
        .hasNext(entitiesPage.hasNext())
        .hasPrevious(entitiesPage.hasPrevious())
        .build();
  }
}
