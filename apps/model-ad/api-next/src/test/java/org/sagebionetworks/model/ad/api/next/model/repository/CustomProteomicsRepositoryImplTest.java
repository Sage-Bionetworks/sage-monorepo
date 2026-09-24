package org.sagebionetworks.model.ad.api.next.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.sagebionetworks.explorers.CtPage;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemIdSpaceQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsSearchQueryDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Tests for CustomProteomicsRepositoryImpl focusing on criteria building and pipeline assembly.
 *
 * <p>All tests call {@link CustomProteomicsRepositoryImpl#findAll} with a mocked
 * {@link MongoTemplate} and capture the {@link Query} passed to {@code count()} to inspect the
 * match criteria, or capture the full {@link Aggregation} to inspect the pipeline shape.
 */
@ExtendWith(MockitoExtension.class)
class CustomProteomicsRepositoryImplTest {

  private static final String COLLECTION_NAME = "protein_de_aggregate";
  private static final String DISPLAY_SYMBOL_FIELD = "display_symbol";
  private static final String ENSEMBL_GENE_ID_FIELD = "ensembl_gene_id";
  private static final String GENE_SYMBOL_FIELD = "gene_symbol";
  private static final String UNIPROTID_FIELD = "uniprotid";
  private static final List<String> FULL_MATCH_SEARCH_FIELDS = List.of(
    ENSEMBL_GENE_ID_FIELD,
    GENE_SYMBOL_FIELD,
    UNIPROTID_FIELD
  );
  private static final String NAME_FIELD = "name.link_text";
  private static final String SEX_FIELD = "sex";
  private static final String TISSUE_FIELD = "tissue";
  private static final String UNIQUE_ID_FIELD = "unique_id";
  private static final String TISSUE = "Hemibrain";
  private static final int REMAINING_BUDGET = 25;

  private static final String ENSA_GENE_SYMBOL = "ensa";
  private static final String ENSA_UNIPROT_ID = "p11934870";
  private static final String ENSA_MOUSE_ENSEMBL_GENE_ID = "ENSMUSG00000038619";
  private static final String MODEL_NAME = "LOAD2";
  private static final String SEX = "Female";
  private static final String ROW_COMPOSITE_ID =
    "ENSMUSG00000000001P27144~" + MODEL_NAME + "~" + SEX;
  private static final String PARENT_COMPOSITE_ID =
    ENSA_MOUSE_ENSEMBL_GENE_ID + "~" + MODEL_NAME + "~" + SEX;
  private static final List<String> MONTH_COLUMNS = List.of(
    "4 months",
    "12 months",
    "18 months",
    "24 months"
  );

  private CustomProteomicsRepositoryImpl repository;

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private AggregationResults<ProteomicsDocument> aggregationResults;

  @Mock
  private AggregationResults<Document> parentSelectionResults;

  @BeforeEach
  void setUp() {
    repository = new CustomProteomicsRepositoryImpl(mongoTemplate);
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(0L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(ProteomicsDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());
  }

  @Test
  @DisplayName("should scope every query to the requested tissue")
  void shouldScopeEveryQueryToRequestedTissue() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), TISSUE);

    assertThat(captureAndConditions()).contains(new Document("tissue", TISSUE));
  }

  @Test
  @DisplayName("should apply every data filter when all filters are provided")
  void shouldApplyEveryDataFilterWhenAllFiltersAreProvided() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .biodomains(List.of("Synapse"))
      .modelType(List.of("Familial AD"))
      .name(List.of("LOAD2"))
      .sex(List.of("Female", "Male"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), TISSUE);

    assertThat(captureAndConditions()).contains(
      new Document("tissue", TISSUE),
      new Document("biodomains", new Document("$in", List.of("Synapse"))),
      new Document("model_type", new Document("$in", List.of("Familial AD"))),
      new Document("name.link_text", new Document("$in", List.of("LOAD2"))),
      new Document("sex", new Document("$in", List.of("Female", "Male")))
    );
  }

  @Test
  @DisplayName("should match all three identifier fields when include filter has items")
  void shouldMatchAllThreeIdentifierFieldsWhenIncludeFilterHasItems() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, List.of(ROW_COMPOSITE_ID), TISSUE);

    assertThat(orBranches(captureAndConditions()))
      .singleElement()
      .satisfies(doc ->
        assertThat((List<Document>) doc.get("$and")).containsExactly(
          new Document(UNIQUE_ID_FIELD, "ENSMUSG00000000001P27144"),
          new Document(NAME_FIELD, MODEL_NAME),
          new Document(SEX_FIELD, SEX)
        )
      );
  }

  @Test
  @DisplayName("should exclude the listed identifiers when exclude filter has items")
  void shouldExcludeListedIdentifiersWhenExcludeFilterHasItems() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, List.of(ROW_COMPOSITE_ID), TISSUE);

    assertThat(captureAndConditions()).anySatisfy(doc -> assertThat(doc).containsKey("$nor"));
  }

  @Test
  @DisplayName("should search on display_symbol for a single term")
  void shouldSearchOnDisplaySymbolForSingleTerm() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .search("p11")
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), TISSUE);

    assertThat(captureAndConditions()).anySatisfy(doc -> {
      assertThat(doc).containsOnlyKeys(DISPLAY_SYMBOL_FIELD);
      assertThat(doc.get(DISPLAY_SYMBOL_FIELD)).hasToString("\\Qp11\\E");
    });
  }

  @Test
  @DisplayName("should full match every identifier field for comma-separated terms")
  void shouldFullMatchEveryIdentifierFieldForCommaSeparatedTerms() {
    List<Document> branches = searchBranchesFor(
      ENSA_GENE_SYMBOL + "," + ENSA_UNIPROT_ID + "," + ENSA_MOUSE_ENSEMBL_GENE_ID
    );

    assertThat(branches)
      .as("one branch per identifier field, so each term matches wherever it belongs")
      .hasSize(FULL_MATCH_SEARCH_FIELDS.size());
    for (String field : FULL_MATCH_SEARCH_FIELDS) {
      assertThat(inPatterns(branchFor(branches, field), field))
        .as("every term is full-matched against %s", field)
        .extracting(Pattern::pattern)
        .containsExactly(
          fullMatch(ENSA_GENE_SYMBOL),
          fullMatch(ENSA_UNIPROT_ID),
          fullMatch(ENSA_MOUSE_ENSEMBL_GENE_ID)
        );
    }
  }

  @Test
  @DisplayName("should full match ensembl_gene_id when the single term is a full ensembl gene id")
  void shouldFullMatchEnsemblGeneIdForSingleFullIdTerm() {
    Document searchCondition = searchConditionFor(ENSA_MOUSE_ENSEMBL_GENE_ID);

    assertThat(searchCondition.keySet())
      .as("a full ensembl gene id routes to ensembl_gene_id with no display_symbol branch")
      .containsExactly(ENSEMBL_GENE_ID_FIELD);
    assertThat(inPatterns(searchCondition, ENSEMBL_GENE_ID_FIELD))
      .extracting(Pattern::pattern)
      .containsExactly(fullMatch(ENSA_MOUSE_ENSEMBL_GENE_ID));
  }

  @Test
  @DisplayName("should match case-insensitively when the term is a lowercase ensembl gene id")
  void shouldMatchCaseInsensitivelyForLowercaseSingleFullIdTerm() {
    Document searchCondition = searchConditionFor(ENSA_MOUSE_ENSEMBL_GENE_ID.toLowerCase());

    assertThat(searchCondition.keySet()).containsExactly(ENSEMBL_GENE_ID_FIELD);
    assertThat(inPatterns(searchCondition, ENSEMBL_GENE_ID_FIELD)).allSatisfy(pattern ->
      assertThat(pattern.flags() & Pattern.CASE_INSENSITIVE).isNotZero()
    );
  }

  @Test
  @DisplayName("should match nothing when search contains only commas")
  void shouldMatchNothingWhenSearchContainsOnlyCommas() {
    assertThat(searchConditionFor(",,")).isEqualTo(new Document("_id", null));
  }

  @Test
  @DisplayName("should not apply search filter in INCLUDE mode")
  void shouldNotApplySearchFilterInIncludeMode() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .search("p11")
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, List.of(ROW_COMPOSITE_ID), TISSUE);

    assertThat(captureAndConditions()).noneSatisfy(doc ->
      assertThat(doc).containsKey(DISPLAY_SYMBOL_FIELD)
    );
  }

  @Test
  @DisplayName("should not apply search filter when search is empty")
  void shouldNotApplySearchFilterWhenSearchIsEmpty() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .search("   ")
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), TISSUE);

    assertThat(captureAndConditions()).noneSatisfy(doc ->
      assertThat(doc).containsKey(DISPLAY_SYMBOL_FIELD)
    );
  }

  @Test
  @DisplayName("should sort by the log2_fc sub-field for every heatmap month column")
  void shouldSortByLog2FcSubFieldForEveryMonthColumn() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    for (String monthColumn : MONTH_COLUMNS) {
      repository.findAll(
        PageRequest.of(0, 10, Sort.by(Sort.Order.asc(monthColumn))),
        query,
        Collections.emptyList(),
        TISSUE
      );
    }

    ArgumentCaptor<Aggregation> captor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate, times(MONTH_COLUMNS.size())).aggregate(
      captor.capture(),
      eq(COLLECTION_NAME),
      eq(ProteomicsDocument.class)
    );

    List<Aggregation> aggregations = captor.getAllValues();
    for (int i = 0; i < MONTH_COLUMNS.size(); i++) {
      String monthColumn = MONTH_COLUMNS.get(i);
      String pipeline = aggregations.get(i).toString();
      assertThat(pipeline)
        .as("$sort for '%s' should use the nested log2_fc path", monthColumn)
        .contains(monthColumn + ".log2_fc");
      assertThat(pipeline)
        .as("$sort for '%s' should not reference the raw object as a sort key", monthColumn)
        .doesNotContain("\"" + monthColumn + "\" :");
    }
  }

  @Test
  @DisplayName("should sort by link_text when sorting by name")
  void shouldSortByLinkTextWhenSortingByName() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(
      PageRequest.of(0, 10, Sort.by(Sort.Order.asc("name"))),
      query,
      Collections.emptyList(),
      TISSUE
    );

    assertThat(captureAggregation().toString()).contains("name.link_text");
  }

  @Test
  @DisplayName("should sort by display_symbol directly without a computed sort field")
  void shouldSortByDisplaySymbolDirectlyWithoutComputedSortField() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(
      PageRequest.of(0, 10, Sort.by(Sort.Order.asc(DISPLAY_SYMBOL_FIELD))),
      query,
      Collections.emptyList(),
      TISSUE
    );

    assertThat(captureAggregation().toString())
      .contains("\"" + DISPLAY_SYMBOL_FIELD + "\" : 1")
      .doesNotContain(DISPLAY_SYMBOL_FIELD + "_sort");
  }

  @Test
  @DisplayName("should match items against the parent gene fields when the parent space is used")
  void shouldMatchItemsAgainstParentGeneFieldsWhenParentSpaceIsAskedFor() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .itemIdSpace(ItemIdSpaceQueryDto.PARENT)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, List.of(PARENT_COMPOSITE_ID), TISSUE);

    List<Document> andConditions = captureAndConditions();
    assertThat(orBranches(andConditions))
      .singleElement()
      .satisfies(doc ->
        assertThat((List<Document>) doc.get("$and")).containsExactly(
          new Document(ENSEMBL_GENE_ID_FIELD, ENSA_MOUSE_ENSEMBL_GENE_ID),
          new Document(NAME_FIELD, MODEL_NAME),
          new Document(SEX_FIELD, SEX)
        )
      );
    assertThat(andConditions.toString())
      .as("an isoform's own unique_id has no part in a gene-level match")
      .doesNotContain(UNIQUE_ID_FIELD);
  }

  @Test
  @DisplayName("should spend the remaining budget on parent genes rather than on rows")
  void shouldSpendRemainingBudgetOnParentGenesRatherThanRows() {
    stubParentSelection(List.of(PARENT_COMPOSITE_ID));
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .remainingBudget(REMAINING_BUDGET)
      .build();

    // A later page, so a budget that never reached the base class would show up as a $skip.
    repository.findAll(PageRequest.of(2, 10), query, Collections.emptyList(), TISSUE);

    assertThat(captureParentSelection().toString())
      .as("the budget buys whole genes, so it is the gene selection that is limited")
      .contains("$group")
      .contains(String.valueOf(REMAINING_BUDGET));
    assertThat(captureAggregation().toString())
      .as("every protein isoform of an admitted gene is wanted, so the rows take no limit")
      .doesNotContain("$skip")
      .doesNotContain("$limit")
      .contains(ENSA_MOUSE_ENSEMBL_GENE_ID);
  }

  @Test
  @DisplayName("should answer hasRowsForPrebudgetedParents against the parent gene fields")
  void shouldAnswerHasRowsForPrebudgetedParentsAgainstParentGeneFields() {
    when(mongoTemplate.exists(any(Query.class), eq(COLLECTION_NAME))).thenReturn(true);
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .prebudgetedParentIds(List.of(PARENT_COMPOSITE_ID))
      .build();

    CtPage<ProteomicsDocument> page = repository.findAll(
      PageRequest.of(0, 10),
      query,
      Collections.emptyList(),
      TISSUE
    );

    assertThat(page.getHasRowsForPrebudgetedParents()).isTrue();
    assertThat(captureExistsQuery().getQueryObject().toString())
      .as("an unpinned isoform is looked for by its gene, not by its own id")
      .contains(ENSEMBL_GENE_ID_FIELD)
      .doesNotContain(UNIQUE_ID_FIELD);
  }

  private void stubParentSelection(List<String> parentTokens) {
    when(
      mongoTemplate.aggregate(any(Aggregation.class), eq(COLLECTION_NAME), eq(Document.class))
    ).thenReturn(parentSelectionResults);
    when(parentSelectionResults.getMappedResults()).thenReturn(
      parentTokens.stream().map(token -> new Document("_id", token)).toList()
    );
  }

  /** The $or branches of the one $and condition that carries the item filter. */
  private static List<Document> orBranches(List<Document> andConditions) {
    return andConditions
      .stream()
      .filter(doc -> doc.containsKey("$or"))
      .findFirst()
      .map(doc -> (List<Document>) doc.get("$or"))
      .orElseThrow(() -> new AssertionError("no item filter in " + andConditions));
  }

  /** Runs a search-only query (EXCLUDE mode, no items) and returns its search condition. */
  private Document searchConditionFor(String search) {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .search(search)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    repository.findAll(PageRequest.of(0, 10), query, Collections.emptyList(), TISSUE);

    return searchCondition(captureAndConditions());
  }

  private List<Document> searchBranchesFor(String search) {
    return (List<Document>) searchConditionFor(search).get("$or");
  }

  /** The one $and condition that isn't the mandatory tissue scoping. */
  private static Document searchCondition(List<Document> andConditions) {
    return andConditions
      .stream()
      .filter(condition -> !condition.containsKey(TISSUE_FIELD))
      .findFirst()
      .orElseThrow(() -> new AssertionError("no search condition in " + andConditions));
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

  private List<Document> captureAndConditions() {
    Document criteriaDoc = captureCountQuery().getQueryObject();
    assertThat(criteriaDoc).containsKey("$and");
    return (List<Document>) criteriaDoc.get("$and");
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
      eq(ProteomicsDocument.class)
    );
    return captor.getValue();
  }

  private Aggregation captureParentSelection() {
    ArgumentCaptor<Aggregation> captor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(captor.capture(), eq(COLLECTION_NAME), eq(Document.class));
    return captor.getValue();
  }

  private Query captureExistsQuery() {
    ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).exists(captor.capture(), eq(COLLECTION_NAME));
    return captor.getValue();
  }
}
