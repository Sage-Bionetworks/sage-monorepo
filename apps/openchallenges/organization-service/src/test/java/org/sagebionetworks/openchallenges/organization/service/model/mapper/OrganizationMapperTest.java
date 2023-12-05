package org.sagebionetworks.openchallenges.organization.service.model.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.organization.service.model.dto.OrganizationDto;
import org.sagebionetworks.openchallenges.organization.service.model.entity.OrganizationEntity;

public class OrganizationMapperTest {
  @Test
  public void ConvertToEntity_ShouldReturnDtoProperties_WhenDtoPropertiesPassed() {

    // create new Dto with properties to be copied to organization entity
    OrganizationDto dTo = new OrganizationDto();
    dTo.setName("Test Organization");
    dTo.setDescription("This is a test organization");
    dTo.setId(4599L);
    dTo.setEmail("testemail@test.edu");
    dTo.setLogin("login");
    dTo.setAvatarKey("avatar Key");
    dTo.setWebsiteUrl("url");
    dTo.setChallengeCount(7);
    dTo.setAcronym(null);

    OrganizationMapper mapper = new OrganizationMapper();

    OrganizationEntity entity = mapper.convertToEntity(dTo);

    Long actualId = entity.getId();
    String actualName = entity.getName();
    String actualEmail = entity.getEmail();
    String actualLogin = entity.getLogin();
    String actualAvatarKey = entity.getAvatarKey();
    String actualWebsiteUrl = entity.getWebsiteUrl();
    Integer actualChallengeCount = entity.getChallengeCount();
    String actualDescription = entity.getDescription();
    String actualAcronym = entity.getAcronym();

    Long actualId2 = dTo.getId();
    String actualName2 = dTo.getName();
    String actualEmail2 = dTo.getEmail();
    String actualLogin2 = dTo.getLogin();
    String actualAvatarKey2 = dTo.getAvatarKey();
    String actualWebsiteUrl2 = dTo.getWebsiteUrl();
    Integer actualChallengeCount2 = dTo.getChallengeCount();
    String actualDescription2 = dTo.getDescription();
    String actualAcronym2 = dTo.getAcronym();

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

    OrganizationDto dTo = mapper.convertToDto(entity);

    Long actualId = dTo.getId();
    String actualName = dTo.getName();
    String actualEmail = dTo.getEmail();
    String actualLogin = dTo.getLogin();
    String actualAvatarKey = dTo.getAvatarKey();
    String actualWebsiteUrl = dTo.getWebsiteUrl();
    Integer actualChallengeCount = dTo.getChallengeCount();
    String actualDescription = dTo.getDescription();
    String actualAcronym = dTo.getAcronym();

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
