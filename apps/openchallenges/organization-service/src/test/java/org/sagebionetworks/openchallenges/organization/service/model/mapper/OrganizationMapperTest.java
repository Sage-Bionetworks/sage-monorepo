package org.sagebionetworks.openchallenges.organization.service.model.mapper;

import org.junit.jupiter.api.Assertions;
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

    Long actualId = entity.getId();
    String actualName = entity.getName();
    String actualEmail = entity.getEmail();
    String actualLogin = entity.getLogin();
    String actualAvatarKey = entity.getAvatarKey();
    String actualWebsiteUrl = entity.getWebsiteUrl();
    Integer actualChallengeCount = entity.getChallengeCount();
    String actualDescription = entity.getDescription();
    String actualAcronym = entity.getAcronym();

    Long actualId2 = dto.getId();
    String actualName2 = dto.getName();
    String actualEmail2 = dto.getEmail();
    String actualLogin2 = dto.getLogin();
    String actualAvatarKey2 = dto.getAvatarKey();
    String actualWebsiteUrl2 = dto.getWebsiteUrl();
    Integer actualChallengeCount2 = dto.getChallengeCount();
    String actualDescription2 = dto.getDescription();
    String actualAcronym2 = dto.getAcronym();

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

    OrganizationDto dto = mapper.convertToDto(entity);

    Long actualId = dto.getId();
    String actualName = dto.getName();
    String actualEmail = dto.getEmail();
    String actualLogin = dto.getLogin();
    String actualAvatarKey = dto.getAvatarKey();
    String actualWebsiteUrl = dto.getWebsiteUrl();
    Integer actualChallengeCount = dto.getChallengeCount();
    String actualDescription = dto.getDescription();
    String actualAcronym = dto.getAcronym();

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
