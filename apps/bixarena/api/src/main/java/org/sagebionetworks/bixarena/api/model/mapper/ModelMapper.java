package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.LicenseTypeDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelDto;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;
import org.springframework.beans.BeanUtils;

public class ModelMapper {

  public ModelDto convertToDto(ModelEntity entity) {
    ModelDto dto = new ModelDto();
    if (entity != null) {
      // Copy properties automatically, excluding fields that need special handling
      BeanUtils.copyProperties(entity, dto, "id", "license");

      // Handle type conversions
      dto.setId(entity.getId().toString());
      dto.setLicense(LicenseTypeDto.fromValue(entity.getLicense()));
    }
    return dto;
  }

  public List<ModelDto> convertToDtoList(List<ModelEntity> entities) {
    return entities.stream().map(this::convertToDto).toList();
  }
}
