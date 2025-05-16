package org.sagebionetworks.agora.gene.api.model.mapper;

import org.sagebionetworks.agora.gene.api.model.document.OverallScoresDocument;
import org.sagebionetworks.agora.gene.api.model.dto.OverallScoresDto;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class OverallScoresMapper extends BaseMapper<OverallScoresDocument, OverallScoresDto> {

  @Override
  public OverallScoresDocument convertToEntity(OverallScoresDto dto, Object... args) {
    OverallScoresDocument entity = new OverallScoresDocument();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public OverallScoresDto convertToDto(OverallScoresDocument entity, Object... args) {
    OverallScoresDto dto = new OverallScoresDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
