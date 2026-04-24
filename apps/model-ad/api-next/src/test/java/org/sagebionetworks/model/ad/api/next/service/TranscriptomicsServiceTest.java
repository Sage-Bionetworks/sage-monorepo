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
import org.sagebionetworks.model.ad.api.next.model.document.Link;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.LinkMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.TranscriptomicsMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.TranscriptomicsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TranscriptomicsServiceTest {

  @Mock
  private TranscriptomicsRepository repository;

  private TranscriptomicsService service;
  private TranscriptomicsMapper mapper;

  private static final String TISSUE = "brain";
  private static final String SEX_COHORT = "Females";

  @BeforeEach
  void setUp() {
    mapper = new TranscriptomicsMapper(new LinkMapper());
    service = new TranscriptomicsService(repository, mapper);
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    Page<TranscriptomicsDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(
        any(Pageable.class),
        any(TranscriptomicsSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    TranscriptomicsPageDto result = service.loadTranscriptomics(query, TISSUE, SEX_COHORT);

    assertThat(result.getTranscriptomics()).isEmpty();
    assertThat(result.getPage().getTotalElements()).isZero();
    verify(repository).findAll(
      any(Pageable.class),
      any(TranscriptomicsSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should return all tissue and sex cohort items when exclude filter has no items")
  void shouldReturnAllTissueAndSexCohortItemsWhenExcludeFilterHasNoItems() {
    TranscriptomicsDocument doc = createTranscriptomicsDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<TranscriptomicsDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(TranscriptomicsSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    TranscriptomicsPageDto result = service.loadTranscriptomics(query, TISSUE, SEX_COHORT);

    assertThat(result.getTranscriptomics()).hasSize(1);
    assertThat(result.getTranscriptomics().get(0).getEnsemblGeneId()).isEqualTo("ENSG00000001");
    verify(repository).findAll(
      any(Pageable.class),
      any(TranscriptomicsSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should respect search term when exclude filter has no items")
  void shouldRespectSearchTermWhenExcludeFilterHasNoItems() {
    TranscriptomicsDocument doc = createTranscriptomicsDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<TranscriptomicsDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(TranscriptomicsSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("Symbol")
      .pageNumber(0)
      .pageSize(100)
      .build();

    TranscriptomicsPageDto result = service.loadTranscriptomics(query, TISSUE, SEX_COHORT);

    assertThat(result.getTranscriptomics()).hasSize(1);
    assertThat(result.getTranscriptomics().get(0).getGeneSymbol()).isEqualTo("Symbol1");
    verify(repository).findAll(
      any(Pageable.class),
      any(TranscriptomicsSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should use search-only method for comma-separated search when no items to exclude")
  void shouldUseSearchOnlyMethodForCommaSeparatedSearchWhenNoItemsToExclude() {
    TranscriptomicsDocument doc1 = createTranscriptomicsDocument(
      "ENSG00000001",
      "GENE1",
      "Symbol1"
    );
    TranscriptomicsDocument doc2 = createTranscriptomicsDocument(
      "ENSG00000002",
      "GENE2",
      "Symbol2"
    );
    Page<TranscriptomicsDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findAll(
        any(Pageable.class),
        any(TranscriptomicsSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("symbol1,symbol2")
      .pageNumber(0)
      .pageSize(100)
      .build();

    TranscriptomicsPageDto result = service.loadTranscriptomics(query, TISSUE, SEX_COHORT);

    assertThat(result.getTranscriptomics()).hasSize(2);
    verify(repository).findAll(
      any(Pageable.class),
      any(TranscriptomicsSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should return matching composite identifiers when include filter has items")
  void shouldReturnMatchingCompositeIdentifiersWhenIncludeFilterHasItems() {
    TranscriptomicsDocument doc = createTranscriptomicsDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<TranscriptomicsDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(TranscriptomicsSearchQueryDto.class),
        eq(List.of("ENSG00000001~GENE1")),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .items(List.of("ENSG00000001~GENE1"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    TranscriptomicsPageDto result = service.loadTranscriptomics(query, TISSUE, SEX_COHORT);

    assertThat(result.getTranscriptomics()).hasSize(1);
    assertThat(result.getTranscriptomics().get(0).getEnsemblGeneId()).isEqualTo("ENSG00000001");
    verify(repository).findAll(
      any(Pageable.class),
      any(TranscriptomicsSearchQueryDto.class),
      eq(List.of("ENSG00000001~GENE1")),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should return non-matching composite identifiers when exclude filter has items")
  void shouldReturnNonMatchingCompositeIdentifiersWhenExcludeFilterHasItems() {
    TranscriptomicsDocument doc = createTranscriptomicsDocument("ENSG00000002", "GENE2", "Symbol2");
    Page<TranscriptomicsDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(TranscriptomicsSearchQueryDto.class),
        eq(List.of("ENSG00000001~GENE1")),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .items(List.of("ENSG00000001~GENE1"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    TranscriptomicsPageDto result = service.loadTranscriptomics(query, TISSUE, SEX_COHORT);

    assertThat(result.getTranscriptomics()).hasSize(1);
    assertThat(result.getTranscriptomics().get(0).getEnsemblGeneId()).isEqualTo("ENSG00000002");
    verify(repository).findAll(
      any(Pageable.class),
      any(TranscriptomicsSearchQueryDto.class),
      eq(List.of("ENSG00000001~GENE1")),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should perform partial case-insensitive search when exclude filter has search term")
  void shouldPerformPartialCaseInsensitiveSearchWhenExcludeFilterHasSearchTerm() {
    TranscriptomicsDocument doc = createTranscriptomicsDocument(
      "ENSG00000001",
      "TestGene",
      "TestSymbol"
    );
    Page<TranscriptomicsDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(TranscriptomicsSearchQueryDto.class),
        eq(List.of("ENSG00000002~GENE2")),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .items(List.of("ENSG00000002~GENE2"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    TranscriptomicsPageDto result = service.loadTranscriptomics(query, TISSUE, SEX_COHORT);

    assertThat(result.getTranscriptomics()).hasSize(1);
    assertThat(result.getTranscriptomics().get(0).getGeneSymbol()).isEqualTo("TestSymbol");
    verify(repository).findAll(
      any(Pageable.class),
      any(TranscriptomicsSearchQueryDto.class),
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
    TranscriptomicsDocument doc1 = createTranscriptomicsDocument(
      "ENSG00000001",
      "GENE1",
      "Symbol1"
    );
    TranscriptomicsDocument doc2 = createTranscriptomicsDocument(
      "ENSG00000002",
      "GENE2",
      "Symbol2"
    );
    Page<TranscriptomicsDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findAll(
        any(Pageable.class),
        any(TranscriptomicsSearchQueryDto.class),
        eq(List.of("ENSG00000003~GENE3")),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .items(List.of("ENSG00000003~GENE3"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("symbol1,symbol2")
      .pageNumber(0)
      .pageSize(100)
      .build();

    TranscriptomicsPageDto result = service.loadTranscriptomics(query, TISSUE, SEX_COHORT);

    assertThat(result.getTranscriptomics()).hasSize(2);
    verify(repository).findAll(
      any(Pageable.class),
      any(TranscriptomicsSearchQueryDto.class),
      eq(List.of("ENSG00000003~GENE3")),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should not use search when include filter is specified")
  void shouldNotUseSearchWhenIncludeFilterIsSpecified() {
    TranscriptomicsDocument doc = createTranscriptomicsDocument("ENSG00000001", "GENE1", "Symbol1");
    Page<TranscriptomicsDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(TranscriptomicsSearchQueryDto.class),
        eq(List.of("ENSG00000001~GENE1")),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .items(List.of("ENSG00000001~GENE1"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .search("test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    TranscriptomicsPageDto result = service.loadTranscriptomics(query, TISSUE, SEX_COHORT);

    assertThat(result.getTranscriptomics()).hasSize(1);
    verify(repository).findAll(
      any(Pageable.class),
      any(TranscriptomicsSearchQueryDto.class),
      eq(List.of("ENSG00000001~GENE1")),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should handle multiple composite identifiers")
  void shouldHandleMultipleCompositeIdentifiers() {
    TranscriptomicsDocument doc1 = createTranscriptomicsDocument(
      "ENSG00000001",
      "GENE1",
      "Symbol1"
    );
    TranscriptomicsDocument doc2 = createTranscriptomicsDocument(
      "ENSG00000002",
      "GENE2",
      "Symbol2"
    );
    Page<TranscriptomicsDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findAll(
        any(Pageable.class),
        any(TranscriptomicsSearchQueryDto.class),
        eq(List.of("ENSG00000001~GENE1", "ENSG00000002~GENE2")),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .items(List.of("ENSG00000001~GENE1", "ENSG00000002~GENE2"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    TranscriptomicsPageDto result = service.loadTranscriptomics(query, TISSUE, SEX_COHORT);

    assertThat(result.getTranscriptomics()).hasSize(2);
    verify(repository).findAll(
      any(Pageable.class),
      any(TranscriptomicsSearchQueryDto.class),
      eq(List.of("ENSG00000001~GENE1", "ENSG00000002~GENE2")),
      eq(TISSUE),
      eq(SEX_COHORT)
    );
  }

  @Test
  @DisplayName("should use default page size when not specified")
  void shouldUseDefaultPageSizeWhenNotSpecified() {
    Page<TranscriptomicsDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(
        any(Pageable.class),
        any(TranscriptomicsSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE),
        eq(SEX_COHORT)
      )
    ).thenReturn(page);

    TranscriptomicsSearchQueryDto query = TranscriptomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(null)
      .pageSize(null)
      .build();

    service.loadTranscriptomics(query, TISSUE, SEX_COHORT);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(
      pageableCaptor.capture(),
      any(TranscriptomicsSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE),
      eq(SEX_COHORT)
    );

    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(100);
  }

  private TranscriptomicsDocument createTranscriptomicsDocument(
    String ensemblGeneId,
    String name,
    String geneSymbol
  ) {
    TranscriptomicsDocument.FoldChangeResult result =
      TranscriptomicsDocument.FoldChangeResult.builder().log2Fc(1.5).adjPVal(0.01).build();

    TranscriptomicsDocument document = new TranscriptomicsDocument();
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
