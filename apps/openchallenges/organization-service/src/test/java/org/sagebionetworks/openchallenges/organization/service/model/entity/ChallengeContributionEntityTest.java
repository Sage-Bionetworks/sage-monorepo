package org.sagebionetworks.openchallenges.organization.service.model.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChallengeContributionEntityTest {

  private OrganizationEntity organization;
  private ChallengeContributionEntity contributor1;
  private ChallengeContributionEntity contributor2;
  private ChallengeContributionEntity contributor3;
  private Long entityId1 = 1L;
  private Long entityId2 = 2L;
  private String role = "role";

  @BeforeEach
  void setup() {
    // Create an OrganizationEntity
    organization = new OrganizationEntity();

    // Create different ChallengeContributionEntity using different methods
    contributor1 = new ChallengeContributionEntity();
    contributor1.setId(entityId1);
    contributor1.setRole(role);
    contributor1.setOrganization(organization);

    contributor2 = new ChallengeContributionEntity(entityId1, role, organization);

    contributor3 =
        ChallengeContributionEntity.builder()
            .id(entityId2)
            .organization(organization)
            .role(role)
            .build();
  }

  @Test
  void getters_ShouldReturnExpectedValues_WhenArgumentsPassed() {
    // Verify the values are set when the setters are used to set them (NoArgsConstructor)
    Assertions.assertEquals(entityId1, contributor1.getId());
    Assertions.assertEquals(organization, contributor1.getOrganization());
    Assertions.assertEquals(role, contributor1.getRole());

    // Verify the values are set with the ArgsConstructor
    Assertions.assertEquals(entityId1, contributor2.getId());
    Assertions.assertEquals(organization, contributor2.getOrganization());
    Assertions.assertEquals(role, contributor2.getRole());
  }

  @Test
  void getters_ShouldReturnArgumentsPassed_WhenBuiltWithBuilder() {
    // Verify the values are set
    Assertions.assertEquals(entityId2, contributor3.getId());
    Assertions.assertEquals(organization, contributor3.getOrganization());
    Assertions.assertEquals(role, contributor3.getRole());
  }

  @Test
  void shouldBeTheSameOrDifferent_WhenTwoChallengeContributionEntitiesCompared() {
    // Verify the generated equals() method
    Assertions.assertEquals(contributor1, contributor2);
    Assertions.assertNotEquals(contributor1, contributor3);
  }

  @Test
  void hashCode_ShouldBeTheSameOrDifferent_WhenTwoChallengeContributionEntitiesCompared() {
    // Verify the generated hashCode() method
    Assertions.assertEquals(contributor1.hashCode(), contributor2.hashCode());
    Assertions.assertNotEquals(contributor1.hashCode(), contributor3.hashCode());
  }
}
