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

    Long retrievedId = organizationEntity.getId();
    String retrievedName = organizationEntity.getName();
    String retrievedEmail = organizationEntity.getEmail();
    String retrievedLogin = organizationEntity.getLogin();
    String retrievedAvatarKey = organizationEntity.getAvatarKey();
    String retrievedWebsiteUrl = organizationEntity.getWebsiteUrl();
    Integer retrievedChallengeCount = organizationEntity.getChallengeCount();
    String retrievedDescription = organizationEntity.getDescription();
    String retrievedAcronym = organizationEntity.getAcronym();

    Long retrievedId2 = user.getId();
    String retrievedName2 = user.getName();
    String retrievedEmail2 = user.getEmail();
    String retrievedLogin2 = user.getLogin();
    String retrievedAvatarKey2 = user.getAvatarKey();
    String retrievedWebsiteUrl2 = user.getWebsiteUrl();
    Integer retrievedChallengeCount2 = user.getChallengeCount();
    String retrievedDescription2 = user.getDescription();
    String retrievedAcronym2 = user.getAcronym();

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

    Long retrievedId = user.getId();
    String retrievedName = user.getName();
    String retrievedEmail = user.getEmail();
    String retrievedLogin = user.getLogin();
    String retrievedAvatarKey = user.getAvatarKey();
    String retrievedWebsiteUrl = user.getWebsiteUrl();
    Integer retrievedChallengeCount = user.getChallengeCount();
    String retrievedDescription = user.getDescription();
    String retrievedAcronym = user.getAcronym();

    Long retrievedId2 = organizationEntity.getId();
    String retrievedName2 = organizationEntity.getName();
    String retrievedEmail2 = organizationEntity.getEmail();
    String retrievedLogin2 = organizationEntity.getLogin();
    String retrievedAvatarKey2 = organizationEntity.getAvatarKey();
    String retrievedWebsiteUrl2 = organizationEntity.getWebsiteUrl();
    Integer retrievedChallengeCount2 = organizationEntity.getChallengeCount();
    String retrievedDescription2 = organizationEntity.getDescription();
    String retrievedAcronym2 = organizationEntity.getAcronym();

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
