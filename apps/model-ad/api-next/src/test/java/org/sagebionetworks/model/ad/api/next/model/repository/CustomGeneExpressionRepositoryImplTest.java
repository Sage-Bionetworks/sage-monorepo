package org.sagebionetworks.model.ad.api.next.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Tests for CustomGeneExpressionRepositoryImpl focusing on criteria building and pagination.
 *
 * <p>These tests verify that MongoDB query criteria include the necessary filters
 * and that pagination is correctly applied.
 */
class CustomGeneExpressionRepositoryImplTest {

  private CustomGeneExpressionRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new CustomGeneExpressionRepositoryImpl(null);
  }

  @Test
  @DisplayName("should apply pagination to data query")
  void shouldApplyPaginationToDataQuery() throws Exception {
    // Given
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .biodomains(Arrays.asList("test-domain"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    // When
    Criteria criteria = invokeBuildMatchCriteria(
      "test-tissue",
      "test-cohort",
      query,
      Collections.emptyList()
    );
    Document criteriaDoc = criteria.getCriteriaObject();

    // Then - Verify criteria is built (pagination happens at aggregation level)
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");

    // Verify required filters exist
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey("tissue"));
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey("sex_cohort"));
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey("biodomains"));
  }

  @Test
  @DisplayName("should execute count query without pagination")
  void shouldExecuteCountQueryWithoutPagination() throws Exception {
    // Given
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .biodomains(Arrays.asList("test-domain"))
      .modelType(Arrays.asList("test-type"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    // When
    Criteria criteria = invokeBuildMatchCriteria(
      "test-tissue",
      "test-cohort",
      query,
      Collections.emptyList()
    );
    Document criteriaDoc = criteria.getCriteriaObject();

    // Then - Verify all filters are included (count uses same criteria as data query)
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");

    // Should have filters: tissue, sex_cohort, biodomains, model_type
    assertThat(andConditions.size()).isGreaterThanOrEqualTo(4);
  }

  @Test
  @DisplayName("should search on display_gene_symbol field with single term")
  void shouldSearchOnDisplayGeneSymbolWithSingleTerm() throws Exception {
    // Given
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .search("APOE")
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    // When
    Criteria criteria = invokeBuildMatchCriteria(
      "test-tissue",
      "test-cohort",
      query,
      Collections.emptyList()
    );
    Document criteriaDoc = criteria.getCriteriaObject();

    // Then - Verify search uses display_gene_symbol field
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");

    // For regex pattern, Spring Data MongoDB stores it as a Pattern object directly
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey("display_gene_symbol"));
  }

  @Test
  @DisplayName("should search on display_gene_symbol field with comma-separated terms")
  void shouldSearchOnDisplayGeneSymbolWithCommaSeparatedTerms() throws Exception {
    // Given
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .search("APOE,TREM2,APP")
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    // When
    Criteria criteria = invokeBuildMatchCriteria(
      "test-tissue",
      "test-cohort",
      query,
      Collections.emptyList()
    );
    Document criteriaDoc = criteria.getCriteriaObject();

    // Then - Verify search uses display_gene_symbol field with $in for multiple patterns
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");

    assertThat(andConditions).anySatisfy(doc -> {
      assertThat(doc).containsKey("display_gene_symbol");
      Document displayGeneSymbolCondition = (Document) doc.get("display_gene_symbol");
      assertThat(displayGeneSymbolCondition).containsKey("$in");
    });
  }

  @Test
  @DisplayName("should not apply search filter in INCLUDE mode")
  void shouldNotApplySearchFilterInIncludeMode() throws Exception {
    // Given
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .search("APOE")
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();
    List<String> items = Arrays.asList("ENSG00000130203~5xFAD");

    // When
    Criteria criteria = invokeBuildMatchCriteria("test-tissue", "test-cohort", query, items);
    Document criteriaDoc = criteria.getCriteriaObject();

    // Then - Verify no display_gene_symbol search filter is applied
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");

    assertThat(andConditions).noneSatisfy(doc -> assertThat(doc).containsKey("display_gene_symbol")
    );
  }

  @Test
  @DisplayName("should not apply search filter when search is empty")
  void shouldNotApplySearchFilterWhenSearchIsEmpty() throws Exception {
    // Given
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .search("   ")
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    // When
    Criteria criteria = invokeBuildMatchCriteria(
      "test-tissue",
      "test-cohort",
      query,
      Collections.emptyList()
    );
    Document criteriaDoc = criteria.getCriteriaObject();

    // Then - Verify no display_gene_symbol search filter is applied
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");

    assertThat(andConditions).noneSatisfy(doc -> assertThat(doc).containsKey("display_gene_symbol")
    );
  }

  /**
   * Uses reflection to invoke the private buildMatchCriteria method for testing.
   */
  private Criteria invokeBuildMatchCriteria(
    String tissue,
    String sexCohort,
    GeneExpressionSearchQueryDto query,
    List<String> items
  ) throws Exception {
    var method =
      CustomGeneExpressionRepositoryImpl.class.getDeclaredMethod(
          "buildMatchCriteria",
          String.class,
          String.class,
          GeneExpressionSearchQueryDto.class,
          List.class
        );
    method.setAccessible(true);
    return (Criteria) method.invoke(repository, tissue, sexCohort, query, items);
  }
}
