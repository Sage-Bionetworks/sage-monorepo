package org.sagebionetworks.bixarena.api.model.mapper;

import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.VoteDto;
import org.sagebionetworks.bixarena.api.model.dto.VotePreferenceDto;
import org.sagebionetworks.bixarena.api.model.entity.VoteEntity;

public class VoteMapper {

  public VoteDto convertToDto(VoteEntity entity) {
    if (entity == null) {
      return null;
    }

    return VoteDto.builder()
      .id(entity.getId())
      .battleId(entity.getBattleId())
      .preference(convertPreferenceToDto(entity.getPreference()))
      .createdAt(entity.getCreatedAt())
      .build();
  }

  public List<VoteDto> convertToDtoList(List<VoteEntity> entities) {
    return entities.stream().map(this::convertToDto).toList();
  }

  private VotePreferenceDto convertPreferenceToDto(VoteEntity.VotePreference preference) {
    if (preference == null) {
      return null;
    }
    return switch (preference) {
      case LEFT_MODEL -> VotePreferenceDto.LEFT_MODEL;
      case RIGHT_MODEL -> VotePreferenceDto.RIGHT_MODEL;
      case TIE -> VotePreferenceDto.TIE;
    };
  }
}
