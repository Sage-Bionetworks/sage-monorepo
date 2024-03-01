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
    organization = new OrganizationEntity();

    contributor1 = new OrganizationCategoryEntity();
    contributor1.setId(entityId1);
    contributor1.setCategory(category);
    contributor1.setOrganization(organization);

    contributor2 = new OrganizationCategoryEntity(entityId1, category, organization);

    contributor3 =
        OrganizationCategoryEntity.builder()
            .id(entityId2)
            .organization(organization)
            .category(category)
            .build();
  }

  @Test
  void getters_ShouldReturnExpectedValues_WhenArgumentsPassed() {
    // Verify the values are set when the setters are used to set them (NoArgsConstructor)
    Assertions.assertEquals(entityId1, contributor1.getId(), "ID should match for contributor1");
    Assertions.assertEquals(
        organization, contributor1.getOrganization(), "Organization should match for contributor1");
    Assertions.assertEquals(
        category, contributor1.getCategory(), "Category should match for contributor1");

    // Verify the values are set when set with the ArgsConstructor
    Assertions.assertEquals(entityId1, contributor2.getId(), "ID should match for contributor2");
    Assertions.assertEquals(
        organization, contributor2.getOrganization(), "Organization should match for contributor2");
    Assertions.assertEquals(
        category, contributor2.getCategory(), "Category should match for contributor2");
  }

  @Test
  void shouldReturnArgumentsPassed_WhenOrganizationCategoryEntityBuiltWithClassBuilder() {
    // Verify the values are set
    Assertions.assertEquals(entityId2, contributor3.getId(), "ID should match for contributor3");
    Assertions.assertEquals(
        organization, contributor3.getOrganization(), "Organization should match for contributor3");
    Assertions.assertEquals(
        category, contributor3.getCategory(), "Category should match for contributor3");
  }

  @Test
  void shouldReturnSameOrDifferent_WhenTwoOrganizationCategoryEntityCompared() {
    // Verify the generated equals() method
    Assertions.assertEquals(contributor1, contributor2, "Contributors 1 and 2 should be equal");
    Assertions.assertNotEquals(
        contributor1, contributor3, "Contributors 1 and 3 should not be equal");
  }

  @Test
  void hashCode_ShouldBeTheSameOrDifferent_WhenTwoOrganizationCategoryEntityCompared() {
    // Verify the generated hashCode() method
    Assertions.assertEquals(
        contributor1.hashCode(),
        contributor2.hashCode(),
        "Hash codes of contributors 1 and 2 should match");
    Assertions.assertNotEquals(
        contributor1.hashCode(),
        contributor3.hashCode(),
        "Hash codes of contributors 1 and 3 should not match");
  }
}
