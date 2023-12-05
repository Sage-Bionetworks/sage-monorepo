package org.sagebionetworks.openchallenges.organization.service.model.mapper;

import org.junit.jupiter.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;

public class OrganizationMapperTest {
  @Test
  public void ConvertToEntity_ShouldReturnDtoProperties_WhenDtoPropertiesPassed() {

    // create new Dto with properties to be copied to organization entity
    OrganizationDto user = new OrganizationDto();
    user.setName("Test Organization");
    user.setDescription("This is a test organization");
    user.setId(4599L);
    user.setEmail("testemail@test.edu");
    user.setLogin("login");
    user.setAvatarKey("avatar Key");
    user.setWebsiteUrl("url");
    user.setChallengeCount(7);
    user.setAcronym(null);

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationEntity organizationEntity = mapper.convertToEntity(user);
    OrganizationEntity entityMirror = new OrganizationEntity("");

    // verify the entity properties were copied
    assertThat(OrganizationEntity.getName()).isEqualTo(user.getName());
    assertThat(OrganizationEntity.getDescription()).isEqualTo(user.getDescription());
    assertThat(OrganizationEntity.getId()).isEqualTo(user.getId());
    assertThat(OrganizationEntity.getEmail()).isEqualTo(user.getEmail());
    assertThat(OrganizationEntity.getLogin()).isEqualTo(user.getLogin());
    assertThat(OrganizationEntity.getAvatarKey()).isEqualTo(user.getAvatarKey());
    assertThat(OrganizationEntity.getWebsiteUrl()).isEqualTo(user.getWebsiteUrl());
    assertThat(OrganizationEntity.getChallengeCount()).isEqualTo(user.getChallengeCount());
    assertThat(OrganizationEntity.getAcronym()).isEqualTo(user.getAcronym());
  }

  @Test
  public void
      ConvertToDto_ShouldReturndDtoWithMatchingEntityProperties_WhenEntityPropertiesPassed() {

    OrganizationEntity organizationEntity = new OrganizationEntity();

    organizationEntity.setName("Test Organization");
    organizationEntity.setDescription("This is a test organization");
    organizationEntity.setId(4599L);
    organizationEntity.setEmail("testemail@test.edu");
    organizationEntity.setLogin("login");
    organizationEntity.setAvatarKey("avatar Key");
    organizationEntity.setWebsiteUrl("url");
    organizationEntity.setChallengeCount(7);
    organizationEntity.setAcronym(null);

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationDto user = mapper.convertToDto(entity);

    // verify the dto properties were copied
    assertThat(user.getName()).isEqualTo(organizationEntity.getName());
    assertThat(user.getDescription()).isEqualTo(organizationEntity.getDescription());
    assertThat(user.getId()).isEqualTo(organizationEntity.getId());
    assertThat(user.getEmail()).isEqualTo(organizationEntity.getEmail());
    assertThat(user.getLogin()).isEqualTo(organizationEntity.getLogin());
    assertThat(user.getAvatarKey()).isEqualTo(organizationEntity.getAvatarKey());
    assertThat(user.getWebsiteUrl()).isEqualTo(organizationEntity.getWebsiteUrl());
    assertThat(user.getChallengeCount()).isEqualTo(organizationEntity.getChallengeCount());
    assertThat(user.getAcronym()).isEqualTo(organizationEntity.getAcronym());
  }
}
