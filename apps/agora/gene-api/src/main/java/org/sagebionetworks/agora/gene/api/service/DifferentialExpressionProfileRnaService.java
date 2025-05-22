package org.sagebionetworks.agora.gene.api.service;

import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.DifferentialExpressionProfileRnaDocument;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaSearchQueryDto;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfilesRnaPageDto;
import org.sagebionetworks.agora.gene.api.model.mapper.DifferentialExpressionProfileRnaMapper;
import org.sagebionetworks.agora.gene.api.model.repository.DifferentialExpressionProfileRnaRepository;
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

  private final DifferentialExpressionProfileRnaRepository differentialExpressionProfileRnaRepository;

  private DifferentialExpressionProfileRnaMapper differentialExpressionProfileRnaMapper =
    new DifferentialExpressionProfileRnaMapper();

  public DifferentialExpressionProfileRnaService(
    DifferentialExpressionProfileRnaRepository differentialExpressionProfileRnaRepository
  ) {
    this.differentialExpressionProfileRnaRepository = differentialExpressionProfileRnaRepository;
  }

  public DifferentialExpressionProfilesRnaPageDto listDifferentialExpressionProfilesRna(
    DifferentialExpressionProfileRnaSearchQueryDto query
  ) {
    logger.info("listDifferentialExpressionProfilesRna query: {}", query);

    Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize());

    Page<DifferentialExpressionProfileRnaDocument> page =
      differentialExpressionProfileRnaRepository.findAll(pageable, query);
    logger.info("page: {}", page);

    // TODO: Account for when the page is null
    List<DifferentialExpressionProfileRnaDto> dtos =
      differentialExpressionProfileRnaMapper.convertToDtoList(page.getContent());

    return DifferentialExpressionProfilesRnaPageDto.builder()
      .differentialExpressionProfilesRna(dtos)
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .build();
  }
}
