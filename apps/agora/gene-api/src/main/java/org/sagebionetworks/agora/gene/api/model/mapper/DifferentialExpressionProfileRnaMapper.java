package org.sagebionetworks.agora.gene.api.model.mapper;

import org.sagebionetworks.agora.gene.api.model.document.DifferentialExpressionProfileRnaDocument;
import org.sagebionetworks.agora.gene.api.model.dto.DifferentialExpressionProfileRnaDto;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class DifferentialExpressionProfileRnaMapper
  extends BaseMapper<
    DifferentialExpressionProfileRnaDocument,
    DifferentialExpressionProfileRnaDto
  > {

  @Override
  public DifferentialExpressionProfileRnaDocument convertToEntity(
    DifferentialExpressionProfileRnaDto dto,
    Object... args
  ) {
    DifferentialExpressionProfileRnaDocument entity =
      DifferentialExpressionProfileRnaDocument.builder().build();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public DifferentialExpressionProfileRnaDto convertToDto(
    DifferentialExpressionProfileRnaDocument entity,
    Object... args
  ) {
    DifferentialExpressionProfileRnaDto dto = new DifferentialExpressionProfileRnaDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
