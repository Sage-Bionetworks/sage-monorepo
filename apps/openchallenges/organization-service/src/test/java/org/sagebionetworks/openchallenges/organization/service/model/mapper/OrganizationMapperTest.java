package org.sagebionetworks.openchallenges.organization.service.model.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;

public class OrganizationMapperTest {
  @Test
  public void ConvertToEntity_ShouldReturnDtoProperties_WhenDtoPropertiesPassed() {

    // create new Dto with properties to be copied to organization entity
    OrganizationDto organizationDto = new OrganizationDto();
    organizationDto.setName("Test Organization");
    organizationDto.setDescription("This is a test organization");
    organizationDto.setId(4599L);
    organizationDto.setEmail("testemail@test.edu");
    organizationDto.setLogin("login");
    organizationDto.setAvatarKey("avatar Key");
    organizationDto.setWebsiteUrl("url");
    organizationDto.setChallengeCount(7);
    organizationDto.setAcronym(null);

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationEntity entity = mapper.convertToEntity(organizationDto);

    Long actualId = entity.getId();
    String actualName = entity.getName();
    String actualEmail = entity.getEmail();
    String actualLogin = entity.getLogin();
    String actualAvatarKey = entity.getAvatarKey();
    String actualWebsiteUrl = entity.getWebsiteUrl();
    Integer actualChallengeCount = entity.getChallengeCount();
    String actualDescription = entity.getDescription();
    String actualAcronym = entity.getAcronym();

    Long actualId2 = organizationDto.getId();
    String actualName2 = organizationDto.getName();
    String actualEmail2 = organizationDto.getEmail();
    String actualLogin2 = organizationDto.getLogin();
    String actualAvatarKey2 = organizationDto.getAvatarKey();
    String actualWebsiteUrl2 = organizationDto.getWebsiteUrl();
    Integer actualChallengeCount2 = organizationDto.getChallengeCount();
    String actualDescription2 = organizationDto.getDescription();
    String actualAcronym2 = organizationDto.getAcronym();

    // verify the entity properties were copied
    Assertions.assertEquals(actualId, actualId2);
    Assertions.assertEquals(actualName, actualName2);
    Assertions.assertEquals(actualEmail, actualEmail2);
    Assertions.assertEquals(actualLogin, actualLogin2);
    Assertions.assertEquals(actualAvatarKey, actualAvatarKey2);
    Assertions.assertEquals(actualWebsiteUrl, actualWebsiteUrl2);
    Assertions.assertEquals(actualChallengeCount, actualChallengeCount2);
    Assertions.assertEquals(actualDescription, actualDescription2);
    Assertions.assertEquals(actualAcronym, actualAcronym2);
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

    OrganizationDto organizationDto = mapper.convertToDto(entity);

    Long actualId = organizationDto.getId();
    String actualName = organizationDto.getName();
    String actualEmail = organizationDto.getEmail();
    String actualLogin = organizationDto.getLogin();
    String actualAvatarKey = organizationDto.getAvatarKey();
    String actualWebsiteUrl = organizationDto.getWebsiteUrl();
    Integer actualChallengeCount = organizationDto.getChallengeCount();
    String actualDescription = organizationDto.getDescription();
    String actualAcronym = organizationDto.getAcronym();

    Long actualId2 = entity.getId();
    String actualName2 = entity.getName();
    String actualEmail2 = entity.getEmail();
    String actualLogin2 = entity.getLogin();
    String actualAvatarKey2 = entity.getAvatarKey();
    String actualWebsiteUrl2 = entity.getWebsiteUrl();
    Integer actualChallengeCount2 = entity.getChallengeCount();
    String actualDescription2 = entity.getDescription();
    String actualAcronym2 = entity.getAcronym();

    // verify the dto properties were copied
    Assertions.assertEquals(actualId, actualId2);
    Assertions.assertEquals(actualName, actualName2);
    Assertions.assertEquals(actualEmail, actualEmail2);
    Assertions.assertEquals(actualLogin, actualLogin2);
    Assertions.assertEquals(actualAvatarKey, actualAvatarKey2);
    Assertions.assertEquals(actualWebsiteUrl, actualWebsiteUrl2);
    Assertions.assertEquals(actualChallengeCount, actualChallengeCount2);
    Assertions.assertEquals(actualDescription, actualDescription2);
    Assertions.assertEquals(actualAcronym, actualAcronym2);
  }
}
