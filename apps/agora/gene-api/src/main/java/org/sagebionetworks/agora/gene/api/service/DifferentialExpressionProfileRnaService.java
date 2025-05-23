package org.sagebionetworks.agora.gene.api.service;

import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionProfileDocument;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileDto;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfilePageDto;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileSearchQueryDto;
import org.sagebionetworks.agora.gene.api.model.mapper.RnaDifferentialExpressionProfileMapper;
import org.sagebionetworks.agora.gene.api.model.repository.RnaDifferentialExpressionProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DifferentialExpressionProfileRnaService {

  private static final Logger logger = LoggerFactory.getLogger(
    DifferentialExpressionProfileRnaService.class
  );

  private final RnaDifferentialExpressionProfileRepository rnaDifferentialExpressionProfileRepository;

  private RnaDifferentialExpressionProfileMapper rnaDifferentialExpressionProfileMapper =
    new RnaDifferentialExpressionProfileMapper();

  public DifferentialExpressionProfileRnaService(
    RnaDifferentialExpressionProfileRepository rnaDifferentialExpressionProfileRepository
  ) {
    this.rnaDifferentialExpressionProfileRepository = rnaDifferentialExpressionProfileRepository;
  }

  public RnaDifferentialExpressionProfilePageDto listRnaDifferentialExpressionProfiles(
    RnaDifferentialExpressionProfileSearchQueryDto query
  ) {
    logger.info("listDifferentialExpressionProfilesRna query: {}", query);

    Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());

    Page<RnaDifferentialExpressionProfileDocument> page =
      rnaDifferentialExpressionProfileRepository.findAll(pageable, query);
    logger.info("page: {}", page);

    List<RnaDifferentialExpressionProfileDto> dtos =
      rnaDifferentialExpressionProfileMapper.convertToDtoList(page.getContent());

    return RnaDifferentialExpressionProfilePageDto.builder()
      .rnaDifferentialExpressionProfiles(dtos)
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();
  }
}
