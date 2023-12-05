package org.sagebionetworks.openchallenges.organization.service.model.mapper;

import org.junit.jupiter.api.Assertions;
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

    // verify the entity properties were copied
    Assertions.assertThat(organizationEntity.getName()).isEqualTo(user.getName());
    Assertions.assertThat(organizationEntity.getDescription()).isEqualTo(user.getDescription());
    Assertions.assertThat(organizationEntity.getId()).isEqualTo(user.getId());
    Assertions.assertThat(organizationEntity.getEmail()).isEqualTo(user.getEmail());
    Assertions.assertThat(organizationEntity.getLogin()).isEqualTo(user.getLogin());
    Assertions.assertThat(organizationEntity.getAvatarKey()).isEqualTo(user.getAvatarKey());
    Assertions.assertThat(organizationEntity.getWebsiteUrl()).isEqualTo(user.getWebsiteUrl());
    Assertions.assertThat(organizationEntity.getChallengeCount())
        .isEqualTo(user.getChallengeCount());
    Assertions.assertThat(organizationEntity.getAcronym()).isEqualTo(user.getAcronym());
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
    Assertions.assertThat(user.getName()).isEqualTo(organizationEntity.getName());
    Assertions.assertThat(user.getDescription()).isEqualTo(organizationEntity.getDescription());
    Assertions.assertThat(user.getId()).isEqualTo(organizationEntity.getId());
    Assertions.assertThat(user.getEmail()).isEqualTo(organizationEntity.getEmail());
    Assertions.assertThat(user.getLogin()).isEqualTo(organizationEntity.getLogin());
    Assertions.assertThat(user.getAvatarKey()).isEqualTo(organizationEntity.getAvatarKey());
    Assertions.assertThat(user.getWebsiteUrl()).isEqualTo(organizationEntity.getWebsiteUrl());
    Assertions.assertThat(user.getChallengeCount())
        .isEqualTo(organizationEntity.getChallengeCount());
    Assertions.assertThat(user.getAcronym()).isEqualTo(organizationEntity.getAcronym());
  }
}
