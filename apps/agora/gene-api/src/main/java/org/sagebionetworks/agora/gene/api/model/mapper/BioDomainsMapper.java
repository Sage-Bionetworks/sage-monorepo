package org.sagebionetworks.agora.gene.api.model.mapper;

import org.sagebionetworks.agora.gene.api.model.document.BioDomainsDocument;
import org.sagebionetworks.agora.gene.api.model.dto.BioDomainsDto;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class BioDomainsMapper extends BaseMapper<BioDomainsDocument, BioDomainsDto> {

  @Override
  public BioDomainsDocument convertToEntity(BioDomainsDto dto, Object... args) {
    BioDomainsDocument entity = new BioDomainsDocument();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public BioDomainsDto convertToDto(BioDomainsDocument entity, Object... args) {
    BioDomainsDto dto = new BioDomainsDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
