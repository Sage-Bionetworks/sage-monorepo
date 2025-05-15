package org.sagebionetworks.agora.gene.api.model.mapper;

import org.sagebionetworks.agora.gene.api.model.document.GeneDocument;
import org.sagebionetworks.agora.gene.api.model.dto.GeneDto;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class GeneMapper extends BaseMapper<GeneDocument, GeneDto> {

  @Override
  public GeneDocument convertToEntity(GeneDto dto, Object... args) {
    GeneDocument entity = new GeneDocument();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public GeneDto convertToDto(GeneDocument entity, Object... args) {
    GeneDto dto = new GeneDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
