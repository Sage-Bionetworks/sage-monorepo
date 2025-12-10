package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.GeneExpressionMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.GeneExpressionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class GeneExpressionServiceTest {

  @Mock
  private GeneExpressionRepository repository;

  private GeneExpressionService service;
  private GeneExpressionMapper mapper;

  private static final String TISSUE = "brain";
  private static final String SEX_COHORT = "Females";

  @BeforeEach
  void setUp() {
    mapper = new GeneExpressionMapper();
    service = new GeneExpressionService(repository, mapper);
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    GeneExpressionsPageDto result = service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    assertThat(result.getGeneExpressions()).isEmpty();
    assertThat(result.getPage().getTotalElements()).isZero();
    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should return all tissue and sex cohort items when exclude filter has no items")
  void shouldReturnAllTissueAndSexCohortItemsWhenExcludeFilterHasNoItems() {
    GeneExpressionDocument doc = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByTissueAndSexCohort(eq(TISSUE), eq(SEX_COHORT), any(PageRequest.class))
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    GeneExpressionsPageDto result = service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    assertThat(result.getGeneExpressions()).hasSize(1);
    assertThat(result.getGeneExpressions().get(0).getEnsemblGeneId()).isEqualTo("ENSG00000001");
    verify(repository).findByTissueAndSexCohort(eq(TISSUE), eq(SEX_COHORT), any(PageRequest.class));
  }

  @Test
  @DisplayName("should respect search term when exclude filter has no items")
  void shouldRespectSearchTermWhenExcludeFilterHasNoItems() {
    GeneExpressionDocument doc = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByTissueAndSexCohortAndGeneSymbolContaining(
        eq(TISSUE),
        eq(SEX_COHORT),
        eq("\\QSymbol\\E"),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("Symbol")
      .pageNumber(0)
      .pageSize(100)
      .build();

    GeneExpressionsPageDto result = service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    assertThat(result.getGeneExpressions()).hasSize(1);
    assertThat(result.getGeneExpressions().get(0).getGeneSymbol()).isEqualTo("Symbol1");
    verify(repository).findByTissueAndSexCohortAndGeneSymbolContaining(
      eq(TISSUE),
      eq(SEX_COHORT),
      eq("\\QSymbol\\E"),
      any(PageRequest.class)
    );
  }

  @Test
  @DisplayName("should use search-only method for comma-separated search when no items to exclude")
  void shouldUseSearchOnlyMethodForCommaSeparatedSearchWhenNoItemsToExclude() {
    GeneExpressionDocument doc1 = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    GeneExpressionDocument doc2 = createGeneExpressionDocument("ENSG00000002", "GENE2", "Symbol2");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findByTissueAndSexCohortAndGeneSymbolInIgnoreCase(
        eq(TISSUE),
        eq(SEX_COHORT),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("symbol1,symbol2")
      .pageNumber(0)
      .pageSize(100)
      .build();

    GeneExpressionsPageDto result = service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    assertThat(result.getGeneExpressions()).hasSize(2);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Pattern>> patternsCaptor = ArgumentCaptor.forClass(List.class);
    verify(repository).findByTissueAndSexCohortAndGeneSymbolInIgnoreCase(
      eq(TISSUE),
      eq(SEX_COHORT),
      patternsCaptor.capture(),
      any(PageRequest.class)
    );

    List<Pattern> patterns = patternsCaptor.getValue();
    assertThat(patterns).hasSize(2);
    assertThat(patterns.get(0).pattern()).isEqualTo("^\\Qsymbol1\\E$");
    assertThat(patterns.get(1).pattern()).isEqualTo("^\\Qsymbol2\\E$");
  }

  @Test
  @DisplayName("should return matching composite identifiers when include filter has items")
  void shouldReturnMatchingCompositeIdentifiersWhenIncludeFilterHasItems() {
    GeneExpressionDocument doc = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByTissueAndSexCohortAndCompositeIdentifiers(
        eq(TISSUE),
        eq(SEX_COHORT),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(List.of("ENSG00000001~GENE1"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    GeneExpressionsPageDto result = service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    assertThat(result.getGeneExpressions()).hasSize(1);
    assertThat(result.getGeneExpressions().get(0).getEnsemblGeneId()).isEqualTo("ENSG00000001");

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Map<String, Object>>> conditionsCaptor = ArgumentCaptor.forClass(
      List.class
    );
    verify(repository).findByTissueAndSexCohortAndCompositeIdentifiers(
      eq(TISSUE),
      eq(SEX_COHORT),
      conditionsCaptor.capture(),
      any(PageRequest.class)
    );

    List<Map<String, Object>> conditions = conditionsCaptor.getValue();
    assertThat(conditions).hasSize(1);
    verifyCompositeCondition(conditions.get(0), "ENSG00000001", "GENE1");
  }

  @Test
  @DisplayName("should return non-matching composite identifiers when exclude filter has items")
  void shouldReturnNonMatchingCompositeIdentifiersWhenExcludeFilterHasItems() {
    GeneExpressionDocument doc = createGeneExpressionDocument("ENSG00000002", "GENE2", "Symbol2");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByTissueAndSexCohortExcludingCompositeIdentifiers(
        eq(TISSUE),
        eq(SEX_COHORT),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(List.of("ENSG00000001~GENE1"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    GeneExpressionsPageDto result = service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    assertThat(result.getGeneExpressions()).hasSize(1);
    assertThat(result.getGeneExpressions().get(0).getEnsemblGeneId()).isEqualTo("ENSG00000002");

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Map<String, Object>>> conditionsCaptor = ArgumentCaptor.forClass(
      List.class
    );
    verify(repository).findByTissueAndSexCohortExcludingCompositeIdentifiers(
      eq(TISSUE),
      eq(SEX_COHORT),
      conditionsCaptor.capture(),
      any(PageRequest.class)
    );

    List<Map<String, Object>> conditions = conditionsCaptor.getValue();
    assertThat(conditions).hasSize(1);
    verifyCompositeCondition(conditions.get(0), "ENSG00000001", "GENE1");
  }

  @Test
  @DisplayName("should perform partial case-insensitive search when exclude filter has search term")
  void shouldPerformPartialCaseInsensitiveSearchWhenExcludeFilterHasSearchTerm() {
    GeneExpressionDocument doc = createGeneExpressionDocument(
      "ENSG00000001",
      "TestGene",
      "TestSymbol"
    );
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByTissueAndSexCohortAndGeneSymbolContainingExcludingCompositeIdentifiers(
        eq(TISSUE),
        eq(SEX_COHORT),
        any(String.class),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(List.of("ENSG00000002~GENE2"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    GeneExpressionsPageDto result = service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    assertThat(result.getGeneExpressions()).hasSize(1);
    assertThat(result.getGeneExpressions().get(0).getGeneSymbol()).isEqualTo("TestSymbol");

    ArgumentCaptor<String> searchCaptor = ArgumentCaptor.forClass(String.class);
    verify(repository).findByTissueAndSexCohortAndGeneSymbolContainingExcludingCompositeIdentifiers(
      eq(TISSUE),
      eq(SEX_COHORT),
      searchCaptor.capture(),
      anyList(),
      any(PageRequest.class)
    );

    // Verify Pattern.quote was used (wraps search term in \Q \E)
    assertThat(searchCaptor.getValue()).isEqualTo("\\Qtest\\E");
  }

  @Test
  @DisplayName(
    "should perform case-insensitive full match search when exclude filter has " +
    "comma-separated search terms"
  )
  void shouldPerformCaseInsensitiveFullMatchSearchWhenExcludeFilterHasCommaSeparatedSearchTerms() {
    GeneExpressionDocument doc1 = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    GeneExpressionDocument doc2 = createGeneExpressionDocument("ENSG00000002", "GENE2", "Symbol2");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findByTissueAndSexCohortAndGeneSymbolInIgnoreCaseExcludingCompositeIdentifiers(
        eq(TISSUE),
        eq(SEX_COHORT),
        anyList(),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(List.of("ENSG00000003~GENE3"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("symbol1,symbol2")
      .pageNumber(0)
      .pageSize(100)
      .build();

    GeneExpressionsPageDto result = service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    assertThat(result.getGeneExpressions()).hasSize(2);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Pattern>> patternsCaptor = ArgumentCaptor.forClass(List.class);
    verify(
      repository
    ).findByTissueAndSexCohortAndGeneSymbolInIgnoreCaseExcludingCompositeIdentifiers(
      eq(TISSUE),
      eq(SEX_COHORT),
      patternsCaptor.capture(),
      anyList(),
      any(PageRequest.class)
    );

    List<Pattern> patterns = patternsCaptor.getValue();
    assertThat(patterns).hasSize(2);
    assertThat(patterns.get(0).pattern()).isEqualTo("^\\Qsymbol1\\E$");
    assertThat(patterns.get(1).pattern()).isEqualTo("^\\Qsymbol2\\E$");
    // Verify case-insensitive flag is set
    assertThat(patterns.get(0).flags() & Pattern.CASE_INSENSITIVE).isNotZero();
  }

  @Test
  @DisplayName("should not use search when include filter is specified")
  void shouldNotUseSearchWhenIncludeFilterIsSpecified() {
    GeneExpressionDocument doc = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByTissueAndSexCohortAndCompositeIdentifiers(
        eq(TISSUE),
        eq(SEX_COHORT),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(List.of("ENSG00000001~GENE1"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .search("test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    GeneExpressionsPageDto result = service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    assertThat(result.getGeneExpressions()).hasSize(1);
    // Should use findByTissueAndSexCohortAndCompositeIdentifiers, not search methods
    verify(repository).findByTissueAndSexCohortAndCompositeIdentifiers(
      eq(TISSUE),
      eq(SEX_COHORT),
      anyList(),
      any(PageRequest.class)
    );
  }

  @Test
  @DisplayName("should handle multiple composite identifiers")
  void shouldHandleMultipleCompositeIdentifiers() {
    GeneExpressionDocument doc1 = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    GeneExpressionDocument doc2 = createGeneExpressionDocument("ENSG00000002", "GENE2", "Symbol2");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findByTissueAndSexCohortAndCompositeIdentifiers(
        eq(TISSUE),
        eq(SEX_COHORT),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(List.of("ENSG00000001~GENE1", "ENSG00000002~GENE2"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    GeneExpressionsPageDto result = service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    assertThat(result.getGeneExpressions()).hasSize(2);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Map<String, Object>>> conditionsCaptor = ArgumentCaptor.forClass(
      List.class
    );
    verify(repository).findByTissueAndSexCohortAndCompositeIdentifiers(
      eq(TISSUE),
      eq(SEX_COHORT),
      conditionsCaptor.capture(),
      any(PageRequest.class)
    );

    List<Map<String, Object>> conditions = conditionsCaptor.getValue();
    assertThat(conditions).hasSize(2);
    verifyCompositeCondition(conditions.get(0), "ENSG00000001", "GENE1");
    verifyCompositeCondition(conditions.get(1), "ENSG00000002", "GENE2");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is invalid")
  void shouldThrowExceptionWhenCompositeIdentifierIsInvalid() {
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(List.of("InvalidFormat"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> service.loadGeneExpressions(query, TISSUE, SEX_COHORT))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Invalid composite identifier format");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should trim whitespace from search term")
  void shouldTrimWhitespaceFromSearchTerm() {
    GeneExpressionDocument doc = createGeneExpressionDocument(
      "ENSG00000001",
      "TestGene",
      "TestSymbol"
    );
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByTissueAndSexCohortAndGeneSymbolContainingExcludingCompositeIdentifiers(
        eq(TISSUE),
        eq(SEX_COHORT),
        any(String.class),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(List.of("ENSG00000002~GENE2"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("  test  ")
      .pageNumber(0)
      .pageSize(100)
      .build();

    service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    ArgumentCaptor<String> searchCaptor = ArgumentCaptor.forClass(String.class);
    verify(repository).findByTissueAndSexCohortAndGeneSymbolContainingExcludingCompositeIdentifiers(
      eq(TISSUE),
      eq(SEX_COHORT),
      searchCaptor.capture(),
      anyList(),
      any(PageRequest.class)
    );

    // Should be trimmed
    assertThat(searchCaptor.getValue()).isEqualTo("\\Qtest\\E");
  }

  @Test
  @DisplayName("should filter empty gene symbols from comma-separated search")
  void shouldFilterEmptyGeneSymbolsFromCommaSeparatedSearch() {
    GeneExpressionDocument doc = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByTissueAndSexCohortAndGeneSymbolInIgnoreCaseExcludingCompositeIdentifiers(
        eq(TISSUE),
        eq(SEX_COHORT),
        anyList(),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(List.of("ENSG00000002~GENE2"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("symbol1, , ,symbol3")
      .pageNumber(0)
      .pageSize(100)
      .build();

    service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Pattern>> patternsCaptor = ArgumentCaptor.forClass(List.class);
    verify(
      repository
    ).findByTissueAndSexCohortAndGeneSymbolInIgnoreCaseExcludingCompositeIdentifiers(
      eq(TISSUE),
      eq(SEX_COHORT),
      patternsCaptor.capture(),
      anyList(),
      any(PageRequest.class)
    );

    List<Pattern> patterns = patternsCaptor.getValue();
    // Should only have 2 patterns (empty strings filtered out)
    assertThat(patterns).hasSize(2);
    assertThat(patterns.get(0).pattern()).isEqualTo("^\\Qsymbol1\\E$");
    assertThat(patterns.get(1).pattern()).isEqualTo("^\\Qsymbol3\\E$");
  }

  private GeneExpressionDocument createGeneExpressionDocument(
    String ensemblGeneId,
    String name,
    String geneSymbol
  ) {
    GeneExpressionDocument.FoldChangeResult result =
      GeneExpressionDocument.FoldChangeResult.builder().log2Fc(1.5).adjPVal(0.01).build();

    GeneExpressionDocument document = new GeneExpressionDocument();
    document.setId(new ObjectId());
    document.setTissue(TISSUE);
    document.setSexCohort(SEX_COHORT);
    document.setEnsemblGeneId(ensemblGeneId);
    document.setName(name);
    document.setGeneSymbol(geneSymbol);
    document.setMatchedControl("Control1");
    document.setModelType("Mouse");
    document.setFourMonths(result);
    return document;
  }

  @SuppressWarnings("unchecked")
  private void verifyCompositeCondition(
    Map<String, Object> condition,
    String expectedEnsemblGeneId,
    String expectedName
  ) {
    assertThat(condition).containsKey("$and");
    List<Map<String, Object>> andConditions = (List<Map<String, Object>>) condition.get("$and");
    assertThat(andConditions).hasSize(2);

    assertThat(andConditions.get(0)).containsEntry("ensembl_gene_id", expectedEnsemblGeneId);
    assertThat(andConditions.get(1)).containsEntry("name", expectedName);
  }
}
