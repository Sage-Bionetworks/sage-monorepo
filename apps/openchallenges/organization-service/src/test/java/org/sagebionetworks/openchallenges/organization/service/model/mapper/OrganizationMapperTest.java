package org.sagebionetworks.openchallenges.organization.service.model.mapper;

import org.junit.jupiter.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;

public class OrganizationMapperTest {
  @Test
  public void ConvertToEntity_ShouldReturnDtoProperties_WhenDtoPropertiesPassed() {

    // create new Dto with properties to be copied to organization entity
    OrganizationDto dto = new OrganizationDto();
    dto.setName("Test Organization");
    dto.setDescription("This is a test organization");
    dto.setId(4599L);
    dto.setEmail("testemail@test.edu");
    dto.setLogin("login");
    dto.setAvatarKey("avatar Key");
    dto.setWebsiteUrl("url");
    dto.setChallengeCount(7);
    dto.setAcronym(null);

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationEntity entity = mapper.convertToEntity(dto);

    assertThat(entity.getName()).isEqualTo(dto.getName());
    assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
    assertThat(entity.getId()).isEqualTo(dto.getId());
    assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
    assertThat(entity.getLogin()).isEqualTo(dto.getLogin());
    assertThat(entity.getAvatarKey()).isEqualTo(dto.getAvatarKey());
    assertThat(entity.getWebsiteUrl()).isEqualTo(dto.getWebsiteUrl());
    assertThat(entity.getChallengeCount()).isEqualTo(dto.getChallengeCount());
    assertThat(entity.getAcronym()).isEqualTo(dto.getAcronym());
  }

  @Test
  public void convertToEntity_ShouldReturnNullProperties_WhenDtoPropertiesNotPassed() {

    OrganizationDto dto = new OrganizationDto(null);
    // don't set dto properties to avoid BeanUtils.copyProperties from passing them to the new
    // entity

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationEntity entity = mapper.convertToEntity(dto);

    assertThat(entity.getName()).isEqualTo(null);
    assertThat(entity.getDescription()).isEqualTo(null);
    assertThat(entity.getId()).isEqualTo(null);
    assertThat(entity.getEmail()).isEqualTo(null);
    assertThat(entity.getLogin()).isEqualTo(null);
    assertThat(entity.getAvatarKey()).isEqualTo(null);
    assertThat(entity.getWebsiteUrl()).isEqualTo(null);
    assertThat(entity.getChallengeCount()).isEqualTo(null);
    assertThat(entity.getAcronym()).isEqualTo(null);
  }

  @Test
  public void
      ConvertToDto_ShouldReturndDtoWithMatchingEntityProperties_WhenEntityPropertiesPassed() {

    OrganizationEntity entity = new OrganizationEntity();
    entity.setName("Test Organization");
    entity.setDescription("This is a test organization");
    entity.setId(4599L);
    entity.setEmail("testemail@test.edu");
    entity.setLogin("login");
    entity.setAvatarKey("avatar Key");
    entity.setWebsiteUrl("url");
    entity.setChallengeCount(7);
    entity.setAcronym(null);

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationDto dto = mapper.convertToDto(entity);

    assertThat(dto.getName()).isEqualTo(entity.getName());
    assertThat(dto.getDescription()).isEqualTo(entity.getDescription());
    assertThat(dto.getId()).isEqualTo(entity.getId());
    assertThat(dto.getEmail()).isEqualTo(entity.getEmail());
    assertThat(dto.getLogin()).isEqualTo(entity.getLogin());
    assertThat(dto.getAvatarKey()).isEqualTo(entity.getAvatarKey());
    assertThat(dto.getWebsiteUrl()).isEqualTo(entity.getWebsiteUrl());
    assertThat(dto.getChallengeCount()).isEqualTo(entity.getChallengeCount());
    assertThat(dto.getAcronym()).isEqualTo(entity.getAcronym());
  }

  @Test
  public void convertToDto_ShouldReturnNullProperties_WhenEntityPropertiesNotPassed() {

    OrganizationEntity entity = new OrganizationEntity(null);
    // don't set entity properties to avoid BeanUtils.copyProperties from passing them to the new
    // entity

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationDto dto = mapper.convertToDto(entity);
    // don't set dto properties to avoid BeanUtils.copyProperties from passing them to the new
    // entity

    assertThat(dto.getName()).isEqualTo(null);
    assertThat(dto.getDescription()).isEqualTo(null);
    assertThat(dto.getId()).isEqualTo(null);
    assertThat(dto.getEmail()).isEqualTo(null);
    assertThat(dto.getLogin()).isEqualTo(null);
    assertThat(dto.getAvatarKey()).isEqualTo(null);
    assertThat(dto.getWebsiteUrl()).isEqualTo(null);
    assertThat(dto.getChallengeCount()).isEqualTo(null);
    assertThat(dto.getAcronym()).isEqualTo(null);
  }
}
