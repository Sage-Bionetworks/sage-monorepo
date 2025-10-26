package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.ArrayList;
import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.MessageCreateDto;
import org.sagebionetworks.bixarena.api.model.entity.MessageEntity;

public class MessageMapper {

  public MessageEntity convertCreateToEntity(MessageCreateDto dto) {
    if (dto == null) return null;
    return MessageEntity.builder()
      .role(dto.getRole() != null ? dto.getRole().getValue() : null)
      .content(dto.getContent())
      .build();
  }

  public List<MessageEntity> convertCreateList(List<MessageCreateDto> list) {
    if (list == null) return new ArrayList<>();
    return list.stream().map(this::convertCreateToEntity).toList();
  }
}
