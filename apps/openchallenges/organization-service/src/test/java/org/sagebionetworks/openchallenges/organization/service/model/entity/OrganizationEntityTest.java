package org.sagebionetworks.openchallenges.organization.service.model.entity;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrganizationEntityTest {

  @Test
  public void
      OrganizationEntityBuilderArguments_ShouldReturnArgumentsPassed_WhenOrganizationEntityBuilt() {

    Long id = 1L;
    String name = "Test Organization";
    String email = "test@example.com";
    String login = "testorg";
    String avatarKey = "avatarKey";
    String websiteUrl = "https://example.com";
    Integer challengeCount = 5;
    String description = "Test description";
    OffsetDateTime createdAt = OffsetDateTime.now();
    OffsetDateTime updatedAt = OffsetDateTime.now();
    String acronym = "TO";

    OrganizationEntity organizationEntity =
        OrganizationEntity.builder()
            .id(id)
            .name(name)
            .email(email)
            .login(login)
            .avatarKey(avatarKey)
            .websiteUrl(websiteUrl)
            .challengeCount(challengeCount)
            .categories(Collections.emptyList())
            .challengeContributions(Collections.emptyList())
            .description(description)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .acronym(acronym)
            .build();
    Long retrievedId = organizationEntity.getId();
    String retrievedName = organizationEntity.getName();
    String retrievedEmail = organizationEntity.getEmail();
    String retrievedLogin = organizationEntity.getLogin();
    String retrievedAvatarKey = organizationEntity.getAvatarKey();
    String retrievedWebsiteUrl = organizationEntity.getWebsiteUrl();
    Integer retrievedChallengeCount = organizationEntity.getChallengeCount();
    List<OrganizationCategoryEntity> retrievedCategories = organizationEntity.getCategories();
    List<ChallengeContributionEntity> retrievedChallengeContributions =
        organizationEntity.getChallengeContributions();
    String retrievedDescription = organizationEntity.getDescription();
    OffsetDateTime retrievedCreatedAt = organizationEntity.getCreatedAt();
    OffsetDateTime retrievedUpdatedAt = organizationEntity.getUpdatedAt();
    String retrievedAcronym = organizationEntity.getAcronym();

    Assertions.assertEquals(id, retrievedId);
    Assertions.assertEquals(name, retrievedName);
    Assertions.assertEquals(email, retrievedEmail);
    Assertions.assertEquals(login, retrievedLogin);
    Assertions.assertEquals(avatarKey, retrievedAvatarKey);
    Assertions.assertEquals(websiteUrl, retrievedWebsiteUrl);
    Assertions.assertEquals(challengeCount, retrievedChallengeCount);
    Assertions.assertEquals(Collections.emptyList(), retrievedCategories);
    Assertions.assertEquals(Collections.emptyList(), retrievedChallengeContributions);
    Assertions.assertEquals(description, retrievedDescription);
    Assertions.assertEquals(createdAt, retrievedCreatedAt);
    Assertions.assertEquals(updatedAt, retrievedUpdatedAt);
    Assertions.assertEquals(acronym, retrievedAcronym);
  }

  @Test
  public void
      OrganizationEntityMethodsToSetArguments_ShouldReturnArguments_WhenOrganizationEntityArgumentsPassed() {

    OrganizationEntity organizationEntity = new OrganizationEntity();

    Long id = 1L;
    String name = "Test Organization";
    String email = "test@example.com";
    String login = "testorg";
    String avatarKey = "avatarKey";
    String websiteUrl = "https://example.com";
    Integer challengeCount = 5;
    String description = "Test description";
    OffsetDateTime createdAt = OffsetDateTime.now();
    OffsetDateTime updatedAt = OffsetDateTime.now();
    String acronym = "TO";

    organizationEntity.setAcronym(acronym);
    organizationEntity.setAvatarKey(avatarKey);
    organizationEntity.setChallengeCount(challengeCount);
    organizationEntity.setCreatedAt(createdAt);
    organizationEntity.setDescription(description);
    organizationEntity.setEmail(email);
    organizationEntity.setId(id);
    organizationEntity.setLogin(login);
    organizationEntity.setName(name);
    organizationEntity.setUpdatedAt(updatedAt);
    organizationEntity.setWebsiteUrl(websiteUrl);
    organizationEntity.setCategories(Collections.emptyList());
    organizationEntity.setChallengeContributions(Collections.emptyList());

    Long retrievedId = organizationEntity.getId();
    String retrievedName = organizationEntity.getName();
    String retrievedEmail = organizationEntity.getEmail();
    String retrievedLogin = organizationEntity.getLogin();
    String retrievedAvatarKey = organizationEntity.getAvatarKey();
    String retrievedWebsiteUrl = organizationEntity.getWebsiteUrl();
    Integer retrievedChallengeCount = organizationEntity.getChallengeCount();
    List<OrganizationCategoryEntity> retrievedCategories = organizationEntity.getCategories();
    List<ChallengeContributionEntity> retrievedChallengeContributions =
        organizationEntity.getChallengeContributions();
    String retrievedDescription = organizationEntity.getDescription();
    OffsetDateTime retrievedCreatedAt = organizationEntity.getCreatedAt();
    OffsetDateTime retrievedUpdatedAt = organizationEntity.getUpdatedAt();
    String retrievedAcronym = organizationEntity.getAcronym();

    Assertions.assertEquals(id, retrievedId);
    Assertions.assertEquals(name, retrievedName);
    Assertions.assertEquals(email, retrievedEmail);
    Assertions.assertEquals(login, retrievedLogin);
    Assertions.assertEquals(avatarKey, retrievedAvatarKey);
    Assertions.assertEquals(websiteUrl, retrievedWebsiteUrl);
    Assertions.assertEquals(challengeCount, retrievedChallengeCount);
    Assertions.assertEquals(Collections.emptyList(), retrievedCategories);
    Assertions.assertEquals(Collections.emptyList(), retrievedChallengeContributions);
    Assertions.assertEquals(description, retrievedDescription);
    Assertions.assertEquals(createdAt, retrievedCreatedAt);
    Assertions.assertEquals(updatedAt, retrievedUpdatedAt);
    Assertions.assertEquals(acronym, retrievedAcronym);
  }

  @Test
  public void HashCode_ShouldBuildHashCodeOfArgumentsForOrganizationEntity_WhenArgumentsPassed() {

    OffsetDateTime createdAt = OffsetDateTime.now();
    OffsetDateTime updatedAt = OffsetDateTime.now();

    OrganizationEntity organizationEntity = new OrganizationEntity();

    organizationEntity.setId(1L);
    organizationEntity.setName("Test Organization");
    organizationEntity.setEmail("test@example.com");
    organizationEntity.setLogin("testorg");
    organizationEntity.setDescription("Test description");
    organizationEntity.setAvatarKey("avatarKey");
    organizationEntity.setWebsiteUrl("https://example.com");
    organizationEntity.setChallengeCount(5);
    organizationEntity.setCreatedAt(createdAt);
    organizationEntity.setUpdatedAt(updatedAt);
    organizationEntity.setAcronym("TO");

    OrganizationEntity organizationEntity2 = new OrganizationEntity();
    organizationEntity2.setId(1L);
    organizationEntity2.setName("Test Organization");
    organizationEntity2.setEmail("test@example.com");
    organizationEntity2.setLogin("testorg");
    organizationEntity2.setDescription("Test description");
    organizationEntity2.setAvatarKey("avatarKey");
    organizationEntity2.setWebsiteUrl("https://example.com");
    organizationEntity2.setChallengeCount(5);
    organizationEntity2.setCreatedAt(createdAt);
    organizationEntity2.setUpdatedAt(updatedAt);
    organizationEntity2.setAcronym("TO");

    int hashCode1 = organizationEntity.hashCode();
    int hashCode2 = organizationEntity2.hashCode();

    Assertions.assertEquals(hashCode1, hashCode2);
  }

  @Test
  public void EqualsObject_ShouldReturnBoolean_WhenPassedTwoObjects() {

    OffsetDateTime createdAt = OffsetDateTime.now();
    OffsetDateTime updatedAt = OffsetDateTime.now();

    OrganizationEntity organizationEntity = new OrganizationEntity();
    organizationEntity.setId(1L);
    organizationEntity.setName("Test Organization");
    organizationEntity.setEmail("test@example.com");
    organizationEntity.setLogin("testorg");
    organizationEntity.setDescription("Test description");
    organizationEntity.setAvatarKey("avatarKey");
    organizationEntity.setWebsiteUrl("https://example.com");
    organizationEntity.setCreatedAt(createdAt);
    organizationEntity.setUpdatedAt(updatedAt);
    organizationEntity.setChallengeCount(5);
    organizationEntity.setAcronym("TO");

    OrganizationEntity organizationEntity2 = new OrganizationEntity();
    organizationEntity2.setId(1L);
    organizationEntity2.setName("Test Organization");
    organizationEntity2.setEmail("test@example.com");
    organizationEntity2.setLogin("testorg");
    organizationEntity2.setDescription("Test description");
    organizationEntity2.setAvatarKey("avatarKey");
    organizationEntity2.setWebsiteUrl("https://example.com");
    organizationEntity2.setChallengeCount(5);
    organizationEntity2.setCreatedAt(createdAt);
    organizationEntity2.setUpdatedAt(updatedAt);
    organizationEntity2.setAcronym("TO");

    OrganizationEntity organizationEntity3 = new OrganizationEntity();
    organizationEntity3.setId(2L);
    organizationEntity3.setName("Another Organization");
    organizationEntity3.setEmail("another@example.com");
    organizationEntity3.setLogin("anotherorg");
    organizationEntity3.setDescription("Another description");
    organizationEntity3.setAvatarKey("anotherAvatarKey");
    organizationEntity3.setWebsiteUrl("https://another-example.com");
    organizationEntity3.setChallengeCount(10);
    organizationEntity3.setCreatedAt(createdAt);
    organizationEntity3.setUpdatedAt(updatedAt);
    organizationEntity3.setAcronym("AO");

    boolean equals1 = organizationEntity.equals(organizationEntity2);
    boolean equals2 = organizationEntity2.equals(organizationEntity);
    boolean equals3 = organizationEntity.equals(organizationEntity3);

    Assertions.assertTrue(equals1);
    Assertions.assertTrue(equals2);
    Assertions.assertFalse(equals3);
  }
}
