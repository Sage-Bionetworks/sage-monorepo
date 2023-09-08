package org.sagebionetworks.openchallenges.challenge.service.service;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeInputDataTypeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeInputDataTypeSearchQueryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeInputDataTypesPageDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeInputDataTypeEntity;
import org.sagebionetworks.openchallenges.challenge.service.model.mapper.ChallengeInputDataTypeMapper;
import org.sagebionetworks.openchallenges.challenge.service.model.repository.ChallengeInputDataTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ChallengeInputDataTypeService {

  //@Autowired private ChallengeInputDataTypeRepository challengeInputDataTypeRepository;
  private final ChallengeInputDataTypeRepository challengeInputDataTypeRepository;

  public ChallengeInputDataTypeService(ChallengeInputDataTypeRepository challengeInputDataTypeRepository) {
    this.challengeInputDataTypeRepository = challengeInputDataTypeRepository;
  }

  private ChallengeInputDataTypeMapper challengeInputDataTypeMapper =
      new ChallengeInputDataTypeMapper();

  private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("name");

  @Transactional(readOnly = true)
  public ChallengeInputDataTypesPageDto listChallengeInputDataTypes(
      ChallengeInputDataTypeSearchQueryDto query) {

    log.info("query {}", query);

    Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());

    List<String> fieldsToSearchBy = SEARCHABLE_FIELDS;
    Page<ChallengeInputDataTypeEntity> entitiesPage =
        challengeInputDataTypeRepository.findAll(
            pageable, query, fieldsToSearchBy.toArray(new String[0]));

    List<ChallengeInputDataTypeDto> challengeInputDataTypes =
        challengeInputDataTypeMapper.convertToDtoList(entitiesPage.getContent());

    return ChallengeInputDataTypesPageDto.builder()
        .challengeInputDataTypes(challengeInputDataTypes)
        .number(entitiesPage.getNumber())
        .size(entitiesPage.getSize())
        .totalElements(entitiesPage.getTotalElements())
        .totalPages(entitiesPage.getTotalPages())
        .hasNext(entitiesPage.hasNext())
        .hasPrevious(entitiesPage.hasPrevious())
        .build();
  }
}