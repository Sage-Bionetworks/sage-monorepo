package org.sagebionetworks.agora.api.next.service;

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
import org.sagebionetworks.agora.api.next.model.document.NominatedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetsPageDto;
import org.sagebionetworks.agora.api.next.model.mapper.NominatedTargetMapper;
import org.sagebionetworks.agora.api.next.model.repository.NominatedTargetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NominatedTargetServiceTest {

  @Mock
  private NominatedTargetRepository repository;

  private NominatedTargetService service;
  private NominatedTargetMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new NominatedTargetMapper();
    service = new NominatedTargetService(repository, mapper);
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    Page<NominatedTargetDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedTargetsPageDto result = service.loadNominatedTargets(query);

    assertThat(result.getNominatedTargets()).isEmpty();
    assertThat(result.getPage().getTotalElements()).isZero();
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should return all items when exclude filter has no items")
  void shouldReturnAllItemsWhenExcludeFilterHasNoItems() {
    NominatedTargetDocument doc = createNominatedTargetDocument("APOE");
    Page<NominatedTargetDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedTargetsPageDto result = service.loadNominatedTargets(query);

    assertThat(result.getNominatedTargets()).hasSize(1);
    assertThat(result.getNominatedTargets().get(0).getHgncSymbol()).isEqualTo("APOE");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should return matching items when include filter has items")
  void shouldReturnMatchingItemsWhenIncludeFilterHasItems() {
    NominatedTargetDocument doc = createNominatedTargetDocument("APOE");
    Page<NominatedTargetDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of("APOE"))
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(List.of("APOE"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedTargetsPageDto result = service.loadNominatedTargets(query);

    assertThat(result.getNominatedTargets()).hasSize(1);
    assertThat(result.getNominatedTargets().get(0).getHgncSymbol()).isEqualTo("APOE");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of("APOE"))
    );
  }

  @Test
  @DisplayName("should return non-matching items when exclude filter has items")
  void shouldReturnNonMatchingItemsWhenExcludeFilterHasItems() {
    NominatedTargetDocument doc = createNominatedTargetDocument("TREM2");
    Page<NominatedTargetDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of("APOE"))
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(List.of("APOE"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedTargetsPageDto result = service.loadNominatedTargets(query);

    assertThat(result.getNominatedTargets()).hasSize(1);
    assertThat(result.getNominatedTargets().get(0).getHgncSymbol()).isEqualTo("TREM2");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of("APOE"))
    );
  }

  @Test
  @DisplayName("should perform partial case-insensitive search when exclude filter has search term")
  void shouldPerformPartialCaseInsensitiveSearchWhenExcludeFilterHasSearchTerm() {
    NominatedTargetDocument doc = createNominatedTargetDocument("TestGene");
    Page<NominatedTargetDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of("APOE"))
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(List.of("APOE"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedTargetsPageDto result = service.loadNominatedTargets(query);

    assertThat(result.getNominatedTargets()).hasSize(1);
    assertThat(result.getNominatedTargets().get(0).getHgncSymbol()).isEqualTo("TestGene");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of("APOE"))
    );
  }

  @Test
  @DisplayName(
    "should perform case-insensitive full match search when exclude filter has comma-separated search terms"
  )
  void shouldPerformCaseInsensitiveFullMatchSearchWhenExcludeFilterHasCommaSeparatedSearchTerms() {
    NominatedTargetDocument doc1 = createNominatedTargetDocument("APOE");
    NominatedTargetDocument doc2 = createNominatedTargetDocument("TREM2");
    Page<NominatedTargetDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of("BIN1"))
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(List.of("BIN1"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("apoe,trem2")
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedTargetsPageDto result = service.loadNominatedTargets(query);

    assertThat(result.getNominatedTargets()).hasSize(2);
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of("BIN1"))
    );
  }

  @Test
  @DisplayName("should not use search when include filter is specified")
  void shouldNotUseSearchWhenIncludeFilterIsSpecified() {
    NominatedTargetDocument doc = createNominatedTargetDocument("APOE");
    Page<NominatedTargetDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of("APOE"))
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(List.of("APOE"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .search("test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedTargetsPageDto result = service.loadNominatedTargets(query);

    assertThat(result.getNominatedTargets()).hasSize(1);
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of("APOE"))
    );
  }

  @Test
  @DisplayName("should use default page size when not specified")
  void shouldUseDefaultPageSizeWhenNotSpecified() {
    Page<NominatedTargetDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(null)
      .pageSize(null)
      .build();

    service.loadNominatedTargets(query);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(
      pageableCaptor.capture(),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of())
    );

    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(100);
  }

  @Test
  @DisplayName("should trim whitespace from search term")
  void shouldTrimWhitespaceFromSearchTerm() {
    NominatedTargetDocument doc = createNominatedTargetDocument("TestGene");
    Page<NominatedTargetDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of("APOE"))
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(List.of("APOE"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("  test  ")
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedTargetsPageDto result = service.loadNominatedTargets(query);

    assertThat(result.getNominatedTargets()).hasSize(1);
    assertThat(result.getNominatedTargets().get(0).getHgncSymbol()).isEqualTo("TestGene");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of("APOE"))
    );
  }

  @Test
  @DisplayName("should filter empty hgnc_symbols from comma-separated search")
  void shouldFilterEmptyHgncSymbolsFromCommaSeparatedSearch() {
    NominatedTargetDocument doc = createNominatedTargetDocument("APOE");
    Page<NominatedTargetDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of("TREM2"))
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(List.of("TREM2"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("apoe, , ,bin1")
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedTargetsPageDto result = service.loadNominatedTargets(query);

    assertThat(result.getNominatedTargets()).hasSize(1);
    assertThat(result.getNominatedTargets().get(0).getHgncSymbol()).isEqualTo("APOE");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of("TREM2"))
    );
  }

  private NominatedTargetDocument createNominatedTargetDocument(String hgncSymbol) {
    NominatedTargetDocument document = new NominatedTargetDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSG00000141510");
    document.setHgncSymbol(hgncSymbol);
    document.setTotalNominations(5);
    document.setInitialNomination(2021);
    document.setNominatingTeams(List.of("Duke", "Emory"));
    document.setCohortStudies(List.of("ROSMAP", "Mayo"));
    document.setInputData(List.of("RNA", "Protein"));
    document.setPrograms(List.of("AMP-AD", "Community"));
    document.setPharosClass("Tbio");
    return document;
  }
}
