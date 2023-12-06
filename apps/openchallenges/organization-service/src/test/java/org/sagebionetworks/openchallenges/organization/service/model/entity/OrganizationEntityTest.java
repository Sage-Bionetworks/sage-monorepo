package org.sagebionetworks.openchallenges.organization.service.model.entity;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class OrganizationEntityTest {

  private static Long id;
  private static String name;
  private static String email;
  private static String login;
  private static String avatarKey;
  private static String websiteUrl;
  private static Integer challengeCount;
  private static String description;
  private static OffsetDateTime createdAt;
  private static OffsetDateTime updatedAt;
  private static String acronym;

  @BeforeAll
  public static void setup() {
    id = 1L;
    name = "Test Organization";
    email = "test@example.com";
    login = "testorg";
    avatarKey = "avatarKey";
    websiteUrl = "https://example.com";
    challengeCount = 5;
    description = "Test description";
    createdAt = OffsetDateTime.now();
    updatedAt = OffsetDateTime.now();
    acronym = "TO";
  }

  @Test
  public void
      OrganizationEntityBuilderArguments_ShouldReturnArgumentsPassed_WhenOrganizationEntityBuilt() {

    OrganizationEntity entity =
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

    Long actualId = entity.getId();
    String actualName = entity.getName();
    String actualEmail = entity.getEmail();
    String actualLogin = entity.getLogin();
    String actualAvatarKey = entity.getAvatarKey();
    String actualWebsiteUrl = entity.getWebsiteUrl();
    Integer actualChallengeCount = entity.getChallengeCount();
    List<OrganizationCategoryEntity> actualCategories = entity.getCategories();
    List<ChallengeContributionEntity> actualChallengeContributions =
        entity.getChallengeContributions();
    String actualDescription = entity.getDescription();
    OffsetDateTime actualCreatedAt = entity.getCreatedAt();
    OffsetDateTime actualUpdatedAt = entity.getUpdatedAt();
    String actualAcronym = entity.getAcronym();

    Assertions.assertEquals(id, actualId);
    Assertions.assertEquals(name, actualName);
    Assertions.assertEquals(email, actualEmail);
    Assertions.assertEquals(login, actualLogin);
    Assertions.assertEquals(avatarKey, actualAvatarKey);
    Assertions.assertEquals(websiteUrl, actualWebsiteUrl);
    Assertions.assertEquals(challengeCount, actualChallengeCount);
    Assertions.assertEquals(Collections.emptyList(), actualCategories);
    Assertions.assertEquals(Collections.emptyList(), actualChallengeContributions);
    Assertions.assertEquals(description, actualDescription);
    Assertions.assertEquals(createdAt, actualCreatedAt);
    Assertions.assertEquals(updatedAt, actualUpdatedAt);
    Assertions.assertEquals(acronym, actualAcronym);
  }

  @Test
  public void
      OrganizationEntityMethodsToSetArguments_ShouldReturnArguments_WhenOrganizationEntityArgumentsPassed() {

    OrganizationEntity entity = new OrganizationEntity();

    entity.setAcronym(acronym);
    entity.setAvatarKey(avatarKey);
    entity.setChallengeCount(challengeCount);
    entity.setCreatedAt(createdAt);
    entity.setDescription(description);
    entity.setEmail(email);
    entity.setId(id);
    entity.setLogin(login);
    entity.setName(name);
    entity.setUpdatedAt(updatedAt);
    entity.setWebsiteUrl(websiteUrl);
    entity.setCategories(Collections.emptyList());
    entity.setChallengeContributions(Collections.emptyList());

    Long actualId = entity.getId();
    String actualName = entity.getName();
    String actualEmail = entity.getEmail();
    String actualLogin = entity.getLogin();
    String actualAvatarKey = entity.getAvatarKey();
    String actualWebsiteUrl = entity.getWebsiteUrl();
    Integer actualChallengeCount = entity.getChallengeCount();
    List<OrganizationCategoryEntity> actualCategories = entity.getCategories();
    List<ChallengeContributionEntity> actualChallengeContributions =
        entity.getChallengeContributions();
    String actualDescription = entity.getDescription();
    OffsetDateTime actualCreatedAt = entity.getCreatedAt();
    OffsetDateTime actualUpdatedAt = entity.getUpdatedAt();
    String actualAcronym = entity.getAcronym();

    Assertions.assertEquals(id, actualId);
    Assertions.assertEquals(name, actualName);
    Assertions.assertEquals(email, actualEmail);
    Assertions.assertEquals(login, actualLogin);
    Assertions.assertEquals(avatarKey, actualAvatarKey);
    Assertions.assertEquals(websiteUrl, actualWebsiteUrl);
    Assertions.assertEquals(challengeCount, actualChallengeCount);
    Assertions.assertEquals(Collections.emptyList(), actualCategories);
    Assertions.assertEquals(Collections.emptyList(), actualChallengeContributions);
    Assertions.assertEquals(description, actualDescription);
    Assertions.assertEquals(createdAt, actualCreatedAt);
    Assertions.assertEquals(updatedAt, actualUpdatedAt);
    Assertions.assertEquals(acronym, actualAcronym);
  }

  @Test
  public void HashCode_ShouldBuildHashCodeOfArgumentsForOrganizationEntity_WhenArgumentsPassed() {

    OrganizationEntity organizationEntity = new OrganizationEntity();

    organizationEntity.setId(id);
    organizationEntity.setName(name);
    organizationEntity.setEmail(email);
    organizationEntity.setLogin(login);
    organizationEntity.setDescription(description);
    organizationEntity.setAvatarKey(avatarKey);
    organizationEntity.setWebsiteUrl(websiteUrl);
    organizationEntity.setChallengeCount(challengeCount);
    organizationEntity.setCreatedAt(createdAt);
    organizationEntity.setUpdatedAt(updatedAt);
    organizationEntity.setAcronym(acronym);

    OrganizationEntity organizationEntity2 = new OrganizationEntity();
    organizationEntity2.setId(id);
    organizationEntity2.setName(name);
    organizationEntity2.setEmail(email);
    organizationEntity2.setLogin(login);
    organizationEntity2.setDescription(description);
    organizationEntity2.setAvatarKey(avatarKey);
    organizationEntity2.setWebsiteUrl(websiteUrl);
    organizationEntity2.setChallengeCount(challengeCount);
    organizationEntity2.setCreatedAt(createdAt);
    organizationEntity2.setUpdatedAt(updatedAt);
    organizationEntity2.setAcronym(acronym);

    int hashCode1 = organizationEntity.hashCode();
    int hashCode2 = organizationEntity2.hashCode();

    Assertions.assertEquals(hashCode1, hashCode2);
  }

  @Test
  public void EqualsObject_ShouldReturnBoolean_WhenPassedTwoObjects() {

    // OffsetDateTime createdAt = OffsetDateTime.now();
    // OffsetDateTime updatedAt = OffsetDateTime.now();

    OrganizationEntity organizationEntity = new OrganizationEntity();
    organizationEntity.setId(id);
    organizationEntity.setName(name);
    organizationEntity.setEmail(email);
    organizationEntity.setLogin(login);
    organizationEntity.setDescription(description);
    organizationEntity.setAvatarKey(avatarKey);
    organizationEntity.setWebsiteUrl(websiteUrl);
    organizationEntity.setChallengeCount(challengeCount);
    organizationEntity.setCreatedAt(createdAt);
    organizationEntity.setUpdatedAt(updatedAt);
    organizationEntity.setAcronym(acronym);

    OrganizationEntity organizationEntity2 = new OrganizationEntity();
    organizationEntity2.setId(id);
    organizationEntity2.setName(name);
    organizationEntity2.setEmail(email);
    organizationEntity2.setLogin(login);
    organizationEntity2.setDescription(description);
    organizationEntity2.setAvatarKey(avatarKey);
    organizationEntity2.setWebsiteUrl(websiteUrl);
    organizationEntity2.setChallengeCount(challengeCount);
    organizationEntity2.setCreatedAt(createdAt);
    organizationEntity2.setUpdatedAt(updatedAt);
    organizationEntity2.setAcronym(acronym);

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
