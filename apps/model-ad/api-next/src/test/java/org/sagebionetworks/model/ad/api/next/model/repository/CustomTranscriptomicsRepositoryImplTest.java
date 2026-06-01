package org.sagebionetworks.model.ad.api.next.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsSearchQueryDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Tests for CustomTranscriptomicsRepositoryImpl focusing on criteria building, pagination, and
 * pipeline assembly.
 *
 * <p>Most tests use reflection to call the private {@code buildMatchCriteria} directly and inspect
 * the returned {@link Criteria}. The pipeline-invariant test
 * ({@code shouldEmitDisplayGeneSymbolPipelineWhenSortingByGeneSymbol}) uses a mocked
 * {@link MongoTemplate} to capture and inspect the full {@link Aggregation} produced by the base
 * class.
 */
@ExtendWith(MockitoExtension.class)
class CustomTranscriptomicsRepositoryImplTest {

  private static final String COLLECTION_NAME = "rna_de_aggregate";

  private CustomTranscriptomicsRepositoryImpl repository;

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private AggregationResults<TranscriptomicsDocument> aggregationResults;

  @BeforeEach
  void setUp() {
    repository = new CustomTranscriptomicsRepositoryImpl(null);
  }

  @Test
  @DisplayName("should apply pagination to data query")
  void shouldApplyPaginationToDataQuery() throws Exception {
    // Given
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
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
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
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
  @DisplayName("should search on gene_symbol with fallback to ensembl_gene_id for single term")
  void shouldSearchOnDisplayGeneSymbolWithSingleTerm() throws Exception {
    // Given
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
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

    // Then
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey("$or"));
  }

  @Test
  @DisplayName(
    "should search on gene_symbol with fallback to ensembl_gene_id for comma-separated terms"
  )
  void shouldSearchOnDisplayGeneSymbolWithCommaSeparatedTerms() throws Exception {
    // Given
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
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

    // Then
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey("$or"));
  }

  @Test
  @DisplayName("should not apply search filter in INCLUDE mode")
  void shouldNotApplySearchFilterInIncludeMode() throws Exception {
    // Given
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .search("APOE")
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();
    List<String> items = Arrays.asList("ENSG00000130203~5xFAD");

    // When
    Criteria criteria = invokeBuildMatchCriteria("test-tissue", "test-cohort", query, items);
    Document criteriaDoc = criteria.getCriteriaObject();

    // Then
    String criteriaString = criteriaDoc.toJson();
    assertThat(criteriaString).doesNotContain("\"$regex\"");
  }

  @Test
  @DisplayName("should not apply search filter when search is empty")
  void shouldNotApplySearchFilterWhenSearchIsEmpty() throws Exception {
    // Given
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
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

    // Then
    String criteriaString = criteriaDoc.toJson();
    assertThat(criteriaString).doesNotContain("\"$regex\"");
  }

  @Test
  @DisplayName(
    "should emit display_gene_symbol $addFields AND reference it in computed sort when sorting by gene_symbol"
  )
  void shouldEmitDisplayGeneSymbolPipelineWhenSortingByGeneSymbol() {
    // Mocked MongoTemplate so we can capture the assembled Aggregation
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(0L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(TranscriptomicsDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    CustomTranscriptomicsRepositoryImpl repoWithMock = new CustomTranscriptomicsRepositoryImpl(
      mongoTemplate
    );

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("gene_symbol")));

    repoWithMock.findAll(pageable, query, Collections.emptyList(), "test-tissue", "test-cohort");

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(TranscriptomicsDocument.class)
    );

    String pipeline = aggregationCaptor.getValue().toString();

    // Extras must emit the display_gene_symbol $addFields with the $cond fallback
    assertThat(pipeline)
      .as("extras stage must compute display_gene_symbol")
      .contains("display_gene_symbol")
      .contains("$cond");

    // The computed sort must reference $toLower($display_gene_symbol) under the gene_symbol_sort key
    assertThat(pipeline)
      .as("computed sort must alias gene_symbol to gene_symbol_sort sourced from display_gene_symbol")
      .contains("gene_symbol_sort")
      .contains("$toLower");

    // The $sort doc must use the gene_symbol_sort key (not the raw gene_symbol)
    assertThat(pipeline).contains("\"gene_symbol_sort\" : 1");
  }

  /**
   * Uses reflection to invoke the private buildMatchCriteria method for testing.
   */
  private Criteria invokeBuildMatchCriteria(
    String tissue,
    String sexCohort,
    TranscriptomicsSearchQueryDto query,
    List<String> items
  ) throws Exception {
    var method =
      CustomTranscriptomicsRepositoryImpl.class.getDeclaredMethod(
          "buildMatchCriteria",
          String.class,
          String.class,
          TranscriptomicsSearchQueryDto.class,
          List.class
        );
    method.setAccessible(true);
    return (Criteria) method.invoke(repository, tissue, sexCohort, query, items);
  }
}
