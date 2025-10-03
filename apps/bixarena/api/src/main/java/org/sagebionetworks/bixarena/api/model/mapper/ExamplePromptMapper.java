package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSourceDto;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptEntity;
import org.springframework.beans.BeanUtils;

public class ExamplePromptMapper {

  public ExamplePromptDto convertToDto(ExamplePromptEntity entity) {
    ExamplePromptDto dto = new ExamplePromptDto();
    if (entity != null) {
      // Copy properties automatically, excluding fields that need special handling
      BeanUtils.copyProperties(entity, dto, "id", "source");

      // Handle type conversions
      dto.setId(entity.getId().toString());
      dto.setSource(ExamplePromptSourceDto.fromValue(entity.getSource()));
    }
    return dto;
  }

  public List<ExamplePromptDto> convertToDtoList(List<ExamplePromptEntity> entities) {
    return entities.stream().map(this::convertToDto).toList();
  }
}
