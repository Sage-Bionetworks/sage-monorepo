package org.sagebionetworks.agora.gene.api.model.mapper;

import org.sagebionetworks.agora.gene.api.model.document.TeamDocument;
import org.sagebionetworks.agora.gene.api.model.dto.TeamDto;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class TeamMapper extends BaseMapper<TeamDocument, TeamDto> {

  @Override
  public TeamDocument convertToEntity(TeamDto dto, Object... args) {
    TeamDocument entity = new TeamDocument();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public TeamDto convertToDto(TeamDocument entity, Object... args) {
    TeamDto dto = new TeamDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }
}
