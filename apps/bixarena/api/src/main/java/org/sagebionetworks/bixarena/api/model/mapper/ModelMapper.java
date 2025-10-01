package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.bixarena.api.model.dto.ModelDto;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;

public class ModelMapper {

  public ModelDto convertToDto(ModelEntity entity) {
    if (entity == null) {
      return null;
    }
    return new ModelDto(
      entity.getId().toString(),
      entity.getSlug(),
      entity.getName(),
      entity.isActive(),
      entity.getCreatedAt(),
      entity.getUpdatedAt()
    ).license(entity.getLicense());
  }

  public List<ModelDto> convertToDtoList(List<ModelEntity> entities) {
    return entities.stream().map(this::convertToDto).collect(Collectors.toList());
  }
}
