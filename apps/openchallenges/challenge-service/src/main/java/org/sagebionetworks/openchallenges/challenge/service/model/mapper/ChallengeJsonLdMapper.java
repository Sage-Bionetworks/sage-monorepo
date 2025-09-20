package org.sagebionetworks.openchallenges.challenge.service.model.mapper;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeCategoryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeJsonLdDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class ChallengeJsonLdMapper extends BaseMapper<ChallengeEntity, ChallengeJsonLdDto> {

  private SimpleChallengePlatformMapper platformMapper = new SimpleChallengePlatformMapper();
  private EdamConceptMapper edamConceptMapper = new EdamConceptMapper();

  @Override
  public ChallengeEntity convertToEntity(ChallengeJsonLdDto dto, Object... args) {
    ChallengeEntity entity = new ChallengeEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public ChallengeJsonLdDto convertToDto(ChallengeEntity entity, Object... args) {
    ChallengeJsonLdDto dto = new ChallengeJsonLdDto();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto, "stars", "inputDataTypes", "platform", "operation");
      dto.setStatus(ChallengeStatusDto.fromValue(entity.getStatus()));
      var platformDto = platformMapper.convertToDto(entity.getPlatform());
      if (platformDto != null) {
        dto.setPlatform(platformDto);
      }
      if (entity.getOperation() != null) {
        dto.setOperation(edamConceptMapper.convertToDto(entity.getOperation()));
      }
      dto.submissionTypes(
        entity
          .getSubmissionTypes()
          .stream()
          .map(o -> ChallengeSubmissionTypeDto.fromValue(o.getName()))
          .toList()
      );
      dto.incentives(
        entity
          .getIncentives()
          .stream()
          .map(o -> ChallengeIncentiveDto.fromValue(o.getName()))
          .toList()
      );
      dto.categories(
        entity
          .getCategories()
          .stream()
          .map(o -> ChallengeCategoryDto.fromValue(o.getName()))
          .toList()
      );
      dto.inputDataTypes(edamConceptMapper.convertToDtoList(entity.getInputDataTypes()));
      dto.starredCount(entity.getStars().size());
      // Add JSON-LD specific properties
      dto.atContext(
        "https://raw.githubusercontent.com/Sage-Bionetworks/core-models/de1000bf4f2ad7f19eb3ff0ff249ed4c046fd247/draft-data-models/challenges.jsonld"
      );
      dto.atId("https://openchallenges.io/api/v1/challenges/" + entity.getId());
      dto.atType("Challenge");
    }
    return dto;
  }
}
