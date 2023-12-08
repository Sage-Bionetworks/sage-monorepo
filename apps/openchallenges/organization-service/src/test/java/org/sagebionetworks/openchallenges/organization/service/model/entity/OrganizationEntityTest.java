package org.sagebionetworks.openchallenges.organization.service.model.entity;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
  private static OrganizationEntity entityFromConstructor;
  private static OrganizationEntity entityFromConstructor2;

  @BeforeEach
  public void setup() {
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

    entityFromConstructor = new OrganizationEntity();

    entityFromConstructor.setId(id);
    entityFromConstructor.setName(name);
    entityFromConstructor.setEmail(email);
    entityFromConstructor.setLogin(login);
    entityFromConstructor.setDescription(description);
    entityFromConstructor.setAvatarKey(avatarKey);
    entityFromConstructor.setWebsiteUrl(websiteUrl);
    entityFromConstructor.setChallengeCount(challengeCount);
    entityFromConstructor.setCreatedAt(createdAt);
    entityFromConstructor.setUpdatedAt(updatedAt);
    entityFromConstructor.setAcronym(acronym);
    entityFromConstructor.setCategories(Collections.emptyList());
    entityFromConstructor.setChallengeContributions(Collections.emptyList());

    entityFromConstructor2 = new OrganizationEntity();

    entityFromConstructor2.setId(id);
    entityFromConstructor2.setName(name);
    entityFromConstructor2.setEmail(email);
    entityFromConstructor2.setLogin(login);
    entityFromConstructor2.setDescription(description);
    entityFromConstructor2.setAvatarKey(avatarKey);
    entityFromConstructor2.setWebsiteUrl(websiteUrl);
    entityFromConstructor2.setChallengeCount(challengeCount);
    entityFromConstructor2.setCreatedAt(createdAt);
    entityFromConstructor2.setUpdatedAt(updatedAt);
    entityFromConstructor2.setAcronym(acronym);
    entityFromConstructor2.setCategories(Collections.emptyList());
    entityFromConstructor2.setChallengeContributions(Collections.emptyList());
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

    Assertions.assertEquals(id, entity.getId());
    Assertions.assertEquals(name, entity.getName());
    Assertions.assertEquals(email, entity.getEmail());
    Assertions.assertEquals(login, entity.getLogin());
    Assertions.assertEquals(avatarKey, entity.getAvatarKey());
    Assertions.assertEquals(websiteUrl, entity.getWebsiteUrl());
    Assertions.assertEquals(challengeCount, entity.getChallengeCount());
    Assertions.assertEquals(Collections.emptyList(), entity.getCategories());
    Assertions.assertEquals(Collections.emptyList(), entity.getChallengeContributions());
    Assertions.assertEquals(description, entity.getDescription());
    Assertions.assertEquals(createdAt, entity.getCreatedAt());
    Assertions.assertEquals(updatedAt, entity.getUpdatedAt());
    Assertions.assertEquals(acronym, entity.getAcronym());
  }

  @Test
  public void
      OrganizationEntityMethodsToSetArguments_ShouldReturnArguments_WhenOrganizationEntityArgumentsPassed() {

    Assertions.assertEquals(id, entityFromConstructor.getId());
    Assertions.assertEquals(name, entityFromConstructor.getName());
    Assertions.assertEquals(email, entityFromConstructor.getEmail());
    Assertions.assertEquals(login, entityFromConstructor.getLogin());
    Assertions.assertEquals(avatarKey, entityFromConstructor.getAvatarKey());
    Assertions.assertEquals(websiteUrl, entityFromConstructor.getWebsiteUrl());
    Assertions.assertEquals(challengeCount, entityFromConstructor.getChallengeCount());
    Assertions.assertEquals(Collections.emptyList(), entityFromConstructor.getCategories());
    Assertions.assertEquals(Collections.emptyList(), entityFromConstructor.getChallengeContributions());
    Assertions.assertEquals(description, entityFromConstructor.getDescription());
    Assertions.assertEquals(createdAt, entityFromConstructor.getCreatedAt());
    Assertions.assertEquals(updatedAt, entityFromConstructor.getUpdatedAt());
    Assertions.assertEquals(acronym, entityFromConstructor.getAcronym());
  }

  @Test
  public void HashCode_ShouldBuildHashCodeOfArgumentsForOrganizationEntity_WhenArgumentsPassed() {

    int hashCode1 = entityFromConstructor.hashCode();
    int hashCode2 = entityFromConstructor2.hashCode();

    Assertions.assertEquals(hashCode1, hashCode2);
  }

  @Test
  public void EqualsObject_ShouldReturnBoolean_WhenPassedTwoObjects() {

    OrganizationEntity entityFromConstructor3 = new OrganizationEntity();
    entityFromConstructor3.setId(2L);
    entityFromConstructor3.setName("Another Organization");
    entityFromConstructor3.setEmail("another@example.com");
    entityFromConstructor3.setLogin("anotherorg");
    entityFromConstructor3.setDescription("Another description");
    entityFromConstructor3.setAvatarKey("anotherAvatarKey");
    entityFromConstructor3.setWebsiteUrl("https://another-example.com");
    entityFromConstructor3.setChallengeCount(10);
    entityFromConstructor3.setCreatedAt(createdAt);
    entityFromConstructor3.setUpdatedAt(updatedAt);
    entityFromConstructor3.setAcronym("AO");
    entityFromConstructor3.setCategories(Collections.emptyList());
    entityFromConstructor3.setChallengeContributions(Collections.emptyList());

    boolean equals1 = entityFromConstructor.equals(entityFromConstructor2);
    boolean equals2 = entityFromConstructor2.equals(entityFromConstructor);
    boolean equals3 = entityFromConstructor.equals(entityFromConstructor3);

    Assertions.assertTrue(equals1);
    Assertions.assertTrue(equals2);
    Assertions.assertFalse(equals3);
  }
}
