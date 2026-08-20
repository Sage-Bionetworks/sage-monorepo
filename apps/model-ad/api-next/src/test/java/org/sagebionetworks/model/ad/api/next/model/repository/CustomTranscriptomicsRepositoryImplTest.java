package org.sagebionetworks.model.ad.api.next.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
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
  private static final String ENSEMBL_GENE_ID_FIELD = "ensembl_gene_id";
  private static final String GENE_SYMBOL_FIELD = "gene_symbol";
  private static final String TISSUE_FIELD = "tissue";

  private static final String ENSA_ENSEMBL_GENE_ID = "ENSMUSG00000038619";
  private static final String OTHER_ENSEMBL_GENE_ID = "ENSMUSG00000000001";
  private static final String PLEC_GENE_SYMBOL = "plec";

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

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), "test-tissue");

    Document criteriaDoc = captureCountQuery().getQueryObject();
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");
    assertThat(andConditions).anySatisfy(doc -> assertThat(doc).containsKey(TISSUE_FIELD));
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

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), "test-tissue");

    Document criteriaDoc = captureCountQuery().getQueryObject();
    assertThat(criteriaDoc).containsKey("$and");
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");
    // tissue, biodomains, model_type
    assertThat(andConditions.size()).isGreaterThanOrEqualTo(3);
  }

  @Test
  @DisplayName("should match the requested sex values when filtering by sex")
  void shouldMatchRequestedSexValuesWhenFilteringBySex() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .sex(Arrays.asList("Female", "Male"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), "test-tissue");

    Document criteriaDoc = captureCountQuery().getQueryObject();
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");
    assertThat(andConditions).contains(
      new Document("sex", new Document("$in", List.of("Female", "Male")))
    );
  }

  @Test
  @DisplayName("should search on gene_symbol with fallback to ensembl_gene_id for single term")
  void shouldSearchOnDisplayGeneSymbolWithSingleTerm() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .search("APOE")
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), "test-tissue");

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

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), "test-tissue");

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
      Arrays.asList("ENSG00000130203~5xFAD~Female"),
      "test-tissue"
    );

    assertThat(captureCountQuery().getQueryObject().toString()).doesNotContain(GENE_SYMBOL_FIELD);
  }

  @Test
  @DisplayName("should not apply search filter when search is empty")
  void shouldNotApplySearchFilterWhenSearchIsEmpty() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .search("   ")
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), "test-tissue");

    assertThat(captureCountQuery().getQueryObject().toString())
      .doesNotContain(GENE_SYMBOL_FIELD)
      .doesNotContain(ENSEMBL_GENE_ID_FIELD);
  }

  @Test
  @DisplayName("should search ensembl_gene_id only when search is a full ensembl gene id")
  void shouldSearchEnsemblGeneIdOnlyWhenSearchIsFullEnsemblGeneId() {
    Document searchCondition = searchConditionFor(ENSA_ENSEMBL_GENE_ID);

    assertThat(searchCondition.keySet())
      .as("a full ensembl gene id routes to ensembl_gene_id with no gene_symbol branch")
      .containsExactly(ENSEMBL_GENE_ID_FIELD);
    assertThat(inPatterns(searchCondition, ENSEMBL_GENE_ID_FIELD))
      .extracting(Pattern::pattern)
      .containsExactly(fullMatch(ENSA_ENSEMBL_GENE_ID));
  }

  @Test
  @DisplayName("should match case-insensitively when search is a lowercase full ensembl gene id")
  void shouldMatchCaseInsensitivelyWhenSearchIsLowercaseFullEnsemblGeneId() {
    Document searchCondition = searchConditionFor(ENSA_ENSEMBL_GENE_ID.toLowerCase());

    assertThat(searchCondition.keySet()).containsExactly(ENSEMBL_GENE_ID_FIELD);
    assertThat(inPatterns(searchCondition, ENSEMBL_GENE_ID_FIELD)).allSatisfy(pattern ->
      assertThat(pattern.flags() & Pattern.CASE_INSENSITIVE).isNotZero()
    );
  }

  @Test
  @DisplayName(
    "should route terms independently when search mixes a full ensembl gene id and a gene symbol"
  )
  void shouldRouteTermsIndependentlyWhenSearchMixesFullEnsemblGeneIdAndGeneSymbol() {
    List<Document> branches = searchBranchesFor(ENSA_ENSEMBL_GENE_ID + "," + PLEC_GENE_SYMBOL);

    assertThat(branches)
      .as("ensembl_gene_id branch, gene_symbol branch, and blank-guarded fallback branch")
      .hasSize(3);
    assertThat(inPatterns(branchFor(branches, ENSEMBL_GENE_ID_FIELD), ENSEMBL_GENE_ID_FIELD))
      .extracting(Pattern::pattern)
      .containsExactly(fullMatch(ENSA_ENSEMBL_GENE_ID));
    assertThat(inPatterns(branchFor(branches, GENE_SYMBOL_FIELD), GENE_SYMBOL_FIELD))
      .as("the ensembl gene id term must not leak into the gene_symbol partition")
      .extracting(Pattern::pattern)
      .containsExactly(fullMatch(PLEC_GENE_SYMBOL));
  }

  @Test
  @DisplayName(
    "should route all terms to ensembl_gene_id when every comma term is a full ensembl gene id"
  )
  void shouldRouteAllTermsToEnsemblGeneIdWhenEveryCommaTermIsFullEnsemblGeneId() {
    Document searchCondition = searchConditionFor(
      ENSA_ENSEMBL_GENE_ID + "," + OTHER_ENSEMBL_GENE_ID
    );

    assertThat(searchCondition.keySet()).containsExactly(ENSEMBL_GENE_ID_FIELD);
    assertThat(inPatterns(searchCondition, ENSEMBL_GENE_ID_FIELD))
      .extracting(Pattern::pattern)
      .containsExactly(fullMatch(ENSA_ENSEMBL_GENE_ID), fullMatch(OTHER_ENSEMBL_GENE_ID));
  }

  @Test
  @DisplayName("should trim terms when comma-separated terms have surrounding whitespace")
  void shouldTrimTermsWhenCommaSeparatedTermsHaveSurroundingWhitespace() {
    List<Document> branches = searchBranchesFor(
      "  " + ENSA_ENSEMBL_GENE_ID + " , " + PLEC_GENE_SYMBOL + "  "
    );

    assertThat(branches).hasSize(3);
    assertThat(inPatterns(branchFor(branches, ENSEMBL_GENE_ID_FIELD), ENSEMBL_GENE_ID_FIELD))
      .extracting(Pattern::pattern)
      .containsExactly(fullMatch(ENSA_ENSEMBL_GENE_ID));
    assertThat(inPatterns(branchFor(branches, GENE_SYMBOL_FIELD), GENE_SYMBOL_FIELD))
      .extracting(Pattern::pattern)
      .containsExactly(fullMatch(PLEC_GENE_SYMBOL));
  }

  @Test
  @DisplayName("should fall back to gene_symbol when search is a partial ensembl gene id")
  void shouldFallBackToGeneSymbolWhenSearchIsPartialEnsemblGeneId() {
    String partialEnsemblGeneId = ENSA_ENSEMBL_GENE_ID.substring(
      0,
      ENSA_ENSEMBL_GENE_ID.length() - 1
    );
    List<Document> branches = searchBranchesFor(partialEnsemblGeneId);

    assertThat(branches)
      .as("gene_symbol partial match plus blank-guarded ensembl_gene_id fallback")
      .hasSize(2);
    Pattern geneSymbolPattern = (Pattern) branchFor(branches, GENE_SYMBOL_FIELD)
      .get(GENE_SYMBOL_FIELD);
    assertThat(geneSymbolPattern.pattern())
      .as("a partial term stays an unanchored partial match")
      .isEqualTo(Pattern.quote(partialEnsemblGeneId));
  }

  @Test
  @DisplayName("should fall back to gene_symbol when an ensembl gene id has a version suffix")
  void shouldFallBackToGeneSymbolWhenEnsemblGeneIdHasVersionSuffix() {
    String versionedEnsemblGeneId = ENSA_ENSEMBL_GENE_ID + ".5";
    List<Document> branches = searchBranchesFor(versionedEnsemblGeneId);

    assertThat(branches).hasSize(2);
    Pattern geneSymbolPattern = (Pattern) branchFor(branches, GENE_SYMBOL_FIELD)
      .get(GENE_SYMBOL_FIELD);
    assertThat(geneSymbolPattern.pattern()).isEqualTo(Pattern.quote(versionedEnsemblGeneId));
  }

  @Test
  @DisplayName("should match nothing when search contains only commas")
  void shouldMatchNothingWhenSearchContainsOnlyCommas() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .search(",,")
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), "test-tissue");

    Document criteriaDoc = captureCountQuery().getQueryObject();
    assertThat(searchCondition(criteriaDoc)).isEqualTo(new Document("_id", null));
    assertThat(criteriaDoc.toString())
      .doesNotContain(GENE_SYMBOL_FIELD)
      .doesNotContain(ENSEMBL_GENE_ID_FIELD);
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
      "test-tissue"
    );

    String pipeline = captureAggregation().toString();
    assertThat(pipeline)
      .as("extras stage must compute display_gene_symbol")
      .contains("display_gene_symbol")
      .contains("$cond");
    assertThat(pipeline)
      .as("computed sort must alias gene_symbol to gene_symbol_sort via display_gene_symbol")
      .contains("gene_symbol_sort")
      .contains("$display_gene_symbol");
    assertThat(pipeline).contains("\"gene_symbol_sort\" : 1");
  }

  @Test
  @DisplayName("should sort by log2_fc sub-field when sorting by a heatmap month column")
  void shouldSortByLog2FcWhenSortingByMonthColumn() {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(
      PageRequest.of(0, 10, Sort.by(Sort.Order.asc("4 months"))),
      query,
      Collections.emptyList(),
      "Hippocampus"
    );

    String pipeline = captureAggregation().toString();
    assertThat(pipeline)
      .as("$sort should use the nested log2_fc path, not the raw object")
      .contains("4 months.log2_fc");
    assertThat(pipeline)
      .as("$sort should not reference the raw '4 months' object directly as a sort key")
      .doesNotContain("\"4 months\" :");
  }

  /** Runs a search-only query (EXCLUDE mode, no items) and returns its search condition. */
  private Document searchConditionFor(String search) {
    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .search(search)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), "test-tissue");

    return searchCondition(captureCountQuery().getQueryObject());
  }

  private List<Document> searchBranchesFor(String search) {
    return (List<Document>) searchConditionFor(search).get("$or");
  }

  /** The one $and condition that isn't the mandatory tissue scoping. */
  private static Document searchCondition(Document criteriaDoc) {
    List<Document> andConditions = (List<Document>) criteriaDoc.get("$and");
    return andConditions
      .stream()
      .filter(condition -> !condition.containsKey(TISSUE_FIELD))
      .findFirst()
      .orElseThrow(() -> new AssertionError("no search condition in " + criteriaDoc));
  }

  private static Document branchFor(List<Document> branches, String field) {
    return branches
      .stream()
      .filter(branch -> branch.containsKey(field))
      .findFirst()
      .orElseThrow(() -> new AssertionError("no " + field + " branch in " + branches));
  }

  private static List<Pattern> inPatterns(Document condition, String field) {
    return (List<Pattern>) condition.get(field, Document.class).get("$in");
  }

  private static String fullMatch(String term) {
    return "^" + Pattern.quote(term) + "$";
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
