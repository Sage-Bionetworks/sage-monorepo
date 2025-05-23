package org.sagebionetworks.agora.gene.api.model.mapper;

import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionProfileDocument;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileDto;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class RnaDifferentialExpressionProfileMapper
  extends BaseMapper<
    RnaDifferentialExpressionProfileDocument,
    RnaDifferentialExpressionProfileDto
  > {

  @Override
  public RnaDifferentialExpressionProfileDocument convertToEntity(
    RnaDifferentialExpressionProfileDto dto,
    Object... args
  ) {
    RnaDifferentialExpressionProfileDocument entity =
      RnaDifferentialExpressionProfileDocument.builder().build();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public RnaDifferentialExpressionProfileDto convertToDto(
    RnaDifferentialExpressionProfileDocument entity,
    Object... args
  ) {
    RnaDifferentialExpressionProfileDto dto = new RnaDifferentialExpressionProfileDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
