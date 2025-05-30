package org.sagebionetworks.agora.gene.api.model.mapper;

import org.sagebionetworks.agora.gene.api.model.document.TissueDocument;
import org.sagebionetworks.agora.gene.api.model.dto.TissueDto;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class TissueMapper extends BaseMapper<TissueDocument, TissueDto> {

  @Override
  public TissueDocument convertToEntity(TissueDto dto, Object... args) {
    TissueDocument entity = TissueDocument.builder().build();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public TissueDto convertToDto(TissueDocument entity, Object... args) {
    TissueDto dto = new TissueDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
