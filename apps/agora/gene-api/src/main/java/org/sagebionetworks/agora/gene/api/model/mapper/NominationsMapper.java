package org.sagebionetworks.agora.gene.api.model.mapper;

import org.sagebionetworks.agora.gene.api.model.document.NominationsDocument;
import org.sagebionetworks.agora.gene.api.model.dto.NominationsDto;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class NominationsMapper extends BaseMapper<NominationsDocument, NominationsDto> {

  @Override
  public NominationsDocument convertToEntity(NominationsDto dto, Object... args) {
    NominationsDocument entity = NominationsDocument.builder().build();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public NominationsDto convertToDto(NominationsDocument entity, Object... args) {
    NominationsDto dto = new NominationsDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
