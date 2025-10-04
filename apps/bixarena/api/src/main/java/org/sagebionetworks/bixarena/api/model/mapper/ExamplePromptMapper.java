package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSourceDto;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptEntity;

public class ExamplePromptMapper {

  public ExamplePromptDto convertToDto(ExamplePromptEntity entity) {
    if (entity == null) {
      return null;
    }

    return ExamplePromptDto.builder()
      .id(entity.getId().toString())
      .question(entity.getQuestion())
      .source(ExamplePromptSourceDto.fromValue(entity.getSource()))
      .active(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .build();
  }

  public List<ExamplePromptDto> convertToDtoList(List<ExamplePromptEntity> entities) {
    return entities.stream().map(this::convertToDto).toList();
  }
}
