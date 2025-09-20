package org.sagebionetworks.openchallenges.challenge.service.model.mapper;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeCategoryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

@Slf4j
public class ChallengeMapper extends BaseMapper<ChallengeEntity, ChallengeDto> {

  private SimpleChallengePlatformMapper platformMapper = new SimpleChallengePlatformMapper();
  private EdamConceptMapper edamConceptMapper = new EdamConceptMapper();

  @Override
  public ChallengeEntity convertToEntity(ChallengeDto dto, Object... args) {
    ChallengeEntity entity = new ChallengeEntity();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  @Override
  public ChallengeDto convertToDto(ChallengeEntity entity, Object... args) {
    ChallengeDto dto = new ChallengeDto();
    log.trace("challenge dto initial: {}", dto);
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto, "stars", "inputDataTypes", "platform", "operation");
      log.trace("challenge dto before set: {}", dto);
      dto.setStatus(ChallengeStatusDto.fromValue(entity.getStatus()));
      // Only set the platform when it exists. Avoid serializing an empty object with null fields.
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
      log.trace("challenge dto: {}", dto);
    }
    return dto;
  }
}
