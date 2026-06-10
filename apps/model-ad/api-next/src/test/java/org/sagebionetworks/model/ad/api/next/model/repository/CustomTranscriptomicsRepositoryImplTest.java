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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Tests for CustomTranscriptomicsRepositoryImpl focusing on criteria building and pipeline
 * assembly.
 *
 * <p>All tests call {@link CustomTranscriptomicsRepositoryImpl#findAll} with a mocked
 * {@link MongoTemplate} and capture the {@link Query} passed to {@code count()} to inspect the
 * match criteria, or capture the full {@link Aggregation} to inspect the pipeline shape.
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
    repository = new CustomTranscriptomicsRepositoryImpl(mongoTemplate);
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(0L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(TranscriptomicsDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());
  }

  @Test
  @DisplayName("should apply pagination to data query")
  void shouldApplyPaginationToDataQuery() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .biodomains(Arrays.asList("test-domain"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(
      PageRequest.of(0, 10),
      query,
      Collections.emptyList(),
      "test-tissue",
      "test-cohort"
    );

    Document criteriaDoc = captureCountQuery().getQueryObject();
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey("tissue"));
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey("sex_cohort"));
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey("biodomains"));
  }

  @Test
  @DisplayName("should execute count query without pagination")
  void shouldExecuteCountQueryWithoutPagination() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .biodomains(Arrays.asList("test-domain"))
      .modelType(Arrays.asList("test-type"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(
      PageRequest.of(0, 10),
      query,
      Collections.emptyList(),
      "test-tissue",
      "test-cohort"
    );

    Document criteriaDoc = captureCountQuery().getQueryObject();
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");
    // tissue, sex_cohort, biodomains, model_type
    assertThat(andConditions.size()).isGreaterThanOrEqualTo(4);
  }

  @Test
  @DisplayName("should search on gene_symbol with fallback to ensembl_gene_id for single term")
  void shouldSearchOnDisplayGeneSymbolWithSingleTerm() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .search("APOE")
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(
      PageRequest.of(0, 10),
      query,
      Collections.emptyList(),
      "test-tissue",
      "test-cohort"
    );

    Document criteriaDoc = captureCountQuery().getQueryObject();
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey("$or"));
  }

  @Test
  @DisplayName(
    "should search on gene_symbol with fallback to ensembl_gene_id for comma-separated terms"
  )
  void shouldSearchOnDisplayGeneSymbolWithCommaSeparatedTerms() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .search("APOE,TREM2,APP")
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(
      PageRequest.of(0, 10),
      query,
      Collections.emptyList(),
      "test-tissue",
      "test-cohort"
    );

    Document criteriaDoc = captureCountQuery().getQueryObject();
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey("$or"));
  }

  @Test
  @DisplayName("should not apply search filter in INCLUDE mode")
  void shouldNotApplySearchFilterInIncludeMode() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .search("APOE")
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    repository.findAll(
      PageRequest.of(0, 10),
      query,
      Arrays.asList("ENSG00000130203~5xFAD"),
      "test-tissue",
      "test-cohort"
    );

    assertThat(captureCountQuery().getQueryObject().toJson()).doesNotContain("\"$regex\"");
  }

  @Test
  @DisplayName("should not apply search filter when search is empty")
  void shouldNotApplySearchFilterWhenSearchIsEmpty() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .search("   ")
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(
      PageRequest.of(0, 10),
      query,
      Collections.emptyList(),
      "test-tissue",
      "test-cohort"
    );

    assertThat(captureCountQuery().getQueryObject().toJson()).doesNotContain("\"$regex\"");
  }

  @Test
  @DisplayName(
    "should emit display_gene_symbol $addFields AND reference it in computed sort when sorting by gene_symbol"
  )
  void shouldEmitDisplayGeneSymbolPipelineWhenSortingByGeneSymbol() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(
      PageRequest.of(0, 10, Sort.by(Sort.Order.asc("gene_symbol"))),
      query,
      Collections.emptyList(),
      "test-tissue",
      "test-cohort"
    );

    String pipeline = captureAggregation().toString();
    assertThat(pipeline)
      .as("extras stage must compute display_gene_symbol")
      .contains("display_gene_symbol")
      .contains("$cond");
    assertThat(pipeline)
      .as("computed sort must alias gene_symbol to gene_symbol_sort via display_gene_symbol")
      .contains("gene_symbol_sort")
      .contains("$toLower");
    assertThat(pipeline).contains("\"gene_symbol_sort\" : 1");
  }

  private Query captureCountQuery() {
    ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).count(captor.capture(), eq(COLLECTION_NAME));
    return captor.getValue();
  }

  private Aggregation captureAggregation() {
    ArgumentCaptor<Aggregation> captor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      captor.capture(),
      eq(COLLECTION_NAME),
      eq(TranscriptomicsDocument.class)
    );
    return captor.getValue();
  }
}
