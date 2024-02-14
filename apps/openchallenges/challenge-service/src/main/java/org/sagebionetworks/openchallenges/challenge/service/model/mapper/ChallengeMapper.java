package org.sagebionetworks.openchallenges.challenge.service.model.mapper;

import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeCategoryDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeIncentiveDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeStatusDto;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.ChallengeSubmissionTypeDto;
import org.sagebionetworks.openchallenges.challenge.service.model.entity.ChallengeEntity;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class ChallengeMapper extends BaseMapper<ChallengeEntity, ChallengeDto> {

  private static final Logger LOG = LoggerFactory.getLogger(ChallengeMapper.class);

  private SimpleChallengePlatformMapper platformMapper = new SimpleChallengePlatformMapper();
  private SimpleChallengeInputDataTypeMapper inputDataTypeMapper =
      new SimpleChallengeInputDataTypeMapper();

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
    LOG.info("challenge dto initial: {}", dto);
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto, "stars", "inputDataTypes", "platform");
      LOG.info("challenge dto before set: {}", dto);
      dto.setStatus(ChallengeStatusDto.fromValue(entity.getStatus()));
      dto.setPlatform(platformMapper.convertToDto(entity.getPlatform()));
      dto.submissionTypes(
          entity.getSubmissionTypes().stream()
              .map(o -> ChallengeSubmissionTypeDto.fromValue(o.getName()))
              .toList());
      dto.incentives(
          entity.getIncentives().stream()
              .map(o -> ChallengeIncentiveDto.fromValue(o.getName()))
              .toList());
      dto.categories(
          entity.getCategories().stream()
              .map(o -> ChallengeCategoryDto.fromValue(o.getName()))
              .toList());
      dto.inputDataTypes(inputDataTypeMapper.convertToDtoList(entity.getInputDataTypes()));
      dto.starredCount(entity.getStars().size());
      LOG.info("challenge dto: {}", dto);
    }
    return dto;
  }
}
