package org.sagebionetworks.openchallenges.organization.service.model.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrganizationCategoryEntityTest {
  private OrganizationEntity organization;
  private OrganizationCategoryEntity contributor1;
  private OrganizationCategoryEntity contributor2;
  private OrganizationCategoryEntity contributor3;
  private Long entityId1 = 1L;
  private Long entityId2 = 2L;
  private String category = "category";

  @BeforeEach
  void setup() {
    // Create an OrganizationEntity
    organization = new OrganizationEntity();

    // Create different OrganizationCategoryEntity using different methods
    contributor1 = new OrganizationCategoryEntity();
    contributor1.setId(1L);
    contributor1.setCategory("category");
    contributor1.setOrganization(organization);

    contributor2 = new OrganizationCategoryEntity(1L, "category", organization);

    contributor3 =
        OrganizationCategoryEntity.builder()
            .id(2L)
            .organization(organization)
            .category(category)
            .build();
  }

  @Test
  void OrganizationCategoryEntityGetters_ShouldReturnExpectedValues_WhenArgumentsPassed() {

    // Verify the values are set when the setters are used to set them (NoArgsConstructor)
    Assertions.assertEquals(entityId1, contributor1.getId());
    Assertions.assertEquals(organization, contributor1.getOrganization());
    Assertions.assertEquals(category, contributor1.getCategory());

    // Verify the values are set when set with the ArgsConstructor
    Assertions.assertEquals(entityId1, contributor2.getId());
    Assertions.assertEquals(organization, contributor2.getOrganization());
    Assertions.assertEquals(category, contributor2.getCategory());
  }

  @Test
  void
      OrganizationCategoryEntity_ShouldReturnArgumentsPassed_WhenOrganizationCategoryEntityBuiltWithClassBuilder() {

    // Verify the values are set
    Assertions.assertEquals(entityId2, contributor3.getId());
    Assertions.assertEquals(organization, contributor3.getOrganization());
    Assertions.assertEquals(category, contributor3.getCategory());
  }

  @Test
  void
      OrganizationCategoryEntity_ShouldBeTheSameOrDifferent_WhenTwoOrganizationCategoryEntityCompared() {

    // Verify the generated equals() method
    Assertions.assertEquals(contributor1, contributor2);
    Assertions.assertNotEquals(contributor1, contributor3);
  }

  @Test
  void HashCode_ShouldBeTheSameOrDifferent_WhenTwoOrganizationCategoryEntityCompared() {
    // Verify the generated hashCode() method
    Assertions.assertEquals(contributor1.hashCode(), contributor2.hashCode());
    Assertions.assertNotEquals(contributor1.hashCode(), contributor3.hashCode());
  }
}
