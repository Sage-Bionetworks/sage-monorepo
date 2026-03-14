package org.sagebionetworks.bixarena.api.model.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostDto;
import org.sagebionetworks.bixarena.api.model.entity.QuestPostEntity;

public class QuestPostMapper {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {};

  public QuestPostDto convertToDto(QuestPostEntity entity) {
    if (entity == null) {
      return null;
    }

    return QuestPostDto.builder()
      .postIndex(entity.getPostIndex())
      .date(entity.getDate())
      .title(entity.getTitle())
      .description(entity.getDescription())
      .images(parseImages(entity.getImages()))
      .publishDate(entity.getPublishDate())
      .requiredProgress(entity.getRequiredProgress())
      .requiredTier(entity.getRequiredTier() != null
        ? QuestPostDto.RequiredTierEnum.fromValue(entity.getRequiredTier())
        : null)
      .locked(false)
      .build();
  }

  public QuestPostDto convertToLockedDto(QuestPostEntity entity) {
    if (entity == null) {
      return null;
    }

    return QuestPostDto.builder()
      .postIndex(entity.getPostIndex())
      .date(entity.getDate())
      .title(entity.getTitle())
      .description(null)
      .images(Collections.emptyList())
      .requiredProgress(entity.getRequiredProgress())
      .requiredTier(entity.getRequiredTier() != null
        ? QuestPostDto.RequiredTierEnum.fromValue(entity.getRequiredTier())
        : null)
      .locked(true)
      .build();
  }

  public QuestPostEntity convertToEntity(
      QuestPostCreateOrUpdateDto dto, Long questId, Integer postIndex) {
    return QuestPostEntity.builder()
      .questId(questId)
      .postIndex(postIndex)
      .date(dto.getDate())
      .title(dto.getTitle())
      .description(dto.getDescription())
      .images(serializeImages(dto.getImages()))
      .publishDate(dto.getPublishDate())
      .requiredProgress(dto.getRequiredProgress())
      .requiredTier(dto.getRequiredTier() != null
        ? dto.getRequiredTier().getValue()
        : null)
      .build();
  }

  public void updateEntity(QuestPostEntity entity, QuestPostCreateOrUpdateDto dto) {
    entity.setDate(dto.getDate());
    entity.setTitle(dto.getTitle());
    entity.setDescription(dto.getDescription());
    entity.setImages(serializeImages(dto.getImages()));
    entity.setPublishDate(dto.getPublishDate());
    entity.setRequiredProgress(dto.getRequiredProgress());
    entity.setRequiredTier(dto.getRequiredTier() != null
      ? dto.getRequiredTier().getValue()
      : null);
  }

  private List<URI> parseImages(String json) {
    if (json == null || json.isBlank()) {
      return Collections.emptyList();
    }
    try {
      List<String> urls = OBJECT_MAPPER.readValue(json, STRING_LIST_TYPE);
      return urls.stream().map(URI::create).toList();
    } catch (JsonProcessingException e) {
      return Collections.emptyList();
    }
  }

  private String serializeImages(List<URI> images) {
    if (images == null || images.isEmpty()) {
      return "[]";
    }
    try {
      List<String> urls = images.stream().map(URI::toString).toList();
      return OBJECT_MAPPER.writeValueAsString(urls);
    } catch (JsonProcessingException e) {
      return "[]";
    }
  }
}
