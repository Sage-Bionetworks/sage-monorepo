package org.sagebionetworks.agora.gene.api.model.mapper;

import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionDocument;
import org.sagebionetworks.agora.gene.api.model.dto.GCTGeneDto;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class RnaDifferentialExpressionMapper
  extends BaseMapper<RnaDifferentialExpressionDocument, GCTGeneDto> {

  @Override
  public RnaDifferentialExpressionDocument convertToEntity(GCTGeneDto dto, Object... args) {
    RnaDifferentialExpressionDocument entity = new RnaDifferentialExpressionDocument();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public GCTGeneDto convertToDto(RnaDifferentialExpressionDocument entity, Object... args) {
    GCTGeneDto dto = new GCTGeneDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
