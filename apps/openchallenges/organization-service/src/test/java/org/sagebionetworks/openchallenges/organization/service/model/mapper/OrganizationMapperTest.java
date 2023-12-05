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

    Long retrievedId = entity.getId();
    String retrievedName = entity.getName();
    String retrievedEmail = entity.getEmail();
    String retrievedLogin = entity.getLogin();
    String retrievedAvatarKey = entity.getAvatarKey();
    String retrievedWebsiteUrl = entity.getWebsiteUrl();
    Integer retrievedChallengeCount = entity.getChallengeCount();
    String retrievedDescription = entity.getDescription();
    String retrievedAcronym = entity.getAcronym();

    Long retrievedId2 = organizationDto.getId();
    String retrievedName2 = organizationDto.getName();
    String retrievedEmail2 = organizationDto.getEmail();
    String retrievedLogin2 = organizationDto.getLogin();
    String retrievedAvatarKey2 = organizationDto.getAvatarKey();
    String retrievedWebsiteUrl2 = organizationDto.getWebsiteUrl();
    Integer retrievedChallengeCount2 = organizationDto.getChallengeCount();
    String retrievedDescription2 = organizationDto.getDescription();
    String retrievedAcronym2 = organizationDto.getAcronym();

    // verify the entity properties were copied
    Assertions.assertEquals(retrievedId, retrievedId2);
    Assertions.assertEquals(retrievedName, retrievedName2);
    Assertions.assertEquals(retrievedEmail, retrievedEmail2);
    Assertions.assertEquals(retrievedLogin, retrievedLogin2);
    Assertions.assertEquals(retrievedAvatarKey, retrievedAvatarKey2);
    Assertions.assertEquals(retrievedWebsiteUrl, retrievedWebsiteUrl2);
    Assertions.assertEquals(retrievedChallengeCount, retrievedChallengeCount2);
    Assertions.assertEquals(retrievedDescription, retrievedDescription2);
    Assertions.assertEquals(retrievedAcronym, retrievedAcronym2);
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

    Long retrievedId = organizationDto.getId();
    String retrievedName = organizationDto.getName();
    String retrievedEmail = organizationDto.getEmail();
    String retrievedLogin = organizationDto.getLogin();
    String retrievedAvatarKey = organizationDto.getAvatarKey();
    String retrievedWebsiteUrl = organizationDto.getWebsiteUrl();
    Integer retrievedChallengeCount = organizationDto.getChallengeCount();
    String retrievedDescription = organizationDto.getDescription();
    String retrievedAcronym = organizationDto.getAcronym();

    Long retrievedId2 = entity.getId();
    String retrievedName2 = entity.getName();
    String retrievedEmail2 = entity.getEmail();
    String retrievedLogin2 = entity.getLogin();
    String retrievedAvatarKey2 = entity.getAvatarKey();
    String retrievedWebsiteUrl2 = entity.getWebsiteUrl();
    Integer retrievedChallengeCount2 = entity.getChallengeCount();
    String retrievedDescription2 = entity.getDescription();
    String retrievedAcronym2 = entity.getAcronym();

    // verify the dto properties were copied
    Assertions.assertEquals(retrievedId, retrievedId2);
    Assertions.assertEquals(retrievedName, retrievedName2);
    Assertions.assertEquals(retrievedEmail, retrievedEmail2);
    Assertions.assertEquals(retrievedLogin, retrievedLogin2);
    Assertions.assertEquals(retrievedAvatarKey, retrievedAvatarKey2);
    Assertions.assertEquals(retrievedWebsiteUrl, retrievedWebsiteUrl2);
    Assertions.assertEquals(retrievedChallengeCount, retrievedChallengeCount2);
    Assertions.assertEquals(retrievedDescription, retrievedDescription2);
    Assertions.assertEquals(retrievedAcronym, retrievedAcronym2);
  }
}
