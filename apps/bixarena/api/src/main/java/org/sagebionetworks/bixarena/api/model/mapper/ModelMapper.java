package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.LicenseDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelDto;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;

public class ModelMapper {

  public ModelDto convertToDto(ModelEntity entity) {
    if (entity == null) {
      return null;
    }

    return ModelDto.builder()
      .id(entity.getId().toString())
      .slug(entity.getSlug())
      .name(entity.getName())
      .license(LicenseDto.fromValue(entity.getLicense()))
      .active(entity.isActive())
      .alias(entity.getAlias())
      .externalLink(entity.getExternalLink())
      .organization(entity.getOrganization())
      .description(entity.getDescription())
      .apiModelName(entity.getApiModelName())
      .apiBase(entity.getApiBase())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .build();
  }

  public List<ModelDto> convertToDtoList(List<ModelEntity> entities) {
    return entities.stream().map(this::convertToDto).toList();
  }
}
