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

    OrganizationEntity entity = new OrganizationEntity();

    entity.setId(id);
    entity.setName(name);
    entity.setEmail(email);
    entity.setLogin(login);
    entity.setDescription(description);
    entity.setAvatarKey(avatarKey);
    entity.setWebsiteUrl(websiteUrl);
    entity.setChallengeCount(challengeCount);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);
    entity.setAcronym(acronym);

    OrganizationEntity entity2 = new OrganizationEntity();
    entity2.setId(id);
    entity2.setName(name);
    entity2.setEmail(email);
    entity2.setLogin(login);
    entity2.setDescription(description);
    entity2.setAvatarKey(avatarKey);
    entity2.setWebsiteUrl(websiteUrl);
    entity2.setChallengeCount(challengeCount);
    entity2.setCreatedAt(createdAt);
    entity2.setUpdatedAt(updatedAt);
    entity2.setAcronym(acronym);

    int hashCode1 = entity.hashCode();
    int hashCode2 = entity2.hashCode();

    Assertions.assertEquals(hashCode1, hashCode2);
  }

  @Test
  public void EqualsObject_ShouldReturnBoolean_WhenPassedTwoObjects() {

    OrganizationEntity entity = new OrganizationEntity();

    entity.setId(id);
    entity.setName(name);
    entity.setEmail(email);
    entity.setLogin(login);
    entity.setDescription(description);
    entity.setAvatarKey(avatarKey);
    entity.setWebsiteUrl(websiteUrl);
    entity.setChallengeCount(challengeCount);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);
    entity.setAcronym(acronym);

    OrganizationEntity entity2 = new OrganizationEntity();
    entity2.setId(id);
    entity2.setName(name);
    entity2.setEmail(email);
    entity2.setLogin(login);
    entity2.setDescription(description);
    entity2.setAvatarKey(avatarKey);
    entity2.setWebsiteUrl(websiteUrl);
    entity2.setChallengeCount(challengeCount);
    entity2.setCreatedAt(createdAt);
    entity2.setUpdatedAt(updatedAt);
    entity2.setAcronym(acronym);

    OrganizationEntity entity3 = new OrganizationEntity();
    entity3.setId(2L);
    entity3.setName("Another Organization");
    entity3.setEmail("another@example.com");
    entity3.setLogin("anotherorg");
    entity3.setDescription("Another description");
    entity3.setAvatarKey("anotherAvatarKey");
    entity3.setWebsiteUrl("https://another-example.com");
    entity3.setChallengeCount(10);
    entity3.setCreatedAt(createdAt);
    entity3.setUpdatedAt(updatedAt);
    entity3.setAcronym("AO");

    boolean equals1 = entity.equals(entity2);
    boolean equals2 = entity2.equals(entity);
    boolean equals3 = entity.equals(entity3);

    Assertions.assertTrue(equals1);
    Assertions.assertTrue(equals2);
    Assertions.assertFalse(equals3);
  }
}
