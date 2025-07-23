package org.sagebionetworks.openchallenges.organization.service.model.entity;

import java.time.OffsetDateTime;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrganizationEntityTest {

  private Long id = 1L;
  private String name = "Test Organization";
  private String avatarKey = "avatarKey";
  private String websiteUrl = "https://example.com";
  private Integer challengeCount = 5;
  private String description = "Test description";
  private OffsetDateTime createdAt = OffsetDateTime.now();
  private OffsetDateTime updatedAt = OffsetDateTime.now();
  private String acronym = "TO";
  private OrganizationEntity entityFromConstructor;
  private OrganizationEntity entityFromConstructor2;
  private OrganizationEntity entityFromConstructor3;

  @BeforeEach
  public void setup() {
    entityFromConstructor = new OrganizationEntity();

    entityFromConstructor.setId(id);
    entityFromConstructor.setName(name);
    entityFromConstructor.setDescription(description);
    entityFromConstructor.setAvatarKey(avatarKey);
    entityFromConstructor.setWebsiteUrl(websiteUrl);
    entityFromConstructor.setChallengeCount(challengeCount);
    entityFromConstructor.setCreatedAt(createdAt);
    entityFromConstructor.setUpdatedAt(updatedAt);
    entityFromConstructor.setAcronym(acronym);
    entityFromConstructor.setCategories(Collections.emptyList());
    entityFromConstructor.setChallengeParticipations(Collections.emptyList());

    entityFromConstructor2 = new OrganizationEntity();

    entityFromConstructor2.setId(id);
    entityFromConstructor2.setName(name);
    entityFromConstructor2.setDescription(description);
    entityFromConstructor2.setAvatarKey(avatarKey);
    entityFromConstructor2.setWebsiteUrl(websiteUrl);
    entityFromConstructor2.setChallengeCount(challengeCount);
    entityFromConstructor2.setCreatedAt(createdAt);
    entityFromConstructor2.setUpdatedAt(updatedAt);
    entityFromConstructor2.setAcronym(acronym);
    entityFromConstructor2.setCategories(Collections.emptyList());
    entityFromConstructor2.setChallengeParticipations(Collections.emptyList());

    entityFromConstructor3 = new OrganizationEntity();

    entityFromConstructor3.setId(2L);
    entityFromConstructor3.setName("Another Organization");
    entityFromConstructor3.setDescription("Another description");
    entityFromConstructor3.setAvatarKey("anotherAvatarKey");
    entityFromConstructor3.setWebsiteUrl("https://another-example.com");
    entityFromConstructor3.setChallengeCount(10);
    entityFromConstructor3.setCreatedAt(createdAt);
    entityFromConstructor3.setUpdatedAt(updatedAt);
    entityFromConstructor3.setAcronym("AO");
    entityFromConstructor3.setCategories(Collections.emptyList());
    entityFromConstructor3.setChallengeParticipations(Collections.emptyList());
  }

  @Test
  public void OrganizationEntity_ShouldReturnArgumentsPassed_WhenOrganizationEntityBuiltWithClassBuilder() {
    OrganizationEntity entity = OrganizationEntity.builder()
      .id(id)
      .name(name)
      .avatarKey(avatarKey)
      .websiteUrl(websiteUrl)
      .challengeCount(challengeCount)
      .categories(Collections.emptyList())
      .challengeParticipations(Collections.emptyList())
      .description(description)
      .createdAt(createdAt)
      .updatedAt(updatedAt)
      .acronym(acronym)
      .build();

    Assertions.assertEquals(id, entity.getId());
    Assertions.assertEquals(name, entity.getName());
    Assertions.assertEquals(avatarKey, entity.getAvatarKey());
    Assertions.assertEquals(websiteUrl, entity.getWebsiteUrl());
    Assertions.assertEquals(challengeCount, entity.getChallengeCount());
    Assertions.assertEquals(Collections.emptyList(), entity.getCategories());
    Assertions.assertEquals(Collections.emptyList(), entity.getChallengeParticipations());
    Assertions.assertEquals(description, entity.getDescription());
    Assertions.assertEquals(createdAt, entity.getCreatedAt());
    Assertions.assertEquals(updatedAt, entity.getUpdatedAt());
    Assertions.assertEquals(acronym, entity.getAcronym());
  }

  @Test
  public void OrganizationEntityGetters_ShouldReturnExpectedValues_WhenArgumentsPassed() {
    Assertions.assertEquals(id, entityFromConstructor.getId());
    Assertions.assertEquals(name, entityFromConstructor.getName());
    Assertions.assertEquals(avatarKey, entityFromConstructor.getAvatarKey());
    Assertions.assertEquals(websiteUrl, entityFromConstructor.getWebsiteUrl());
    Assertions.assertEquals(challengeCount, entityFromConstructor.getChallengeCount());
    Assertions.assertEquals(Collections.emptyList(), entityFromConstructor.getCategories());
    Assertions.assertEquals(
      Collections.emptyList(),
      entityFromConstructor.getChallengeParticipations()
    );
    Assertions.assertEquals(description, entityFromConstructor.getDescription());
    Assertions.assertEquals(createdAt, entityFromConstructor.getCreatedAt());
    Assertions.assertEquals(updatedAt, entityFromConstructor.getUpdatedAt());
    Assertions.assertEquals(acronym, entityFromConstructor.getAcronym());
  }

  @Test
  public void HashCode_ShouldBuildHashCodeOfOrganizationEntity_WhenArgumentsPassed() {
    int hashCode1 = entityFromConstructor.hashCode();
    int hashCode2 = entityFromConstructor2.hashCode();

    Assertions.assertEquals(hashCode1, hashCode2);
  }

  @Test
  public void EqualsObject_ShouldBeTheSame_WhenPassedTwoIdenticalObjects() {
    boolean equals1 = entityFromConstructor.equals(entityFromConstructor2);
    boolean equals2 = entityFromConstructor2.equals(entityFromConstructor);

    Assertions.assertTrue(equals1);
    Assertions.assertTrue(equals2);
  }

  @Test
  public void EqualsObject_ShouldBeDifferent_WhenPassedTwoDifferentObjects() {
    boolean equals3 = entityFromConstructor.equals(entityFromConstructor3);

    Assertions.assertFalse(equals3);
  }
}
