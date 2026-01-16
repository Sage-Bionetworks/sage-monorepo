package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.sagebionetworks.model.ad.api.next.model.document.Link;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.GeneExpressionMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.LinkMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.GeneExpressionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    mapper = new GeneExpressionMapper(new LinkMapper());
    service = new GeneExpressionService(repository, mapper);
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    GeneExpressionsPageDto result = service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    assertThat(result.getGeneExpressions()).isEmpty();
    assertThat(result.getPage().getTotalElements()).isZero();
    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should return all tissue and sex cohort items when exclude filter has no items")
  void shouldReturnAllTissueAndSexCohortItemsWhenExcludeFilterHasNoItems() {
    GeneExpressionDocument doc = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
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
    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should respect search term when exclude filter has no items")
  void shouldRespectSearchTermWhenExcludeFilterHasNoItems() {
    GeneExpressionDocument doc = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE),
        eq(SEX_COHORT)
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
    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should use search-only method for comma-separated search when no items to exclude")
  void shouldUseSearchOnlyMethodForCommaSeparatedSearchWhenNoItemsToExclude() {
    GeneExpressionDocument doc1 = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    GeneExpressionDocument doc2 = createGeneExpressionDocument("ENSG00000002", "GENE2", "Symbol2");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE),
        eq(SEX_COHORT)
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
    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should return matching composite identifiers when include filter has items")
  void shouldReturnMatchingCompositeIdentifiersWhenIncludeFilterHasItems() {
    GeneExpressionDocument doc = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of("ENSG00000001~GENE1")),
        eq(TISSUE),
        eq(SEX_COHORT)
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
    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of("ENSG00000001~GENE1")),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should return non-matching composite identifiers when exclude filter has items")
  void shouldReturnNonMatchingCompositeIdentifiersWhenExcludeFilterHasItems() {
    GeneExpressionDocument doc = createGeneExpressionDocument("ENSG00000002", "GENE2", "Symbol2");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of("ENSG00000001~GENE1")),
        eq(TISSUE),
        eq(SEX_COHORT)
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
    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of("ENSG00000001~GENE1")),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
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
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of("ENSG00000002~GENE2")),
        eq(TISSUE),
        eq(SEX_COHORT)
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
    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of("ENSG00000002~GENE2")),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
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
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of("ENSG00000003~GENE3")),
        eq(TISSUE),
        eq(SEX_COHORT)
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
    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of("ENSG00000003~GENE3")),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should not use search when include filter is specified")
  void shouldNotUseSearchWhenIncludeFilterIsSpecified() {
    GeneExpressionDocument doc = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of("ENSG00000001~GENE1")),
        eq(TISSUE),
        eq(SEX_COHORT)
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
    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of("ENSG00000001~GENE1")),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should handle multiple composite identifiers")
  void shouldHandleMultipleCompositeIdentifiers() {
    GeneExpressionDocument doc1 = createGeneExpressionDocument("ENSG00000001", "GENE1", "Symbol1");
    GeneExpressionDocument doc2 = createGeneExpressionDocument("ENSG00000002", "GENE2", "Symbol2");
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of("ENSG00000001~GENE1", "ENSG00000002~GENE2")),
        eq(TISSUE),
        eq(SEX_COHORT)
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
    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of("ENSG00000001~GENE1", "ENSG00000002~GENE2")),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should use default page size when not specified")
  void shouldUseDefaultPageSizeWhenNotSpecified() {
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(null)
      .pageSize(null)
      .build();

    service.loadGeneExpressions(query, TISSUE, SEX_COHORT);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(
      pageableCaptor.capture(),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE),
      eq(SEX_COHORT)
    );

    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(100);
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
    document.setName(Link.builder().linkText(name).linkUrl("").build());
    document.setGeneSymbol(geneSymbol);
    document.setMatchedControl("Control1");
    document.setModelType("Mouse");
    document.setFourMonths(result);
    return document;
  }
}
